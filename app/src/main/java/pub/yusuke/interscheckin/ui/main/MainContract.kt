package pub.yusuke.interscheckin.ui.main

import android.location.Location
import android.os.VibrationEffect
import androidx.compose.runtime.State
import kotlinx.coroutines.flow.Flow
import pub.yusuke.foursquareclient.models.Checkin
import pub.yusuke.foursquareclient.models.Venue

interface MainContract {
    interface ViewModel {
        val drivingModeFlow: Flow<Boolean>
        val locationState: State<LocationState>
        val checkinState: State<CheckinState>
        val venuesState: State<VenuesState>
        val navigationRequiredState: State<String?>
        val snackbarMessageState: State<Int?>

        suspend fun onDrivingModeStateChanged(enabled: Boolean)
        suspend fun checkIn(
            venueId: String,
            shout: String? = null
        )

        fun onVibrationRequested()
        fun onLocationUpdateRequested()
        fun onNavigationFinished()
        fun onSnackbarDisplayed()
    }

    interface Interactor {
        suspend fun fetchVenues(
            latitude: Double,
            longitude: Double,
            hacc: Double?,
            limit: Int? = null,
            query: String? = null
        ): List<Venue>

        fun fetchLocation(): Location
        fun fetchDrivingModelFlow(): Flow<Boolean>
        suspend fun enableDrivingMode(enabled: Boolean)
        suspend fun createCheckin(
            venueId: String,
            shout: String,
            latitude: Double,
            longitude: Double
        ): Checkin

        fun vibrate(vibrationEffect: VibrationEffect)
    }

    sealed class LocationState {
        /**
         * @param lastLocation 最後に保持していた Location
         */
        class Loading(val lastLocation: Location? = null) : LocationState()
        class Loaded(val location: Location) : LocationState()
        class Error(val throwable: Throwable) : LocationState()
    }

    sealed class VenuesState {
        /**
         * @param lastVenues 最後に保持していた Venue たち
         */
        class Loading(val lastVenues: List<Venue>) : VenuesState()
        class Idle(val venues: List<Venue>) : VenuesState()
        class Error(val throwable: Throwable) : VenuesState()
    }

    sealed class CheckinState {
        object InitialIdle : CheckinState()
        object Loading : CheckinState()
        class Idle(val lastCheckin: Checkin) : CheckinState()
        class Error(val throwable: Throwable) : CheckinState()
    }
}
