package com.dgimatov.foursqplacesdemo.model

import io.reactivex.Observable
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Implementation of [FoursquareApiRepo]
 */
class FoursquareApiRepoImpl : FoursquareApiRepo {

    private val logging = HttpLoggingInterceptor()

    private val httpClient = OkHttpClient.Builder()
        .readTimeout(HTTP_READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .addInterceptor(logging)
        .connectTimeout(HTTP_CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .build()

    private val foursquareApi: FoursquareApi = Retrofit.Builder()
        .client(httpClient)
        .baseUrl(FOURSQUARE_HOST)
        .addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(FoursquareApi::class.java)

    init {
        logging.level = HttpLoggingInterceptor.Level.NONE
    }

    override fun getRestaurantsForLocation(
        latLng: LatLng,
        clientId: String,
        clientSecret: String
    ): Observable<FoursquareApiResponse> {
        return foursquareApi.getRestaurantsForLocation("${latLng.lat},${latLng.lng}", clientId, clientSecret)
    }


    companion object {

        private const val FOURSQUARE_HOST = "https://api.foursquare.com"

        private const val HTTP_READ_TIMEOUT_SECONDS = 30L

        private const val HTTP_CONNECT_TIMEOUT_SECONDS = 30L
    }
}