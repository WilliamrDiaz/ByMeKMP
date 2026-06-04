package com.byme.app.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil3.compose.AsyncImage
import com.byme.app.domain.model.User
import com.byme.app.ui.navigation.AppScreens
import com.byme.app.viewmodel.HomeScreenModel
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth

class HomeScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = getScreenModel<HomeScreenModel>()
        val uiState by viewModel.uiState.collectAsState()

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                BottomNavigationBar(
                    onNavigateToLogin = { navigator.push(AppScreens.Login) },
                    onNavigateToProfile = { navigator.push(AppScreens.UserProfile) },
                    onNavigateToMessages = { navigator.push(AppScreens.ChatList) },
                    onNavigateToCalendar = { /* Próximamente */ },
                    onNavigateToHome = { /* Ya estamos aquí */ }
                )
            }
        ) { paddingValues ->
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                // 1. Barra de Búsqueda
                item(span = { GridItemSpan(2) }) {
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = uiState.searchQuery,
                        onValueChange = { viewModel.onSearchQueryChange(it) },
                        placeholder = { Text("Buscar profesionales...") },
                        leadingIcon = { Icon(Icons.Default.Search, null) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        singleLine = true
                    )
                }

                // 2. Sección del Mapa (Placeholder)
                item(span = { GridItemSpan(2) }) {
                    /*MapboxView(
                        modifier = Modifier.fillMaxWidth().height(250.dp).clip(RoundedCornerShape(12.dp)),
                        professionals = uiState.professionals,
                        onProfessionalClick = { id -> navigator.push(AppScreens.ProfessionalDetail(id)) }
                    )*/

                    Card(
                        modifier = Modifier.fillMaxWidth().height(200.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.LocationOn, null, modifier = Modifier.size(40.dp), tint = MaterialTheme.colorScheme.primary)
                                Text("Mapa", fontWeight = FontWeight.Medium)
                                Text("Próximamente", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                            }
                        }
                    }
                }

                // 3. Título Profesionales
                item(span = { GridItemSpan(2) }) {
                    Text("Profesionales cerca de ti", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }

                // 4. Lista de Profesionales
                if (uiState.isLoading) {
                    item(span = { GridItemSpan(2) }) {
                        Box(Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                } else {
                    items(uiState.professionals) { professional ->
                        ProfessionalCard(
                            professional = professional,
                            onClick = { navigator.push(AppScreens.ProfessionalDetail(professional.id)) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProfessionalCard(professional: User, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(modifier = Modifier.size(80.dp).clip(CircleShape), color = MaterialTheme.colorScheme.primaryContainer) {
                if (professional.photoUrl.isEmpty()) {
                    Icon(Icons.Default.Person, null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.primary)
                } else {
                    AsyncImage(
                        model = professional.photoUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        onState = { state ->
                            if (state is coil3.compose.AsyncImagePainter.State.Error) {
                                println("❌ Error Coil en ${professional.name}: ${state.result.throwable.message}")
                            }
                        }
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            Text(professional.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(professional.category, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Star, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(14.dp))
                Text(" ${professional.rating}", fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun BottomNavigationBar(
    onNavigateToLogin: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToMessages: () -> Unit,
    onNavigateToCalendar: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    NavigationBar {
        NavigationBarItem(
            selected = true,
            onClick = onNavigateToHome,
            icon = { Icon(Icons.Default.Home, null) },
            label = { Text("Inicio") }
        )
        NavigationBarItem(
            selected = false,
            onClick = {
                if (Firebase.auth.currentUser != null) onNavigateToMessages()
                else onNavigateToLogin()
            },
            icon = { Icon(Icons.Default.Message, null) },
            label = { Text("Mensajes") }
        )
        NavigationBarItem(
            selected = false,
            onClick = {

                if (Firebase.auth.currentUser != null) onNavigateToCalendar()
                else onNavigateToLogin()
            },
            icon = { Icon(Icons.Default.CalendarMonth, contentDescription = null) },
            label = { Text("Calendario") }
        )
        NavigationBarItem(
            selected = false,
            onClick = {
                if (Firebase.auth.currentUser != null) onNavigateToProfile()
                else onNavigateToLogin()
            },
            icon = { Icon(Icons.Default.Person, null) },
            label = { Text("Perfil") }
        )
    }
}