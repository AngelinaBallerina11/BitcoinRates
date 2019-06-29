package com.angelinaandronova.bitcoinexchangerates.model


data class BitcoinRatesResponse(
    val status: String,
    val name: String,
    val unit: String,
    val period: String,
    val description: String,
    val values: ArrayList<Point>
)