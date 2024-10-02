package com.mammoth.androidlab.ui.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mammoth.androidlab.data.RadioCountry
import com.mammoth.androidlab.repository.RadioRespository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@SuppressLint("StaticFieldLeak")
class CountryListViewModel(
    private val context: Context,
    private val radioRespository: RadioRespository = RadioRespository()
) : ViewModel() {
    private val _countries = MutableStateFlow<List<RadioCountry>?>(null)
    val countries: StateFlow<List<RadioCountry>?> = _countries

    init {
        fetchCountries()
    }

    private fun fetchCountries() {
        viewModelScope.launch {
            // Check cache first
            radioRespository.getCountries(context).collect { countriesList ->
                // Use cached countries
                _countries.value = countriesList
            }
        }
    }

    fun upgradeCountry(country: RadioCountry) {
        viewModelScope.launch(Dispatchers.IO){
            radioRespository.saveCurrentCountry(context = context, currentCountryCode = country.iso_3166_1)
        }
    }
}