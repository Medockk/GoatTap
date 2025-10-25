package com.example.goadtap.presentation.mainscreen

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.goadtap.R
import kotlinx.coroutines.launch

@Composable
fun CoinAnimation(
    modifier: Modifier,
    startPosition: Offset,
    endPosition: Offset,
    animationStart: Boolean,
    onAnimationEnd: () -> Unit
) {
    val offsetY = remember { Animatable(0f)}
    val offsetX = remember { Animatable(0f)}

    val alpha = remember { Animatable(1f)}
    var isAnim by remember { mutableStateOf(false)}
    val durationMillis = 1500

    LaunchedEffect(animationStart) {
        if (animationStart){
            isAnim = true
        }
    }

    LaunchedEffect(isAnim) {
        if (isAnim){
            // Запускаем анимацию одновременно для смещения и исчезновения
            launch {
                offsetY.animateTo(
                    targetValue = endPosition.y - startPosition.y, // Вертикаль
                    animationSpec = tween(durationMillis = durationMillis, easing = LinearEasing)
                )
            }
            launch {
                offsetX.animateTo(
                    targetValue = endPosition.x - startPosition.x, // Горизонталь
                    animationSpec = tween(durationMillis = durationMillis, easing = LinearEasing)
                )
            }

            launch {
                alpha.animateTo(
                    targetValue = 0f, // Полностью прозрачный
                    animationSpec = tween(durationMillis = durationMillis, easing = LinearEasing)
                )
            }
        }
    }

    LaunchedEffect(alpha.value, isAnim) {
        if (isAnim && alpha.value == 0f) {
            onAnimationEnd()
        }
    }

    val density = LocalDensity.current
    Box(modifier = modifier){
        Image(
            painter = painterResource(id = R.drawable.coin), // Замените на изображение
            contentDescription = "coin",
            modifier = Modifier
                .size(56.dp)
                .offset(
                    y = with(density) { startPosition.y.toDp() } + offsetY.value.dp,
                    x = with(density) { startPosition.x.toDp() } + offsetX.value.dp
                )
                .alpha(alpha.value)
        )
    }


}