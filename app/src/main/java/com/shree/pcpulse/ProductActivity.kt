package com.shree.pcpulse

import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.widget.TooltipCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson


class ProductActivity : AppCompatActivity() {
    private var adapter: ProductAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product)
        val customToolbar: Toolbar = findViewById(R.id.toolbar)

        // Set your custom toolbar as the support action bar
        setSupportActionBar(customToolbar)

        // Set the title and center it
        supportActionBar?.title = "Products"
        supportActionBar?.setDisplayShowTitleEnabled(true)

        // logout functionality
        val profileIcon = customToolbar.findViewById<ImageView>(R.id.ivProfile)

        profileIcon.setOnClickListener {
            signOutAndNavigateToSignIn()
        }
        val query = FirebaseDatabase.getInstance().reference.child("computers")

        val options =
            FirebaseRecyclerOptions.Builder<Product>().setQuery(query, Product::class.java).build()
        adapter = ProductAdapter(options) { product, productID, menuItemId ->
            when (menuItemId) {
                R.id.menu_view_details -> {
                    // Handle view details
                    val intent = Intent(this@ProductActivity, DetailActivity::class.java)
                    // Pass product data to DetailActivity
                    val gson = Gson()
                    product.id = productID;
                    val productJson = gson.toJson(product)

                    // Pass the serialized product data to DetailActivity
                    intent.putExtra("product", productJson)
                    // Start the ProductActivity
                    startActivity(intent)
                }

                R.id.menu_add_to_wishlist -> {
                    // Handle add to wishlist
                   //Future Implementation
                }

                R.id.menu_add_to_cart -> {
                    val user = FirebaseAuth.getInstance().currentUser

                    // Check if a user is currently authenticated
                    if (user != null) {
                        val userId = user.uid
                        val cartReference =
                            FirebaseDatabase.getInstance().reference.child("my_cart").child(userId)

                        cartReference.child(productID).get().addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val dataSnapshot = task.result
                                if (dataSnapshot.exists()) {
                                    // Product with the same productId already exists, update the quantity
                                    val currentQuantity = dataSnapshot.getValue(Int::class.java)
                                    val newQuantity = currentQuantity?.plus(1)
                                    cartReference.child(productID).setValue(newQuantity)
                                } else {
                                    // Product doesn't exist in the cart, add it with a quantity of 1
                                    cartReference.child(productID).setValue(1)
                                }
                                // Handle add to cart
                                val intent = Intent(this@ProductActivity, CartActivity::class.java)

                                // Start the ProductActivity
                                startActivity(intent)
                            }
                        }
                    }

                }
            }
        }
        val rView: RecyclerView = findViewById(R.id.rvList)
        rView.layoutManager = LinearLayoutManager(this)
        rView.adapter = adapter
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)

        // Set up item selection listener
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    // Already on the "My Cart" screen, do nothing
                    true
                }

                R.id.my_cart -> {
                    val intent = Intent(this, CartActivity::class.java)
                    startActivity(intent)
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

    override fun onStart() {
        super.onStart()
        adapter?.startListening()
    }
}
