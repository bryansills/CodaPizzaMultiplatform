package com.bignerdranch.codapizza.core

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource

@Composable
actual fun getImageResource(imageResource: ImageResource): Painter {
    return painterResource(id = imageResource.resourceId)
}

private val ImageResource.resourceId: Int
    get() = when (this) {
        else -> R.drawable.pizza_crust
    }