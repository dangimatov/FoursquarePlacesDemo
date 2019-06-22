package com.dgimatov.foursqplacesdemo.model

/**
 * Model which represent a venue
 */
data class Venue(
    val id: String,
    val name: String?,
    val location: VenueLocation
)

/**
 * Custom foursquare's venue's location model
 */
data class VenueLocation(
    val address: String?,
    val lat: Double,
    val lng: Double
)