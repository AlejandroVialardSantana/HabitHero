package com.avs.habithero.ui.activities

import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.avs.habithero.databinding.ActivityLoginBinding
import com.avs.habithero.viewmodel.AuthViewModel

class LoginActivity: AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val authViewModel = AuthViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginPageButton.setOnClickListener {

            val email = binding.emailLoginInput.text.toString()
            val password = binding.passwordLoginInput.text.toString()

            if (validateLogin(email, password)) {
                authViewModel.signIn(email, password, ::onLoginSuccess, ::onLoginFailure)
            }
        }

        binding.backIcon.setOnClickListener {
            finish()
        }
    }

    private fun validateLogin(email: String, password: String): Boolean {
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email address.", Toast.LENGTH_SHORT).show()
            return false
        } else if (password.isEmpty()) {
            Toast.makeText(this, "Please enter your password.", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun onLoginSuccess() {
        Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
        // Navigate to the main activity
    }

    private fun onLoginFailure(error: String) {
        Toast.makeText(this, "Login failed: $error", Toast.LENGTH_SHORT).show()
    }
}