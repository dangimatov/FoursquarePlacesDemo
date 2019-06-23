package com.dgimatov.foursqplacesdemo.view

import android.Manifest.permission.ACCESS_FINE_LOCATION
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED
import com.dgimatov.foursqplacesdemo.R
import com.dgimatov.foursqplacesdemo.di.DependencyResolver
import com.dgimatov.foursqplacesdemo.model.CameraBounds
import com.dgimatov.foursqplacesdemo.model.UserLocationRepo
import com.dgimatov.foursqplacesdemo.model.Venue
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_maps.*

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
            MapState.ZoomInMore -> showZoomMoreMessage()
        }
    }

    private fun showZoomMoreMessage() {
        zoomInMoreText.visibility = View.VISIBLE
    }

    private fun addRestaurants(restaurants: List<Venue>) {
        zoomInMoreText.visibility = View.GONE
        restaurants.forEach {
            val marker = map.addMarker(
                    MarkerOptions()
                            .position(LatLng(it.location.lat, it.location.lng))
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
                            .title(it.name)
            )
            marker.tag = it
        }
    }

    private fun showErrorDialog(e: Throwable) {
        zoomInMoreText.visibility = View.GONE
        AlertDialog.Builder(this)
                .setTitle("Error")
                .setCancelable(true)
                .setMessage("Something went wrong: ${e.message}")
                .setPositiveButton(android.R.string.ok) { dialog, _ -> dialog.dismiss() }
                .show()
    }

    private fun animateToCurrentLocation(latLng: LatLng) {
        zoomInMoreText.visibility = View.GONE
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

            }
        }
    }

    private fun setupMap() {
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun showDetailPanel(restaurant: Venue) {
        detailPanel?.let {
            it.updateState(DetailViewContentState.Loading)
            if (!it.isShowing) {
                it.show()
            }
            detailPresenter.newRestaurantClicked(it, restaurant)
        } ?: run {
            detailPanel = DetailPanel(this)
            showDetailPanel(restaurant)
        }

        detailPanel?.setOnDismissListener { }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.setOnMarkerClickListener {
            val venue = it.tag as Venue
            Log.i("test_", "marker clicked with id: " + venue.id as String?)
            showDetailPanel(venue)
            true
        }
        mapPresenter.mapIsReady()
        map.setOnCameraIdleListener {
            Log.i("test_", "map is idle zoom: ${map.cameraPosition.zoom}")
            mapPresenter.mapIsIdle(map.projection.visibleRegion.latLngBounds.toCameraBounds(), map.cameraPosition.zoom)
        }
    }

    private fun LatLngBounds.toCameraBounds(): CameraBounds {
        val northeast = com.dgimatov.foursqplacesdemo.model.LatLng(
                this.northeast.latitude,
                this.northeast.longitude
        )
        val southwest = com.dgimatov.foursqplacesdemo.model.LatLng(
                this.southwest.latitude,
                this.southwest.longitude
        )
        return CameraBounds(southwest = southwest, northeast = northeast)
    }

}
