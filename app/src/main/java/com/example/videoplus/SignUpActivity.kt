package com.example.videoplus

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import java.time.temporal.TemporalUnit

class SignUpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        val editTextEmail: EditText = findViewById(R.id.email_signUp)
        val editTextPassword: EditText = findViewById(R.id.password_signUp)
        val signUp: Button = findViewById(R.id.button_signUp)
        val alreadyAccount: TextView = findViewById(R.id.alreadyAccount_signUp)

        val progressDialog = ProgressDialog(this, R.style.AppCompatAlertDialogStyle)
        progressDialog.setMessage("Please wait...")
        progressDialog.setCanceledOnTouchOutside(false)

        signUp.setOnClickListener{
            signUpUser(progressDialog, editTextEmail, editTextPassword)
        }

        alreadyAccount.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }
    }

    private fun signUpUser(progressDialog: ProgressDialog, editTextEmail: EditText, editTextPassword: EditText) {

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
                firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    if(task.isSuccessful) {
                        saveUserInfo(progressDialog, firebaseAuth, email, password)
                    }
                    else {
                        Toast.makeText(this, task.exception!!.message, Toast.LENGTH_SHORT).show()
                        progressDialog.dismiss()
                    }
                }
            }
        }
    }

    private fun saveUserInfo(progressDialog: ProgressDialog, firebaseAuth: FirebaseAuth, email: String, password: String) {

        val userId = firebaseAuth.currentUser!!.uid
        val reference = FirebaseFirestore.getInstance().collection("Users")

        val user = HashMap<String, Any>()
        user["userId"] = userId
        user["email"] = email
        user["password"] = password

        reference.document(userId).set(user).addOnCompleteListener { task ->
            if(task.isSuccessful) {
                startActivity(Intent(this, MainActivity::class.java))
                overridePendingTransition(0, 0)
            }
            else {
                progressDialog.dismiss()
                Toast.makeText(this, task.exception!!.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if(FirebaseAuth.getInstance().currentUser != null) {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }
}