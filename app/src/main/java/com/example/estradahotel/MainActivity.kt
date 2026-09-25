package com.example.estradahotel

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.estradahotel.data.HotelEntity
import com.example.estradahotel.ui.theme.EstradaHotelTheme
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EstradaHotelTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    HotelScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotelScreen(viewModel: HotelViewModel = viewModel()) {
    val hotels by viewModel.hotels.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("The Alps Hotels")
                        AsyncImage(
                            model = "file:///android_asset/france_national_flag.png",
                            contentDescription = "France Flag",
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .height(20.dp)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* Profile action */ }) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_account_circle_24),
                            contentDescription = "User Profile"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Search Bar Input
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.onSearchQueryChange(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Search Hotel") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "Search Icon")
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear Search")
                        }
                    }
                },
                singleLine = true,
                shape = MaterialTheme.shapes.medium
            )

            // Hotel Cards List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(hotels) { hotel ->
                    HotelCard(hotel = hotel)
                }
            }
        }
    }
}

@Composable
fun HotelCard(hotel: HotelEntity) {
    val imageUri = "file:///android_asset/${hotel.imageName}"

    // Parse values from location string
    val skiDistance = hotel.location.substringBefore("|").trim()
    val ratingValue = hotel.location.substringAfter("Rating:").trim()

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Square Thumbnail Image on Left
            AsyncImage(
                model = imageUri,
                contentDescription = hotel.name,
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Details Column on Right
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = hotel.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Rating Row
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Rating: ",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Icon(
                        painter = painterResource(id = R.drawable.baseline_star_outline_24),
                        contentDescription = "Rating Star",
                        tint = Color.Black,
                        modifier = Modifier
                            .size(16.dp)
                            .padding(end = 2.dp)
                    )

                    Text(
                        text = ratingValue,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                // Ski Distance
                Text(
                    text = skiDistance,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Price
                val formattedPrice = String.format(Locale.US, "%.2f", hotel.pricePerNight)
                Text(
                    text = "$$formattedPrice / night",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}