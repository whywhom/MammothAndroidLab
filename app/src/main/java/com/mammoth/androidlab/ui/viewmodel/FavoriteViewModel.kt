package com.mammoth.androidlab.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mammoth.androidlab.database.FavoriteStation
import com.mammoth.androidlab.database.FavoriteStationDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FavoriteViewModel(private val favoriteStationDao: FavoriteStationDao) : ViewModel() {

    private val _favoriteStations = MutableStateFlow<List<FavoriteStation>>(emptyList())
    val favoriteStations: StateFlow<List<FavoriteStation>> get() = _favoriteStations

    fun getFavoriteStations() {
        viewModelScope.launch {
            _favoriteStations.value = favoriteStationDao.getAllFavoriteStations()
        }
    }

    fun removeFavorite(station: FavoriteStation) {
        viewModelScope.launch {
            favoriteStationDao.delete(station)
            getFavoriteStations()  // Refresh the list
        }
    }
}