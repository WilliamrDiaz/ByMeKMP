package com.byme.app

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.resources.painterResource

import bymekmp.shared.generated.resources.Res
import bymekmp.shared.generated.resources.compose_multiplatform
import androidx.compose.runtime.LaunchedEffect
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import com.byme.app.data.UserRepositoryImpl
@Composable
@Preview
fun App() {
    MaterialTheme {
        /*var showContent by remember { mutableStateOf(false) }
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primaryContainer)
                .safeContentPadding()
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Button(onClick = { showContent = !showContent }) {
                Text("Click me!")
            }
            AnimatedVisibility(showContent) {
                val greeting = remember { Greeting().greet() }
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Image(painterResource(Res.drawable.compose_multiplatform), null)
                    Text("Compose: $greeting")
                }
            }
        }*/
        // --- prueba para conexion de bd remota ---
        LaunchedEffect(Unit) {
            println("DEBUG: Iniciando prueba de Firebase...")

            try {
                // Instanciamos Firebase y el Repositorio
                val firestore = Firebase.firestore
                val userRepository = UserRepositoryImpl(firestore)

                // Llamamos a la función que queremos probar
                val result = userRepository.getProfessionals()

                // Vemos el resultado en la consola
                result.onSuccess { professionals ->
                    println("ÉXITO: Se encontraron ${professionals.size} profesionales")
                    professionals.forEach { prof ->
                        println("Profesional: ${prof.name} - Categoría: ${prof.category}")
                    }
                }.onFailure { error ->
                    println("ERROR AL CARGAR: ${error.message}")
                    error.printStackTrace()
                }
            } catch (e: Exception) {
                println("EXCEPCIÓN: ${e.message}")
            }
        }
        Column {
            Text("ByMe KMP cargando...")
        }
    }
}