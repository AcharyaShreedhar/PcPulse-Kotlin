package com.shree.pcpulse

import android.util.Log
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class ProductAdapter(
    options: FirebaseRecyclerOptions<Product>,
    private val onMenuItemClick: (Product, String, Int) -> Unit
) : FirebaseRecyclerAdapter<Product, ProductAdapter.MyViewHolder>(options) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return MyViewHolder(inflater.inflate(R.layout.product_row_layout, parent, false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int, model: Product) {
        val productID = getRef(position).key // Get the product ID (key) from the Firebase database

        if (productID != null) {
            holder.bind(model, productID)
        }
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtProductName: TextView = itemView.findViewById(R.id.tvName)
        private val txtProductPrice: TextView = itemView.findViewById(R.id.tvPrice)
        private val imgProductPhoto: ImageView = itemView.findViewById(R.id.imgItem)
        private val ivThreeDot: ImageView = itemView.findViewById(R.id.ivThreeDot)

        fun bind(product: Product, productID: String) {
            txtProductName.text = product.name
            txtProductPrice.text = product.price

            val storage = FirebaseStorage.getInstance()
            val photoRef: StorageReference = storage.getReferenceFromUrl(product.imageUrl)
            Glide.with(imgProductPhoto).load(photoRef).into(imgProductPhoto)

            ivThreeDot.setOnClickListener { view ->
                val popupMenu = PopupMenu(view.context, view)
                val inflater: MenuInflater = popupMenu.menuInflater
                inflater.inflate(R.menu.product_menu, popupMenu.menu)
                popupMenu.setOnMenuItemClickListener { menuItem ->
                    onMenuItemClick(product, productID, menuItem.itemId)
                    true
                }
                popupMenu.show()
            }
        }
    }
}