package com.alandvgarcia.tmdbapp.android.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.Chip
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CollapsingMovieToolbar(
    title: String,
    category: String,
    lazyListState: LazyListState,
    changeCategory: () -> Unit
) {
    var previousOffset = 0
    var scrolledY = 0f

    Row(
        modifier = Modifier
            .graphicsLayer {
                scrolledY += lazyListState.firstVisibleItemScrollOffset - previousOffset
                translationY = scrolledY * 0.5f
                previousOffset = lazyListState.firstVisibleItemScrollOffset
            }
            .height(72.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            title,
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp,
            maxLines = 1,
            modifier = Modifier.padding(16.dp)
        )
        Chip(onClick = { changeCategory() }, modifier = Modifier.padding(16.dp)) {
            Text(text = category)
        }

    }
}
