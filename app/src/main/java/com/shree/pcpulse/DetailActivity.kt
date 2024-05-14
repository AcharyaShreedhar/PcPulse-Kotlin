package com.shree.pcpulse


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.gson.Gson

class DetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        supportActionBar?.title = "Product Details"
        // Enable the back button in the toolbar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        // Retrieve the serialized product data from the intent
        val productJson = intent.getStringExtra("product")

        // Deserialize the product object from JSON using Gson
        val gson = Gson()
        val product = gson.fromJson(productJson, Product::class.java)

        val txtProductName: TextView = findViewById(R.id.tvName)
        val txtProductPrice: TextView = findViewById(R.id.tvPrice)
        val txtProductDescription: TextView = findViewById(R.id.tvDescription)
        val tvModel: TextView = findViewById(R.id.tvModel)
        val tvBrand: TextView = findViewById(R.id.tvBrand)
        val imgPhoto: ImageView = findViewById(R.id.imgItem)

        txtProductName.text = product.name;
        txtProductDescription.text = product.details
        txtProductPrice.text = product.price
        tvModel.text = "Model: " + product.model
        tvBrand.text = "Brand: " + product.brand
        val storage = FirebaseStorage.getInstance()
        val photoRef: StorageReference = storage.getReferenceFromUrl(product.imageUrl)
        Glide.with(imgPhoto).load(photoRef).into(imgPhoto)

        val btnAddToCart = findViewById<Button>(R.id.btnAddToCart)
        btnAddToCart.setOnClickListener {
            val user = FirebaseAuth.getInstance().currentUser
            // Check if a user is currently authenticated
            if (user != null) {
                val userId = user.uid

                val cartReference =
                    FirebaseDatabase.getInstance().reference.child("my_cart").child(userId)
                cartReference.child(product.id).get().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val dataSnapshot = task.result
                        if (dataSnapshot.exists()) {
                            // Product with the same productId already exists, update the quantity
                            val currentQuantity = dataSnapshot.getValue(Int::class.java)
                            val newQuantity = currentQuantity?.plus(1)
                            cartReference.child(product.id).setValue(newQuantity)
                        } else {
                            // Product doesn't exist in the cart, add it with a quantity of 1
                            cartReference.child(product.id).setValue(1)
                        }
                        Toast.makeText(
                            this, "Product is successfully added to the cart.", Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            this,
                            "Error adding the product to the cart. Please try again.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

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
}