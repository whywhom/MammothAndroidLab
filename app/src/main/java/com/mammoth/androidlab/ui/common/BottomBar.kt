package com.mammoth.androidlab.ui.common

import android.annotation.SuppressLint
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.mammoth.androidlab.util.NavPath.favorites
import com.mammoth.androidlab.util.NavPath.home
import com.mammoth.androidlab.util.NavPath.radio

@SuppressLint("RestrictedApi")
@Composable
fun BottomNavigationBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(containerColor = Color.Black) {
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = currentRoute == home, // Set selected based on the current route
            onClick = {
                val destination = navController.findDestination(home)
                navController.navigate(home) {
                    if(destination != null) {
                        popUpTo(home) { inclusive = false }
                    }
                    launchSingleTop = true
                }
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Favorite, contentDescription = "Favorites") },
            label = { Text("Favorites") },
            selected = currentRoute == favorites,
            onClick = {
                val destination = navController.findDestination(favorites)
                navController.navigate(favorites) {
                    if(destination != null) {
                        popUpTo(favorites) { inclusive = false }
                    }
                    launchSingleTop = true
                }
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Radio, contentDescription = "Radio") },
            label = { Text("Radio") },
            selected = currentRoute == radio,
            onClick = {
                val destination = navController.findDestination(radio)
                navController.navigate(radio) {
                    if(destination != null) {
                        popUpTo(radio) { inclusive = false }
                    }
                    launchSingleTop = true
                }
            }
        )
    }
}