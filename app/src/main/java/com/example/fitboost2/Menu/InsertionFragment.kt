package com.example.fitboost2.Menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.fitboost2.R
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class InsertionFragment : Fragment() {

    private lateinit var etProductName: EditText
    private lateinit var etProductCalories: EditText
    private lateinit var etProductFat: EditText
    private lateinit var etProductProtein: EditText
    private lateinit var etProductCarbs: EditText
    private lateinit var btnSaveData: Button

    private lateinit var dbRef: DatabaseReference


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_insertion, container, false)

        etProductName = view.findViewById(R.id.etProductName)
        etProductCalories = view.findViewById(R.id.etProductCalories)
        etProductFat = view.findViewById(R.id.etProductFat)
        etProductProtein = view.findViewById(R.id.etProductProtein)
        etProductCarbs = view.findViewById(R.id.etProductCarbs)
        btnSaveData = view.findViewById(R.id.btnSave)

        dbRef = FirebaseDatabase.getInstance().getReference("FitBoost/Products")

        btnSaveData.setOnClickListener {
            saveProductData()
        }

        return view
    }

    private fun saveProductData() {

        //getting values
        val productName = etProductName.text.toString()
        val productCalories = etProductCalories.text.toString().toDouble()
        val productFat = etProductFat.text.toString().toDouble()
        val productProtein = etProductProtein.text.toString().toDouble()
        val productCarbs = etProductCarbs.text.toString().toDouble()

        if (productName.isEmpty()) {
            etProductName.error = "Please enter name"
        }
        if (productCalories == 0.0) {
            etProductCalories.error = "Please enter Calories"
        }
        if (productFat == 0.0) {
            etProductFat.error = "Please enter Fat"
        }
        if (productProtein == 0.0) {
            etProductProtein.error = "Please enter Protein"
        }
        if (productCarbs == 0.0) {
            etProductCarbs.error = "Please enter Carbs"
        }

        val productId = dbRef.push().key!!

        val product = ProductModel(
            productId,
            productName,
            productCalories,
            productFat,
            productProtein,
            productCarbs
        )

        dbRef.child(productId).setValue(product)
            .addOnCompleteListener {
                Toast.makeText(
                    requireContext(),
                    "Data inserted successfully",
                    Toast.LENGTH_LONG
                ).show()

                etProductName.text.clear()
                etProductCalories.text.clear()
                etProductFat.text.clear()
                etProductProtein.text.clear()
                etProductCarbs.text.clear()

            }.addOnFailureListener { err ->
                Toast.makeText(
                    requireContext(),
                    "Error ${err.message}",
                    Toast.LENGTH_LONG
                ).show()
            }

    }

}
