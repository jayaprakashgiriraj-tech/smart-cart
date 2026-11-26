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

data class TargetAlertResult(
    val isSuccess: Boolean,
    val message: String,
    val productId: String,
    val targetPrice: Int? = null,
    val targetPriceFormatted: String? = null,
    val isActive: Boolean = true
)

object PriceTrackingRepository {
    private val cache = ConcurrentHashMap<String, ProductPriceHistory>()
    private val savedTargetPrices = ConcurrentHashMap<String, String>()

    fun getSavedTargetPrice(productId: String): String? {
        return savedTargetPrices[productId]
    }

    fun saveTargetPrice(productId: String, targetPriceFormatted: String) {
        savedTargetPrices[productId] = targetPriceFormatted
    }

    suspend fun saveTargetPriceRemote(productId: String, targetPrice: Int): TargetAlertResult {
        return withContext(Dispatchers.IO) {
            if (!SecureTokenStorage.hasValidToken()) {
                return@withContext TargetAlertResult(
                    isSuccess = false,
                    message = "Please log in to set a target price alert.",
                    productId = productId
                )
            }
            if (targetPrice <= 0 || targetPrice > 10000000) {
                return@withContext TargetAlertResult(
                    isSuccess = false,
                    message = "Target price must be between ₹1 and ₹1,00,00,000.",
                    productId = productId
                )
            }

            val payload = JSONObject().apply {
                put("product_id", productId)
                put("target_price", targetPrice)
            }

            val responseJson = AuthNetworkClient.authenticatedPost("price_tracking/set_alert.php", payload)
            if (responseJson != null && (responseJson.optBoolean("success", false) || responseJson.optString("status") == "success")) {
                val formatted = "₹" + java.text.NumberFormat.getIntegerInstance(java.util.Locale("en", "IN")).format(targetPrice)
                savedTargetPrices[productId] = formatted
                TargetAlertResult(
                    isSuccess = true,
                    message = responseJson.optString("message", "Target price alert saved successfully"),
                    productId = productId,
                    targetPrice = targetPrice,
                    targetPriceFormatted = formatted,
                    isActive = true
                )
            } else {
                val errorMsg = responseJson?.optString("message") ?: "Failed to save alert. Check network connection."
                TargetAlertResult(
                    isSuccess = false,
                    message = errorMsg,
                    productId = productId
                )
            }
        }
    }

    suspend fun fetchTargetPriceRemote(productId: String): TargetAlertResult? {
        return withContext(Dispatchers.IO) {
            if (!SecureTokenStorage.hasValidToken()) return@withContext null
            val responseJson = AuthNetworkClient.authenticatedGet("price_tracking/get_alert.php?product_id=$productId")
            if (responseJson != null && (responseJson.optBoolean("success", false) || responseJson.optString("status") == "success")) {
                val hasAlert = responseJson.optBoolean("has_alert", false)
                if (hasAlert) {
                    val dataObj = responseJson.optJSONObject("data")
                    if (dataObj != null) {
                        val tp = dataObj.optInt("target_price", 0)
                        val formatted = dataObj.optString("target_price_formatted", "₹$tp")
                        savedTargetPrices[productId] = formatted
                        return@withContext TargetAlertResult(
                            isSuccess = true,
                            message = "Alert found",
                            productId = productId,
                            targetPrice = tp,
                            targetPriceFormatted = formatted,
                            isActive = dataObj.optInt("is_active", 1) == 1
                        )
                    }
                }
            }
            null
        }
    }

    suspend fun removeTargetPriceRemote(productId: String): Boolean {
        return withContext(Dispatchers.IO) {
            if (!SecureTokenStorage.hasValidToken()) return@withContext false
            val payload = JSONObject().apply { put("product_id", productId) }
            val responseJson = AuthNetworkClient.authenticatedPost("price_tracking/remove_alert.php", payload)
            if (responseJson != null && (responseJson.optBoolean("success", false) || responseJson.optString("status") == "success")) {
                savedTargetPrices.remove(productId)
                true
            } else {
                false
            }
        }
    }

    fun getCachedOrLocal(product: Product, platform: String = "all"): ProductPriceHistory {
        val cacheKey = "${product.id}_${platform.lowercase()}"
        val cached = cache[cacheKey]
            ?: if (platform.equals("all", ignoreCase = true)) {
                cache[product.id] ?: cache[product.name]
            } else {
                null
            }
        return cached ?: product.priceHistory
    }

    suspend fun getLivePriceHistory(
        product: Product,
        platform: String = "all",
        forceRefresh: Boolean = false
    ): ProductPriceHistory {
        return withContext(Dispatchers.IO) {
            val normalizedPlatform = platform.trim().lowercase().ifEmpty { "all" }
            val cacheKey = "${product.id}_$normalizedPlatform"

            val cached = cache[cacheKey]
            if (cached != null && cached.isLiveSynced && !forceRefresh && !cached.isError) {
                return@withContext cached
            }

            try {
                val candidateUrls = mutableListOf<String>()
                AuthNetworkClient.customBaseUrl?.let { candidateUrls.add(it) }
                candidateUrls.addAll(AuthNetworkClient.CANDIDATE_URLS)
                candidateUrls.add("http://127.0.0.1/smartcart_backend")
                candidateUrls.add("http://localhost/smartcart_backend")

                var jsonResult: JSONObject? = null

                val platformParam = if (normalizedPlatform != "all") {
                    "&platform=${Uri.encode(normalizedPlatform)}"
                } else {
                    ""
                }

                for (base in candidateUrls.distinct()) {
                    try {
                        val endpoint = "$base/price_tracking/history.php?product_id=${product.id}$platformParam&product_name=${Uri.encode(product.name)}"
                        val url = URL(endpoint)
                        val conn = url.openConnection() as HttpURLConnection
                        if (url.host.contains("ngrok", ignoreCase = true)) {
                            conn.setRequestProperty("ngrok-skip-browser-warning", "true")
                        }
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
                        // try next candidate base URL
                    }
                }

                if (jsonResult != null) {
                    val pObj = jsonResult.optJSONObject("product") ?: JSONObject()
                    val hArray = jsonResult.optJSONArray("history")

                    val curPrice = pObj.optInt("numeric_price", product.numericPrice)
                    val liveSync = jsonResult.optBoolean("live_sync", false)
                    val lastUpdated = jsonResult.optString("last_updated_text", "No historical price observations recorded yet")
                    val respPlatform = jsonResult.optString("platform", normalizedPlatform)
                    val historyStatus = jsonResult.optString("history_status", "no_history")
                    val recordCount = jsonResult.optInt("record_count", 0)

                    val earliestObservation = if (jsonResult.has("earliest_observation") && !jsonResult.isNull("earliest_observation")) {
                        jsonResult.optString("earliest_observation")
                    } else null

                    val latestObservation = if (jsonResult.has("latest_observation") && !jsonResult.isNull("latest_observation")) {
                        jsonResult.optString("latest_observation")
                    } else null

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
                            val itemPlatform = item.optString("platform", respPlatform)
                            val obsAt = if (item.has("observed_at") && !item.isNull("observed_at")) item.optString("observed_at") else null
                            val recDate = if (item.has("recorded_date") && !item.isNull("recorded_date")) item.optString("recorded_date") else null
                            val origPrice = if (item.has("original_price") && !item.isNull("original_price")) item.optInt("original_price") else null
                            val discPct = if (item.has("discount_percent") && !item.isNull("discount_percent")) item.optInt("discount_percent") else null
                            val inStock = item.optBoolean("in_stock", true)
                            val stockStatus = item.optString("stock_status", "in_stock")
                            val seller = if (item.has("seller_name") && !item.isNull("seller_name")) item.optString("seller_name") else null
                            val prodUrl = if (item.has("product_url") && !item.isNull("product_url")) item.optString("product_url") else null
                            val pItemId = if (item.has("platform_item_id") && !item.isNull("platform_item_id")) item.optString("platform_item_id") else null

                            points.add(
                                PricePoint(
                                    label = lbl,
                                    price = priceVal,
                                    formattedPrice = formatted,
                                    isHighest = isHigh,
                                    isLowest = isLow,
                                    platform = itemPlatform,
                                    observedAt = obsAt,
                                    recordedDate = recDate,
                                    originalPrice = origPrice,
                                    discountPercent = discPct,
                                    inStock = inStock,
                                    stockStatus = stockStatus,
                                    sellerName = seller,
                                    productUrl = prodUrl,
                                    platformItemId = pItemId
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
                        isLiveSynced = true,
                        lastUpdatedText = lastUpdated,
                        platform = respPlatform,
                        historyStatus = historyStatus,
                        recordCount = recordCount,
                        earliestObservation = earliestObservation,
                        latestObservation = latestObservation,
                        isError = false,
                        errorMessage = null
                    )

                    cache[cacheKey] = history
                    if (normalizedPlatform == "all") {
                        cache[product.id] = history
                        cache[product.name] = history
                    }
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
                lastUpdatedText = "Unable to connect to price history server (Offline)",
                platform = normalizedPlatform,
                historyStatus = "no_history",
                recordCount = 0,
                isError = true,
                errorMessage = "Unable to connect to price history server. Please verify your connection."
            )
        }
    }
}
