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
        val location: VenueLocation,
        val description: String?,
        val url: String?,
        val hours: Hours?,
        val contact: Contact?
)

/**
 * Model for open hours
 */
data class Hours(val status: String?)

/**
 * Model for open contacts details
 */
data class Contact(val phone: String?)