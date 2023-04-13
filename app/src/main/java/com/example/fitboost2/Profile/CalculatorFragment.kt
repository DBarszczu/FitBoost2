package com.example.fitboost2.Profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.fitboost2.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.example.fitboost2.databinding.FragmentCalculatorBinding

class CalculatorFragment : Fragment() {

    private lateinit var binding: FragmentCalculatorBinding
    private val userId = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCalculatorBinding.inflate(inflater, container, false)

        binding.calculateButton.setOnClickListener {
            val ageText = binding.ageEditText.text.toString()
            val weightText = binding.weightEditText.text.toString()
            val heightText = binding.heightEditText.text.toString()

            if (ageText.isEmpty() || weightText.isEmpty() || heightText.isEmpty()) {
                binding.resultTextView.text = "Please fill in all fields."
                return@setOnClickListener
            }

            val age = ageText.toInt()
            val weight = weightText.toDouble()
            val height = heightText.toDouble()

            val goal = when (binding.goalRadioGroup.checkedRadioButtonId) {
                R.id.loseWeightRadioButton -> -300
                R.id.keepWeightRadioButton -> 0
                R.id.gainWeightRadioButton -> 300
                else -> 0
            }

            val gender = when (binding.genderRadioGroup.checkedRadioButtonId) {
                R.id.maleRadioButton -> Gender.MALE
                R.id.femaleRadioButton -> Gender.FEMALE
                else -> Gender.MALE
            }

            val activityLevel = when (binding.activityLevelSpinner.selectedItemPosition) {
                0 -> 1.2 // Siedzący
                1 -> 1.375 // Lekko aktywny
                2 -> 1.55 // Umiarkowanie aktywny
                3 -> 1.725 // Bardzo aktywny
                else -> 1.9 // Extra aktywny
            }

            val bmr = calculateBMR(age, weight, height, gender, activityLevel, goal)

            binding.resultTextView.text = "Your caloric requirement is: $bmr kcal"

            val database = FirebaseDatabase.getInstance()

            // Tworzenie referencji do węzła w bazie danych, w którym chcemy zapisać wynik
            val ref = database.getReference("Users/$userId/bmr")

            // Zapisanie wyniku w bazie danych
            ref.setValue(bmr)
        }

        return binding.root
    }

    private fun calculateBMR(
        age: Int, weight: Double, height: Double, gender: Gender,
        activityLevel: Double, goal: Int
    ): Int {
        val bmr: Double

        if (gender == Gender.MALE) {
            bmr = 88.36 + (13.4 * weight) + (4.8 * height) - (5.7 * age)
        } else {
            bmr = 447.6 + (9.2 * weight) + (3.1 * height) - (4.3 * age)
        }

        return (bmr * activityLevel + goal).toInt()
    }

    enum class Gender {
        MALE, FEMALE
    }
}
