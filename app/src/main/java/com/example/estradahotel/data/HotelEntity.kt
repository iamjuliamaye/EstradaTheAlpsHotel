package com.example.estradahotel.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "hotels")
data class HotelEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val location: String,
    val pricePerNight: Double,
    val imageName: String
)