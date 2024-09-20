package com.mammoth.androidlab.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mammoth.androidlab.R
import com.mammoth.androidlab.data.RadioStation
import com.mammoth.androidlab.ui.viewmodel.MediaPlayerViewModel
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.launch
import kotlin.random.Random

@Composable
fun MediaPlayerScreen(viewModel: MediaPlayerViewModel, station: RadioStation?) {
    val context = LocalContext.current
    val isPlaying by viewModel.isPlaying.observeAsState(false)
    // Load and play the radio station once the screen is displayed
    station?.let { radio->
        LaunchedEffect(radio.url) {
            if (radio.url.isNotEmpty()) {
                viewModel.playRadio(radio.url)
            }
        }

        AudioPlayerUI(
            albumArtUrl = null,
            trackTitle = station.name,
            artistName = station.url,
            isPlaying = isPlaying,
            onPlayPause = {viewModel.playPause()},
            onNext = {},
            onPrevious =  { },
            onSeek = {},
            progress = 0.0f
        )

        // Release the player when the composable leaves the composition
        DisposableEffect(Unit) {
            onDispose {
                viewModel.playerRelease()
            }
        }
    }
}

@Composable
fun AudioPlayerUI(
    albumArtUrl: String?,
    trackTitle: String,
    artistName: String,
    isPlaying: Boolean,
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onSeek: (Float) -> Unit,
    progress: Float
) {
    // Background gradient for the UI
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = Color.Black
//                Brush.verticalGradient(
//                    listOf(Color(0xFF2B2B2B), Color(0xFF1A1A1A))
//                )
            )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Album Art
            if (albumArtUrl != null) {
                MatrixStyleAlbumArt(
                    painter =
                    rememberAsyncImagePainter(albumArtUrl)
                )
            } else {
                MatrixAudioAnimation()
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Track Title
            Text(
                text = trackTitle,
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

            // Artist Name
            Text(
                text = artistName,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Progress Slider
            Slider(
                value = progress,
                onValueChange = { onSeek(it) },
                colors = SliderDefaults.colors(
                    thumbColor = Color.White,
                    activeTrackColor = Color.Green,
                    inactiveTrackColor = Color.Gray
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Playback Controls
            Row(
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(
                    onClick = { onPrevious() },
                    modifier = Modifier.size(60.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipPrevious,
                        contentDescription = "Previous",
                        tint = Color.White,
                        modifier = Modifier.size(48.dp)
                    )
                }

                IconButton(
                    onClick = { onPlayPause() },
                    modifier = Modifier
                        .size(80.dp)
                        .background(Color.Green, CircleShape)
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "Play/Pause",
                        tint = Color.White,
                        modifier = Modifier.size(48.dp)
                    )
                }

                IconButton(
                    onClick = { onNext() },
                    modifier = Modifier.size(60.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = "Next",
                        tint = Color.White,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun MatrixStyleAlbumArt(painter: Painter) {
    Box(
        modifier = Modifier
            .size(150.dp)
            .clip(CircleShape) // Makes the image round
            .shadow(10.dp, CircleShape) // Adds shadow around the circle
            .drawBehind {
                drawCircle(
                    color = Color.Green.copy(alpha = 0.7f),
                    radius = size.minDimension / 2,
                    center = center
                )
            }
            .graphicsLayer {
                // Simulate a subtle matrix-like distortion if desired
                rotationY = 10f // Slight 3D effect
                scaleX = 1.1f
            },
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painter,
            contentDescription = "Album Art",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            colorFilter = ColorFilter.tint(Color.Green.copy(alpha = 0.5f)) // Adds Matrix-style green tint
        )
    }
}

@Composable
fun MatrixAudioAnimation(modifier: Modifier = Modifier, barCount: Int = 20) {
    // Create a list of animated states for each bar
    val bars = remember { List(barCount) { Animatable(Random.nextFloat()) } }
    val density = LocalDensity.current
    // Animate each bar independently
    LaunchedEffect(Unit) {
        bars.forEachIndexed { index, animatable ->
            launch {
                while (true) {
                    animatable.animateTo(
                        Random.nextFloat(),
                        animationSpec = tween(
                            durationMillis = Random.nextInt(500, 1500),
                            easing = LinearOutSlowInEasing
                        )
                    )
                }
            }
        }
    }

    // Draw the bars as they animate up and down
    Row(
        modifier = modifier.fillMaxWidth()
            .height(300.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            bars.forEachIndexed { index, bar ->
                val xPosition = size.width / barCount * index
                drawMatrixCharacters(xPosition, bar.value, density)
            }
        }
    }
}

fun DrawScope.drawMatrixCharacters(xPosition: Float, heightFactor: Float, density: Density) {
    val textHeight = with(density) { 16.sp.toPx() } // Character size
    val totalRows = (size.height / textHeight).toInt()

    val randomCharacters = generateRandomCharacters(totalRows)

    randomCharacters.forEachIndexed { index, char ->
        val yPosition = size.height * heightFactor + textHeight * index
        if (yPosition <= size.height) {
            drawIntoCanvas { canvas ->
                val paint = android.graphics.Paint().apply {
                    color = android.graphics.Color.GREEN
                    textSize = textHeight
                    alpha = 255
                }
                canvas.nativeCanvas.drawText(
                    char.toString(),
                    xPosition,
                    yPosition,
                    paint
                )
            }
        }
    }
}

fun generateRandomCharacters(count: Int): List<Char> {
    val charPool = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
    return List(count) { charPool.random() }
}