package com.example.goadtap.presentation.upgrade

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.goadtap.R
import com.example.goadtap.presentation.ui.theme.myFont
import kotlin.math.pow

@Composable
fun UpgradeDialog(onDismiss: () -> Unit,
                  onPurchase: (Int, Long, Int) -> Unit,
                  coins: Long,
                  upgrade1Level: Int,
                  upgrade2Level: Int,
                  upgrade3Level: Int,
                  upgrade4Level: Int
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(0.1f))
            .clickable { onDismiss() },
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(modifier = Modifier
            .fillMaxWidth()
            .clickable {}
            .background(Color.Black, RoundedCornerShape(32.dp))
        ) {

            Spacer(modifier = Modifier.height(33.dp))

            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xff7FEA20)
                ),
                modifier = Modifier
                    .padding(horizontal = 30.dp)
                    .height(54.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    "Закрыть",
                    color = Color.White,
                    style = TextStyle(
                        color = Color.White,
                        fontWeight = FontWeight.Medium,
                        fontSize = 20.sp,
                        fontFamily = myFont
                    )
                )
            }

            Spacer(modifier = Modifier.height(28.dp))


            //Upgrade 1
            val upgrade1Cost = (10 * 1.30.pow((upgrade1Level).toDouble())).toLong()
            val coins1 = (1 * 1.10.pow((upgrade2Level).toDouble())).toInt()
            UpgradeItem(
                upgradeName = "Нитка",
                image = R.drawable.nit,
                cost = upgrade1Cost,
                coins = coins1,
                currentLevel = upgrade1Level,
                isOpening = true
            ){
                if (coins >= upgrade1Cost) {
                    onPurchase(1, upgrade1Cost,
                        coins1
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            //Upgrade 2
            val upgrade2Cost = (200 * 1.50.pow((upgrade2Level).toDouble())).toLong()
            val coins2 = (5 * 1.10.pow((upgrade2Level).toDouble())).toInt()
            UpgradeItem(
                upgradeName = "Клубок",
                image = R.drawable.klubok,
                cost = upgrade2Cost,
                coins = coins2,
                currentLevel = upgrade2Level,
                isOpening = upgrade1Level>0
            ){
                if (coins >= upgrade2Cost) {
                    onPurchase(2, upgrade2Cost,
                        coins2
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            //Upgrade 3
            val upgrade3Cost = (800 * 1.50.pow((upgrade3Level).toDouble())).toLong()
            val coins3 = (10 * 1.10.pow((upgrade3Level).toDouble())).toInt()
            UpgradeItem(
                upgradeName = "Спицы",
                image = R.drawable.spitsy,
                cost = upgrade3Cost,
                coins = coins3,
                currentLevel = upgrade3Level,
                isOpening = upgrade2Level>0
            ){
                if (coins >= upgrade3Cost) {
                    onPurchase(3, upgrade3Cost,
                        coins3
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            //Upgrade 4
            val upgrade4Cost = (10000 * 2.10.pow((upgrade4Level).toDouble())).toLong()
            val coins4 = (100 * 1.10.pow((upgrade4Level).toDouble())).toInt()
            UpgradeItem(
                upgradeName = "Платок",
                image = R.drawable.scarf,
                cost = upgrade4Cost,
                coins = coins4,
                currentLevel = upgrade4Level,
                isOpening = upgrade3Level>0
            ){
                if (coins >= upgrade3Cost) {
                    onPurchase(4, upgrade4Cost,
                        coins4
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

        }
    }
}

@Composable
fun UpgradeItem(
    upgradeName: String,
    @DrawableRes image: Int,
    cost: Long,
    coins: Int,
    currentLevel: Int,
    isOpening: Boolean,
    onPurchase: () -> Unit
) {

    Box(
        modifier = Modifier
            .padding(horizontal = 30.dp)
            .background(if (isOpening) Color(0xFF0D58FE) else Color(0xFF0839A5), RoundedCornerShape(32.dp))
            .clip(RoundedCornerShape(32.dp))
            .clickable {
                onPurchase()
            }
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 9.dp)
                .fillMaxWidth()
        ) {
            AsyncImage(
                image,
                contentDescription = null,
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .size(64.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop,
            )
            Spacer(modifier = Modifier.width(13.dp))
            Column() {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$upgradeName ",
                        style = TextStyle(
                            fontSize = 14.sp,
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = myFont
                        )
                    )
                    Text(
                        text = currentLevel.toString(),
                        style = TextStyle(
                            fontSize = 14.sp,
                            color = Color(0xff00FF44),
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = myFont
                        )
                    )
                }
                Spacer(modifier = Modifier.height(15.dp))

                Text(
                    text = "Цена $cost",
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = Color(0xffC2C2C2),
                        fontWeight = FontWeight.Medium,
                        fontFamily = myFont
                    )
                )
            }

            Spacer(modifier = Modifier.weight(1f))
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Image(
                    painter = painterResource(id = R.drawable.coin),
                    contentDescription = null,
                    modifier = Modifier
                        .size(44.dp)
                        .clickable(enabled = isOpening) {
                            onPurchase()
                        }
                )
                Spacer(modifier = Modifier.height(5.dp))
                Text(text = "+$coins", style = TextStyle(
                    fontSize = 24.sp,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = myFont
                ))

            }

        }
    }
    Spacer(modifier = Modifier.height(20.dp))

}