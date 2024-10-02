package com.mammoth.androidlab.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.mammoth.androidlab.data.RadioCountry
import com.mammoth.androidlab.data.RadioStation
import com.mammoth.androidlab.database.AppDatabase
import com.mammoth.androidlab.database.FavoriteStation
import com.mammoth.androidlab.ui.common.BottomNavigationBar
import com.mammoth.androidlab.ui.viewmodel.RadioViewModel
import com.mammoth.androidlab.util.NavPath.countryList
import com.mammoth.androidlab.util.NavPath.home
import kotlinx.coroutines.launch

@Composable
fun RadioScreen(
    viewModel: RadioViewModel,
    navController: NavHostController,
    onBack: () -> Unit,
) {
    val stations by viewModel.radioStations.collectAsState(initial = null)
    val countries by viewModel.countries.collectAsState(initial = emptyList())
    // Variable to hold the selected country
    val currentCountryCode by viewModel.currentCountryCode.collectAsState()

    if (countries?.isNotEmpty() == true) {
        currentCountryCode?.let { viewModel.getStationsForCountry(countryCode = it) }
    }

    val countryName = getCountryName(countries, currentCountryCode)
    val title = if ( countryName != null) "Radio Stations in $countryName" else "Radio Stations"

    Scaffold(
        topBar = {
            RadioAppToolbar(
                feature = Features.RADIO,
                title = title,
                navController = navController,
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
        if (stations == null) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize().background(Color.Black),
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(64.dp),
                    color = Color(0xFF00FF00),
                    trackColor = Color.Black,
                    strokeWidth = 4.dp
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
                    .padding(innerPadding)
            ) {
                // If a country is selected, display the station list
                StationList(
                    viewModel = viewModel,
                    navController = navController,
                    stations = stations?: emptyList()
                )
            }
        }

        BackHandler {
            navController.popBackStack(home, false)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RadioAppToolbar(
    title: String,
    navController: NavHostController,
    feature: Features,
) {
    var menuExpanded by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier.fillMaxWidth()
            .wrapContentHeight()
            .background(color = Color.Transparent)
    ) {
        TopAppBar(
            title = {
                Text(text = title, color = Color(0xFF00FF00))
            },
            actions = {
                IconButton(onClick = { menuExpanded = true }) {
                    Icon(Icons.Filled.Menu, contentDescription = "Menu", tint = Color(0xFF00FF00))
                }

                // Dropdown Menu
                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false },
                    modifier = Modifier.width(150.dp)
                ) {
                    if (feature == Features.RADIO) {
                        DropdownMenuItem(
                            text = { Text("Switch Area") },
                            onClick = {
                                menuExpanded = false
                                navController.navigate(countryList)
                            }
                        )
                    }
                    DropdownMenuItem(
                        text = { Text("Search Station") },
                        onClick = {
                            menuExpanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Setting") },
                        onClick = {
                            menuExpanded = false
                        }
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black)
        )
        HorizontalDivider(thickness = 1.dp, color = Color(0xFF00FF00))
    }
}

fun getCountryName(countries: List<RadioCountry>?, selectedCountry: String?): String? {
    val country =  countries?.find { it.iso_3166_1.equals(selectedCountry, ignoreCase = true) }
    return country?.name
}


@Composable
fun SwitchCountry(navController: NavHostController, selectedCountry: String?) {
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        val selectedText by remember { mutableStateOf("Select a Country") }

        TextButton(onClick = { navController.navigate(countryList) }) {
            Text(text = selectedCountry ?: selectedText, color = Color(0xFF00FF00))
        }
    }
}

@Composable
fun StationList(
    viewModel: RadioViewModel,
    navController: NavHostController,
    stations: List<RadioStation>,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(horizontal = 16.dp),
    ) {
        items(stations) { station ->
            RadioStationItem(
                station = station,
                onClick = {
//                    viewModel.playRadio(station.url)
//                    val encodedUrl = URLEncoder.encode(station.url, StandardCharsets.UTF_8.toString())
                    navController.navigate(route = station)
                }
            )
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(), // Optional padding
                thickness = 1.dp,   // Customize the thickness of the divider
                color = Color.LightGray // Customize the color of the divider
            )
        }
    }
}

@Composable
fun RadioStationItem(
    station: RadioStation,
    onClick: () -> Unit,
    isFavorite: Boolean = false,
    onFavoriteClicked: () -> Unit = {}
) {
    var favorite by remember { mutableStateOf(isFavorite) }

    // Get a coroutine scope for database operations
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val db = AppDatabase.getDatabase(context).favoriteStationDao()

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
//                Text(
//                    text = station.url,
//                    style = MaterialTheme.typography.labelSmall,
//                    color = Color(0xFF00FF00),
//                )
            }
            // Favorite icon
            Icon(
                imageVector = if (favorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                contentDescription = if (favorite) "Remove from favorites" else "Add to favorites",
                modifier = Modifier
                    .size(24.dp)
                    .clickable {
                        favorite= !favorite
                        onFavoriteClicked()
                        scope.launch {
                            if (favorite) {
                                // Add to database
                                db.insert(
                                    FavoriteStation(
                                        name = station.name,
                                        url = station.url,
                                        stationcount = station.stationcount
                                    )
                                )
                            } else {
                                // Remove from database
                                db.delete(
                                    FavoriteStation(
                                        name = station.name,
                                        url = station.url,
                                        stationcount = station.stationcount
                                    )
                                )
                            }
                        }
                    },
                tint = Color(0xFF00FF00),
            )
        }
    }
}

@Composable
fun CountryDropdownMenu(countries: List<RadioCountry>, onCountrySelected: (RadioCountry) -> Unit) {
    // State to control the visibility of the dropdown menu
    var expanded by remember { mutableStateOf(false) }

    // State to store the selected country
    var selectedText by remember { mutableStateOf("Select a Country") }

    // Button to trigger the dropdown
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        TextButton(onClick = { expanded = true }) {
            Text(text = selectedText, color = Color(0xFF00FF00))
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            // List all countries in the dropdown
            countries.forEach { country ->
                DropdownMenuItem(
                    text = { Text(country.name) },
                    onClick = {
                        expanded = false
                        selectedText = country.name // Update the selected country
                        onCountrySelected(country)  // Trigger the callback
                    }
                )
            }
        }
    }
}