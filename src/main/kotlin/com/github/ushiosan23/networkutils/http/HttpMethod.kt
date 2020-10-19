package com.github.ushiosan23.networkutils.http

/**
 * Request method types
 *
 * @see (w3c methods)[https://www.w3.org/Protocols/rfc2616/rfc2616-sec9.html] for more information.
 */
enum class HttpMethod(val acceptData: Boolean = false) {
	GET,
	PATCH,
	DELETE,
	PUT(true),
	POST(true)
}
