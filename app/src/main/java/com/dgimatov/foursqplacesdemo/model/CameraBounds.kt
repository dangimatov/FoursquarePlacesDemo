package com.dgimatov.foursqplacesdemo.model

/**
 * Model for lat lng bounds which map camera currently has
 */
data class CameraBounds(val southwest: LatLng, val northeast: LatLng) {

    /**
     * Check if given boundsToCheck is not laying inside our bounds
     */
    fun notContainBounds(boundsToCheck: CameraBounds): Boolean {
        return !(contains(boundsToCheck.northeast) && contains(boundsToCheck.southwest))
    }

    private fun contains(pointToCheck: LatLng): Boolean {
        val lat = pointToCheck.lat
        return this.southwest.lat <= lat && lat <= this.northeast.lat && this.checkLongitude(pointToCheck.lng)
    }

    private fun checkLongitude(lng: Double): Boolean {
        return if (this.southwest.lng <= this.northeast.lng) {
            this.southwest.lng <= lng && lng <= this.northeast.lng
        } else {
            this.southwest.lng <= lng || lng <= this.northeast.lng
        }
    }

    /**
     * Gives expanded bounds. This implementation expands it half a width to all directions
     */
    fun addBufferZone(): CameraBounds {
        val northeast = this.northeast
        val southwest = this.southwest

        val latDistance = (northeast.lat - southwest.lat) / 2
        val longDistance = (northeast.lng - southwest.lng) / 2

        val expandedSouthwest =
            LatLng(Math.max(-90.0, southwest.lat - latDistance), Math.max(-180.0, southwest.lng - longDistance))
        val expandedNortheast =
            LatLng(Math.min(90.0, northeast.lat + latDistance), Math.min(180.0, northeast.lng + longDistance))

        return CameraBounds(
            expandedSouthwest,
            expandedNortheast
        )
    }
}