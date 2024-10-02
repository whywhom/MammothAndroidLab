package com.mammoth.androidlab.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FavoriteStationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(favoriteStation: FavoriteStation)

    @Delete
    suspend fun delete(favoriteStation: FavoriteStation)

    @Query("SELECT * FROM favorite_stations")
    suspend fun getAllFavoriteStations(): List<FavoriteStation>
}