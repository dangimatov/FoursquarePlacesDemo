package com.dgimatov.foursqplacesdemo.view

import android.Manifest.permission.ACCESS_FINE_LOCATION
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED
import com.dgimatov.foursqplacesdemo.R
import com.dgimatov.foursqplacesdemo.model.UserLocationRepo
import com.dgimatov.foursqplacesdemo.model.UserLocationRepoImpl
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * Main activity which shows a map with restaurants on it
 */
class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var userLocationRepo: UserLocationRepo

    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        userLocationRepo = UserLocationRepoImpl(fusedLocationClient, this)

    }

    override fun onStart() {
        super.onStart()
        compositeDisposable.add(
            subscribeToUserLocation()
        )
    }

    private fun subscribeToUserLocation(): Disposable {
        return userLocationRepo.userLocation()
            .subscribe(
                { animateToCurrentLocation(LatLng(it.lat, it.lng)) },
                { Log.i("test_", "error on location subscription: $it") }
            )
    }

    override fun onStop() {
        super.onStop()
        compositeDisposable.clear()
    }

    private fun animateToCurrentLocation(latLng: LatLng) {
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17f)
        map.animateCamera(cameraUpdate)
        map.addMarker(
            MarkerOptions()
                .position(latLng)
        )
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == UserLocationRepo.LOCATION_PERMISSION_REQUEST_CODE && grantResults.isNotEmpty()) {
            if (grantResults[0] == PERMISSION_GRANTED) {
                userLocationRepo.permissionChanged()
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, ACCESS_FINE_LOCATION)) {
                //TODO bad choice
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
    }
}
