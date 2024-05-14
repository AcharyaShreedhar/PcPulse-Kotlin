package com.shree.pcpulse

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CartActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        val customToolbar: Toolbar = findViewById(R.id.toolbar)

        // Setting custom toolbar as the support action bar
        setSupportActionBar(customToolbar)

        // Set the title and center it
        supportActionBar?.title = "My Cart"
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        // Add a back button to the toolbar
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        // Set up the back button click listener
        customToolbar.setNavigationOnClickListener {
            val intent = Intent(this@CartActivity, ProductActivity::class.java)

            // Start the ProductActivity
            startActivity(intent)
        }

        val profileIcon = customToolbar.findViewById<ImageView>(R.id.ivProfile)
        profileIcon.setOnClickListener {
            signOutAndNavigateToSignIn()
        }

        val currentUser = FirebaseAuth.getInstance().currentUser
        var totalPrice = 0.0
        if (currentUser != null) {
            val userId = currentUser.uid
            val cartReference =
                FirebaseDatabase.getInstance().reference.child("my_cart").child(userId)
            // Query the user's cart items
            cartReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val cartItems = mutableListOf<Product>()
                    val productQuantities = mutableMapOf<String, Int>()
                    for (cartSnapshot in dataSnapshot.children) {
                        // For each cart item, you can get the product ID (cartSnapshot.key) and quantity (cartSnapshot.value)
                        // Retrieve the product details from Firebase using the product ID
                        val productId = cartSnapshot.key
                        val quantity = cartSnapshot.value as Long

                        if (productId != null) {
                            // Add the quantity to the productQuantities map for this product ID
                            productQuantities[productId] = quantity.toInt()

                        }

                        productId?.let {
                            // Query the product details and populate them into the cartItems list
                            FirebaseDatabase.getInstance().reference.child("computers").child(it)
                                .addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(productDataSnapshot: DataSnapshot) {
                                        val product =
                                            productDataSnapshot.getValue(Product::class.java)

                                        if (product != null) {
                                            product.id = productId
                                            // Add the product to the cartItems list
                                            cartItems.add(product)

                                            // Check if cartItems are ready to be displayed in RecyclerView
                                            if (cartItems.size == dataSnapshot.childrenCount.toInt()) {
                                                for (item in cartItems) {
                                                    val quantity = productQuantities[item.id]
                                                    val priceString = item.price.replace(
                                                        Regex("[^\\d.]"), ""
                                                    ) // Remove non-numeric characters
                                                    val price = priceString.toDoubleOrNull()
                                                        ?: 0.0 // Convert to double (default to 0.0 if parsing fails)
                                                    totalPrice += quantity?.times(price) ?: 0.0
                                                }

                                                // After calculating the total price, update the TextView
                                                val tvTotalPrice: TextView =
                                                    findViewById(R.id.tvTotalPrice)
                                                tvTotalPrice.text =
                                                    "Total Price: $" + totalPrice.toString()
                                                // Set up RecyclerView with the cartItems
                                                val recyclerView: RecyclerView =
                                                    findViewById(R.id.rvCart)
                                                recyclerView.layoutManager =
                                                    LinearLayoutManager(this@CartActivity)
                                                val cartAdapter = CartAdapter(
                                                    this@CartActivity,
                                                    cartItems,
                                                    productQuantities,
                                                    cartReference,
                                                    ::updateTotalPrice
                                                )
                                                recyclerView.adapter = cartAdapter
                                            }

                                        }
                                    }

                                    override fun onCancelled(databaseError: DatabaseError) {
                                        // Handle the error
                                    }
                                })
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle the error when fetching the cart
                }
            })
        }

        val btnCheckout: Button = findViewById(R.id.btnCheckout)
        btnCheckout.setOnClickListener {
            // Implement your checkout logic here
            // You can navigate to the checkout page or perform other actions
            val intent = Intent(this@CartActivity, CheckoutActivity::class.java)

            // Start the ProductActivity
            startActivity(intent)
        }
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        // Set up item selection listener
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    val intent = Intent(this, ProductActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.my_cart -> {
                    // Already on the "My Cart" screen, do nothing
                    true
                }

                R.id.stores -> {
                    val intent = Intent(this, StoresActivity::class.java)
                    startActivity(intent)
                    true
                }

                else -> false
            }

        }
        // Set the "My Cart" menu item as selected
        bottomNavigationView.selectedItemId = R.id.my_cart

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

    private fun updateTotalPrice(cartItems: List<Product>, productQuantities: Map<String, Int>) {
        var totalPrice = 0.0

        for (item in cartItems) {
            val quantity = productQuantities[item.id]
            val priceString = item.price.replace(Regex("[^\\d.]"), "")
            val price = priceString.toDoubleOrNull() ?: 0.0
            totalPrice += (quantity ?: 0) * price
        }

        val tvTotalPrice: TextView = findViewById(R.id.tvTotalPrice)
        tvTotalPrice.text = "Total Price: $" + totalPrice.toString()
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
}
