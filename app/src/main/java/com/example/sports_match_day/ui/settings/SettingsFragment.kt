package com.example.sports_match_day.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.sports_match_day.databinding.FragmentSettingsBinding
import com.example.sports_match_day.ui.MainActivity
import com.example.sports_match_day.ui.base.BaseFragment
import com.example.sports_match_day.utils.LocaleManager
import com.example.sports_match_day.utils.constants.PreferencesKeys
import com.pixplicity.easyprefs.library.Prefs
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * Created by Kristo on 31-Mar-21
 */
class SettingsFragment : BaseFragment() {

    private val viewModel: SettingsViewModel by viewModel()

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupLanguage()
        setupDebug()
        changeLanguageOpacity()
    }

    private fun setupLanguage() {

        binding.buttonEnglish.setOnClickListener {
            LocaleManager.setEnglishLanguage()
            changeLanguage()
        }

        binding.buttonGreek.setOnClickListener {
            LocaleManager.setGreekLanguage()
            changeLanguage()
        }
    }

    private fun changeLanguageOpacity(){
        if(LocaleManager.isLanguageEnglish()){
            binding.buttonEnglish.alpha = 1f
            binding.buttonGreek.alpha = 0.3f
        }else{
            binding.buttonEnglish.alpha = 0.3f
            binding.buttonGreek.alpha = 1f
        }
    }

    private fun changeLanguage(){
        val intent = Intent(context, MainActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    private fun setupDebug() {
        binding.switchDebug.isChecked =
            Prefs.getBoolean(PreferencesKeys.DEBUG_ON, false)
        binding.switchDebug.setOnClickListener {
            Prefs.putBoolean(PreferencesKeys.DEBUG_ON, binding.switchDebug.isChecked)
        }
    }
}