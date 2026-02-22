package com.spashtai.navigator.utils

import android.content.Context
import android.net.Uri
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

object FileUtils {
    fun getMultipartPart(context: Context, uri: Uri, partName: String): MultipartBody.Part? {
        val file = uriToFile(context, uri) ?: return null
        val requestFile = file.asRequestBody(context.contentResolver.getType(uri)?.toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(partName, file.name, requestFile)
    }

    private fun uriToFile(context: Context, uri: Uri): File? {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val tempFile = File(context.cacheDir, "temp_upload_${System.currentTimeMillis()}")
        val outputStream = FileOutputStream(tempFile)
        
        inputStream.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }
        return tempFile
    }
}
