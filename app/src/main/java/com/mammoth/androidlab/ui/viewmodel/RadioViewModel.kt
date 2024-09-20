package com.mammoth.androidlab.ui.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.ViewModel
import com.mammoth.androidlab.data.RadioStation
import com.mammoth.androidlab.repository.remote.RadioService
import com.mammoth.androidlab.util.getCurrentCountryCode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mammoth.androidlab.data.RadioCountry
import com.mammoth.androidlab.media.RadioPlayer
import com.mammoth.androidlab.repository.local.DataStoreManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale

@SuppressLint("StaticFieldLeak")
class RadioViewModel(
    private val context: Context,
) : ViewModel() {
    private val radioService = RadioService()
    private val _radioStations = MutableStateFlow<List<RadioStation>>(emptyList())
    val radioStations: StateFlow<List<RadioStation>> = _radioStations
    private val _countries = MutableStateFlow<List<RadioCountry>?>(null)
    val countries: StateFlow<List<RadioCountry>?> = _countries
    private val _currentCountryCode = MutableStateFlow<String?>(null)
    val currentCountryCode: StateFlow<String?> = _currentCountryCode

    init {
        getCurrentCountry()
        fetchCountries()
    }

    private fun getCurrentCountry() {
        viewModelScope.launch {
            // Check cache first
            DataStoreManager.getCurrentCountry(context).collect { country ->
                if (country != null) {
                    // Use cached countries
                    _currentCountryCode.value = country.toString()
                } else {
                    // Fetch from API if no cache available
                    val currentCountryCode = getCurrentCountryCode()
                    viewModelScope.launch(Dispatchers.IO){
                        DataStoreManager.saveCurrentCountry(context = context, currentCountry = currentCountryCode)
                    }
                    _currentCountryCode.value = currentCountryCode
                }
            }
        }
    }

    private fun fetchCountries() {
        viewModelScope.launch {
            // Check cache first
            DataStoreManager.getCountries(context).collect { cachedCountriesJson ->
                if (cachedCountriesJson != null) {
                    cachedCountriesJson as String
                    // Use cached countries
                    val countriesList: List<RadioCountry> = Gson().fromJson(cachedCountriesJson, object : TypeToken<List<RadioCountry>>() {}.type)
                    _countries.value = countriesList
                } else {
                    // Fetch from API if no cache available
                    radioService.getCountries { countryList ->
                        if (countryList != null) {
                            _countries.value = countryList
                            // Cache the fetched country list
                            val countryListJson = Gson().toJson(countryList)
                            viewModelScope.launch(Dispatchers.IO){
                                DataStoreManager.saveCountries(context = context, countryListJson = countryListJson)
                            }
                        }
                    }
                }
            }
        }
    }

    fun getStationsForCountry(countryCode: String) {
        _countries.value?.let {countries ->
            val countryName = getCurrentCountry(countries = countries ,currentCountry = countryCode)
            countryName?.let {currentCountryName ->
                viewModelScope.launch {
                    radioService.getRadioStationsByCountry(currentCountryName) { stations ->
                        stations?.let {
                            _radioStations.value = it
                        }
                    }
                }
            }
        }
    }

    private fun getCurrentCountry(countries: List<RadioCountry>, currentCountry: String): String? {
        val country = countries.find {
            it.iso_3166_1.lowercase(Locale.ROOT)
                .trim() == currentCountry.lowercase()
        }
        return country?.name
    }

    fun upgradeCountry(country: RadioCountry) {
        viewModelScope.launch(Dispatchers.IO){
            DataStoreManager.saveCurrentCountry(context = context, currentCountry = country.iso_3166_1)
        }
        _currentCountryCode.value = country.iso_3166_1
        getStationsForCountry(countryCode = country.iso_3166_1)
    }
}