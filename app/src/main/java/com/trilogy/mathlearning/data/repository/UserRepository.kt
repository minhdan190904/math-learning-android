package com.trilogy.mathlearning.data.repository

import com.trilogy.mathlearning.domain.model.LoginResDto
import com.trilogy.mathlearning.domain.model.RefreshTokenDto
import com.trilogy.mathlearning.domain.model.UserDto
import com.trilogy.mathlearning.domain.model.UserResDto
import com.trilogy.mathlearning.network.api.AuthApi
import com.trilogy.mathlearning.network.api.UserApi
import com.trilogy.mathlearning.utils.NetworkResource
import com.trilogy.mathlearning.utils.handleNetworkCall
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.collections.plus

@Singleton
class UserRepository @Inject constructor(
    private val userApi: UserApi
) {
    private val commonErrors = mapOf(
        400 to "Dữ liệu không hợp lệ",
        401 to "Thông tin đăng nhập không đúng",
        403 to "Tài khoản chưa được phép truy cập",
        404 to "Không tìm thấy",
        429 to "Thao tác quá nhanh. Vui lòng thử lại sau.",
        500 to "Lỗi máy chủ. Vui lòng thử lại sau."
    )


    suspend fun getUser(): NetworkResource<UserResDto> {
        return handleNetworkCall(
            call = { userApi.getMyUser() },
            customErrorMessages = commonErrors
        )
    }
}