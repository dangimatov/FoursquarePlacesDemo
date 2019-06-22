package com.dgimatov.foursqplacesdemo.model

import io.reactivex.Observable

/**
 * Repo which serves info about user's current location
 */
interface UserLocationRepo {

    /**
     * @return user's current location
     */
    fun userLocation(): Observable<LatLng>

    /**
     * tells repo that permissions changed
     */
    fun permissionChanged()

    companion object {
        const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
}