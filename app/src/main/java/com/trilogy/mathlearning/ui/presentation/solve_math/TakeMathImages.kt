package com.trilogy.mathlearning.ui.presentation.solve_math

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.AddAPhoto
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.trilogy.mathlearning.MathInlineSentence
import com.trilogy.mathlearning.R
import com.trilogy.mathlearning.data.repository.BaiToan
import com.trilogy.mathlearning.utils.UiState

@Composable
fun TakeMathImages(
    navController: NavController
) {
    val vm: TakeMathViewModel = hiltViewModel()
    val images by vm.images.collectAsState()
    val selectedItem by vm.selectedItemSolveStyle.collectAsState()
    val baiToanState by vm.result.collectAsState()
    val uploadState by vm.imageUploadState.collectAsState()

    var showSolveStyleOptions by remember { mutableStateOf(false) }
    val listSolveStyle = SolveStyles.all

    val context = LocalContext.current
    val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.ic_no_image)

    Scaffold(
        topBar = {
            TopBar { navController.popBackStack() }
        },
        bottomBar = {
            ConfirmBottomBar(
                onConfirm = { vm.solve(images) },
                enabled = images.isNotEmpty() && baiToanState !is UiState.Loading
            )
        },
        containerColor = Color.White
    ) { innerPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text("Chọn phong cách giải toán", style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.height(8.dp))

            SolveStyleOption(
                solveStyle = selectedItem,
                modifier = Modifier.padding(bottom = 8.dp),
                onClick = { showSolveStyleOptions = true }
            )

            Spacer(Modifier.height(16.dp))

            Text("Ảnh", style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.height(8.dp))

            if (images.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    CustomImageDisplay(
                        bitmap = bitmap,
                        onImageClick = {},
                        backgroundColor = Color.White
                    )
                }
            }

            if (images.isNotEmpty()) {
                Row(Modifier.fillMaxWidth()) {
                    BitmapThumb(
                        bitmap = images.first(),
                        onRemove = { vm.removeAt(0) }
                    )
                }
                Spacer(Modifier.height(10.dp))
            }

            if (images.size > 1) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    images.drop(1).forEachIndexed { idx, bm ->
                        BitmapThumb(
                            bitmap = bm,
                            onRemove = { vm.removeAt(idx + 1) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (images.size < 4) {
                        repeat(4 - images.size) {
                            Spacer(Modifier.weight(1f))
                        }
                    }
                }
                Spacer(Modifier.height(10.dp))
            }

            Text(
                text = "Chỉ được thêm tối đa 4 ảnh",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(12.dp))

            OutlinedButton(
                onClick = { navController.navigate("editor") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = images.size < 4
            ) {
                Icon(Icons.Outlined.AddAPhoto, contentDescription = null, tint = Color.LightGray)
                Spacer(Modifier.width(8.dp))
                Text("Thêm ảnh", color = Color.LightGray)
            }

            Spacer(Modifier.height(8.dp))

            when (uploadState) {
                is UiState.Loading -> {
                    Text(
                        text = "Đang tải ảnh lên...",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.height(4.dp))
                }
                is UiState.Failure -> {
                    val err = (uploadState as UiState.Failure).error ?: "Tải ảnh thất bại"
                    Text(
                        text = err,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Red
                    )
                    Spacer(Modifier.height(4.dp))
                }
                else -> {}
            }

            Spacer(Modifier.height(16.dp))

            when (baiToanState) {
                is UiState.Success -> {
                    val result = (baiToanState as UiState.Success<BaiToan>).data
                    Column {
                        Text(
                            text = "Loại bài toán",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(Modifier.height(8.dp))
                        MathInlineSentence(raw = result.category)

                        Spacer(Modifier.height(16.dp))

                        Text(
                            text = "Kết quả giải toán",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(Modifier.height(8.dp))
                        MathInlineSentence(raw = result.result)

                        Spacer(Modifier.height(16.dp))

                        Text(
                            text = "Các bước giải",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(Modifier.height(8.dp))

                        result.steps.forEach { step ->
                            MathInlineSentence(raw = "• $step")
                        }
                    }
                }
                is UiState.Failure -> {
                    val err = (baiToanState as UiState.Failure).error ?: "Giải toán thất bại"
                    Text(
                        text = err,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Red
                    )
                }
                else -> {}
            }
        }
    }

    if (showSolveStyleOptions) {
        SolveStyleScreen(
            options = listSolveStyle,
            selectedId = selectedItem.id,
            onSelected = { vm.setSelectedItemSolveStyle(it) },
            onClickSelect = { showSolveStyleOptions = false },
            onBack = { showSolveStyleOptions = false }
        )
    }

    if (baiToanState is UiState.Loading) {
        LoadingSolveMathScreen()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    onClose: () -> Unit
) {
    TopAppBar(
        title = { Text("Thêm ảnh giải toán") },
        navigationIcon = {
            IconButton(onClick = onClose) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Quay lại",
                    tint = Color.Black
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xFFF5F5F5).copy(alpha = 0.9f),
            titleContentColor = Color.Black
        )
    )
}

@Composable
private fun ConfirmBottomBar(onConfirm: () -> Unit, enabled: Boolean = true) {
    Surface(
        tonalElevation = 3.dp,
        color = Color(0xFFF5F5F5).copy(alpha = 0.9f)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .imePadding()
                .padding(16.dp)
        ) {
            Button(
                onClick = onConfirm,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF42A5F5)),
                enabled = enabled
            ) {
                Text("Xác nhận", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
fun SolveStyleOption(
    modifier: Modifier = Modifier,
    solveStyle: SolveStyle,
    onClick: () -> Unit = {}
) {
    OutlinedCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color.LightGray),
        colors = CardDefaults.outlinedCardColors(
            containerColor = Color.White
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(solveStyle.emoji)
                Spacer(Modifier.width(8.dp))
                Text(
                    text = solveStyle.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = Color.LightGray
            )
        }
    }
}

@Composable
private fun TutorBanner() {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFFF5F5F5),
        tonalElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            Modifier
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(0.08f))
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(0.25f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Image,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer.copy(0.9f)
                    )
                }
                Spacer(Modifier.width(6.dp))
                Box(
                    Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.secondary.copy(0.25f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Image,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer.copy(0.9f)
                    )
                }
            }

            Spacer(Modifier.width(12.dp))

            Column(Modifier.weight(1f)) {
                Text(
                    "Hỏi đáp 1:1 cá nhân",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
                )
                Text(
                    "Hỏi gia sư, cho đến khi bạn hiểu",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun BitmapThumb(bitmap: Bitmap, onRemove: () -> Unit, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(4.dp)
    Box(
        modifier = modifier
            .clip(shape)
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(0.25f), shape)
    ) {
        CustomImageDisplay(
            bitmap = bitmap,
            onImageClick = {}
        )

        Box(
            modifier = Modifier
                .padding(0.dp)
                .align(Alignment.TopEnd)
        ) {
            IconButton(
                onClick = onRemove,
                modifier = Modifier
                    .scale(0.4f)
                    .clip(CircleShape)
                    .background(Color.Gray)
                    .border(1.dp, Color(0xFFE0E0E0), CircleShape)
            ) {
                Icon(Icons.Outlined.Close, contentDescription = "Xoá", tint = Color.White)
            }
        }
    }
}

@Preview
@Composable
fun DemoReview() {
    val navController = NavController(context = LocalContext.current)
    TakeMathImages(navController)
}
