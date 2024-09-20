package com.mammoth.androidlab.repository.remote

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mammoth.androidlab.data.RadioCountry
import com.mammoth.androidlab.data.RadioStation
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class RadioService {

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
                    stationsNoEmpty.sortedBy { it.name }
                    callback(stationsNoEmpty)
                }
            }
        })
    }

    fun getCountries(callback: (List<RadioCountry>?) -> Unit) {
        val url = "$baseUrl/json/countries"

        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(null)
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.let { responseBody ->
                    val gson = Gson()
                    val type = object : TypeToken<List<RadioCountry>>() {}.type
                    val countries: List<RadioCountry> = gson.fromJson(responseBody.string(), type)
                    val countriesNoEmpty = countries.filter {
                        it.name.isNotEmpty() && it.iso_3166_1.isNotEmpty()
                    }
                    countriesNoEmpty.sortedBy { it.name }
                    callback(countriesNoEmpty)
                }
            }
        })
    }
}
