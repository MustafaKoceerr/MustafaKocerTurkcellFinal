package com.example.mustafakocer.ui.login


import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mustafakocer.R
import com.example.mustafakocer.ui.login.ui.theme.LightBlue
import com.example.mustafakocer.ui.login.viewmodel.LoginViewModel

@Composable
//loginViewModel: LoginViewModel = viewModel(),loginState: Resource<Any>
fun LoginScreen(viewModel: LoginViewModel = viewModel()) {
    var username by remember { mutableStateOf("") }
    var usernameError by rememberSaveable { mutableStateOf<String?>(null) }
    var passwordError by rememberSaveable { mutableStateOf<String?>(null) }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by rememberSaveable { mutableStateOf(false) }
    var isFocusedUsername by remember { mutableStateOf(false) }
    var isFocusedPassword by remember { mutableStateOf(false) }

    var isLoading by remember { mutableStateOf(false) }
    // Observe loading state from ViewModel or any other source
    LoadingIndicator(isLoading = isLoading)
    // loading'deyken loading gösterecek

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Image(
            painter = painterResource(id = R.drawable.login_background), // Replace with your background image
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 10.dp
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                colors = CardDefaults.cardColors(
                    containerColor = LightBlue,
                )
            ) {
                Column(
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF1976D2), // Dark blue
                                    Color(0xFF64B5F6)  // Light blue
                                )
                            )
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(modifier = Modifier.padding(16.dp)) { // Add padding within the Box
                        // Content of your Column goes here
                    }
                    Text(
                        text = "Login",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.Black
                        ),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.ic_shopping_cart),
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(80.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text(text = "Username") },
                        modifier = Modifier
                            .padding(horizontal = 12.dp)
                            .fillMaxWidth()
                            .onFocusChanged { focusState ->
                                isFocusedUsername = focusState.isFocused
                            },
                        shape = RoundedCornerShape(8.dp),
                        leadingIcon = {
                            androidx.compose.material.Icon(
                                Icons.Default.Person,
                                contentDescription = "Email Icon",
                                tint = if (isFocusedUsername) Color.DarkGray else Color.LightGray
                            )
                        },
                        isError = usernameError != null,
                        singleLine = true
                    )
                    usernameError?.let {
                        Text(
                            text = it,
                            color = Color.Red,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))


                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text(text = "Password") },
                        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp)

                            .onFocusChanged { focusState ->
                                isFocusedPassword = focusState.isFocused
                            },
                        shape = RoundedCornerShape(8.dp),
                        leadingIcon = {
                            androidx.compose.material.Icon(
                                Icons.Default.Lock,
                                contentDescription = "Password Icon",
                                tint = if (isFocusedPassword) Color.DarkGray else Color.LightGray
                            )
                        },
                        trailingIcon = {
                            androidx.compose.material3.IconToggleButton(
                                checked = isPasswordVisible,
                                onCheckedChange = { isPasswordVisible = it },
                                modifier = Modifier.padding(end = 8.dp)
                            ) {
                                androidx.compose.material.Icon(
                                    imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (isPasswordVisible) "Hide password" else "Show password",
                                    tint = Color.Black,
                                )
                            }
                        },
                        isError = passwordError != null,
                        singleLine = true
                    )
                    passwordError?.let {
                        Text(
                            text = it,
                            color = Color.Red,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            // Simulate login action
                            usernameError = if (username.isBlank()) {
                                "Email cannot be empty"
                            } else {
                                null
                            }
                            passwordError = if (password.isBlank()) {
                                "Password cannot be empty"
                            } else {
                                null
                            }
                            if (username.isNotBlank() && password.isNotBlank()) {
                                // login kontrol işlemi yapılacak
                                // todo retrofit işlemi yapılacak
                                viewModel.loginUser(username, password)
                                Log.d("viewmodel","deneme")
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .padding(horizontal = 12.dp)
                            .clip(RoundedCornerShape(8.dp))
                    ) {
                        Text(text = "Login", fontSize = 18.sp)
                    }


                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Forgot Password?",
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.primary,
                        textDecoration = TextDecoration.Underline,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    )


                    /*
                          when (val resource = loginState){
                              is Resource.Loading -> {
                                  isLoading = true // Set loading state before making API call
                              }
                              is Resource.Failure -> {
                                  Snackbar(
                                      content = { Text(text = "Hata ${resource.errorCode} ${resource.errorBody}") },
                                      action = {
                                          // Opsiyonel: Hata mesajını kapatmak için bir eylem butonu ekleyebilirsiniz.
                                          // actionLabel = "Kapat",
                                          // onClick = { snackbarState.dismiss() }
                                      }
                                  )
                              }

                              is Resource.Success -> {
                                  // todo diğer aktiviteye geçiş yap
                              }
                          }
                     */


                }
            }
        }
    }
}

