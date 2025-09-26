package com.trilogy.mathlearning.ui.presentation.solve_math

import androidx.compose.ui.graphics.Color

// ---------- Model ----------
data class SolveStyle(
    val id: String,
    val title: String,
    val subtitle: String,
    val emoji: String,
    val enabled: Boolean = true,
    val tags: List<String> = emptyList()
)

// ---------- Preset gradient palettes ----------
object GradientPresets {
    val Ocean = listOf(Color(0xFF1976D2), Color(0xFF42A5F5), Color(0xFF64B5F6))
    val Sunset = listOf(Color(0xFFFF8A00), Color(0xFFFF3D81), Color(0xFF8A63FF))
    val Neon   = listOf(Color(0xFF00E5FF), Color(0xFF00FFA3), Color(0xFF00E5FF))
    val Aurora = listOf(Color(0xFF7CF5FF), Color(0xFF98F5B5), Color(0xFFC9A7FF))
    val Rainbow = listOf(
        Color.Red, Color.Yellow, Color.Green, Color.Cyan, Color.Blue, Color.Magenta, Color.Red
    )
}

// map mỗi style -> bảng màu khác nhau (thay theo ý bạn)
private fun paletteFor(style: SolveStyle): List<Color> = when (style.id) {
    "short"      -> GradientPresets.Ocean
    "step"       -> GradientPresets.Sunset
    "teacher"    -> GradientPresets.Aurora
    "beginner"   -> GradientPresets.Neon
    "academic"   -> GradientPresets.Rainbow
    "interactive"-> GradientPresets.Sunset
    else         -> GradientPresets.Ocean
}

object SolveStyles {
    val all = listOf(
        SolveStyle(
            "short", "Trả lời ngắn gọn", "Khi bạn chỉ cần kết quả/lời giải ngắn.", "✍️",
            tags = listOf("Nhanh", "Gọn")
        ),
        SolveStyle(
            "interactive", "Tương tác / Trắc nghiệm", "Hỏi–đáp dẫn dắt, luyện tập.", "🤝",
            enabled = true, tags = listOf("Trắc nghiệm")
        ),

        SolveStyle(
            "funny", "Hài hước / dí dỏm", "Giải thích nhẹ nhàng kèm ví dụ vui.", "😂",
            tags = listOf("Thư giãn", "Vui vẻ")
        ),

        SolveStyle(
            "exam", "Dạng đề thi", "Trình bày giống lời giải trong đề thi chính thức.", "📑",
            tags = listOf("Thi cử", "Chuẩn mực")
        ),

        SolveStyle(
            "fastest", "Nhanh nhất có thể", "Ưu tiên tốc độ, ít giải thích.", "⚡",
            tags = listOf("Nhanh", "Tốc độ")
        ),

        SolveStyle(
            "story", "Kể chuyện minh họa", "Biến toán học thành câu chuyện dễ nhớ.", "📖",
            tags = listOf("Sáng tạo", "Dễ nhớ")
        ),
        SolveStyle(
            "step", "Giải thích từng bước", "Hợp người mới, đầy đủ lý giải.", "🧠",
            tags = listOf("Chi tiết", "Từng bước")
        ),
        SolveStyle(
            "teacher", "Phong cách giảng dạy", "Giống giáo viên trên lớp, có ví dụ.", "🎓",
            tags = listOf("Dễ hiểu", "Ví dụ")
        ),
        SolveStyle(
            "beginner", "Dành cho người mới", "Ngôn ngữ đơn giản, trình bày rõ.", "🐣",
            tags = listOf("Cơ bản")
        ),
        SolveStyle(
            "academic", "Học thuật / Chứng minh", "Cho học sinh nâng cao, luyện thi.", "🖊️",
            tags = listOf("Chặt chẽ")
        )
    )
}