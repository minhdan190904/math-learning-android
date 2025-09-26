package com.trilogy.mathlearning.ui.presentation.article

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.trilogy.mathlearning.MathInlineSentence
import com.trilogy.mathlearning.MathTex
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class MathArticle(
    val id: String,
    val title: String,
    val blocks: List<ArticleBlock>
)

@Serializable
data class GridItem(
    val text: String? = null,
    val tex: String? = null,
    val imageUrl: String? = null
)


@Serializable
sealed class ArticleBlock {
    @Serializable
    @SerialName("title")
    data class Title(val text: String) : ArticleBlock()

    @Serializable
    @SerialName("section")
    data class Section(val text: String) : ArticleBlock()

    @Serializable
    @SerialName("text")
    data class Text(val text: String) : ArticleBlock()

    @Serializable
    @SerialName("text_katex")
    data class TextKatex(val raw: String) : ArticleBlock()

    @Serializable
    @SerialName("math")
    data class Math(val tex: String, val display: Boolean = true) : ArticleBlock()

    @Serializable
    @SerialName("image")
    data class Image(
        val url: String,
        val caption: String? = null,
        val widthDp: Int? = null,
        val heightDp: Int? = null
    ) : ArticleBlock()

    @Serializable
    @SerialName("spacer")
    data class Spacer(val heightDp: Int = 12) : ArticleBlock()

    @Serializable
    @SerialName("grid")
    data class Grid(
        val columns: Int,
        val items: List<GridItem>
    ) : ArticleBlock()
}

private val jsonFmt = Json {
    ignoreUnknownKeys = true
    classDiscriminator = "type"
}

fun parseArticle(json: String): MathArticle = jsonFmt.decodeFromString(json)

fun parseBold(input: String): AnnotatedString {
    if (!input.contains("**")) return AnnotatedString(input)
    val regex = Regex("\\*\\*(.+?)\\*\\*")
    val builder = AnnotatedString.Builder()
    var lastIndex = 0
    for (m in regex.findAll(input)) {
        val range = m.range
        if (range.first > lastIndex) builder.append(input.substring(lastIndex, range.first))
        val boldText = m.groupValues[1]
        builder.withStyle(SpanStyle(fontWeight = FontWeight.SemiBold)) {
            append(boldText)
        }
        lastIndex = range.last + 1
    }
    if (lastIndex < input.length) builder.append(input.substring(lastIndex))
    return builder.toAnnotatedString()
}

@Composable
fun ArticleScreen(
    json: String,
    modifier: Modifier = Modifier
) {
    val article = remember(json) { parseArticle(json) }
    Surface(modifier = modifier.fillMaxSize(), color = Color.White) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = article.title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = Color(0xFF42A5F5),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
            }

            items(article.blocks) { block ->
                RenderBlock(block)
            }
        }
    }
}

@Composable
fun RenderBlock(block: ArticleBlock) {
    when (block) {
        is ArticleBlock.Title -> {
            Text(
                text = block.text,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }
        is ArticleBlock.Section -> {
            Text(
                text = block.text,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
        is ArticleBlock.Text -> {
            Text(text = parseBold(block.text), style = MaterialTheme.typography.bodyLarge)
        }
        is ArticleBlock.TextKatex -> {
            MathInlineSentence(raw = block.raw, modifier = Modifier.fillMaxWidth())
        }
        is ArticleBlock.Math -> {
            MathTex(tex = block.tex, displayMode = block.display, modifier = Modifier.fillMaxWidth())
        }
        is ArticleBlock.Image -> {
            val w = block.widthDp?.dp ?: Modifier.fillMaxWidth()
            val h = block.heightDp?.dp
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                AsyncImage(
                    model = block.url,
                    contentDescription = block.caption,
                    modifier = Modifier
                        .then(if (block.widthDp != null) Modifier.width(block.widthDp.dp) else Modifier.fillMaxWidth())
                        .then(if (h != null) Modifier.height(h) else Modifier.wrapContentHeight())
                )
                if (!block.caption.isNullOrBlank()) {
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = block.caption!!,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
                    )
                }
            }
        }
        is ArticleBlock.Spacer -> {
            Spacer(Modifier.height(block.heightDp.dp))
        }

        is ArticleBlock.Grid -> {
            RenderGrid(block)
        }
    }
}

@Composable
private fun GridCell(item: GridItem, modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        when {
            item.tex != null -> {
                // inline/compact trong ô để không đội chiều cao
                MathTex(
                    tex = item.tex,
                    displayMode = false,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 40.dp, max = 140.dp)
                )
            }
            item.imageUrl != null -> {
                AsyncImage(
                    model = item.imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 72.dp, max = 160.dp),
                    contentScale = ContentScale.Fit // giữ tỉ lệ, không cắt
                )
            }
            else -> {
                Text(
                    text = item.text.orEmpty(),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}


@Composable
fun RenderGrid(block: ArticleBlock.Grid) {
    val cols = block.columns.coerceAtLeast(1)
    val rows = block.items.chunked(cols)

    val lineColor = Color(0xFFE0E0E0)
    val lineW = 1.dp
    val cellPad = 8.dp

    // Khung + kẻ dọc liên tục theo số cột
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(lineW, lineColor) // viền ngoài
            .drawBehind {
                val w = size.width
                val h = size.height
                val px = lineW.toPx()
                // vẽ vạch dọc giữa các cột (bỏ cột đầu/cuối)
                for (i in 1 until cols) {
                    val x = w * i / cols
                    drawRect(
                        color = lineColor,
                        topLeft = Offset(x - px / 2f, 0f),
                        size = Size(px, h)
                    )
                }
            }
    ) {
        Column {
            rows.forEachIndexed { rIndex, row ->
                Row(Modifier.fillMaxWidth()) {
                    row.forEach { item ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 88.dp) // tránh ô quá thấp
                        ) {
                            GridCell(
                                item,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(cellPad)
                            )
                        }
                    }
                    // bổ sung ô rỗng nếu hàng cuối thiếu cột để căn đều
                    if (row.size < cols) {
                        repeat(cols - row.size) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .heightIn(min = 88.dp)
                            )
                        }
                    }
                }
                // kẻ ngang giữa các hàng (liền nhau)
                if (rIndex < rows.size - 1) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(lineW)
                            .background(lineColor)
                    )
                }
            }
        }
    }
}


@Preview
@Composable
fun ArticleFromAsset() {
    val context = LocalContext.current
    val fileName = "article_derivative.json"
    val json = remember(fileName) {
        loadJsonFromAsset(context, "articles/$fileName")
    }
    ArticleScreen(json = json)
}

fun loadJsonFromAsset(context: Context, fileName: String): String {
    return context.assets.open(fileName).bufferedReader().use { it.readText() }
}



