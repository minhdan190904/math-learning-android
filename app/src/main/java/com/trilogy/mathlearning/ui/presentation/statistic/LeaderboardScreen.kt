package com.trilogy.mathlearning.ui.presentation.statistic

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trilogy.mathlearning.domain.model.StatisticLikeResDto
import com.trilogy.mathlearning.domain.model.StatisticResDto
import com.trilogy.mathlearning.utils.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardScreen(
    vm: StatisticViewModel,
    onBack: () -> Unit
) {
    val pointState by vm.pointLeaderboardState.collectAsState()
    val likeState by vm.likeLeaderboardState.collectAsState()

    var tab by remember { mutableStateOf(0) } // 0: điểm, 1: likes

    LaunchedEffect(Unit) {
        if (pointState is UiState.Empty) vm.loadTopUsersByPoints()
        if (likeState is UiState.Empty) vm.loadTopUsersByLikes()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Bảng xếp hạng") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "Quay lại"
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = Color(0xFFF6F7FB)
    ) { inner ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
        ) {
            TabRow(
                selectedTabIndex = tab,
                containerColor = Color.White
            ) {
                Tab(
                    selected = tab == 0,
                    onClick = { tab = 0 },
                    text = { Text("Điểm") }
                )
                Tab(
                    selected = tab == 1,
                    onClick = { tab = 1 },
                    text = { Text("Lượt thích") }
                )
            }

            when (tab) {
                0 -> LeaderboardTabPoint(pointState)
                1 -> LeaderboardTabLike(likeState)
            }
        }
    }
}

/* ---- Tab: theo điểm ---- */

@Composable
private fun LeaderboardTabPoint(state: UiState<StatisticResDto>) {
    when (state) {
        UiState.Loading, UiState.Empty -> Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) { CircularProgressIndicator() }

        is UiState.Failure -> Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) { Text(state.error ?: "Lỗi tải bảng xếp hạng") }

        is UiState.Success -> {
            val data = state.data
            LeaderboardList(
                topUsers = data.topUsers.map {
                    LeaderboardItemData(
                        name = it.name.ifBlank { it.email.substringBefore("@") },
                        email = it.email,
                        value = it.point,
                        rank = it.rank
                    )
                },
                meValue = data.currentUser.point,
                meRank = data.currentUser.rank,
                valueSuffix = "XP"
            )
        }
    }
}

/* ---- Tab: theo like ---- */

@Composable
private fun LeaderboardTabLike(state: UiState<StatisticLikeResDto>) {
    when (state) {
        UiState.Loading, UiState.Empty -> Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) { CircularProgressIndicator() }

        is UiState.Failure -> Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) { Text(state.error ?: "Lỗi tải bảng xếp hạng") }

        is UiState.Success -> {
            val data = state.data
            LeaderboardList(
                topUsers = data.topUsers.map {
                    LeaderboardItemData(
                        name = it.name.ifBlank { it.email.substringBefore("@") },
                        email = it.email,
                        value = it.like,
                        rank = it.rank
                    )
                },
                meValue = data.currentUser.like,
                meRank = data.currentUser.rank,
                valueSuffix = "like"
            )
        }
    }
}

/* ---- Shared layout ---- */

private data class LeaderboardItemData(
    val name: String,
    val email: String,
    val value: Int,
    val rank: Int
)

@Composable
private fun LeaderboardList(
    topUsers: List<LeaderboardItemData>,
    meValue: Int,
    meRank: Int,
    valueSuffix: String
) {
    val bluePrimary = Color(0xFF1565C0)
    val blueLight = Color(0xFF42A5F5)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        // Card user hiện tại
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(bluePrimary, blueLight)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "#$meRank",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text("Hạng của bạn", fontWeight = FontWeight.SemiBold)
                    Text(
                        "$meValue $valueSuffix",
                        color = Color(0xFF8A8F98),
                        fontSize = 13.sp
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 8.dp)
        ) {
            itemsIndexed(topUsers, key = { _, u -> u.email }) { index, user ->
                LeaderboardRow(
                    index = index,
                    data = user
                )
            }
        }
    }
}

@Composable
private fun LeaderboardRow(
    index: Int,
    data: LeaderboardItemData
) {
    val rank = data.rank
    val name = data.name
    val email = data.email
    val value = data.value

    val isTop1 = rank == 1
    val isTop2 = rank == 2
    val isTop3 = rank == 3

    val bgColor = when {
        isTop1 -> Color(0xFFFFF7E0)
        isTop2 -> Color(0xFFF2F6FF)
        isTop3 -> Color(0xFFEFFBF3)
        else -> Color.White
    }

    val pillColor = when {
        isTop1 -> Color(0xFFFFE082)
        isTop2 -> Color(0xFFBBDEFB)
        isTop3 -> Color(0xFFC8E6C9)
        else -> Color(0xFFF3F4F6)
    }

    val bluePrimary = Color(0xFF1565C0)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "#$rank",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = bluePrimary
            )

            Spacer(Modifier.width(10.dp))

            val initial = name.firstOrNull()?.uppercaseChar()?.toString()
                ?: email.firstOrNull()?.uppercaseChar()?.toString()
                ?: "?"

            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE3F2FD)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initial,
                    fontWeight = FontWeight.Bold,
                    color = bluePrimary
                )
            }

            Spacer(Modifier.width(10.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = name,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
                Text(
                    text = email,
                    color = Color(0xFF8A8F98),
                    fontSize = 12.sp
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(pillColor)
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$value XP",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
