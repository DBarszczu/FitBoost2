package com.example.fitboost2.Profile

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
        private const val THEME_PREFS = "theme_prefs"
        private const val THEME_KEY = "theme_key"
    }

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        // Inicjalizuj SharedPreferences
        sharedPreferences = requireActivity().getSharedPreferences(THEME_PREFS, Context.MODE_PRIVATE)

        // Znajdź element Switch i przycisk resetowania motywu
        val themeSwitch = view.findViewById<Switch>(R.id.theme_switch)


        // Ustaw stan przełącznika na podstawie zapisanej wartości w SharedPreferences
        val isDarkTheme = sharedPreferences.getBoolean(THEME_KEY, false)
        themeSwitch.isChecked = isDarkTheme

        // Dodaj nasłuchiwacz zmiany stanu przełącznika
        themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Zmień motyw na ciemny
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                // Zapisz wartość motywu w SharedPreferences
                sharedPreferences.edit().putBoolean(THEME_KEY, true).apply()
            } else {
                // Zmień motyw na domyślny
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                // Zapisz wartość motywu w SharedPreferences
                sharedPreferences.edit().putBoolean(THEME_KEY, false).apply()
            }
        }

        // Dodaj nasłuchiwacz kliknięcia przycisku resetowania motywu


        return view
    }
}
