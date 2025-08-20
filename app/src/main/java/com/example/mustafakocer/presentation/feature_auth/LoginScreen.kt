package com.example.mustafakocer.presentation.feature_auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.mustafakocer.R
import com.example.mustafakocer.presentation.common.components.SplashBackground
import com.example.mustafakocer.presentation.feature_auth.components.LoginFields
import com.example.mustafakocer.presentation.feature_auth.contract.LoginEvent
import com.example.mustafakocer.presentation.feature_auth.contract.LoginUiState
import com.example.mustafakocer.presentation.feature_splash.components.SplashLogo

@Composable
fun LoginScreen(
    state: LoginUiState,
    onEvent: (LoginEvent) -> Unit,
    snackbarHostState: SnackbarHostState
) {
    Scaffold(snackbarHost = { SnackbarHost(hostState = snackbarHostState) }) { innerPadding ->
        SplashBackground {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
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
                    SplashLogo()

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
                                text = stringResource(id = R.string.login_title),
                                style = MaterialTheme.typography.headlineSmall
                            )

                            LoginFields(
                                state = state,
                                onEvent = onEvent,
                                onSubmit = { onEvent(LoginEvent.LoginClicked) }
                            )

                            Button(
                                onClick = { onEvent(LoginEvent.LoginClicked) },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !state.isLoading
                            ) {
                                // Button metinleri labelLarge => Inter
                                Text(text = stringResource(id = R.string.login_cta))
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                TextButton(onClick = { /* TODO */ }, enabled = !state.isLoading) {
                                    Text(stringResource(id = R.string.login_forgot_password))
                                }
                                TextButton(onClick = { /* TODO */ }, enabled = !state.isLoading) {
                                    Text(stringResource(id = R.string.login_create_account))
                                }
                            }
                        }
                    }

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
}
