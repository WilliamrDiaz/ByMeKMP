package com.byme.app.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import com.byme.app.domain.model.User
import com.byme.app.ui.components.MapboxView
import com.byme.app.ui.navigation.AppScreens
import com.byme.app.viewmodel.HomeScreenModel
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth

private val iOSBlue = Color(0xFF007AFF)
private val iOSGray = Color(0xFF8E8E93)
private val iOSLightGray = Color(0xFFF2F2F7)
private val iOSAvatarBg = Color(0xFFE5F1FF)

class HomeScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = getScreenModel<HomeScreenModel>()
        val uiState by viewModel.uiState.collectAsState()
        
        val scaffoldState = rememberBottomSheetScaffoldState(
            bottomSheetState = rememberStandardBottomSheetState(
                initialValue = SheetValue.PartiallyExpanded
            )
        )

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                BottomNavigationBar(
                    onNavigateToLogin = { navigator.push(AppScreens.Login) },
                    onNavigateToProfile = { navigator.push(AppScreens.UserProfile) },
                    onNavigateToMessages = { navigator.push(AppScreens.ChatList) },
                    onNavigateToCalendar = { navigator.push(AppScreens.Calendar) },
                    onNavigateToHome = { }
                )
            }
        ) { _ ->
            BottomSheetScaffold(
                scaffoldState = scaffoldState,
                sheetPeekHeight = 240.dp,
                sheetShape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                sheetContainerColor = Color.White,
                sheetContent = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 400.dp, max = 800.dp)
                            .padding(horizontal = 16.dp)
                    ) {
                        // Título dinámico con conteo
                        val title = if (uiState.professionals.isEmpty()) {
                            "Buscando profesionales..."
                        } else {
                            "${uiState.professionals.size} profesionales cerca de ti"
                        }

                        Text(
                            text = title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = Color.Black,
                            modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
                        )
                        
                        if (uiState.isLoading && uiState.professionals.isEmpty()) {
                            Box(Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(color = iOSBlue)
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                                contentPadding = PaddingValues(bottom = 100.dp)
                            ) {
                                items(uiState.professionals) { professional ->
                                    ProfessionalListItem(
                                        professional = professional,
                                        onClick = { navigator.push(AppScreens.ProfessionalDetail(professional.id)) }
                                    )
                                    HorizontalDivider(
                                        modifier = Modifier.padding(top = 12.dp), 
                                        thickness = 0.5.dp, 
                                        color = iOSLightGray
                                    )
                                }
                            }
                        }
                    }
                }
            ) { _ ->
                Box(modifier = Modifier.fillMaxSize()) {
                    // Mapa de fondo
                    MapboxView(
                        modifier = Modifier.fillMaxSize(),
                        professionals = uiState.professionals,
                        onProfessionalClick = { id -> navigator.push(AppScreens.ProfessionalDetail(id)) }
                    )

                    // Barra de búsqueda flotante
                    Column(
                        modifier = Modifier
                            .statusBarsPadding()
                            .padding(16.dp)
                    ) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(24.dp),
                            color = Color.White.copy(alpha = 0.95f),
                            tonalElevation = 0.dp,
                            shadowElevation = 8.dp
                        ) {
                            OutlinedTextField(
                                value = uiState.searchQuery,
                                onValueChange = { viewModel.onSearchQueryChange(it) },
                                placeholder = { Text("¿Qué servicio necesitas?", color = iOSGray) },
                                leadingIcon = { Icon(Icons.Default.Search, null, tint = iOSGray) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(24.dp),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedBorderColor = Color.Transparent,
                                    focusedBorderColor = Color.Transparent,
                                    cursorColor = iOSBlue
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProfessionalListItem(professional: User, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(80.dp).clip(RoundedCornerShape(16.dp)),
            color = iOSAvatarBg
        ) {
            if (professional.photoUrl.isEmpty()) {
                Icon(
                    imageVector = Icons.Default.Person, 
                    contentDescription = null, 
                    modifier = Modifier.size(45.dp), 
                    tint = iOSBlue
                )
            } else {
                AsyncImage(
                    model = professional.photoUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
        
        Spacer(Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "${professional.name} ${professional.lastname}",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 17.sp,
                color = Color.Black
            )
            Text(
                text = professional.category,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = iOSGray
            )
            Row(
                verticalAlignment = Alignment.CenterVertically, 
                modifier = Modifier.padding(top = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Star, 
                    contentDescription = null, 
                    tint = iOSBlue, 
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = " ${professional.rating}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = " (${professional.reviewCount} reseñas)",
                    fontSize = 13.sp,
                    color = iOSGray
                )
            }
        }
        
        IconButton(onClick = onClick) {
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Ver detalle",
                tint = iOSGray.copy(alpha = 0.5f)
            )
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
    Surface(
        shadowElevation = 8.dp,
        color = Color.White
    ) {
        NavigationBar(
            containerColor = Color.White,
            tonalElevation = 0.dp
        ) {
            val selectedColor = iOSBlue
            val unselectedColor = iOSGray

            NavigationBarItem(
                selected = true,
                onClick = onNavigateToHome,
                icon = { Icon(Icons.Default.Home, null) },
                label = { Text("Inicio") },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = selectedColor,
                    unselectedIconColor = unselectedColor,
                    selectedTextColor = selectedColor,
                    unselectedTextColor = unselectedColor,
                    indicatorColor = Color.Transparent
                )
            )
            NavigationBarItem(
                selected = false,
                onClick = {
                    if (Firebase.auth.currentUser != null) onNavigateToMessages()
                    else onNavigateToLogin()
                },
                icon = { Icon(Icons.Default.Message, null) },
                label = { Text("Mensajes") },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = selectedColor,
                    unselectedIconColor = unselectedColor,
                    selectedTextColor = selectedColor,
                    unselectedTextColor = unselectedColor,
                    indicatorColor = Color.Transparent
                )
            )
            NavigationBarItem(
                selected = false,
                onClick = {
                    if (Firebase.auth.currentUser != null) onNavigateToCalendar()
                    else onNavigateToLogin()
                },
                icon = { Icon(Icons.Default.CalendarMonth, null) },
                label = { Text("Calendario") },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = selectedColor,
                    unselectedIconColor = unselectedColor,
                    selectedTextColor = selectedColor,
                    unselectedTextColor = unselectedColor,
                    indicatorColor = Color.Transparent
                )
            )
            NavigationBarItem(
                selected = false,
                onClick = {
                    if (Firebase.auth.currentUser != null) onNavigateToProfile()
                    else onNavigateToLogin()
                },
                icon = { Icon(Icons.Default.Person, null) },
                label = { Text("Perfil") },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = selectedColor,
                    unselectedIconColor = unselectedColor,
                    selectedTextColor = selectedColor,
                    unselectedTextColor = unselectedColor,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}
