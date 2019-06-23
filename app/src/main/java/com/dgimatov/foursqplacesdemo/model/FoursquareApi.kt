package com.dgimatov.foursqplacesdemo.model

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit interfaces for Foursquare API
 */
interface FoursquareApi {

    /**
     * Calls Foursquare Api to fetch list of restaurants for given latlng bounds
     * @param ll latlng for the area around which we want a list of restaurants
     * @param client_id foursquare's client_id
     * @param client_secret foursquare's client_secret
     */
    @GET("/v2/venues/search?categoryId=4d4b7105d754a06374d81259&limit=50&intent=browse&v=20190622")
    fun getRestaurantsForBounds(
            @Query("ne") northeast: String,
            @Query("sw") southwest: String,
            @Query("client_id") client_id: String,
            @Query("client_secret") client_secret: String
    ): Observable<FoursquareSearchApiResponse>

    /**
     * Calls Foursquare Api to fetch venue details
     * This is a 'Premium call' which has a limit of 50 calls a day on FS side
     * @param ll latlng for the area around which we want a list of restaurants
     * @param client_id foursquare's client_id
     * @param client_secret foursquare's client_secret
     */
    @GET("/v2/venues/{venueId}?v=20190622")
    fun getVenueDetail(
            @Path("venueId") venueId: String,
            @Query("client_id") client_id: String,
            @Query("client_secret") client_secret: String
    ): Observable<FoursquareVenueDetailApiResponse>
}