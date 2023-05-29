package com.example.android.marsphotos.network

import com.example.biobot.data.Message
import com.example.biobot.utils.Constants.BASE_URL
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit


/**
 * Build the Moshi object with Kotlin adapter factory that Retrofit will be using.
 */
val httpClient = OkHttpClient.Builder()
    .connectTimeout(900, TimeUnit.SECONDS) // Increase connection timeout to 30 seconds
    .readTimeout(900, TimeUnit.SECONDS) // Increase read timeout to 30 seconds
    .writeTimeout(900, TimeUnit.SECONDS) // Increase write timeout to 30 seconds
    .build()

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

/**
 * The Retrofit object with the Moshi converter.
 */
private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .client(httpClient)
    .baseUrl(BASE_URL)
    .build()

/**
 * A public interface that exposes the [getPhotos] method
 */
interface BertApiService {

    @GET("/runPython")
    suspend fun getResponse(
        @Query("query") query: String
    ): Message
}

/**
 * A public Api object that exposes the lazy-initialized Retrofit service
 */
object MarsApi {
    val retrofitService: BertApiService by lazy { retrofit.create(BertApiService::class.java) }
}