package com.trilogy.mathlearning.data.repository

import com.trilogy.mathlearning.domain.model.StatisticLikeResDto
import com.trilogy.mathlearning.domain.model.StatisticResDto
import com.trilogy.mathlearning.domain.model.UserProgressResDto
import com.trilogy.mathlearning.network.api.StatisticApi
import com.trilogy.mathlearning.utils.NetworkResource
import com.trilogy.mathlearning.utils.handleNetworkCall
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StatisticRepository @Inject constructor(
    private val api: StatisticApi
) {

    private val errors = mapOf(
        400 to "Dữ liệu không hợp lệ",
        401 to "Không được phép",
        403 to "Bị từ chối",
        404 to "Không tìm thấy",
        429 to "Thử lại sau",
        500 to "Lỗi máy chủ"
    )

    suspend fun getTopUsersByPoints(): NetworkResource<StatisticResDto> =
        handleNetworkCall({ api.getTopUsersByPoints() }, errors)

    suspend fun getTopUsersByLikes(): NetworkResource<StatisticLikeResDto> =
        handleNetworkCall({ api.getTopUsersByLikes() }, errors)

    suspend fun getUserProgress(): NetworkResource<UserProgressResDto> =
        handleNetworkCall({ api.getUserProgress() }, errors)
}
