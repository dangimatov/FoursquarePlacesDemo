package com.dgimatov.foursqplacesdemo.view

import com.dgimatov.foursqplacesdemo.model.LatLng
import com.dgimatov.foursqplacesdemo.model.Venue

/**
 * General map view
 */
interface MapView {

    /**
     * Update state to one of [MapState]
     */
    fun updateState(state: MapState)
}

/**
 * All possible states which map view can be in
 */
sealed class MapState {

    /**
     * Animate to a position and put marker on it
     */
    data class AnimateToLocation(val latLng: LatLng) : MapState()

    /**
     * Add restaurant markers on a map
     */
    data class AddRestaurants(val restaurants: List<Venue>) : MapState()

    /**
     * Show that error happened
     */
    data class Error(val exception: Throwable) : MapState()

}