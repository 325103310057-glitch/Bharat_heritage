/**
 * Firebase Client SDK Initialization for Bharat Heritage
 * Uses Firebase v9+ Modular API
 * All configuration is read from environment variables.
 */

import { initializeApp, getApps, getApp, FirebaseApp } from 'firebase/app';
import {
  getAuth,
  Auth,
  RecaptchaVerifier,
  signInWithPhoneNumber,
  ConfirmationResult,
} from 'firebase/auth';

const firebaseConfig = {
  apiKey: import.meta.env.VITE_FIREBASE_API_KEY,
  authDomain: import.meta.env.VITE_FIREBASE_AUTH_DOMAIN,
  projectId: import.meta.env.VITE_FIREBASE_PROJECT_ID,
  storageBucket: import.meta.env.VITE_FIREBASE_STORAGE_BUCKET,
  messagingSenderId: import.meta.env.VITE_FIREBASE_MESSAGING_SENDER_ID,
  appId: import.meta.env.VITE_FIREBASE_APP_ID,
};

// Singleton Firebase App
export const app: FirebaseApp = !getApps().length
  ? initializeApp(firebaseConfig)
  : getApp();

// Firebase Auth Instance
export const auth: Auth = getAuth(app);

// Language config
auth.useDeviceLanguage();

/**
 * Setup invisible or normal reCAPTCHA verifier for Phone Auth
 */
export const setupRecaptcha = (
  containerId: string = 'recaptcha-container'
): RecaptchaVerifier => {
  // Clear any existing verifier on window if present
  if ((window as any).recaptchaVerifier) {
    try {
      (window as any).recaptchaVerifier.clear();
    } catch (e) {
      console.warn('Error clearing previous recaptcha verifier:', e);
    }
  }

  const verifier = new RecaptchaVerifier(auth, containerId, {
    size: 'invisible',
    callback: (_response: any) => {
      console.log('reCAPTCHA verified successfully by Firebase.');
    },
    'expired-callback': () => {
      console.warn('reCAPTCHA expired. User may need to retry.');
    },
  });

  (window as any).recaptchaVerifier = verifier;
  return verifier;
};

/**
 * Send real SMS OTP to Indian mobile number using Firebase Phone Auth
 */
export const sendRealSmsOtp = async (
  e164PhoneNumber: string,
  appVerifier: RecaptchaVerifier
): Promise<ConfirmationResult> => {
  return await signInWithPhoneNumber(auth, e164PhoneNumber, appVerifier);
};
