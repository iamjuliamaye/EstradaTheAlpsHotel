package com.example.estradahotel.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HotelDao {
    @Query("SELECT * FROM hotels")
    fun getAllHotels(): Flow<List<HotelEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHotels(hotels: List<HotelEntity>)

    @Query("SELECT COUNT(*) FROM hotels")
    suspend fun getCount(): Int

    @Query("DELETE FROM hotels")
    suspend fun clearAll()
}

