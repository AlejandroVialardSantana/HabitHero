package com.avs.habithero.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.avs.habithero.databinding.ActivitySignUpBinding
import com.avs.habithero.repository.AuthRepository
import com.avs.habithero.viewmodel.AuthViewModel

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val authRepository = AuthRepository()
        viewModel = AuthViewModel(authRepository)

        binding.signUpPageButton.setOnClickListener {
            val email = binding.emailRegisterInput.text.toString()
            val password = binding.passwordRegisterInput.text.toString()
            val confirmPassword = binding.confirmPasswordInput.text.toString()
            val username = binding.usernameRegisterInput.text.toString()

            if(validateSignUp(email, password, confirmPassword)) {
                viewModel.signUp(email, password, username).observe(this) { result ->
                    result.onSuccess {
                        onSignUpSuccess()
                    }
                    result.onFailure { error ->
                        onSignUpFailure(error.message ?: "An unknown error occurred")
                    }
                }
            }
        }

        binding.loginLink.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.backIcon.setOnClickListener {
            finish()
        }
    }

    private fun validateSignUp(email: String, password: String, confirmPassword: String): Boolean {
        if(email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show()
            return false
        }

        if(password.length < 6) {
            Toast.makeText(this, "Password should be at least 6 characters long", Toast.LENGTH_SHORT).show()
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show()
            return false
        }

        if(password != confirmPassword) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun onSignUpSuccess() {
        Toast.makeText(this, "Sign up successful", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun onSignUpFailure(error: String) {
        Toast.makeText(this, "Sign up failed: $error", Toast.LENGTH_SHORT).show()
    }
}