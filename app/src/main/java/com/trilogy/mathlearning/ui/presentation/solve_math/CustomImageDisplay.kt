package com.trilogy.mathlearning.ui.presentation.solve_math

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity

@Composable
fun CustomImageDisplay(
    bitmap: Bitmap,
    onImageClick: () -> Unit,
    backgroundColor: Color = Color(0xFFE0E0E0),
    modifier: Modifier = Modifier
) {

        var boxWidth by remember { mutableIntStateOf(0) }

        Box(
            modifier = modifier
                .fillMaxWidth()
                .onGloballyPositioned {
                    boxWidth = it.size.width
                }
                .height(with(LocalDensity.current) { boxWidth.toDp() })
                .background(color = backgroundColor)
                .clickable { onImageClick() },
            contentAlignment = Alignment.Center
        ) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Selected Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
                    .clickable(
                        onClick = onImageClick,
                        indication = null,
                        interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
                    )
            )
        }

}