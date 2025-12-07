// StepWrapper.kt
package com.trilogy.mathlearning.data.repository

import android.graphics.Bitmap
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.Schema
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.generationConfig
import com.google.gson.Gson
import com.trilogy.mathlearning.utils.UiState

data class BaiToan(
    val steps: List<String>,
    val result: String,
    val category: String,
    val contentMathProblem: String
)


object GenerateSolutionSteps {

    suspend fun generateSteps(listOfBitmap: List<Bitmap>): UiState<BaiToan> {
        try {
            /* 2. Định nghĩa schema JSON  */
            val jsonSchema = Schema.obj(
                mapOf(
                    "steps" to Schema.array(Schema.string()),
                    "result" to Schema.string(),
                    "category" to Schema.string(),
                    "contentMathProblem" to Schema.string()
                )
            )

            /* 3. Khởi tạo model Gemini  */
            val model = Firebase.ai(backend = GenerativeBackend.googleAI())
                .generativeModel(
                    modelName = "gemini-2.5-flash",
                    generationConfig = generationConfig {
                        responseMimeType = "application/json"
                        responseSchema = jsonSchema
                    }
                )

            /* 4. Tạo prompt đa phương tiện  */
            val prompt = content {
                listOfBitmap.forEach { bmp ->
                    image(bmp)
                }

                text(
                    """
                Đây là hình chụp một bài toán. 
                Trả về JSON chứa mảng "steps" liệt kê ngắn gọn từng bước giải.
                Trả lời hoàn toàn bằng tiếng việt nhé.
                Nhớ là có kết quả cụ thể ở cuối nhé (Quan trọng nhất nhé tức là hỏi gì cuối lời giả phải có đáp án cụ thể).
                (chỉ JSON, không văn bản ngoài).
                Quan trọng nhất là phải trả lời đúng từng bước giải bài toán này.
                Để hiển thị các ký tự đặc biệt phiền bạn generate character đặc biệt theo công thức của hàm js sau:
                        // Render cả câu có chữ + công thức (inline \( ... \) hoặc ${'$'}${'$'} ... ${'$'}${'$'})
                        function renderInlineText(rawHtml) {
                          const el = document.getElementById('c');
                          el.innerHTML = rawHtml; // rawHtml có delimiters \( \) / ${'$'}${'$'}
                          try {
                            renderMathInElement(el, {
                              delimiters: [
                                {left: "\\(", right: "\\)", display: false},
                                {left: "${'$'}${'$'}", right: "${'$'}${'$'}", display: true}
                              ],
                              throwOnError: false
                            });
                          } catch (e) {
                            el.textContent = 'Render error: ' + e.message;
                          }
                        }
                """.trimIndent()
                )
            }

            /* 5. Gọi Gemini  */
            val json = model.generateContent(prompt).text ?: return UiState.Empty

            /* 6. Parse JSON thành List<String>  */
            val baitoan = Gson().fromJson(json, BaiToan::class.java)

            return UiState.Success(baitoan)

        } catch (ex: Exception) {
            Log.e("GenerateSolutionSteps", "Error generating steps: ${ex.message}", ex)
            return UiState.Failure(ex.message ?: "Unknown error")
        }
    }
}