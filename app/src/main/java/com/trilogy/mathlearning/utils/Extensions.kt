package com.trilogy.mathlearning.utils

import com.firebase.ui.auth.data.model.User
import com.trilogy.mathlearning.domain.model.UserDto
import com.trilogy.mathlearning.domain.model.UserResDto
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException

var tokenApi: String?
    get() = SharedPreferencesReManager.getData(TOKEN_KEY, String::class.java)
    set(value) {
        SharedPreferencesReManager.saveData(TOKEN_KEY, value)
    }

var myUser: UserResDto?
    get() = SharedPreferencesReManager.getData(USER_KEY, UserResDto::class.java)
    set(value) {
        SharedPreferencesReManager.saveData(USER_KEY, value)
    }



suspend fun <T> handleNetworkCall(
    call: suspend () -> T,
    customErrorMessages: Map<Int, String> = emptyMap()
): NetworkResource<T> {
    return try {
        val result = call()
        NetworkResource.Success(result)
    } catch (e: HttpException) {
        val code = e.code()
        val errorBody = e.response()?.errorBody()?.string()
        val serverMessage = try {
            if (!errorBody.isNullOrBlank()) {
                val json = JSONObject(errorBody)
                val rawMessage = json.opt("message")
                when (rawMessage) {
                    is String -> rawMessage
                    is JSONArray -> rawMessage.optString(0)
                    else -> json.optString("error", null)
                }
            } else null
        } catch (_: Exception) {
            null
        }

        val message = customErrorMessages[code]
            ?: serverMessage
            ?: "Đã xảy ra lỗi ($code). Vui lòng thử lại sau."

        NetworkResource.Error(
            message = message,
            responseCode = code
        )
    } catch (e: IOException) {
        NetworkResource.NetworkException(
            message = "Không thể kết nối tới máy chủ. Vui lòng kiểm tra kết nối mạng."
        )
    } catch (e: Exception) {
        NetworkResource.Error(
            message = "Đã xảy ra lỗi không xác định. Vui lòng thử lại sau."
        )
    }
}