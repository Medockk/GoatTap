package com.example.goadtap

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow


@Dao
interface CoinDataDao {

    @Query("SELECT * FROM coin_data WHERE id = 1")
    fun getCoinData(): Flow<CoinData?> // Используем Flow для асинхронного получения данных

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCoinData(coinData: CoinData)

    @Update
    suspend fun updateCoinData(coinData: CoinData)
}