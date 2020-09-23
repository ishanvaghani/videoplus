package com.example.videoplus

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class SignInActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        val editTextEmail: EditText = findViewById(R.id.email_signIn)
        val editTextPassword: EditText = findViewById(R.id.password_signIn)
        val signIn: Button = findViewById(R.id.button_signIn)
        val createAccount: TextView = findViewById(R.id.createAccount_signIn)

        val progressDialog = ProgressDialog(this, R.style.AppCompatAlertDialogStyle)
        progressDialog.setMessage("Please wait...")
        progressDialog.setCanceledOnTouchOutside(false)

        signIn.setOnClickListener{
            signInUser(progressDialog, editTextEmail, editTextPassword)
        }

        createAccount.setOnClickListener {
            finish()
        }
    }

    private fun signInUser(progressDialog: ProgressDialog, editTextEmail: EditText, editTextPassword: EditText) {
        val email = editTextEmail.text.toString().trim()
        val password = editTextPassword.text.toString().trim()
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\\\.+[a-z]+"

        when {
            TextUtils.isEmpty(email) -> editTextEmail.error = "Field Required"
            email.matches(emailPattern.toRegex()) -> editTextEmail.error = "Invalid email"
            TextUtils.isEmpty(password) -> editTextPassword.error = "Field Required"
            password.length < 6 -> editTextPassword.error = "Password is short"

            else -> {
                progressDialog.show()

                val firebaseAuth = FirebaseAuth.getInstance()
                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    if(task.isSuccessful) {
                        startActivity(Intent(this, MainActivity::class.java))
                        overridePendingTransition(0, 0)
                    }
                    else {
                        Toast.makeText(this, task.exception!!.message, Toast.LENGTH_SHORT).show()
                        progressDialog.dismiss()
                    }
                }
            }
        }
    }
}