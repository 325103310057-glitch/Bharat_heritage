package com.example.ui.screens

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.auth.AuthState
import com.example.auth.AuthViewModel
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.DeepSaffron
import com.example.ui.theme.HeritageGold
import com.example.ui.theme.RoyalNavy
import com.example.ui.theme.RoyalSaffron
import com.example.ui.theme.SandstoneIvory

@Composable
fun PhoneLoginScreen(
  authViewModel: AuthViewModel,
  authState: AuthState,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val activity = context as? Activity
  val focusManager = LocalFocusManager.current

  var phoneNumber by remember { mutableStateOf("") }
  val scrollState = rememberScrollState()

  val isLoading = authState is AuthState.Loading
  val errorMessage = when (authState) {
    is AuthState.Unauthenticated -> authState.errorMessage
    else -> null
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(
        brush = Brush.verticalGradient(
          colors = listOf(
            RoyalNavy,
            DeepNavy,
            Color(0xFF070D18)
          )
        )
      )
      .imePadding()
      .verticalScroll(scrollState),
    contentAlignment = Alignment.Center
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .widthIn(max = 480.dp)
        .padding(horizontal = 24.dp, vertical = 32.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      // Emblem Header
      Box(
        modifier = Modifier
          .size(80.dp)
          .clip(CircleShape)
          .background(
            brush = Brush.linearGradient(
              colors = listOf(RoyalSaffron, DeepSaffron)
            )
          )
          .border(2.dp, HeritageGold, CircleShape),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.Security,
          contentDescription = "Bharat Emblem",
          tint = Color.White,
          modifier = Modifier.size(42.dp)
        )
      }

      Spacer(modifier = Modifier.height(16.dp))

      Text(
        text = "BHARAT HERITAGE",
        color = HeritageGold,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 3.sp,
        textAlign = TextAlign.Center
      )

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = "Phone Authentication",
        color = SandstoneIvory,
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily.Serif,
        textAlign = TextAlign.Center
      )

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = "Enter your Indian mobile number to receive a real SMS verification code directly from Firebase.",
        color = Color(0xFFB0BEC5),
        fontSize = 14.sp,
        textAlign = TextAlign.Center,
        lineHeight = 20.sp,
        modifier = Modifier.padding(horizontal = 8.dp)
      )

      Spacer(modifier = Modifier.height(28.dp))

      // Input Card
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("phone_login_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
          containerColor = Color(0xFF131D2E)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
        ) {
          Text(
            text = "Mobile Number",
            color = SandstoneIvory,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
          )

          Spacer(modifier = Modifier.height(8.dp))

          OutlinedTextField(
            value = phoneNumber,
            onValueChange = { input ->
              val clean = input.filter { it.isDigit() }
              if (clean.length <= 10) {
                phoneNumber = clean
              }
            },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("phone_number_input"),
            singleLine = true,
            leadingIcon = {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 12.dp, end = 4.dp)
              ) {
                // Indian Tricolor indicator
                Text(
                  text = "\uD83C\uDDEE\uD83C\uDDF3 +91",
                  color = SandstoneIvory,
                  fontWeight = FontWeight.Bold,
                  fontSize = 15.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                  modifier = Modifier
                    .height(20.dp)
                    .width(1.dp)
                    .background(Color(0xFF455A64))
                )
              }
            },
            placeholder = {
              Text(
                text = "98765 43210",
                color = Color(0xFF78909C),
                fontSize = 15.sp
              )
            },
            keyboardOptions = KeyboardOptions(
              keyboardType = KeyboardType.Phone,
              imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
              onDone = {
                focusManager.clearFocus()
                if (activity != null && phoneNumber.length == 10) {
                  authViewModel.sendOtp(activity, phoneNumber)
                }
              }
            ),
            colors = OutlinedTextFieldDefaults.colors(
              focusedTextColor = Color.White,
              unfocusedTextColor = Color.White,
              focusedBorderColor = RoyalSaffron,
              unfocusedBorderColor = Color(0xFF37474F),
              focusedContainerColor = Color(0xFF0D1522),
              unfocusedContainerColor = Color(0xFF0D1522)
            ),
            shape = RoundedCornerShape(12.dp)
          )

          Text(
            text = "Enter 10-digit number starting with 6, 7, 8, or 9",
            color = Color(0xFF90A4AE),
            fontSize = 12.sp,
            modifier = Modifier.padding(top = 6.dp, start = 4.dp)
          )

          if (errorMessage != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Surface(
              modifier = Modifier
                .fillMaxWidth()
                .testTag("error_banner"),
              color = Color(0xFF3E1F24),
              shape = RoundedCornerShape(8.dp)
            ) {
              Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  imageVector = Icons.Default.Info,
                  contentDescription = "Error",
                  tint = Color(0xFFFF8A80),
                  modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = errorMessage,
                  color = Color(0xFFFFCDD2),
                  fontSize = 13.sp,
                  lineHeight = 18.sp
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(20.dp))

          Button(
            onClick = {
              focusManager.clearFocus()
              if (activity != null) {
                authViewModel.sendOtp(activity, phoneNumber)
              }
            },
            enabled = !isLoading && phoneNumber.length == 10,
            modifier = Modifier
              .fillMaxWidth()
              .height(52.dp)
              .testTag("send_otp_button"),
            colors = ButtonDefaults.buttonColors(
              containerColor = RoyalSaffron,
              disabledContainerColor = Color(0xFF37474F)
            ),
            shape = RoundedCornerShape(12.dp)
          ) {
            if (isLoading) {
              CircularProgressIndicator(
                color = Color.White,
                modifier = Modifier.size(22.dp),
                strokeWidth = 2.5.dp
              )
              Spacer(modifier = Modifier.width(10.dp))
              Text(
                text = "Dispatching Real SMS...",
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp
              )
            } else {
              Icon(
                imageVector = Icons.Default.Phone,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "Get Real OTP via SMS",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Production Security & Real OTP Badge
      Surface(
        color = Color(0xFF0F1B2B),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E3A5F)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.padding(14.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.Security,
            contentDescription = "Firebase Security",
            tint = HeritageGold,
            modifier = Modifier.size(24.dp)
          )
          Spacer(modifier = Modifier.width(12.dp))
          Column {
            Text(
              text = "Production Firebase Authentication",
              color = SandstoneIvory,
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "Real OTP directly via Google Firebase SMS Gateway. No mock codes or local simulations.",
              color = Color(0xFF90A4AE),
              fontSize = 11.sp,
              lineHeight = 15.sp
            )
          }
        }
      }
    }
  }
}
