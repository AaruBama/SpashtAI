import { useState } from "react";
import { ECGPulseWave } from "@/app/components/ECGPulseWave";
import { 
  VoiceDiagnosisIcon,
  ChatDiagnosisIcon,
  UploadReportIcon,
  AcousticDiagnosisIcon,
  HeartRateIcon,
  AIBrainIcon,
  ScanAnalyzeIcon,
  MedicalRecordsIcon,
  MedicationIcon,
  GeneticIcon,
  MedicalAlertIcon
} from "@/app/components/HealthcareIcons";
import { motion } from "motion/react";

interface IconShowcaseProps {
  icon: React.ReactNode;
  label: string;
  description: string;
}

function IconShowcase({ icon, label, description }: IconShowcaseProps) {
  return (
    <motion.div 
      className="flex flex-col items-center p-6 bg-white rounded-xl border border-gray-200 hover:border-cyan-400 hover:shadow-lg transition-all duration-300"
      whileHover={{ scale: 1.05 }}
      whileTap={{ scale: 0.95 }}
    >
      <div className="text-cyan-500 mb-3">
        {icon}
      </div>
      <h3 className="font-semibold text-gray-900 mb-1 text-center">{label}</h3>
      <p className="text-sm text-gray-500 text-center">{description}</p>
    </motion.div>
  );
}

function LoadingStateDemo() {
  return (
    <div className="bg-gradient-to-br from-gray-900 to-gray-800 rounded-xl p-8 border border-gray-700">
      <div className="flex items-center justify-between mb-4">
        <div className="flex items-center gap-3">
          <div className="w-10 h-10 bg-cyan-500/20 rounded-full flex items-center justify-center">
            <AIBrainIcon className="w-6 h-6" color="#00D9FF" />
          </div>
          <div>
            <h3 className="font-semibold text-white">AI Analysis in Progress</h3>
            <p className="text-sm text-gray-400">Processing your health data...</p>
          </div>
        </div>
        <div className="flex gap-2">
          <motion.div 
            className="w-2 h-2 bg-cyan-400 rounded-full"
            animate={{ opacity: [0.3, 1, 0.3] }}
            transition={{ duration: 1.5, repeat: Infinity }}
          />
          <motion.div 
            className="w-2 h-2 bg-cyan-400 rounded-full"
            animate={{ opacity: [0.3, 1, 0.3] }}
            transition={{ duration: 1.5, repeat: Infinity, delay: 0.2 }}
          />
          <motion.div 
            className="w-2 h-2 bg-cyan-400 rounded-full"
            animate={{ opacity: [0.3, 1, 0.3] }}
            transition={{ duration: 1.5, repeat: Infinity, delay: 0.4 }}
          />
        </div>
      </div>
      <ECGPulseWave color="#00D9FF" duration={2.5} className="mb-4" />
      <div className="flex justify-between items-center">
        <span className="text-xs text-gray-500">Neural network analyzing patterns...</span>
        <span className="text-xs text-cyan-400 font-mono">78%</span>
      </div>
    </div>
  );
}

export default function App() {
  const [activeColor, setActiveColor] = useState("#00D9FF");

  const colors = [
    { name: "Cyan", value: "#00D9FF" },
    { name: "Green", value: "#10B981" },
    { name: "Purple", value: "#8B5CF6" },
    { name: "Pink", value: "#EC4899" },
    { name: "Orange", value: "#F59E0B" }
  ];

  const icons = [
    {
      icon: <VoiceDiagnosisIcon className="w-12 h-12" />,
      label: "Voice Diagnosis",
      description: "Voice-activated health assessment"
    },
    {
      icon: <ChatDiagnosisIcon className="w-12 h-12" />,
      label: "Chat Diagnosis",
      description: "AI-powered text consultation"
    },
    {
      icon: <UploadReportIcon className="w-12 h-12" />,
      label: "Upload Reports",
      description: "Import medical documents"
    },
    {
      icon: <AcousticDiagnosisIcon className="w-12 h-12" />,
      label: "Acoustic Diagnosis",
      description: "Sound-based health analysis"
    },
    {
      icon: <HeartRateIcon className="w-12 h-12" />,
      label: "Heart Rate",
      description: "Cardiovascular monitoring"
    },
    {
      icon: <AIBrainIcon className="w-12 h-12" />,
      label: "AI Brain",
      description: "Neural network processing"
    },
    {
      icon: <ScanAnalyzeIcon className="w-12 h-12" />,
      label: "Scan & Analyze",
      description: "Medical imaging review"
    },
    {
      icon: <MedicalRecordsIcon className="w-12 h-12" />,
      label: "Medical Records",
      description: "Health history & reports"
    },
    {
      icon: <MedicationIcon className="w-12 h-12" />,
      label: "Medication",
      description: "Prescription management"
    },
    {
      icon: <GeneticIcon className="w-12 h-12" />,
      label: "Genetic",
      description: "DNA & genetic analysis"
    },
    {
      icon: <MedicalAlertIcon className="w-12 h-12" />,
      label: "Alert",
      description: "Critical health warnings"
    }
  ];

  return (
    <div className="min-h-screen bg-gradient-to-br from-gray-50 to-gray-100 py-12 px-4 sm:px-6 lg:px-8">
      <div className="max-w-7xl mx-auto">
        {/* Header */}
        <motion.div 
          className="text-center mb-12"
          initial={{ opacity: 0, y: -20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.6 }}
        >
          <div className="flex items-center justify-center gap-3 mb-4">
            <HeartRateIcon className="w-12 h-12" color="#00D9FF" />
            <h1 className="text-5xl font-bold bg-gradient-to-r from-cyan-500 to-blue-500 bg-clip-text text-transparent">
              Healthcare AI System
            </h1>
          </div>
          <p className="text-gray-600 text-lg">
            Complete icon set and ECG pulse wave animations for your medical AI application
          </p>
        </motion.div>

        {/* ECG Pulse Wave Demo Section */}
        <motion.section 
          className="mb-16"
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.6, delay: 0.2 }}
        >
          <h2 className="text-3xl font-bold text-gray-900 mb-6">ECG Pulse Wave Loading Animation</h2>
          
          {/* Color Selector */}
          <div className="flex items-center gap-2 mb-6">
            <span className="text-sm font-medium text-gray-700">Choose Color:</span>
            {colors.map((color) => (
              <button
                key={color.value}
                onClick={() => setActiveColor(color.value)}
                className={`px-4 py-2 rounded-lg text-sm font-medium transition-all ${
                  activeColor === color.value
                    ? "bg-gray-900 text-white shadow-lg scale-105"
                    : "bg-white text-gray-700 hover:bg-gray-100 border border-gray-200"
                }`}
              >
                {color.name}
              </button>
            ))}
          </div>

          {/* ECG Variations */}
          <div className="grid grid-cols-1 gap-6">
            {/* Dark Background */}
            <div className="bg-gradient-to-br from-gray-900 to-gray-800 rounded-xl p-8 border border-gray-700">
              <p className="text-sm text-gray-400 mb-4">Dark Theme - Ideal for medical displays</p>
              <ECGPulseWave color={activeColor} duration={2} />
            </div>

            {/* Light Background */}
            <div className="bg-white rounded-xl p-8 border border-gray-200">
              <p className="text-sm text-gray-500 mb-4">Light Theme - Clean and minimal</p>
              <ECGPulseWave color={activeColor} duration={2} />
            </div>

            {/* With Context */}
            <LoadingStateDemo />
          </div>
        </motion.section>

        {/* Icons Grid */}
        <motion.section
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.6, delay: 0.4 }}
        >
          <h2 className="text-3xl font-bold text-gray-900 mb-6">Healthcare Icon Set</h2>
          <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 gap-6">
            {icons.map((item, index) => (
              <motion.div
                key={item.label}
                initial={{ opacity: 0, scale: 0.8 }}
                animate={{ opacity: 1, scale: 1 }}
                transition={{ duration: 0.3, delay: 0.5 + index * 0.05 }}
              >
                <IconShowcase
                  icon={item.icon}
                  label={item.label}
                  description={item.description}
                />
              </motion.div>
            ))}
          </div>
        </motion.section>

        {/* Usage Examples */}
        <motion.section 
          className="mt-16 bg-white rounded-xl p-8 border border-gray-200"
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.6, delay: 0.8 }}
        >
          <h2 className="text-2xl font-bold text-gray-900 mb-4">Quick Integration Examples</h2>
          
          <div className="space-y-6">
            {/* Example 1 */}
            <div>
              <h3 className="text-lg font-semibold text-gray-800 mb-2">ECG Pulse Wave</h3>
              <div className="bg-gray-50 rounded-lg p-4 font-mono text-sm overflow-x-auto">
                <code className="text-gray-800">
                  {`import { ECGPulseWave } from "@/app/components/ECGPulseWave";\n\n<ECGPulseWave color="#00D9FF" duration={2} />`}
                </code>
              </div>
            </div>

            {/* Example 2 */}
            <div>
              <h3 className="text-lg font-semibold text-gray-800 mb-2">Healthcare Icons</h3>
              <div className="bg-gray-50 rounded-lg p-4 font-mono text-sm overflow-x-auto">
                <code className="text-gray-800">
                  {`import { VoiceDiagnosisIcon, HeartRateIcon } from "@/app/components/HealthcareIcons";\n\n<VoiceDiagnosisIcon className="w-6 h-6" color="#00D9FF" />\n<HeartRateIcon className="w-8 h-8" color="#10B981" />`}
                </code>
              </div>
            </div>
          </div>
        </motion.section>

        {/* Feature Cards */}
        <motion.section 
          className="mt-12 grid grid-cols-1 md:grid-cols-3 gap-6"
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.6, delay: 1 }}
        >
          <div className="bg-gradient-to-br from-cyan-50 to-blue-50 rounded-xl p-6 border border-cyan-200">
            <VoiceDiagnosisIcon className="w-10 h-10 mb-4" color="#06B6D4" />
            <h3 className="font-bold text-gray-900 mb-2">Voice Diagnosis</h3>
            <p className="text-sm text-gray-600">
              Enable hands-free medical consultations with voice-activated AI diagnosis
            </p>
          </div>

          <div className="bg-gradient-to-br from-green-50 to-emerald-50 rounded-xl p-6 border border-green-200">
            <UploadReportIcon className="w-10 h-10 mb-4" color="#10B981" />
            <h3 className="font-bold text-gray-900 mb-2">Upload Reports</h3>
            <p className="text-sm text-gray-600">
              Seamlessly import and analyze medical documents, lab results, and imaging
            </p>
          </div>

          <div className="bg-gradient-to-br from-purple-50 to-violet-50 rounded-xl p-6 border border-purple-200">
            <AcousticDiagnosisIcon className="w-10 h-10 mb-4" color="#8B5CF6" />
            <h3 className="font-bold text-gray-900 mb-2">Acoustic Diagnosis</h3>
            <p className="text-sm text-gray-600">
              Advanced sound-based analysis for respiratory and cardiac conditions
            </p>
          </div>
        </motion.section>
      </div>
    </div>
  );
}
