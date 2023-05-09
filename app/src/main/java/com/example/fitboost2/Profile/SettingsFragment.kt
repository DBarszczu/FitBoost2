package com.example.fitboost2.Profile

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Switch
import androidx.appcompat.app.AppCompatDelegate
import com.example.fitboost2.R

class SettingsFragment : Fragment() {

    companion object {
        const val THEME_PREFS = "theme_prefs"
        const val THEME_KEY = "theme_key"
    }

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var themeSwitch: Switch

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        // Inicjalizuj SharedPreferences
        sharedPreferences = requireActivity().getSharedPreferences(THEME_PREFS, Context.MODE_PRIVATE)

        // Znajdź element Switch i przycisk resetowania motywu
        themeSwitch = view.findViewById<Switch>(R.id.theme_switch)

        // Ustaw stan przełącznika na podstawie zapisanej wartości w SharedPreferences
        val isDarkTheme = sharedPreferences.getBoolean(THEME_KEY, false)
        themeSwitch.isChecked = isDarkTheme

        // Dodaj nasłuchiwacz zmiany stanu przełącznika
        themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            setDarkModeEnabled(isChecked)
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        // Wczytaj stan ciemnego motywu z SharedPreferences
        val isDarkTheme = sharedPreferences.getBoolean(THEME_KEY, false)
        // Ustaw stan przełącznika na podstawie wczytanej wartości
        themeSwitch.isChecked = isDarkTheme
        // Ustaw tryb ciemnego motywu na podstawie wczytanej wartości
        setDarkModeEnabled(isDarkTheme)
    }

    override fun onPause() {
        super.onPause()
        // Zapisz stan ciemnego motywu w SharedPreferences
        setDarkModeEnabled(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES)
    }

    private fun setDarkModeEnabled(isEnabled: Boolean) {
        if (isEnabled) {
            // Włącz ciemny motyw
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            // Wyłącz ciemny motyw
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
        // Zapisz stan motywu w SharedPreferences
        sharedPreferences.edit().putBoolean(THEME_KEY, isEnabled).apply()
    }
}
