package com.github.ushiosan23.networkutils.http

import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest

/**
 * Internal object to manage http connections.
 *
 * This object also contains some base request utilities.
 */
internal object HttpConnector {

	/* ---------------------------------------------------------
	 *
	 * Internal properties
	 *
	 * --------------------------------------------------------- */

	/**
	 * Http client connection
	 */
	private var httpClient: HttpClient? = null

	/* ---------------------------------------------------------
	 *
	 * Public methods
	 *
	 * --------------------------------------------------------- */

	/**
	 * Get http instance client
	 *
	 * @param createNew Determine if gets a new client instance or last used client
	 * @param replaceClient Determine if current client object is replaced by new instance
	 *
	 * @return [HttpClient] instance
	 */
	fun getClient(createNew: Boolean = false, replaceClient: Boolean = true): HttpClient {
		if (httpClient == null || createNew) {
			if (replaceClient)
				httpClient = createHttpClient()
			else
				return createHttpClient()
		}

		return httpClient!!
	}


	/**
	 * Create a valid http request body data from map object.
	 * This method is used to make request with body data normally used in HTML forms (POST, PUT, PATCH)
	 *
	 * @param map Map data to transform
	 *
	 * @return [HttpRequest.BodyPublisher] data object
	 */
	fun mapToHttpData(map: Map<String, String>): HttpRequest.BodyPublisher {
		// Output http request data
		val dataString = StringBuilder()

		// Iterate all entries to make data
		map.entries.forEach { entry ->
			// Check if data is not empty
			// And add "&" to separate map data
			if (dataString.isNotEmpty()) dataString.append("&")

			// Insert data to string builder
			dataString.append(URLEncoder.encode(entry.key, Charsets.UTF_8))
			dataString.append("=")
			dataString.append(URLEncoder.encode(entry.value, Charsets.UTF_8))
		}

		// Make request data structure
		return HttpRequest.BodyPublishers.ofString(dataString.toString())
	}

	/* ---------------------------------------------------------
	 *
	 * Internal methods
	 *
	 * --------------------------------------------------------- */

	/**
	 * Create new http client instance
	 *
	 * @return [HttpClient] instance object
	 */
	private fun createHttpClient(): HttpClient = HttpClient.newBuilder()
		.version(HttpClient.Version.HTTP_2)
		.build()

}
