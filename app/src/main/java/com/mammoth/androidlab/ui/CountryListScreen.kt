package com.mammoth.androidlab.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
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
import com.mammoth.androidlab.ui.viewmodel.RadioViewModel

@Composable
fun CountryListScreen(viewModel: RadioViewModel, navController: NavHostController) {
    val countries by viewModel.countries.collectAsState()
    if (countries != null) {
        // Display the list of countries (use LazyColumn for a scrollable list)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            LazyColumn(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.Start
            ) {
                items(countries!!) { country ->
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
    } else {
        // Show a loading indicator
        Text(text = "Loading countries...")
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