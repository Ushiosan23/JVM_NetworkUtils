package com.github.ushiosan23.networkutils.extensions

import java.net.http.HttpHeaders

/**
 * Convert headers to immutable map
 *
 * @return [Map] Header results
 */
fun HttpHeaders.toMap(): Map<String, String> {
	val fMap = mutableMapOf<String, String>()

	map().entries.forEach {
		fMap[it.key] = it.value.toString()
	}

	return fMap.toMap()
}
