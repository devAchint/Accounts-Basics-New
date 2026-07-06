package com.techuntried.accountsbasics2.domain.repository

import com.techuntried.accountsbasics2.domain.model.GlobalConfig
import com.techuntried.accountsbasics2.domain.model.appConfig.AppConfigResponse
import kotlinx.coroutines.flow.StateFlow

interface GlobalConfigController {
    val globalConfig: StateFlow<GlobalConfig>

    fun initialize(
       appConfigResponse: AppConfigResponse
    )


}