package com.shree.pcpulse

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class Register : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val etEmail: TextInputEditText = findViewById(R.id.etEmail)
        val etPassword: TextInputEditText = findViewById(R.id.etPassword)
        val btnRegister: Button = findViewById(R.id.btnRegister)
        val loginLink: TextView = findViewById(R.id.tvLoginLink)
        val progressBar: ProgressBar = findViewById(R.id.progressBar)

        // Initialize Firebase Auth
        var auth: FirebaseAuth = Firebase.auth

        // Set an OnClickListener for the login button
        btnRegister.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Please enter both email and password", Toast.LENGTH_SHORT)
                    .show()
            } else {

                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { registrationTask ->

                        progressBar.visibility = View.GONE
                        if (registrationTask.isSuccessful) {
                            // Sign out the current user session after successful registration
                            Firebase.auth.signOut()
                            // Sign in success, update UI with the signed-in user's information
                            val user = auth.currentUser
                            Toast.makeText(
                                baseContext,
                                "Registration Successfull",
                                Toast.LENGTH_SHORT,
                            ).show()
                            // Create an Intent to start the SignInActivity
                            val intent = Intent(this, SignInActivity::class.java)

                            // Start the LoginActivity
                            startActivity(intent)

                            // Optionally finish the Register Activity
                            finish()
                        } else {
                            // Handle registration failure
                            if (registrationTask.exception is FirebaseAuthUserCollisionException) {
                                // Email is already in use
                                Toast.makeText(
                                    baseContext,
                                    "The email address is already in use by another account.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                // Other registration failure, display a generic message
                                Toast.makeText(
                                    baseContext,
                                    "Registration failed. Please try Again",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            progressBar.visibility = View.GONE
                        }
                    }
            }

        }
        // Set an OnClickListener for the login link
        loginLink.setOnClickListener {
            // Create an Intent to start the SignIn Activity
            val intent = Intent(this, SignInActivity::class.java)

            // Start the SignIn Activity
            startActivity(intent)

            // Optionally finish the SignInActivity
            finish()
        }
    }
}