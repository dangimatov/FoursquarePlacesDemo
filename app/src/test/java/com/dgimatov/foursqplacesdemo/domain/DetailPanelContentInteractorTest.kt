package com.dgimatov.foursqplacesdemo.domain

import com.dgimatov.foursqplacesdemo.model.*
import com.dgimatov.foursqplacesdemo.view.DetailView
import com.dgimatov.foursqplacesdemo.view.DetailViewContentState
import com.nhaarman.mockitokotlin2.*
import io.reactivex.Observable
import io.reactivex.Observable.just
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class DetailPanelContentInteractorTest {

    @Mock
    lateinit var foursquareApiRepoImpl: FoursquareApiRepoImpl

    @Mock
    lateinit var view: DetailView

    lateinit var testee: DetailPanelContentInteractor

    @Before
    fun setup() {
        testee = DetailPanelContentInteractor(
                foursquareApiRepoImpl, "clientId", "clientSecret", Schedulers.trampoline()
        )
    }

    @Test
    fun `no one clicks on a marker - no requests have been made, no state has been emitted`() {
        //When
        //Then
        verify(foursquareApiRepoImpl, never()).getRestaurantDetailInfo(any(), any(), any())
        verify(view, never()).updateState(any())
    }

    @Test
    fun `happy case - someone clicked on a marker - we fetch venue details`() {
        //Given
        whenever(foursquareApiRepoImpl.getRestaurantDetailInfo("venueId", "clientId", "clientSecret"))
                .thenReturn(just(response))

        //When
        testee.newRestaurantClicked(view, venue)

        //Then
        verify(foursquareApiRepoImpl).getRestaurantDetailInfo("venueId", "clientId", "clientSecret")
        verify(view, only()).updateState(DetailViewContentState.ShowDetails(venueDetails))
    }

    @Test
    fun `unhappy case - error happened`() {
        val exception = RuntimeException()
        //Given
        whenever(foursquareApiRepoImpl.getRestaurantDetailInfo("venueId", "clientId", "clientSecret"))
                .thenReturn(Observable.error(exception))

        //When
        testee.newRestaurantClicked(view, venue)

        //Then
        verify(foursquareApiRepoImpl).getRestaurantDetailInfo("venueId", "clientId", "clientSecret")
        verify(view, only()).updateState(DetailViewContentState.Error(exception, venue))
    }

    companion object {
        val venue = Venue("venueId", "Yammy", VenueLocation("address", 0.0, 0.0, null))

        private val venueDetails = VenueDetails(
                "venueId",
                "name",
                null,
                VenueLocation(null, 0.0, 0.0, null),
                "description",
                "url"
        )
        private val response = FoursquareVenueDetailApiResponse(
                FoursquareVenueDetailsWrapper(
                        venueDetails
                )
        )
    }
}