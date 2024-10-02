package com.mammoth.androidlab.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.mammoth.androidlab.data.RadioCountry
import com.mammoth.androidlab.ui.viewmodel.CountryListViewModel
import com.mammoth.androidlab.util.NavPath.home

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountryListScreen(viewModel: CountryListViewModel, navController: NavHostController) {

    val countries by viewModel.countries.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                Text(text = "Area", color = Color(0xFF00FF00))
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black)
            )
        }
    ) { innerPadding ->
        if (countries == null) {
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
                AreaList(
                    viewModel = viewModel,
                    navController = navController,
                    countries = countries?: emptyList()
                )
            }
        }

        BackHandler {
            navController.popBackStack(home, false)
        }
    }
}

@Composable
fun AreaList(viewModel: CountryListViewModel, navController: NavHostController, countries: List<RadioCountry>) {
    LazyColumn(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.Start
    ) {
        items(countries) { country ->
            MatrixTextItem(
                text = country.name,
                onCountrySelected = {
                    viewModel.upgradeCountry(country)
                    navController.popBackStack()
                }
            )
        }
    }
}

@Composable
fun MatrixTextItem(text: String, onCountrySelected: () -> Unit) {
    // Matrix-style green text with monospace font
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = text,
            color = Color(0xFF00FF00), // Neon green color
            fontFamily = FontFamily.Monospace, // Monospace font for terminal effect
            fontSize = 18.sp,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .padding(8.dp)
                .clickable {
                    onCountrySelected.invoke()
                }
        )
    }
}