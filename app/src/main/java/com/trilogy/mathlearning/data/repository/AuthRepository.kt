package com.trilogy.mathlearning.data.repository

import com.trilogy.mathlearning.domain.model.ActiveUserDto
import com.trilogy.mathlearning.domain.model.ForgotPasswordDto
import com.trilogy.mathlearning.domain.model.LoginDto
import com.trilogy.mathlearning.domain.model.LoginResDto
import com.trilogy.mathlearning.domain.model.RefreshTokenDto
import com.trilogy.mathlearning.domain.model.RegisterDto
import com.trilogy.mathlearning.domain.model.ResDto
import com.trilogy.mathlearning.domain.model.ResetPasswordDto
import com.trilogy.mathlearning.domain.model.UserDto
import com.trilogy.mathlearning.network.api.AuthApi
import com.trilogy.mathlearning.utils.NetworkResource
import com.trilogy.mathlearning.utils.handleNetworkCall
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val authApi: AuthApi
) {

    private val commonErrors = mapOf(
        400 to "Dữ liệu không hợp lệ",
        401 to "Thông tin đăng nhập không đúng",
        403 to "Tài khoản chưa được phép truy cập",
        404 to "Không tìm thấy",
        429 to "Thao tác quá nhanh. Vui lòng thử lại sau.",
        500 to "Lỗi máy chủ. Vui lòng thử lại sau."
    )

    suspend fun registerUser(registerDto: RegisterDto): NetworkResource<UserDto> {
        return handleNetworkCall(
            call = { authApi.register(registerDto) },
            customErrorMessages = commonErrors + mapOf(
                409 to "Email đã tồn tại"
            )
        )
    }

    suspend fun activateUser(activeUserDto: ActiveUserDto): NetworkResource<UserDto> {
        return handleNetworkCall(
            call = { authApi.activate(activeUserDto) },
            customErrorMessages = commonErrors + mapOf(
                400 to "Mã kích hoạt không hợp lệ hoặc đã hết hạn",
                404 to "Không tìm thấy tài khoản cần kích hoạt"
            )
        )
    }

    suspend fun login(loginDto: LoginDto): NetworkResource<LoginResDto> {
        return handleNetworkCall(
            call = { authApi.login(loginDto) },
            customErrorMessages = commonErrors + mapOf(
                403 to "Tài khoản chưa kích hoạt. Vui lòng kiểm tra email.",
                423 to "Tài khoản đang bị khoá tạm thời"
            )
        )
    }

    suspend fun forgotPassword(dto: ForgotPasswordDto): NetworkResource<ResDto> {
        return handleNetworkCall(
            call = { authApi.forgotPassword(dto) },
            customErrorMessages = commonErrors + mapOf(
                404 to "Email không tồn tại trong hệ thống"
            )
        )
    }

    suspend fun resetPassword(dto: ResetPasswordDto): NetworkResource<ResDto> {
        return handleNetworkCall(
            call = { authApi.resetPassword(dto) },
            customErrorMessages = commonErrors + mapOf(
                400 to "Mã/Link đặt lại mật khẩu không hợp lệ hoặc đã hết hạn"
            )
        )
    }

    suspend fun refreshToken(dto: RefreshTokenDto): NetworkResource<LoginResDto> {
        return handleNetworkCall(
            call = { authApi.refreshToken(dto) },
            customErrorMessages = commonErrors + mapOf(
                401 to "Refresh token không hợp lệ",
                403 to "Refresh token đã hết hạn hoặc bị thu hồi"
            )
        )
    }
}
