package com.dgimatov.foursqplacesdemo.domain

import android.util.Log
import com.dgimatov.foursqplacesdemo.model.FoursquareApiRepo
import com.dgimatov.foursqplacesdemo.model.LatLng
import com.dgimatov.foursqplacesdemo.model.UserLocationRepo
import com.dgimatov.foursqplacesdemo.view.MapPresenter
import com.dgimatov.foursqplacesdemo.view.MapState
import com.dgimatov.foursqplacesdemo.view.MapView
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.BehaviorSubject

/**
 * Interactor responsible for any state change happening on a map
 * by any moment. Contains all the business logic to manage map screen
 */
class MapInteractor(
    private val userLocationRepo: UserLocationRepo,
    private val foursquareApiRepo: FoursquareApiRepo,
    private val foursquareClientId: String,
    private val foursquareClientSecret: String,
    val scheduler: Scheduler
) : MapPresenter {

    private val mapIsReadySubject = BehaviorSubject.create<Unit>()

    private val compositeDisposable = CompositeDisposable()

    private lateinit var view: MapView

    private fun state(): Observable<MapState> {
        return Observable.combineLatest(
            mapIsReadySubject,
            userLocationRepo.userLocation(),
            BiFunction<Unit, LatLng, LatLng> { _, latlng -> latlng })
            .doOnNext { view.updateState(MapState.AnimateToLocation(it)) }
            .switchMap { latlng ->
                foursquareApiRepo.getRestaurantsForLocation(latlng, foursquareClientId, foursquareClientSecret)
                    .map { it.response.venues }
                    .map { MapState.AddRestaurants(it) as MapState }
            }
            .onErrorReturn { MapState.Error(it) }
    }

    override fun onStart(mapView: MapView) {
        view = mapView

        compositeDisposable.add(
            state()
                .observeOn(scheduler)
                .subscribe(
                    { view.updateState(it) },
                    { Log.i("test_", "error happened in state subscription: $it") }
                )
        )
    }

    override fun onStop() {
        compositeDisposable.clear()
    }

    override fun permissionChanged() {
        userLocationRepo.permissionChanged()
    }

    override fun mapIsReady() {
        mapIsReadySubject.onNext(Unit)
    }
}