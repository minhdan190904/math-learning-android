package com.trilogy.mathlearning.ui.presentation.math

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.trilogy.mathlearning.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamListScreen(
    items: List<ExamIndexItem>,
    onOpen: (ExamIndexItem) -> Unit,
    onBack: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Navy,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                title = { Text("Đề thi Toán", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = painterResource(R.drawable.ic_back),
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            )
        }
    ) { inner ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(inner),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(items) { item ->
                ExamRow(item, onClick = { onOpen(item) })
            }
        }
    }
}

@Composable
private fun ExamRow(item: ExamIndexItem, onClick: () -> Unit) {
    Surface(
        shape = examShapes().large,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        border = CardStroke,
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // icon trái theo ảnh (ô bo tròn xanh)
            Box(
                modifier = Modifier.size(56.dp)
                    .clip(examShapes().large)
                    .background(Navy),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_loading_screen), // ảnh “graduation/box” của bạn
                    contentDescription = null,
                    modifier = Modifier.size(28.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(item.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(R.drawable.ic_clock), // đồng hồ nhỏ
                        contentDescription = null, tint = ChipBlue
                    )
                    Spacer(Modifier.width(6.dp))
                    Text("${item.durationMin} phút", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            // Nút tròn vàng mũi tên
            Box(
                modifier = Modifier.size(40.dp)
                    .clip(examShapes().large)
                    .background(AccentYellow),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_back),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}
