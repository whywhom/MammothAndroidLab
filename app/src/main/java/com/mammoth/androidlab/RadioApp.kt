package com.mammoth.androidlab

import android.app.Application
import com.mammoth.androidlab.database.AppDatabase

class RadioApp: Application() {
    override fun onCreate() {
        super.onCreate()
        val db = AppDatabase.getDatabase(this.applicationContext)
    }
}