package com.byme.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import cafe.adriel.voyager.navigator.Navigator
import com.byme.app.domain.repository.UserRepositoryInterface
import com.byme.app.ui.auth.SplashScreen
import com.byme.app.ui.theme.ByMeTheme
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import org.koin.compose.koinInject
import androidx.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    ByMeTheme {
        Navigator(SplashScreen())
        
        // --- prueba para conexion de bd remota ---
        val userRepository = koinInject<UserRepositoryInterface>()
        LaunchedEffect(Unit) {
            println("DEBUG: Iniciando prueba de Firebase...")
            val firestore = Firebase.firestore
            val result = userRepository.getProfessionals()

            result.onSuccess { professionals ->
                println("ÉXITO: Se encontraron ${professionals.size} profesionales")
                professionals.forEach { prof ->
                    println("Profesional: ${prof.name} - Categoría: ${prof.category}")
                }
            }.onFailure { error ->
                println("ERROR AL CARGAR: ${error.message}")
                error.printStackTrace()
            }
        }
    }
}
