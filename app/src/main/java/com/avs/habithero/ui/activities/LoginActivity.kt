package com.avs.habithero.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.avs.habithero.databinding.ActivityLoginBinding
import com.avs.habithero.repository.AuthRepository
import com.avs.habithero.viewmodel.AuthViewModel

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val authRepository = AuthRepository()
        authViewModel = AuthViewModel(authRepository)


        binding.loginPageButton.setOnClickListener {

            val email = binding.emailLoginInput.text.toString()
            val password = binding.passwordLoginInput.text.toString()

            if (validateLogin(email, password)) {
                authViewModel.signIn(email, password).observe(this) { result ->
                    result.onSuccess {
                        onLoginSuccess()
                    }
                    result.onFailure { error ->
                        onLoginFailure(error.message ?: "Ha ocurrido un error desconocido")
                    }
                }
            }
        }

        binding.signUpLink.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
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
            Toast.makeText(this, "Por favor, ingrese una contraseña.", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun onLoginSuccess() {
        Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
    }

    private fun onLoginFailure(error: String) {
        Toast.makeText(this, "Error: $error", Toast.LENGTH_SHORT).show()
    }
}