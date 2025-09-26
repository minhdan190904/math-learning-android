package com.trilogy.mathlearning.ui.presentation.solve_math

//noinspection UsingMaterialAndMaterial3Libraries
import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.RadioButton
import androidx.compose.material.RadioButtonDefaults
import androidx.compose.material.TabRowDefaults.Divider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SolveStyleScreen(
    options: List<SolveStyle>,
    selectedId: String?,
    onSelected: (SolveStyle) -> Unit,
    onClickSelect: () -> Unit,
    onBack: () -> Unit
) {
    val expanded = remember { mutableStateListOf<String>() }

    BackHandler {
        onBack()
    }

    Scaffold(
        topBar = {
            TopBarSolveStyleScreen(
                onClickSelect = { onClickSelect() }
            )
        },
        containerColor = Color.White
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(options, key = { it.id }) { opt ->
                val isSelected = opt.id == selectedId
                val isExpanded = opt.id in expanded

                StyleCard(
                    option = opt,
                    selected = isSelected,
                    expanded = isExpanded,
                    onToggleExpand = {
                        if (isExpanded) expanded.remove(opt.id) else expanded.add(opt.id)
                    },
                    onClick = { onSelected(opt) },
                    gradientColors = GradientPresets.Ocean,
                    durationMs = 1600,
                    thickness = 2.dp
                )
            }
        }
    }
}

@Composable
fun TopBarSolveStyleScreen(
    onClickSelect: () -> Unit
) {
    Surface(
        tonalElevation = 3.dp,
        color = Color(0xFFF5F5F5).copy(alpha = 0.9f),
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    "Chọn cách AI giải toán",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "Bạn có thể đổi lựa chọn bất kỳ lúc nào.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = "Chọn",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                color = Color(0xFF42A5F5),
                modifier = Modifier
                    .clickable(onClick = onClickSelect)
                    .padding(8.dp)
            )
        }
    }
}


// ---------- Card: glass + chạy viền + tags + pill + nút i ----------
@Composable
private fun StyleCard(
    option: SolveStyle,
    selected: Boolean,
    expanded: Boolean,
    onToggleExpand: () -> Unit,
    onClick: () -> Unit,
    gradientColors: List<Color>,
    durationMs: Int,
    thickness: Dp
) {
    val shape = RoundedCornerShape(18.dp)
    val base = Color(0xFFF5F5F5)
    val onBase = MaterialTheme.colorScheme.onSurface

    // LED border animation
    val infinite = rememberInfiniteTransition(label = "grad")
    val phase by infinite.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(durationMs, easing = LinearEasing)),
        label = "phase"
    )

    val borderBrush = if (selected)
        Brush.linearGradient(
            colors = gradientColors,
            start = Offset(1000f * phase, 0f),
            end = Offset(0f, 1000f * (1f - phase))
        )
    else Brush.linearGradient(
        listOf(
            MaterialTheme.colorScheme.outline.copy(0.22f),
            MaterialTheme.colorScheme.outline.copy(0.12f)
        )
    )

    val elev by animateDpAsState(if (selected) 10.dp else 2.dp, label = "elev")
    val bgColor by animateColorAsState(
        if (selected) base.copy(alpha = 0.5f) else base,
        label = "bg"
    )
    val alpha = if (option.enabled) 1f else 0.45f

    Surface(
        tonalElevation = elev,
        shadowElevation = elev,
        shape = shape,
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elev, shape, clip = false)
            .border(thickness, borderBrush, shape)
            .alpha(alpha)
            .clickable(enabled = option.enabled) { onClick() }
    ) {
        Column(Modifier.background(bgColor).padding(horizontal = 16.dp, vertical = 14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                GlassEmoji(option.emoji)

                Spacer(Modifier.width(14.dp))

                Column(Modifier.weight(1f)) {
                    Text(
                        option.title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = onBase,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            option.subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                }

                // expand info
                IconButton(onClick = onToggleExpand) {
                    // vẽ chevron thủ công (nhẹ)
                    Chevron(expanded = expanded, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                RadioButton(
                    selected = selected,
                    onClick = { if (option.enabled) onClick() },
                    enabled = option.enabled,
                    colors = RadioButtonDefaults.colors(
                        selectedColor = Color(0xFF42A5F5),
                        unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledColor = MaterialTheme.colorScheme.outline.copy(0.6f)
                    ),
                    modifier = Modifier.scale(1.3f)
                )
            }

            // tags row
            if (option.tags.isNotEmpty()) {
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    option.tags.forEach { TagChip(it) }
                }
            }

            // expanded detail
            if (expanded) {
                Spacer(Modifier.height(10.dp))
                Divider(color = MaterialTheme.colorScheme.outline.copy(0.15f))
                Spacer(Modifier.height(10.dp))
                Text(
                    "Chi tiết: phong cách này ưu tiên cấu trúc lời giải gọn, tập trung kết quả; có thể đính kèm các bước tóm tắt khi cần.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// ---------- Components ----------
@Composable
private fun GlassEmoji(emoji: String) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    listOf(Color(0x66FFFFFF), Color(0x11FFFFFF))
                )
            )
            .border(1.dp, Color.White.copy(0.35f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(emoji, fontSize = 22.sp)
    }
}

@Composable
private fun TagChip(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(MaterialTheme.colorScheme.secondary.copy(0.1f))
            .border(
                1.dp,
                MaterialTheme.colorScheme.secondary.copy(0.35f),
                RoundedCornerShape(50)
            )
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}

@Composable
private fun Chevron(expanded: Boolean, color: Color) {
    // vẽ V ngược / V thường
    Canvas(Modifier.size(20.dp)) {
        val w = size.width
        val h = size.height
        val y = if (expanded) h * 0.6f else h * 0.4f
        val y2 = if (expanded) h * 0.4f else h * 0.6f
        drawLine(
            color, Offset(w * 0.2f, y), Offset(w * 0.5f, y2),
            strokeWidth = 2.5f, cap = StrokeCap.Round
        )
        drawLine(
            color, Offset(w * 0.8f, y), Offset(w * 0.5f, y2),
            strokeWidth = 2.5f, cap = StrokeCap.Round
        )
    }
}

// ---------- Preview ----------
@Preview(showBackground = true)
@Composable
fun DemoStylePicker() {
    var selected by remember { mutableStateOf("short") }
    val options = remember {
        listOf(
            SolveStyle("short", "Trả lời ngắn gọn", "Khi bạn chỉ cần kết quả/lời giải ngắn.", "✍️",
                tags = listOf("Nhanh", "Gọn")),
            SolveStyle("interactive", "Tương tác / Trắc nghiệm", "Hỏi–đáp dẫn dắt, luyện tập.", "🤝",
                enabled = true, tags = listOf("Trắc nghiệm")),

            SolveStyle("funny", "Hài hước / dí dỏm", "Giải thích nhẹ nhàng kèm ví dụ vui.", "😂",
                tags = listOf("Thư giãn", "Vui vẻ")),

            SolveStyle("exam", "Dạng đề thi", "Trình bày giống lời giải trong đề thi chính thức.", "📑",
                tags = listOf("Thi cử", "Chuẩn mực")),

            SolveStyle("fastest", "Nhanh nhất có thể", "Ưu tiên tốc độ, ít giải thích.", "⚡",
                tags = listOf("Nhanh", "Tốc độ")),

            SolveStyle("story", "Kể chuyện minh họa", "Biến toán học thành câu chuyện dễ nhớ.", "📖",
                tags = listOf("Sáng tạo", "Dễ nhớ")),
            SolveStyle("step", "Giải thích từng bước", "Hợp người mới, đầy đủ lý giải.", "🧠",
                tags = listOf("Chi tiết", "Từng bước")),
            SolveStyle("teacher", "Phong cách giảng dạy", "Giống giáo viên trên lớp, có ví dụ.", "🎓",
                tags = listOf("Dễ hiểu", "Ví dụ")),
            SolveStyle("beginner", "Dành cho người mới", "Ngôn ngữ đơn giản, trình bày rõ.", "🐣",
                tags = listOf("Cơ bản")),
            SolveStyle("academic", "Học thuật / Chứng minh", "Cho học sinh nâng cao, luyện thi.", "🖊️",
                tags = listOf("Chặt chẽ"))
        )
    }
//    SolveStyleScreen(
//        options = options,
//        selectedId = selected,
//        onSelected = { selected = it.id }
//    )
}
