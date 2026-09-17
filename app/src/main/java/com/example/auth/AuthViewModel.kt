package com.example.auth

import android.app.Activity
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class AuthViewModel : ViewModel() {

  companion object {
    private const val TAG = "BharatAuth"
    private const val RESEND_INTERVAL_SECONDS = 60
  }

  private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
  val authState: StateFlow<AuthState> = _authState.asStateFlow()

  private var auth: FirebaseAuth? = null
  private var timerJob: Job? = null
  private var currentVerificationId: String? = null
  private var currentResendToken: PhoneAuthProvider.ForceResendingToken? = null
  private var currentPhoneE164: String? = null

  init {
    try {
      auth = FirebaseAuth.getInstance()
      val currentUser = auth?.currentUser
      if (currentUser != null) {
        fetchIdTokenAndSetAuthenticated(currentUser)
      } else {
        _authState.value = AuthState.Unauthenticated()
      }

      auth?.addAuthStateListener { firebaseAuth ->
        val user = firebaseAuth.currentUser
        if (user == null) {
          if (_authState.value !is AuthState.CodeSent) {
            _authState.value = AuthState.Unauthenticated()
          }
        } else {
          fetchIdTokenAndSetAuthenticated(user)
        }
      }
    } catch (e: Exception) {
      Log.e(TAG, "Error initializing FirebaseAuth: ${e.message}", e)
      _authState.value = AuthState.Unauthenticated(
        errorMessage = "Firebase initialization note: ${e.localizedMessage ?: "Please verify Firebase setup"}"
      )
    }
  }

  /**
   * Validate Indian mobile number:
   * Must be 10 digits and start with 6, 7, 8, or 9.
   */
  fun validateIndianPhoneNumber(rawPhone: String): String? {
    val clean = rawPhone.replace(Regex("[^0-9]"), "")
    val digits = when {
      clean.startsWith("91") && clean.length == 12 -> clean.substring(2)
      clean.startsWith("0") && clean.length == 11 -> clean.substring(1)
      else -> clean
    }

    if (digits.length != 10) {
      return "Please enter a valid 10-digit Indian mobile number."
    }

    val firstDigit = digits.first()
    if (firstDigit !in listOf('6', '7', '8', '9')) {
      return "Indian mobile numbers must start with 6, 7, 8, or 9."
    }

    return null
  }

  /**
   * Normalizes to standard E.164 (+91XXXXXXXXXX)
   */
  fun formatToE164(rawPhone: String): String {
    val clean = rawPhone.replace(Regex("[^0-9]"), "")
    val digits = when {
      clean.startsWith("91") && clean.length == 12 -> clean.substring(2)
      clean.startsWith("0") && clean.length == 11 -> clean.substring(1)
      else -> clean
    }
    return "+91$digits"
  }

  /**
   * Start REAL Firebase Phone Authentication
   * Firebase sends the real SMS OTP to the user's mobile number.
   */
  fun sendOtp(activity: Activity, rawPhone: String) {
    val validationError = validateIndianPhoneNumber(rawPhone)
    if (validationError != null) {
      _authState.value = AuthState.Unauthenticated(errorMessage = validationError)
      return
    }

    val e164 = formatToE164(rawPhone)
    currentPhoneE164 = e164
    _authState.value = AuthState.Loading

    val firebaseAuth = auth
    if (firebaseAuth == null) {
      _authState.value = AuthState.Unauthenticated(
        errorMessage = "Firebase Auth is not available. Please verify Firebase project setup."
      )
      return
    }

    val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
      override fun onVerificationCompleted(credential: PhoneAuthCredential) {
        Log.d(TAG, "onVerificationCompleted: auto-retrieval / instant verification")
        // Instant verification or auto-retrieval
        signInWithPhoneCredential(credential)
      }

      override fun onVerificationFailed(e: FirebaseException) {
        Log.e(TAG, "onVerificationFailed: ${e.message}", e)
        val userFriendlyMessage = when (e) {
          is FirebaseAuthInvalidCredentialsException ->
            "Invalid phone number or request format. Please verify and try again."
          is FirebaseTooManyRequestsException ->
            "Too many requests from this device. Please wait a few minutes before trying again."
          else -> {
            val msg = e.localizedMessage ?: "Application verification failed."
            if (msg.contains("App verification", ignoreCase = true) || msg.contains("reCAPTCHA", ignoreCase = true) || msg.contains("SafetyNet", ignoreCase = true) || msg.contains("Play Integrity", ignoreCase = true)) {
              "Firebase verification: Ensure SHA-1 & SHA-256 fingerprints and Phone Auth are enabled in Firebase Console ($msg)"
            } else {
              msg
            }
          }
        }
        _authState.value = AuthState.Unauthenticated(errorMessage = userFriendlyMessage)
      }

      override fun onCodeSent(
        verificationId: String,
        token: PhoneAuthProvider.ForceResendingToken
      ) {
        Log.d(TAG, "onCodeSent: SMS OTP dispatched by Firebase to $e164")
        currentVerificationId = verificationId
        currentResendToken = token

        _authState.value = AuthState.CodeSent(
          verificationId = verificationId,
          phoneNumber = e164,
          resendToken = token,
          secondsRemaining = RESEND_INTERVAL_SECONDS
        )

        startResendTimer()
      }
    }

    try {
      val options = PhoneAuthOptions.newBuilder(firebaseAuth)
        .setPhoneNumber(e164)
        .setTimeout(RESEND_INTERVAL_SECONDS.toLong(), TimeUnit.SECONDS)
        .setActivity(activity)
        .setCallbacks(callbacks)
        .build()

      PhoneAuthProvider.verifyPhoneNumber(options)
    } catch (e: Exception) {
      Log.e(TAG, "Failed to call PhoneAuthProvider.verifyPhoneNumber: ${e.message}", e)
      _authState.value = AuthState.Unauthenticated(
        errorMessage = e.localizedMessage ?: "Failed to initiate SMS verification."
      )
    }
  }

  /**
   * Resend REAL SMS OTP using Firebase ForceResendingToken
   */
  fun resendOtp(activity: Activity) {
    val e164 = currentPhoneE164 ?: return
    val token = currentResendToken ?: return
    val firebaseAuth = auth ?: return

    val currentState = _authState.value as? AuthState.CodeSent
    _authState.value = currentState?.copy(
      secondsRemaining = RESEND_INTERVAL_SECONDS,
      errorMessage = null,
      isVerifying = false
    ) ?: AuthState.Loading

    val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
      override fun onVerificationCompleted(credential: PhoneAuthCredential) {
        signInWithPhoneCredential(credential)
      }

      override fun onVerificationFailed(e: FirebaseException) {
        Log.e(TAG, "Resend onVerificationFailed: ${e.message}")
        val errorMsg = when (e) {
          is FirebaseTooManyRequestsException -> "SMS quota exceeded or requests throttled. Please try later."
          else -> e.localizedMessage ?: "Failed to resend SMS."
        }
        _authState.value = (currentState ?: AuthState.CodeSent(
          verificationId = currentVerificationId ?: "",
          phoneNumber = e164,
          resendToken = token
        )).let {
          (it as AuthState.CodeSent).copy(errorMessage = errorMsg, isVerifying = false)
        }
      }

      override fun onCodeSent(
        verificationId: String,
        resendingToken: PhoneAuthProvider.ForceResendingToken
      ) {
        currentVerificationId = verificationId
        currentResendToken = resendingToken
        _authState.value = AuthState.CodeSent(
          verificationId = verificationId,
          phoneNumber = e164,
          resendToken = resendingToken,
          secondsRemaining = RESEND_INTERVAL_SECONDS
        )
        startResendTimer()
      }
    }

    try {
      val options = PhoneAuthOptions.newBuilder(firebaseAuth)
        .setPhoneNumber(e164)
        .setTimeout(RESEND_INTERVAL_SECONDS.toLong(), TimeUnit.SECONDS)
        .setActivity(activity)
        .setCallbacks(callbacks)
        .setForceResendingToken(token)
        .build()

      PhoneAuthProvider.verifyPhoneNumber(options)
    } catch (e: Exception) {
      Log.e(TAG, "Error in resendOtp: ${e.message}", e)
    }
  }

  /**
   * Verify REAL SMS OTP with Firebase confirmationResult / PhoneAuthProvider
   */
  fun verifyOtp(enteredOtp: String) {
    val verificationId = currentVerificationId ?: return
    if (enteredOtp.length != 6) {
      val state = _authState.value as? AuthState.CodeSent
      if (state != null) {
        _authState.value = state.copy(errorMessage = "Please enter the full 6-digit OTP received via SMS.")
      }
      return
    }

    val state = _authState.value as? AuthState.CodeSent
    if (state != null) {
      _authState.value = state.copy(isVerifying = true, errorMessage = null)
    }

    try {
      val credential = PhoneAuthProvider.getCredential(verificationId, enteredOtp)
      signInWithPhoneCredential(credential)
    } catch (e: Exception) {
      Log.e(TAG, "Error creating PhoneAuthCredential: ${e.message}", e)
      val stateAfter = _authState.value as? AuthState.CodeSent
      if (stateAfter != null) {
        _authState.value = stateAfter.copy(
          isVerifying = false,
          errorMessage = "Invalid verification code format."
        )
      }
    }
  }

  private fun signInWithPhoneCredential(credential: PhoneAuthCredential) {
    val firebaseAuth = auth
    if (firebaseAuth == null) {
      _authState.value = AuthState.Unauthenticated(errorMessage = "Firebase Auth not ready.")
      return
    }

    firebaseAuth.signInWithCredential(credential)
      .addOnCompleteListener { task ->
        if (task.isSuccessful) {
          val user = task.result.user
          if (user != null) {
            timerJob?.cancel()
            fetchIdTokenAndSetAuthenticated(user)
          } else {
            _authState.value = AuthState.Unauthenticated(errorMessage = "Sign-in succeeded but user profile was null.")
          }
        } else {
          val exception = task.exception
          Log.e(TAG, "signInWithCredential failed: ${exception?.message}", exception)
          val errorText = when (exception) {
            is FirebaseAuthInvalidCredentialsException ->
              "Invalid verification code. Please check the SMS and enter the correct 6-digit OTP."
            else -> exception?.localizedMessage ?: "Failed to verify OTP with Firebase."
          }

          val current = _authState.value
          if (current is AuthState.CodeSent) {
            _authState.value = current.copy(isVerifying = false, errorMessage = errorText)
          } else {
            _authState.value = AuthState.Unauthenticated(errorMessage = errorText)
          }
        }
      }
  }

  private fun fetchIdTokenAndSetAuthenticated(user: com.google.firebase.auth.FirebaseUser) {
    user.getIdToken(false).addOnCompleteListener { tokenTask ->
      val token = if (tokenTask.isSuccessful) tokenTask.result.token else null
      val profile = UserProfile(
        uid = user.uid,
        phoneNumber = user.phoneNumber ?: currentPhoneE164 ?: "+91-Verified",
        displayName = "Bharat Seeker",
        preferredLanguage = "English",
        serverSyncStatus = if (token != null) "Firebase Verified (Token Ready)" else "Signed In"
      )
      _authState.value = AuthState.Authenticated(
        user = user,
        idToken = token,
        profile = profile
      )
    }
  }

  private fun startResendTimer() {
    timerJob?.cancel()
    timerJob = viewModelScope.launch {
      for (i in RESEND_INTERVAL_SECONDS downTo 1) {
        val state = _authState.value
        if (state is AuthState.CodeSent) {
          _authState.value = state.copy(secondsRemaining = i)
        } else {
          break
        }
        delay(1000)
      }
      val state = _authState.value
      if (state is AuthState.CodeSent) {
        _authState.value = state.copy(secondsRemaining = 0)
      }
    }
  }

  fun resetToPhoneInput() {
    timerJob?.cancel()
    _authState.value = AuthState.Unauthenticated()
  }

  fun signOut() {
    timerJob?.cancel()
    try {
      auth?.signOut()
    } catch (e: Exception) {
      Log.e(TAG, "signOut error: ${e.message}")
    }
    _authState.value = AuthState.Unauthenticated()
  }
}
