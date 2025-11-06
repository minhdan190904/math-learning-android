package com.trilogy.mathlearning.ui.presentation.bottom_navigation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

// ====== MÀU SẮC (dễ chỉnh) ======
private val BarBlue        = Color(0xFF123B7A)   // nền thanh
private val PillSelected   = Color(0xFF2D86FF)   // nền item chọn
private val PillSelectedBg = PillSelected.copy(alpha = 0.25f) // nền “pill” mờ
private val IconSelected   = Color.White
private val IconUnselected = Color(0xFFDFE8FF)
private val TextSelected   = Color.White
private val TextUnselected = Color(0xFFBFD3FF)

// ====== STYLE ======
private val BarHeight      = 70.dp
private val PillWidth      = 92.dp
private val PillHeight     = 46.dp
private val PillRadius     = 16.dp
private val IconSizeOff    = 22.dp
private val IconSizeOn     = 28.dp
private val LiftOn         = 6.dp

@Composable
fun CustomBottomBar(
    items: List<BottomDest>,
    currentRoute: String?,
    onItemClick: (String) -> Unit
) {
    // Thanh nền bo nhẹ ở hai góc trên, không đổ bóng
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(BarBlue)
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(BarHeight),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                val selected = currentRoute == item.route

                val iconSize by animateDpAsState(
                    targetValue = if (selected) IconSizeOn else IconSizeOff,
                    animationSpec = tween(180, easing = FastOutSlowInEasing)
                )
                val lift by animateDpAsState(
                    targetValue = if (selected) LiftOn else 0.dp,
                    animationSpec = tween(160, easing = FastOutSlowInEasing)
                )
                val iconTint by animateColorAsState(
                    targetValue = if (selected) IconSelected else IconUnselected,
                    animationSpec = tween(120)
                )
                val textTint by animateColorAsState(
                    targetValue = if (selected) TextSelected else TextUnselected,
                    animationSpec = tween(120)
                )

                // Tắt ripple hoàn toàn:
                val interaction = remember { MutableInteractionSource() }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable(
                            indication = null,                 // <- NO RIPPLE
                            interactionSource = interaction
                        ) { onItemClick(item.route) },
                    contentAlignment = Alignment.Center
                ) {

                    // “Pill” mờ phía sau khi được chọn (không shadow để khỏi "bóng xấu")
                    if (selected) {
                        Box(
                            modifier = Modifier
                                .width(PillWidth)
                                .height(PillHeight)
                                .offset { IntOffset(x = 0, y = -lift.roundToPx()) }
                                .clip(RoundedCornerShape(PillRadius))
                                .background(PillSelectedBg)
                        )
                    }

                    // Icon + Label
                    Column(
                        modifier = Modifier
                            .offset { IntOffset(0, -lift.roundToPx()) },
                        horizontalAlignment = Alignment.CenterHorizontally
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
                            color = textTint,
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
