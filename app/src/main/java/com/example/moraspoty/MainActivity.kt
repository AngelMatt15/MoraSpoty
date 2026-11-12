package com.example.moraspoty

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Paleta de colores MoraSpoty
val BgDarkBerry = Color(0xFF13091E)
val CardPurple = Color(0xFF231436)
val AccentMora = Color(0xFFB5179E)
val TextLight = Color(0xFFF8F0FB)

// 1. MODELO: Estructura base de una canción
data class Song(
    val id: Int,
    val title: String,
    val artist: String,
    var isFavorite: Boolean = false
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MoraSpotyApp()
        }
    }
}

@Composable
fun MoraSpotyApp() {
    // 2. ESTADO (Lógica de negocio simple)
    var currentScreen by remember { mutableIntStateOf(0) } // 0: Home, 1: Player, 2: Favoritos
    var isPlaying by remember { mutableStateOf(false) }

    // Catálogo inicial de prueba
    val songList = remember {
        mutableStateListOf(
            Song(1, "Compilando a las 3 AM", "DJ Gradle"),
            Song(2, "NullPointer en mi Corazón", "Los Kotlin"),
            Song(3, "Sin Internet en el Examen", "Banda UTTN"),
            Song(4, "Lofi para Pasar el Cuatri", "Chico Sintaxis")
        )
    }
    var currentSong by remember { mutableStateOf(songList[0]) }

    Scaffold(
        // Barra de navegación inferior para alternar entre las 3 vistas
        bottomBar = {
            NavigationBar(containerColor = CardPurple) {
                NavigationBarItem(
                    selected = currentScreen == 0,
                    onClick = { currentScreen = 0 },
                    icon = { Text("🏠", fontSize = 20.sp) },
                    label = { Text("Inicio", color = TextLight) }
                )
                NavigationBarItem(
                    selected = currentScreen == 1,
                    onClick = { currentScreen = 1 },
                    icon = { Text("🎵", fontSize = 20.sp) },
                    label = { Text("Player", color = TextLight) }
                )
                NavigationBarItem(
                    selected = currentScreen == 2,
                    onClick = { currentScreen = 2 },
                    icon = { Text("💜", fontSize = 20.sp) },
                    label = { Text("Favoritos", color = TextLight) }
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BgDarkBerry)
                .padding(padding)
        ) {
            // Navegación condicional de las 3 vistas
            when (currentScreen) {
                0 -> HomeScreen(
                    songs = songList,
                    onSongClick = { song ->
                        currentSong = song
                        isPlaying = true
                        currentScreen = 1 // Abre el reproductor al tocar canción
                    }
                )
                1 -> PlayerScreen(
                    song = currentSong,
                    isPlaying = isPlaying,
                    onPlayPause = { isPlaying = !isPlaying },
                    onToggleFav = {
                        val index = songList.indexOf(currentSong)
                        if (index != -1) {
                            val updated = currentSong.copy(isFavorite = !currentSong.isFavorite)
                            songList[index] = updated
                            currentSong = updated
                        }
                    }
                )
                2 -> FavoritesScreen(
                    favorites = songList.filter { it.isFavorite }
                )
            }
        }
    }
}

// VISTA 1: Catálogo principal
@Composable
fun HomeScreen(songs: List<Song>, onSongClick: (Song) -> Unit) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("MoraSpoty", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = AccentMora)
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(songs) { song ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardPurple),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clickable { onSongClick(song) }
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("▶", color = AccentMora, fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(song.title, color = TextLight, fontWeight = FontWeight.Bold)
                            Text(song.artist, color = Color.Gray, fontSize = 14.sp)
                        }
                    }
                }
            }
        }
    }
}

// VISTA 2: Reproductor de música
@Composable
fun PlayerScreen(
    song: Song,
    isPlaying: Boolean,
    onPlayPause: () -> Unit,
    onToggleFav: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Portada simulada del álbum
        Box(
            modifier = Modifier
                .size(200.dp)
                .background(CardPurple, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text("🎧", fontSize = 72.sp)
        }
        Spacer(modifier = Modifier.height(24.dp))

        Text(song.title, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextLight)
        Text(song.artist, fontSize = 16.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(24.dp))

        // Controles principales
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = onToggleFav) {
                Text(
                    text = if (song.isFavorite) "💜 Favorito" else "🤍 Me gusta",
                    fontSize = 18.sp,
                    color = AccentMora
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(
                onClick = onPlayPause,
                colors = ButtonDefaults.buttonColors(containerColor = AccentMora)
            ) {
                Text(if (isPlaying) "⏸ Pausar" else "▶ Reproducir", color = TextLight)
            }
        }
    }
}

// VISTA 3: Lista de canciones favoritas
@Composable
fun FavoritesScreen(favorites: List<Song>) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Tus Favoritos", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = TextLight)
        Spacer(modifier = Modifier.height(16.dp))

        if (favorites.isEmpty()) {
            Text("Aún no tienes canciones con corazón.", color = Color.Gray)
        } else {
            LazyColumn {
                items(favorites) { song ->
                    Text(
                        text = "💜 ${song.title} - ${song.artist}",
                        color = TextLight,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }
}