package com.bignerdranch.codapizza.core.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.inset
import com.bignerdranch.codapizza.core.OrderStatus


// TODO: Try getting the pizza slice vector image working with something like this:
// https://medium.com/androiddevelopers/making-jellyfish-move-in-compose-animating-imagevectors-and-applying-agsl-rendereffects-3666596a8888
@Composable
fun OrderProgressBar(orderStatus: OrderStatus, modifier: Modifier = Modifier) {
//    val context = LocalContext.current
//    val pizzaDrawable = remember { ContextCompat.getDrawable(context, R.drawable.ic_pizza_slice) }

    val infiniteTransition = rememberInfiniteTransition(label = "infinite")
//    val pizzaRotation by infiniteTransition.animateFloat(
//        label = "rotation",
//        initialValue = 0f,
//        targetValue = 360f,
//        animationSpec = infiniteRepeatable(
//            animation = tween(3000, easing = LinearEasing)
//        )
//    )

    val completedWidthPercentage by animateFloatAsState(
        label = "completedWidth",
        targetValue = orderStatus.ordinal.toFloat() / (OrderStatus.entries.size - 1),
        animationSpec = tween(1500)
    )

    Canvas(modifier = modifier) {
        val barVerticalPadding = size.height * 0.35f
        val barHeight = size.height - (barVerticalPadding * 2)
        val barSize = size.copy(height = barHeight)
        drawRect(
            color = Color.LightGray,
            size = barSize,
            topLeft = Offset(x = 0f, y = barVerticalPadding)
        )

        val completedWidth = size.width * completedWidthPercentage
        val completedBarSize = barSize.copy(width = completedWidth)
        drawRect(
            color = Color.Red,
            size = completedBarSize,
            topLeft = Offset(x = 0f, y = barVerticalPadding)
        )

        inset(vertical = size.height * 0.3f) {
            (0 until OrderStatus.entries.size).forEach { separatorIndex ->
                val separatorWidthMidpointPercentage = separatorIndex.toFloat() / (OrderStatus.entries.size - 1)
                val separatorSize = size.copy(width = 20f)
                val separatorWidthOffset = (size.width * separatorWidthMidpointPercentage) - (separatorSize.width / 2)
                val separatorOffset = Offset(x =  separatorWidthOffset, y = 0f)
                drawRect(
                    color = Color.DarkGray,
                    size = separatorSize,
                    topLeft = separatorOffset
                )
            }
        }

//        if (pizzaDrawable != null) {

            drawCircle(
                color = Color.Yellow,
                radius = size.height * 0.45f,
                center = Offset(x = completedWidth, y = size.height * 0.5f)
            )

//            val iconSize = Size(width = size.height * 0.8f, height = size.height * 0.8f)
//            val iconImageBitmap = pizzaDrawable.toBitmap(iconSize.width.toInt(), iconSize.height.toInt())
//            val iconTopLeft = Offset(x = completedWidth - (iconSize.width / 2), y = size.height * 0.1f)
//            val iconCenter = Offset(x = completedWidth, y = size.height * 0.5f)
//
//            rotate(
//                degrees = pizzaRotation,
//                pivot = iconCenter
//            ) {
//                drawImage(
//                    image = iconImageBitmap.asImageBitmap(),
//                    topLeft = iconTopLeft
//                )
//            }
//        }
    }
}
