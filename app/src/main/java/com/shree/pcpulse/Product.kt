package com.shree.pcpulse

import java.io.Serializable

data class Product(
    var id: String,
    val name: String,
    val details: String,
    val model: String,
    val brand: String,
    val price: String,
    val imageUrl: String
) : Serializable {
    // Empty constructor for Firebase (no-argument constructor)
    constructor() : this("", "", "", "", "", "", "")
}