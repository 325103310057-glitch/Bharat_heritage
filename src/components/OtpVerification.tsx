import React, { useState, useEffect } from 'react';
import { ConfirmationResult, User } from 'firebase/auth';
import { setupRecaptcha, sendRealSmsOtp } from '../firebase';

interface OtpVerificationProps {
  confirmationResult: ConfirmationResult;
  phoneNumber: string;
  onSuccess: (user: User, idToken: string) => void;
  onBack: () => void;
}

export const OtpVerification: React.FC<OtpVerificationProps> = ({
  confirmationResult: initialConfirmationResult,
  phoneNumber,
  onSuccess,
  onBack,
}) => {
  const [confirmationResult, setConfirmationResult] = useState<ConfirmationResult>(initialConfirmationResult);
  const [otp, setOtp] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);
  const [secondsRemaining, setSecondsRemaining] = useState(60);

  useEffect(() => {
    if (secondsRemaining <= 0) return;
    const timer = setInterval(() => {
      setSecondsRemaining((prev) => Math.max(0, prev - 1));
    }, 1000);
    return () => clearInterval(timer);
  }, [secondsRemaining]);

  const handleVerify = async (e?: React.FormEvent) => {
    if (e) e.preventDefault();
    if (otp.length !== 6) {
      setErrorMessage('Please enter the complete 6-digit SMS OTP.');
      return;
    }

    setIsLoading(true);
    setErrorMessage(null);

    try {
      // 1. Confirm OTP directly with Firebase
      const userCredential = await confirmationResult.confirm(otp);
      const user = userCredential.user;
      console.log('Firebase user successfully authenticated:', user.uid);

      // 2. Obtain Firebase ID token for Render backend verification
      const idToken = await user.getIdToken(false);

      // 3. Optional: Trigger backend sync with ID token
      const apiBaseUrl = import.meta.env.VITE_API_BASE_URL;
      if (apiBaseUrl) {
        try {
          await fetch(`${apiBaseUrl}/api/profile/sync`, {
            method: 'POST',
            headers: {
              'Content-Type': 'application/json',
              Authorization: `Bearer ${idToken}`,
            },
            body: JSON.stringify({
              phoneNumber: user.phoneNumber || phoneNumber,
              displayName: 'Bharat Seeker',
            }),
          });
        } catch (apiErr) {
          console.warn('Backend sync warning (non-blocking):', apiErr);
        }
      }

      onSuccess(user, idToken);
    } catch (error: any) {
      console.error('OTP Verification error:', error);
      let msg = 'Failed to verify code. Please check the code and try again.';
      if (error.code === 'auth/invalid-verification-code') {
        msg = 'Invalid OTP code entered. Please check your SMS and try again.';
      } else if (error.code === 'auth/code-expired') {
        msg = 'This verification code has expired. Please click "Resend SMS".';
      } else if (error.message) {
        msg = error.message;
      }
      setErrorMessage(msg);
    } finally {
      setIsLoading(false);
    }
  };

  const handleResend = async () => {
    if (secondsRemaining > 0) return;
    setIsLoading(true);
    setErrorMessage(null);

    try {
      const verifier = setupRecaptcha('recaptcha-resend-container');
      const newConfirmation = await sendRealSmsOtp(phoneNumber, verifier);
      setConfirmationResult(newConfirmation);
      setSecondsRemaining(60);
      setOtp('');
      console.log('Resent real SMS OTP to:', phoneNumber);
    } catch (error: any) {
      console.error('Resend error:', error);
      setErrorMessage(error.message || 'Failed to resend SMS OTP.');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div style={{
      maxWidth: '440px',
      margin: '0 auto',
      padding: '24px',
      backgroundColor: '#131D2E',
      borderRadius: '20px',
      border: '1px solid #1E2D42',
      boxShadow: '0 8px 32px rgba(0,0,0,0.5)',
      color: '#FBF8F2',
      fontFamily: 'system-ui, -apple-system, sans-serif'
    }}>
      <div style={{
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'space-between',
        marginBottom: '16px'
      }}>
        <button
          type="button"
          onClick={onBack}
          style={{
            background: 'transparent',
            border: 'none',
            color: '#CAD1DC',
            cursor: 'pointer',
            fontSize: '14px',
            display: 'flex',
            alignItems: 'center',
            gap: '6px'
          }}
        >
          ← Change Number
        </button>
        <span style={{
          fontSize: '11px',
          color: '#D4AF37',
          fontWeight: 'bold',
          letterSpacing: '1.5px'
        }}>
          STEP 2 OF 2
        </span>
      </div>

      <div style={{ textAlign: 'center', marginBottom: '24px' }}>
        <div style={{
          width: '64px',
          height: '64px',
          margin: '0 auto 16px',
          borderRadius: '50%',
          background: 'linear-gradient(135deg, #FF6F00, #E65100)',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          border: '2px solid #D4AF37',
          fontSize: '28px'
        }}>
          📩
        </div>
        <h2 style={{
          margin: '0 0 6px',
          fontSize: '22px',
          fontWeight: 'bold',
          fontFamily: 'Georgia, serif'
        }}>
          Verify Phone Number
        </h2>
        <p style={{
          fontSize: '13px',
          color: '#90A4AE',
          margin: 0
        }}>
          Real SMS OTP sent to:
        </p>
        <div style={{
          color: '#D4AF37',
          fontSize: '16px',
          fontWeight: 'bold',
          marginTop: '4px'
        }}>
          {phoneNumber}
        </div>
      </div>

      <form onSubmit={handleVerify}>
        <div style={{ marginBottom: '20px' }}>
          <label style={{
            display: 'block',
            fontSize: '13px',
            fontWeight: '600',
            marginBottom: '8px',
            color: '#CAD1DC',
            textAlign: 'center'
          }}>
            Enter 6-Digit SMS Code
          </label>
          <input
            type="text"
            inputMode="numeric"
            maxLength={6}
            value={otp}
            onChange={(e) => {
              const clean = e.target.value.replace(/\D/g, '');
              setOtp(clean);
              if (clean.length === 6) {
                // Auto trigger verify
                setTimeout(() => handleVerify(), 100);
              }
            }}
            placeholder="• • • • • •"
            style={{
              width: '100%',
              boxSizing: 'border-box',
              background: '#0D1522',
              border: '2px solid #FF6F00',
              borderRadius: '12px',
              padding: '14px',
              color: '#FFFFFF',
              fontSize: '24px',
              fontWeight: 'bold',
              textAlign: 'center',
              letterSpacing: '12px',
              outline: 'none'
            }}
            autoFocus
            autoComplete="one-time-code"
            required
          />
        </div>

        {errorMessage && (
          <div style={{
            backgroundColor: '#3E1F24',
            border: '1px solid #842029',
            borderRadius: '8px',
            padding: '10px 14px',
            marginBottom: '16px',
            fontSize: '12px',
            color: '#FFCDD2',
            display: 'flex',
            gap: '8px',
            alignItems: 'center'
          }}>
            <span>⚠️</span>
            <span>{errorMessage}</span>
          </div>
        )}

        <div id="recaptcha-resend-container"></div>

        <button
          type="submit"
          disabled={isLoading || otp.length !== 6}
          style={{
            width: '100%',
            padding: '14px',
            borderRadius: '12px',
            border: 'none',
            background: otp.length === 6 && !isLoading
              ? 'linear-gradient(135deg, #FF6F00, #E65100)'
              : '#37474F',
            color: '#FFFFFF',
            fontWeight: 'bold',
            fontSize: '15px',
            cursor: otp.length === 6 && !isLoading ? 'pointer' : 'not-allowed',
            transition: 'all 0.2s',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            gap: '8px'
          }}
        >
          {isLoading ? (
            <>
              <span>⏳</span>
              <span>Verifying with Firebase...</span>
            </>
          ) : (
            <>
              <span>✅</span>
              <span>Verify & Continue</span>
            </>
          )}
        </button>

        <div style={{ textAlign: 'center', marginTop: '16px' }}>
          {secondsRemaining > 0 ? (
            <span style={{ fontSize: '13px', color: '#90A4AE' }}>
              Resend code in {secondsRemaining}s
            </span>
          ) : (
            <button
              type="button"
              onClick={handleResend}
              disabled={isLoading}
              style={{
                background: 'transparent',
                border: '1px solid #D4AF37',
                borderRadius: '8px',
                padding: '8px 16px',
                color: '#D4AF37',
                fontWeight: '600',
                fontSize: '13px',
                cursor: 'pointer'
              }}
            >
              🔄 Resend Real SMS OTP
            </button>
          )}
        </div>
      </form>
    </div>
  );
};
