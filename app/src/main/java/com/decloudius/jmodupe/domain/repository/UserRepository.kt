package com.decloudius.jmodupe.domain.repository

import com.decloudius.jmodupe.domain.model.User

interface UserRepository {

    suspend fun insertUser(user: User)

    suspend fun getUser(): User?

    suspend fun getUserByEmail(email: String): User?

    suspend fun deleteUser()
}
