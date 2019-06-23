package com.dgimatov.foursqplacesdemo.model

/**
 * FoursquareApi response model
 */
data class FoursquareSearchApiResponse(val response: VenuesWrapper)

/**
 * Wrapper around venues list
 */
data class VenuesWrapper(val venues: List<Venue>)