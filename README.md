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

### 1. MedGemma Local Server Setup
The app requires the local analysis server to be running to process medical reports.
*   **Repository**: [AaruBama/medgemma-local-server](https://github.com/AaruBama/medgemma-local-server)
*   Clone the repository and follow its README to start the FastAPI server on your local machine. Ensure your machine and the Android device are on the same local network.

### 2. Android App Configuration
*   **Update IP Address**: Open `app/src/main/java/com/spashtai/navigator/data/remote/RetrofitClient.kt`.
*   Change the `PHYSICAL_DEVICE_IP` constant (e.g., `"192.168.1.5"`) to match the IPv4 address of the computer running the MedGemma server.
*   **Build & Run**: Sync the project with Gradle in Android Studio and deploy to your physical device or emulator.

### 3. Permissions & Security
*   The app requires Network access and File/Camera permissions to interact with and analyze medical reports.
*   **Cleartext Traffic**: The `AndroidManifest.xml` has `xml android:usesCleartextTraffic="true"` enabled to allow HTTP connections to your local development server. Ensure you transition to secure HTTPS for any production deployment.
