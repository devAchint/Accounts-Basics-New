package com.techuntried.accountsbasics2.utils

import com.techuntried.accountsbasics2.domain.model.BaseApiResponse
import io.ktor.client.call.body
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ServerResponseException

sealed interface ApiResult<out T> {
    data class Error(val errorMessage: String) : ApiResult<Nothing>
    data class Success<R>(val data: R) : ApiResult<R>
}

suspend fun <T> getApiResponse(
    request: suspend () -> T
): ApiResult<T> {
    return try {
        val result = request()
        ApiResult.Success(result)
    } catch (e: RedirectResponseException) { // 3xx
        ApiResult.Error("Redirect Error: ${e.response.status.value}")
    } catch (e: ClientRequestException) { // 4xx
        val baseApiResponse = e.response.body<BaseApiResponse>()

        ApiResult.Error("Client Error: ${e.response.status.value}")
    } catch (e: ServerResponseException) { // 5xx
        ApiResult.Error("Server Error: ${e.response.status.value}")
    } catch (e: SocketTimeoutException) {
        ApiResult.Error("Request timed out")
    } catch (e: Exception) {
        ApiResult.Error("Unexpected error: ${e.message}")
    }
}