package com.example.estradahotel

import android.content.Context
import android.util.Log
import org.json.JSONObject

// One rating line
data class Rating(val category: String, val score: Double)

// One guest review
data class Review(val username: String, val country: String, val text: String)

// One room offered by a hotel.
data class HotelRoom(
    val id: Int,
    val type: String,
    val bedType: String,
    val maxGuests: Int,
    val features: List<String>,
    val pricePerNight: Int
)

// Everything the Booking Details screen needs for one hotel
data class HotelDetails(
    val id: Int,
    val name: String,
    val ratings: List<Rating>,
    val reviews: List<Review>,
    val rooms: List<HotelRoom>
)

object HotelDetailsLoader {

    // Reads hotel details from assets.
    // Loads details ONLY if an exact JSON file exists for the hotel (e.g. hotels_details.1000.json or hotels_details.1008.json).
    // Returns null for any hotel that does not have its own JSON file.
    fun load(context: Context, hotelId: Int): HotelDetails? {
        val exactFileExists = try {
            context.assets.open("hotels_details.$hotelId.json").close()
            true
        } catch (_: Exception) {
            false
        }

        if (!exactFileExists) {
            return null
        }

        return try {
            val text = context.assets
                .open("hotels_details.$hotelId.json")
                .bufferedReader()
                .use { it.readText() }
            parse(JSONObject(text))
        } catch (e: Exception) {
            Log.e("HotelDetailsLoader", "Could not load hotel $hotelId", e)
            null
        }
    }

    private fun parse(root: JSONObject): HotelDetails {
        val guestReviews = root.getJSONObject("guest_reviews")

        val ratingsArray = guestReviews.getJSONArray("ratings_categories")
        val ratings = (0 until ratingsArray.length()).map { i ->
            val item = ratingsArray.getJSONObject(i)
            val key = item.keys().next()
            Rating(category = key, score = item.getDouble(key))
        }

        val reviewsArray = guestReviews.getJSONArray("reviews_objects")
        val reviews = (0 until reviewsArray.length()).map { i ->
            val item = reviewsArray.getJSONObject(i)
            Review(
                username = item.getString("username"),
                country = item.getString("country"),
                text = item.getString("review_text")
            )
        }

        val roomsArray = root.getJSONArray("rooms")
        val rooms = (0 until roomsArray.length()).map { i ->
            val item = roomsArray.getJSONObject(i)
            val featuresArray = item.getJSONArray("room_features")
            HotelRoom(
                id = item.getInt("room_id"),
                type = item.getString("room_type"),
                bedType = item.getString("room_bed_type"),
                maxGuests = item.getInt("room_total_number_of_guests"),
                features = (0 until featuresArray.length()).map { featuresArray.getString(it) },
                pricePerNight = item.getInt("room_price_for_one_night")
            )
        }

        return HotelDetails(
            id = root.getInt("hotel_id"),
            name = root.getString("hotel_name"),
            ratings = ratings,
            reviews = reviews,
            rooms = rooms
        )
    }
}
