package com.simats.smartcart.data

import androidx.compose.runtime.mutableStateListOf
import com.simats.smartcart.network.AuthNetworkClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLDecoder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

data class SavedComparisonItem(
    val id: String = UUID.randomUUID().toString(),
    val product1: Product,
    val product2: Product,
    val title: String = "${product1.name} vs ${product2.name}",
    val categoryLabel: String = product1.getComparisonCategoryLabel(),
    val winnerProduct: Product = if (product1.valueScore >= product2.valueScore) product1 else product2,
    val summary: String = "${if (product1.valueScore >= product2.valueScore) product1.name else product2.name} is rated higher for your priority use case.",
    val savedAt: Long = System.currentTimeMillis()
) {
    fun getFormattedDate(): String {
        val now = System.currentTimeMillis()
        val diffHours = (now - savedAt) / (1000 * 60 * 60)
        return if (diffHours < 24) {
            "Saved today"
        } else {
            val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            "Saved " + sdf.format(Date(savedAt))
        }
    }
}

object SavedComparisonRepository {
    private val _items = mutableStateListOf<SavedComparisonItem>()

    val items: List<SavedComparisonItem>
        get() = _items

    init {
        // Default sample comparison matching original app state
        val jbl = ProductCatalog.findProductByNameOrNull("JBL Tune 510BT")
        val soundcore = ProductCatalog.findProductByNameOrNull("Soundcore H30i")
        if (jbl != null && soundcore != null) {
            _items.add(
                SavedComparisonItem(
                    id = "default_comp_1",
                    product1 = jbl,
                    product2 = soundcore,
                    title = "JBL Tune 510BT vs Soundcore H30i",
                    categoryLabel = "Online classes",
                    winnerProduct = jbl,
                    summary = "JBL Tune 510BT is rated higher for your priority use case (Clear calls + lighter fit)."
                )
            )
        }

        // Sync any persisted saved comparisons from PHP backend
        syncFromBackend()
    }

    private fun clean(str: String): String {
        return try {
            URLDecoder.decode(str, "UTF-8")
        } catch (_: Exception) {
            str
        }.trim().lowercase()
    }

    private fun matches(s1: String, s2: String): Boolean {
        return s1 == s2 || s1.contains(s2) || s2.contains(s1)
    }

    fun isSaved(prod1Name: String, prod2Name: String): Boolean {
        if (prod1Name.isBlank() || prod2Name.isBlank()) return false
        val c1 = clean(prod1Name)
        val c2 = clean(prod2Name)
        return _items.any { item ->
            val ic1 = clean(item.product1.name)
            val ic2 = clean(item.product2.name)
            (matches(c1, ic1) && matches(c2, ic2)) || (matches(c1, ic2) && matches(c2, ic1))
        }
    }

    fun isSaved(product1: Product, product2: Product): Boolean {
        return isSaved(product1.name, product2.name)
    }

    fun saveComparison(product1: Product, product2: Product, title: String? = null): SavedComparisonItem {
        val c1 = clean(product1.name)
        val c2 = clean(product2.name)
        val existing = _items.firstOrNull { item ->
            val ic1 = clean(item.product1.name)
            val ic2 = clean(item.product2.name)
            (matches(c1, ic1) && matches(c2, ic2)) || (matches(c1, ic2) && matches(c2, ic1))
        }
        if (existing != null) {
            return existing
        }

        val winner = if (product1.valueScore >= product2.valueScore) product1 else product2
        val itemTitle = title ?: "${product1.name} vs ${product2.name}"
        val summary = "${winner.name} is rated higher for your priority use case (${winner.bestFor})."
        val newItem = SavedComparisonItem(
            product1 = product1,
            product2 = product2,
            title = itemTitle,
            categoryLabel = product1.getComparisonCategoryLabel(),
            winnerProduct = winner,
            summary = summary
        )
        _items.add(0, newItem)

        syncSaveToBackend(newItem)
        return newItem
    }

    fun removeComparison(product1Name: String, product2Name: String) {
        val c1 = clean(product1Name)
        val c2 = clean(product2Name)
        val toRemove = _items.filter { item ->
            val ic1 = clean(item.product1.name)
            val ic2 = clean(item.product2.name)
            (matches(c1, ic1) && matches(c2, ic2)) || (matches(c1, ic2) && matches(c2, ic1))
        }
        _items.removeAll(toRemove)

        toRemove.forEach {
            syncRemoveFromBackend(it)
        }
    }

    fun removeById(id: String) {
        val toRemove = _items.filter { it.id == id }
        _items.removeAll(toRemove)
        toRemove.forEach {
            syncRemoveFromBackend(it)
        }
    }

    fun toggleSaveComparison(product1: Product, product2: Product): Boolean {
        return if (isSaved(product1.name, product2.name)) {
            removeComparison(product1.name, product2.name)
            false
        } else {
            saveComparison(product1, product2)
            true
        }
    }

    private fun syncSaveToBackend(item: SavedComparisonItem) {
        CoroutineScope(Dispatchers.IO).launch {
            val user = UserAccountRepository.currentUser
            val userId = user?.id ?: 10
            val email = user?.email ?: "jayaprakashgiriraj@gmail.com"

            for (baseUrl in AuthNetworkClient.CANDIDATE_URLS) {
                try {
                    val url = URL("$baseUrl/comparisons/save.php")
                    val conn = (url.openConnection() as HttpURLConnection).apply {
                        requestMethod = "POST"
                        connectTimeout = 3000
                        readTimeout = 3000
                        doOutput = true
                        setRequestProperty("Content-Type", "application/json")
                    }

                    val json = JSONObject().apply {
                        put("user_id", userId)
                        put("email", email)
                        put("prod1", item.product1.id.ifBlank { item.product1.name })
                        put("prod2", item.product2.id.ifBlank { item.product2.name })
                        put("title", item.title)
                    }

                    OutputStreamWriter(conn.outputStream).use { writer ->
                        writer.write(json.toString())
                        writer.flush()
                    }

                    if (conn.responseCode in 200..299) {
                        break
                    }
                } catch (_: Exception) {
                    // Try next candidate
                }
            }
        }
    }

    private fun syncRemoveFromBackend(item: SavedComparisonItem) {
        CoroutineScope(Dispatchers.IO).launch {
            val user = UserAccountRepository.currentUser
            val userId = user?.id ?: 10
            val email = user?.email ?: "jayaprakashgiriraj@gmail.com"

            for (baseUrl in AuthNetworkClient.CANDIDATE_URLS) {
                try {
                    val url = URL("$baseUrl/comparisons/remove.php")
                    val conn = (url.openConnection() as HttpURLConnection).apply {
                        requestMethod = "POST"
                        connectTimeout = 3000
                        readTimeout = 3000
                        doOutput = true
                        setRequestProperty("Content-Type", "application/json")
                    }

                    val json = JSONObject().apply {
                        put("user_id", userId)
                        put("email", email)
                        put("prod1", item.product1.id.ifBlank { item.product1.name })
                        put("prod2", item.product2.id.ifBlank { item.product2.name })
                    }

                    OutputStreamWriter(conn.outputStream).use { writer ->
                        writer.write(json.toString())
                        writer.flush()
                    }

                    if (conn.responseCode in 200..299) {
                        break
                    }
                } catch (_: Exception) {
                    // Try next candidate
                }
            }
        }
    }

    fun syncFromBackend() {
        CoroutineScope(Dispatchers.IO).launch {
            val user = UserAccountRepository.currentUser
            val userId = user?.id ?: 10
            val email = user?.email ?: "jayaprakashgiriraj@gmail.com"

            for (baseUrl in AuthNetworkClient.CANDIDATE_URLS) {
                try {
                    val url = URL("$baseUrl/comparisons/saved.php?user_id=$userId&email=${java.net.URLEncoder.encode(email, "UTF-8")}")
                    val conn = (url.openConnection() as HttpURLConnection).apply {
                        requestMethod = "GET"
                        connectTimeout = 3000
                        readTimeout = 3000
                    }

                    if (conn.responseCode in 200..299) {
                        val response = BufferedReader(InputStreamReader(conn.inputStream)).use { it.readText() }
                        val root = JSONObject(response)
                        if (root.optBoolean("success", false)) {
                            val comps = root.optJSONArray("comparisons")
                            if (comps != null && comps.length() > 0) {
                                kotlinx.coroutines.withContext(Dispatchers.Main) {
                                    for (i in 0 until comps.length()) {
                                        val obj = comps.getJSONObject(i)
                                        val p1Name = obj.optString("p1_name")
                                        val p2Name = obj.optString("p2_name")
                                        if (p1Name.isNotBlank() && p2Name.isNotBlank()) {
                                            if (!isSaved(p1Name, p2Name)) {
                                                val p1 = ProductCatalog.findProductByNameOrNull(p1Name)
                                                val p2 = ProductCatalog.findProductByNameOrNull(p2Name)
                                                if (p1 != null && p2 != null) {
                                                    val winnerName = obj.optString("winner_name")
                                                    val winner = if (winnerName.equals(p2.name, ignoreCase = true)) p2 else p1
                                                    val title = obj.optString("comparison_title", "${p1.name} vs ${p2.name}")
                                                    val summary = obj.optString("summary", "")
                                                    _items.add(
                                                        SavedComparisonItem(
                                                            id = "remote_" + obj.optString("comparison_id", UUID.randomUUID().toString()),
                                                            product1 = p1,
                                                            product2 = p2,
                                                            title = title,
                                                            categoryLabel = p1.getComparisonCategoryLabel(),
                                                            winnerProduct = winner,
                                                            summary = summary
                                                        )
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            break
                        }
                    }
                } catch (_: Exception) {
                    // Try next candidate
                }
            }
        }
    }
}

