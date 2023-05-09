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
import com.example.fitboost2.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.*

class ProductDetailsFragment : Fragment() {

    private lateinit var tvProductId: TextView
    private lateinit var tvProductName: TextView
    private lateinit var tvProductCalories: TextView
    private lateinit var tvProductFat: TextView
    private lateinit var tvProductProtein: TextView
    private lateinit var tvProductCarbs: TextView
    private lateinit var btnUpdate: Button
    private lateinit var btnDelete: Button
    private val userId = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_product_details, container, false)
        initView(view)
        setValuesToViews()

        btnUpdate.setOnClickListener {
            openUpdateDialog(
                requireArguments().getString("ProductId").toString(),
                requireArguments().getString("ProductName").toString()
            )
        }

        btnDelete.setOnClickListener {
            deleteRecord(
                requireArguments().getString("ProductId").toString()
            )
        }

        val addProductBtn = view.findViewById<Button>(R.id.addproducttomeals)
        addProductBtn.setOnClickListener {
            val productId = requireArguments().getString("ProductId").toString()
            val dbRef =
                FirebaseDatabase.getInstance().getReference("FitBoost/Products").child(productId)
            dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val product = snapshot.getValue(ProductModel::class.java)
                    val inflater = LayoutInflater.from(requireContext())
                    val dialogView = inflater.inflate(R.layout.add_product_dialog, null)

                    val builder = AlertDialog.Builder(requireContext()).apply {
                        setTitle("Enter weight (in grams)")
                        setView(dialogView)
                        setPositiveButton("OK") { _, _ ->
                            val weightEditText = dialogView.findViewById<EditText>(R.id.gramedittxt)
                            val weight = weightEditText.text.toString().toDoubleOrNull() ?: 0.0
                            val modifiedProduct = product?.let {
                                ProductModel(
                                    it.ProductId + System.currentTimeMillis(), // add timestamp to the ID to make it unique
                                    it.ProductName,
                                    it.ProductCalories!! * weight / 100,
                                    it.ProductFat!! * weight / 100,
                                    it.ProductProtein!! * weight / 100,
                                    it.ProductCarbs!! * weight / 100
                                )
                            }

                            val calendar = Calendar.getInstance()
                            val year = calendar.get(Calendar.YEAR)
                            val month = calendar.get(Calendar.MONTH)
                            val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
                            val userMealsRef = FirebaseDatabase.getInstance()
                                .getReference("Users/$userId/Meals/$year/$month/$dayOfMonth")

                            modifiedProduct?.let {
                                modifiedProduct.ProductId?.let { it1 ->
                                    userMealsRef.child(it1).setValue(it)
                                } // use the modified product ID here
                                Toast.makeText(
                                    requireContext(),
                                    "Product copied to meals",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                        setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
                    }

                    builder.show()

                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), "Error: ${error.message}", Toast.LENGTH_LONG)
                        .show()
                }
            })
        }


        return view

    }


    private fun initView(view: View) {
        Log.d("MyFragment", "Initializing view...")
        tvProductName = view.findViewById(R.id.tvProductName)
        tvProductCalories = view.findViewById(R.id.tvProductCalories)
        tvProductFat = view.findViewById(R.id.tvProductFat)
        tvProductProtein = view.findViewById(R.id.tvProductProtein)
        tvProductCarbs = view.findViewById(R.id.tvProductCarbs)

        btnUpdate = view.findViewById(R.id.btnUpdate)
        btnDelete = view.findViewById(R.id.btnDelete)
    }

    private fun setValuesToViews() {
        tvProductName.text = requireArguments().getString("ProductName")
        tvProductCalories.text = requireArguments().getString("ProductCalories")
        tvProductFat.text = requireArguments().getString("ProductFat")
        tvProductProtein.text = requireArguments().getString("ProductProtein")
        tvProductCarbs.text = requireArguments().getString("ProductCarbs")
    }


    private fun deleteRecord(
        id: String
    ) {
        val dbRef = FirebaseDatabase.getInstance().getReference("FitBoost/Products").child(id)
        val mTask = dbRef.removeValue()
        mTask.addOnSuccessListener {
            Toast.makeText(requireContext(), "Product data deleted", Toast.LENGTH_LONG).show()

            val fragment = FetchingFragmentProduct()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame_layout, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }.addOnFailureListener { error ->
            Toast.makeText(requireContext(), "Deleting Err ${error.message}", Toast.LENGTH_LONG)
                .show()
        }
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
        val dbRef = FirebaseDatabase.getInstance().getReference("FitBoost/Products").child(id)
        val empInfo = ProductModel(id, name, calories, fat, protein, carbs)
        dbRef.setValue(empInfo)
    }

}