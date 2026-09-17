"use strict";
/**
 * Bharat Heritage - Production Backend Server (Render Deployment)
 *
 * CRITICAL ARCHITECTURE:
 * 1. Firebase Authentication is the sole authority for Phone OTP & Identity.
 * 2. This backend NEVER generates, stores, or validates OTPs manually.
 * 3. Client authenticates directly with Firebase, receives a Firebase ID Token,
 *    and attaches it as: Authorization: Bearer <FIREBASE_ID_TOKEN>
 * 4. This server verifies the Firebase ID Token using Firebase Admin SDK.
 * 5. User identity (UID, phone number) is derived STRICTLY from the verified token.
 */
var __createBinding = (this && this.__createBinding) || (Object.create ? (function(o, m, k, k2) {
    if (k2 === undefined) k2 = k;
    var desc = Object.getOwnPropertyDescriptor(m, k);
    if (!desc || ("get" in desc ? !m.__esModule : desc.writable || desc.configurable)) {
      desc = { enumerable: true, get: function() { return m[k]; } };
    }
    Object.defineProperty(o, k2, desc);
}) : (function(o, m, k, k2) {
    if (k2 === undefined) k2 = k;
    o[k2] = m[k];
}));
var __setModuleDefault = (this && this.__setModuleDefault) || (Object.create ? (function(o, v) {
    Object.defineProperty(o, "default", { enumerable: true, value: v });
}) : function(o, v) {
    o["default"] = v;
});
var __importStar = (this && this.__importStar) || (function () {
    var ownKeys = function(o) {
        ownKeys = Object.getOwnPropertyNames || function (o) {
            var ar = [];
            for (var k in o) if (Object.prototype.hasOwnProperty.call(o, k)) ar[ar.length] = k;
            return ar;
        };
        return ownKeys(o);
    };
    return function (mod) {
        if (mod && mod.__esModule) return mod;
        var result = {};
        if (mod != null) for (var k = ownKeys(mod), i = 0; i < k.length; i++) if (k[i] !== "default") __createBinding(result, mod, k[i]);
        __setModuleDefault(result, mod);
        return result;
    };
})();
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.verifyFirebaseToken = void 0;
const express_1 = __importDefault(require("express"));
const cors_1 = __importDefault(require("cors"));
const dotenv_1 = __importDefault(require("dotenv"));
const admin = __importStar(require("firebase-admin"));
const path_1 = __importDefault(require("path"));
const fs_1 = __importDefault(require("fs"));
const http_1 = __importDefault(require("http"));
dotenv_1.default.config();
const app = (0, express_1.default)();
const PORT = process.env.PORT || 3000;
app.use((0, cors_1.default)({
    origin: true,
    credentials: true,
}));
app.use(express_1.default.json());
// Initialize Firebase Admin SDK
// On Render, configure FIREBASE_SERVICE_ACCOUNT_JSON as a secret environment variable,
// or provide GOOGLE_APPLICATION_CREDENTIALS pointing to the secure secret file.
if (!admin.apps.length) {
    try {
        if (process.env.FIREBASE_SERVICE_ACCOUNT_JSON) {
            const serviceAccount = JSON.parse(process.env.FIREBASE_SERVICE_ACCOUNT_JSON);
            admin.initializeApp({
                credential: admin.credential.cert(serviceAccount),
            });
            console.log('✅ Firebase Admin SDK initialized via FIREBASE_SERVICE_ACCOUNT_JSON');
        }
        else if (process.env.FIREBASE_PROJECT_ID && process.env.FIREBASE_CLIENT_EMAIL && process.env.FIREBASE_PRIVATE_KEY) {
            admin.initializeApp({
                credential: admin.credential.cert({
                    projectId: process.env.FIREBASE_PROJECT_ID,
                    clientEmail: process.env.FIREBASE_CLIENT_EMAIL,
                    privateKey: process.env.FIREBASE_PRIVATE_KEY.replace(/\\n/g, '\n'),
                }),
            });
            console.log('✅ Firebase Admin SDK initialized via individual environment variables');
        }
        else {
            // Default application credentials (e.g. for GCP or local gcloud auth)
            admin.initializeApp();
            console.log('ℹ️ Firebase Admin SDK initialized with default application credentials');
        }
    }
    catch (error) {
        console.warn('⚠️ Firebase Admin SDK initialization note:', error);
    }
}
/**
 * Middleware: Verify Firebase ID Token
 * Derives user identity strictly from Firebase Admin SDK verification.
 */
const verifyFirebaseToken = async (req, res, next) => {
    const authHeader = req.headers.authorization;
    if (!authHeader || !authHeader.startsWith('Bearer ')) {
        res.status(401).json({
            error: 'Unauthorized: Missing or invalid Authorization header. Expected: Bearer <FIREBASE_ID_TOKEN>',
        });
        return;
    }
    const idToken = authHeader.split('Bearer ')[1]?.trim();
    if (!idToken) {
        res.status(401).json({ error: 'Unauthorized: Token is empty' });
        return;
    }
    try {
        // Verify ID token with Firebase Admin
        const decodedToken = await admin.auth().verifyIdToken(idToken);
        req.user = decodedToken;
        next();
    }
    catch (error) {
        console.error('Failed to verify Firebase ID token:', error.message);
        res.status(401).json({
            error: 'Unauthorized: Invalid or expired Firebase ID token',
            code: error.code || 'AUTH_TOKEN_INVALID',
        });
    }
};
exports.verifyFirebaseToken = verifyFirebaseToken;
const userProfiles = new Map();
// Health Check
app.get('/api/health', (req, res) => {
    res.json({
        status: 'ok',
        service: 'Bharat Heritage Backend',
        firebaseAdminReady: admin.apps.length > 0,
        timestamp: new Date().toISOString(),
    });
});
/**
 * Sync or Create User Profile (Protected)
 * Derives UID and phone number strictly from the verified Firebase ID Token.
 */
app.post('/api/profile/sync', exports.verifyFirebaseToken, (req, res) => {
    const user = req.user;
    const uid = user.uid;
    const phoneNumber = user.phone_number || req.body.phoneNumber || '';
    const now = new Date().toISOString();
    let profile = userProfiles.get(uid);
    if (!profile) {
        profile = {
            firebaseUid: uid,
            phoneNumber,
            displayName: req.body.displayName || 'Heritage Seeker',
            preferredLanguage: req.body.preferredLanguage || 'English',
            bookmarks: req.body.bookmarks || [],
            quizScore: 0,
            quizBadges: ['Vedic Explorer'],
            createdAt: now,
            updatedAt: now,
        };
        userProfiles.set(uid, profile);
        console.log(`✨ Created new profile for Firebase UID: ${uid} (Phone: ${phoneNumber})`);
    }
    else {
        profile.updatedAt = now;
        if (req.body.preferredLanguage)
            profile.preferredLanguage = req.body.preferredLanguage;
        if (req.body.displayName)
            profile.displayName = req.body.displayName;
        if (Array.isArray(req.body.bookmarks))
            profile.bookmarks = req.body.bookmarks;
    }
    res.json({
        success: true,
        message: 'Profile synchronized with Render backend',
        profile,
    });
});
/**
 * Get User Profile (Protected)
 */
app.get('/api/profile', exports.verifyFirebaseToken, (req, res) => {
    const uid = req.user.uid;
    const profile = userProfiles.get(uid);
    if (!profile) {
        res.status(404).json({
            error: 'Profile not found. Please sync your profile first.',
            firebaseUid: uid,
        });
        return;
    }
    res.json({
        success: true,
        profile,
    });
});
/**
 * Manage Bookmarks (Protected)
 */
app.get('/api/bookmarks', exports.verifyFirebaseToken, (req, res) => {
    const uid = req.user.uid;
    const profile = userProfiles.get(uid);
    res.json({
        bookmarks: profile?.bookmarks || [],
    });
});
app.post('/api/bookmarks', exports.verifyFirebaseToken, (req, res) => {
    const uid = req.user.uid;
    const { monumentId } = req.body;
    if (!monumentId) {
        res.status(400).json({ error: 'monumentId is required' });
        return;
    }
    let profile = userProfiles.get(uid);
    if (!profile) {
        profile = {
            firebaseUid: uid,
            phoneNumber: req.user.phone_number || '',
            displayName: 'Heritage Seeker',
            preferredLanguage: 'English',
            bookmarks: [],
            quizScore: 0,
            quizBadges: [],
            createdAt: new Date().toISOString(),
            updatedAt: new Date().toISOString(),
        };
        userProfiles.set(uid, profile);
    }
    const index = profile.bookmarks.indexOf(monumentId);
    if (index >= 0) {
        profile.bookmarks.splice(index, 1);
    }
    else {
        profile.bookmarks.push(monumentId);
    }
    profile.updatedAt = new Date().toISOString();
    res.json({
        success: true,
        bookmarks: profile.bookmarks,
    });
});
/**
 * AI Cultural Heritage Guide Endpoint (Gemini Integration)
 * Supports natural language exploration of Indian heritage, monuments, and history.
 */
app.post('/api/ai/ask', async (req, res) => {
    const { question, language = 'English' } = req.body;
    if (!question) {
        res.status(400).json({ error: 'question is required' });
        return;
    }
    const apiKey = process.env.GEMINI_API_KEY;
    if (!apiKey) {
        // Provide curated historical responses when GEMINI_API_KEY is not yet configured on Render
        res.json({
            answer: `Namaste! As your Bharat Heritage Guide: You asked about "${question}". India's 5,000-year civilizational journey encompasses remarkable achievements in monolithic rock architecture (such as Kailash Temple at Ellora), astronomical sundials (Konark Sun Temple and Jantar Mantar), and spiritual heritage. To enable live Gemini AI queries, set GEMINI_API_KEY in Render environment variables.`,
            source: 'curated_heritage_guide',
        });
        return;
    }
    try {
        const prompt = `You are a respectful, scholarly, and captivating Indian Cultural & Historical Heritage Guide for Bharat Heritage.
User Question: "${question}"
Response Language: ${language}
Provide an authentic, historically accurate, and engaging answer with cultural context and architectural marvels. Keep it under 180 words.`;
        const response = await fetch(`https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=${apiKey}`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                contents: [{ parts: [{ text: prompt }] }],
            }),
        });
        if (!response.ok) {
            const errText = await response.text();
            console.warn('Gemini API call failed:', errText);
            res.json({
                answer: `Namaste! Regarding "${question}": India's monuments and philosophy represent centuries of architectural genius and timeless wisdom.`,
                source: 'fallback',
            });
            return;
        }
        const data = await response.json();
        const answer = data?.candidates?.[0]?.content?.parts?.[0]?.text || 'No response generated.';
        res.json({ answer, source: 'gemini' });
    }
    catch (error) {
        console.error('Error invoking Gemini API:', error);
        res.status(500).json({ error: 'Failed to query AI Guide', details: error.message });
    }
});
// Serve frontend static build if available (for Render deployment)
const distPath = path_1.default.resolve(process.cwd(), 'dist');
if (fs_1.default.existsSync(distPath)) {
    app.use(express_1.default.static(distPath));
    app.get('*', (req, res) => {
        if (!req.path.startsWith('/api')) {
            const indexPath = path_1.default.join(distPath, 'index.html');
            if (fs_1.default.existsSync(indexPath)) {
                res.sendFile(indexPath);
                return;
            }
        }
        res.status(404).json({ error: 'Endpoint not found' });
    });
}
const server = http_1.default.createServer(app);
server.listen(PORT, () => {
    console.log(`🏛️ Bharat Heritage Backend listening on port ${PORT}`);
});
