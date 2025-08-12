package com.example.mustafakocer.presentation.feature_auth.components


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    emailError: String? = null,
    passwordError: String? = null,
    onSubmit: () -> Unit = { onEvent(LoginEvent.LoginClicked) }
) {
    val focusManager = LocalFocusManager.current
    val passwordFocus = remember { FocusRequester() }
    var isPasswordVisible by remember { mutableStateOf(false) }

    // DEĞİŞTİ: İki alanı bir Column içine alarak aralarındaki boşluğu
    // Spacer yerine Column'un kendi `spacedBy` özelliği ile yöneteceğiz.
    // Bu, daha temiz bir yaklaşımdır.
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp) // Boşluğu buradan kontrol et.
    ) {
        // E-POSTA
        OutlinedTextField(
            value = state.email,
            onValueChange = { onEvent(LoginEvent.EmailChanged(it)) },
            modifier = modifier.fillMaxWidth(),
            enabled = !state.isLoading,
            shape = RoundedCornerShape(16.dp),
            singleLine = true,
            label = { Text(stringResource(R.string.login_username_label)) },
            placeholder = { Text(stringResource(R.string.login_username_placeholder)) },
            leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null) },
            isError = !emailError.isNullOrBlank(),
            supportingText = {
                if (!emailError.isNullOrBlank()) {
                    Text(text = emailError, color = MaterialTheme.colorScheme.error)
                } else {
                    // Hata olmadığında, layout'un zıplamasını önlemek için
                    // sabit bir boşluk bırakıyoruz. Bu, TextField'ın yüksekliğini sabit tutar.
                    Spacer(Modifier.height(0.dp)) // Veya çok küçük bir değer
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

        // ŞİFRE
        OutlinedTextField(
            value = state.password,
            onValueChange = { onEvent(LoginEvent.PasswordChanged(it)) },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(passwordFocus),
            enabled = !state.isLoading,
            shape = RoundedCornerShape(16.dp),
            singleLine = true,
            label = { Text(stringResource(R.string.login_password_label)) },
            placeholder = { Text(stringResource(R.string.login_password_placeholder)) },
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
            isError = !passwordError.isNullOrBlank(),
            supportingText = {
                if (!passwordError.isNullOrBlank()) {
                    Text(text = passwordError, color = MaterialTheme.colorScheme.error)
                } else {
                    // Aynı şekilde, hata olmadığında sabit bir boşluk bırakıyoruz.
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