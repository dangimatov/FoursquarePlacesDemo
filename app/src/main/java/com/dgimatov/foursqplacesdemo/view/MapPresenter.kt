package com.dgimatov.foursqplacesdemo.view

/**
 * Presenter for [MapView]
 */
interface MapPresenter {

    /**
     * Tells our business logic that permission have been changed
     */
    fun permissionChanged()

    /**
     * Tells our business logic that map is ready by framework
     */
    fun mapIsReady()

    /**
     * View should not be managed anymore
     */
    fun onStop()

    /**
     * View should be managed again
     */
    fun onStart(mapView: MapView, foursquareClientId: String, foursquareClientSecret: String)
}