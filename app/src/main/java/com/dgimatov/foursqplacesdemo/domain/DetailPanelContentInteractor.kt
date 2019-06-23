package com.dgimatov.foursqplacesdemo.domain

import android.util.Log
import com.dgimatov.foursqplacesdemo.model.FoursquareApiRepo
import com.dgimatov.foursqplacesdemo.model.Venue
import com.dgimatov.foursqplacesdemo.view.DetailPanelContentPresenter
import com.dgimatov.foursqplacesdemo.view.DetailView
import com.dgimatov.foursqplacesdemo.view.DetailViewContentState
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject

/**
 * Business logic responsible for managing content state for [DetailView]
 */
class DetailPanelContentInteractor(
    private val foursquareApiRepo: FoursquareApiRepo,
    private val foursquareClientId: String,
    private val foursquareClientSecret: String,
    private val scheduler: Scheduler
) : DetailPanelContentPresenter {

    private val newRestaurantClickedSubject = PublishSubject.create<Venue>()

    private var detailsDisposable: Disposable? = null

    private fun state(): Observable<DetailViewContentState> {
        return newRestaurantClickedSubject
            .switchMap { venue ->
                foursquareApiRepo.getRestaurantDetailInfo(
                    id = venue.id, clientId = foursquareClientId, clientSecret = foursquareClientSecret
                )
                    .map {
                        it.response.venue
                    }
                    .map { DetailViewContentState.ShowDetails(it) as DetailViewContentState }
                    .onErrorReturn { DetailViewContentState.Error(it, venue) }
            }

    }

    override fun newRestaurantClicked(view: DetailView, restaurant: Venue) {

        detailsDisposable?.let {
            if (!it.isDisposed) {
                newRestaurantClickedSubject.onNext(restaurant)
                return
            }
        }

        detailsDisposable = state()
            .observeOn(scheduler)
            .subscribe(
                { view.updateState(it) },
                { Log.i("test_", "error happened in venure details subscription") }
            )

        newRestaurantClickedSubject.onNext(restaurant)
    }

    override fun onStop() {
        detailsDisposable?.dispose()
    }
}