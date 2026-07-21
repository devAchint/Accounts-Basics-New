package com.techuntried.accountsbasics2.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techuntried.accountsbasics2.ads.GoogleConsentManager
import com.techuntried.accountsbasics2.data.repository.DataStoreRepository
import com.techuntried.accountsbasics2.usecases.LogEventType
import com.techuntried.accountsbasics2.usecases.LogEventUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UserSettingsModel(
    val sound: Boolean = true,
    val haptic: Boolean = true,
    val showCorrect: Boolean = true,
    val timer: Boolean = true,
    val username: String? = null
)

data class SettingsUiState(
    val isLoading: Boolean = false,
    val isSoundEnabled: Boolean = false,
    val isHapticEnabled: Boolean = false,
    val isShowCorrectEnabled: Boolean = false,
    val isTimerEnabled: Boolean = false,
    val username: String? = null,
    val dataPreferencesVisible: Boolean = false
)

sealed interface PreferenceType {
    data class Sound(val value: Boolean) : PreferenceType
    data class Haptic(val value: Boolean) : PreferenceType
    data class Timer(val value: Boolean) : PreferenceType
    data class ShowCorrect(val value: Boolean) : PreferenceType
    data class Name(val value: String) : PreferenceType
}

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
    private val logEventUseCase: LogEventUseCase,
    private val consentManager: GoogleConsentManager
) :
    ViewModel() {

    val settingsUiState: StateFlow<SettingsUiState> =
        dataStoreRepository.getUserSettingsFlow()
            .map { settings ->
                SettingsUiState(
                    isSoundEnabled = settings.sound,
                    isHapticEnabled = settings.haptic,
                    isShowCorrectEnabled = settings.showCorrect,
                    isTimerEnabled = settings.timer,
                    username = settings.username,
                    dataPreferencesVisible = consentManager.privacyOptionsRequired()
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = SettingsUiState(isLoading = true)
            )

    fun updateUserPreference(type: PreferenceType) {
        viewModelScope.launch {
            when (type) {
                is PreferenceType.Haptic -> {
                    dataStoreRepository.setHapticEnabled(type.value)
                }

                is PreferenceType.Name -> {
                    dataStoreRepository.saveUsername(type.value)
                }

                is PreferenceType.ShowCorrect -> {
                    dataStoreRepository.setShowCorrect(type.value)
                }

                is PreferenceType.Sound -> {
                    dataStoreRepository.setSoundEnabled(type.value)
                }

                is PreferenceType.Timer -> {
                    dataStoreRepository.setTimerEnabled(type.value)
                }
            }
        }
    }

    fun logEvent(logEventType: LogEventType) {
        viewModelScope.launch {
            logEventUseCase(logEventType)
        }
    }

}
   