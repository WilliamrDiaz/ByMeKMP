package com.byme.app.ui.professional

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil3.compose.AsyncImage
import com.byme.app.domain.model.*
import com.byme.app.ui.home.BottomNavigationBar
import com.byme.app.ui.navigation.AppScreens
import com.byme.app.viewmodel.ProfessionalDetailScreenModel
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth

data class ProfessionalDetailScreen(val professionalId: String) : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = getScreenModel<ProfessionalDetailScreenModel>()
        val uiState by viewModel.uiState.collectAsState()

        LaunchedEffect(professionalId) {
            viewModel.loadProfessional(professionalId)
        }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = { Text("Detalle del Profesional") },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                        }
                    },
                    actions = {
                        IconButton(onClick = { }) {
                            Icon(Icons.Default.MoreVert, null)
                        }
                    }
                )
            },
            bottomBar = {
                BottomNavigationBar(
                    onNavigateToLogin = { navigator.push(AppScreens.Login) },
                    onNavigateToProfile = { navigator.push(AppScreens.UserProfile) },
                    onNavigateToMessages = { navigator.push(AppScreens.ChatList) },
                    onNavigateToCalendar = { },
                    onNavigateToHome = { navigator.popUntilRoot() }
                )
            }
        ) { paddingValues ->
            when {
                uiState.isLoading -> {
                    Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator() }
                }
                uiState.professional != null -> {
                    ProfessionalDetailContent(
                        professional = uiState.professional!!,
                        reviews = uiState.reviews,
                        services = uiState.services,
                        schedules = uiState.schedules,
                        selectedTab = uiState.selectedTab,
                        onTabSelected = { viewModel.onTabSelected(it) },
                        onContactClick = {
                            val currentUser = Firebase.auth.currentUser
                            if (currentUser != null) {
                                val prof = uiState.professional!!
                                val chatId = "${currentUser.uid}_${prof.id}"
                                navigator.push(AppScreens.ChatDetail(chatId, "${prof.name} ${prof.lastname}"))
                            } else {
                                navigator.push(AppScreens.Login)
                            }
                        },
                        modifier = Modifier.padding(paddingValues)
                    )
                }
                else -> {
                    Box(Modifier.fillMaxSize(), Alignment.Center) {
                        Text(uiState.errorMessage ?: "No encontrado")
                    }
                }
            }
        }
    }
}

@Composable
fun ProfessionalDetailContent(
    professional: User,
    reviews: List<Review>,
    services: List<Service>,
    schedules: List<Schedule>,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    onContactClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(16.dp)) {

        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(modifier = Modifier.size(110.dp).clip(RoundedCornerShape(16.dp)), color = MaterialTheme.colorScheme.primaryContainer) {
                if (professional.photoUrl.isEmpty()) {
                    Icon(Icons.Default.Person, null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.primary)
                } else {
                    AsyncImage(model = professional.photoUrl, contentDescription = null, modifier = Modifier.fillMaxSize())
                }
            }
            Spacer(Modifier.width(16.dp))
            Column {
                Text("${professional.name} ${professional.lastname}", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(professional.category, fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("${professional.rating}", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    Icon(Icons.Default.Star, null, tint = Color(0xFFFFC107), modifier = Modifier.size(16.dp))
                    Text(" (${professional.reviewCount} reseñas)", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                }
                Spacer(Modifier.height(8.dp))
                Button(onClick = onContactClick, shape = RoundedCornerShape(24.dp)) {
                    Text("Contactar", fontWeight = FontWeight.Medium)
                }
            }
        }

        Spacer(Modifier.height(20.dp))
        Text("Sobre mí", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
        Text(professional.description.ifEmpty { "Sin descripción disponible." }, fontSize = 14.sp, lineHeight = 20.sp)

        Spacer(Modifier.height(20.dp))

        //  iconos de pestañas
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            val tabs = listOf("Servicios" to Icons.Default.Work, "Horarios" to Icons.Default.Schedule, "Reseñas" to Icons.Default.Star)
            tabs.forEachIndexed { index, (label, icon) ->
                FilterChip(
                    selected = selectedTab == index,
                    onClick = { onTabSelected(index) },
                    label = { Text(label) },
                    leadingIcon = { Icon(icon, null, modifier = Modifier.size(16.dp)) }
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // Contenido de las pestañas
        when (selectedTab) {
            0 -> ServicesSection(services)
            1 -> SchedulesSection(schedules)
            2 -> ReviewsSection(reviews)
        }
    }
}

@Composable
fun ServicesSection(services: List<Service>) {
    services.forEach { service ->
        Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), shape = RoundedCornerShape(12.dp)) {
            Column(Modifier.padding(12.dp)) {
                Text(service.name, fontWeight = FontWeight.Bold)
                Text(service.description, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
            }
        }
    }
}

@Composable
fun SchedulesSection(schedules: List<Schedule>) {
    schedules.forEach { schedule ->
        Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), shape = RoundedCornerShape(12.dp)) {
            Row(Modifier.padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(schedule.day, fontWeight = FontWeight.Medium)
                Text(schedule.hours, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
fun ReviewsSection(reviews: List<Review>) {
    reviews.forEach { review ->
        Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), shape = RoundedCornerShape(12.dp)) {
            Column(Modifier.padding(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(review.userName, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                    repeat(review.rating.toInt()) { Icon(Icons.Default.Star, null, tint = Color(0xFFFFC107), modifier = Modifier.size(14.dp)) }
                }
                Text(review.comment, fontSize = 13.sp)
            }
        }
    }
}