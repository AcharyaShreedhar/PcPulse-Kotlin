package com.shree.pcpulse

import StoreAdapter
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class StoresActivity : AppCompatActivity() {
    private lateinit var adapter: StoreAdapter
    private val userLocation = Location(43.1448, -80.2644) // User's location
    private var currentSortingOption: SortingOption = SortingOption.DISTANCE
    private var storeList = mutableListOf<Store>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stores)
        val customToolbar: Toolbar = findViewById(R.id.toolbar)

        // Setting custom toolbar as the support action bar
        setSupportActionBar(customToolbar)
        supportActionBar?.title = "Recommended Stores"
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)
        // Enable the back button in the toolbar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        customToolbar.setNavigationOnClickListener {
            val intent = Intent(this@StoresActivity, ProductActivity::class.java)

            // Start the ProductActivity
            startActivity(intent)
        }

        // logout functionality
        val profileIcon = customToolbar.findViewById<ImageView>(R.id.ivProfile)
        profileIcon.setOnClickListener {
            signOutAndNavigateToSignIn()
        }

        val recyclerView: RecyclerView = findViewById(R.id.rvStoreList)
        adapter = StoreAdapter()

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // Initialize the Firebase database reference
        val query = FirebaseDatabase.getInstance().reference.child("stores")

        // Add a listener to fetch data and populate the RecyclerView
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                storeList.clear() // Clear the list before adding new data

                for (snapshot in dataSnapshot.children) {
                    val storeName = snapshot.child("name").value.toString()
                    val storeRating = snapshot.child("rating").value.toString().toDouble()
                    val storeAddress = snapshot.child("address").value.toString()
                    val storeLatitude = snapshot.child("lattitude").value.toString().toDouble()
                    val storeLongitude = snapshot.child("longitude").value.toString().toDouble()

                    val distance = HaversineDistanceCalculator.calculateDistance(
                        userLocation.latitude, userLocation.longitude, storeLatitude, storeLongitude
                    )

                    val store = Store(
                        storeName,
                        storeLatitude,
                        storeLongitude,
                        storeAddress,
                        distance,
                        storeRating
                    )
                    storeList.add(store)
                }

                sortAndSetStores()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle the error here
                Log.e("StoresActivity", "Database Error: ${databaseError.toException()}")
            }
        })
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
                    val intent = Intent(this, CartActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.stores -> {
                    // Already on the "My Cart" screen, do nothing
                    true
                }

                else -> false
            }
        }
        // Set the "My Cart" menu item as selected
        bottomNavigationView.selectedItemId = R.id.stores
    }


    private fun sortAndSetStores() {
        val sortedStoreList = when (currentSortingOption) {
            SortingOption.DISTANCE -> storeList.sortedBy { it.distance }
            SortingOption.RATING -> storeList.sortedByDescending { it.rating }
        }
        adapter.setStores(sortedStoreList)
    }

    fun showDropdownMenu(view: View) {
        val popupMenu = PopupMenu(this, findViewById(R.id.btnSort))
        popupMenu.menuInflater.inflate(R.menu.dropdown_menu, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_item1 -> {
                    currentSortingOption = SortingOption.DISTANCE
                    sortAndSetStores()
                    return@setOnMenuItemClickListener true
                }

                R.id.menu_item2 -> {
                    currentSortingOption = SortingOption.RATING
                    sortAndSetStores()
                    return@setOnMenuItemClickListener true
                }

                else -> return@setOnMenuItemClickListener false
            }
        }

        popupMenu.show()
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


    data class Location(val latitude: Double, val longitude: Double)
    private enum class SortingOption {
        DISTANCE, RATING
    }
}