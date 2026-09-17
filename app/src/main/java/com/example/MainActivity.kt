package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.auth.AuthState
import com.example.auth.AuthViewModel
import com.example.ui.screens.MainAppScreen
import com.example.ui.screens.OtpVerificationScreen
import com.example.ui.screens.PhoneLoginScreen
import com.example.ui.theme.BharatHeritageTheme
import com.example.ui.theme.DarkBackground

class MainActivity : ComponentActivity() {
  private val authViewModel: AuthViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      BharatHeritageTheme {
        Surface(
          modifier = Modifier.fillMaxSize(),
          color = DarkBackground
        ) {
          val authState by authViewModel.authState.collectAsStateWithLifecycle()

          when (val state = authState) {
            is AuthState.Loading, is AuthState.Unauthenticated -> {
              PhoneLoginScreen(
                authViewModel = authViewModel,
                authState = state
              )
            }

            is AuthState.CodeSent -> {
              OtpVerificationScreen(
                authViewModel = authViewModel,
                codeSentState = state
              )
            }

            is AuthState.Authenticated -> {
              MainAppScreen(
                authViewModel = authViewModel,
                authenticatedState = state
              )
            }
          }
        }
      }
    }
  }
}

