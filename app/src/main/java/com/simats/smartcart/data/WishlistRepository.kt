package com.simats.smartcart.data

import androidx.compose.runtime.mutableStateListOf
import java.net.URLDecoder

data class WishlistItem(
    val product: Product,
    val originalPrice: String = "",
    val priceDropEnabled: Boolean = true,
    val savedAt: Long = System.currentTimeMillis()
)

object WishlistRepository {
    private val _items = mutableStateListOf<WishlistItem>()

    val items: List<WishlistItem>
        get() = _items

    init {
        // Initialize default wishlist sample decisions
        val jbl = ProductCatalog.findProductByNameOrNull("JBL Tune 510BT")
        val soundcore = ProductCatalog.findProductByNameOrNull("Soundcore H30i")
        if (jbl != null) {
            _items.add(
                WishlistItem(
                    product = jbl,
                    originalPrice = "₹2,999",
                    priceDropEnabled = true
                )
            )
        }
        if (soundcore != null) {
            _items.add(
                WishlistItem(
                    product = soundcore,
                    originalPrice = "₹2,699",
                    priceDropEnabled = true
                )
            )
        }
    }

    fun isWishlisted(productName: String): Boolean {
        if (productName.isBlank()) return false
        val cleanName = try {
            URLDecoder.decode(productName, "UTF-8")
        } catch (_: Exception) {
            productName
        }.trim().lowercase()
        return _items.any { 
            val itemClean = it.product.name.trim().lowercase()
            itemClean == cleanName || itemClean.contains(cleanName) || cleanName.contains(itemClean)
        }
    }

    fun isWishlisted(product: Product): Boolean {
        return isWishlisted(product.name)
    }

    fun add(product: Product) {
        if (!isWishlisted(product.name)) {
            val originalPriceStr = if (product.numericPrice > 0) {
                val markedUp = (product.numericPrice * 1.12).toInt()
                "₹" + String.format("%,d", markedUp)
            } else {
                ""
            }
            _items.add(
                0, // Add new items at the top so the latest wishlisted item is immediately visible
                WishlistItem(
                    product = product,
                    originalPrice = originalPriceStr,
                    priceDropEnabled = true
                )
            )
        }
    }

    fun remove(productName: String) {
        if (productName.isBlank()) return
        val cleanName = try {
            URLDecoder.decode(productName, "UTF-8")
        } catch (_: Exception) {
            productName
        }.trim().lowercase()
        _items.removeAll { 
            val itemClean = it.product.name.trim().lowercase()
            itemClean == cleanName || itemClean.contains(cleanName) || cleanName.contains(itemClean)
        }
    }

    fun remove(product: Product) {
        remove(product.name)
    }

    fun toggleWishlist(product: Product): Boolean {
        return if (isWishlisted(product.name)) {
            remove(product.name)
            false
        } else {
            add(product)
            true
        }
    }

    fun toggleWishlist(productName: String): Boolean {
        if (productName.isBlank()) return false
        val decoded = try {
            URLDecoder.decode(productName, "UTF-8")
        } catch (_: Exception) {
            productName
        }
        val prod = ProductCatalog.findProductByNameOrNull(decoded)
            ?: ProductCatalog.products.find { 
                it.name.contains(decoded, ignoreCase = true) || decoded.contains(it.name, ignoreCase = true) 
            }
            ?: Product(
                id = "wish_" + decoded.hashCode(),
                name = decoded,
                brand = "SmartCart",
                category = "Mobiles",
                subcategory = "Gadgets",
                price = "₹2,999",
                numericPrice = 2999,
                rating = "4.5",
                specs = "Premium Quality",
                matchScore = "90% match",
                matchPercentage = 90
            )
        return toggleWishlist(prod)
    }

    fun setPriceDrop(productName: String, enabled: Boolean) {
        val cleanName = productName.trim().lowercase()
        val index = _items.indexOfFirst { 
            val itemClean = it.product.name.trim().lowercase()
            itemClean == cleanName || itemClean.contains(cleanName) || cleanName.contains(itemClean)
        }
        if (index >= 0) {
            val current = _items[index]
            _items[index] = current.copy(priceDropEnabled = enabled)
        }
    }
}
