package com.example.fitboost2

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.fitboost2.Login_SignUp.LoginActivity
import com.example.fitboost2.Profile.*
import com.example.fitboost2.databinding.FragmentProfileBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

class ProfileFragment : Fragment() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var binding: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mAuth = FirebaseAuth.getInstance()
        mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), getGoogleSignInOptions())


        binding.bntpersolandataProfile.setOnClickListener {
            val fragment = PersonalDataFragment()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame_layout, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
        binding.bntsummaryProfile.setOnClickListener {
            val fragment = SummaryFragment()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame_layout, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
        binding.bntbodymeasurementsProfile.setOnClickListener {
            val fragment = BodyMeasurementsFragment()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame_layout, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        binding.bntcalculatorProfile.setOnClickListener {
            val fragment = CalculatorFragment()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame_layout, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
        binding.bntsettingsProfile.setOnClickListener {
            val fragment = SettingsFragment()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame_layout, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        // Przycisk "Log out"
        binding.bntlogoutProfile.setOnClickListener {
            logout()
        }

        // Wyświetl email użytkownika, jeśli jest zalogowany
        mAuth.currentUser?.let { user ->
            binding.tvEmail.text = user.email
        }
    }


    private fun getGoogleSignInOptions(): GoogleSignInOptions {
        return GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
    }

    private fun logout() {
        mAuth.signOut()
        mGoogleSignInClient.signOut().addOnCompleteListener(requireActivity()) {
            Toast.makeText(requireContext(), "Log out", Toast.LENGTH_SHORT).show()
            // Przejdź do ekranu logowania

            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
    }
}


