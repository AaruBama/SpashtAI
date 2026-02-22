package com.spashtai.navigator.ui.components.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

// Voice Diagnosis Icon - Microphone
fun voiceDiagnosisIcon(color: Color = Color.Black): ImageVector {
    return ImageVector.Builder(
        name = "VoiceDiagnosis",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            fill = null,
            stroke = SolidColor(color),
            strokeLineWidth = 1.5f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(12f, 2f)
            curveTo(10.34f, 2f, 9f, 3.34f, 9f, 5f)
            verticalLineTo(12f)
            curveTo(9f, 13.66f, 10.34f, 15f, 12f, 15f)
            curveTo(13.66f, 15f, 15f, 13.66f, 15f, 12f)
            verticalLineTo(5f)
            curveTo(15f, 3.34f, 13.66f, 2f, 12f, 2f)
            close()
            moveTo(19f, 10f)
            verticalLineTo(12f)
            curveTo(19f, 15.866f, 15.866f, 19f, 12f, 19f)
            curveTo(8.134f, 19f, 5f, 15.866f, 5f, 12f)
            verticalLineTo(10f)
            moveTo(12f, 19f)
            verticalLineTo(22f)
            moveTo(8f, 22f)
            horizontalLineTo(16f)
        }
    }.build()
}

// Upload Report Icon - Document with upload arrow
fun uploadReportIcon(color: Color = Color.Black): ImageVector {
    return ImageVector.Builder(
        name = "UploadReport",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            fill = null,
            stroke = SolidColor(color),
            strokeLineWidth = 1.5f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(14f, 3f)
            verticalLineTo(7f)
            curveTo(14f, 7.265f, 14.105f, 7.52f, 14.293f, 7.707f)
            curveTo(14.48f, 7.895f, 14.735f, 8f, 15f, 8f)
            horizontalLineTo(19f)
            moveTo(17f, 21f)
            horizontalLineTo(7f)
            curveTo(6.47f, 21f, 5.961f, 20.789f, 5.586f, 20.414f)
            curveTo(5.211f, 20.039f, 5f, 19.53f, 5f, 19f)
            verticalLineTo(5f)
            curveTo(5f, 4.47f, 5.211f, 3.961f, 5.586f, 3.586f)
            curveTo(5.961f, 3.211f, 6.47f, 3f, 7f, 3f)
            horizontalLineTo(14f)
            lineTo(19f, 8f)
            verticalLineTo(19f)
            curveTo(19f, 19.53f, 18.789f, 20.039f, 18.414f, 20.414f)
            curveTo(18.039f, 20.789f, 17.53f, 21f, 17f, 21f)
            close()
            moveTo(12f, 17f)
            verticalLineTo(11f)
            moveTo(9f, 14f)
            lineTo(12f, 11f)
            lineTo(15f, 14f)
        }
    }.build()
}

// Acoustic Diagnosis Icon - Stethoscope
fun acousticDiagnosisIcon(color: Color = Color.Black): ImageVector {
    return ImageVector.Builder(
        name = "AcousticDiagnosis",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            fill = null,
            stroke = SolidColor(color),
            strokeLineWidth = 1.5f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(6f, 3f)
            curveTo(6f, 2.448f, 6.448f, 2f, 7f, 2f)
            curveTo(7.552f, 2f, 8f, 2.448f, 8f, 3f)
            verticalLineTo(4f)
            curveTo(8f, 7.314f, 10.686f, 10f, 14f, 10f)
            horizontalLineTo(16f)
            verticalLineTo(12f)
            curveTo(16f, 15.866f, 12.866f, 19f, 9f, 19f)
            curveTo(5.134f, 19f, 2f, 15.866f, 2f, 12f)
            verticalLineTo(11f)
            moveTo(18f, 3f)
            curveTo(18f, 2.448f, 18.448f, 2f, 19f, 2f)
            curveTo(19.552f, 2f, 20f, 2.448f, 20f, 3f)
            verticalLineTo(4f)
            curveTo(20f, 7.314f, 17.314f, 10f, 14f, 10f)
        }
        path(
            fill = null,
            stroke = SolidColor(color),
            strokeLineWidth = 1.5f
        ) {
            moveTo(21f, 15f)
            curveTo(21f, 16.105f, 20.105f, 17f, 19f, 17f)
            curveTo(17.895f, 17f, 17f, 16.105f, 17f, 15f)
            curveTo(17f, 13.895f, 17.895f, 13f, 19f, 13f)
            curveTo(20.105f, 13f, 21f, 13.895f, 21f, 15f)
            close()
        }
        path(
            fill = null,
            stroke = SolidColor(color),
            strokeLineWidth = 1.5f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(19f, 17f)
            verticalLineTo(19f)
            curveTo(19f, 20.105f, 18.105f, 21f, 17f, 21f)
            horizontalLineTo(15f)
        }
    }.build()
}

// Heart Rate Icon - Heart with ECG wave
fun heartRateIcon(color: Color = Color.Black): ImageVector {
    return ImageVector.Builder(
        name = "HeartRate",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            fill = null,
            stroke = SolidColor(color),
            strokeLineWidth = 1.5f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(3f, 12f)
            horizontalLineTo(7f)
            lineTo(10f, 6f)
            lineTo(14f, 18f)
            lineTo(17f, 12f)
            horizontalLineTo(21f)
            moveTo(20.84f, 4.61f)
            curveTo(20.329f, 4.099f, 19.723f, 3.694f, 19.055f, 3.417f)
            curveTo(18.388f, 3.141f, 17.673f, 2.998f, 16.95f, 2.998f)
            curveTo(16.228f, 2.998f, 15.512f, 3.141f, 14.845f, 3.417f)
            curveTo(14.177f, 3.694f, 13.571f, 4.099f, 13.06f, 4.61f)
            lineTo(12f, 5.67f)
            lineTo(10.94f, 4.61f)
            curveTo(9.908f, 3.578f, 8.509f, 2.999f, 7.05f, 2.999f)
            curveTo(5.591f, 2.999f, 4.192f, 3.578f, 3.16f, 4.61f)
            curveTo(2.128f, 5.642f, 1.549f, 7.041f, 1.549f, 8.5f)
            curveTo(1.549f, 9.959f, 2.128f, 11.358f, 3.16f, 12.39f)
            lineTo(4.22f, 13.45f)
            lineTo(12f, 21.23f)
            lineTo(19.78f, 13.45f)
            lineTo(20.84f, 12.39f)
            curveTo(21.351f, 11.879f, 21.756f, 11.273f, 22.033f, 10.605f)
            curveTo(22.31f, 9.938f, 22.452f, 9.223f, 22.452f, 8.5f)
            curveTo(22.452f, 7.778f, 22.31f, 7.062f, 22.033f, 6.395f)
            curveTo(21.756f, 5.727f, 21.351f, 5.121f, 20.84f, 4.61f)
            close()
        }
    }.build()
}

// AI Brain Icon - Brain
fun aiBrainIcon(color: Color = Color.Black): ImageVector {
    return ImageVector.Builder(
        name = "AIBrain",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            fill = null,
            stroke = SolidColor(color),
            strokeLineWidth = 1.5f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(9.5f, 2f)
            curveTo(8.97f, 2f, 8.461f, 2.211f, 8.086f, 2.586f)
            curveTo(7.711f, 2.961f, 7.5f, 3.47f, 7.5f, 4f)
            curveTo(7.5f, 4.53f, 7.711f, 5.039f, 8.086f, 5.414f)
            curveTo(8.461f, 5.789f, 8.97f, 6f, 9.5f, 6f)
            moveTo(14.5f, 2f)
            curveTo(15.03f, 2f, 15.539f, 2.211f, 15.914f, 2.586f)
            curveTo(16.289f, 2.961f, 16.5f, 3.47f, 16.5f, 4f)
            curveTo(16.5f, 4.53f, 16.289f, 5.039f, 15.914f, 5.414f)
            curveTo(15.539f, 5.789f, 15.03f, 6f, 14.5f, 6f)
            moveTo(12f, 2f)
            verticalLineTo(6f)
            curveTo(9.613f, 6f, 7.324f, 6.948f, 5.636f, 8.636f)
            curveTo(3.948f, 10.324f, 3f, 12.613f, 3f, 15f)
            verticalLineTo(16f)
            curveTo(3f, 16.53f, 3.211f, 17.039f, 3.586f, 17.414f)
            curveTo(3.961f, 17.789f, 4.47f, 18f, 5f, 18f)
            curveTo(5.53f, 18f, 6.039f, 17.789f, 6.414f, 17.414f)
            curveTo(6.789f, 17.039f, 7f, 16.53f, 7f, 16f)
            verticalLineTo(15f)
            moveTo(12f, 6f)
            curveTo(14.387f, 6f, 16.676f, 6.948f, 18.364f, 8.636f)
            curveTo(20.052f, 10.324f, 21f, 12.613f, 21f, 15f)
            verticalLineTo(16f)
            curveTo(21f, 16.53f, 20.789f, 17.039f, 20.414f, 17.414f)
            curveTo(20.039f, 17.789f, 19.53f, 18f, 19f, 18f)
            curveTo(18.47f, 18f, 17.961f, 17.789f, 17.586f, 17.414f)
            curveTo(17.211f, 17.039f, 17f, 16.53f, 17f, 16f)
            verticalLineTo(15f)
            moveTo(7f, 15f)
            curveTo(7f, 16.061f, 6.579f, 17.078f, 5.828f, 17.828f)
            curveTo(5.078f, 18.579f, 4.061f, 19f, 3f, 19f)
            verticalLineTo(22f)
            horizontalLineTo(21f)
            verticalLineTo(19f)
            curveTo(19.939f, 19f, 18.922f, 18.579f, 18.172f, 17.828f)
            curveTo(17.421f, 17.078f, 17f, 16.061f, 17f, 15f)
        }
    }.build()
}

// Scan Analyze Icon - Scan frame
fun scanAnalyzeIcon(color: Color = Color.Black): ImageVector {
    return ImageVector.Builder(
        name = "ScanAnalyze",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            fill = null,
            stroke = SolidColor(color),
            strokeLineWidth = 1.5f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(3f, 7f)
            verticalLineTo(5f)
            curveTo(3f, 3.895f, 3.895f, 3f, 5f, 3f)
            horizontalLineTo(7f)
            moveTo(21f, 7f)
            verticalLineTo(5f)
            curveTo(21f, 3.895f, 20.105f, 3f, 19f, 3f)
            horizontalLineTo(17f)
            moveTo(3f, 17f)
            verticalLineTo(19f)
            curveTo(3f, 20.105f, 3.895f, 21f, 5f, 21f)
            horizontalLineTo(7f)
            moveTo(21f, 17f)
            verticalLineTo(19f)
            curveTo(21f, 20.105f, 20.105f, 21f, 19f, 21f)
            horizontalLineTo(17f)
        }
        path(
            fill = null,
            stroke = SolidColor(color),
            strokeLineWidth = 2f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(3f, 12f)
            horizontalLineTo(21f)
        }
    }.build()
}

// Medical Records Icon - Document with medical cross
fun medicalRecordsIcon(color: Color = Color.Black): ImageVector {
    return ImageVector.Builder(
        name = "MedicalRecords",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            fill = null,
            stroke = SolidColor(color),
            strokeLineWidth = 1.5f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(4f, 5f)
            curveTo(4f, 3.895f, 4.895f, 3f, 6f, 3f)
            horizontalLineTo(18f)
            curveTo(19.105f, 3f, 20f, 3.895f, 20f, 5f)
            verticalLineTo(19f)
            curveTo(20f, 20.105f, 19.105f, 21f, 18f, 21f)
            horizontalLineTo(6f)
            curveTo(4.895f, 21f, 4f, 20.105f, 4f, 19f)
            close()
            moveTo(12f, 7f)
            verticalLineTo(13f)
            moveTo(9f, 10f)
            horizontalLineTo(15f)
            moveTo(8f, 17f)
            horizontalLineTo(16f)
        }
    }.build()
}
