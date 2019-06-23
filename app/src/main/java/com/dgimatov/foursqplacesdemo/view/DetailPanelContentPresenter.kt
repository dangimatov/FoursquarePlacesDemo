package com.dgimatov.foursqplacesdemo.view

import com.dgimatov.foursqplacesdemo.model.Venue

/**
 * Presenter for [DetailView]
 */
interface DetailPanelContentPresenter {

    /**
     * No need to manage state anymore
     */
    fun onStop()

    /**
     * New restaurant marker was clicked
     */
    fun newRestaurantClicked(view: DetailView, restaurant: Venue)
}