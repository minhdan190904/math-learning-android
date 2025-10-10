package com.trilogy.mathlearning.utils

import retrofit2.HttpException
import java.io.IOException

var tokenApi: String?
    get() = SharedPreferencesReManager.getData(TOKEN_KEY, String::class.java)
    set(value) {
        SharedPreferencesReManager.saveData(TOKEN_KEY, value)
    }

suspend fun <T> handleNetworkCall(
    call: suspend () -> T,
    customErrorMessages: Map<Int, String> = emptyMap()
): NetworkResource<T> {
    return try {
        val response = call()
        NetworkResource.Success(response)
    } catch (ex: HttpException) {
        val defaultMessages = mapOf(
            404 to "Not found",
            500 to "Internal Server Error. Please try again later."
        )
        val errorMessage = customErrorMessages[ex.code()] ?: defaultMessages[ex.code()] ?: "Server error: ${ex.message()}"
        NetworkResource.Error(message = errorMessage, responseCode = ex.code())
    } catch (ex: IOException) {
        NetworkResource.NetworkException("Network error. Please check your connection.")
    } catch (ex: Exception) {
        NetworkResource.Error(ex.message ?: "Unexpected error")
    }
}