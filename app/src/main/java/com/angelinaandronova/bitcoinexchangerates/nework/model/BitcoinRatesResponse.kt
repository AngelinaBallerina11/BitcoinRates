package com.angelinaandronova.bitcoinexchangerates.nework.model


data class BitcoinRatesResponse(
    val status: String,
    val name: String,
    val unit: String,
    val period: String,
    val description: String,
    val values: ArrayList<Point>
)