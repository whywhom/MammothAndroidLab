package com.mammoth.androidlab.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.mammoth.androidlab.ui.common.BottomNavigationBar

@Composable
fun HomeScreen(
    navController: NavHostController,
    onBack: () -> Unit,
) {

    Scaffold(
        topBar = {
            RadioAppToolbar(
                title = "Radio App",
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
            Text(
                text = "建设中……",
                color = Color(0xFF00FF00),
                fontSize = 16.sp
            )
        }
    }
}
