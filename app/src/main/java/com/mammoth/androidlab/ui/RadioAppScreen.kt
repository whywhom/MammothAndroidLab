package com.mammoth.androidlab.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun RadioAppScreen(
    onBack: () -> Unit = {}
) {
    Box(modifier = Modifier.fillMaxSize()) {
        MaterialTheme {
            MammothRadio(onBack = onBack)
        }
    }
}