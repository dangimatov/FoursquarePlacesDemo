package com.dgimatov.foursqplacesdemo.model

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

/**
 * Implementation of [UserLocationRepo]
 */
open class UserLocationRepoImpl(
    private val fusedLocationProviderClient: FusedLocationProviderClient,
    private val activity: Activity
) : UserLocationRepo {
    private val lastKnownLocationSubject = BehaviorSubject.create<LatLng>()
    private val permissionChangedSubject = PublishSubject.create<Unit>()

    override fun userLocation(): Observable<LatLng> {
        return permissionChangedSubject
            .startWith(Unit)
            .switchMap {
                val hasLocationPermission = ActivityCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED

                if (hasLocationPermission) {
                    fusedLocationProviderClient.lastLocation.addOnSuccessListener {
                        Log.i("test_", "UserLocationRepoImpl location emission: $it")
                        lastKnownLocationSubject.onNext(LatLng(it.latitude, it.longitude))
                    }
                } else {
                    ActivityCompat.requestPermissions(
                        activity,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        UserLocationRepo.LOCATION_PERMISSION_REQUEST_CODE
                    )
                }
                lastKnownLocationSubject
            }
    }

    override fun permissionChanged() {
        permissionChangedSubject.onNext(Unit)
    }
}