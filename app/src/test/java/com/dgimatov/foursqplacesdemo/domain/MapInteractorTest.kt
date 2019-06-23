package com.dgimatov.foursqplacesdemo.domain

import com.dgimatov.foursqplacesdemo.model.*
import com.dgimatov.foursqplacesdemo.view.MapState
import com.dgimatov.foursqplacesdemo.view.MapView
import com.nhaarman.mockitokotlin2.*
import io.reactivex.Observable.empty
import io.reactivex.Observable.just
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class MapInteractorTest {

    @Mock
    lateinit var userLocationRepoImpl: UserLocationRepoImpl

    @Mock
    lateinit var foursquareApiRepoImpl: FoursquareApiRepoImpl

    @Mock
    lateinit var view: MapView

    lateinit var testee: MapInteractor

    private val foursquareApiSubject = BehaviorSubject.create<FoursquareSearchApiResponse>()

    @Before
    fun setup() {
        whenever(userLocationRepoImpl.userLocation()).thenReturn(just(latLng0))

        whenever(foursquareApiRepoImpl.getRestaurantsForBounds(any(), any(), any())).thenReturn(
            foursquareApiSubject
        )
        testee = MapInteractor(
            userLocationRepoImpl, foursquareApiRepoImpl, "id", "client",
            Schedulers.trampoline()
        )
    }

    @Test
    fun `map is not there`() {
        //When
        testee.onStart(view)

        //Then
        val inOrder = inOrder(view)
        inOrder.verify(view, never()).updateState(any())
    }

    @Test
    fun `map is there and we animate - map is not idle yet`() {

        //When
        testee.onStart(view)
        testee.mapIsReady()

        //Then
        verify(view, only()).updateState(MapState.AnimateToLocation(LatLng(0.0, 0.0)))
    }

    @Test
    fun `map is there, we animate`() {

        //When
        testee.onStart(view)
        testee.mapIsReady()

        //Then
        verify(userLocationRepoImpl, only()).userLocation()
        verify(view, only()).updateState(MapState.AnimateToLocation(LatLng(0.0, 0.0)))
    }

    @Test
    fun `happy start flow - we animate to current location and load restaurants`() {

        //When
        testee.onStart(view)
        testee.mapIsReady()
        testee.mapIsIdle(bounds1, ZOOM_OK)
        foursquareApiSubject.onNext(foursquareSearchApiResponse)

        //Then
        verify(userLocationRepoImpl, times(1)).userLocation()
        verify(foursquareApiRepoImpl, times(1)).getRestaurantsForBounds(any(), any(), any())
        val inOrder = inOrder(view)
        inOrder.verify(view).updateState(MapState.AnimateToLocation(latLng0))
        inOrder.verify(view).updateState(MapState.AddRestaurants(restaurants))
    }

    @Test
    fun `user didnt give the permission for location and we are not animating automatically - ask user to zoom in`() {
        //Given
        whenever(userLocationRepoImpl.userLocation()).thenReturn(empty())

        //When
        testee.onStart(view)
        testee.mapIsReady()
        testee.mapIsIdle(bounds1, ZOOM_NOT_OK)
        testee.mapIsIdle(bounds1, ZOOM_OK)
        foursquareApiSubject.onNext(foursquareSearchApiResponse)

        //Then
        verify(userLocationRepoImpl, times(1)).userLocation()
        verify(foursquareApiRepoImpl, times(1)).getRestaurantsForBounds(any(), any(), any())
        val inOrder = inOrder(view)
        inOrder.verify(view, never()).updateState(MapState.AnimateToLocation(latLng0))
        inOrder.verify(view).updateState(MapState.ZoomInMore)
        inOrder.verify(view).updateState(MapState.AddRestaurants(restaurants))
    }

    @Test
    fun `happy flow + we move camera but not enough - we're still in buffer zone - so no additional request`() {

        //When
        testee.onStart(view)
        testee.mapIsReady()
        testee.mapIsIdle(bounds1, ZOOM_OK)
        foursquareApiSubject.onNext(foursquareSearchApiResponse)
        testee.mapIsIdle(bounds1_1, ZOOM_OK)

        //Then
        verify(userLocationRepoImpl, times(1)).userLocation()
        verify(foursquareApiRepoImpl, times(1)).getRestaurantsForBounds(bounds1.addBufferZone(), "id", "client")
        val inOrder = inOrder(view)
        inOrder.verify(view).updateState(MapState.AnimateToLocation(latLng0))
        inOrder.verify(view).updateState(MapState.AddRestaurants(restaurants))
    }

    @Test
    fun `happy flow + we this time move camera beyond buffer zone - so we have to make additional request`() {

        //When
        testee.onStart(view)
        testee.mapIsReady()
        testee.mapIsIdle(bounds1, ZOOM_OK)
        foursquareApiSubject.onNext(foursquareSearchApiResponse)
        testee.mapIsIdle(bounds2, ZOOM_OK)

        //Then
        verify(userLocationRepoImpl, times(1)).userLocation()
        verify(foursquareApiRepoImpl, times(1)).getRestaurantsForBounds(bounds1.addBufferZone(), "id", "client")
        verify(foursquareApiRepoImpl, times(1)).getRestaurantsForBounds(bounds2.addBufferZone(), "id", "client")
        val inOrder = inOrder(view)
        inOrder.verify(view).updateState(MapState.AnimateToLocation(latLng0))
        inOrder.verify(view, times(2)).updateState(MapState.AddRestaurants(restaurants))
    }

    @Test
    fun `happy flow + we zoom out and zoom in again`() {

        //When
        testee.onStart(view)
        testee.mapIsReady()
        testee.mapIsIdle(bounds1, ZOOM_OK)
        foursquareApiSubject.onNext(foursquareSearchApiResponse)
        testee.mapIsIdle(bounds2, ZOOM_NOT_OK)
        testee.mapIsIdle(bounds2, ZOOM_OK)

        //Then
        verify(userLocationRepoImpl, times(1)).userLocation()
        verify(foursquareApiRepoImpl, times(1)).getRestaurantsForBounds(bounds1.addBufferZone(), "id", "client")
        verify(foursquareApiRepoImpl, times(1)).getRestaurantsForBounds(bounds2.addBufferZone(), "id", "client")
        val inOrder = inOrder(view)
        inOrder.verify(view).updateState(MapState.AnimateToLocation(latLng0))
        inOrder.verify(view, times(1)).updateState(MapState.AddRestaurants(restaurants))
        verify(view).updateState(MapState.ZoomInMore)
        inOrder.verify(view, times(1)).updateState(MapState.AddRestaurants(restaurants))
    }

    @Test
    fun `unhappy flow - we animated to user location but `() {

        //When
        testee.onStart(view)
        testee.mapIsReady()
        testee.mapIsIdle(bounds1, ZOOM_OK)
        foursquareApiSubject.onNext(foursquareSearchApiResponse)
        testee.mapIsIdle(bounds2, ZOOM_OK)

        //Then
        verify(userLocationRepoImpl, times(1)).userLocation()
        verify(foursquareApiRepoImpl, times(1)).getRestaurantsForBounds(bounds1.addBufferZone(), "id", "client")
        verify(foursquareApiRepoImpl, times(1)).getRestaurantsForBounds(bounds2.addBufferZone(), "id", "client")
        val inOrder = inOrder(view)
        inOrder.verify(view).updateState(MapState.AnimateToLocation(latLng0))
        inOrder.verify(view, times(2)).updateState(MapState.AddRestaurants(restaurants))
    }

    companion object {
        val ZOOM_OK = MapInteractor.ZOOM_LEVEL_THRESHOLD + 1
        val ZOOM_NOT_OK = MapInteractor.ZOOM_LEVEL_THRESHOLD - 1
        val latLng0 = LatLng(0.0, 0.0)
        val latLng1 = LatLng(1.0, 1.0)
        val latLng1_1 = LatLng(1.1, 1.1)
        val latLng2 = LatLng(2.0, 2.0)
        val bounds1 = CameraBounds(latLng0, latLng1)
        val bounds1_1 = CameraBounds(latLng0, latLng1_1)
        val bounds2 = CameraBounds(latLng0, latLng2)

        val restaurants = listOf(Venue("1", "Yammy", VenueLocation("address", 0.0, 0.0, null)))

        val foursquareSearchApiResponse = FoursquareSearchApiResponse(
            VenuesWrapper(
                restaurants
            )
        )
    }
}