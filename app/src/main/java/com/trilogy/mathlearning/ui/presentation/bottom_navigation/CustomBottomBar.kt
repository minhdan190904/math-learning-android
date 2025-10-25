package com.trilogy.mathlearning.ui.presentation.bottom_navigation
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.runtime.*
import androidx.compose.animation.core.animateDpAsState
import kotlin.math.roundToInt

// Màu sắc
private val BarBlue = Color(0xFF143C77)          // nền thanh
private val PillBlue = Color(0xFF2B66C5)         // viên bo nhạt hơn
private val TextSelected = Color(0xFFFFFFFF)     // chữ khi chọn
private val TextUnselected = Color(0xFFBFD3FF)   // chữ chưa chọn
private val IconUnselected = Color(0xFFDFE8FF)

@Composable
fun CustomBottomBar(
    items: List<BottomDest>,
    currentRoute: String?,
    onItemClick: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(BarBlue)
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                val selected = currentRoute == item.route
                val iconSize by animateDpAsState(
                    targetValue = if (selected) 28.dp else 22.dp,
                    animationSpec = spring(dampingRatio = 0.8f, stiffness = 300f)
                )
                val lift by animateDpAsState(
                    targetValue = if (selected) 6.dp else 0.dp,
                    animationSpec = tween(180)
                )
                val labelColor by animateColorAsState(
                    targetValue = if (selected) TextSelected else TextUnselected,
                    animationSpec = tween(120)
                )
                val iconTint by animateColorAsState(
                    targetValue = if (selected) Color.White else IconUnselected,
                    animationSpec = tween(120)
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable { onItemClick(item.route) },
                    contentAlignment = Alignment.Center
                ) {
                    // Viên bo nổi (pill)
                    if (selected) {
                        Box(
                            modifier = Modifier
                                .width(84.dp)
                                .height(44.dp)
                                .offset { IntOffset(0, (-lift.roundToPx())) }
                                .shadow(4.dp, RoundedCornerShape(16.dp), clip = false)
                                .clip(RoundedCornerShape(16.dp))
                                .background(PillBlue.copy(alpha = 0.35f))
                        )
                    }

                    // Icon + Label
                    Column(
                        modifier = Modifier
                            .offset { IntOffset(0, (-lift.roundToPx())) },
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label,
                            modifier = Modifier.size(iconSize),
                            tint = iconTint
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = item.label,
                            color = labelColor,
                            fontSize = 11.sp,
                            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}
