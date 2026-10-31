package com.simats.smartcart.data

data class UserAccount(
    val id: Int = 0,
    val name: String,
    val email: String,
    val profilePhoto: String? = null,
    val passwordHash: String = ""
)

object UserAccountRepository {
    private val registeredUsers = mutableListOf(
        UserAccount(10, "Aarav Mehta", "aarav.mehta@example.com", null, "password123"),
        UserAccount(11, "Demo User", "you@example.com", null, "password123")
    )

    var currentUser: UserAccount? = UserAccount(10, "Aarav Mehta", "aarav.mehta@example.com", null, "password123")

    fun isEmailRegistered(email: String): Boolean {
        return registeredUsers.any { it.email.equals(email.trim(), ignoreCase = true) }
    }

    fun registerUser(name: String, email: String, password: String): Boolean {
        if (isEmailRegistered(email)) {
            return false
        }
        val newUser = UserAccount(
            id = (registeredUsers.maxOfOrNull { it.id } ?: 0) + 1,
            name = name.trim(),
            email = email.trim(),
            passwordHash = password
        )
        registeredUsers.add(newUser)
        currentUser = newUser
        return true
    }

    fun authenticate(email: String, password: String): Boolean {
        val user = registeredUsers.find {
            it.email.equals(email.trim(), ignoreCase = true) && it.passwordHash == password
        }
        if (user != null) {
            currentUser = user
            return true
        }
        return false
    }

    fun resetPassword(email: String, newPassword: String): Boolean {
        val index = registeredUsers.indexOfFirst { it.email.equals(email.trim(), ignoreCase = true) }
        if (index >= 0) {
            val user = registeredUsers[index]
            registeredUsers[index] = user.copy(passwordHash = newPassword)
            return true
        }
        return false
    }

    fun updateProfile(name: String, email: String, photo: String? = null) {
        val current = currentUser
        if (current != null) {
            val updated = current.copy(name = name.trim(), email = email.trim(), profilePhoto = photo)
            currentUser = updated
            val index = registeredUsers.indexOfFirst { it.id == current.id }
            if (index >= 0) {
                registeredUsers[index] = updated
            }
        } else {
            val newAcc = UserAccount(1, name.trim(), email.trim(), photo, "password123")
            currentUser = newAcc
            registeredUsers.add(newAcc)
        }
    }
}
