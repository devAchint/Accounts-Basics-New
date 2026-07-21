package com.techuntried.accountsbasics2.di

import com.techuntried.accountsbasics2.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import kotlinx.serialization.json.Json
import javax.inject.Singleton
import io.ktor.serialization.kotlinx.json.json

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun providesHttpClient(): HttpClient {
        val json = Json {
            ignoreUnknownKeys = true
        }
        return HttpClient(Android) {
            install(Logging) {
                level = LogLevel.ALL
            }
            install(ContentNegotiation) {
                json(json)
            }
            defaultRequest {
                url("https://unified.nihaura.com/")
                header("api-token", "crLYI7EJ5PF82408bR3D9YSJkyLSr3Emq")
            }
        }
    }
}