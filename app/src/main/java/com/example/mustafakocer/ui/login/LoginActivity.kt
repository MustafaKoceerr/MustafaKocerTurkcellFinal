package com.example.mustafakocer.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mustafakocer.data.PreferenceKeys
import com.example.mustafakocer.data.model.LoginResponse
import com.example.mustafakocer.data.model.Resource
import com.example.mustafakocer.ui.home.HomeActivity
import com.example.mustafakocer.ui.login.ui.theme.LoginScreenComposeTheme
import com.example.mustafakocer.ui.login.viewmodel.LoginViewModel
import com.example.mustafakocer.util.visibleProgressBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LoginScreenComposeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    val loginViewModel: LoginViewModel = viewModel()
                    val loginState = loginViewModel.loginState.collectAsState().value
                    //val loginState by loginViewModel.loginState.collectAsState()

                    LoginScreen(loginViewModel) // -> bu güzel

                    when(loginState){

                        is Resource.Failure ->{
                            LoadingIndicator(false)
                           Log.d("login","Login basarisiz")
                        }
                        is Resource.Loading -> {
                            LoadingIndicator(true)
                        }
                        is Resource.Waiting ->{
                            LoadingIndicator(false)
                            Log.d("deneme","${PreferenceKeys.USER_ID},  ve ${PreferenceKeys.KEY_AUTH}")
                        }

                        is Resource.Success -> {
                            LoadingIndicator(false)
                            // todo datastore'a kaydet
                         /*
                            lifecycleScope.launch {

                            }
                          */

                            val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }


                }
            }
        }

    }

}


@Composable
fun LoadingIndicator(isLoading: Boolean) {
    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color.White)
        }
    }
}



