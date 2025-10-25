package com.trilogy.mathlearning.ui.presentation.math

import android.annotation.SuppressLint
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

/**
 * WebView nạp assets/math.html rồi gọi hàm JS:
 *   - renderInlineText(raw)   // text có \(..\) hoặc $$..$$
 *   - renderLatex(tex, true|false) // block/inline TeX
 *
 * math.html trong assets đã định nghĩa 2 hàm trên (KaTeX + auto-render).
 */
sealed interface MathContent {
    data class Inline(val raw: String) : MathContent
    data class Latex(val tex: String, val display: Boolean) : MathContent
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun MathHtmlView(
    content: MathContent,
    modifier: Modifier = Modifier
) {
    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            WebView(ctx).apply {
                webViewClient = android.webkit.WebViewClient()
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.cacheMode = WebSettings.LOAD_NO_CACHE
                loadUrl("file:///android_asset/math.html") // dùng file math.html đã cung cấp
            }
        },
        update = { wv ->
            wv.postDelayed({
                fun esc(s: String) = s
                    .replace("\\", "\\\\")
                    .replace("'", "\\'")
                    .replace("\n", "\\n")

                when (content) {
                    is MathContent.Inline ->
                        wv.evaluateJavascript("renderInlineText('${esc(content.raw)}')", null)
                    is MathContent.Latex ->
                        wv.evaluateJavascript("renderLatex('${esc(content.tex)}', ${content.display})", null)
                }
            }, 50)
        }
    )
}
