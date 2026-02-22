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

1.  **Local Server**: Ensure the `medgemma_server.py` is running on a machine accessible to the Android device.
2.  **Network Configuration**: Update your laptop's local IP in `RetrofitClient.kt`.
3.  **Cleartext Traffic**: The `AndroidManifest.xml` has cleartext traffic enabled for local development. For production, ensure HTTPS is implemented.
