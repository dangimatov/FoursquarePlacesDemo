package com.dgimatov.foursqplacesdemo.model

/**
 * POJO of a response
 */
data class FoursquareVenueDetailApiResponse(
        val response: FoursquareVenueDetailsWrapper
)

/**
 * POJO of a venue detail's wrapper
 */
data class FoursquareVenueDetailsWrapper(
        val venue: VenueDetails
)

/**
 * Model for venue's details
 */
data class VenueDetails(
        val id: String,
        val name: String?,
        val phone: String?,
        val location: VenueLocation,
        val description: String?,
        val url: String?
)