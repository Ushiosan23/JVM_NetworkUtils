package com.github.ushiosan23.networkutils.http

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import java.net.http.HttpResponse

/**
 * Check if request is valid json response
 */
val HttpResponse<*>.isValidJson: Boolean
	get() = try {
		when (val body = body()) {
			is JsonElement, JsonObject -> true
			is String -> {
				Json.decodeFromString<JsonElement>(body)
				true
			}
			else -> false
		}
	} catch (ignored: Exception) {
		false
	}

/**
 * Get json element from response object
 *
 * @return [JsonElement] if is valid json or `null` if not
 */
fun HttpResponse<*>.getJson(): JsonElement? {
	// Check if is valid json
	if (!isValidJson) return null

	// Decode element
	return try {
		when (val body = body()) {
			is JsonElement -> body
			is JsonObject -> body
			is String -> Json.decodeFromString(body)
			else -> null
		}
	} catch (ignored: Exception) {
		null
	}
}

/**
 * Get custom element from response object
 *
 * @return [T] object or `null` if is not valid json object
 */
inline fun <reified T> HttpResponse<*>.getJson(clazz: Class<T>): T? {
	// Check if is valid json
	if (!isValidJson) return null

	// Decode element
	return try {
		when (val body = body()) {
			is JsonElement -> Json.decodeFromJsonElement<T>(body)
			is JsonObject -> Json.decodeFromJsonElement<T>(body)
			is String -> Json.decodeFromString<T>(body)
			else -> null
		}
	} catch (ignored: Exception) {
		null
	}
}
