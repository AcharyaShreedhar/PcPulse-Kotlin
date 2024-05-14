package com.shree.pcpulse

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignInActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        val etEmail: TextInputEditText = findViewById(R.id.etEmail)
        val etPassword: TextInputEditText = findViewById(R.id.etPassword)
        val progressBar: ProgressBar = findViewById(R.id.progressBar)
        val loginButton: Button = findViewById(R.id.btnLogin)
        val registerLink: TextView = findViewById(R.id.tvRegisterLink)
        // Initialize Firebase Auth
        var auth: FirebaseAuth = Firebase.auth

        val currentUser = auth.currentUser
        Log.d("currectUser",currentUser.toString());
        if (currentUser != null) {
            val intent = Intent(this@SignInActivity, ProductActivity::class.java)

            // Start the ProductActivity
            startActivity(intent)

            // Optionally finish the SignInActivity
            finish()
        }

        // Set an OnClickListener for the login button
        loginButton.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Email and Password are Required Field", Toast.LENGTH_SHORT)
                    .show()
            } else {

                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        progressBar.visibility = View.GONE
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(
                                baseContext,
                                "Login Successful.",
                                Toast.LENGTH_SHORT,
                            ).show()
                            // Create an Intent to start the ProductActivity
                            val intent = Intent(this@SignInActivity, ProductActivity::class.java)

                            // Start the ProductActivity
                            startActivity(intent)

                            // Optionally finish the SignInActivity
                            finish()

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(
                                baseContext,
                                "Login Failed. Please Try Again",
                                Toast.LENGTH_SHORT,
                            ).show()

                        }
                    }

            }
        }
        // Set an OnClickListener for the login button
        registerLink.setOnClickListener {
            // Create an Intent to start the RegisterActivity
            val intent = Intent(this@SignInActivity, Register::class.java)

            // Start the ProductActivity
            startActivity(intent)

            // Optionally finish the SignInActivity
            finish()
        }
    }
}