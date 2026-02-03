import { motion } from "motion/react";

interface ECGPulseWaveProps {
  className?: string;
  color?: string;
  duration?: number;
}

export function ECGPulseWave({ 
  className = "", 
  color = "#00D9FF",
  duration = 2
}: ECGPulseWaveProps) {
  // ECG path data - represents a typical heartbeat pattern
  const ecgPath = "M 0 50 L 20 50 L 25 50 L 28 20 L 30 80 L 32 40 L 35 50 L 45 50 L 50 30 L 52 50 L 70 50 L 100 50";
  
  return (
    <div className={`relative w-full h-16 overflow-hidden ${className}`}>
      {/* Grid background */}
      <svg 
        className="absolute inset-0 w-full h-full opacity-10" 
        xmlns="http://www.w3.org/2000/svg"
      >
        <defs>
          <pattern 
            id="ecg-grid" 
            width="10" 
            height="10" 
            patternUnits="userSpaceOnUse"
          >
            <path 
              d="M 10 0 L 0 0 0 10" 
              fill="none" 
              stroke={color} 
              strokeWidth="0.5"
            />
          </pattern>
        </defs>
        <rect width="100%" height="100%" fill="url(#ecg-grid)" />
      </svg>

      {/* Animated ECG line */}
      <svg 
        className="absolute inset-0 w-full h-full" 
        viewBox="0 0 200 100" 
        preserveAspectRatio="none"
        xmlns="http://www.w3.org/2000/svg"
      >
        <defs>
          <linearGradient id="ecg-gradient" x1="0%" y1="0%" x2="100%" y2="0%">
            <stop offset="0%" stopColor={color} stopOpacity="0" />
            <stop offset="20%" stopColor={color} stopOpacity="1" />
            <stop offset="80%" stopColor={color} stopOpacity="1" />
            <stop offset="100%" stopColor={color} stopOpacity="0" />
          </linearGradient>
          
          <filter id="ecg-glow">
            <feGaussianBlur stdDeviation="1.5" result="coloredBlur"/>
            <feMerge>
              <feMergeNode in="coloredBlur"/>
              <feMergeNode in="SourceGraphic"/>
            </feMerge>
          </filter>
        </defs>

        {/* Main ECG wave */}
        <motion.path
          d={ecgPath}
          fill="none"
          stroke="url(#ecg-gradient)"
          strokeWidth="2"
          strokeLinecap="round"
          strokeLinejoin="round"
          filter="url(#ecg-glow)"
          initial={{ pathLength: 0, x: -100 }}
          animate={{ 
            pathLength: [0, 1, 1, 0],
            x: [-100, 0, 100, 200]
          }}
          transition={{
            duration: duration,
            repeat: Infinity,
            ease: "linear",
            times: [0, 0.3, 0.7, 1]
          }}
        />

        {/* Secondary trailing wave for effect */}
        <motion.path
          d={ecgPath}
          fill="none"
          stroke={color}
          strokeWidth="1.5"
          strokeLinecap="round"
          strokeLinejoin="round"
          opacity={0.3}
          initial={{ pathLength: 0, x: -100 }}
          animate={{ 
            pathLength: [0, 1, 1, 0],
            x: [-100, 0, 100, 200]
          }}
          transition={{
            duration: duration,
            repeat: Infinity,
            ease: "linear",
            times: [0, 0.3, 0.7, 1],
            delay: 0.1
          }}
        />
      </svg>

      {/* Glowing dot at the end */}
      <svg 
        className="absolute inset-0 w-full h-full" 
        viewBox="0 0 200 100" 
        preserveAspectRatio="none"
        xmlns="http://www.w3.org/2000/svg"
      >
        <motion.circle
          r="3"
          fill={color}
          filter="url(#ecg-glow)"
          initial={{ cx: -10, cy: 50 }}
          animate={{ 
            cx: [20, 35, 52, 210],
            cy: [50, 50, 50, 50]
          }}
          transition={{
            duration: duration,
            repeat: Infinity,
            ease: "linear"
          }}
        />
      </svg>
    </div>
  );
}
