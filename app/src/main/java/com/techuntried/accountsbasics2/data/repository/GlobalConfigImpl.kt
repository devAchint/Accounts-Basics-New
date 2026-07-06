package com.techuntried.accountsbasics2.data.repository

import com.techuntried.accountsbasics2.domain.model.GlobalConfig
import com.techuntried.accountsbasics2.domain.model.appConfig.AppConfigResponse
import com.techuntried.accountsbasics2.domain.repository.GlobalConfigController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GlobalConfigImpl @Inject constructor() :
    GlobalConfigController {

    private val _globalConfig = MutableStateFlow(GlobalConfig())
    override val globalConfig: StateFlow<GlobalConfig>
        get() = _globalConfig.asStateFlow()


    override fun initialize(appConfigResponse: AppConfigResponse) {
        _globalConfig.update {
            it.copy(
                scoreRatingEnabled = appConfigResponse.scoreRatingEnabled,
                levelRatingEnabled = appConfigResponse.levelRatingEnabled,
                adsEnabled = appConfigResponse.adsEnabled,
                scoreNativeAdEnabled = appConfigResponse.scoreNativeAdEnabled,
                interClicks = appConfigResponse.interstitialAdClicks,
                interstitialAdUnit = appConfigResponse.interstitialAdUnit,
                appOpenAdUnit = appConfigResponse.appOpenAdUnit,
                gameNativeAdUnit = appConfigResponse.gameNativeAdUnit,
                rewardedAdUnit = appConfigResponse.rewardedAdUnit,
                bannerAdUnit = appConfigResponse.bannerAdUnit,
                scoreNativeAdUnit = appConfigResponse.scoreNativeAdUnit,
            )
        }
    }

}