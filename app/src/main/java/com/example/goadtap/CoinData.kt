package com.example.goadtap

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "coin_data")
data class CoinData(
    @PrimaryKey val id: Int = 1,
    val coins: Long = 0,
    val tapValue: Int = 1,
    val upgrade1Level: Int = 0,
    val upgrade2Level: Int = 0,
    val upgrade3Level: Int = 0,
    val upgrade4Level: Int = 0,

    val isSuccess: Boolean = false,
)
