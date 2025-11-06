// data/repository/PracticeRepository.kt
package com.trilogy.mathlearning.data.repository

import com.trilogy.mathlearning.domain.model.*
import com.trilogy.mathlearning.network.api.MathConfigApi
import com.trilogy.mathlearning.network.api.PracticeApi
import com.trilogy.mathlearning.utils.NetworkResource
import com.trilogy.mathlearning.utils.handleNetworkCall
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PracticeRepository @Inject constructor(
    private val mathConfigApi: MathConfigApi,
    private val practiceApi: PracticeApi
) {
    private val errors = mapOf(
        400 to "Dữ liệu không hợp lệ",
        401 to "Không được phép",
        403 to "Bị từ chối",
        404 to "Không tìm thấy",
        429 to "Thử lại sau",
        500 to "Lỗi máy chủ"
    )

    suspend fun getMathConfig(): NetworkResource<MathConfigResDto> =
        handleNetworkCall({ mathConfigApi.getMathConfig() }, errors)

    suspend fun createPractice(grade: Int, chapterId: Int?, examType: String?): NetworkResource<PracticeResDto> =
        handleNetworkCall({
            practiceApi.createPractice(CreatePracticeReqDto(grade, chapterId, examType))
        }, errors)

    suspend fun getExercises(practiceId: String): NetworkResource<List<ExerciseResDto>> =
        handleNetworkCall({ practiceApi.getExercises(practiceId) }, errors)

    suspend fun submitPractice(practiceId: String, timeSpent: Int, answers: List<SubmitAnswerReqDto>):
            NetworkResource<PracticeResDto> =
        handleNetworkCall({
            practiceApi.submitPractice(SubmitPracticeReqDto(practiceId, timeSpent, answers))
        }, errors)

    suspend fun listPractices(): NetworkResource<ListPracticesResDto> =
        handleNetworkCall({ practiceApi.listPractices() }, errors)
}
