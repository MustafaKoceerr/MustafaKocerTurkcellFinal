package com.example.mustafakocer.presentation.feature_auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.mustafakocer.presentation.feature_auth.components.*
import com.example.mustafakocer.presentation.feature_auth.contract.LoginEvent
import com.example.mustafakocer.presentation.feature_auth.contract.LoginUiState
import com.example.mustafakocer.presentation.common.components.SplashBackground
import com.example.mustafakocer.R
import com.example.mustafakocer.presentation.feature_splash.components.SplashLogo

@Composable
fun LoginScreen(
    state: LoginUiState,
    onEvent: (LoginEvent) -> Unit,
) {

    SplashBackground { // aynı degrade – marka tutarlılığı
        Box(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .imePadding()
                .padding(horizontal = 20.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Marka
                SplashLogo()

                // Form kartı
                Surface(
                    tonalElevation = 3.dp,
                    shape = MaterialTheme.shapes.extraLarge,
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.login_title), // "Welcome back"
                            style = MaterialTheme.typography.headlineSmall
                        )

                        LoginFields(
                            state = state,
                            onEvent = onEvent,
                            onSubmit = { onEvent(LoginEvent.LoginClicked) })

                        Button(
                            onClick = { onEvent(LoginEvent.LoginClicked) },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !state.isLoading
                        ) { Text(text = stringResource(id = R.string.login_cta)) }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            TextButton(onClick = { /*TODO: */ }, enabled = !state.isLoading) {
                                Text(stringResource(id = R.string.login_forgot_password))
                            }
                            TextButton(onClick = { /*TODO: */ }, enabled = !state.isLoading) {
                                Text(stringResource(id = R.string.login_create_account))
                            }
                        }
                    }
                }

                // Alt bilgi (opsiyonel)
                Text(
                    text = stringResource(id = R.string.login_terms_hint),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }

            if (state.isLoading) {
                CircularProgressIndicator()
            }
        }
    }
}