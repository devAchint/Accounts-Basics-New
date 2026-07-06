package com.techuntried.accountsbasics2.di

import com.techuntried.accountsbasics2.data.repository.GlobalConfigImpl
import dagger.Module
import dagger.Binds
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import com.techuntried.accountsbasics2.data.repository.NetworkRepositoryImpl
import com.techuntried.accountsbasics2.domain.repository.GlobalConfigController
import com.techuntried.accountsbasics2.domain.repository.NetworkRepository

@Module
@InstallIn(SingletonComponent::class)
abstract class BindsModule {

    @Binds
    @Singleton
    abstract fun bindNetworkRepository(
        networkImpl: NetworkRepositoryImpl
    ): NetworkRepository

    @Binds
    @Singleton
    abstract fun bindGlobalConfig(
        globalConfigImpl: GlobalConfigImpl
    ): GlobalConfigController

}