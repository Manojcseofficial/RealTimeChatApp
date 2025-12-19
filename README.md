# Real-Time Chat App (Android)

## Overview
A single-screen Android chat application that supports real-time communication using Socket.IO/WebSockets and handles offline scenarios with automatic message retry.

-----------------------------------------------------------

## Features

### ✅ Real-Time Chat (P0)
- WebSocket-based communication using PieSocket
- Messages sync instantly across multiple clients (Android ↔ Browser)
- No manual refresh required

### ✅ Offline Support with Auto-Retry
- Messages are queued when the device is offline
- Automatic WebSocket reconnection when network is restored
- Queued messages are retried automatically after reconnection

### ✅ Error & Empty State Handling
- Handles network failure gracefully
- Displays empty state when no chats are available
- Handles no-internet scenarios without crashing

### ✅ Chat Preview
- Displays latest message preview

### ✅ Chat Reset on App Close
- Chats are stored only in memory
- All conversations are cleared when the app is closed (as required)

---

## Architecture
- **MVVM (Model–View–ViewModel)**
- ViewModel handles:
  - WebSocket connection
  - Offline queue & retry logic
  - Network state handling
- Activity handles:
  - XML UI
  - Observing LiveData
  - User interactions

-----------------------------------------------------------

## Tech Stack
- Kotlin
- XML UI
- Android ViewModel & LiveData
- OkHttp WebSocket
- ConnectivityManager NetworkCallbacks

-----------------------------------------------------------

## WebSocket
- PieSocket Demo Broadcast Endpoint
- URL: wss://demo.piesocket.com/v3/1?api_key=**YOUR_API_KEY**


-----------------------------------------------------------

## Testing
- Tested real-time messaging across:
  - Android ↔ Browser
  - Android ↔ Android
- Offline scenarios tested by disabling internet
- Auto-retry verified after reconnect

-----------------------------------------------------------

## Demo
A demo video is provided showcasing:
- Real-time messaging
- Offline message queue
- Auto-retry after reconnection
- Chat reset on app close

-----------------------------------------------------------

## Notes
- This project intentionally does not persist chat data.
- Designed according to the requirements.

## ✅DEMO RECORDING

🎥 https://github.com/user-attachments/assets/a79d2629-6e98-48bd-934f-f0cf49287a9a

screenshots/
<img width="1920" height="1080" alt="empty_screen" src="https://github.com/user-attachments/assets/7cad3825-e29f-44e7-8bc7-5ae36b924883" />
<img width="1920" height="1080" alt="real_time_chat" src="https://github.com/user-attachments/assets/a74da20e-842e-4124-9dea-f491a4f21f39" />
<img width="1920" height="1080" alt="offline_mode" src="https://github.com/user-attachments/assets/87b4772b-c465-4a0c-85c3-4bdf7254b530" />
<img width="1920" height="1080" alt="queued_message" src="https://github.com/user-attachments/assets/74ee42c5-2bb0-483a-a81d-04c513f11430" />
<img width="1920" height="1080" alt="sync_automatically" src="https://github.com/user-attachments/assets/031ab6d0-243b-42ca-b871-7f9b99070922" />
<img width="1920" height="1080" alt="final_screen" src="https://github.com/user-attachments/assets/1afc71d7-c530-418e-808c-daf05a9d91d9" />
<img width="1920" height="1080" alt="after_close" src="https://github.com/user-attachments/assets/bbb90ed2-66f0-49da-b154-6c934c9ec02e" />

