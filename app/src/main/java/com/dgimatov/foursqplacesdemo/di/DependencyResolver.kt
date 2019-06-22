package com.dgimatov.foursqplacesdemo.di

import android.app.Activity
import com.dgimatov.foursqplacesdemo.domain.MapInteractor
import com.dgimatov.foursqplacesdemo.model.FoursquareApiRepo
import com.dgimatov.foursqplacesdemo.model.FoursquareApiRepoImpl
import com.dgimatov.foursqplacesdemo.model.UserLocationRepo
import com.dgimatov.foursqplacesdemo.model.UserLocationRepoImpl
import com.dgimatov.foursqplacesdemo.view.MapPresenter
import com.google.android.gms.location.LocationServices
import io.reactivex.android.schedulers.AndroidSchedulers

/**
 * Resolves dependencies across activity lifescope
 */
class DependencyResolver(activity: Activity) {

    private val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity)

    private val userLocationRepo: UserLocationRepo = UserLocationRepoImpl(fusedLocationProviderClient, activity)

    private val foursquareApiRepo: FoursquareApiRepo = FoursquareApiRepoImpl()

    val mapPresenter: MapPresenter =
        MapInteractor(userLocationRepo, foursquareApiRepo, scheduler = AndroidSchedulers.mainThread())

}