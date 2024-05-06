package com.avs.habithero.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.avs.habithero.repositories.AuthRepository
import com.google.firebase.auth.AuthCredential
import io.grpc.Context

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {

    fun signIn(email: String, password: String): LiveData<Result<Boolean>> {
        return authRepository.signIn(email, password)
    }

    fun signUp(email: String, password: String, username: String): LiveData<Result<Boolean>> {
        return authRepository.signUp(email, password, username)
    }

    fun isUserLoggedIn(): Boolean {
        return authRepository.isUserLoggedIn()
    }

    fun signOut() {
        authRepository.signOut()
    }

    fun getUsername(): LiveData<String> {
        return authRepository.getUsername()
    }

    fun resetPassword(email: String): LiveData<Result<Boolean>> {
        return authRepository.resetPassword(email)
    }

    fun getCurrentUserId(): String {
        return authRepository.getCurrentUserId() ?: ""
    }
}