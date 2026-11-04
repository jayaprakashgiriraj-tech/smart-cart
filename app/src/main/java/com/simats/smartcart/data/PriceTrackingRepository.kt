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
                    val pObj = jsonResult.getJSONObject("product")
                    val hArray = jsonResult.getJSONArray("history")

                    val curPrice = pObj.optInt("numeric_price", product.numericPrice)
                    val highPrice = pObj.optInt("highest_numeric_price", (curPrice * 1.15).toInt())
                    val lowPrice = pObj.optInt("lowest_numeric_price", (curPrice * 0.88).toInt())
                    val highFormatted = pObj.optString("highest_recorded_price", ProductCatalog.formatIndianRupees(highPrice))
                    val lowFormatted = pObj.optString("lowest_recorded_price", ProductCatalog.formatIndianRupees(lowPrice))
                    val highDate = pObj.optString("highest_recorded_date", "5 Oct")
                    val lowDate = pObj.optString("lowest_recorded_date", "31 Oct")
                    val savings = pObj.optString("savings_from_peak", ProductCatalog.formatIndianRupees(highPrice - curPrice))
                    val savingsPct = pObj.optInt("savings_percent", 12)
                    val lastUpdated = jsonResult.optString("last_updated_text", "Updated today (Live)")

                    val points = mutableListOf<PricePoint>()
                    for (i in 0 until hArray.length()) {
                        val item = hArray.getJSONObject(i)
                        val priceVal = item.optInt("recorded_price", curPrice)
                        val lbl = item.optString("label", item.optString("recorded_date", "Day $i"))
                        val isHigh = item.optBoolean("is_highest", priceVal == highPrice)
                        val isLow = item.optBoolean("is_lowest", priceVal == lowPrice)
                        points.add(
                            PricePoint(
                                label = lbl,
                                price = priceVal,
                                formattedPrice = ProductCatalog.formatIndianRupees(priceVal),
                                isHighest = isHigh,
                                isLowest = isLow
                            )
                        )
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
                        savingsFromPeak = highPrice - curPrice,
                        savingsFromPeakFormatted = savings,
                        savingsPercent = savingsPct,
                        pricePoints = points.ifEmpty { product.priceHistory.pricePoints },
                        minGraphBound = lowPrice,
                        maxGraphBound = highPrice,
                        suggestedTargetPrice = lowPrice,
                        suggestedTargetPriceFormatted = lowFormatted,
                        estimatedNextWeekMinFormatted = ProductCatalog.formatIndianRupees((lowPrice * 0.98).toInt()),
                        estimatedNextWeekMaxFormatted = ProductCatalog.formatIndianRupees((curPrice * 1.03).toInt()),
                        isLiveSynced = true,
                        lastUpdatedText = lastUpdated
                    )

                    cache[product.id] = history
                    cache[product.name] = history
                    return@withContext history
                }
            } catch (_: Exception) {
                // Ignore and fall through to local real-time generator
            }

            val local = product.priceHistory
            cache[product.id] = local
            cache[product.name] = local
            return@withContext local
        }
    }
}

