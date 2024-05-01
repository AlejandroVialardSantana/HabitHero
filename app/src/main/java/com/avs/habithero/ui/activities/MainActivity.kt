package com.avs.habithero.ui.activities

import android.content.Intent
import android.os.Bundle
import com.avs.habithero.databinding.ActivityMainBinding
import com.avs.habithero.repositories.AuthRepository
import com.avs.habithero.viewmodel.AuthViewModel

class MainActivity: BaseActivity() {

    private lateinit var binding: ActivityMainBinding
    private val authRepository = AuthRepository()
    private val authViewModel = AuthViewModel(authRepository)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (authViewModel.isUserLoggedIn()) {
            navigateToHome()
        }

        binding.mainLoginButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.mainSignUpButton.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }

    private fun navigateToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}