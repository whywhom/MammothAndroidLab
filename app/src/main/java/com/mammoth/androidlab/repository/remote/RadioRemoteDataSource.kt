package com.mammoth.androidlab.repository.remote

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mammoth.androidlab.data.RadioCountry
import com.mammoth.androidlab.data.RadioStation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class RadioRemoteDataSource {

    companion object {
        private val baseUrl = "http://de1.api.radio-browser.info"
    }
    private val client = OkHttpClient()

    fun getRadioStationsByCountry(countryCode: String, callback: (List<RadioStation>?) -> Unit) {
        val url = "$baseUrl/json/stations/bycountry/$countryCode"

        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(null) // Handle failure
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.let { responseBody ->
                    val gson = Gson()
                    val type = object : TypeToken<List<RadioStation>>() {}.type
                    val stations: List<RadioStation> = gson.fromJson(responseBody.string(), type)
                    val stationsNoEmpty = stations.filter {
                        it.name.trimStart().isNotEmpty() && it.url.trimStart().trimEnd().isNotEmpty()
                    }
                    val stationsNoEmptyNoRepeat = stationsNoEmpty.distinctBy{it.url}
                    stationsNoEmptyNoRepeat.sortedBy { it.name }
                    callback(stationsNoEmptyNoRepeat)
                }
            }
        })
    }

    suspend fun getCountries() : MutableList<RadioCountry> {
        return withContext(Dispatchers.IO) {
            val countriesNoEmpty: MutableList<RadioCountry> = mutableListOf()
            try {
                val url = "$baseUrl/json/countries"

                val request = Request
                    .Builder()
                    .url(url)
                    .build()

                // Execute the request
                val response: Response = client.newCall(request).execute()
                // Return the response body as a string
                if (response.isSuccessful) {
                    response.body?.let { responseBody ->
                        val gson = Gson()
                        val type = object : TypeToken<List<RadioCountry>>() {}.type
                        val countries: List<RadioCountry> = gson.fromJson(responseBody.string(), type)
                         countries.filter {
                            it.name.isNotEmpty() && it.iso_3166_1.isNotEmpty()
                            countriesNoEmpty.add(it)
                        }
                        countriesNoEmpty.sortedBy { it.name }
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
            countriesNoEmpty
        }
    }
}
