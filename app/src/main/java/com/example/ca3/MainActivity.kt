package com.example.ca3

import android.os.Bundle
import java.net.URLDecoder
import java.net.URLEncoder
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage

data class Song(
    val title: String,
    val artist: String,
    val genre: String
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "songList"
    ) {
        composable("songList") {
            SongApp(navController)
        }

        composable("details/{title}/{artist}/{genre}/{description}") { backStackEntry ->
            val title = URLDecoder.decode(
                backStackEntry.arguments?.getString("title") ?: "",
                "UTF-8"
            )
            val artist = URLDecoder.decode(
                backStackEntry.arguments?.getString("artist") ?: "",
                "UTF-8"
            )
            val genre = URLDecoder.decode(
                backStackEntry.arguments?.getString("genre") ?: "",
                "UTF-8"
            )
            val description = URLDecoder.decode(
                backStackEntry.arguments?.getString("description") ?: "",
                "UTF-8"
            )

            SongDetailScreen(
                title = title,
                artist = artist,
                genre = genre,
                description = description,
                navController = navController
            )
        }
    }
}

@Composable
fun SongApp(navController: NavController) {
    val songList = remember {
        mutableStateListOf(
            Song("Buzzcut Season", "Lorde", "Pop"),
            Song("Ribs", "Lorde", "Pop"),
            Song("CUNTISSIMO", "MARINA", "Indie"),
            Song("Headphone On", "Addison Rae", "Pop"),
            Song("Fame is a Gun", "Addison Rae", "Pop")
        )
    }

    var title by remember { mutableStateOf("") }
    var artist by remember { mutableStateOf("") }
    var genre by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "My Song Playlist",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            itemsIndexed(songList) { index, song ->
                SongItem(
                    song = song,
                    onDelete = { songList.removeAt(index) },
                    onViewDetails = {
                        val description = when (song.title) {
                            "Buzzcut Season" -> "A dreamy pop song by Lorde that talks about youth, imagination, and escaping reality."
                            "Ribs" -> "One of Lorde’s most emotional songs, focusing on growing up and the fear of change."
                            else -> null
                        }

                        if (description != null) {
                            navController.navigate(
                                "details/${
                                    URLEncoder.encode(song.title, "UTF-8")
                                }/${
                                    URLEncoder.encode(song.artist, "UTF-8")
                                }/${
                                    URLEncoder.encode(song.genre, "UTF-8")
                                }/${
                                    URLEncoder.encode(description, "UTF-8")
                                }"
                            )
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Add a New Song",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = artist,
            onValueChange = { artist = it },
            label = { Text("Artist") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = genre,
            onValueChange = { genre = it },
            label = { Text("Genre") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                if (title.isNotBlank() && artist.isNotBlank() && genre.isNotBlank()) {
                    songList.add(Song(title, artist, genre))
                    title = ""
                    artist = ""
                    genre = ""
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Song")
        }
    }
}

@Composable
fun SongItem(
    song: Song,
    onDelete: () -> Unit,
    onViewDetails: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            if (song.title == "Buzzcut Season") {
                AsyncImage(
                    model = "https://i1.sndcdn.com/artworks-000069865512-28nyn6-t500x500.jpg",
                    contentDescription = "Buzzcut Season cover",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))
            }

            Text(text = "Title: ${song.title}")
            Text(text = "Artist: ${song.artist}")
            Text(text = "Genre: ${song.genre}")

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (song.title == "Buzzcut Season" || song.title == "Ribs") {
                    Button(onClick = onViewDetails) {
                        Text("View Details")
                    }
                }

                Button(
                    onClick = onDelete,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red
                    )
                ) {
                    Text("Delete")
                }
            }
        }
    }
}

@Composable
fun SongDetailScreen(
    title: String,
    artist: String,
    genre: String,
    description: String,
    navController: NavController
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Song Details",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Title: $title")
        Text(text = "Artist: $artist")
        Text(text = "Genre: $genre")

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Description:",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(text = description)

        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = { navController.popBackStack() }) {
            Text("Back")
        }
    }
}