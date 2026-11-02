package com.example.estradahotel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.estradahotel.data.AppDatabase
import com.example.estradahotel.data.BookingEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class BookingViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getDatabase(application).bookingDao()


    val bookings: StateFlow<List<BookingEntity>> = dao.getAllBookings()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )


    private var isSaving = false


    fun saveBooking(booking: BookingEntity, onSaved: () -> Unit) {
        if (isSaving) return
        isSaving = true
        viewModelScope.launch {
            dao.insertBooking(booking)
            isSaving = false
            onSaved()
        }
    }
}