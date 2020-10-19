package com.github.ushiosan23.networkutils.extensions

/**
 * Convert number to hexadecimal string
 *
 * @return [String] hexadecimal format
 */
fun Number.toHexString(): String = Integer.toHexString(this.toInt())
