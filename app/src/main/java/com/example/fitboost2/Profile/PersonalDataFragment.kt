package com.example.fitboost2.Profile

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.fitboost2.databinding.DialogChangeEmailBinding
import com.example.fitboost2.databinding.FragmentPersonalDataBinding
import com.example.fitboost2.databinding.DialogChangeNameBinding
import com.example.fitboost2.databinding.DialogChangePasswordBinding
import com.example.fitboost2.databinding.DialogChangeSurnameBinding
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class PersonalDataFragment : Fragment() {

    private var _binding: FragmentPersonalDataBinding? = null
    private val binding get() = _binding!!
    private lateinit var mAuth: FirebaseAuth
    private lateinit var nameDialog: Dialog
    private lateinit var nameEditText: EditText
    private lateinit var namesaveButton: Button

    private lateinit var surnameDialog: Dialog
    private lateinit var surnameEditText: EditText
    private lateinit var surnamesaveButton: Button

    private lateinit var emailDialog: Dialog
    private lateinit var emailEditText: EditText
    private lateinit var emailsaveButton: Button

    private lateinit var passwordDialog: Dialog
    private lateinit var passwordEditText: EditText
    private lateinit var passwordsaveButton: Button


    private lateinit var userId: String
    private lateinit var databaseReference: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPersonalDataBinding.inflate(inflater, container, false)
        val view = binding.root


        binding.ChangeEmail.setOnClickListener {
            emailDialog = Dialog(requireContext())
            val emailDialogBinding = DialogChangeEmailBinding.inflate(layoutInflater)
            emailDialog.setContentView(emailDialogBinding.root)
            emailEditText = emailDialogBinding.emailEditText
            emailsaveButton = emailDialogBinding.emailsaveButton


            emailEditText = emailDialogBinding.emailEditText
            emailsaveButton = emailDialogBinding.emailsaveButton

            emailsaveButton.setOnClickListener {
                val email = emailEditText.text.toString().trim()
                val user = mAuth.currentUser

                if (user != null) {
                    user.updateEmail(email).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(
                                requireContext(),
                                "Email updated successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                            emailDialog.dismiss()
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Failed to update email",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    Toast.makeText(requireContext(), "No user found", Toast.LENGTH_SHORT).show()
                }
            }

            emailDialog.show()
        }

        binding.ChangePassword.setOnClickListener {
            passwordDialog = Dialog(requireContext())
            val passwordDialogBinding = DialogChangePasswordBinding.inflate(layoutInflater)
            passwordDialog.setContentView(passwordDialogBinding.root)
            val oldPasswordEditText = passwordDialogBinding.OldPasswordEditText
            passwordEditText = passwordDialogBinding.PasswordEditText1
            val confirmPasswordEditText = passwordDialogBinding.PasswordEditText2
            passwordsaveButton = passwordDialogBinding.passwordsaveButton

            passwordsaveButton.setOnClickListener {
                val oldPassword = oldPasswordEditText.text.toString().trim()
                val newPassword = passwordEditText.text.toString().trim()
                val confirmPassword = confirmPasswordEditText.text.toString().trim()

                if (newPassword == confirmPassword) { // sprawdzenie czy hasła są takie same
                    val credential = EmailAuthProvider.getCredential(mAuth.currentUser?.email.toString(), oldPassword)
                    mAuth.currentUser?.reauthenticate(credential)?.addOnCompleteListener { authTask ->
                        if (authTask.isSuccessful) {
                            mAuth.currentUser?.updatePassword(newPassword)?.addOnCompleteListener { updateTask ->
                                if (updateTask.isSuccessful) {
                                    Toast.makeText(
                                        requireContext(),
                                        "Password updated successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    passwordDialog.dismiss()
                                } else {
                                    Toast.makeText(
                                        requireContext(),
                                        "Failed to update password",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Old password is incorrect",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        "New password and confirm password do not match",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            passwordDialog.show()
        }







        mAuth = FirebaseAuth.getInstance()
        mAuth.currentUser?.let { user ->
            binding.txtEmailPersonalDataUser.text = user.email
        }

        userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        databaseReference = FirebaseDatabase.getInstance().reference.child("Users").child(userId)
            .child("PersonalData")

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val name = dataSnapshot.child("name").value?.toString()
                    if (name != null) {
                        binding.txtNamePersonalDataUser.text = name
                    }

                    val surname = dataSnapshot.child("surname").value?.toString()
                    if (surname != null) {
                        binding.txtSurnamePersonalDataUser.text = surname
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // handle error
            }
        })

        binding.ChangeName.setOnClickListener {
            nameDialog = Dialog(requireContext())
            val nameDialogBinding = DialogChangeNameBinding.inflate(layoutInflater)
            nameDialog.setContentView(nameDialogBinding.root)

            nameEditText = nameDialogBinding.nameEditText
            namesaveButton = nameDialogBinding.namesaveButton

            namesaveButton.setOnClickListener {
                val name = nameEditText.text.toString().trim()

                if (name.isNotEmpty()) {
                    val data = hashMapOf<String, String>(
                        "name" to name,
                        "surname" to binding.txtSurnamePersonalDataUser.text.toString() // add current surname
                    )

                    databaseReference.setValue(data).addOnSuccessListener {
                        if (isAdded()) { // check if the fragment is attached to the activity
                            nameDialog.dismiss()
                        }
                    }
                }
            }

            nameDialog.show()
        }

        binding.ChangeSurname.setOnClickListener {
            surnameDialog = Dialog(requireContext())
            val surnameDialogBinding = DialogChangeSurnameBinding.inflate(layoutInflater)
            surnameDialog.setContentView(surnameDialogBinding.root)

            surnameEditText = surnameDialogBinding.surnameEditText
            surnamesaveButton = surnameDialogBinding.surnamesaveButton

            surnamesaveButton.setOnClickListener {
                val surname = surnameEditText.text.toString().trim()

                if (surname.isNotEmpty()) {
                    val data = hashMapOf<String, String>(
                        "name" to binding.txtNamePersonalDataUser.text.toString(), // add current name
                        "surname" to surname
                    )

                    databaseReference.setValue(data).addOnSuccessListener {
                        if (isAdded()) { // check if the fragment is attached to the activity
                            surnameDialog.dismiss()
                        }
                    }
                }
            }

            surnameDialog.show()
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
