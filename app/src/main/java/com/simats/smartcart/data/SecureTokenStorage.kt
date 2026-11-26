package com.simats.smartcart.data

import android.content.Context
import android.content.SharedPreferences
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

object SecureTokenStorage {
    private const val ANDROID_KEYSTORE = "AndroidKeyStore"
    private const val KEY_ALIAS = "smartcart_auth_master_key"
    private const val PREFS_NAME = "com.simats.smartcart.auth_secure_prefs"
    private const val KEY_ENCRYPTED_TOKEN = "encrypted_auth_token"
    private const val KEY_TOKEN_EXPIRES_AT = "token_expires_at"
    private const val KEY_STORED_USER_ID = "stored_user_id"
    private const val GCM_IV_LENGTH = 12
    private const val GCM_TAG_LENGTH = 128

    @Volatile
    private var prefs: SharedPreferences? = null

    @Volatile
    private var inMemoryToken: String? = null

    @Volatile
    private var inMemoryExpiresAt: String? = null

    @Volatile
    private var inMemoryUserId: Int? = null

    fun init(context: Context) {
        if (prefs == null) {
            synchronized(this) {
                if (prefs == null) {
                    try {
                        prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                        loadPersistedToken()
                    } catch (_: Throwable) {
                        // Fall back to memory in test or non-standard environments
                    }
                }
            }
        }
    }

    private fun getOrCreateKey(): SecretKey {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
        if (!keyStore.containsAlias(KEY_ALIAS)) {
            val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE)
            val parameterSpec = KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256)
                .build()
            keyGenerator.init(parameterSpec)
            keyGenerator.generateKey()
        }
        val entry = keyStore.getEntry(KEY_ALIAS, null) as KeyStore.SecretKeyEntry
        return entry.secretKey
    }

    private fun encrypt(plainText: String): String {
        val secretKey = getOrCreateKey()
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        val iv = cipher.iv
        val cipherText = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))
        val combined = ByteArray(iv.size + cipherText.size)
        System.arraycopy(iv, 0, combined, 0, iv.size)
        System.arraycopy(cipherText, 0, combined, iv.size, cipherText.size)
        return Base64.encodeToString(combined, Base64.NO_WRAP)
    }

    private fun decrypt(encryptedBase64: String): String? {
        return try {
            val combined = Base64.decode(encryptedBase64, Base64.NO_WRAP)
            if (combined.size <= GCM_IV_LENGTH) return null
            val iv = combined.copyOfRange(0, GCM_IV_LENGTH)
            val cipherText = combined.copyOfRange(GCM_IV_LENGTH, combined.size)
            val secretKey = getOrCreateKey()
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            val spec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
            cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)
            val decryptedBytes = cipher.doFinal(cipherText)
            String(decryptedBytes, Charsets.UTF_8)
        } catch (_: Throwable) {
            null
        }
    }

    @Synchronized
    fun saveToken(token: String, expiresAt: String? = null, userId: Int? = null) {
        inMemoryToken = token
        inMemoryExpiresAt = expiresAt
        inMemoryUserId = userId
        val p = prefs ?: return
        try {
            val encrypted = encrypt(token)
            p.edit().apply {
                putString(KEY_ENCRYPTED_TOKEN, encrypted)
                putString(KEY_TOKEN_EXPIRES_AT, expiresAt)
                if (userId != null) putInt(KEY_STORED_USER_ID, userId)
                apply()
            }
        } catch (_: Throwable) {
            // Keep in-memory cache if Keystore is unavailable
        }
    }

    @Synchronized
    fun getToken(): String? {
        if (!inMemoryToken.isNullOrBlank()) return inMemoryToken
        loadPersistedToken()
        return inMemoryToken
    }

    private fun loadPersistedToken() {
        val p = prefs ?: return
        try {
            val encrypted = p.getString(KEY_ENCRYPTED_TOKEN, null)
            if (!encrypted.isNullOrBlank()) {
                val decrypted = decrypt(encrypted)
                if (!decrypted.isNullOrBlank()) {
                    inMemoryToken = decrypted
                    inMemoryExpiresAt = p.getString(KEY_TOKEN_EXPIRES_AT, null)
                    val uid = p.getInt(KEY_STORED_USER_ID, -1)
                    if (uid > 0) inMemoryUserId = uid
                }
            }
        } catch (_: Throwable) {
            // Fallback gracefully
        }
    }

    fun getExpiresAt(): String? {
        return inMemoryExpiresAt ?: prefs?.getString(KEY_TOKEN_EXPIRES_AT, null)
    }

    fun getStoredUserId(): Int? {
        return inMemoryUserId ?: run {
            val id = prefs?.getInt(KEY_STORED_USER_ID, -1) ?: -1
            if (id > 0) id else null
        }
    }

    @Synchronized
    fun clearToken() {
        inMemoryToken = null
        inMemoryExpiresAt = null
        inMemoryUserId = null
        try {
            prefs?.edit()?.apply {
                remove(KEY_ENCRYPTED_TOKEN)
                remove(KEY_TOKEN_EXPIRES_AT)
                remove(KEY_STORED_USER_ID)
                apply()
            }
        } catch (_: Throwable) {
            // No-op
        }
    }

    fun hasValidToken(): Boolean {
        return !getToken().isNullOrBlank()
    }
}
