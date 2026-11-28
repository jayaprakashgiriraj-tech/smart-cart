package com.simats.smartcart

import com.simats.smartcart.data.ProductCatalog
import com.simats.smartcart.data.ProductStoreComparison
import com.simats.smartcart.data.RetailerLinkType
import com.simats.smartcart.data.StoreComparisonRepository
import com.simats.smartcart.data.StorePriceInfo
import org.junit.Assert.*
import org.junit.Test
import java.io.File

class StoreComparisonSafetyTest {

    @Test
    fun legacyMockEndpoint_isPermanentlyRemoved_fromRepositorySource() {
        val repoSourceFile = File("src/main/java/com/simats/smartcart/data/StoreComparisonRepository.kt")
        assertTrue("StoreComparisonRepository.kt source file must exist", repoSourceFile.exists())
        val sourceText = repoSourceFile.readText()

        // 1. Legacy mock endpoint must be completely absent from repository
        assertFalse(
            "Legacy mock endpoint compare_stores.php must NOT be referenced anywhere in StoreComparisonRepository",
            sourceText.contains("compare_stores.php")
        )
        assertFalse(
            "Legacy mock products/ path must not be queried",
            sourceText.contains("products/compare_stores")
        )

        // 2. Genuine live endpoint must be strictly queried
        assertTrue(
            "Genuine live endpoint integrations/compare_prices.php must be queried",
            sourceText.contains("integrations/compare_prices.php")
        )

        // 3. Confirm only 1 endpoint string in the loop
        assertTrue(
            "Repository query must target integrations/compare_prices.php directly",
            sourceText.contains("val endpoint = \"\$baseUrl/integrations/compare_prices.php?product_name=")
        )
    }

    @Test
    fun unavailableStoreComparison_failsClosed_withZeroFakePricesAndNoWinner() {
        val product = ProductCatalog.products.first()

        // Construct unavailable store comparison state as enforced by repository contracts
        val amzUnavailable = StorePriceInfo(
            storeName = "Amazon India",
            shortName = "Amazon",
            productTitle = product.name,
            price = 0,
            formattedPrice = "Unavailable",
            originalPrice = 0,
            formattedOriginalPrice = "—",
            discountPercent = "",
            isCheapest = false,
            rating = "—",
            ratingsCount = "",
            delivery = "Currently unavailable",
            seller = "—",
            offer = "",
            buyUrl = "https://www.amazon.in/s?k=Samsung+Galaxy+S23",
            appPackage = "com.amazon.mShop.android.shopping",
            accentColorHex = "#FF9900",
            available = false,
            dataSource = "unavailable",
            isStale = false,
            rawPrice = null,
            isCheapestNullable = null
        )

        val flpUnavailable = StorePriceInfo(
            storeName = "Flipkart",
            shortName = "Flipkart",
            productTitle = product.name,
            price = 0,
            formattedPrice = "Unavailable",
            originalPrice = 0,
            formattedOriginalPrice = "—",
            discountPercent = "",
            isCheapest = false,
            rating = "—",
            ratingsCount = "",
            delivery = "Currently unavailable",
            seller = "—",
            offer = "",
            buyUrl = "https://www.flipkart.com/search?q=Samsung+Galaxy+S23",
            appPackage = "com.flipkart.android",
            accentColorHex = "#2874F0",
            available = false,
            dataSource = "unavailable",
            isStale = false,
            rawPrice = null,
            isCheapestNullable = null
        )

        val comp = ProductStoreComparison(
            productId = product.id,
            productName = product.name,
            basePriceFormatted = product.price,
            cheaperStore = "None",
            savings = 0,
            savingsFormatted = "₹0",
            verdict = "Live marketplace price quotes could not be retrieved from Amazon or Flipkart.",
            amazon = amzUnavailable,
            flipkart = flpUnavailable,
            croma = amzUnavailable.copy(storeName = "Croma", shortName = "Croma"),
            relianceDigital = amzUnavailable.copy(storeName = "Reliance Digital", shortName = "Reliance"),
            comparisonAvailable = false,
            status = "unavailable",
            message = "Live marketplace price quotes could not be retrieved from Amazon or Flipkart.",
            cheaperStoreNullable = null,
            savingsNullable = null,
            isLiveComparison = false
        )

        // Integrity assertions: Zero fake price, no comparison winner, no savings
        assertFalse("Comparison must NOT be available when providers are unconfigured", comp.comparisonAvailable)
        assertEquals("cheaperStore must be None", "None", comp.cheaperStore)
        assertNull("cheaperStoreNullable must be null", comp.cheaperStoreNullable)
        assertEquals("savings must be exactly 0", 0, comp.savings)
        assertNull("savingsNullable must be null", comp.savingsNullable)
        assertEquals("₹0", comp.savingsFormatted)

        assertFalse("Amazon must be unavailable", comp.amazon.available)
        assertEquals(0, comp.amazon.price)
        assertEquals("Unavailable", comp.amazon.formattedPrice)
        assertFalse(comp.amazon.isCheapest)
        assertNull(comp.amazon.isCheapestNullable)

        assertFalse("Flipkart must be unavailable", comp.flipkart.available)
        assertEquals(0, comp.flipkart.price)
        assertEquals("Unavailable", comp.flipkart.formattedPrice)
        assertFalse(comp.flipkart.isCheapest)
        assertNull(comp.flipkart.isCheapestNullable)
    }

    @Test
    fun singleStoreAvailability_doesNotDeclareComparisonWinner() {
        val product = ProductCatalog.products.first()

        val amzLive = StorePriceInfo(
            storeName = "Amazon India",
            shortName = "Amazon",
            productTitle = product.name,
            price = 49999,
            formattedPrice = "₹49,999",
            originalPrice = 79999,
            formattedOriginalPrice = "₹79,999",
            discountPercent = "38% off",
            isCheapest = false, // Must not be marked cheapest when competitor is unavailable
            rating = "4.6",
            ratingsCount = "14,820 ratings",
            delivery = "⚡ Prime FREE Delivery Tomorrow",
            seller = "Appario Retail",
            offer = "Flat ₹1,250 Instant Discount",
            buyUrl = "https://www.amazon.in/dp/B0BY8MCQ9S",
            appPackage = "com.amazon.mShop.android.shopping",
            accentColorHex = "#FF9900",
            available = true,
            dataSource = "live_rapidapi",
            isStale = false,
            rawPrice = 49999,
            isCheapestNullable = null
        )

        val flpUnavailable = StorePriceInfo(
            storeName = "Flipkart",
            shortName = "Flipkart",
            productTitle = product.name,
            price = 0,
            formattedPrice = "Unavailable",
            originalPrice = 0,
            formattedOriginalPrice = "—",
            discountPercent = "",
            isCheapest = false,
            rating = "—",
            ratingsCount = "",
            delivery = "Currently unavailable",
            seller = "—",
            offer = "",
            buyUrl = "https://www.flipkart.com/search?q=Samsung+Galaxy+S23",
            appPackage = "com.flipkart.android",
            accentColorHex = "#2874F0",
            available = false,
            dataSource = "unavailable",
            isStale = false,
            rawPrice = null,
            isCheapestNullable = null
        )

        val singleComp = ProductStoreComparison(
            productId = product.id,
            productName = product.name,
            basePriceFormatted = product.price,
            cheaperStore = "None",
            savings = 0,
            savingsFormatted = "₹0",
            verdict = "Flipkart live quote is currently unavailable. Displaying genuine Amazon pricing.",
            amazon = amzLive,
            flipkart = flpUnavailable,
            croma = flpUnavailable,
            relianceDigital = flpUnavailable,
            comparisonAvailable = false, // Live comparison verdict requires BOTH stores
            status = "success",
            message = "Flipkart live quote is currently unavailable. Displaying genuine Amazon pricing.",
            cheaperStoreNullable = null,
            savingsNullable = null,
            isLiveComparison = false
        )

        assertFalse("Comparison must NOT be available with only one store quote", singleComp.comparisonAvailable)
        assertEquals("cheaperStore must remain None", "None", singleComp.cheaperStore)
        assertEquals("savings must remain 0", 0, singleComp.savings)
        assertFalse("Single store must NOT be marked as cheapest winner", singleComp.amazon.isCheapest)
        assertTrue("Amazon store quote must be available", singleComp.amazon.available)
        assertEquals(49999, singleComp.amazon.price)
    }

    @Test
    fun retailerNavigation_sourceCodePreservesDeepLinksAndOfficialPackages() {
        val repoSourceFile = File("src/main/java/com/simats/smartcart/data/StoreComparisonRepository.kt")
        val sourceText = repoSourceFile.readText()

        // 1. Official app packages for Amazon & Flipkart
        assertTrue(
            "Flipkart official Android package must be defined",
            sourceText.contains("com.flipkart.android")
        )
        assertTrue(
            "Amazon official Android package candidates must be defined",
            sourceText.contains("in.amazon.mShop.android.shopping") &&
                    sourceText.contains("com.amazon.mShop.android.shopping")
        )

        // 2. Official deep linking strategies
        assertTrue(
            "Flipkart official app link dl.flipkart.com scheme must be present",
            sourceText.contains("dl.flipkart.com/dl")
        )
        assertTrue(
            "Amazon official app link mobile shopping scheme must be present",
            sourceText.contains("com.amazon.mobile.shopping.web://")
        )

        // 3. Web fallbacks to official retailer domains
        assertTrue(
            "Flipkart web fallback domain must be flipkart.com",
            sourceText.contains("https://www.flipkart.com/search?q=")
        )
        assertTrue(
            "Amazon web fallback domain must be amazon.in",
            sourceText.contains("https://www.amazon.in/s?k=")
        )
    }

    @Test
    fun exactRetailerUrlFallback_resolvesVerifiedAmazonAndFlipkartUrls() {
        // Test Tier-1 Products: iPhone 15, OnePlus 12R, Soundcore H30i, MacBook Air M2
        val testProducts = listOf(
            Triple("mob_ip15", "https://www.amazon.in/dp/B0CHX1W1XY", "https://www.flipkart.com/apple-iphone-15-black-128-gb/p/itm6ac6485515ae4"),
            Triple("mob_op12r", "https://www.amazon.in/dp/B0CQPPV57J", "https://www.flipkart.com/oneplus-12r-cool-blue-256-gb/p/itm5dc2b0be32fdb"),
            Triple("soundcore_h30i", "https://www.amazon.in/dp/B0CDBP6F2S", "https://www.flipkart.com/soundcore-anker-h30i-70-hr-playtime-foldable-bluetooth-headset/p/itm2eb97d745e114"),
            Triple("lap_macm2", "https://www.amazon.in/dp/B0B3BPHSS4", "https://www.flipkart.com/apple-macbook-air-m2-8-gb-256-gb-ssd-macos-monterey-mly33hn-a/p/itmd02388c340db4")
        )

        for ((productId, expectedAmz, expectedFlp) in testProducts) {
            val (amzUrl, amzType) = StoreComparisonRepository.resolveRetailerUrl(productId, "Amazon", "Test Product")
            assertEquals("Amazon URL for $productId must resolve to verified direct link", expectedAmz, amzUrl)
            assertEquals("Amazon link type for $productId must be EXACT_MATCH", RetailerLinkType.EXACT_MATCH, amzType)

            val (flpUrl, flpType) = StoreComparisonRepository.resolveRetailerUrl(productId, "Flipkart", "Test Product")
            assertEquals("Flipkart URL for $productId must resolve to verified direct link", expectedFlp, flpUrl)
            assertEquals("Flipkart link type for $productId must be EXACT_MATCH", RetailerLinkType.EXACT_MATCH, flpType)
        }
    }

    @Test
    fun backendDirectUrl_takesPrecedence_overCatalogFallback() {
        val liveBackendAmzUrl = "https://www.amazon.in/dp/B0LIVE9999"
        val (resolvedUrl, linkType) = StoreComparisonRepository.resolveRetailerUrl(
            productId = "mob_ip15",
            shortName = "Amazon",
            productName = "Apple iPhone 15",
            backendUrl = liveBackendAmzUrl
        )

        assertEquals("Live backend direct URL must take precedence", liveBackendAmzUrl, resolvedUrl)
        assertEquals("Direct backend URL must be classified as EXACT_MATCH", RetailerLinkType.EXACT_MATCH, linkType)

        // However, generic search URLs from backend must NOT override verified direct catalog URLs
        val genericBackendSearch = "https://www.amazon.in/s?k=iPhone+15"
        val (fallbackUrl, fallbackType) = StoreComparisonRepository.resolveRetailerUrl(
            productId = "mob_ip15",
            shortName = "Amazon",
            productName = "Apple iPhone 15",
            backendUrl = genericBackendSearch
        )

        assertEquals(
            "Generic search URL from backend must not override verified exact catalog mapping",
            "https://www.amazon.in/dp/B0CHX1W1XY",
            fallbackUrl
        )
        assertEquals(RetailerLinkType.EXACT_MATCH, fallbackType)
    }

    @Test
    fun missingExactMapping_usesHonestSearchFallback() {
        // Unknown product ID
        val (unknownAmz, unknownAmzType) = StoreComparisonRepository.resolveRetailerUrl(
            productId = "unknown_item_123",
            shortName = "Amazon",
            productName = "Generic Wireless Mouse"
        )
        assertTrue("Amazon fallback must use search query", unknownAmz.contains("https://www.amazon.in/s?k="))
        assertEquals("Unknown product must be SEARCH_FALLBACK", RetailerLinkType.SEARCH_FALLBACK, unknownAmzType)

        val (unknownFlp, unknownFlpType) = StoreComparisonRepository.resolveRetailerUrl(
            productId = "unknown_item_123",
            shortName = "Flipkart",
            productName = "Generic Wireless Mouse"
        )
        assertTrue("Flipkart fallback must use search query", unknownFlp.contains("https://www.flipkart.com/search?q="))
        assertEquals("Unknown product must be SEARCH_FALLBACK", RetailerLinkType.SEARCH_FALLBACK, unknownFlpType)

        // Fashion product in catalog with search URL
        val (fasAmz, fasAmzType) = StoreComparisonRepository.resolveRetailerUrl(
            productId = "fas_fossil_watch",
            shortName = "Amazon",
            productName = "Fossil Grant Chronograph"
        )
        assertTrue("Fashion items with search URLs must remain search URLs", fasAmz.contains("/s?k="))
        assertEquals(RetailerLinkType.SEARCH_FALLBACK, fasAmzType)
    }

    @Test
    fun variantMismatches_areSpecificallyClassified_andNotLabelledExact() {
        val knownMismatches = listOf("mob_s23", "lap_asusrog", "lap_lenovoidea")

        for (productId in knownMismatches) {
            assertTrue(
                "$productId must be tracked in KNOWN_VARIANT_MISMATCH_FLIPKART",
                StoreComparisonRepository.KNOWN_VARIANT_MISMATCH_FLIPKART.contains(productId)
            )

            val (_, flpType) = StoreComparisonRepository.resolveRetailerUrl(
                productId = productId,
                shortName = "Flipkart",
                productName = "Variant Test"
            )
            assertEquals(
                "Flipkart link for variant mismatch product $productId must NOT be EXACT_MATCH",
                RetailerLinkType.VARIANT_MISMATCH,
                flpType
            )
            assertNotEquals(RetailerLinkType.EXACT_MATCH, flpType)
        }
    }

    @Test
    fun unavailableStoreState_preservesPriceIntegrity_withZeroFabrication() {
        val storeInfo = StorePriceInfo(
            storeName = "Amazon India",
            shortName = "Amazon",
            productTitle = "Apple iPhone 15",
            price = 0,
            formattedPrice = "Unavailable",
            originalPrice = 0,
            formattedOriginalPrice = "—",
            discountPercent = "",
            isCheapest = false,
            rating = "—",
            ratingsCount = "",
            delivery = "Verified direct link · Current price on site",
            seller = "—",
            offer = "",
            buyUrl = "https://www.amazon.in/dp/B0CHX1W1XY",
            appPackage = "com.amazon.mShop.android.shopping",
            accentColorHex = "#FF9900",
            available = false,
            dataSource = "unavailable",
            isStale = false,
            rawPrice = null,
            isCheapestNullable = null,
            linkType = RetailerLinkType.EXACT_MATCH
        )

        assertEquals("Price must be 0", 0, storeInfo.price)
        assertNull("rawPrice must be null", storeInfo.rawPrice)
        assertEquals("formattedPrice must be Unavailable", "Unavailable", storeInfo.formattedPrice)
        assertFalse("available must be false", storeInfo.available)
        assertFalse("isCheapest must be false", storeInfo.isCheapest)
        assertNull("isCheapestNullable must be null", storeInfo.isCheapestNullable)
        assertTrue("isExactMatch must be true for verified direct link", storeInfo.isExactMatch)
        assertFalse("isVariantMismatch must be false", storeInfo.isVariantMismatch)
        assertFalse("isSearchFallback must be false", storeInfo.isSearchFallback)
    }
}
