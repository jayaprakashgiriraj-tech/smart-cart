package com.simats.smartcart.network

import com.simats.smartcart.data.ProductCatalog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

// -----------------------------------------------------------------------------
// Data Classes for Machine Learning & Shopping Intelligence
// -----------------------------------------------------------------------------
data class MlRecommendationItem(
    val id: String,
    val name: String,
    val brand: String,
    val category: String,
    val subcategory: String,
    val price: String,
    val numericPrice: Double,
    val rating: Double,
    val reviewCount: Int,
    val specs: String,
    val bestFor: String,
    val isProductionCatalog: Boolean,
    val matchScore: Double,
    val matchReason: String
)

data class MlPricePoint(
    val date: String,
    val predictedPrice: Double,
    val expectedChange: Double,
    val expectedChangePercent: Double
)

data class MlPricePrediction(
    val productId: String,
    val productName: String,
    val category: String,
    val currentPrice: Double,
    val forecastDays: Int,
    val priceTrend: String,
    val bestDayToBuy: String,
    val estimatedLowestPrice: Double,
    val forecast: List<MlPricePoint>,
    val disclaimer: String
)

data class MlClassificationResult(
    val inputText: String,
    val predictedCategory: String,
    val confidence: Double,
    val confidencePercentage: String,
    val probabilities: Map<String, Double>
)

data class MlSearchResult(
    val query: String,
    val results: List<MlRecommendationItem>,
    val parsedCategory: String?,
    val parsedBudget: Double?,
    val totalCount: Int
)

data class MlAssistantResponse(
    val sessionId: String,
    val reply: String,
    val intent: String,
    val recommendedProducts: List<MlRecommendationItem>,
    val clarificationNeeded: Boolean = false
)

data class MlCartSwap(
    val originalProductId: String,
    val originalProductName: String,
    val alternativeProductId: String,
    val alternativeProductName: String,
    val savingsAmount: Double,
    val savingsPercent: Double,
    val recommendationReason: String
)

data class MlCartAnalysis(
    val cartTotal: Double,
    val totalQuantity: Int,
    val isOverBudget: Boolean,
    val budgetMessage: String,
    val cheaperAlternatives: List<MlCartSwap>,
    val totalPotentialSavings: Double,
    val complementaryRecommendations: List<MlRecommendationItem>
)

data class MlPriceAnalytics(
    val productId: String,
    val productName: String,
    val currentPrice: Double,
    val highestPrice: Double,
    val lowestPrice: Double,
    val savingsFromPeak: Double,
    val savingsFromPeakPercent: Double,
    val isLowestPrice: Boolean,
    val chartPoints: List<MlPricePoint>,
    val targetAlertEvaluation: Map<String, Any>?,
    val disclaimer: String
)


// -----------------------------------------------------------------------------
// Unified ML & Shopping Intelligence Client
// -----------------------------------------------------------------------------
object MlNetworkClient {
    val CANDIDATE_URLS = listOf(
        "http://10.0.2.2:8000",
        "http://127.0.0.1:8000",
        "http://172.23.52.172:8000",
        "http://172.23.49.142:8000"
    )

    var customBaseUrl: String? = null

    @Volatile
    private var detectedBaseUrl: String? = null

    fun getActiveUrl(): String {
        return customBaseUrl ?: detectedBaseUrl ?: CANDIDATE_URLS.first()
    }

    private fun postJson(endpoint: String, jsonBody: JSONObject): String? {
        val candidates = if (customBaseUrl != null) listOf(customBaseUrl!!) else (listOfNotNull(detectedBaseUrl) + CANDIDATE_URLS).distinct()

        for (baseUrl in candidates) {
            var conn: HttpURLConnection? = null
            try {
                val fullUrl = "$baseUrl$endpoint"
                val url = URL(fullUrl)
                conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.connectTimeout = 2500
                conn.readTimeout = 4000
                conn.doOutput = true
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
                conn.setRequestProperty("Accept", "application/json")

                OutputStreamWriter(conn.outputStream, "UTF-8").use { writer ->
                    writer.write(jsonBody.toString())
                    writer.flush()
                }

                val responseCode = conn.responseCode
                if (responseCode in 200..299) {
                    val stream = conn.inputStream
                    val response = BufferedReader(InputStreamReader(stream)).use { it.readText() }
                    detectedBaseUrl = baseUrl
                    return response
                }
            } catch (e: Exception) {
                // Try next candidate URL
            } finally {
                conn?.disconnect()
            }
        }
        return null
    }

    private fun getJson(endpoint: String): String? {
        val candidates = if (customBaseUrl != null) listOf(customBaseUrl!!) else (listOfNotNull(detectedBaseUrl) + CANDIDATE_URLS).distinct()

        for (baseUrl in candidates) {
            var conn: HttpURLConnection? = null
            try {
                val fullUrl = "$baseUrl$endpoint"
                val url = URL(fullUrl)
                conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "GET"
                conn.connectTimeout = 2000
                conn.readTimeout = 3000
                conn.setRequestProperty("Accept", "application/json")

                val responseCode = conn.responseCode
                if (responseCode in 200..299) {
                    val stream = conn.inputStream
                    val response = BufferedReader(InputStreamReader(stream)).use { it.readText() }
                    detectedBaseUrl = baseUrl
                    return response
                }
            } catch (e: Exception) {
                // Try next candidate
            } finally {
                conn?.disconnect()
            }
        }
        return null
    }

    private fun parseProductItem(obj: JSONObject): MlRecommendationItem {
        return MlRecommendationItem(
            id = obj.optString("id", obj.optString("product_id", "")),
            name = obj.optString("name", obj.optString("product_name", "")),
            brand = obj.optString("brand", ""),
            category = obj.optString("category", ""),
            subcategory = obj.optString("subcategory", ""),
            price = obj.optString("price", ""),
            numericPrice = obj.optDouble("numeric_price", obj.optDouble("current_price", 0.0)),
            rating = obj.optDouble("rating", 4.0),
            reviewCount = obj.optInt("review_count", 0),
            specs = obj.optString("specs", ""),
            bestFor = obj.optString("best_for", ""),
            isProductionCatalog = obj.optInt("is_production_catalog", 1) == 1,
            matchScore = obj.optDouble("match_score", obj.optDouble("relevance_score", 0.9)),
            matchReason = obj.optString("match_reason", "Recommended match")
        )
    }

    // -------------------------------------------------------------------------
    // MODULE 1: AI SEARCH
    // -------------------------------------------------------------------------
    suspend fun searchProducts(
        query: String,
        userId: Int? = null,
        pageSize: Int = 20
    ): MlSearchResult = withContext(Dispatchers.IO) {
        val body = JSONObject().apply {
            put("query", query)
            if (userId != null) put("user_id", userId)
            put("page_size", pageSize)
        }

        val raw = postJson("/api/search", body)
        if (raw != null) {
            try {
                val json = JSONObject(raw)
                val resultsArray = json.optJSONArray("results") ?: JSONArray()
                val items = mutableListOf<MlRecommendationItem>()
                for (i in 0 until resultsArray.length()) {
                    items.add(parseProductItem(resultsArray.getJSONObject(i)))
                }
                val parsed = json.optJSONObject("parsed_query")
                return@withContext MlSearchResult(
                    query = query,
                    results = items,
                    parsedCategory = parsed?.optString("category"),
                    parsedBudget = parsed?.optDouble("max_budget"),
                    totalCount = json.optInt("total_results", items.size)
                )
            } catch (e: Exception) {
                // Fallback to local
            }
        }

        // Local catalog keyword search fallback
        val catalog = ProductCatalog.getAllProducts()
        val qLower = query.lowercase()
        val filtered = catalog.filter {
            it.name.lowercase().contains(qLower) ||
            it.brand.lowercase().contains(qLower) ||
            it.category.lowercase().contains(qLower) ||
            it.specs.lowercase().contains(qLower)
        }.ifEmpty { catalog.take(pageSize) }

        return@withContext MlSearchResult(
            query = query,
            results = filtered.take(pageSize).map {
                MlRecommendationItem(
                    id = it.id, name = it.name, brand = it.brand, category = it.category,
                    subcategory = it.subcategory, price = it.price, numericPrice = it.numericPrice.toDouble(),
                    rating = it.rating.toDouble(), reviewCount = it.reviewCount, specs = it.specs,
                    bestFor = it.bestFor, isProductionCatalog = true, matchScore = 0.85, matchReason = "Catalog match"
                )
            },
            parsedCategory = null,
            parsedBudget = null,
            totalCount = filtered.size
        )
    }

    suspend fun autocomplete(prefix: String): List<String> = withContext(Dispatchers.IO) {
        val raw = getJson("/api/search/autocomplete?q=$prefix")
        if (raw != null) {
            try {
                val json = JSONObject(raw)
                val arr = json.optJSONArray("suggestions") ?: JSONArray()
                val list = mutableListOf<String>()
                for (i in 0 until arr.length()) {
                    list.add(arr.getString(i))
                }
                return@withContext list
            } catch (e: Exception) { }
        }
        return@withContext ProductCatalog.getAllProducts()
            .map { it.name }
            .filter { it.lowercase().startsWith(prefix.lowercase()) }
            .take(6)
    }

    // -------------------------------------------------------------------------
    // MODULE 2: RECOMMENDATIONS
    // -------------------------------------------------------------------------
    suspend fun getRecommendations(
        productId: String? = null,
        userId: Int? = null,
        topK: Int = 6,
        alpha: Double = 0.65
    ): List<MlRecommendationItem> = withContext(Dispatchers.IO) {
        val endpoint = if (userId != null) "/api/recommendations/$userId?top_k=$topK" else "/recommend"
        val raw = if (userId != null) {
            getJson(endpoint)
        } else {
            postJson("/recommend", JSONObject().apply {
                if (productId != null) put("product_id", productId)
                put("top_k", topK)
            })
        }

        if (raw != null) {
            try {
                val json = JSONObject(raw)
                val recsArray = json.optJSONArray("recommendations") ?: JSONArray()
                val items = mutableListOf<MlRecommendationItem>()
                for (i in 0 until recsArray.length()) {
                    items.add(parseProductItem(recsArray.getJSONObject(i)))
                }
                if (items.isNotEmpty()) return@withContext items
            } catch (e: Exception) { }
        }

        // Local fallback if ML server is offline
        val catalogItems = ProductCatalog.getAllProducts()
        val filtered = if (productId != null) {
            val curr = catalogItems.firstOrNull { it.id == productId }
            if (curr != null) {
                catalogItems.filter { it.category == curr.category && it.id != productId }
            } else catalogItems
        } else catalogItems

        return@withContext filtered.take(topK).map { item ->
            MlRecommendationItem(
                id = item.id, name = item.name, brand = item.brand, category = item.category,
                subcategory = item.subcategory, price = item.price, numericPrice = item.numericPrice.toDouble(),
                rating = item.rating.toDouble(), reviewCount = item.reviewCount, specs = item.specs,
                bestFor = item.bestFor, isProductionCatalog = true, matchScore = 0.95,
                matchReason = "Top product in ${item.category}"
            )
        }
    }

    // -------------------------------------------------------------------------
    // MODULE 4: SHOPPING ASSISTANT
    // -------------------------------------------------------------------------
    suspend fun chatAssistant(
        message: String,
        sessionId: String = "app_session",
        userId: Int? = null
    ): MlAssistantResponse = withContext(Dispatchers.IO) {
        val body = JSONObject().apply {
            put("message", message)
            put("session_id", sessionId)
            if (userId != null) put("user_id", userId)
        }

        val raw = postJson("/api/assistant/chat", body)
        if (raw != null) {
            try {
                val json = JSONObject(raw)
                val recsArray = json.optJSONArray("recommended_products") ?: JSONArray()
                val recs = mutableListOf<MlRecommendationItem>()
                for (i in 0 until recsArray.length()) {
                    recs.add(parseProductItem(recsArray.getJSONObject(i)))
                }
                return@withContext MlAssistantResponse(
                    sessionId = json.optString("session_id", sessionId),
                    reply = json.optString("reply", "Here are matching recommendations:"),
                    intent = json.optString("intent", "general"),
                    recommendedProducts = recs,
                    clarificationNeeded = json.optBoolean("clarification_needed", false)
                )
            } catch (e: Exception) { }
        }

        // Local assistant fallback
        val catalog = ProductCatalog.getAllProducts()
        return@withContext MlAssistantResponse(
            sessionId = sessionId,
            reply = "I found some top-rated options from our catalog matching your query:",
            intent = "search_fallback",
            recommendedProducts = catalog.take(3).map {
                MlRecommendationItem(
                    id = it.id, name = it.name, brand = it.brand, category = it.category,
                    subcategory = it.subcategory, price = it.price, numericPrice = it.numericPrice.toDouble(),
                    rating = it.rating.toDouble(), reviewCount = it.reviewCount, specs = it.specs,
                    bestFor = it.bestFor, isProductionCatalog = true, matchScore = 0.9, matchReason = "Top choice"
                )
            }
        )
    }

    // -------------------------------------------------------------------------
    // MODULE 5: PRICE INTELLIGENCE & FORECAST
    // -------------------------------------------------------------------------
    suspend fun predictPrice(
        productId: String,
        daysAhead: Int = 7
    ): MlPricePrediction? = withContext(Dispatchers.IO) {
        val body = JSONObject().apply {
            put("product_id", productId)
            put("days_ahead", daysAhead)
        }

        val raw = postJson("/predict-price", body)
        if (raw != null) {
            try {
                val json = JSONObject(raw)
                val forecastArray = json.getJSONArray("forecast")
                val forecastPoints = mutableListOf<MlPricePoint>()
                for (i in 0 until forecastArray.length()) {
                    val p = forecastArray.getJSONObject(i)
                    forecastPoints.add(
                        MlPricePoint(
                            date = p.getString("date"),
                            predictedPrice = p.getDouble("predicted_price"),
                            expectedChange = p.getDouble("expected_change"),
                            expectedChangePercent = p.getDouble("expected_change_percent")
                        )
                    )
                }

                return@withContext MlPricePrediction(
                    productId = json.getString("product_id"),
                    productName = json.getString("product_name"),
                    category = json.getString("category"),
                    currentPrice = json.getDouble("current_price"),
                    forecastDays = json.getInt("forecast_days"),
                    priceTrend = json.getString("price_trend"),
                    bestDayToBuy = json.getString("best_day_to_buy"),
                    estimatedLowestPrice = json.getDouble("estimated_lowest_price"),
                    forecast = forecastPoints,
                    disclaimer = json.getString("disclaimer")
                )
            } catch (e: Exception) { }
        }

        // Local fallback
        val prod = ProductCatalog.getAllProducts().firstOrNull { it.id == productId } ?: return@withContext null
        val base = prod.numericPrice.toDouble()
        val mockPoints = listOf(
            MlPricePoint("Day 1", base * 0.99, -base * 0.01, -1.0),
            MlPricePoint("Day 2", base * 0.985, -base * 0.015, -1.5),
            MlPricePoint("Day 3", base * 0.98, -base * 0.02, -2.0),
            MlPricePoint("Day 4", base * 0.99, -base * 0.01, -1.0),
            MlPricePoint("Day 5", base * 0.975, -base * 0.025, -2.5),
            MlPricePoint("Day 6", base * 0.97, -base * 0.03, -3.0),
            MlPricePoint("Day 7", base * 0.98, -base * 0.02, -2.0)
        )
        return@withContext MlPricePrediction(
            productId = prod.id,
            productName = prod.name,
            category = prod.category,
            currentPrice = base,
            forecastDays = daysAhead,
            priceTrend = "dropping",
            bestDayToBuy = "Day 6",
            estimatedLowestPrice = base * 0.97,
            forecast = mockPoints,
            disclaimer = "DISCLAIMER: Predicted prices are statistical estimates derived from historical data. Actual marketplace prices fluctuate continuously and are not guaranteed."
        )
    }

    suspend fun getPriceAnalytics(productId: String, days: Int = 60): MlPriceAnalytics? = withContext(Dispatchers.IO) {
        val raw = getJson("/api/prices/$productId?days=$days")
        if (raw != null) {
            try {
                val json = JSONObject(raw)
                val chartArr = json.optJSONArray("chart_points") ?: JSONArray()
                val points = mutableListOf<MlPricePoint>()
                for (i in 0 until chartArr.length()) {
                    val p = chartArr.getJSONObject(i)
                    points.add(
                        MlPricePoint(
                            date = p.optString("date", ""),
                            predictedPrice = p.optDouble("price", 0.0),
                            expectedChange = 0.0,
                            expectedChangePercent = 0.0
                        )
                    )
                }

                return@withContext MlPriceAnalytics(
                    productId = json.getString("product_id"),
                    productName = json.optString("product_name", productId),
                    currentPrice = json.optDouble("current_price", 0.0),
                    highestPrice = json.optDouble("highest_price", 0.0),
                    lowestPrice = json.optDouble("lowest_price", 0.0),
                    savingsFromPeak = json.optDouble("savings_from_peak", 0.0),
                    savingsFromPeakPercent = json.optDouble("savings_from_peak_percent", 0.0),
                    isLowestPrice = json.optBoolean("is_all_time_low", false),
                    chartPoints = points,
                    targetAlertEvaluation = null,
                    disclaimer = json.optString("disclaimer", "")
                )
            } catch (e: Exception) { }
        }
        return@withContext null
    }

    // -------------------------------------------------------------------------
    // MODULE 6: SMART CART OPTIMIZATION
    // -------------------------------------------------------------------------
    suspend fun analyzeCart(
        items: List<Map<String, Any>>,
        userBudget: Double? = null
    ): MlCartAnalysis? = withContext(Dispatchers.IO) {
        val itemsArr = JSONArray()
        for (it in items) {
            itemsArr.put(JSONObject(it))
        }
        val body = JSONObject().apply {
            put("items", itemsArr)
            if (userBudget != null) put("user_budget", userBudget)
        }

        val raw = postJson("/api/cart/analyze", body)
        if (raw != null) {
            try {
                val json = JSONObject(raw)
                val bAnalysis = json.optJSONObject("budget_analysis")
                val isOver = bAnalysis?.optBoolean("is_over_budget", false) ?: false
                val msg = bAnalysis?.optString("message", "Cart analyzed successfully.") ?: "Cart analyzed."

                val swapsArr = json.optJSONArray("cheaper_alternatives") ?: JSONArray()
                val swaps = mutableListOf<MlCartSwap>()
                for (i in 0 until swapsArr.length()) {
                    val s = swapsArr.getJSONObject(i)
                    swaps.add(
                        MlCartSwap(
                            originalProductId = s.optString("original_product_id"),
                            originalProductName = s.optString("original_product_name"),
                            alternativeProductId = s.optString("alternative_product_id"),
                            alternativeProductName = s.optString("alternative_product_name"),
                            savingsAmount = s.optDouble("savings_amount", 0.0),
                            savingsPercent = s.optDouble("savings_percent", 0.0),
                            recommendationReason = s.optString("recommendation_reason")
                        )
                    )
                }

                val compArr = json.optJSONArray("complementary_recommendations") ?: JSONArray()
                val comps = mutableListOf<MlRecommendationItem>()
                for (i in 0 until compArr.length()) {
                    comps.add(parseProductItem(compArr.getJSONObject(i)))
                }

                return@withContext MlCartAnalysis(
                    cartTotal = json.optDouble("cart_total", 0.0),
                    totalQuantity = json.optInt("total_quantity", items.size),
                    isOverBudget = isOver,
                    budgetMessage = msg,
                    cheaperAlternatives = swaps,
                    totalPotentialSavings = json.optDouble("total_potential_savings", 0.0),
                    complementaryRecommendations = comps
                )
            } catch (e: Exception) { }
        }
        return@withContext null
    }

    // -------------------------------------------------------------------------
    // MODULE 7: PRODUCT CLASSIFIER
    // -------------------------------------------------------------------------
    suspend fun classifyProduct(title: String, description: String = ""): MlClassificationResult? = withContext(Dispatchers.IO) {
        val body = JSONObject().apply {
            put("title", title)
            put("description", description)
        }
        val raw = postJson("/classify", body) ?: return@withContext null
        try {
            val json = JSONObject(raw)
            val probMap = mutableMapOf<String, Double>()
            val probsObj = json.optJSONObject("probabilities")
            probsObj?.keys()?.forEach { k ->
                probMap[k] = probsObj.getDouble(k)
            }
            return@withContext MlClassificationResult(
                inputText = json.getString("input_text"),
                predictedCategory = json.getString("predicted_category"),
                confidence = json.getDouble("confidence"),
                confidencePercentage = json.getString("confidence_percentage"),
                probabilities = probMap
            )
        } catch (e: Exception) {
            return@withContext null
        }
    }

    // -------------------------------------------------------------------------
    // MODULE 9: INTERACTIONS & PERSISTENCE
    // -------------------------------------------------------------------------
    suspend fun recordInteraction(
        userId: Int,
        productId: String,
        productName: String,
        category: String,
        interactionType: String,
        weight: Double = 1.0
    ): Boolean = withContext(Dispatchers.IO) {
        val body = JSONObject().apply {
            put("user_id", userId)
            put("product_id", productId)
            put("product_name", productName)
            put("category", category)
            put("interaction_type", interactionType)
            put("weight", weight)
        }
        val raw = postJson("/interactions/record", body)
        return@withContext raw != null
    }
}
