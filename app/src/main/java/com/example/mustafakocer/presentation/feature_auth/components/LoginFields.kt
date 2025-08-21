package com.example.mustafakocer.presentation.feature_auth.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.mustafakocer.R
import com.example.mustafakocer.presentation.feature_auth.contract.LoginEvent
import com.example.mustafakocer.presentation.feature_auth.contract.LoginUiState

@Composable
fun LoginFields(
    state: LoginUiState,
    onEvent: (LoginEvent) -> Unit,
    modifier: Modifier = Modifier,
    onSubmit: () -> Unit = { onEvent(LoginEvent.LoginClicked) }
) {
    val focusManager = LocalFocusManager.current
    val passwordFocus = remember { FocusRequester() }
    var isPasswordVisible by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        // USERNAME — Inter bodyLarge
        OutlinedTextField(
            value = state.email,
            onValueChange = { onEvent(LoginEvent.EmailChanged(it)) },
            modifier = modifier.fillMaxWidth(),
            enabled = !state.isLoading,
            shape = RoundedCornerShape(16.dp),
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyLarge, // <<< Inter
            label = { Text(stringResource(R.string.login_username_label), style = MaterialTheme.typography.labelLarge) },
            placeholder = { Text(stringResource(R.string.login_username_placeholder), style = MaterialTheme.typography.bodyLarge) },
            leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null) },
            isError = !state.emailError.isNullOrBlank(),
            supportingText = {
                if (!state.emailError.isNullOrBlank()) {
                    Text(
                        text = state.emailError,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error
                    )
                } else {
                    Spacer(Modifier.height(0.dp))
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { passwordFocus.requestFocus() }
            )
        )

        // PASSWORD — Inter bodyLarge
        OutlinedTextField(
            value = state.password,
            onValueChange = { onEvent(LoginEvent.PasswordChanged(it)) },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(passwordFocus),
            enabled = !state.isLoading,
            shape = RoundedCornerShape(16.dp),
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyLarge, // <<< Inter
            label = { Text(stringResource(R.string.login_password_label), style = MaterialTheme.typography.labelLarge) },
            placeholder = { Text(stringResource(R.string.login_password_placeholder), style = MaterialTheme.typography.bodyLarge) },
            leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null) },
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconToggleButton(
                    checked = isPasswordVisible,
                    onCheckedChange = { isPasswordVisible = it },
                    enabled = !state.isLoading
                ) {
                    Icon(
                        imageVector = if (isPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = stringResource(
                            if (isPasswordVisible) R.string.cd_hide_password else R.string.cd_show_password
                        )
                    )
                }
            },
            isError = !state.passwordError.isNullOrBlank(),
            supportingText = {
                if (!state.passwordError.isNullOrBlank()) {
                    Text(
                        text = state.passwordError,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error
                    )
                } else {
                    Spacer(Modifier.height(0.dp))
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus(force = true)
                    onSubmit()
                }
            )
        )
    }
}
