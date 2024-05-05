package com.avs.habithero.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.avs.habithero.models.User
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AuthRepository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

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

    fun signUp(email: String, password: String, username: String): LiveData<Result<Boolean>> {
        val result = MutableLiveData<Result<Boolean>>()

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                auth.currentUser?.let { user ->
                    val newUser = User(user.uid, username, email)
                    saveUserData(newUser, result)
                } ?: run {
                    result.value = Result.failure(Exception("Failed to get user after sign up"))
                }
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

    private fun saveUserData(user: User, result: MutableLiveData<Result<Boolean>>) {
        val userData = hashMapOf(
            "userId" to user.userId,
            "username" to user.username,
            "email" to user.email
        )

        db.collection("users").document(user.userId).set(userData).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                result.value = Result.success(true)
            } else {
                result.value =
                    Result.failure(task.exception ?: Exception("An unknown error occurred"))
            }
        }
    }

    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    fun signOut() {
        auth.signOut()
    }

    fun getCurrentUserId(): String {
        return auth.currentUser?.uid ?: ""
    }

    fun getUsername(): LiveData<String> {
        val result = MutableLiveData<String>()

        db.collection("users").document(getCurrentUserId()).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val username = task.result?.getString("username") ?: ""
                result.value = username
            } else {
                result.value = ""
            }
        }
        return result
    }

    fun resetPassword(email: String): LiveData<Result<Boolean>> {
        val result = MutableLiveData<Result<Boolean>>()

        auth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
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