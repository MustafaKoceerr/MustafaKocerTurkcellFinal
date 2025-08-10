package com.example.mustafakocer.presentation.feature_auth.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.mustafakocer.presentation.feature_auth.contract.LoginEvent
import com.example.mustafakocer.presentation.feature_auth.contract.LoginUiState

// Bu Composable, sadece email ve şifre alanlarını içerir.
@Composable
fun LoginFields(
    state: LoginUiState,
    onEvent: (LoginEvent) -> Unit
) {
    var isPasswordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = state.email,
        onValueChange = { onEvent(LoginEvent.EmailChanged(it)) },
        label = { Text("Username") },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Username Icon") },
        singleLine = true
    )
    Spacer(modifier = Modifier.height(8.dp))
    OutlinedTextField(
        value = state.password,
        onValueChange = { onEvent(LoginEvent.PasswordChanged(it)) },
        label = { Text("Password") },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password Icon") },
        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconToggleButton(checked = isPasswordVisible, onCheckedChange = { isPasswordVisible = it }) {
                Icon(
                    imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                    contentDescription = "Toggle password visibility"
                )
            }
        },
        singleLine = true
    )
}