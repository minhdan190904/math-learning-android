package com.trilogy.mathlearning.utils

import com.trilogy.mathlearning.R

sealed class OnBoardingPage(
    val imageRes: Int,
    val title: String,
    val description: String
) {
    object AiSolve : OnBoardingPage(
        imageRes = R.drawable.one,
        title = "Giải Toán Bằng AI",
        description = "Nhận đề bài, AI sẽ phân tích & đưa ra lời giải chi tiết từng bước."
    )

    object CommunityHelp : OnBoardingPage(
        imageRes = R.drawable.two,
        title = "Trợ Giúp Cộng Đồng",
        description = "Đăng đề lên cộng đồng, cùng thảo luận, like/star để xác thực độ chính xác."
    )

    object TeacherReview : OnBoardingPage(
        imageRes = R.drawable.three,
        title = "Giáo Viên Chấm",
        description = "Giáo viên sẽ review & feedback trực tiếp, đảm bảo lời giải luôn chuẩn."
    )
}
