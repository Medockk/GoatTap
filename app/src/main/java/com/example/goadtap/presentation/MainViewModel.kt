package com.example.goadtap.presentation

import android.media.MediaPlayer
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.goadtap.AppDatabase
import com.example.goadtap.CoinData
import com.example.goadtap.CoinDataDao
import com.example.goadtap.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class MainViewModel(
    private val coinDataDao: CoinDataDao,
    private val mediaPlayer: MediaPlayer
): ViewModel() {


    private val _coinData = MutableStateFlow<CoinData?>(null)
    val coinData: StateFlow<CoinData?> = _coinData.asStateFlow()

    val showUpgradeDialog = mutableStateOf(false)
    val playerIsOn = mutableStateOf(true)

    init {
        loadCoinData()
    }

    private fun loadCoinData() {
        viewModelScope.launch {
            coinDataDao.getCoinData().collect { data ->
                _coinData.value = data ?: CoinData() // Initialize if null
                if (data == null) {
                    coinDataDao.insertCoinData(CoinData())
                } else {
                    _coinData.update {
                        it?.copy(
                           isSuccess = data.upgrade4Level > 0
                        )
                    }
                }
            }
        }
    }

    fun restart() {
        _coinData.value = CoinData()
        viewModelScope.launch(Dispatchers.IO) {
            coinDataDao.updateCoinData(CoinData())
        }
    }

    var job: Job? = null
    fun onTap() {
        job?.cancel("")

         _coinData.value = coinData.value?.copy(
             coins = coinData.value?.coins!! + coinData.value?.tapValue!!
         ) ?: CoinData()

        job = viewModelScope.launch(Dispatchers.IO) {
            coinDataDao.updateCoinData(coinData.value?: CoinData())
        }
    }


    fun purchaseUpgrade(upgradeType: Int, cost: Long, upgradeValue: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val currentData = _coinData.value ?: CoinData()
            if (currentData.coins >= cost) {
                val newCoins = currentData.coins - cost

                val updatedData = when (upgradeType) {
                    1 -> currentData.copy(coins = newCoins, tapValue = currentData.tapValue + upgradeValue, upgrade1Level = currentData.upgrade1Level + 1)
                    2 -> currentData.copy(coins = newCoins, tapValue = currentData.tapValue + upgradeValue, upgrade2Level = currentData.upgrade2Level + 1)
                    3 -> currentData.copy(coins = newCoins, tapValue = currentData.tapValue + upgradeValue, upgrade3Level = currentData.upgrade3Level + 1)
                    4 -> currentData.copy(coins = newCoins, tapValue = currentData.tapValue + upgradeValue, upgrade4Level = currentData.upgrade4Level + 1)
                    else -> currentData // В случае косяка
                }
                coinDataDao.updateCoinData(updatedData)
            }
        }
    }


    fun startPlayer(){
        mediaPlayer.setVolume(0.2f, 0.2f)
        mediaPlayer.isLooping = true
        mediaPlayer.start()
        playerIsOn.value = true
    }

    fun stopPlayer(){
        mediaPlayer.pause()
        playerIsOn.value = false
    }

    companion object {
        fun Factory(): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                val application = checkNotNull(extras[APPLICATION_KEY])

                val database = AppDatabase.getDatabase(application)
                val mediaPlayer = MediaPlayer.create(application, R.raw.background_sound)
                return MainViewModel(database.coinDataDao(), mediaPlayer) as T
            }
        }
    }
}