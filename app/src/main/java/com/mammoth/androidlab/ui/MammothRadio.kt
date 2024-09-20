package com.mammoth.androidlab.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.mammoth.androidlab.data.RadioStation
import com.mammoth.androidlab.media.RadioPlayer
import com.mammoth.androidlab.util.NavPath.countryList
import com.mammoth.androidlab.util.NavPath.radio
import com.mammoth.androidlab.ui.viewmodel.MediaPlayerViewModel
import com.mammoth.androidlab.ui.viewmodel.RadioViewModel
import com.mammoth.androidlab.util.NavPath.favorites
import com.mammoth.androidlab.util.NavPath.home

@Composable
fun MammothRadio(onBack: () -> Unit) {
    val context = LocalContext.current
    val navController = rememberNavController()
    val radioViewModel = RadioViewModel(
        context = context
    )
    val mediaPlayerViewModel = MediaPlayerViewModel(
        context = context,
        radioPlayer = RadioPlayer(context)
    )
    NavHost(navController = navController, startDestination = home) {
        composable(home) {
            HomeScreen(
                navController = navController,
                onBack = onBack
            )
        }
        composable(favorites) {
            FavoritesScreen(
                navController = navController,
                onBack = onBack
            )
        }
        composable(radio) {
            RadioScreen(
                viewModel = radioViewModel,
                navController = navController,
                onBack = onBack
            )
        }
        composable(countryList) {
            CountryListScreen(
                viewModel = radioViewModel,
                navController = navController
            )
        }
        composable<RadioStation> {backStackEntry ->
            val station: RadioStation = backStackEntry.toRoute()
            MediaPlayerScreen(
                viewModel = mediaPlayerViewModel,
                station = station
            )
        }
    }
}

