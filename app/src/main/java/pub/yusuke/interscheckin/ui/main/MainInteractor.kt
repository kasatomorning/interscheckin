package pub.yusuke.interscheckin.ui.main

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.VibrationEffect
import android.os.VibratorManager
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import pub.yusuke.foursquareclient.models.Checkin
import pub.yusuke.foursquareclient.models.Venue
import pub.yusuke.interscheckin.repositories.UserPreferencesRepository
import pub.yusuke.interscheckin.repositories.foursquarecheckins.FoursquareCheckinsRepository
import pub.yusuke.interscheckin.repositories.foursquarecheckins.FoursquarePlacesRepository
import javax.inject.Inject

class MainInteractor @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val foursquarePlacesRepository: FoursquarePlacesRepository,
    private val foursquareCheckinsRepository: FoursquareCheckinsRepository,
    @ApplicationContext private val context: Context
) : MainContract.Interactor {
    private val vibratorManager =
        context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
    private val fusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    override suspend fun fetchVenues(
        latitude: Double,
        longitude: Double,
        hacc: Double?,
        limit: Int?,
        query: String?
    ): List<Venue> =
        foursquarePlacesRepository.searchPlacesNearby(
            latitude = latitude,
            longitude = longitude,
            hacc = hacc,
            limit = limit,
            query = query
        )

    @SuppressLint("MissingPermission")
    override fun fetchLocation(): Location {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        return fusedLocationClient.lastLocation.result
    }

    override fun fetchDrivingModelFlow(): Flow<Boolean> =
        userPreferencesRepository
            .userPreferencesFlow
            .map { it.drivingMode }

//    override fun fetchLocationFlow(
//        intervalMills: Long,
//        minUpdateIntervalMillis: Long,
//    ): Flow<Location> {
//        val locationRequest = LocationRequest.Builder(intervalMills)
//            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
//            .setMinUpdateIntervalMillis(minUpdateIntervalMillis)
//            .build()
//
//        return fusedLocationProviderClient
//            .locationFlow(locationRequest)
//    }

    override suspend fun enableDrivingMode(enabled: Boolean) =
        userPreferencesRepository.enableDrivingMode(enabled)

    override suspend fun createCheckin(
        venueId: String,
        shout: String,
        latitude: Double,
        longitude: Double
    ): Checkin = foursquareCheckinsRepository.createCheckin(
        venueId = venueId,
        shout = shout,
        latitude = latitude,
        longitude = longitude
    )

    override fun vibrate(vibrationEffect: VibrationEffect) =
        vibratorManager.defaultVibrator.vibrate(vibrationEffect)
}
