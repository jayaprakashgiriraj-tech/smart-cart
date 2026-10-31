package com.simats.smartcart.network

import com.simats.smartcart.data.UserAccount
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

data class AuthResponse(
    val isSuccess: Boolean,
    val message: String,
    val user: UserAccount? = null
)

// Kept for backward compatibility if referenced
typealias SignUpResponse = AuthResponse

object AuthNetworkClient {
    // Primary candidate endpoints:
    // 1. "http://127.0.0.1:8080/smartcart_backend" -> Physical Android phone connected via USB cable (adb reverse)
    // 2. "http://172.23.49.142/smartcart_backend"   -> Machine LAN IP address on local network
    // 3. "http://10.0.2.2/smartcart_backend"        -> Android Studio Emulator loopback
    // 4. "http://10.0.2.2:8080/smartcart_backend"   -> Android Studio Emulator alternative port
    val CANDIDATE_URLS = listOf(
        "http://127.0.0.1:8080/smartcart_backend",
        "http://172.23.49.142/smartcart_backend",
        "http://10.0.2.2/smartcart_backend",
        "http://10.0.2.2:8080/smartcart_backend"
    )

    // Current active or manual URL override
    var customBaseUrl: String? = null

    @Volatile
    private var detectedBaseUrl: String? = null

    fun getActiveUrl(): String {
        return customBaseUrl ?: detectedBaseUrl ?: CANDIDATE_URLS.first()
    }

    suspend fun registerUser(name: String, email: String, password: String): AuthResponse {
        return withContext(Dispatchers.IO) {
            val jsonParam = JSONObject().apply {
                put("name", name.trim())
                put("email", email.trim())
                put("password", password)
            }
            executeWithFallback("auth/signup.php", jsonParam)
        }
    }

    suspend fun loginUser(email: String, password: String): AuthResponse {
        return withContext(Dispatchers.IO) {
            val jsonParam = JSONObject().apply {
                put("email", email.trim())
                put("password", password)
            }
            executeWithFallback("auth/login.php", jsonParam)
        }
    }

    suspend fun resetPassword(email: String, newPassword: String): AuthResponse {
        return withContext(Dispatchers.IO) {
            val jsonParam = JSONObject().apply {
                put("email", email.trim())
                put("new_password", newPassword)
            }
            executeWithFallback("auth/reset_password.php", jsonParam)
        }
    }

    private fun executeWithFallback(relativeEndpoint: String, jsonParam: JSONObject): AuthResponse {
        // If a manual or previously detected working URL exists, try it first
        val preferred = customBaseUrl ?: detectedBaseUrl
        if (preferred != null) {
            val result = tryPostRequest("$preferred/$relativeEndpoint", jsonParam)
            if (result != null) {
                detectedBaseUrl = preferred
                return result
            }
            // If failed, reset detected and try others
            detectedBaseUrl = null
        }

        // Try candidate URLs in order
        for (baseUrl in CANDIDATE_URLS) {
            if (baseUrl == preferred) continue
            val fullUrl = "$baseUrl/$relativeEndpoint"
            val result = tryPostRequest(fullUrl, jsonParam)
            if (result != null) {
                detectedBaseUrl = baseUrl
                return result
            }
        }

        return AuthResponse(
            isSuccess = false,
            message = "Unable to connect to XAMPP backend. If testing on a physical phone, keep USB connected and run 'adb reverse tcp:8080 tcp:80' in your terminal."
        )
    }

    private fun tryPostRequest(fullUrl: String, jsonParam: JSONObject): AuthResponse? {
        var conn: HttpURLConnection? = null
        return try {
            val url = URL(fullUrl)
            conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
            conn.setRequestProperty("Accept", "application/json")
            conn.connectTimeout = 3000
            conn.readTimeout = 5000
            conn.doOutput = true
            conn.doInput = true

            val os = OutputStreamWriter(conn.outputStream, "UTF-8")
            os.write(jsonParam.toString())
            os.flush()
            os.close()

            val responseCode = conn.responseCode
            val inputStream = if (responseCode in 200..299) {
                conn.inputStream
            } else {
                conn.errorStream ?: conn.inputStream
            }

            val reader = BufferedReader(InputStreamReader(inputStream, "UTF-8"))
            val responseStringBuilder = StringBuilder()
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                responseStringBuilder.append(line)
            }
            reader.close()

            val rawResponse = responseStringBuilder.toString().trim()
            if (rawResponse.isEmpty()) {
                return AuthResponse(
                    isSuccess = false,
                    message = "Server returned empty response (HTTP $responseCode)"
                )
            }

            val responseJson = JSONObject(rawResponse)
            val isSuccess = responseJson.optBoolean("success", false) ||
                    responseJson.optString("status").equals("success", ignoreCase = true)
            val message = responseJson.optString("message", if (isSuccess) "Success" else "Request failed")

            val userObj = responseJson.optJSONObject("user")
            val user = if (userObj != null) {
                UserAccount(
                    id = userObj.optInt("id", 0),
                    name = userObj.optString("name", ""),
                    email = userObj.optString("email", ""),
                    profilePhoto = if (userObj.has("profile_photo") && !userObj.isNull("profile_photo")) userObj.getString("profile_photo") else null
                )
            } else null

            AuthResponse(
                isSuccess = isSuccess,
                message = message,
                user = user
            )
        } catch (e: Exception) {
            // Connection failed for this candidate host
            null
        } finally {
            conn?.disconnect()
        }
    }
}
