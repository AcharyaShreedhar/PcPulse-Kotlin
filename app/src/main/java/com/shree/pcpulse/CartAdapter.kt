package com.shree.pcpulse

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


class CartAdapter(
    private val context: Context,
    private val cartItems: MutableList<Product>,
    private var productQuantities: Map<String, Int>,
    private val cartReference: DatabaseReference,
    private val updateTotalPrice: (List<Product>, Map<String, Int>) -> Unit

) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.cart_product, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val product = cartItems[position]

        // Load product details into the view
        holder.itemName.text = product.name
        holder.itemPrice.text = product.price
        val quantity = productQuantities[product.id]
        holder.itemQuantity.text = "Quantity: $quantity"

        val storage = FirebaseStorage.getInstance()
        val photoRef: StorageReference = storage.getReferenceFromUrl(product.imageUrl)
        Glide.with(holder.itemImage).load(photoRef).into(holder.itemImage)

        holder.ivRemove.setOnClickListener {
            // Find the index of the product to be removed
            val indexToRemove = cartItems.indexOf(product)

            // Check if the product is in the cartItems list
            if (indexToRemove >= 0) {
                // Remove the product at the specified index
                cartItems.removeAt(indexToRemove)

                // Update productQuantities by removing the product ID
                productQuantities -= product.id

                // Notify the adapter that data has changed
                notifyDataSetChanged()

                // Remove the item from the Firebase database
                removeItemFromDatabase(product.id)
            }
        }
    }

    private fun removeItemFromDatabase(productId: String) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val userId = currentUser.uid

            // Build the reference to the specific cart item for the current user
            val userCartReference = cartReference
//            Log.d("Firebase", "User Cart Reference: $userCartReference")
//            Log.d("Firebase", "Product ID to Remove: $productId")

            // Remove the item from the database by setting its value to null
            userCartReference.child(productId).setValue(null).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    updateTotalPrice(cartItems, productQuantities)
                    Log.d("Firebase", "Item removed successfully")
                } else {
                    Log.e("Firebase", "Failed to remove item: ${task.exception}")
                }
            }

        }
    }


    override fun getItemCount(): Int {
        return cartItems.size
    }

    inner class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemName: TextView = itemView.findViewById(R.id.tvItemName)
        val itemPrice: TextView = itemView.findViewById(R.id.tvItemPrice)
        val itemQuantity: TextView = itemView.findViewById(R.id.tvItemQuantity)
        val itemImage: ImageView = itemView.findViewById(R.id.imgItem)
        val ivRemove: ImageView = itemView.findViewById(R.id.ivRemove)
    }
}
