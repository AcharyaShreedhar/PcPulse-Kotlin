package com.shree.pcpulse

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

class CheckoutActivity : AppCompatActivity() {
    private val validator = Validator()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)
        val customToolbar: Toolbar = findViewById(R.id.toolbar)

        // Setting  custom toolbar as the support action bar
        setSupportActionBar(customToolbar)

        // Set the title and center it
        supportActionBar?.title = "Check Out"
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        // Add a back button to the toolbar
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val profileIcon = customToolbar.findViewById<ImageView>(R.id.ivProfile)
        profileIcon.setOnClickListener {
            signOutAndNavigateToSignIn()
        }

        // Set up the back button click listener
        customToolbar.setNavigationOnClickListener {
            val intent = Intent(this@CheckoutActivity, CartActivity::class.java)

            // Start the ProductActivity
            startActivity(intent)
        }
        val btnPlaceOrder = findViewById<Button>(R.id.btnPlaceOrder)
        btnPlaceOrder.setOnClickListener {
            // Handle button click

            if (validateFields()) {
                // All fields are valid, proceed to order confirmation
                //  order confirmation logic
                Toast.makeText(
                    this, "Checkout Successful. Your Order is confirmed!", Toast.LENGTH_SHORT
                ).show()
                val intent = Intent(this, ProductActivity::class.java)
                startActivity(intent)
            }

        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                // Handle the back button click
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun validateFields(): Boolean {
        // Retrieve the values from EditText fields
        val name = findViewById<TextInputEditText>(R.id.etName).text.toString()
        val email = findViewById<TextInputEditText>(R.id.etEmail).text.toString()
        val phoneNumber = findViewById<TextInputEditText>(R.id.etPhoneNumber).text.toString()
        val address = findViewById<TextInputEditText>(R.id.etAddress).text.toString()
        val city = findViewById<TextInputEditText>(R.id.etCity).text.toString()
        val state = findViewById<TextInputEditText>(R.id.etState).text.toString()
        val zipCode = findViewById<TextInputEditText>(R.id.etZipCode).text.toString()
        val cardNumber = findViewById<TextInputEditText>(R.id.etCreditCardNumber).text.toString()
        val expiryDate = findViewById<TextInputEditText>(R.id.etExpiryDate).text.toString()

        val securityCode = findViewById<TextInputEditText>(R.id.etSecurityCode).text.toString()

        // Regular expressions for validation (you can customize these)
        val emailRegex = "^[A-Za-z0-9+_.-]+@(.+)\$"
        val phoneNumberRegex = "^[0-9]{10}\$"
        val zipCodeRegex = "^[0-9]{5}\$"
        val cardNumberRegex = "^[0-9]{16}\$"

        val securityCodeRegex = "^[0-9]{3}\$"

        // Validate each field
        if (validator.isFieldEmpty(name)) {
            showError("Name is required.")
            return false
        }
        if (validator.isFieldEmpty(email)) {
            showError("Email is required.")
            return false
        }
        if (!validator.isValidRegex(email, emailRegex)) {
            showError("Invalid email address.")
            return false
        }
        if (validator.isFieldEmpty(phoneNumber)) {
            showError("Phone Number is required.")
            return false
        }
        if (!validator.isValidRegex(phoneNumber, phoneNumberRegex)) {
            showError("Invalid phone number.")
            return false
        }
        if (validator.isFieldEmpty(address)) {
            showError("Address is required.")
            return false
        }
        if (validator.isFieldEmpty(city)) {
            showError("City is required.")
            return false
        }
        if (validator.isFieldEmpty(state)) {
            showError("State is required.")
            return false
        }
        if (validator.isFieldEmpty(zipCode)) {
            showError("Zip Code is required.")
            return false
        }
        if (!validator.isValidRegex(zipCode, zipCodeRegex)) {
            showError("Invalid ZIP code.")
            return false
        }
        if (validator.isFieldEmpty(cardNumber)) {
            showError("Card Number is required.")
            return false
        }
        if (!validator.isValidRegex(cardNumber, cardNumberRegex)) {
            showError("Invalid card number.")
            return false
        }
        if (validator.isFieldEmpty(expiryDate)) {
            showError("ExpiryDate is required.")
            return false
        }

        if (validator.isFieldEmpty(securityCode)) {
            showError("Security Code is required.")
            return false
        }
        if (!validator.isValidRegex(securityCode, securityCodeRegex)) {
            showError("Invalid security code (3 digits).")
            return false
        }

        // All fields are valid
        return true
    }

    private fun showError(errorMessage: String) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
    }

    private fun signOutAndNavigateToSignIn() {
        // Sign out the user
        FirebaseAuth.getInstance().signOut()

        // Navigate to the SignInActivity
        val intent = Intent(this, SignInActivity::class.java)
        startActivity(intent)

        // Finish the current activity to prevent going back to it after signing out
        finish()
    }
}
