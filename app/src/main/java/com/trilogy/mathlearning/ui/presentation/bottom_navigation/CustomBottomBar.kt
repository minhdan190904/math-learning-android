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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val BarBlue        = Color(0xFF123B7A)
private val PillSelected   = Color(0xFF2D86FF)
private val PillSelectedBg = PillSelected.copy(alpha = 0.25f)
private val IconSelected   = Color.White
private val IconUnselected = Color(0xFFDFE8FF)
private val TextSelected   = Color.White
private val TextUnselected = Color(0xFFBFD3FF)

private val BarHeight      = 70.dp
private val ItemRadius     = 16.dp
private val IconSizeOff    = 22.dp
private val IconSizeOn     = 28.dp

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
                .height(BarHeight),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items.forEach { item ->
                val selected = currentRoute == item.route

                val bgColor by animateColorAsState(
                    targetValue = if (selected) PillSelectedBg else Color.Transparent,
                    animationSpec = tween(220, easing = FastOutSlowInEasing),
                    label = "bgColor"
                )
                val iconTint by animateColorAsState(
                    targetValue = if (selected) IconSelected else IconUnselected,
                    animationSpec = tween(180),
                    label = "iconTint"
                )
                val textTint by animateColorAsState(
                    targetValue = if (selected) TextSelected else TextUnselected,
                    animationSpec = tween(180),
                    label = "textTint"
                )
                val iconSize by animateDpAsState(
                    targetValue = if (selected) IconSizeOn else IconSizeOff,
                    animationSpec = tween(200, easing = FastOutSlowInEasing),
                    label = "iconSize"
                )

                val interaction = remember { MutableInteractionSource() }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(ItemRadius))
                        .background(bgColor)
                        .clickable(
                            indication = null,
                            interactionSource = interaction
                        ) { onItemClick(item.route) },
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 6.dp, vertical = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label,
                            modifier = Modifier.size(iconSize),
                            tint = iconTint
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = item.label,
                            color = textTint,
                            fontSize = 10.sp,
                            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}
