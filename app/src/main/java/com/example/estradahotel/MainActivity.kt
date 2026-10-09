package com.example.estradahotel

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.estradahotel.data.HotelEntity
import com.example.estradahotel.ui.theme.EstradaHotelTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EstradaHotelTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Black
                ) {
                    var selectedHotelId by rememberSaveable { mutableStateOf<Int?>(null) }
                    val viewModel: HotelViewModel = viewModel()
                    val hotels by viewModel.hotels.collectAsState()
                    val selectedHotel = hotels.find { it.hotelId == selectedHotelId }

                    if (selectedHotelId == null || selectedHotel == null) {
                        HotelScreen(
                            viewModel = viewModel,
                            onHotelClick = { hotel ->
                                selectedHotelId = hotel.hotelId
                            }
                        )
                    } else {
                        BookingDetailsScreen(
                            hotelId = selectedHotel.hotelId,
                            onBackClick = { selectedHotelId = null }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotelScreen(
    viewModel: HotelViewModel = viewModel(),
    onHotelClick: (HotelEntity) -> Unit = {}
) {
    val hotels by viewModel.hotels.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    Scaffold(
        containerColor = Color.Black,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("The Alps Hotels", color = Color.White)
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
                            contentDescription = "User Profile",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Search field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.onSearchQueryChange(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Search Hotel", color = Color.Gray) },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "Search Icon", tint = Color.White)
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear Search", tint = Color.White)
                        }
                    }
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFF444444),
                    unfocusedBorderColor = Color(0xFF222222),
                    cursorColor = Color.White
                ),
                shape = MaterialTheme.shapes.medium
            )

            // Hotel Cards List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(hotels) { hotel ->
                    HotelCard(hotel = hotel, onClick = { onHotelClick(hotel) })
                }
            }
        }
    }
}

@Composable
fun HotelCard(
    hotel: HotelEntity,
    onClick: () -> Unit = {}
) {
    val imageUri = "file:///android_asset/${hotel.imageName}"

    // Parse values from location string
    val skiDistance = hotel.location.substringBefore("|").trim()
    val ratingString = hotel.location.substringAfter("Rating:").trim()
    val ratingNumeric = ratingString.toDoubleOrNull() ?: 0.0

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E1E1E)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = imageUri,
                contentDescription = hotel.name,
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = hotel.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Rating
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Rating: ",
                        fontSize = 14.sp,
                        color = Color.LightGray
                    )

                    StarRatingBar(rating = ratingNumeric)

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = ratingString,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                // Ski
                Text(
                    text = skiDistance,
                    fontSize = 14.sp,
                    color = Color.LightGray
                )
            }
        }
    }
}

@Composable
fun StarRatingBar(
    rating: Double,
    maxStars: Int = 5
) {
    val starRating = (rating / 2.0).coerceIn(0.0, maxStars.toDouble())
    val fullStars = starRating.toInt()

    Row(verticalAlignment = Alignment.CenterVertically) {
        for (i in 1..maxStars) {
            val isFilled = i <= fullStars

            Icon(
                painter = painterResource(
                    id = if (isFilled) R.drawable.baseline_star_outline_24 else R.drawable.baseline_star_outline_24
                ),
                contentDescription = "Star $i",
                tint = if (isFilled) Color(0xFFFFC107) else Color.DarkGray,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingDetailsScreen(
    hotelId: Int,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val hotelDetails = remember(hotelId) {
        HotelDetailsLoader.load(context, hotelId)
    }

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("Guest reviews", "Room selection")
    val yellowTabColor = Color(0xFFFFC107)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Booking",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black
                )
            )
        },
        containerColor = Color.Black
    ) { innerPadding ->
        if (hotelDetails == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No details found",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Tabs
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = Color.Black,
                    contentColor = yellowTabColor,
                    indicator = { tabPositions ->
                        if (selectedTabIndex < tabPositions.size) {
                            TabRowDefaults.SecondaryIndicator(
                                Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                                color = yellowTabColor
                            )
                        }
                    }
                ) {
                    tabTitles.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = {
                                Text(
                                    text = title,
                                    color = if (selectedTabIndex == index) yellowTabColor else Color.White,
                                    fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 15.sp
                                )
                            }
                        )
                    }
                }

                // Hotel Name Bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF181818))
                        .padding(vertical = 20.dp, horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = hotelDetails.name,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = Color.White
                    )
                }

                // Tab Content
                when (selectedTabIndex) {
                    0 -> GuestReviewsTab(details = hotelDetails)
                    1 -> RoomSelectionTab(details = hotelDetails)
                }
            }
        }
    }
}

@Composable
fun GuestReviewsTab(details: HotelDetails) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Ratings Area
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Ratings",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(16.dp))

            details.ratings.forEach { rating ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = rating.category,
                            fontSize = 15.sp,
                            color = Color(0xFFE0E0E0)
                        )
                        Text(
                            text = rating.score.toString(),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    LinearProgressIndicator(
                        progress = { (rating.score / 10.0).toFloat().coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = Color.White,
                        trackColor = Color(0xFF333333)
                    )
                }
            }
        }

        // Reviews Area
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Reviews",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                items(details.reviews) { review ->
                    ReviewItemCard(review = review)
                }
            }
        }
    }
}

@Composable
fun ReviewItemCard(review: Review) {
    Card(
        modifier = Modifier
            .width(280.dp)
            .heightIn(min = 180.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E1E1E)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Circle with reviewer initial
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xFF333333), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = review.username.firstOrNull()?.uppercase() ?: "",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = review.username,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color.White
                    )
                    Text(
                        text = review.country,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = review.text,
                fontSize = 14.sp,
                color = Color(0xFFD0D0D0),
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
fun RoomSelectionTab(details: HotelDetails) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Rooms",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            details.rooms.forEach { room ->
                RoomItemCard(room = room)
            }
        }
    }
}

@Composable
fun RoomItemCard(room: HotelRoom) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E1E1E)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = room.type,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Bed: ",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color(0xFFB0B0B0)
                )
                Text(
                    text = room.bedType,
                    fontSize = 14.sp,
                    color = Color.White
                )

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = "Total number of guests: ",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color(0xFFB0B0B0)
                )
                Text(
                    text = room.maxGuests.toString(),
                    fontSize = 14.sp,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Room features box
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFF2A2A2A)
            ) {
                Text(
                    text = room.features.joinToString(", "),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(10.dp),
                    color = Color(0xFFE0E0E0),
                    lineHeight = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "€ ${room.pricePerNight}",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}
