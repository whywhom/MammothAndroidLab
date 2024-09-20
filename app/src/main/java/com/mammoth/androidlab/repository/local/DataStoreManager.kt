package com.mammoth.androidlab.repository.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore("app_preferences")

enum class StorageType{
    InMemoryStorage,
    PrimitiveStorage
}

object DataStoreManager {
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

    fun getCountries(context: Context, storageType: StorageType = StorageType.InMemoryStorage): Flow<Any?> {
        return when (storageType) {
            StorageType.InMemoryStorage -> {
                flow {
                    emit(inMemoryMap[COUNTRY_KEY.toString()])
                }
            }
            StorageType.PrimitiveStorage -> {
                context.dataStore.data.map { preferences ->
                    preferences[COUNTRY_KEY]
                }
            }
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

    fun getCurrentCountry(context: Context, storageType: StorageType = StorageType.PrimitiveStorage,): Flow<Any?> {
        return when (storageType) {
            StorageType.InMemoryStorage -> {
                flow {
                    emit(inMemoryMap[CURRENT_COUNTRY_KEY.toString()])
                }
            }
            StorageType.PrimitiveStorage -> {
                context.dataStore.data.map { preferences ->
                    preferences[CURRENT_COUNTRY_KEY]
                }
            }
        }
    }
}