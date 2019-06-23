package com.dgimatov.foursqplacesdemo.view

import com.dgimatov.foursqplacesdemo.model.Venue
import com.dgimatov.foursqplacesdemo.model.VenueDetails

/**
 * General Detail View to show restaurant additional info
 */
interface DetailView {
    fun updateState(state: DetailViewContentState)
}

/**
 * All possible state of DetailView's content
 */
sealed class DetailViewContentState {

    /**
     * Details are loading
     */
    object Loading : DetailViewContentState()

    /**
     * Details loaded successfully. Show them
     */
    data class ShowDetails(val venueDetails: VenueDetails) : DetailViewContentState()

    /**
     * Error happened
     */
    data class Error(val exception: Throwable, val venue: Venue) : DetailViewContentState()

}