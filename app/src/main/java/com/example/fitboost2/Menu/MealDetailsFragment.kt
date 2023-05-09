package com.example.fitboost2.Menu

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment

import com.example.fitboost2.MenuFragment
import com.example.fitboost2.R

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MealDetailsFragment : Fragment() {

    private lateinit var tvProductId: TextView
    private lateinit var tvProductName: TextView
    private lateinit var tvProductCalories: TextView
    private lateinit var tvProductFat: TextView
    private lateinit var tvProductProtein: TextView
    private lateinit var tvProductCarbs: TextView
    private lateinit var btnUpdateMeal: Button
    private lateinit var btnDeleteMeal: Button
    private val userId = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_meal_details, container, false)

        initView(view)
        setValuesToViews()

        btnUpdateMeal.setOnClickListener {
            openUpdateDialog(
                requireArguments().getString("ProductId").toString(),
                requireArguments().getString("ProductName").toString()
            )
        }

        btnDeleteMeal.setOnClickListener {
            deleteRecord(
                requireArguments().getString("ProductId").toString()
            )
        }

        return view
    }

    private fun deleteRecord(
        id: String
    ) {


        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        val dbRef = FirebaseDatabase.getInstance()
            .getReference("Users/$userId/Meals/$year/$month/$dayOfMonth").child(id)
        val mTask = dbRef.removeValue()
        mTask.addOnSuccessListener {
            Toast.makeText(requireContext(), "Product data deleted", Toast.LENGTH_LONG).show()

            val fragment = MenuFragment()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame_layout, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }.addOnFailureListener { error ->
            Toast.makeText(requireContext(), "Deleting Err ${error.message}", Toast.LENGTH_LONG)
                .show()
        }
    }


    private fun initView(view: View) {
        Log.d("MyFragment", "Initializing view...")
        tvProductName = view.findViewById(R.id.tvProductName)
        tvProductCalories = view.findViewById(R.id.tvProductCalories)
        tvProductFat = view.findViewById(R.id.tvProductFat)
        tvProductProtein = view.findViewById(R.id.tvProductProtein)
        tvProductCarbs = view.findViewById(R.id.tvProductCarbs)

        btnUpdateMeal = view.findViewById(R.id.btnUpdateMeal)
        btnDeleteMeal = view.findViewById(R.id.btnDeleteMeal)
    }

    private fun setValuesToViews() {
        tvProductName.text = requireArguments().getString("ProductName")
        tvProductCalories.text =
            String.format("%.1f", requireArguments().getString("ProductCalories")?.toFloat())
        tvProductFat.text =
            String.format("%.1f", requireArguments().getString("ProductFat")?.toFloat())
        tvProductProtein.text =
            String.format("%.1f", requireArguments().getString("ProductProtein")?.toFloat())
        tvProductCarbs.text =
            String.format("%.1f", requireArguments().getString("ProductCarbs")?.toFloat())
    }


    private fun openUpdateDialog(
        ProductId: String,
        ProductName: String
    ) {
        val mDialog = AlertDialog.Builder(requireContext())
        val inflater = layoutInflater
        val mDialogView = inflater.inflate(R.layout.update_dialog_product, null)

        mDialog.setView(mDialogView)

        val etProductName = mDialogView.findViewById<EditText>(R.id.etProductName)
        val etProductCalories = mDialogView.findViewById<EditText>(R.id.etProductCalories)
        val etProductFat = mDialogView.findViewById<EditText>(R.id.etProductFat)
        val etProductProtein = mDialogView.findViewById<EditText>(R.id.etProductProtein)
        val etProductCarbs = mDialogView.findViewById<EditText>(R.id.etProductCarbs)


        val btnUpdateData = mDialogView.findViewById<Button>(R.id.btnUpdateData)

        etProductName.setText(arguments?.getString("ProductName").toString())
        etProductCalories.setText(
            arguments?.getString("ProductCalories", 0.0.toString()).toString()
        )
        etProductFat.setText(arguments?.getString("ProductFat", 0.0.toString()).toString())
        etProductProtein.setText(arguments?.getString("ProductProtein", 0.0.toString()).toString())
        etProductCarbs.setText(arguments?.getString("ProductCarbs", 0.0.toString()).toString())

        mDialog.setTitle("Updating $ProductName Record")

        val alertDialog = mDialog.create()
        alertDialog.show()

        btnUpdateData.setOnClickListener {
            updateEmpData(
                ProductId,
                etProductName.text.toString(),
                etProductCalories.text.toString().toDouble(),
                etProductFat.text.toString().toDouble(),
                etProductProtein.text.toString().toDouble(),
                etProductCarbs.text.toString().toDouble()
            )

            Toast.makeText(requireContext(), "Product Data Updated", Toast.LENGTH_LONG).show()

            //we are setting updated data to our textviews
            tvProductName.text = etProductName.text.toString()
            tvProductCalories.text = etProductCalories.text.toString()
            tvProductFat.text = etProductFat.text.toString()
            tvProductProtein.text = etProductProtein.text.toString()
            tvProductCarbs.text = etProductCarbs.text.toString()




            alertDialog.dismiss()
        }

    }

    private fun updateEmpData(
        id: String,
        name: String,
        calories: Double,
        fat: Double,
        protein: Double,
        carbs: Double
    ) {

        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        val dbRef = FirebaseDatabase.getInstance()
            .getReference("Users/$userId/Meals/$year/$month/$dayOfMonth").child(id)
        val empInfo = ProductModel(id, name, calories, fat, protein, carbs)
        dbRef.setValue(empInfo)
    }
}