package com.spashtai.navigator.data.remote

import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object RetrofitClient {
    private const val PHYSICAL_DEVICE_IP = "192.168.1.5" // Your laptop's local IP (Updated)
    private const val EMULATOR_HOST = "10.0.2.2"
    
    private val baseUrl: String
        get() = if (android.os.Build.MODEL.contains("sdk", ignoreCase = true) || 
                   android.os.Build.FINGERPRINT.contains("generic", ignoreCase = true)) {
            "http://$EMULATOR_HOST:8000/"
        } else {
            "http://$PHYSICAL_DEVICE_IP:8000/"
        }

    val apiService: ApiService by lazy {
        val okHttpClient = okhttp3.OkHttpClient.Builder()
            .connectTimeout(300, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(300, java.util.concurrent.TimeUnit.SECONDS)
            .writeTimeout(300, java.util.concurrent.TimeUnit.SECONDS)
            .build()

        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
