package com.angelinaandronova.bitcoinexchangerates

import com.angelinaandronova.bitcoinexchangerates.mainScreen.MainViewModel
import com.angelinaandronova.bitcoinexchangerates.nework.model.BitcoinRatesResponse
import com.angelinaandronova.bitcoinexchangerates.nework.model.Point
import org.mockito.ArgumentMatchers.anyFloat
import org.mockito.ArgumentMatchers.anyString
import kotlin.random.Random

object TestUtils {

    fun getDefaultSuccessfulResponse(numPoints: Int) = BitcoinRatesResponse(
        status = "",
        name = "",
        unit = "",
        period = "",
        description = "",
        values = arrayListOf<Point>().apply {
            repeat(numPoints) {
                add(Point(1f, 1f))
            }
        }
    )

    fun getRandomTimespan() = MainViewModel.TimeSpan.values()[Random.nextInt(2)]

}
