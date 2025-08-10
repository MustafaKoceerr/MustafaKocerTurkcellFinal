package com.example.mustafakocer.presentation.feature_auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mustafakocer.presentation.feature_auth.components.LoginFields
import com.example.mustafakocer.presentation.feature_auth.contract.LoginEvent
import com.example.mustafakocer.presentation.feature_auth.contract.LoginUiState

@Composable
fun LoginScreen(
    state: LoginUiState,
    onEvent: (LoginEvent) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Welcome Back!", style = MaterialTheme.typography.headlineLarge)

            LoginFields(state = state, onEvent = onEvent)

            Button(
                onClick = { onEvent(LoginEvent.LoginClicked) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading // Yüklenme sırasında butonu devre dışı bırak.
            ) {
                Text("Login")
            }
        }

        // Yüklenme göstergesini, state'e göre ekranın ortasında göster.
        if (state.isLoading) {
            CircularProgressIndicator()
        }
    }
}