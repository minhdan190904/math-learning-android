package com.trilogy.mathlearning.data.repository

import com.trilogy.mathlearning.domain.model.*
import com.trilogy.mathlearning.network.api.AnswerApi
import com.trilogy.mathlearning.network.api.QuestionApi
import com.trilogy.mathlearning.utils.NetworkResource
import com.trilogy.mathlearning.utils.handleNetworkCall
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CommunityRepository @Inject constructor(
    private val questionApi: QuestionApi,
    private val answerApi: AnswerApi
) {
    private val common = mapOf(
        400 to "Dữ liệu không hợp lệ",
        401 to "Không được phép",
        403 to "Bị từ chối truy cập",
        404 to "Không tìm thấy",
        429 to "Thử lại sau",
        500 to "Lỗi máy chủ"
    )

    /** LIST */
    suspend fun loadQuestions(): NetworkResource<List<QuestionResDto>> =
        handleNetworkCall(
            call = { questionApi.getQuestions().docs }, // <-- lấy mảng trong docs
            customErrorMessages = common
        )

    /** DETAIL */
    suspend fun loadQuestionDetail(id: String): NetworkResource<QuestionResDto> =
        handleNetworkCall({ questionApi.getQuestionDetail(id) }, common)

    /** CREATE QUESTION */
    suspend fun createQuestion(content: String?, imageUrl: String?): NetworkResource<QuestionResDto> {
        val body = CreateQuestionReqDto(
            content = content?.takeIf { it.isNotBlank() },
            imageUrl = imageUrl?.takeIf { it.isNotBlank() }
        )
        return handleNetworkCall({ questionApi.createQuestion(body) }, common)
    }

    /** ANSWER */
    suspend fun createAnswer(
        questionId: String,
        content: String?,
        imageUrl: String?
    ): NetworkResource<AnswerResDto> {
        val body = CreateAnswerReqDto(
            questionId = questionId,
            content = content?.takeIf { it.isNotBlank() },
            imageUrl = imageUrl?.takeIf { it.isNotBlank() }
        )
        return handleNetworkCall({ answerApi.createAnswer(body) }, common)
    }

    suspend fun toggleLike(answerId: String): NetworkResource<AnswerResDto> =
        handleNetworkCall({ answerApi.toggleLike(ToggleLikeAnswerReqDto(answerId)) }, common)
}
