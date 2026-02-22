import React from "react";

// Voice/Chat Diagnosis Icon
export function VoiceDiagnosisIcon({ className = "w-6 h-6", color = "currentColor" }: { className?: string; color?: string }) {
  return (
    <svg className={className} viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
      <path d="M12 2C10.34 2 9 3.34 9 5V12C9 13.66 10.34 15 12 15C13.66 15 15 13.66 15 12V5C15 3.34 13.66 2 12 2Z" 
        stroke={color} strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round" fill="none"/>
      <path d="M19 10V12C19 15.866 15.866 19 12 19C8.13401 19 5 15.866 5 12V10" 
        stroke={color} strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round"/>
      <path d="M12 19V22" 
        stroke={color} strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round"/>
      <path d="M8 22H16" 
        stroke={color} strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round"/>
      <circle cx="12" cy="5" r="1" fill={color}/>
      <circle cx="12" cy="8" r="1" fill={color}/>
      <circle cx="12" cy="11" r="1" fill={color}/>
    </svg>
  );
}

// Chat Message Icon
export function ChatDiagnosisIcon({ className = "w-6 h-6", color = "currentColor" }: { className?: string; color?: string }) {
  return (
    <svg className={className} viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
      <path d="M21 15C21 15.5304 20.7893 16.0391 20.4142 16.4142C20.0391 16.7893 19.5304 17 19 17H7L3 21V5C3 4.46957 3.21071 3.96086 3.58579 3.58579C3.96086 3.21071 4.46957 3 5 3H19C19.5304 3 20.0391 3.21071 20.4142 3.58579C20.7893 3.96086 21 4.46957 21 5V15Z" 
        stroke={color} strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round"/>
      <path d="M9 8H15" stroke={color} strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round"/>
      <path d="M9 12H13" stroke={color} strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round"/>
      <circle cx="16" cy="8" r="1" fill={color}/>
    </svg>
  );
}

// Upload Health Report Icon
export function UploadReportIcon({ className = "w-6 h-6", color = "currentColor" }: { className?: string; color?: string }) {
  return (
    <svg className={className} viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
      <path d="M14 3V7C14 7.26522 14.1054 7.51957 14.2929 7.70711C14.4804 7.89464 14.7348 8 15 8H19" 
        stroke={color} strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round"/>
      <path d="M17 21H7C6.46957 21 5.96086 20.7893 5.58579 20.4142C5.21071 20.0391 5 19.5304 5 19V5C5 4.46957 5.21071 3.96086 5.58579 3.58579C5.96086 3.21071 6.46957 3 7 3H14L19 8V19C19 19.5304 18.7893 20.0391 18.4142 20.4142C18.0391 20.7893 17.5304 21 17 21Z" 
        stroke={color} strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round"/>
      <path d="M12 17V11" stroke={color} strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round"/>
      <path d="M9 14L12 11L15 14" stroke={color} strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round"/>
    </svg>
  );
}

// Acoustic Diagnosis Icon (Stethoscope)
export function AcousticDiagnosisIcon({ className = "w-6 h-6", color = "currentColor" }: { className?: string; color?: string }) {
  return (
    <svg className={className} viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
      <path d="M6 3C6 2.44772 6.44772 2 7 2C7.55228 2 8 2.44772 8 3V4C8 7.31371 10.6863 10 14 10H16V12C16 15.866 12.866 19 9 19C5.13401 19 2 15.866 2 12V11" 
        stroke={color} strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round"/>
      <path d="M18 3C18 2.44772 18.4477 2 19 2C19.5523 2 20 2.44772 20 3V4C20 7.31371 17.3137 10 14 10" 
        stroke={color} strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round"/>
      <circle cx="19" cy="15" r="2" stroke={color} strokeWidth="1.5"/>
      <path d="M19 17V19C19 20.1046 18.1046 21 17 21H15" 
        stroke={color} strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round"/>
      <circle cx="9" cy="22" r="1" fill={color}/>
    </svg>
  );
}

// Heart Rate Monitor Icon
export function HeartRateIcon({ className = "w-6 h-6", color = "currentColor" }: { className?: string; color?: string }) {
  return (
    <svg className={className} viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
      <path d="M3 12H7L10 6L14 18L17 12H21" 
        stroke={color} strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round"/>
      <path d="M20.84 4.61C20.3292 4.099 19.7228 3.69364 19.0554 3.41708C18.3879 3.14052 17.6725 2.99817 16.95 2.99817C16.2275 2.99817 15.5121 3.14052 14.8446 3.41708C14.1772 3.69364 13.5708 4.099 13.06 4.61L12 5.67L10.94 4.61C9.90831 3.57831 8.50903 2.99871 7.05 2.99871C5.59096 2.99871 4.19169 3.57831 3.16 4.61C2.12831 5.64169 1.54871 7.04097 1.54871 8.5C1.54871 9.95903 2.12831 11.3583 3.16 12.39L4.22 13.45L12 21.23L19.78 13.45L20.84 12.39C21.351 11.8792 21.7564 11.2728 22.0329 10.6054C22.3095 9.93789 22.4518 9.2225 22.4518 8.5C22.4518 7.7775 22.3095 7.06211 22.0329 6.39464C21.7564 5.72718 21.351 5.12084 20.84 4.61Z" 
        stroke={color} strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round" fill="none"/>
    </svg>
  );
}

// Medical AI Brain Icon
export function AIBrainIcon({ className = "w-6 h-6", color = "currentColor" }: { className?: string; color?: string }) {
  return (
    <svg className={className} viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
      <path d="M9.5 2C8.96957 2 8.46086 2.21071 8.08579 2.58579C7.71071 2.96086 7.5 3.46957 7.5 4C7.5 4.53043 7.71071 5.03914 8.08579 5.41421C8.46086 5.78929 8.96957 6 9.5 6" 
        stroke={color} strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round"/>
      <path d="M14.5 2C15.0304 2 15.5391 2.21071 15.9142 2.58579C16.2893 2.96086 16.5 3.46957 16.5 4C16.5 4.53043 16.2893 5.03914 15.9142 5.41421C15.5391 5.78929 15.0304 6 14.5 6" 
        stroke={color} strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round"/>
      <path d="M12 2V6M12 6C9.61305 6 7.32387 6.94821 5.63604 8.63604C3.94821 10.3239 3 12.6131 3 15V16C3 16.5304 3.21071 17.0391 3.58579 17.4142C3.96086 17.7893 4.46957 18 5 18C5.53043 18 6.03914 17.7893 6.41421 17.4142C6.78929 17.0391 7 16.5304 7 16V15" 
        stroke={color} strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round"/>
      <path d="M12 6C14.3869 6 16.6761 6.94821 18.364 8.63604C20.0518 10.3239 21 12.6131 21 15V16C21 16.5304 20.7893 17.0391 20.4142 17.4142C20.0391 17.7893 19.5304 18 19 18C18.4696 18 17.9609 17.7893 17.5858 17.4142C17.2107 17.0391 17 16.5304 17 16V15" 
        stroke={color} strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round"/>
      <path d="M7 15C7 16.0609 6.57857 17.0783 5.82843 17.8284C5.07828 18.5786 4.06087 19 3 19V22H21V19C19.9391 19 18.9217 18.5786 18.1716 17.8284C17.4214 17.0783 17 16.0609 17 15" 
        stroke={color} strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round"/>
      <circle cx="9" cy="11" r="1" fill={color}/>
      <circle cx="15" cy="11" r="1" fill={color}/>
      <circle cx="12" cy="14" r="1" fill={color}/>
      <line x1="9" y1="11" x2="12" y2="14" stroke={color} strokeWidth="1" opacity="0.5"/>
      <line x1="15" y1="11" x2="12" y2="14" stroke={color} strokeWidth="1" opacity="0.5"/>
    </svg>
  );
}

// Scan/Analyze Icon
export function ScanAnalyzeIcon({ className = "w-6 h-6", color = "currentColor" }: { className?: string; color?: string }) {
  return (
    <svg className={className} viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
      <path d="M3 7V5C3 3.89543 3.89543 3 5 3H7" 
        stroke={color} strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round"/>
      <path d="M21 7V5C21 3.89543 20.1046 3 19 3H17" 
        stroke={color} strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round"/>
      <path d="M3 17V19C3 20.1046 3.89543 21 5 21H7" 
        stroke={color} strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round"/>
      <path d="M21 17V19C21 20.1046 20.1046 21 19 21H17" 
        stroke={color} strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round"/>
      <path d="M3 12H21" 
        stroke={color} strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
      <circle cx="12" cy="12" r="2" fill={color} opacity="0.5"/>
    </svg>
  );
}

// Medical History/Records Icon
export function MedicalRecordsIcon({ className = "w-6 h-6", color = "currentColor" }: { className?: string; color?: string }) {
  return (
    <svg className={className} viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
      <rect x="4" y="3" width="16" height="18" rx="2" 
        stroke={color} strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round"/>
      <path d="M12 7V13" stroke={color} strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round"/>
      <path d="M9 10H15" stroke={color} strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round"/>
      <path d="M8 17H16" stroke={color} strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round"/>
    </svg>
  );
}

// Pill/Medication Icon
export function MedicationIcon({ className = "w-6 h-6", color = "currentColor" }: { className?: string; color?: string }) {
  return (
    <svg className={className} viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
      <rect x="6" y="6" width="12" height="12" rx="2" 
        stroke={color} strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round" 
        transform="rotate(45 12 12)"/>
      <path d="M7.05 7.05L16.95 16.95" 
        stroke={color} strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round"/>
    </svg>
  );
}

// DNA/Genetic Icon
export function GeneticIcon({ className = "w-6 h-6", color = "currentColor" }: { className?: string; color?: string }) {
  return (
    <svg className={className} viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
      <path d="M7 3C7 3 9 4.5 9 8C9 11.5 7 13 7 13" 
        stroke={color} strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round"/>
      <path d="M17 3C17 3 15 4.5 15 8C15 11.5 17 13 17 13" 
        stroke={color} strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round"/>
      <path d="M7 11C7 11 9 12.5 9 16C9 19.5 7 21 7 21" 
        stroke={color} strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round"/>
      <path d="M17 11C17 11 15 12.5 15 16C15 19.5 17 21 17 21" 
        stroke={color} strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round"/>
      <line x1="9" y1="6" x2="15" y2="6" stroke={color} strokeWidth="1.5"/>
      <line x1="9" y1="10" x2="15" y2="10" stroke={color} strokeWidth="1.5"/>
      <line x1="9" y1="14" x2="15" y2="14" stroke={color} strokeWidth="1.5"/>
      <line x1="9" y1="18" x2="15" y2="18" stroke={color} strokeWidth="1.5"/>
    </svg>
  );
}

// Alert/Warning Icon
export function MedicalAlertIcon({ className = "w-6 h-6", color = "currentColor" }: { className?: string; color?: string }) {
  return (
    <svg className={className} viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
      <path d="M10.29 3.86L1.82 18C1.64537 18.3024 1.55298 18.6453 1.55199 18.9945C1.55099 19.3437 1.64141 19.6871 1.81442 19.9905C1.98743 20.2939 2.23675 20.5467 2.53773 20.7239C2.83871 20.901 3.18082 20.9962 3.53 21H20.47C20.8192 20.9962 21.1613 20.901 21.4623 20.7239C21.7633 20.5467 22.0126 20.2939 22.1856 19.9905C22.3586 19.6871 22.449 19.3437 22.448 18.9945C22.447 18.6453 22.3546 18.3024 22.18 18L13.71 3.86C13.5317 3.56611 13.2807 3.32312 12.9812 3.15448C12.6817 2.98585 12.3437 2.89725 12 2.89725C11.6563 2.89725 11.3183 2.98585 11.0188 3.15448C10.7193 3.32312 10.4683 3.56611 10.29 3.86Z" 
        stroke={color} strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round"/>
      <path d="M12 9V13" stroke={color} strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round"/>
      <circle cx="12" cy="17" r="1" fill={color}/>
    </svg>
  );
}
