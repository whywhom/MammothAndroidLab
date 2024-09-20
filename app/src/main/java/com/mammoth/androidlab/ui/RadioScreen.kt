package com.mammoth.androidlab.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.setValue
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
import com.mammoth.androidlab.ui.common.BottomNavigationBar
import com.mammoth.androidlab.util.NavPath.countryList
import com.mammoth.androidlab.ui.viewmodel.RadioViewModel

@Composable
fun RadioScreen(
    viewModel: RadioViewModel,
    navController: NavHostController,
    onBack: () -> Unit,
) {
    val stations by viewModel.radioStations.collectAsState(initial = emptyList())
    val countries by viewModel.countries.collectAsState(initial = emptyList())
    // Variable to hold the selected country
    val selectedCountry by viewModel.currentCountryCode.collectAsState()

    val context = LocalContext.current


    if (countries?.isNotEmpty() == true) {
        selectedCountry?.let { viewModel.getStationsForCountry(countryCode = it) }
    }

    Scaffold(
        topBar = {
            RadioAppToolbar(
                title = getCountryName(countries, selectedCountry)?:"Radio App",
                onBackClick = {},
                onSearchClick = {}
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(innerPadding)
        ) {
            // Show the Dropdown Menu to select a country
            SwitchCountry(
                navController = navController,
                selectedCountry = getCountryName(countries, selectedCountry)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // If a country is selected, display the station list
            StationList(viewModel = viewModel, navController = navController, stations = stations)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RadioAppToolbar(
    title: String,
    onBackClick: () -> Unit,
    onSearchClick: () -> Unit
) {
    TopAppBar(
        title = {
            Text(text = title, color = Color(0xFF00FF00))
        },
//        navigationIcon = {
//            IconButton(onClick = onBackClick) {
//                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
//            }
//        },
        actions = {
            IconButton(onClick = onSearchClick) {
                Icon(Icons.Filled.Menu, contentDescription = "Menu", tint = Color(0xFF00FF00))
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black)
    )
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
fun RadioStationItem(station: RadioStation, onClick: () -> Unit) {
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
        Column(
            modifier = Modifier
                .background(color = Color.Transparent)
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
//            Text(text = station.url, style = MaterialTheme.typography.bodySmall)
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