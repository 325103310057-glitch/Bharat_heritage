package com.example.auth

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthProvider

/**
 * Authoritative authentication state for Bharat Heritage.
 */
sealed interface AuthState {
  /**
   * Checking initial authentication or performing verification operation.
   */
  data object Loading : AuthState

  /**
   * User is not authenticated. Ready for Indian mobile number input.
   */
  data class Unauthenticated(val errorMessage: String? = null) : AuthState

  /**
   * Firebase has dispatched a real SMS OTP. Waiting for user to enter 6-digit code.
   */
  data class CodeSent(
    val verificationId: String,
    val phoneNumber: String,
    val resendToken: PhoneAuthProvider.ForceResendingToken?,
    val secondsRemaining: Int = 60,
    val errorMessage: String? = null,
    val isVerifying: Boolean = false
  ) : AuthState

  /**
   * User is authenticated by Firebase Authentication.
   */
  data class Authenticated(
    val user: FirebaseUser,
    val idToken: String? = null,
    val profile: UserProfile? = null
  ) : AuthState
}

data class UserProfile(
  val uid: String,
  val phoneNumber: String,
  val displayName: String = "Heritage Seeker",
  val preferredLanguage: String = "English",
  val bookmarks: List<String> = emptyList(),
  val quizScore: Int = 120,
  val quizBadges: List<String> = listOf("Vedic Explorer", "Temple Chronicler"),
  val serverSyncStatus: String = "Local Synced"
)
