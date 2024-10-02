package com.mammoth.androidlab.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.mammoth.androidlab.database.AppDatabase
import com.mammoth.androidlab.database.FavoriteStation
import com.mammoth.androidlab.database.FavoriteStationDao
import com.mammoth.androidlab.ui.common.BottomNavigationBar
import com.mammoth.androidlab.ui.viewmodel.FavoriteViewModel
import com.mammoth.androidlab.util.NavPath.home

class FavoriteViewModelFactory(private val dao: FavoriteStationDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavoriteViewModel::class.java)) {
            return FavoriteViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

@Composable
fun FavoritesScreen(
    navController: NavHostController,
    viewModel: FavoriteViewModel = viewModel(factory = FavoriteViewModelFactory(AppDatabase.getDatabase(
        LocalContext.current).favoriteStationDao())),
    onStationClick: (FavoriteStation) -> Unit = {},
    onBack: () -> Unit,
) {
    val favoriteStations by viewModel.favoriteStations.collectAsState()

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.getFavoriteStations()
    }

    Scaffold(
        topBar = {
            RadioAppToolbar(
                title = "Favorites",
                navController = navController,
                feature = Features.FAVORITIES,
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = Color.Black,
                contentColor = Color(0xFF00FF00),
            ) {
                BottomNavigationBar(navController = navController)
            }
        },
    ) { innerPadding ->
        if (favoriteStations.isEmpty()) {
            Text(
                text = "No favorite stations",
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                style = MaterialTheme.typography.bodyMedium
            )
        } else {
            LazyColumn(
                modifier = Modifier.background(Color.Black).fillMaxSize().padding(innerPadding)
            ) {
                items(favoriteStations) { station ->
                    FavoriteStationItem(
                        station = station,
                        onClick = { onStationClick(station) },
                        onRemoveClick = { viewModel.removeFavorite(station) }
                    )
                }
            }
        }

        BackHandler {
            navController.popBackStack(home, false)
        }
    }
}

@Composable
fun FavoriteStationItem(
    station: FavoriteStation,
    onClick: () -> Unit,
    onRemoveClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent,
            contentColor = Color.Transparent,
        ),
        onClick = { onClick.invoke() },
    ) {
        Row(
            modifier = Modifier
                .background(color = Color.Transparent)
                .fillMaxWidth()
                .padding(16.dp)
                .clickable { /* Handle item click */ },
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Start icon
            Icon(
                imageVector = Icons.Filled.Radio,
                contentDescription = "Radio",
                tint = Color(0xFF00FF00),
                modifier = Modifier.size(24.dp)
            )

            // Station name text
            Column(
                modifier = Modifier
                    .weight(1f)
                    .background(color = Color.Transparent)
                    .padding(start = 16.dp),
            ) {
                Text(text = station.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF00FF00), // Neon green color
                    fontFamily = FontFamily.Monospace, // Monospace font for terminal effect
                    fontSize = 16.sp,
                    textAlign = TextAlign.Start,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable {
                            onClick.invoke()
                        }
                )
            }
            // Favorite icon
            // Remove favorite icon
            IconButton(onClick = onRemoveClick) {
                Icon(
                    imageVector = Icons.Filled.Remove,
                    contentDescription = "Remove Favorite",
                    tint = Color(0xFF00FF00),
                )
            }
        }
    }
}