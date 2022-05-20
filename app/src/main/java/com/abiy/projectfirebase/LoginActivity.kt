package com.abiy.projectfirebase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.abiy.projectfirebase.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var binding : ActivityLoginBinding
    lateinit var auth : FirebaseAuth
    lateinit var edt_email_login : EditText
    lateinit var edt_password_login : EditText
    lateinit var btn_login : Button
    lateinit var ref: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityLoginBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        edt_email_login = findViewById(R.id.edt_email_login)
        edt_password_login = findViewById(R.id.edt_password_login)
        btn_login = findViewById(R.id.btn_login)

        btn_login.setOnClickListener(this)



        auth = FirebaseAuth.getInstance()

        binding.tvToRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        binding.btnLogin.setOnClickListener {
                val email = binding.edtEmailLogin.text.toString()
                val password = binding.edtPasswordLogin.text.toString()

                if (email.isEmpty()){
                    binding.edtEmailLogin.error = "Email Must be Filled!"
                    binding.edtEmailLogin.requestFocus()
                    return@setOnClickListener
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    binding.edtEmailLogin.error = "Try Another Email!"
                    binding.edtEmailLogin.requestFocus()
                    return@setOnClickListener
                }

                if (password.isEmpty()) {
                    binding.edtPasswordLogin.error = "Password Must be Filled!"
                    binding.edtPasswordLogin.requestFocus()
                    return@setOnClickListener
                }


                LoginFirebase(email,password)
        }
    }

    private fun LoginFirebase(email: String, password: String) {
        auth.signInWithEmailAndPassword(email,password)

            .addOnCompleteListener(this){
                if (it.isSuccessful){
                    Toast.makeText(this,"Welcome! $email",Toast.LENGTH_SHORT).show()
                    val intent = Intent(this,MainActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this,"${it.exception?.message}",Toast.LENGTH_SHORT).show()
                }
            }
    }

    override fun onClick(v: View?) {
        saveData()
    }

    private fun saveData() {
        val email : String = edt_email_login.text.toString().trim()
        val password : String = edt_password_login.text.toString().trim()

        if (email.isEmpty()){
            edt_email_login.error = "Try Again"
            return
        }

        if (password.isEmpty()){
            edt_password_login.error = "Try Again"
            return
        }

        val ref = FirebaseDatabase.getInstance().getReference("Account")
        val account = ref.push().key

        val acc = Account(account,email, password)

        if (account != null) {
            ref.child(account).setValue(acc).addOnCompleteListener{
                Toast.makeText(applicationContext, "Login Successfully",Toast.LENGTH_SHORT).show()
            }
        }
    }
}