package com.byme.app.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.byme.app.domain.model.User

@Composable
expect fun MapboxView(
    modifier: Modifier,
    professionals: List<User>,
    onProfessionalClick: (String) -> Unit
)