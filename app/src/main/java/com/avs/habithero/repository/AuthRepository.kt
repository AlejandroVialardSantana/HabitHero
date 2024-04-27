package com.avs.habithero.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth

class AuthRepository {

    private var auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun signIn(email: String, password: String): LiveData<Result<Boolean>> {
        val result = MutableLiveData<Result<Boolean>>()

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                result.value = Result.success(true)
            } else {
                result.value =
                    Result.failure(task.exception ?: Exception("An unknown error occurred"))
            }
        }
        return result
    }

    fun signUp(email: String, password: String): LiveData<Result<Boolean>> {
        val result = MutableLiveData<Result<Boolean>>()

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                result.value = Result.success(true)
            } else {
                result.value =
                    Result.failure(task.exception ?: Exception("An unknown error occurred"))
            }
        }
        return result
    }

    fun signUpWithGoogle(credential: AuthCredential): LiveData<Result<Boolean>> {
        val result = MutableLiveData<Result<Boolean>>()

        auth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                result.value = Result.success(true)
            } else {
                result.value =
                    Result.failure(task.exception ?: Exception("An unknown error occurred"))
            }
        }
        return result
    }
}