package com.dgimatov.foursqplacesdemo.model

import io.reactivex.Observable

/**
 * Repo for Foursquare Api Data
 */
interface FoursquareApiRepo {

    /**
     * Gives a list of restaurants close to given latlng bounds
     * @param northeast northeast
     * @param southwest southwest
     * @param client_id foursquare's client_id
     * @param client_secret foursquare's client_secret
     */
    fun getRestaurantsForBounds(
            northeast: LatLng,
            southwest: LatLng,
            clientId: String,
            clientSecret: String
    ): Observable<FoursquareSearchApiResponse>

    /**
     * Gives a detail info on given venue
     * @param id id of a venue
     * @param client_id foursquare's client_id
     * @param client_secret foursquare's client_secret
     */
    fun getRestaurantDetailInfo(
            id: String,
            clientId: String,
            clientSecret: String
    ): Observable<FoursquareVenueDetailApiResponse>
}