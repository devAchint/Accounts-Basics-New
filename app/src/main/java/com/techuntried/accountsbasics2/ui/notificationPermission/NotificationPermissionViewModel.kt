package com.techuntried.accountsbasics2.ui.notificationPermission

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import androidx.lifecycle.viewModelScope
import com.techuntried.accountsbasics2.data.repository.DataStoreRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


data class NotificationPermissionUiState(
    val isLoading: Boolean = false,
    val message: String? = null,
    val firstTimeFlagSaved: Boolean = false
)

@HiltViewModel
class NotificationPermissionViewModel @Inject constructor(private val dataStoreRepository: DataStoreRepository) :
    ViewModel() {

    private val _notificationPermissionUiState =
        MutableStateFlow<NotificationPermissionUiState>(NotificationPermissionUiState())
    val notificationPermissionUiState = _notificationPermissionUiState.asStateFlow()


    fun saveFirstTime(){
        viewModelScope.launch {
            dataStoreRepository.saveIsFirstTime(false)
            _notificationPermissionUiState.update { it.copy(firstTimeFlagSaved = true) }
        }
    }

    fun clearMsg() {
        _notificationPermissionUiState.update { it.copy(message = null) }
    }

}
   