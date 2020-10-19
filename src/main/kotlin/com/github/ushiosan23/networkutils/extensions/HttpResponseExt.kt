package com.github.ushiosan23.networkutils.extensions

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import java.net.http.HttpResponse


/**
 * Get json element request response data or `null` if is not valid json data
 */
val HttpResponse<*>.jsonObject: JsonObject?
	get() = try {
		if (!isJson()) null
		else Json.decodeFromString<JsonObject>(body().toString())
	} catch (err: Exception) {
		err.printStackTrace()
		null
	}

/**
 * Determine if response data is json element
 *
 * @return [Boolean] operation result
 */
fun HttpResponse<*>.isJson(): Boolean = try {
	Json.decodeFromString<JsonObject>(body().toString())
	true
} catch (err: Exception) {
	err.printStackTrace()
	false
}
