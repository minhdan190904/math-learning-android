package com.trilogy.mathlearning

import android.annotation.SuppressLint
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import org.json.JSONObject

/**
 * Render một công thức KaTeX trong WebView.
 * - displayMode = false để inline, true để dạng block.
 */
@Composable
fun MathTex(
    tex: String,
    modifier: Modifier = Modifier,
    displayMode: Boolean = true
) {
    val context = LocalContext.current
    val safeTex = remember(tex) { JSONObject.quote(tex) }
    var pageReady by remember { mutableStateOf(false) }

    AndroidView(
        modifier = modifier,
        factory = {
            WebView(context).apply {
                initMathWebView()
                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        pageReady = true
                        evaluateJavascript(
                            "renderLatex($safeTex, ${if (displayMode) "true" else "false"})",
                            null
                        )
                    }
                }
                loadUrl("file:///android_asset/math.html")
            }
        },
        update = { web ->
            if (pageReady) {
                web.evaluateJavascript(
                    "renderLatex($safeTex, ${if (displayMode) "true" else "false"})",
                    null
                )
            }
        }
    )
}

/**
 * Render cả câu có chữ và công thức inline \( ... \) hoặc display $$ ... $$.
 * Ví dụ:
 * raw = "Cho phương trình \\( \\int_{0}^{1} x^{2} \\, dx = \\frac{1}{3} \\) :"
 */
@Composable
fun MathInlineSentence(
    raw: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val safe = remember(raw) { JSONObject.quote(raw) }
    var pageReady by remember { mutableStateOf(false) }

    AndroidView(
        modifier = modifier,
        factory = {
            WebView(context).apply {
                initMathWebView()
                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        pageReady = true
                        evaluateJavascript("renderInlineText($safe)", null)
                    }
                }
                loadUrl("file:///android_asset/math.html")
            }
        },
        update = { web ->
            if (pageReady) {
                web.evaluateJavascript("renderInlineText($safe)", null)
            }
        }
    )
}

@SuppressLint("SetJavaScriptEnabled")
private fun WebView.initMathWebView() {
    settings.javaScriptEnabled = true
    // Tùy chọn thêm:
    // setBackgroundColor(Color.TRANSPARENT)
    // WebView.setWebContentsDebuggingEnabled(true)
}

/* =====================
 *       PREVIEW
 * ===================== */

@Preview(showBackground = true)
@Composable
private fun Preview_InlineSentence() {
    MathInlineSentence(
        raw = "Ví dụ, với hàm \\(f(x) = x^4\\), ta có \\(f'(x) = 4x^3\\) và \\(f''(x) = 12x^2\\)"
    )


}
