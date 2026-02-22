# Spasht AI - Healthcare Co-Pilot for Frontline Workers

Spasht AI is a state-of-the-art Android healthcare co-pilot designed for frontline workers in India. It assists in clinical diagnostics via voice, image analysis of medical reports, and comprehensive patient history management.

## 🚀 Key Features

- **Report Scanner (MedGemma Integration)**: Real-time analysis of medical reports (Radiology, Blood tests, etc.) with detailed insights and follow-up chat capabilities.
- **Voice Diagnostic**: Interactive assistant for symptom checking and patient guidance.
- **Clinical Dashboard**: Overview of patient stats and recent activities.
- **Patient History**: Persistent storage and retrieval of previous medical screenings.

## 🧠 AI Architecture: MedGemma Integration

The app currently uses a **Local First** AI strategy for clinical report analysis:

### Current Implementation: Local MedGemma Server
- **Model**: `google/medgemma-1.5-4b-it`
- **Execution**: The app communicates with a local FastAPI server (`medgemma_server.py`) running on the developer machine.
- **Visual Understanding**: Uses instruction-tuned vision-language models to "read" and interpret medical images/PDFs.
- **Detailed Findings**: Implements a strict "Hindi-First" filtering system to provide compassionate, clinical summaries in Hindi while keeping complex reasoning internal.
- **Contextual Chat**: Supports multi-turn follow-up questions by maintaining conversation history between the app and the server.

### Future Roadmap: API Transition
While the current version runs on a local server for rapid prototyping and privacy-first local testing, the architecture is designed to be **API-ready**:
- **Retrofit Service**: The `ApiService.kt` and `RetrofitClient.kt` are structured to point to any URL. 
- **Scalability**: Updating to high-concurrency cloud APIs (like Gemini Pro Vision or hosted MedGemma instances) only requires updating the `BASE_URL` in `RetrofitClient.kt`.

## 🛠 Tech Stack

- **Frontend**: Android (Jetpack Compose, Material 3)
- **Architecture**: MVVM with StateFlow
- **Networking**: Retrofit & OkHttp
- **Database**: Room for local persistence
- **Backend (Development)**: Python, FastAPI, Transformers (PyTorch/MPS for Apple Silicon)

## 🏗 Setup and Integration

Follow these steps to run the Spasht AI Android application on your local machine:

### 1. Prerequisites
- **Android Studio** (Latest stable version recommended, e.g., Iguana or Jellyfish).
- **Android SDK** (API level 34 or higher).
- **Physical Android Device** (Recommended) or Android Emulator.

### 2. MedGemma Local Server Setup (Backend)
The app relies on a companion local server for analyzing medical reports to ensure data privacy and zero cloud cost during development.
*   **Server Repository**: You must clone and run the backend from [AaruBama/medgemma-local-server](https://github.com/AaruBama/medgemma-local-server).
*   Follow the backend repo's `README.md` to activate the Python environment, download the weights, and launch the FastAPI server.
*   *Note: Ensure your Android device/emulator and the computer running the server are connected to the **same local Wi-Fi network**.*

### 3. Clone and Open the Android Project
```bash
git clone git@github.com:AaruBama/SpashtAI.git
cd SpashtAI
```
*   Launch **Android Studio**, select `Open`, and navigate to the cloned `SpashtAI` directory.
*   Wait for Gradle to sync the project dependencies.

### 4. Configure Networking (Crucial Step)
To allow the Android app to talk to your local Python server, you must provide your computer's local IP address.
*   Find your computer's local IPv4 address (e.g., using `ifconfig` on macOS/Linux or `ipconfig` on Windows).
*   Open the file: `app/src/main/java/com/spashtai/navigator/data/remote/RetrofitClient.kt`.
*   Update the `PHYSICAL_DEVICE_IP` constant:
    ```kotlin
    private const val PHYSICAL_DEVICE_IP = "192.168.1.5" // Replace with YOUR local IP
    ```

### 5. Permissions & Security Note
*   The application requests **Network** (for API communication) and **Storage/Camera** (to read and capture medical reports).
*   **Cleartext Traffic**: Within `AndroidManifest.xml`, `android:usesCleartextTraffic="true"` is temporarily enabled. This is by design to permit unencrypted HTTP traffic solely so the app can communicate with your local development server over your home Wi-Fi.

### 6. Build and Run
*   Connect your Android device via USB (ensure USB Debugging is enabled) or start an emulator.
*   Click the **Play (Run 'app')** button in Android Studio.
*   The Spasht AI app will install and launch. Navigate to the "Report Scanner" tab, upload an image, and it will instantly interface with your local MedGemma server!
