package com.example.goadtap.presentation

import android.media.SoundPool
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.center
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.util.UnstableApi
import com.example.goadtap.R
import com.example.goadtap.presentation.mainscreen.MainScreen
import com.example.goadtap.presentation.ui.theme.myFont
import com.example.goadtap.presentation.upgrade.UpgradeDialog

class MainActivity : ComponentActivity() {

    private var soundPool: SoundPool? = null
    private var tapSound: Int = 0
    private var purchaseSound: Int = 0
    private var eeeSound: Int = 0

    @androidx.annotation.OptIn(UnstableApi::class)
    @RequiresApi(35)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        soundPool = SoundPool.Builder()
            .setMaxStreams(3)
            .build()

        tapSound = soundPool?.load(this, R.raw.tap, 1) ?: 0
        purchaseSound = soundPool?.load(this, R.raw.pay, 1) ?: 0
        eeeSound = soundPool?.load(this, R.raw.eee, 1) ?: 0

        setContent {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xff0C376D))
                    .windowInsetsPadding(WindowInsets.statusBars),
                color = Color(0xff0C376D)
            ) {
                TapperApp()
            }
        }
    }


    data class CoinAnimationData(val offset: Offset)

    @UnstableApi
    @RequiresApi(35)
    @Composable
    fun TapperApp() {

        val viewModel: MainViewModel = viewModel(factory = MainViewModel.Factory())
        val coinData by viewModel.coinData.collectAsState(initial = null)
        val coinAnimations = remember { mutableStateListOf<CoinAnimationData>() }
        var animationStart by remember { mutableStateOf(false) }


        LaunchedEffect(coinAnimations.size) {
            if (coinAnimations.isEmpty() && animationStart) {
                animationStart = false
            }
        }

        Column {
            Row(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .padding(horizontal = 10.dp)
                    .fillMaxWidth()
            ) {

                Text(
                    text = "Цель: Купить Платок" + if ((coinData?.upgrade4Level
                            ?: 0) > 0 && coinData?.isSuccess == true
                    ) "✅" else "",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 10.dp)
                )
                Spacer(Modifier.weight(1f))
                IconButton(
                    onClick = {
                        if (viewModel.playerIsOn.value) viewModel.stopPlayer() else viewModel.startPlayer()
                    },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = Color(0xff217C2E)
                    )
                ) {
                    Icon(
                        painter = painterResource(id = if (viewModel.playerIsOn.value) R.drawable.sound_on else R.drawable.sound_off),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier
                            .padding(9.dp)
                            .size(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(31.dp))

            MainScreen(
                coinData = coinData,
                coinAnimations = coinAnimations,
                animationStart = animationStart,
                onRelease = {
                    // Анимируем сбор монет
                    if (coinAnimations.isNotEmpty() && !animationStart) {
                        animationStart = true
                    }
                    soundPool?.autoPause()
                },
                onTap = {
                    soundPool?.play(tapSound, 10f, 10f, 0, 0, 1f)
                    viewModel.onTap()

                    if (!animationStart) {
                        coinAnimations.add(CoinAnimationData(offset = it))
                    }
                    if (coinAnimations.size >= 15 && !animationStart) {
                        animationStart = true
                    }
                },
                onOpenUpgrades = { viewModel.showUpgradeDialog.value = true }
            )
        }



        if (viewModel.showUpgradeDialog.value) {
            UpgradeDialog(
                onDismiss = { viewModel.showUpgradeDialog.value = false },
                onPurchase = { upgradeType, cost, upgradeValue ->

                    // При покупке улучшения появляется звук покупки
                    viewModel.purchaseUpgrade(upgradeType, cost, upgradeValue)
                    soundPool?.play(purchaseSound, 0.5f, 0.5f, 0, 0, 1f)


                },
                coins = coinData?.coins ?: 0,
                upgrade1Level = coinData?.upgrade1Level ?: 0,
                upgrade2Level = coinData?.upgrade2Level ?: 0,
                upgrade3Level = coinData?.upgrade3Level ?: 0,
                upgrade4Level = coinData?.upgrade4Level ?: 0,
            )
        }

        if (coinData?.isSuccess == true) {
            viewModel.showUpgradeDialog.value = false
            soundPool?.play(eeeSound, 1f, 1f, 0, 0, 1f)
        }

        var boxSize by remember { mutableStateOf(IntSize.Zero) }
        AnimatedVisibility(
            visible = coinData?.isSuccess == true,
            enter = scaleIn(animationSpec = tween(durationMillis = 2000)),
            exit = scaleOut(animationSpec = tween(durationMillis = 1000))
        ) {
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = Color.Transparent,
                modifier = Modifier
                    .onGloballyPositioned {
                        boxSize = it.size
                    }
                    .background(
                        Brush.radialGradient(
                            colorStops = arrayOf(
                                0.0f to Color(0xffffffff),
                                0.25f to Color(0xFFCEE0F5),
                                1.0f to Color(0xff034A9B),
                            ),
                            center = Offset(
                                boxSize.center.x.toFloat(),
                                boxSize.center.y.toFloat()
                            ),
                            radius = boxSize.width.toFloat() + 100f
                        ),
                    )
            ) {
                Image(
                    painter = painterResource(id = R.drawable.end_back),
                    contentDescription = null,
                    modifier = Modifier
                        .alpha(0.7f)
                        .fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                Column(
                    Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Spacer(modifier = Modifier.width(1.dp))

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.end_platok),
                            contentDescription = null,
                            modifier = Modifier
                                .padding(horizontal = 21.dp)
                                .fillMaxWidth(),
                        )

                        Text(
                            text = "Игра\nокончена",
                            style = TextStyle(
                                color = Color(0xff77FF00),
                                fontSize = 48.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                fontFamily = myFont
                            ),
                            modifier = Modifier
                                .padding(bottom = 48.dp)
                        )
                    }
                    Button(
                        onClick = {
                            viewModel.restart()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xff7FEA20)
                        ),
                        modifier = Modifier
                            .padding(start = 30.dp, end = 30.dp, bottom = 48.dp)
                            .height(54.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            "Начать заново",
                            color = Color.White,
                            style = TextStyle(
                                fontFamily = myFont,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }
            }
        }

    }

}

