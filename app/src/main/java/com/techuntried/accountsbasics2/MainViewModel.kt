package com.techuntried.accountsbasics2

import android.media.tv.AdRequest
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techuntried.accountsbasics2.ads.AdsManager
import com.techuntried.accountsbasics2.ads.AppOpenAdManager
import com.techuntried.accountsbasics2.data.repository.DataStoreRepository
import com.techuntried.accountsbasics2.data.repository.DeviceInfoProvider
import com.techuntried.accountsbasics2.domain.model.GameEconomy
import com.techuntried.accountsbasics2.domain.model.account.CreateGuestAccountRequest
import com.techuntried.accountsbasics2.domain.model.account.UserAppVersionRequest
import com.techuntried.accountsbasics2.domain.repository.GlobalConfigController
import com.techuntried.accountsbasics2.domain.repository.NetworkMonitor
import com.techuntried.accountsbasics2.domain.repository.NetworkRepository
import com.techuntried.accountsbasics2.utils.ApiResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

sealed interface MainUiState {
    data object Loading : MainUiState
    data class Success(val isFirstTime: Boolean) : MainUiState
}

fun MainUiState.isFirstTime() = when (this) {
    MainUiState.Loading -> null
    is MainUiState.Success -> isFirstTime
}

fun MainUiState.shouldKeepSplashScreen() = this is MainUiState.Loading


@HiltViewModel
class MainViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
    private val networkMonitor: NetworkMonitor,
    private val networkRepository: NetworkRepository,
    private val globalConfigController: GlobalConfigController,
    private val deviceInfoProvider: DeviceInfoProvider,
//    private val consentManager: GoogleConsentManager,
    private val adsManager: AdsManager
) :
    ViewModel() {

    val uiState = dataStoreRepository.observeFirstTimeFlow()
        .map(MainUiState::Success)
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            MainUiState.Loading
        )

    private val globalConfig = globalConfigController.globalConfig
    private var isConfigFetched: Boolean = false
    private val adsSdkInitialized = MutableStateFlow(false)

    init {
        observeNetworkAndFetchConfig()
        checkIsUserLoggedIn()
//        observeAdsInitialization()
        checkAppVersionCode()
    }

//    private fun observeAdsInitialization() {
//
//        viewModelScope.launch {
//
//            combine(
//                globalConfigController.globalConfig,
//                consentManager.canRequestAds,
//                adsSdkInitialized
//            ) { config, canRequestAds, initialized ->
//                config.adsEnabled &&
//                        canRequestAds &&
//                        !initialized
//            }
//                .first { it }
//
//            adsManager.initialize {
//
//                adsSdkInitialized.value = true
//
//                viewModelScope.launch {
//                    loadAds()
//                }
//            }
//        }
//    }

    private fun checkAppVersionCode() {
        viewModelScope.launch {

            try {
                val userProfile =
                    dataStoreRepository.getUserProfile()
                        ?: return@launch


                val userId = userProfile.userId
                val savedVersion = userProfile.appVersion
                val currentVersion = BuildConfig.VERSION_NAME

                if (savedVersion != currentVersion) {
                    val response =
                        networkRepository.updateAppVersion(
                            UserAppVersionRequest(
                                userId = userId,
                                appVersion = currentVersion
                            )
                        )

                    when (response) {
                        is ApiResult.Error -> {

                            Log.d(
                                "APP_VERSION",
                                "Update failed: ${response.errorMessage}"
                            )
                        }

                        is ApiResult.Success -> {
                            dataStoreRepository.saveUserProfile(
                                userProfile.copy(
                                    appVersion = currentVersion
                                )
                            )
                        }
                    }

                } else {
                    Log.d(
                        "APP_VERSION",
                        "Version already up to date"
                    )
                }

            } catch (e: Exception) {
                Log.e(
                    "APP_VERSION",
                    "Exception: ${e.message}"
                )
            }
        }
    }

//    private fun loadAds() {
//        try {
//            if (globalConfig.value.adsEnabled) {
//                val appOpenAdUnit = globalConfig.value.testOrRealAppOpenAdUnit()
//                appOpenAdUnit?.let {
//                    loadAppOpenAd(adUnit = it)
//                }
//
//                val interAdUnit = globalConfig.value.testOrRealInterstitialAdUnit()
//                interAdUnit?.let {
//                    loadInterstitialAd(it)
//                }
//                val rewardedAdUnit = globalConfig.value.testOrRealRewardedAdUnit()
//                rewardedAdUnit?.let {
//                    loadRewardedAd(it)
//                }
//            }
//        } catch (e: Exception) {
//            Log.d("MYDEBUG", "${e.message}")
//        }
//    }

//    fun loadInterstitialAd(adUnit: String) {
//        val adRequest = AdRequest.Builder(adUnit).build()
//        val preloadConfig = PreloadConfiguration(adRequest, bufferSize = 2)
//        InterstitialAdPreloader.start(
//            adUnit,
//            preloadConfig
//        )
//    }
//
//    fun loadRewardedAd(adUnit: String) {
//        val adRequest = AdRequest.Builder(adUnit).build()
//        val preloadConfig = PreloadConfiguration(adRequest)
//        RewardedAdPreloader.start(adUnit, preloadConfig)
//    }
//
//    fun loadAppOpenAd(adUnit: String) {
//        AppOpenAdManager.loadAd(adUnit = adUnit)
//    }

    private fun checkIsUserLoggedIn() {
        viewModelScope.launch {
            try {
                val fcmToken = null//FirebaseMessaging.getInstance().token.await()
                val userProfile = dataStoreRepository.getUserProfile()
                if (userProfile == null) {
                    val country = deviceInfoProvider.getCountry()
                    createGuestAccount(country = country, fcmToken = fcmToken)
                }
            } catch (e: Exception) {
                Log.d("MYDEBUG", "Error checking login: ${e.message}")
            }
        }
    }

    suspend fun createGuestAccount(fcmToken: String?, country: String) {
        try {
            val guestUid = getGuestUID()
            val response = networkRepository.createGuestAccount(
                CreateGuestAccountRequest(
                    country = country,
                    uid = guestUid,
                    age = null,
                    fcmToken = fcmToken,
                    appVersion = BuildConfig.VERSION_NAME
                )
            )
            when (response) {
                is ApiResult.Error -> {
                    Log.d("MYDEBUG", response.errorMessage)
                }

                is ApiResult.Success -> {
                    if (response.data.status) {
                        dataStoreRepository.saveUserProfile(response.data.user)
                    }
                }
            }
        } catch (e: Exception) {
            Log.d("MYDEBUG", "${e.message}")
        }
    }


    suspend fun getGuestUID(): String {
        val existingId = dataStoreRepository.getGuestUID()
        if (existingId != null) {
            return existingId
        }
        val newId = UUID.randomUUID().toString()
        dataStoreRepository.saveGuestUID(newId)
        return newId
    }

    private fun observeNetworkAndFetchConfig() {
        viewModelScope.launch {
            networkMonitor.isOnline.collect { isConnected ->
                // If the internet is validated AND we haven't fetched config yet
                if (isConnected && !isConfigFetched) {
                    fetchAppConfig()
                }
            }
        }
    }

    private fun fetchAppConfig() {
        viewModelScope.launch {
            try {
                when (val response = networkRepository.fetchAppConfig()) {
                    is ApiResult.Error -> {
                        isConfigFetched = false
                    }

                    is ApiResult.Success -> {
                        if (response.data.status) {
                            val appConfig = response.data.appConfig
                            globalConfigController.initialize(appConfig)
                            val gameEconomy = GameEconomy(
                                unlockLevelCoins = appConfig.unlockLevelCoins,
                                bombCoins = appConfig.bombCoins,
                                addTimeCoins = appConfig.addTimeCoins,
                                tryAgainCoins = appConfig.tryAgainCoins,
                                correctAnswerCoins = appConfig.correctAnswerCoins
                            )
                            dataStoreRepository.saveGameEconomy(gameEconomy)
                            isConfigFetched = true
                        }
                    }
                }
            } catch (e: Exception) {
                Log.d("MYDEBUG", "${e.message}")
            }
        }
    }
}