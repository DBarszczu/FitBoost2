package com.example.fitboost2.Profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.fitboost2.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat

class BodyMeasurementsFragment : Fragment() {

    // Referencja do bazy danych Firebase
    private lateinit var database: FirebaseDatabase

    // Referencja do węzła z wagami użytkownika w Firebase Realtime Database
    private lateinit var userWeightsRef: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_body_measurements, container, false)

        // Inicjalizacja bazy danych Firebase
        database = FirebaseDatabase.getInstance()

        // Pobranie ID użytkownika
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        // Utworzenie referencji do węzła z wagami użytkownika
        userWeightsRef = database.getReference("Users/$userId/Weights")

        // Przycisk do dodawania wagi użytkownika
        val addWeightButton = view.findViewById<Button>(R.id.addWeightButton)
        addWeightButton.setOnClickListener {
            addWeight()
        }

        return view
    }

    // Dodanie wagi użytkownika do Firebase Realtime Database
    private fun addWeight() {
        // Pobranie wagi z EditText
        val weightEditText = view?.findViewById<EditText>(R.id.weightEditText)
        val weightString = weightEditText?.text.toString()

        // Sprawdzenie, czy wprowadzono wartość wagową
        if (weightString.isEmpty()) {
            Toast.makeText(context, "Wprowadź wartość wagową", Toast.LENGTH_SHORT).show()
            return
        }

        // Konwersja wartości wagowej na Double
        val weight = weightString.toDouble()

        // Dodanie wagi do bazy danych Firebase
        val currentTimeMillis = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("dd_MM_yyyy")
        val formattedDate = dateFormat.format(currentTimeMillis)
        val weightData = Weight(weight, formattedDate)
        userWeightsRef.child(formattedDate).setValue(weightData)

        // Wyświetlenie powiadomienia o dodaniu wagi
        Toast.makeText(context, "Dodano wagę: $weight kg", Toast.LENGTH_SHORT).show()
    }


    // Klasa Weight
    data class Weight(
        val weight: Double = 0.0,
        val data: String = ""
    )

}
