package com.angelinaandronova.bitcoinexchangerates.model

/**
 * Data class representing a point on a line chart
 * @param x is time in milliseconds
 * @param y is Bitcoin price at time {@code x}
 */
data class Point(val x: Float, val y: Float)