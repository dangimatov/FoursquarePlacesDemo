package com.dgimatov.foursqplacesdemo.model

import io.reactivex.Observable

/**
 * Repo for Foursquare Api Data
 */
interface FoursquareApiRepo {

    /**
     * Gives a list of restaurants close to given latlng
     * @param latLng for the area around which we want a list of restaurants
     * @param client_id foursquare's client_id
     * @param client_secret foursquare's client_secret
     */
    fun getRestaurantsForLocation(latLng: LatLng, clientId: String, clientSecret: String): Observable<FoursquareApiResponse>
}