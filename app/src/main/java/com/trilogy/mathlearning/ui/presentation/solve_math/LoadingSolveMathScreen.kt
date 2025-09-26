package com.trilogy.mathlearning.ui.presentation.solve_math

import android.graphics.BitmapFactory
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.trilogy.mathlearning.R

@Composable
fun LoadingSolveMathScreen() {
    val context = LocalContext.current
    val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.ic_loading_screen)
    Scaffold(
        topBar = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.TopEnd
            ){
                IconButton(onClick = {

                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_close),
                        contentDescription = "Back",
                        tint = Color.Black,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        },
        containerColor = Color.White
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CustomImageDisplay(
                bitmap = bitmap,
                onImageClick = {},
                backgroundColor = Color.Transparent
            )

            Spacer(Modifier.height(8.dp))

            Text("AI đang giải bài toán của bạn...", textAlign = TextAlign.Center)

            Spacer(Modifier.height(32.dp))

            //blue
            CircularProgressIndicator(
                strokeWidth = 2.dp,
                color = Color.Blue,
                modifier = Modifier.size(48.dp)
            )
        }
    }
}

@Preview
@Composable
fun LoadingSolveMathScreenPreview() {
    LoadingSolveMathScreen()
}