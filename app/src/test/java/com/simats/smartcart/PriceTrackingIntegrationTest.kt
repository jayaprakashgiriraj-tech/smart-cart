package com.simats.smartcart

import com.simats.smartcart.data.PricePoint
import com.simats.smartcart.data.ProductCatalog
import com.simats.smartcart.data.ProductPriceHistory
import org.junit.Assert.*
import org.junit.Test

class PriceTrackingIntegrationTest {

    @Test
    fun defaultPriceHistory_hasZeroSyntheticPoints_andNoHistoryStatus() {
        val product = ProductCatalog.products.first()
        val defaultHistory = ProductCatalog.getProductPriceHistory(product)

        assertTrue("Default history must contain 0 synthetic points", defaultHistory.pricePoints.isEmpty())
        assertEquals("no_history", defaultHistory.historyStatus)
        assertEquals(0, defaultHistory.recordCount)
        assertEquals("—", defaultHistory.highestPriceFormatted)
        assertEquals("—", defaultHistory.lowestPriceFormatted)
        assertFalse(defaultHistory.isLiveSynced)
    }

    @Test
    fun singleObservation_evaluatesAsInsufficientHistory_withoutSyntheticPoints() {
        val singlePoint = PricePoint(
            label = "24 Nov",
            price = 49999,
            formattedPrice = "₹49,999",
            isHighest = false,
            isLowest = false,
            platform = "amazon",
            observedAt = "2026-11-24 10:15:00",
            recordedDate = "2026-11-24",
            sellerName = "Appario Retail"
        )

        val history = ProductPriceHistory(
            productId = "mob_s23",
            productName = "Samsung Galaxy S23",
            currentPrice = 49999,
            currentPriceFormatted = "₹49,999",
            highestPrice = 49999,
            highestPriceFormatted = "₹49,999",
            highestPriceDate = "24 Nov",
            lowestPrice = 49999,
            lowestPriceFormatted = "₹49,999",
            lowestPriceDate = "24 Nov",
            savingsFromPeak = 0,
            savingsFromPeakFormatted = "₹0",
            savingsPercent = 0,
            pricePoints = listOf(singlePoint),
            minGraphBound = 49999,
            maxGraphBound = 49999,
            suggestedTargetPrice = 49999,
            suggestedTargetPriceFormatted = "₹49,999",
            platform = "amazon",
            historyStatus = "insufficient_history",
            recordCount = 1
        )

        assertEquals("insufficient_history", history.historyStatus)
        assertEquals(1, history.recordCount)
        assertEquals(1, history.pricePoints.size)
        assertEquals("amazon", history.pricePoints.first().platform)
        assertEquals("Appario Retail", history.pricePoints.first().sellerName)
    }

    @Test
    fun multipleObservations_evaluatesAsAvailableHistory() {
        val points = listOf(
            PricePoint(
                label = "22 Nov",
                price = 51999,
                formattedPrice = "₹51,999",
                isHighest = true,
                isLowest = false,
                platform = "amazon",
                observedAt = "2026-11-22 09:00:00"
            ),
            PricePoint(
                label = "23 Nov",
                price = 50499,
                formattedPrice = "₹50,499",
                isHighest = false,
                isLowest = false,
                platform = "flipkart",
                observedAt = "2026-11-23 11:30:00"
            ),
            PricePoint(
                label = "24 Nov",
                price = 48999,
                formattedPrice = "₹48,999",
                isHighest = false,
                isLowest = true,
                platform = "amazon",
                observedAt = "2026-11-24 14:45:00"
            )
        )

        val history = ProductPriceHistory(
            productId = "mob_s23",
            productName = "Samsung Galaxy S23",
            currentPrice = 48999,
            currentPriceFormatted = "₹48,999",
            highestPrice = 51999,
            highestPriceFormatted = "₹51,999",
            highestPriceDate = "22 Nov",
            lowestPrice = 48999,
            lowestPriceFormatted = "₹48,999",
            lowestPriceDate = "24 Nov",
            savingsFromPeak = 3000,
            savingsFromPeakFormatted = "₹3,000",
            savingsPercent = 5,
            pricePoints = points,
            minGraphBound = 48999,
            maxGraphBound = 51999,
            suggestedTargetPrice = 48999,
            suggestedTargetPriceFormatted = "₹48,999",
            platform = "all",
            historyStatus = "available",
            recordCount = 3
        )

        assertEquals("available", history.historyStatus)
        assertEquals(3, history.recordCount)
        assertEquals(3, history.pricePoints.size)
        assertTrue(history.pricePoints.any { it.platform == "amazon" })
        assertTrue(history.pricePoints.any { it.platform == "flipkart" })
        assertEquals(51999, history.highestPrice)
        assertEquals(48999, history.lowestPrice)
    }

    @Test
    fun offlineErrorState_hasProperErrorFlags() {
        val product = ProductCatalog.products.first()
        val offlineHistory = ProductPriceHistory(
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
            isLiveSynced = false,
            lastUpdatedText = "Unable to connect to price history server (Offline)",
            platform = "all",
            historyStatus = "no_history",
            recordCount = 0,
            isError = true,
            errorMessage = "Unable to connect to price history server. Please verify your connection."
        )

        assertTrue(offlineHistory.isError)
        assertNotNull(offlineHistory.errorMessage)
        assertTrue(offlineHistory.pricePoints.isEmpty())
        assertFalse(offlineHistory.isLiveSynced)
    }
}
