package com.dgimatov.foursqplacesdemo.view

/**
 * Presenter for [DetailView]
 */
interface DetailPanelContentPresenter {

    /**
     * No need to manage state anymore
     */
    fun onStop()

    /**
     * New restaurant marker was clocked
     */
    fun newRestaurantClicked(view: DetailView, id: String)
}