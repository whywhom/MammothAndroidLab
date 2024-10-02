package com.mammoth.androidlab.ui.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mammoth.androidlab.data.RadioCountry
import com.mammoth.androidlab.data.RadioStation
import com.mammoth.androidlab.repository.RadioRespository
import com.mammoth.androidlab.repository.remote.RadioRemoteDataSource
import com.mammoth.androidlab.util.getCurrentCountryCode
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Locale

@SuppressLint("StaticFieldLeak")
class RadioViewModel(
    private val context: Context,
    private val radioRespository: RadioRespository = RadioRespository()
) : ViewModel() {
    private val radioService = RadioRemoteDataSource()
    private val _radioStations = MutableStateFlow<List<RadioStation>?>(null)
    val radioStations: StateFlow<List<RadioStation>?> = _radioStations
    private val _countries = MutableStateFlow<List<RadioCountry>?>(null)
    val countries: StateFlow<List<RadioCountry>?> = _countries
    private val _currentCountryCode = MutableStateFlow<String?>(null)
    val currentCountryCode: StateFlow<String?> = _currentCountryCode

    init {
        getStationByCountry()
    }

    private fun getStationByCountry() {
        viewModelScope.launch {
            async{getCurrentCountry()}
            async{fetchCountries()}
        }
    }

    private suspend fun getCurrentCountry(){
        val country = radioRespository.getCurrentCountry(context)
        if (country != null) {
            _currentCountryCode.value = country.toString()
        } else {
            val currentCountryCode = getCurrentCountryCode()
            radioRespository.saveCurrentCountry(context = context, currentCountryCode = currentCountryCode)
            _currentCountryCode.value = currentCountryCode
        }
    }

    private suspend fun fetchCountries(){
        radioRespository.getCountries(context).collect { cachedCountries ->
            _countries.value = cachedCountries
        }
    }

    fun getStationsForCountry(countryCode: String) {
        _countries.value?.let { list ->
            val countryName =
                getCurrentCountryByCode(countries = list, currentCountry = countryCode)
            countryName?.let { country ->
                radioService.getRadioStationsByCountry(country) { stations ->
                    stations?.let {
                        _radioStations.value = it
                    }
                }
            }
        }
    }

    private fun getCurrentCountryByCode(countries: List<RadioCountry>, currentCountry: String): String? {
        val country = countries.find {
            it.iso_3166_1.lowercase(Locale.ROOT)
                .trim() == currentCountry.lowercase()
        }
        return country?.name
    }

//    fun upgradeCountry(country: RadioCountry) {
//        viewModelScope.launch(Dispatchers.IO){
//            DataStoreManager.saveCurrentCountry(context = context, currentCountry = country.iso_3166_1)
//        }
//        _currentCountryCode.value = country.iso_3166_1
//        viewModelScope.launch {
//            getStationsForCountry(countryCode = country.iso_3166_1)
//        }
//    }
}