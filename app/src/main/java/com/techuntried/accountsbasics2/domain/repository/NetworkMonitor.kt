package com.techuntried.accountsbasics2.domain.repository

import kotlinx.coroutines.flow.Flow

interface NetworkMonitor {
    fun isConnected(): Boolean
    val isOnline: Flow<Boolean>
}