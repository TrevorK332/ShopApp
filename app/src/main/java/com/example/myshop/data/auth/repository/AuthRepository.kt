package com.example.myshop.data.auth.repository

import com.example.myshop.util.Resource
import com.google.firebase.auth.FirebaseUser
import com.example.myshop.model.User

interface AuthRepository {
    val currentUser: FirebaseUser?
    suspend fun login(email: String, password: String): Resource<FirebaseUser>
    suspend fun signup(user: User, password: String)
    suspend fun retrieveData(): Resource<User>
    suspend fun updateData(user: User)
    suspend fun forgotPassword(email: String): Resource<String>
    fun logout()
}