package com.mammoth.androidlab.repository.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

val Context.dataStore by preferencesDataStore("app_preferences")

enum class StorageType{
    InMemoryStorage,
    PrimitiveStorage
}

object RadioLocalDataSource {
    private val inMemoryMap: MutableMap<String, Any> = HashMap()

    private val COUNTRY_KEY = stringPreferencesKey("countries")
    private val CURRENT_COUNTRY_KEY = stringPreferencesKey("current_country")

    suspend fun saveCountries(storageType: StorageType = StorageType.InMemoryStorage, context: Context, countryListJson: String) {
        when (storageType) {
            StorageType.InMemoryStorage -> {
                inMemoryMap[COUNTRY_KEY.toString()] = countryListJson
            }
            StorageType.PrimitiveStorage -> {
                context.dataStore.edit { preferences ->
                    preferences[COUNTRY_KEY] = countryListJson
                }
            }
        }
    }

    suspend fun getCountries(context: Context, storageType: StorageType = StorageType.InMemoryStorage): String? {
        return withContext(Dispatchers.IO) {
            var result: String? = null
            when (storageType) {
                StorageType.InMemoryStorage -> {
                    result = (inMemoryMap[COUNTRY_KEY.toString()]) as String?
                }

                StorageType.PrimitiveStorage -> {
                    context.dataStore.data.map { preferences ->
                        result = preferences[COUNTRY_KEY]
                    }
                }
            }
            result
        }
    }

    suspend fun saveCurrentCountry(storageType: StorageType = StorageType.PrimitiveStorage, context: Context, currentCountry: String) {
        when (storageType) {
            StorageType.InMemoryStorage -> {
                inMemoryMap[CURRENT_COUNTRY_KEY.toString()] = currentCountry
            }
            StorageType.PrimitiveStorage -> {
                context.dataStore.edit { preferences ->
                    preferences[CURRENT_COUNTRY_KEY] = currentCountry
                }
            }
        }
    }

    suspend fun getCurrentCountry(context: Context, storageType: StorageType = StorageType.PrimitiveStorage,): String? {
        return withContext(Dispatchers.IO) {
            var result: String? = null
            when (storageType) {
                StorageType.InMemoryStorage -> {
                    result = (inMemoryMap[CURRENT_COUNTRY_KEY.toString()]) as String

                }
                StorageType.PrimitiveStorage -> {
                    context.dataStore.data.map { preferences ->
                        result = preferences[CURRENT_COUNTRY_KEY]
                    }
                }
            }
            result
        }

    }
}