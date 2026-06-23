# ChatApp — Android (Kotlin + Jetpack Compose)

Ek pura real-time chat app — Discord ki tarah.

## 🚀 Quick Start (GitHub se APK)

1. **Is folder ko GitHub pe push karo**
2. **Actions tab pe jao** → latest run → Artifacts → `ChatApp-debug`
3. APK download karo aur install karo

> GitHub Actions automatically APK build karta hai har push pe!

## 📱 Screens

| Screen | Description |
|--------|-------------|
| Login | Username ya email se login |
| Signup | Naya account, profile photo |
| OTP Verify | Email verification 6-digit |
| Home | Server list + empty state |
| Server Chat | Real-time group chat |
| Contacts | DM conversations list |
| DM Chat | Direct messages, voice, photo |
| Add Members | Invite link + user search |
| Profile | Apni profile dekhna |
| Edit Profile | Nickname change, photo |
| Voice Call | Ringing → Active → End |
| Video Call | Full screen video |
| Report | User report karna |
| Admin Panel | Gmail, OTP template, DB settings |

## 🎨 Design

- Background: `#0A0A0A` (pure dark)
- Accent: `#25D366` (WhatsApp green)
- Destructive: `#FF4444` (red)

## 🔌 Backend

API already live hai:
```
https://ee7bc21f-b2d7-429c-90f7-4db5c5a51203-00-gdz2mgezl6uw.sisko.replit.dev/api/v1/
```

URL change karne ke liye:
`app/src/main/java/com/chatapp/network/RetrofitClient.kt`

## 🛠️ Local Build (Android Studio)

1. **Android Studio Hedgehog+** install karo
2. Ye folder open karo
3. Gradle sync hone do
4. Run karo (API 26+)

## 📋 Permissions

App automatically maangta hai:
- 🔔 Notifications
- 📷 Camera
- 🎤 Microphone
- 📂 Storage / Media

## 🔒 API Routes Used

```
POST /auth/login          ← Login
POST /auth/signup         ← Sign up
POST /auth/send-otp       ← OTP bhejo
POST /auth/verify-otp     ← OTP verify
GET  /users/me            ← Apni profile
PATCH /users/me           ← Profile update
GET  /servers             ← Server list
POST /servers             ← Server banao
POST /servers/join        ← Server join
GET  /servers/:id/messages ← Messages
POST /servers/:id/messages ← Message bhejo
GET  /chats               ← DM list
GET  /chats/:id/messages  ← DM messages
POST /chats/:id/messages  ← DM bhejo
GET  /admin/stats         ← Admin stats
PUT  /admin/settings      ← Admin settings
```

## ⚡ WebSocket

Real-time ke liye:
```
wss://ee7bc21f-b2d7-429c-90f7-4db5c5a51203-00-gdz2mgezl6uw.sisko.replit.dev/ws
```

Events: `message:new`, `dm:new`, `auth:ok`
