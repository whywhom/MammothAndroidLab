package com.mammoth.androidlab.util

import java.util.Locale

fun getCurrentCountryCode(): String {
    return Locale.getDefault().country
}