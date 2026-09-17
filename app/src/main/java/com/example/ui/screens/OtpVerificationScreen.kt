package com.example.ui.screens

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
fun OtpVerificationScreen(
  authViewModel: AuthViewModel,
  codeSentState: AuthState.CodeSent,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val activity = context as? Activity
  val focusManager = LocalFocusManager.current

  var otpCode by remember { mutableStateOf("") }
  val scrollState = rememberScrollState()

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
        .padding(horizontal = 24.dp, vertical = 28.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // Top back button
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        IconButton(
          onClick = { authViewModel.resetToPhoneInput() },
          modifier = Modifier.testTag("back_to_phone_button")
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back to Phone Input",
            tint = SandstoneIvory
          )
        }
        Spacer(modifier = Modifier.weight(1f))
        Text(
          text = "STEP 2 OF 2",
          color = HeritageGold,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          letterSpacing = 1.5.sp
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Verification Icon
      Box(
        modifier = Modifier
          .size(72.dp)
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
          imageVector = Icons.AutoMirrored.Filled.Send,
          contentDescription = "SMS OTP",
          tint = Color.White,
          modifier = Modifier.size(36.dp)
        )
      }

      Spacer(modifier = Modifier.height(16.dp))

      Text(
        text = "Verify Phone Number",
        color = SandstoneIvory,
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily.Serif,
        textAlign = TextAlign.Center
      )

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = "Real Firebase SMS OTP sent to:",
        color = Color(0xFFB0BEC5),
        fontSize = 14.sp,
        textAlign = TextAlign.Center
      )

      Text(
        text = codeSentState.phoneNumber,
        color = HeritageGold,
        fontSize = 17.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(top = 2.dp)
      )

      Spacer(modifier = Modifier.height(24.dp))

      // OTP Card
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("otp_verification_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
          containerColor = Color(0xFF131D2E)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text(
            text = "Enter 6-Digit SMS Code",
            color = SandstoneIvory,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.fillMaxWidth()
          )

          Spacer(modifier = Modifier.height(12.dp))

          // 6-digit text input
          OutlinedTextField(
            value = otpCode,
            onValueChange = { input ->
              val clean = input.filter { it.isDigit() }
              if (clean.length <= 6) {
                otpCode = clean
                if (clean.length == 6) {
                  focusManager.clearFocus()
                  authViewModel.verifyOtp(clean)
                }
              }
            },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("otp_input_field"),
            singleLine = true,
            placeholder = {
              Text(
                text = "• • • • • •",
                color = Color(0xFF78909C),
                fontSize = 22.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                letterSpacing = 12.sp
              )
            },
            textStyle = androidx.compose.ui.text.TextStyle(
              fontSize = 24.sp,
              fontWeight = FontWeight.Bold,
              letterSpacing = 12.sp,
              textAlign = TextAlign.Center,
              color = Color.White
            ),
            keyboardOptions = KeyboardOptions(
              keyboardType = KeyboardType.NumberPassword,
              imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
              onDone = {
                focusManager.clearFocus()
                if (otpCode.length == 6) {
                  authViewModel.verifyOtp(otpCode)
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

          if (codeSentState.errorMessage != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Surface(
              modifier = Modifier
                .fillMaxWidth()
                .testTag("otp_error_banner"),
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
                  text = codeSentState.errorMessage,
                  color = Color(0xFFFFCDD2),
                  fontSize = 13.sp,
                  lineHeight = 18.sp
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(20.dp))

          // Verify Button
          Button(
            onClick = {
              focusManager.clearFocus()
              authViewModel.verifyOtp(otpCode)
            },
            enabled = !codeSentState.isVerifying && otpCode.length == 6,
            modifier = Modifier
              .fillMaxWidth()
              .height(52.dp)
              .testTag("verify_otp_button"),
            colors = ButtonDefaults.buttonColors(
              containerColor = RoyalSaffron,
              disabledContainerColor = Color(0xFF37474F)
            ),
            shape = RoundedCornerShape(12.dp)
          ) {
            if (codeSentState.isVerifying) {
              CircularProgressIndicator(
                color = Color.White,
                modifier = Modifier.size(22.dp),
                strokeWidth = 2.5.dp
              )
              Spacer(modifier = Modifier.width(10.dp))
              Text(
                text = "Authenticating with Firebase...",
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
              )
            } else {
              Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "Verify OTP & Continue",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
              )
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          // Resend Timer & Button
          if (codeSentState.secondsRemaining > 0) {
            Text(
              text = "Resend SMS in ${codeSentState.secondsRemaining}s",
              color = Color(0xFF90A4AE),
              fontSize = 13.sp,
              textAlign = TextAlign.Center
            )
          } else {
            OutlinedButton(
              onClick = {
                if (activity != null) {
                  authViewModel.resendOtp(activity)
                }
              },
              modifier = Modifier
                .fillMaxWidth()
                .testTag("resend_otp_button"),
              colors = ButtonDefaults.outlinedButtonColors(
                contentColor = HeritageGold
              ),
              border = androidx.compose.foundation.BorderStroke(1.dp, HeritageGold),
              shape = RoundedCornerShape(12.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Resend",
                modifier = Modifier.size(16.dp),
                tint = HeritageGold
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "Resend Real SMS OTP",
                color = HeritageGold,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
              )
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          Text(
            text = "Wrong number? Edit phone number",
            color = Color(0xFF64B5F6),
            fontSize = 13.sp,
            modifier = Modifier
              .clickable { authViewModel.resetToPhoneInput() }
              .padding(8.dp)
          )
        }
      }
    }
  }
}
