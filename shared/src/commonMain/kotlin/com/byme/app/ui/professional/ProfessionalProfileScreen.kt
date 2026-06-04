package com.byme.app.ui.professional

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
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
import com.byme.app.ui.home.BottomNavigationBar
import com.byme.app.ui.navigation.AppScreens
import com.byme.app.viewmodel.ProfessionalProfileScreenModel
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.launch

class ProfessionalProfileScreen : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val scope = rememberCoroutineScope()
        val viewModel = getScreenModel<ProfessionalProfileScreenModel>()
        val uiState by viewModel.uiState.collectAsState()

        var showAddServiceDialog by remember { mutableStateOf(false) }
        var showAddScheduleDialog by remember { mutableStateOf(false) }
        var serviceName by remember { mutableStateOf("") }
        var serviceDescription by remember { mutableStateOf("") }

        LaunchedEffect(uiState.isSuccess) {
            if (uiState.isSuccess) {
                viewModel.resetSuccess()
            }
        }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = { Text("Perfil Profesional") },
                    actions = {
                        var showMenu by remember { mutableStateOf(false) }
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = null)
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Acerca de ByMe") },
                                onClick = {
                                    showMenu = false
                                    navigator.push(AppScreens.About)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Cerrar sesión") },
                                onClick = {
                                    scope.launch {
                                        try {
                                            Firebase.auth.signOut()
                                            showMenu = false
                                            navigator.popUntilRoot()
                                        }catch (e: Exception) {
                                            e.printStackTrace()
                                            showMenu = false
                                        }
                                    }

                                }
                            )
                        }
                    }
                )
            },
            bottomBar = {
                BottomNavigationBar(
                    onNavigateToLogin = { navigator.push(AppScreens.Login) },
                    onNavigateToProfile = { },
                    onNavigateToMessages = { navigator.push(AppScreens.ChatList) },
                    onNavigateToCalendar = { },
                    onNavigateToHome = { navigator.popUntilRoot() }
                )
            }
        ) { paddingValues ->
            if (uiState.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Foto de perfil
                    Box(contentAlignment = Alignment.BottomEnd) {
                        Surface(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    modifier = Modifier.size(60.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        Surface(
                            modifier = Modifier.size(28.dp),
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surface,
                            shadowElevation = 2.dp
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Nombre
                    OutlinedTextField(
                        value = uiState.name,
                        onValueChange = { viewModel.onNameChange(it) },
                        placeholder = { Text("Nombre") },
                        trailingIcon = {
                            Icon(Icons.Default.Edit, contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Apellido
                    OutlinedTextField(
                        value = uiState.lastname,
                        onValueChange = { viewModel.onLastnameChange(it) },
                        placeholder = { Text("Apellido") },
                        trailingIcon = {
                            Icon(Icons.Default.Edit, contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Descripción
                    OutlinedTextField(
                        value = uiState.description,
                        onValueChange = { viewModel.onDescriptionChange(it) },
                        placeholder = { Text("Descripción de tu perfil") },
                        trailingIcon = {
                            Icon(Icons.Default.Edit, contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        shape = RoundedCornerShape(12.dp),
                        maxLines = 5
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Servicios
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Servicios",
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        IconButton(onClick = { showAddServiceDialog = true }) {
                            Icon(Icons.Default.Add, contentDescription = "Agregar servicio",
                                tint = MaterialTheme.colorScheme.primary)
                        }
                    }

                    if (uiState.services.isEmpty()) {
                        Text(
                            text = "No hay servicios registrados",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    } else {
                        uiState.services.forEach { service ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = service.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text(text = service.description, fontSize = 13.sp,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                                    }
                                    IconButton(onClick = { viewModel.removeService(service) }) {
                                        Icon(Icons.Default.Close, contentDescription = "Eliminar",
                                            tint = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Horarios
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Horarios",
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        IconButton(onClick = { showAddScheduleDialog = true }) {
                            Icon(Icons.Default.Add, contentDescription = "Agregar horario",
                                tint = MaterialTheme.colorScheme.primary)
                        }
                    }

                    if (uiState.schedules.isEmpty()) {
                        Text(
                            text = "No hay horarios agregados",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    } else {
                        uiState.schedules.forEach { schedule ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = schedule.day, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        schedule.hours.split("\n").forEach { jornada ->
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(text = "•", fontSize = 13.sp,
                                                    color = MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier.padding(end = 4.dp))
                                                Text(text = jornada, fontSize = 13.sp,
                                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                                            }
                                        }
                                    }
                                    IconButton(onClick = { viewModel.removeSchedule(schedule) }) {
                                        Icon(Icons.Default.Close, contentDescription = "Eliminar",
                                            tint = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }

                    // Botones
                    if (uiState.hasChanges) {
                        Spacer(modifier = Modifier.height(32.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = { viewModel.saveProfile() },
                                modifier = Modifier.weight(1f).height(50.dp),
                                shape = RoundedCornerShape(24.dp),
                                enabled = !uiState.isSaving
                            ) {
                                if (uiState.isSaving) {
                                    CircularProgressIndicator(
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                } else {
                                    Text("Guardar", fontSize = 16.sp)
                                }
                            }
                            OutlinedButton(
                                onClick = { viewModel.loadProfile() },
                                modifier = Modifier.weight(1f).height(50.dp),
                                shape = RoundedCornerShape(24.dp)
                            ) {
                                Text("Cancelar", fontSize = 16.sp)
                            }
                        }
                    }

                    // Mensaje de Error
                    uiState.errorMessage?.let { error ->
                        Spacer(modifier = Modifier.height(12.dp))
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = error,
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }

        // Diálogo agregar servicio
        if (showAddServiceDialog) {
            AlertDialog(
                onDismissRequest = { showAddServiceDialog = false },
                title = { Text("Agregar servicio") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = serviceName,
                            onValueChange = { serviceName = it },
                            placeholder = { Text("Nombre del servicio") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = serviceDescription,
                            onValueChange = { serviceDescription = it },
                            placeholder = { Text("Descripción del servicio") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            maxLines = 3
                        )
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        if (serviceName.isNotEmpty()) {
                            viewModel.addService(serviceName, serviceDescription)
                            serviceName = "";
                            serviceDescription = "";
                            showAddServiceDialog = false
                        }
                    }) { Text("Agregar") }
                },
                dismissButton = {
                    OutlinedButton(onClick = { showAddServiceDialog = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }

        if (showAddScheduleDialog) {
            var dayExpanded by remember { mutableStateOf(false) }
            var startTimeExpanded by remember { mutableStateOf(false) }
            var endTimeExpanded by remember { mutableStateOf(false) }

            AlertDialog(
                onDismissRequest = { showAddScheduleDialog = false },
                title = { Text("Agregar horario") },
                text = {
                    Column {
                        ExposedDropdownMenuBox(
                            expanded = dayExpanded,
                            onExpandedChange = { dayExpanded = it }
                        ) {
                            OutlinedTextField(
                                value = uiState.selectedDay,
                                onValueChange = { },
                                readOnly = true,
                                placeholder = { Text("Selecciona el día") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(dayExpanded) },
                                modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable),
                                shape = RoundedCornerShape(12.dp)
                            )
                            ExposedDropdownMenu(expanded = dayExpanded, onDismissRequest = { dayExpanded = false }) {
                                viewModel.dayOptions.forEach { day ->
                                    DropdownMenuItem(
                                        text = { Text(day) },
                                        onClick = {
                                            viewModel.onDaySelected(day);
                                            dayExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            ExposedDropdownMenuBox(
                                expanded = startTimeExpanded,
                                onExpandedChange = { startTimeExpanded = it },
                                modifier = Modifier.weight(1f)
                            ) {
                                OutlinedTextField(
                                    value = uiState.selectedStartTime,
                                    onValueChange = { },
                                    readOnly = true,
                                    placeholder = { Text("Inicio") },
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = startTimeExpanded)
                                    },
                                    modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                ExposedDropdownMenu(
                                    expanded = startTimeExpanded,
                                    onDismissRequest = { startTimeExpanded = false }
                                ) {
                                    viewModel.timeOptions.forEach { time ->
                                        DropdownMenuItem(
                                            text = { Text(time) },
                                            onClick = {
                                                viewModel.onStartTimeSelected(time);
                                                startTimeExpanded = false
                                            }
                                        )
                                    }
                                }
                            }

                            ExposedDropdownMenuBox(
                                expanded = endTimeExpanded,
                                onExpandedChange = { endTimeExpanded = it },
                                modifier = Modifier.weight(1f)
                            ) {
                                OutlinedTextField(
                                    value = uiState.selectedEndTime,
                                    onValueChange = { },
                                    readOnly = true,
                                    placeholder = { Text("Fin") },
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = endTimeExpanded)
                                    },
                                    modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                ExposedDropdownMenu(
                                    expanded = endTimeExpanded,
                                    onDismissRequest = { endTimeExpanded = false }
                                ) {
                                    viewModel.timeOptions.forEach { time ->
                                        DropdownMenuItem(
                                            text = { Text(time) },
                                            onClick = {
                                                viewModel.onEndTimeSelected(time);
                                                endTimeExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        viewModel.addSchedule();
                        showAddScheduleDialog = false
                    }) { Text("Agregar") }
                },
                dismissButton = {
                    OutlinedButton(onClick = { showAddScheduleDialog = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}