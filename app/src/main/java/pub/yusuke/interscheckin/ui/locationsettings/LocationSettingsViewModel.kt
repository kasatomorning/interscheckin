package pub.yusuke.interscheckin.ui.locationsettings

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class LocationSettingsViewModel @Inject constructor(
    private val interactor: LocationSettingsInteractor,
) : ViewModel(), LocationSettingsContract.ViewModel {
    private val _screenStateFlow: MutableStateFlow<LocationSettingsContract.ScreenState> = MutableStateFlow(LocationSettingsContract.ScreenState.Loading)
    override val screenStateFlow: StateFlow<LocationSettingsContract.ScreenState>
        get() = _screenStateFlow.asStateFlow()

    override fun setPeriodicLocationRetrievalEnabled(enabled: Boolean) {
        interactor.setPeriodicLocationRetrievalEnabled(enabled)
        _screenStateFlow.value = (_screenStateFlow.value as LocationSettingsContract.ScreenState.Idle).copy(
            periodicLocationRetrievalEnabled = enabled,
        )
    }

    override fun setPeriodicLocationRetrievalPreset(preset: LocationSettingsContract.PeriodicLocationRetrievalIntervalPreset) {
        interactor.setPeriodicLocationRetrievalIntervalPreset(preset)
        _screenStateFlow.value = (_screenStateFlow.value as LocationSettingsContract.ScreenState.Idle).copy(
            periodicLocationRetrievalIntervalPreset = preset,
        )
    }

    init {
        fetchContents()
    }

    private fun fetchContents() {
        val periodicLocationRetrievalEnabled = interactor.getPeriodicLocationRetrievalEnabled()
        val periodicLocationRetrievalIntervalPreset = interactor.getPeriodicLocationRetrievalIntervalPreset()

        _screenStateFlow.value = LocationSettingsContract.ScreenState.Idle(
            periodicLocationRetrievalEnabled = periodicLocationRetrievalEnabled,
            periodicLocationRetrievalIntervalPreset = periodicLocationRetrievalIntervalPreset,
        )
    }
}
