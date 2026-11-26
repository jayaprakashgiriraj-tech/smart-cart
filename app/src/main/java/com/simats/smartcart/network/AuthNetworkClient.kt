package com.simats.smartcart.network

import com.simats.smartcart.data.UserAccount
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.net.HttpURLConnection
import java.net.URL

data class AuthResponse(
    val isSuccess: Boolean,
    val message: String,
    val user: UserAccount? = null,
    val token: String? = null,
    val expiresAt: String? = null
)

// Kept for backward compatibility if referenced
typealias SignUpResponse = AuthResponse

object AuthNetworkClient {
    // Primary candidate endpoints:
    // 1. "http://127.0.0.1:8080/smartcart_backend" -> Physical Android phone connected via USB cable (adb reverse)
    // 2. "http://172.23.48.104/smartcart_backend"  -> Verified active PC LAN IP address on local network
    // 3. "http://10.0.2.2/smartcart_backend"        -> Android Studio Emulator loopback
    // 4. "http://10.0.2.2:8080/smartcart_backend"   -> Android Studio Emulator alternative port
    val CANDIDATE_URLS = listOf(
        "https://oasis-fasting-preflight.ngrok-free.dev/smartcart_backend",
        "http://127.0.0.1:8080/smartcart_backend",
        "http://172.23.48.104/smartcart_backend",
        "http://172.23.48.104:8080/smartcart_backend",
        "http://172.23.52.172/smartcart_backend",
        "http://172.23.52.172:8080/smartcart_backend",
        "http://172.23.49.142/smartcart_backend",
        "http://10.0.2.2/smartcart_backend",
        "http://10.0.2.2:8080/smartcart_backend"
    )

    // Current active or manual URL override
    var customBaseUrl: String? = null

    @Volatile
    private var detectedBaseUrl: String? = null

    fun getActiveUrl(): String {
        return (customBaseUrl ?: detectedBaseUrl ?: CANDIDATE_URLS.first()).trimEnd('/')
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

    suspend fun updateProfile(name: String, email: String): AuthResponse {
        return withContext(Dispatchers.IO) {
            val jsonParam = JSONObject().apply {
                put("email", email.trim())
                put("name", name.trim())
            }
            executeWithFallback("auth/update_profile.php", jsonParam)
        }
    }

    suspend fun uploadProfilePhoto(email: String, file: File): AuthResponse {
        return withContext(Dispatchers.IO) {
            val preferred = (customBaseUrl ?: detectedBaseUrl ?: CANDIDATE_URLS.first()).trimEnd('/')
            val urlString = "$preferred/auth/upload_profile.php"
            try {
                val boundary = "===Boundary===" + System.currentTimeMillis()
                val url = URL(urlString)
                val conn = url.openConnection() as HttpURLConnection
                if (url.host.contains("ngrok", ignoreCase = true)) {
                    conn.setRequestProperty("ngrok-skip-browser-warning", "true")
                }
                conn.requestMethod = "POST"
                conn.doOutput = true
                conn.doInput = true
                conn.useCaches = false
                conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=$boundary")
                conn.connectTimeout = 4000
                conn.readTimeout = 6000

                val outputStream = conn.outputStream
                val writer = PrintWriter(OutputStreamWriter(outputStream, "UTF-8"), true)

                // email field
                writer.append("--$boundary\r\n")
                writer.append("Content-Disposition: form-data; name=\"email\"\r\n\r\n")
                writer.append(email.trim()).append("\r\n")
                writer.flush()

                // profile_image file field
                writer.append("--$boundary\r\n")
                writer.append("Content-Disposition: form-data; name=\"profile_image\"; filename=\"${file.name}\"\r\n")
                writer.append("Content-Type: image/jpeg\r\n\r\n")
                writer.flush()

                val fileInputStream = FileInputStream(file)
                val buffer = ByteArray(4096)
                var bytesRead: Int
                while (fileInputStream.read(buffer).also { bytesRead = it } != -1) {
                    outputStream.write(buffer, 0, bytesRead)
                }
                outputStream.flush()
                fileInputStream.close()

                writer.append("\r\n").flush()
                writer.append("--$boundary--\r\n").flush()
                writer.close()

                val responseCode = conn.responseCode
                val stream = if (responseCode in 200..299) conn.inputStream else conn.errorStream ?: conn.inputStream
                val reader = BufferedReader(InputStreamReader(stream, "UTF-8"))
                val raw = reader.readText().trim()
                reader.close()
                conn.disconnect()

                if (raw.isNotEmpty()) {
                    val json = JSONObject(raw)
                    val isSuccess = json.optBoolean("success", false)
                    val message = json.optString("message", if (isSuccess) "Photo uploaded" else "Upload failed")
                    AuthResponse(isSuccess = isSuccess, message = message)
                } else {
                    AuthResponse(isSuccess = false, message = "Empty response from server")
                }
            } catch (e: Exception) {
                AuthResponse(isSuccess = false, message = e.message ?: "Upload connection failed")
            }
        }
    }

    private fun executeWithFallback(relativeEndpoint: String, jsonParam: JSONObject): AuthResponse {
        val cleanEndpoint = relativeEndpoint.trimStart('/')
        // If a manual or previously detected working URL exists, try it first
        val preferred = customBaseUrl?.trimEnd('/') ?: detectedBaseUrl?.trimEnd('/')
        if (preferred != null) {
            val result = tryPostRequest("$preferred/$cleanEndpoint", jsonParam)
            if (result != null) {
                detectedBaseUrl = preferred
                return result
            }
            // If failed, reset detected and try others
            detectedBaseUrl = null
        }

        // Try candidate URLs in order
        for (baseUrl in CANDIDATE_URLS) {
            val cleanBase = baseUrl.trimEnd('/')
            if (cleanBase == preferred) continue
            val fullUrl = "$cleanBase/$cleanEndpoint"
            val result = tryPostRequest(fullUrl, jsonParam)
            if (result != null) {
                detectedBaseUrl = cleanBase
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
            if (url.host.contains("ngrok", ignoreCase = true)) {
                conn.setRequestProperty("ngrok-skip-browser-warning", "true")
            }
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
            conn.setRequestProperty("Accept", "application/json")
            val activeToken = com.simats.smartcart.data.SecureTokenStorage.getToken()
            if (!activeToken.isNullOrBlank()) {
                conn.setRequestProperty("Authorization", "Bearer $activeToken")
            }
            conn.connectTimeout = 3000
            conn.readTimeout = 5000
            conn.doOutput = true
            conn.doInput = true

            val os = OutputStreamWriter(conn.outputStream, "UTF-8")
            os.write(jsonParam.toString())
            os.flush()
            os.close()

            val responseCode = conn.responseCode
            if (responseCode == 401) {
                com.simats.smartcart.data.SecureTokenStorage.clearToken()
            }
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

            val token = if (responseJson.has("token") && !responseJson.isNull("token")) responseJson.getString("token") else null
            val expiresAt = if (responseJson.has("expires_at") && !responseJson.isNull("expires_at")) responseJson.getString("expires_at") else null

            if (isSuccess && token != null) {
                com.simats.smartcart.data.SecureTokenStorage.saveToken(token, expiresAt, user?.id)
            }

            AuthResponse(
                isSuccess = isSuccess,
                message = message,
                user = user,
                token = token,
                expiresAt = expiresAt
            )
        } catch (e: Exception) {
            // Connection failed for this candidate host
            null
        } finally {
            conn?.disconnect()
        }
    }

    suspend fun logoutUser(): AuthResponse {
        return withContext(Dispatchers.IO) {
            val result = executeWithFallback("auth/logout.php", JSONObject())
            com.simats.smartcart.data.SecureTokenStorage.clearToken()
            com.simats.smartcart.data.UserAccountRepository.currentUser = null
            result
        }
    }

    suspend fun authenticatedPost(relativeEndpoint: String, jsonParam: JSONObject): JSONObject? {
        return withContext(Dispatchers.IO) {
            executeHttpWithFallback(relativeEndpoint, "POST", jsonParam)
        }
    }

    suspend fun authenticatedGet(relativeEndpoint: String): JSONObject? {
        return withContext(Dispatchers.IO) {
            executeHttpWithFallback(relativeEndpoint, "GET", null)
        }
    }

    private fun executeHttpWithFallback(relativeEndpoint: String, method: String, jsonParam: JSONObject?): JSONObject? {
        val cleanEndpoint = relativeEndpoint.trimStart('/')
        val preferred = customBaseUrl?.trimEnd('/') ?: detectedBaseUrl?.trimEnd('/')
        if (preferred != null) {
            val result = tryRawHttpRequest("$preferred/$cleanEndpoint", method, jsonParam)
            if (result != null) {
                detectedBaseUrl = preferred
                return result
            }
            detectedBaseUrl = null
        }

        for (baseUrl in CANDIDATE_URLS) {
            val cleanBase = baseUrl.trimEnd('/')
            if (cleanBase == preferred) continue
            val fullUrl = "$cleanBase/$cleanEndpoint"
            val result = tryRawHttpRequest(fullUrl, method, jsonParam)
            if (result != null) {
                detectedBaseUrl = cleanBase
                return result
            }
        }
        return null
    }

    private fun tryRawHttpRequest(fullUrl: String, method: String, jsonParam: JSONObject?): JSONObject? {
        var conn: HttpURLConnection? = null
        return try {
            val url = URL(fullUrl)
            conn = url.openConnection() as HttpURLConnection
            if (url.host.contains("ngrok", ignoreCase = true)) {
                conn.setRequestProperty("ngrok-skip-browser-warning", "true")
            }
            conn.requestMethod = method
            conn.setRequestProperty("Accept", "application/json")
            val token = com.simats.smartcart.data.SecureTokenStorage.getToken()
            if (!token.isNullOrBlank()) {
                conn.setRequestProperty("Authorization", "Bearer $token")
            }
            conn.connectTimeout = 3000
            conn.readTimeout = 5000

            if (method.equals("POST", ignoreCase = true) && jsonParam != null) {
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
                conn.doOutput = true
                val os = OutputStreamWriter(conn.outputStream, "UTF-8")
                os.write(jsonParam.toString())
                os.flush()
                os.close()
            }

            val responseCode = conn.responseCode
            if (responseCode == 401) {
                com.simats.smartcart.data.SecureTokenStorage.clearToken()
            }

            val inputStream = if (responseCode in 200..299) conn.inputStream else conn.errorStream ?: conn.inputStream
            val reader = BufferedReader(InputStreamReader(inputStream, "UTF-8"))
            val rawResponse = reader.readText().trim()
            reader.close()

            if (rawResponse.isNotEmpty()) {
                JSONObject(rawResponse)
            } else null
        } catch (_: Exception) {
            null
        } finally {
            conn?.disconnect()
        }
    }
}
