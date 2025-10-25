package com.trilogy.mathlearning.ui.presentation.math

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

private fun looksLikeTex(s: String) =
    s.contains("\\(") || s.contains("\\)") || s.contains("$$")

@Composable
fun RenderInlineOrText(raw: String, modifier: Modifier = Modifier) {
    if (looksLikeTex(raw)) {
        MathHtmlView(MathContent.Inline(raw), modifier)
    } else {
        Text(raw, modifier)
    }
}

@Composable
fun RenderTexBlock(tex: String, display: Boolean, modifier: Modifier = Modifier) {
    MathHtmlView(MathContent.Latex(tex, display), modifier)
}
