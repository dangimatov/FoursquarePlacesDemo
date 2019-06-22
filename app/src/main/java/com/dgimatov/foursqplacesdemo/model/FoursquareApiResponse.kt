package com.dgimatov.foursqplacesdemo.model

/**
 * FoursquareApi response model
 */
data class FoursquareApiResponse(val response: VenuesWrapper)

/**
 * Wrapper around venues list
 */
data class VenuesWrapper(val venues: List<Venue>)