package com.avs.habithero.ui.fragments

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.avs.habithero.R
import com.avs.habithero.databinding.FragmentSettingsBinding
import com.avs.habithero.repositories.AuthRepository
import com.avs.habithero.ui.activities.HomeActivity
import com.avs.habithero.ui.activities.MainActivity
import com.avs.habithero.viewmodel.AuthViewModel

class SettingsFragment: Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val authRepository = AuthRepository()
    private val authViewModel = AuthViewModel(authRepository)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.changeLanguage.setOnClickListener {
            changeLanguage()
        }

        binding.signOut.setOnClickListener {
            authViewModel.signOut()
            val intent = Intent(requireContext(), MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Método que muestra un diálogo para cambiar el idioma de la aplicación
    private fun changeLanguage() {
        val languages = resources.getStringArray(R.array.languages)
        val languageCodes = resources.getStringArray(R.array.language_codes)
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(getString(R.string.select_language))
        builder.setSingleChoiceItems(languages, -1) { dialog, which ->
            setLocale(languageCodes[which])
            dialog.dismiss()
            restartActivity()
        }
        builder.create().show()
    }

    private fun setLocale(languageCode: String) {
        savePreferences(languageCode)
    }

    // Método que guarda el idioma seleccionado en las preferencias compartidas
    private fun savePreferences(languageCode: String) {
        val sharedPreferences = requireContext().getSharedPreferences("Settings", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("My_Lang", languageCode)
            apply()
        }
    }

    private fun restartActivity() {
        val intent = Intent(requireContext(), HomeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        startActivity(intent)
    }
}