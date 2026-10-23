package com.simats.smartcart.data

data class UserAccount(
    val name: String,
    val email: String,
    val passwordHash: String
)

object UserAccountRepository {
    private val registeredUsers = mutableListOf(
        UserAccount("Aarav Mehta", "aarav.mehta@example.com", "password123"),
        UserAccount("Demo User", "you@example.com", "password123")
    )

    fun isEmailRegistered(email: String): Boolean {
        return registeredUsers.any { it.email.equals(email.trim(), ignoreCase = true) }
    }

    fun registerUser(name: String, email: String, password: String): Boolean {
        if (isEmailRegistered(email)) {
            return false
        }
        registeredUsers.add(UserAccount(name.trim(), email.trim(), password))
        return true
    }

    fun authenticate(email: String, password: String): Boolean {
        return registeredUsers.any {
            it.email.equals(email.trim(), ignoreCase = true) && it.passwordHash == password
        }
    }
}
