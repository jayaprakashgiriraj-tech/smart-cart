package com.simats.smartcart.data

import android.net.Uri
import com.simats.smartcart.network.AuthNetworkClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.ConcurrentHashMap

object PriceTrackingRepository {
    private val cache = ConcurrentHashMap<String, ProductPriceHistory>()
    private val savedTargetPrices = ConcurrentHashMap<String, String>()

    fun getSavedTargetPrice(productId: String): String? {
        return savedTargetPrices[productId]
    }

    fun saveTargetPrice(productId: String, targetPriceFormatted: String) {
        savedTargetPrices[productId] = targetPriceFormatted
    }

    fun getCachedOrLocal(product: Product): ProductPriceHistory {
        return cache[product.id] ?: cache[product.name] ?: product.priceHistory
    }

    suspend fun getLivePriceHistory(product: Product): ProductPriceHistory {
        return withContext(Dispatchers.IO) {
            val cached = cache[product.id] ?: cache[product.name]
            if (cached != null && cached.isLiveSynced) {
                return@withContext cached
            }

            try {
                val candidateUrls = AuthNetworkClient.CANDIDATE_URLS
                var jsonResult: JSONObject? = null

                for (base in candidateUrls) {
                    try {
                        val endpoint = "$base/price_tracking/history.php?product_id=${product.id}&product_name=${Uri.encode(product.name)}"
                        val url = URL(endpoint)
                        val conn = url.openConnection() as HttpURLConnection
                        conn.connectTimeout = 3000
                        conn.readTimeout = 3000
                        conn.requestMethod = "GET"

                        if (conn.responseCode in 200..299) {
                            val reader = BufferedReader(InputStreamReader(conn.inputStream))
                            val responseStr = reader.use { it.readText() }
                            val json = JSONObject(responseStr)
                            if (json.optBoolean("success", false) || json.optString("status") == "success") {
                                jsonResult = json
                                break
                            }
                        }
                    } catch (_: Exception) {
                        // try next candidate
                    }
                }

                if (jsonResult != null) {
                    val pObj = jsonResult.optJSONObject("product") ?: JSONObject()
                    val hArray = jsonResult.optJSONArray("history")

                    val curPrice = pObj.optInt("numeric_price", product.numericPrice)
                    val liveSync = jsonResult.optBoolean("live_sync", false)
                    val lastUpdated = jsonResult.optString("last_updated_text", "No historical price observations recorded yet")

                    val hasHighest = pObj.has("highest_numeric_price") && !pObj.isNull("highest_numeric_price")
                    val highPrice = if (hasHighest) pObj.optInt("highest_numeric_price", curPrice) else curPrice
                    val highFormatted = if (hasHighest) {
                        pObj.optString("highest_recorded_price", ProductCatalog.formatIndianRupees(highPrice))
                    } else {
                        "—"
                    }
                    val highDate = if (hasHighest && !pObj.isNull("highest_recorded_date")) {
                        pObj.optString("highest_recorded_date", "—")
                    } else {
                        "—"
                    }

                    val hasLowest = pObj.has("lowest_numeric_price") && !pObj.isNull("lowest_numeric_price")
                    val lowPrice = if (hasLowest) pObj.optInt("lowest_numeric_price", curPrice) else curPrice
                    val lowFormatted = if (hasLowest) {
                        pObj.optString("lowest_recorded_price", ProductCatalog.formatIndianRupees(lowPrice))
                    } else {
                        "—"
                    }
                    val lowDate = if (hasLowest && !pObj.isNull("lowest_recorded_date")) {
                        pObj.optString("lowest_recorded_date", "—")
                    } else {
                        "—"
                    }

                    val savings = if (hasHighest) pObj.optString("savings_from_peak", "₹0") else "₹0"
                    val savingsPct = if (hasHighest) pObj.optInt("savings_percent", 0) else 0

                    val points = mutableListOf<PricePoint>()
                    if (hArray != null) {
                        for (i in 0 until hArray.length()) {
                            val item = hArray.getJSONObject(i)
                            val priceVal = item.optInt("recorded_price", item.optInt("observed_price", curPrice))
                            val lbl = item.optString("label", item.optString("recorded_date", "Day ${i + 1}"))
                            val isHigh = item.optBoolean("is_highest", false)
                            val isLow = item.optBoolean("is_lowest", false)
                            val formatted = item.optString("formatted_price", ProductCatalog.formatIndianRupees(priceVal))
                            points.add(
                                PricePoint(
                                    label = lbl,
                                    price = priceVal,
                                    formattedPrice = formatted,
                                    isHighest = isHigh,
                                    isLowest = isLow
                                )
                            )
                        }
                    }

                    val suggestedTarget = if (hasLowest && lowPrice > 0) lowPrice else curPrice
                    val suggestedTargetFormatted = if (hasLowest && lowPrice > 0) lowFormatted else product.price

                    val predMin = if (pObj.has("estimated_next_week_min_formatted") && !pObj.isNull("estimated_next_week_min_formatted")) {
                        pObj.optString("estimated_next_week_min_formatted", "—")
                    } else {
                        "—"
                    }
                    val predMax = if (pObj.has("estimated_next_week_max_formatted") && !pObj.isNull("estimated_next_week_max_formatted")) {
                        pObj.optString("estimated_next_week_max_formatted", "—")
                    } else {
                        "—"
                    }

                    val history = ProductPriceHistory(
                        productId = product.id,
                        productName = product.name,
                        currentPrice = curPrice,
                        currentPriceFormatted = ProductCatalog.formatIndianRupees(curPrice),
                        highestPrice = highPrice,
                        highestPriceFormatted = highFormatted,
                        highestPriceDate = highDate,
                        lowestPrice = lowPrice,
                        lowestPriceFormatted = lowFormatted,
                        lowestPriceDate = lowDate,
                        savingsFromPeak = if (hasHighest) highPrice - curPrice else 0,
                        savingsFromPeakFormatted = savings,
                        savingsPercent = savingsPct,
                        pricePoints = points,
                        minGraphBound = lowPrice,
                        maxGraphBound = highPrice,
                        suggestedTargetPrice = suggestedTarget,
                        suggestedTargetPriceFormatted = suggestedTargetFormatted,
                        estimatedNextWeekMinFormatted = predMin,
                        estimatedNextWeekMaxFormatted = predMax,
                        isLiveSynced = liveSync,
                        lastUpdatedText = lastUpdated
                    )

                    cache[product.id] = history
                    cache[product.name] = history
                    return@withContext history
                }
            } catch (_: Exception) {
                // Return honest offline state on network failure, do not cache error
            }

            // Explicit offline error state when network call fails
            return@withContext ProductPriceHistory(
                productId = product.id,
                productName = product.name,
                currentPrice = product.numericPrice,
                currentPriceFormatted = product.price,
                highestPrice = product.numericPrice,
                highestPriceFormatted = "—",
                highestPriceDate = "—",
                lowestPrice = product.numericPrice,
                lowestPriceFormatted = "—",
                lowestPriceDate = "—",
                savingsFromPeak = 0,
                savingsFromPeakFormatted = "₹0",
                savingsPercent = 0,
                pricePoints = emptyList(),
                minGraphBound = product.numericPrice,
                maxGraphBound = product.numericPrice,
                suggestedTargetPrice = product.numericPrice,
                suggestedTargetPriceFormatted = product.price,
                estimatedNextWeekMinFormatted = "—",
                estimatedNextWeekMaxFormatted = "—",
                isLiveSynced = false,
                lastUpdatedText = "Unable to connect to price history server (Offline)"
            )
        }
    }
}
