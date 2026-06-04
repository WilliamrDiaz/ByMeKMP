package com.byme.app.ui.navigation

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import com.byme.app.ui.auth.LoginScreen
import com.byme.app.ui.auth.RegisterScreen
import com.byme.app.ui.auth.SplashScreen
import com.byme.app.ui.home.HomeScreen
import com.byme.app.ui.professional.OfferServiceScreen
import com.byme.app.ui.professional.ProfessionalDetailScreen
import com.byme.app.ui.professional.ProfessionalProfileScreen

sealed class AppScreens : Screen {

    // Ruta para splash
    object Splash : Screen {
        @Composable
        override fun Content() {
            SplashScreen().Content()
        }
    }

    // Ruta para login
    object Login : Screen {
        @Composable
        override fun Content() {
            LoginScreen().Content()
        }
    }

    // Ruta para registro
    object Register : Screen {
        @Composable
        override fun Content() {
            RegisterScreen().Content()
        }
    }

    // Ruta para home
    object Home : Screen {
        @Composable
        override fun Content() {
            HomeScreen().Content()
        }
    }

    // Ruta para detalle del Profesional
    data class ProfessionalDetail(val professionalId: String) : Screen {
        @Composable
        override fun Content() {
            ProfessionalDetailScreen(professionalId).Content()
        }
    }

    // Ruta para lista de Chats
    object ChatList : Screen {
        @Composable
        override fun Content() {
            // ChatListScreen().Content()
        }
    }

    // Ruta para detalle de Chat
    data class ChatDetail(val chatId: String, val professionalName: String) : Screen {
        @Composable
        override fun Content() {
            // ChatDetailScreen(chatId, professionalName).Content()
        }
    }

    // Ruta para perfil de Usuario
    object UserProfile : Screen {
        @Composable
        override fun Content() {
            // ProfileScreen().Content()
        }
    }

    // Ruta para perfil del Profesional
    object ProfessionalProfile : Screen {
        @Composable
        override fun Content() {
            ProfessionalProfileScreen().Content()
        }
    }

    // Ruta para ofrecer Servicio
    object OfferService : Screen {
        @Composable
        override fun Content() {
            OfferServiceScreen().Content()
        }
    }

    // Ruta para Acerca de / Créditos
    object About : Screen {
        @Composable
        override fun Content() {
            // AboutScreen().Content()
        }
    }
}