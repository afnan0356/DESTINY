import { initializeApp, getApps, getApp } from 'firebase/app';
import { getFirestore, collection, doc } from 'firebase/firestore';
import { getAuth } from 'firebase/auth';

const firebaseConfig = {
  apiKey: process.env.NEXT_PUBLIC_FIREBASE_API_KEY || "AIzaSyAd1Wz8-cXx453soMlWkaBuu0clsEZmp4U",
  authDomain: process.env.NEXT_PUBLIC_FIREBASE_AUTH_DOMAIN || "thedestiny.firebaseapp.com",
  projectId: process.env.NEXT_PUBLIC_FIREBASE_PROJECT_ID || "thedestiny",
  storageBucket: process.env.NEXT_PUBLIC_FIREBASE_STORAGE_BUCKET || "thedestiny.firebasestorage.app",
  messagingSenderId: process.env.NEXT_PUBLIC_FIREBASE_MESSAGING_SENDER_ID || "367033397753",
  appId: process.env.NEXT_PUBLIC_FIREBASE_APP_ID || "1:367033397753:web:destiny",
};

export const app = getApps().length > 0 ? getApp() : initializeApp(firebaseConfig);

export const firestoreDatabaseId =
  process.env.NEXT_PUBLIC_FIRESTORE_DATABASE_ID ||
  "ai-studio-destinyenginefou-426b2146-03fb-4e0a-9529-113af0ff5d1b";

export const db = getFirestore(app, firestoreDatabaseId);
export const auth = getAuth(app);

// Firestore Collections
export const usersCol = collection(db, 'users');
export const saveSlotsCol = collection(db, 'saveSlots');
export const livesCol = collection(db, 'lives');
export const charactersCol = collection(db, 'characters');
export const relationshipsCol = collection(db, 'relationships');
export const familyEventsCol = collection(db, 'familyEvents');
