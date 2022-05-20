package com.abiy.projectfirebase.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.abiy.projectfirebase.LoginActivity
import com.abiy.projectfirebase.R
import com.abiy.projectfirebase.databinding.FragmentUserBinding
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException

class UserFragment : Fragment() {

    private var _binding : FragmentUserBinding? = null
    lateinit var auth : FirebaseAuth

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUserBinding.inflate(inflater, container, false)
        return  binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser

        if (user != null){
            binding.edtName.setText(user.displayName)
            binding.edtEmail.setText(user.email)

            if (user.isEmailVerified){
                binding.iconVerify.visibility = View.VISIBLE
                binding.iconNotVerify.visibility = View.GONE
            } else {
                binding.iconVerify.visibility = View.GONE
                binding.iconNotVerify.visibility = View.VISIBLE
            }
        }

        binding.btnLogout.setOnClickListener {
            btnLogout()
        }

        binding.btnVerify.setOnClickListener {
            emailVerification()
        }

        binding.btnChangePass.setOnClickListener {
            changePass()
        }
    }

    private fun changePass() {
        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser

        binding.cvCurrentPass.visibility = View.VISIBLE

        binding.btnCancel.setOnClickListener {
            binding.cvCurrentPass.visibility = View.GONE
        }

        binding.btnConfirm.setOnClickListener btnConfirm@{
            val pass = binding.edtCurrentPassword.text.toString()

            if (pass.isEmpty()){
                binding.edtCurrentPassword.error = "You have to Fill the Password!"
                binding.edtCurrentPassword.requestFocus()
                return@btnConfirm
            }

            user.let {
                val userCredential = EmailAuthProvider.getCredential(it?.email!!,pass)
                it.reauthenticate(userCredential).addOnCompleteListener { task ->
                   when {
                       task.isSuccessful -> {
                           binding.cvCurrentPass.visibility = View.GONE
                           binding.cvUpdatePass.visibility =View.VISIBLE
                       }
                       task.exception is FirebaseAuthInvalidCredentialsException -> {
                           binding.edtCurrentPassword.error = "You input the wrong Password!"
                           binding.edtCurrentPassword.requestFocus()
                       }
                       else -> {
                           Toast.makeText(activity,"${task.exception?.message}",Toast.LENGTH_SHORT).show()
                       }
                   }
                }
            }

            binding.btnNewCancel.setOnClickListener {
                binding.cvCurrentPass.visibility = View.GONE
                binding.cvUpdatePass.visibility =View.GONE
            }

            binding.btnNewChange.setOnClickListener newChangePassword@ {



                val newPass = binding.edtNewPass.text.toString()
                val passConfirm = binding.edtConfirmPass.text.toString()

                if (newPass.isEmpty()){
                    binding.edtCurrentPassword.error = "You have to Fill the Password"
                    binding.edtCurrentPassword.requestFocus()
                    return@newChangePassword
                }

                if (passConfirm.isEmpty()){
                    binding.edtCurrentPassword.error = "Repeat new Password"
                    binding.edtCurrentPassword.requestFocus()
                    return@newChangePassword
                }

                if (newPass.length < 5) {
                    binding.edtCurrentPassword.error = "Password Minimum is 5 Character"
                    binding.edtCurrentPassword.requestFocus()
                    return@newChangePassword
                }

                if (passConfirm.length < 5) {
                    binding.edtCurrentPassword.error = "Password is not match"
                    binding.edtCurrentPassword.requestFocus()
                    return@newChangePassword
                }

                if (newPass != passConfirm) {
                    binding.edtCurrentPassword.error = "Password is not match"
                    binding.edtCurrentPassword.requestFocus()
                    return@newChangePassword
                }

                user?.let {
                    user.updatePassword(newPass).addOnCompleteListener {
                        if (it.isSuccessful) {
                            Toast.makeText(activity,"Password successfully change!",Toast.LENGTH_SHORT).show()
                            successLogout()
                        } else {
                            Toast.makeText(activity,"${it.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    private fun successLogout() {
        auth = FirebaseAuth.getInstance()
        auth.signOut()

        val intent = Intent(context, LoginActivity::class.java)
        startActivity(intent)
        activity?.finish()

        Toast.makeText(activity,"Please Login Again", Toast.LENGTH_SHORT).show()
    }

    private fun emailVerification() {
        val user = auth.currentUser
        user?.sendEmailVerification()?.addOnCompleteListener {
            if(it.isSuccessful){
                Toast.makeText(activity, "Email Verification has been send", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(activity, "${it.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun btnLogout() {
        auth = FirebaseAuth.getInstance()
        auth.signOut()
        val intent = Intent(context,LoginActivity::class.java)
        activity?.finish()
    }
}
