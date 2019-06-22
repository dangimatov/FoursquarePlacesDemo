package com.dgimatov.foursqplacesdemo.model

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Calls Foursquare Api to fetch list of restaurants for given latlng
 * @param ll latlng for the area around which we want a list of restaurants
 * @param client_id foursquare's client_id
 * @param client_secret foursquare's client_secret
 */
interface FoursquareApi {

    @GET("/v2/venues/search?categoryId=4d4b7105d754a06374d81259&limit=50&radius=500&v=&v=20190622")
    fun getRestaurantsForLocation(
        @Query("ll") coordinates: String,
        @Query("client_id") client_id: String,
        @Query("client_secret") client_secret: String
    ): Observable<FoursquareApiResponse>
}