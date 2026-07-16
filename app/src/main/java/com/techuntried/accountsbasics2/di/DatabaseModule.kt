package com.techuntried.accountsbasics2.di

import android.content.Context
import androidx.room.Room
import com.techuntried.accountsbasics2.data.database.AppDatabase
import com.techuntried.accountsbasics2.data.repository.RealNetworkMonitor
import com.techuntried.accountsbasics2.domain.repository.NetworkMonitor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {

    @Singleton
    @Provides
    fun provideMyAppDb(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "myAppDb")
            .fallbackToDestructiveMigration(true)
            .build()
    }

    @Provides
    fun providesCategoryProgressDao(db: AppDatabase) = db.categoryProgressDao()

    @Provides
    fun providesCategoryDao(db: AppDatabase) = db.subjectDao()

    @Provides
    fun providesLevelDao(db: AppDatabase) = db.levelDao()

    @Provides
    fun providesQuestionDao(db: AppDatabase) = db.questionDao()

    @Provides
    fun providesLearnContentDao(db: AppDatabase) = db.learnContentDao()

    @Provides
    @Singleton
    fun provideNetworkMonitor(
        @ApplicationContext context: Context
    ): NetworkMonitor {
        return RealNetworkMonitor(context)
    }
}

