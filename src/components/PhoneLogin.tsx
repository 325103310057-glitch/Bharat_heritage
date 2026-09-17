import React, { useState, useEffect, useRef } from 'react';
import { setupRecaptcha, sendRealSmsOtp } from '../firebase';
import { ConfirmationResult } from 'firebase/auth';

interface PhoneLoginProps {
  onCodeSent: (confirmationResult: ConfirmationResult, formattedPhone: string) => void;
}

export const PhoneLogin: React.FC<PhoneLoginProps> = ({ onCodeSent }) => {
  const [phoneNumber, setPhoneNumber] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);
  const recaptchaContainerRef = useRef<HTMLDivElement>(null);

  // Clean up recaptcha on unmount
  useEffect(() => {
    return () => {
      if ((window as any).recaptchaVerifier) {
        try {
          (window as any).recaptchaVerifier.clear();
          (window as any).recaptchaVerifier = null;
        } catch (e) {
          // ignore cleanup errors
        }
      }
    };
  }, []);

  const validateIndianPhone = (raw: string): string | null => {
    const clean = raw.replace(/\D/g, '');
    let digits = clean;
    if (clean.startsWith('91') && clean.length === 12) {
      digits = clean.slice(2);
    } else if (clean.startsWith('0') && clean.length === 11) {
      digits = clean.slice(1);
    }

    if (digits.length !== 10) {
      return 'Please enter a valid 10-digit Indian mobile number.';
    }

    if (!['6', '7', '8', '9'].includes(digits[0])) {
      return 'Indian mobile numbers must start with 6, 7, 8, or 9.';
    }

    return null;
  };

  const handleSendOtp = async (e: React.FormEvent) => {
    e.preventDefault();
    setErrorMessage(null);

    const validationError = validateIndianPhone(phoneNumber);
    if (validationError) {
      setErrorMessage(validationError);
      return;
    }

    const clean = phoneNumber.replace(/\D/g, '');
    const digits = clean.length === 12 && clean.startsWith('91') ? clean.slice(2) : clean;
    const e164 = `+91${digits}`;

    setIsLoading(true);

    try {
      const verifier = setupRecaptcha('recaptcha-container');
      const confirmationResult = await sendRealSmsOtp(e164, verifier);
      console.log('Firebase real SMS OTP dispatched to:', e164);
      onCodeSent(confirmationResult, e164);
    } catch (error: any) {
      console.error('Firebase Phone Auth error:', error);
      let message = 'Failed to send SMS code. Please try again.';
      if (error.code === 'auth/invalid-phone-number') {
        message = 'Invalid phone number format. Please enter a valid Indian number.';
      } else if (error.code === 'auth/too-many-requests') {
        message = 'Too many requests. Please wait a few minutes before trying again.';
      } else if (error.code === 'auth/quota-exceeded') {
        message = 'SMS quota exceeded for today. Please contact support or try tomorrow.';
      } else if (error.message) {
        message = error.message;
      }
      setErrorMessage(message);
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
          🏛️
        </div>
        <div style={{
          color: '#D4AF37',
          fontSize: '11px',
          fontWeight: 'bold',
          letterSpacing: '3px',
          textTransform: 'uppercase'
        }}>
          Bharat Heritage
        </div>
        <h2 style={{
          margin: '6px 0 8px',
          fontSize: '22px',
          fontWeight: 'bold',
          fontFamily: 'Georgia, serif'
        }}>
          Phone Authentication
        </h2>
        <p style={{
          fontSize: '13px',
          color: '#90A4AE',
          lineHeight: '1.5',
          margin: 0
        }}>
          Enter your 10-digit Indian mobile number to receive a real SMS verification code via Firebase.
        </p>
      </div>

      <form onSubmit={handleSendOtp}>
        <div style={{ marginBottom: '16px' }}>
          <label style={{
            display: 'block',
            fontSize: '13px',
            fontWeight: '600',
            marginBottom: '8px',
            color: '#CAD1DC'
          }}>
            Mobile Number
          </label>
          <div style={{
            display: 'flex',
            alignItems: 'center',
            backgroundColor: '#0D1522',
            border: '1px solid #37474F',
            borderRadius: '12px',
            overflow: 'hidden',
            padding: '0 12px'
          }}>
            <span style={{
              fontWeight: 'bold',
              color: '#FBF8F2',
              fontSize: '15px',
              paddingRight: '8px',
              borderRight: '1px solid #37474F',
              display: 'flex',
              alignItems: 'center',
              gap: '6px'
            }}>
              🇮🇳 +91
            </span>
            <input
              type="tel"
              value={phoneNumber}
              onChange={(e) => {
                const val = e.target.value.replace(/\D/g, '');
                if (val.length <= 10) setPhoneNumber(val);
              }}
              placeholder="98765 43210"
              style={{
                flex: 1,
                background: 'transparent',
                border: 'none',
                padding: '14px 12px',
                color: '#FFFFFF',
                fontSize: '16px',
                outline: 'none'
              }}
              autoComplete="tel-national"
              required
            />
          </div>
          <span style={{
            fontSize: '11px',
            color: '#78909C',
            display: 'block',
            marginTop: '6px'
          }}>
            Must be 10 digits starting with 6, 7, 8, or 9
          </span>
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

        <div id="recaptcha-container" ref={recaptchaContainerRef}></div>

        <button
          type="submit"
          disabled={isLoading || phoneNumber.length !== 10}
          style={{
            width: '100%',
            padding: '14px',
            borderRadius: '12px',
            border: 'none',
            background: phoneNumber.length === 10 && !isLoading
              ? 'linear-gradient(135deg, #FF6F00, #E65100)'
              : '#37474F',
            color: '#FFFFFF',
            fontWeight: 'bold',
            fontSize: '15px',
            cursor: phoneNumber.length === 10 && !isLoading ? 'pointer' : 'not-allowed',
            transition: 'all 0.2s',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            gap: '8px'
          }}
        >
          {isLoading ? (
            <>
              <span style={{ display: 'inline-block', animation: 'spin 1s linear infinite' }}>⏳</span>
              <span>Sending Real SMS OTP...</span>
            </>
          ) : (
            <>
              <span>📱</span>
              <span>Get Real OTP via SMS</span>
            </>
          )}
        </button>
      </form>

      <div style={{
        marginTop: '20px',
        padding: '12px',
        backgroundColor: '#0F1B2B',
        borderRadius: '10px',
        border: '1px solid #1E3A5F',
        fontSize: '11px',
        color: '#90A4AE',
        lineHeight: '1.4'
      }}>
        <strong style={{ color: '#D4AF37', display: 'block', marginBottom: '2px' }}>
          🔒 Production Firebase Phone Auth
        </strong>
        Real SMS OTP dispatched directly through Google Firebase SMS Gateway. No mock OTPs or simulated verification.
      </div>
    </div>
  );
};
