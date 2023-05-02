package com.example.fitboost2

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.fitboost2.Diet.DietBreakfastFragment
import com.example.fitboost2.Diet.DietBrunchFragment
import com.example.fitboost2.Diet.DietDinnerFragment
import com.example.fitboost2.Diet.DietLunchFragment
import com.example.fitboost2.Diet.DietSnackFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class DietFragment : Fragment() {

    private val userId = FirebaseAuth.getInstance().currentUser?.uid
    private lateinit var btnBreakfast: Button
    private lateinit var btnBrunch: Button
    private lateinit var btnLunch: Button
    private lateinit var bntAfternoon_Snack: Button
    private lateinit var btnDinner: Button


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_diet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bmrTextView = view.findViewById<TextView>(R.id.bmrTextView)
        btnBreakfast = view.findViewById(R.id.bntBreakfast)
        btnBrunch = view.findViewById(R.id.bntBrunch)
        btnLunch = view.findViewById(R.id.bntLunch)
        bntAfternoon_Snack = view.findViewById(R.id.bntAfternoon_Snack)
        btnDinner = view.findViewById(R.id.bntDinner)


        btnBreakfast.setOnClickListener {
            val fragment = DietBreakfastFragment()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame_layout, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
        btnBrunch.setOnClickListener {
            val fragment = DietBrunchFragment()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame_layout, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
        btnLunch.setOnClickListener {
            val fragment = DietLunchFragment()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame_layout, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
        bntAfternoon_Snack.setOnClickListener {
            val fragment = DietSnackFragment()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame_layout, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        btnDinner.setOnClickListener {
            val fragment = DietDinnerFragment()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame_layout, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        val dbRefBmr = FirebaseDatabase.getInstance().getReference("Users/$userId/bmr")

        dbRefBmr.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val bmr = snapshot.getValue(Double::class.java) ?: 0.0
                bmrTextView.text = "$bmr"
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("DietFragment", "Błąd odczytu BMR z bazy danych", error.toException())
            }
        })
    }
}
