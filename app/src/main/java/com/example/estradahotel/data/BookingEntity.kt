package com.example.estradahotel.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookings")
data class BookingEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val firstName: String,
    val lastName: String,
    val hotelId: Int,
    val hotelName: String,
    val roomType: String,
    val checkInMillis: Long,
    val checkOutMillis: Long,
    val adults: Int,
    val children: Int,
    val rooms: Int,
    val bookingType: String,
    val paymentMethod: String,
    val totalPrice: Int
)