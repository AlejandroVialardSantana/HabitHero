package com.avs.habithero.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.avs.habithero.repository.AuthRepository
import com.google.firebase.auth.AuthCredential

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {

    fun signIn(email: String, password: String): LiveData<Result<Boolean>> {
        return authRepository.signIn(email, password)
    }

    fun signUp(email: String, password: String, username: String): LiveData<Result<Boolean>> {
        return authRepository.signUp(email, password, username)
    }

    fun signUpWithGoogle(credential: AuthCredential): LiveData<Result<Boolean>> {
        return authRepository.signUpWithGoogle(credential)
    }

    fun isUserLoggedIn(): Boolean {
        return authRepository.isUserLoggedIn()
    }

    fun signOut() {
        authRepository.signOut()
    }
}