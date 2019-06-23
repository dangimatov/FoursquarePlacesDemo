package com.dgimatov.foursqplacesdemo.view

import android.Manifest.permission.ACCESS_FINE_LOCATION
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED
import com.dgimatov.foursqplacesdemo.R
import com.dgimatov.foursqplacesdemo.di.DependencyResolver
import com.dgimatov.foursqplacesdemo.model.UserLocationRepo
import com.dgimatov.foursqplacesdemo.model.Venue
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

/**
 * Main activity which shows a map with restaurants on it
 */
class MapsActivity : AppCompatActivity(), OnMapReadyCallback, MapView {


    private lateinit var map: GoogleMap

    private lateinit var mapPresenter: MapPresenter
    private lateinit var detailPresenter: DetailPanelContentPresenter

    private var detailPanel: DetailPanel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        val dependencyResolver = DependencyResolver(this)
        mapPresenter = dependencyResolver.mapPresenter
        detailPresenter = dependencyResolver.detailViewContentPresenter

        setupMap()
    }

    override fun onStart() {
        super.onStart()
        mapPresenter.onStart(
                mapView = this
        )
    }

    override fun onStop() {
        super.onStop()
        mapPresenter.onStop()
        detailPresenter.onStop()
    }

    override fun updateState(state: MapState) {
        Log.i("test_", "MapView updateState: ${state.javaClass.simpleName}")
        when (state) {
            is MapState.AnimateToLocation -> animateToCurrentLocation(LatLng(state.latLng.lat, state.latLng.lng))
            is MapState.AddRestaurants -> addRestaurants(state.restaurants)
            is MapState.Error -> showErrorDialog(state.exception)
        }
    }

    private fun addRestaurants(restaurants: List<Venue>) {
        restaurants.forEach {
            val marker = map.addMarker(
                    MarkerOptions()
                            .position(LatLng(it.location.lat, it.location.lng))
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
                            .title(it.name)
            )
            marker.tag = it.id
        }
    }

    private fun showErrorDialog(e: Throwable) {
        AlertDialog.Builder(this)
                .setTitle("Error")
                .setCancelable(true)
                .setMessage("Something went wrong: ${e.message}")
                .setPositiveButton(android.R.string.ok) { dialog, _ -> dialog.dismiss() }
                .show()
    }

    private fun animateToCurrentLocation(latLng: LatLng) {
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17f)
        map.animateCamera(cameraUpdate)
        map.clear()
        map.addMarker(
                MarkerOptions()
                        .position(latLng)
                        .title("You're here")
        )
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == UserLocationRepo.LOCATION_PERMISSION_REQUEST_CODE && grantResults.isNotEmpty()) {
            if (grantResults[0] == PERMISSION_GRANTED) {
                mapPresenter.permissionChanged()
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, ACCESS_FINE_LOCATION)) {
                //TODO bad choice
            }
        }
    }

    private fun setupMap() {
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun showDetailPanel(id: String) {
        detailPanel?.let {
            it.updateState(DetailViewContentState.Loading)
            if (!it.isShowing) {
                it.show()
            }
            detailPresenter.newRestaurantClicked(it, id)
        } ?: run {
            detailPanel = DetailPanel(this)
            showDetailPanel(id)
        }

        detailPanel?.setOnDismissListener { }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.setOnMarkerClickListener {
            Log.i("test_", "marker clicked with id: " + it.tag as String?)
            showDetailPanel(it.tag as String)
            true
        }
        mapPresenter.mapIsReady()
    }
}
