package com.techuntried.accountsbasics2.domain.model

data class GameEconomy(
    val unlockLevelCoins: Int = 100,
    val bombCoins: Int = 100,
    val addTimeCoins: Int = 50,
    val tryAgainCoins: Int = 25,
    val correctAnswerCoins: Int = 5,
)