package com.avs.habithero.ui.activities

import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.avs.habithero.databinding.ActivityResetPasswordBinding
import com.avs.habithero.repositories.AuthRepository
import com.avs.habithero.viewmodel.AuthViewModel

class ResetPasswordActivity: AppCompatActivity() {

    private lateinit var binding: ActivityResetPasswordBinding
    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()
        val authRepository = AuthRepository()
        authViewModel = AuthViewModel(authRepository)

        binding.backIcon.setOnClickListener {
            finish()
        }

        binding.changePasswordButton.setOnClickListener {
            val email = binding.emailResetInput.text.toString()
            if (validateEmail(email)) {
                authViewModel.resetPassword(email).observe(this) { result ->
                    result.onSuccess {
                        onResetPasswordSuccess()
                    }
                    result.onFailure { error ->
                        onResetPasswordFailure(error.message ?: "Ha ocurrido un error desconocido")
                    }
                }
            }
        }
    }

    private fun validateEmail(email: String): Boolean {
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Por favor, ingrese un correo electrónico válido.", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun onResetPasswordSuccess() {
        Toast.makeText(this, "Se ha enviado un correo electrónico para restablecer la contraseña.", Toast.LENGTH_SHORT).show()
    }

    private fun onResetPasswordFailure(errorMessage: String) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
    }
}