package com.byme.app.ui.professional

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.byme.app.viewmodel.OfferServiceScreenModel

class OfferServiceScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = getScreenModel<OfferServiceScreenModel>()
        val uiState by viewModel.uiState.collectAsState()

        var showAddServiceDialog by remember { mutableStateOf(false) }
        var showAddScheduleDialog by remember { mutableStateOf(false) }
        var serviceName by remember { mutableStateOf("") }
        var serviceDescription by remember { mutableStateOf("") }
        var categoryExpanded by remember { mutableStateOf(false) }
        var experienceExpanded by remember { mutableStateOf(false) }

        val experienceOptions = listOf("1 a 4 años", "4 a 8 años", "8 o más años")

        LaunchedEffect(uiState.isSuccess) {
            if (uiState.isSuccess) {
                viewModel.resetSuccess()
                navigator.pop()
            }
        }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = { Text("Prestar Servicio") },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                        }
                    },
                    actions = {
                        IconButton(onClick = { }) {
                            Icon(Icons.Default.MoreVert, contentDescription = null)
                        }
                    }
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
                        .padding(24.dp)
                ) {
                    // Categoría
                    Text(
                        "¿Qué servicio quieres brindar?",
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    ExposedDropdownMenuBox(
                        expanded = categoryExpanded,
                        onExpandedChange = { categoryExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = uiState.selectedCategory,
                            onValueChange = { },
                            readOnly = true,
                            placeholder = { Text("Selecciona una categoría") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                            shape = RoundedCornerShape(12.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = categoryExpanded,
                            onDismissRequest = { categoryExpanded = false }
                        ) {
                            uiState.categories.forEach { category ->
                                DropdownMenuItem(
                                    text = { Text(category.name) },
                                    onClick = {
                                        viewModel.onCategorySelected(category.name)
                                        categoryExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Experiencia
                    Text(
                        "¿Cuánta experiencia tienes?",
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp, color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    ExposedDropdownMenuBox(
                        expanded = experienceExpanded,
                        onExpandedChange = { experienceExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = uiState.selectedExperience,
                            onValueChange = { },
                            readOnly = true,
                            placeholder = { Text("Selecciona tu experiencia") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = experienceExpanded)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                            shape = RoundedCornerShape(12.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = experienceExpanded,
                            onDismissRequest = { experienceExpanded = false }
                        ) {
                            experienceOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        viewModel.onExperienceSelected(option)
                                        experienceExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Descripción
                    Text(
                        "Descripción de tu perfil",
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp, color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = uiState.description,
                        onValueChange = { viewModel.onDescriptionChange(it) },
                        placeholder = { Text("Cuéntanos sobre ti y tus servicios...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        shape = RoundedCornerShape(12.dp),
                        maxLines = 5
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Servicios
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Servicios", fontWeight = FontWeight.Medium, fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
                        IconButton(onClick = { showAddServiceDialog = true }) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Agregar Servicio",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    if (uiState.services.isEmpty()) {
                        Text(
                            text = "No hay servicios agregados aún",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    } else {
                        uiState.services.forEachIndexed { index, service ->
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
                                        Text(
                                            text = service.name,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                        Text(
                                            text = service.description,
                                            fontSize = 13.sp,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                        )
                                    }
                                    IconButton(onClick = { viewModel.removeService(index) }) {
                                        Icon(
                                            Icons.Default.Close,
                                            contentDescription = "Eliminar",
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Horarios
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "Horarios",
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        IconButton(onClick = { showAddScheduleDialog = true }) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Agregar horario",
                                tint = MaterialTheme.colorScheme.primary)
                        }
                    }

                    if (uiState.schedules.isEmpty()) {
                        Text(
                            text = "No hay horarios agregados aún",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    } else {
                        uiState.schedules.forEachIndexed { index, schedule ->
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
                                        Text(
                                            text = schedule.day,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                        schedule.hours.split("\n").forEach { jornada ->
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = "•",
                                                    fontSize = 13.sp,
                                                    color = MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier.padding(end = 4.dp)
                                                )
                                                Text(
                                                    text = jornada,
                                                    fontSize = 13.sp,
                                                    color = MaterialTheme.colorScheme.onSurface.copy(
                                                        alpha = 0.7f
                                                    )
                                                )
                                            }
                                        }
                                    }
                                    IconButton(onClick = { viewModel.removeSchedule(index) }) {
                                        Icon(
                                            Icons.Default.Close,
                                            contentDescription = "Eliminar",
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    // Título profesional
                    Text(
                        text = "¿Tienes un título en tu profesión",
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.AttachFile,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Subir archivo",
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "Próximamente",
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Botones
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = { viewModel.saveProfile() },
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp),
                            shape = RoundedCornerShape(24.dp),
                            enabled = !uiState.isSaving
                        ) {
                            if (uiState.isSaving)
                                CircularProgressIndicator(
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(24.dp))
                            else Text("Guardar", fontSize = 16.sp)
                        }
                        OutlinedButton(
                            onClick = { navigator.pop() },
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp),
                            shape = RoundedCornerShape(24.dp)
                        ) {
                            Text("Cancelar", fontSize = 16.sp)
                        }
                    }

                    // Error
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
                title = { Text("Agregar Servicio") },
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
                    Button(
                        onClick = {
                            if (serviceName.isNotEmpty()) {
                                viewModel.addService(serviceName, serviceDescription)
                                serviceName = ""
                                serviceDescription = ""
                                showAddServiceDialog = false
                            }
                        }
                    ) {
                        Text("Agregar")
                    }
                },
                dismissButton = {
                    OutlinedButton(onClick = { showAddServiceDialog = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }

        // Diálogo agregar horario
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
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = dayExpanded)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                                shape = RoundedCornerShape(12.dp)
                            )
                            ExposedDropdownMenu(
                                expanded = dayExpanded,
                                onDismissRequest = { dayExpanded = false }
                            ) {
                                viewModel.dayOptions.forEach { day ->
                                    DropdownMenuItem(
                                        text = { Text(day) },
                                        onClick = {
                                            viewModel.onDaySelected(day)
                                            dayExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
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
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor(),
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
                                                viewModel.onStartTimeSelected(time)
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
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor(),
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
                                                viewModel.onEndTimeSelected(time)
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
                    Button(
                        onClick = {
                            viewModel.addSchedule()
                            showAddScheduleDialog = false
                        }
                    ) {
                        Text("Agregar")
                    }
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