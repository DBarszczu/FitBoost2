package com.example.fitboost2

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.example.fitboost2.Diet.DietBreakfastFragment
import com.example.fitboost2.Diet.DietBrunchFragment
import com.example.fitboost2.Diet.DietDinnerFragment
import com.example.fitboost2.Diet.DietLunchFragment
import com.example.fitboost2.Training.Test
import com.example.fitboost2.Training.TrainingBackFragment
import com.example.fitboost2.Training.TrainingBellyFragment
import com.example.fitboost2.Training.TrainingBicepsFragment
import com.example.fitboost2.Training.TrainingButtocksFragment
import com.example.fitboost2.Training.TrainingChestFragment
import com.example.fitboost2.Training.TrainingLegsFragment
import com.example.fitboost2.Training.TrainingShouldersFragment
import com.example.fitboost2.Training.TrainingTricepsFragment
import com.example.fitboost2.databinding.FragmentTraininigBicepsBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class TrainingFragment : Fragment() {

    private lateinit var bntChestTraining: Button
    private lateinit var bntShouldersTraining: Button
    private lateinit var bntBicepsTraining: Button
    private lateinit var bntTricpesTraininig: Button
    private lateinit var bntBackTraining: Button
    private lateinit var bntButtocksTraining: Button
    private lateinit var bntLegsTraining: Button
    private lateinit var bntBellyTraining: Button
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_training, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bntChestTraining = view.findViewById(R.id.bntChestTraining)
        bntShouldersTraining = view.findViewById(R.id.bntShouldersTraining)
        bntBicepsTraining = view.findViewById(R.id.bntBicepsTraining)
        bntTricpesTraininig = view.findViewById(R.id.bntTricpesTraininig)
        bntBackTraining = view.findViewById(R.id.bntBackTraining)
        bntButtocksTraining = view.findViewById(R.id.bntButtocksTraining)
        bntLegsTraining = view.findViewById(R.id.bntLegsTraining)
        bntBellyTraining = view.findViewById(R.id.bntBellyTraining)

        bntChestTraining.setOnClickListener {
            val fragment = TrainingChestFragment()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame_layout, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
        bntShouldersTraining.setOnClickListener {
            val fragment = TrainingShouldersFragment()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame_layout, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        bntBicepsTraining.setOnClickListener {
            val fragment = TrainingBicepsFragment()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame_layout, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
        bntTricpesTraininig.setOnClickListener {
            val fragment = TrainingTricepsFragment()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame_layout, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
        bntBackTraining.setOnClickListener {
            val fragment = TrainingBackFragment()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame_layout, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
        bntButtocksTraining.setOnClickListener {
            val fragment = TrainingButtocksFragment()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame_layout, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
        bntLegsTraining.setOnClickListener {
            val fragment = TrainingLegsFragment()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame_layout, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
        bntBellyTraining.setOnClickListener {
            val fragment = TrainingBellyFragment()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame_layout, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

    }

}