package com.dgimatov.foursqplacesdemo.domain

import android.util.Log
import com.dgimatov.foursqplacesdemo.model.*
import com.dgimatov.foursqplacesdemo.view.MapPresenter
import com.dgimatov.foursqplacesdemo.view.MapState
import com.dgimatov.foursqplacesdemo.view.MapView
import io.reactivex.Observable
import io.reactivex.Observable.merge
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

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

    private val mapIsIdleSubject = PublishSubject.create<CameraBounds>()

    private val compositeDisposable = CompositeDisposable()

    private lateinit var view: MapView

    private var currentMarkerBounds: CameraBounds? = null

    private fun state(): Observable<MapState> {
        return merge(
                Observable.combineLatest(
                        mapIsReadySubject,
                        mapIsIdleSubject
                                .filter { currentMarkerBounds?.notContainBounds(it) ?: true },
                        BiFunction<Unit, CameraBounds, CameraBounds> { _, bounds -> bounds })
                        .switchMap { bounds ->
                            val expandedBounds = bounds.addBufferZone()
                            foursquareApiRepo.getRestaurantsForBounds(
                                    northeast = expandedBounds.northeast,
                                    southwest = expandedBounds.southwest,
                                    clientId = foursquareClientId,
                                    clientSecret = foursquareClientSecret)
                                    .map { it.response.venues }
                                    .map {
                                        currentMarkerBounds = expandedBounds
                                        MapState.AddRestaurants(it) as MapState
                                    }
                        },
                animateToCurrentLocation())
                .onErrorReturn { MapState.Error(it) }
    }

    private fun animateToCurrentLocation(): Observable<MapState> {
        return mapIsReadySubject
                .switchMap { userLocationRepo.userLocation() }
                .map { MapState.AnimateToLocation(it) }
    }

    override fun mapIsIdle(cameraBounds: CameraBounds) {
        mapIsIdleSubject.onNext(cameraBounds)
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