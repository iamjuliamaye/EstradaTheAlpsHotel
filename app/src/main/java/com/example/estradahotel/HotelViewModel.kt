package com.example.estradahotel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.estradahotel.data.AppDatabase
import com.example.estradahotel.data.HotelEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.json.JSONArray

class HotelViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getDatabase(application).hotelDao()

    // State for the current search query string
    val searchQuery = MutableStateFlow("")

    // Combines the database hotel list with the search query to produce filtered results
    val hotels: StateFlow<List<HotelEntity>> = combine(
        dao.getAllHotels(),
        searchQuery
    ) { hotelList, query ->
        if (query.isBlank()) {
            hotelList
        } else {
            hotelList.filter { hotel ->
                hotel.name.contains(query, ignoreCase = true) ||
                        hotel.location.contains(query, ignoreCase = true)
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        loadHotelsFromAssets()
    }

    fun onSearchQueryChange(newQuery: String) {
        searchQuery.value = newQuery
    }

    private fun loadHotelsFromAssets() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                dao.clearAll()

                val jsonString = getApplication<Application>()
                    .assets
                    .open("hotels.json")
                    .bufferedReader()
                    .use { it.readText() }

                val jsonArray = JSONArray(jsonString)
                val hotelList = mutableListOf<HotelEntity>()

                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)

                    val name = obj.optString("hotel_name", "Hotel")
                    val rating = obj.optDouble("hotel_rating", 0.0)
                    val distance = obj.optDouble("hotel_to_ski_distance", 0.0)
                    val coverImagePath = obj.optString("hotel_cover_image", "")

                    hotelList.add(
                        HotelEntity(
                            name = name,
                            location = "Ski Distance: ${distance}km | Rating: $rating",
                            pricePerNight = rating * 25.0,
                            imageName = coverImagePath
                        )
                    )
                }

                dao.insertHotels(hotelList)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}