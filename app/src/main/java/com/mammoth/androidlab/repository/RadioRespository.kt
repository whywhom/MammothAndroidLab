package com.mammoth.androidlab.repository

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mammoth.androidlab.data.RadioCountry
import com.mammoth.androidlab.repository.local.RadioLocalDataSource
import com.mammoth.androidlab.repository.local.StorageType
import com.mammoth.androidlab.repository.remote.RadioRemoteDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class RadioRespository(
    private val remoteDataSource: RadioRemoteDataSource = RadioRemoteDataSource(),
    private val localDataSource: RadioLocalDataSource = RadioLocalDataSource
) {
    suspend fun getCurrentCountry(context: Context, storageType: StorageType = StorageType.PrimitiveStorage): String? {
        return localDataSource.getCurrentCountry(context,storageType)
    }

    suspend fun saveCurrentCountry(storageType: StorageType = StorageType.PrimitiveStorage, context: Context, currentCountryCode: String) {
        localDataSource.saveCurrentCountry(context = context, currentCountry = currentCountryCode)
    }

    suspend fun getCountries(context: Context) : Flow<MutableList<RadioCountry>> {
        return withContext(Dispatchers.IO) {
            val cachedCountriesJson = localDataSource.getCountries(context)
            if (cachedCountriesJson != null) {
                cachedCountriesJson as String
                // Use cached countries
                val countriesList: MutableList<RadioCountry> = Gson().fromJson(
                    cachedCountriesJson,
                    object : TypeToken<MutableList<RadioCountry>>() {}.type
                )

                flow {
                    emit(countriesList)
                }
            } else {
                // Fetch from API if no cache available
                val countryList = remoteDataSource.getCountries()
                flow {
                    emit(countryList)
                }
            }
        }
    }
}