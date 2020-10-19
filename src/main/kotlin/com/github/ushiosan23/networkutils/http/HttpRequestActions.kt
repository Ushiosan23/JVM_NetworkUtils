package com.github.ushiosan23.networkutils.http

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.suspendCancellableCoroutine
import java.net.HttpURLConnection
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpHeaders
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.*
import javax.net.ssl.SSLSession
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.resume

/**
 * Http object connector
 */
object HttpRequestActions : CoroutineScope {

	/* ---------------------------------------------------------
	 *
	 * Internal properties
	 *
	 * --------------------------------------------------------- */

	/**
	 * Coroutine job executor
	 */
	private val coroutineJob: Job = Job()

	/* ---------------------------------------------------------
	 *
	 * Implemented properties
	 *
	 * --------------------------------------------------------- */

	/**
	 * Coroutine context property.
	 *
	 * This coroutine is not launched in main thread
	 */
	override val coroutineContext: CoroutineContext
		get() = coroutineJob + Dispatchers.Default

	/* ---------------------------------------------------------
	 *
	 * Public properties
	 *
	 * --------------------------------------------------------- */

	/**
	 * Property used to manage client elements
	 */
	@JvmStatic
	var createNewClientEachRequest: Boolean = false

	/* ---------------------------------------------------------
	 *
	 * Synchronous methods
	 *
	 * --------------------------------------------------------- */

	/**
	 * Check if url exists
	 *
	 * @param url Target request url
	 *
	 * @return [Boolean] result
	 */
	@JvmStatic
	fun urlExists(url: URI): Boolean = try {
		val client = HttpConnector.getClient(true, replaceClient = false)
		val request = makeHeaderRequest(url)
		val response = client.send(request, HttpResponse.BodyHandlers.discarding())

		response.statusCode() == HttpURLConnection.HTTP_OK
	} catch (err: Exception) {
		false
	}

	/**
	 * Check if url exists and return throwable if occurs an error
	 *
	 * @param url Target request url
	 * @param block Callback called if url exists
	 *
	 * @return [Throwable] Url error
	 */
	@JvmStatic
	fun urlExistsActionCatchError(
		url: URI,
		block: () -> Unit
	): Throwable? = try {
		val client = HttpConnector.getClient(true, replaceClient = false)
		val request = makeHeaderRequest(url)
		val response = client.send(request, HttpResponse.BodyHandlers.discarding())

		if (response.statusCode() == HttpURLConnection.HTTP_OK) {
			block.invoke()
			null
		} else Exception("Response code: ${response.statusCode()}")
	} catch (err: Exception) {
		err
	}

	/**
	 * Check if url exists and return throwable if occurs an error
	 *
	 * @param url Target request url
	 * @param block Callback called if url exists
	 *
	 * @see urlExistsActionCatchError
	 */
	@JvmStatic
	fun urlExistsAction(
		url: URI,
		block: () -> Unit
	) {
		urlExistsActionCatchError(url, block)?.printStackTrace()
	}

	/**
	 * Get url headers.
	 *
	 * This method doesn't make a full request, it just makes a HEAD http request and it's faster.
	 * You can get the document size or a specific header without downloading all data.
	 *
	 * @param url Target url
	 *
	 * @return [HttpHeaders] result
	 */
	@JvmStatic
	fun urlHeaders(url: URI): HttpHeaders {
		val client = HttpConnector.getClient(true, replaceClient = false)
		val request = makeHeaderRequest(url)

		val response = client.send(request, HttpResponse.BodyHandlers.discarding())
		return response.headers()
	}

	/**
	 * Get url content length
	 *
	 * @param url Target request url
	 *
	 * @return [Long] content size in bytes or `-1` if header not found
	 */
	@JvmStatic
	fun urlContentLength(url: URI): Long {
		val headers = urlHeaders(url)

		return try {
			val optional = headers.firstValueAsLong("content-length")
			if (optional.isPresent) optional.asLong else -1L
		} catch (err: Exception) {
			err.printStackTrace()
			-1L
		}
	}

	/**
	 * Create synchronous http request.
	 *
	 * This method blocks current thread until the request completes or times out.
	 *
	 * @param url Target request url
	 * @param method Target request method
	 * @param data Http data to send
	 * @param headers Custom http headers
	 *
	 * @return [HttpResponse] response result data
	 * @see HttpMethod
	 */
	@JvmStatic
	fun requestURI(
		url: URI,
		method: HttpMethod = HttpMethod.GET,
		data: Map<String, String>? = null,
		headers: Array<String>? = null
	): HttpResponse<String> {
		val client = HttpConnector.getClient(createNewClientEachRequest)
		val request = makeRequestObject(url, method, data, headers)

		return try {
			client.send(request, HttpResponse.BodyHandlers.ofString())
		} catch (err: Exception) {
			err.printStackTrace()
			errorRequest(client, request, err)
		}
	}

	/* ---------------------------------------------------------
	 *
	 * Callback methods
	 *
	 * --------------------------------------------------------- */

	/**
	 * Create callback http request.
	 *
	 * This method doesn't block current thread but it's necessary to use block function
	 * (In Java it's recommend to use lambdas).
	 *
	 * @param url Target request url
	 * @param method Target request method
	 * @param data Http data to send
	 * @param headers Custom http headers
	 * @param block Interface block callback
	 *
	 * @see HttpMethod
	 */
	@JvmStatic
	fun requestURI(
		url: URI,
		method: HttpMethod = HttpMethod.GET,
		data: Map<String, String>? = null,
		headers: Array<String>? = null,
		block: (response: HttpResponse<String>) -> Unit
	) {
		val client = HttpConnector.getClient(createNewClientEachRequest)
		val request = makeRequestObject(url, method, data, headers)

		client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
			.thenApply(block::invoke)
			.exceptionally { block.invoke(errorRequest(client, request, it)) }
			.thenAccept { }
	}

	/* ---------------------------------------------------------
	 *
	 * Asynchronous methods
	 *
	 * --------------------------------------------------------- */

	/**
	 * Create asynchronous http request.
	 *
	 * This method doesn't block current thread.
	 *
	 * @param url Target request url
	 * @param method Target request method
	 * @param data Http data to send
	 * @param headers Custom http headers
	 *
	 * @return [HttpResponse] response result data
	 * @see HttpMethod
	 */
	@JvmStatic
	suspend fun requestURIAsync(
		url: URI,
		method: HttpMethod = HttpMethod.GET,
		data: Map<String, String>? = null,
		headers: Array<String>? = null
	): HttpResponse<String> = suspendCancellableCoroutine { coroutine ->
		val client = HttpConnector.getClient(createNewClientEachRequest)
		val request = makeRequestObject(url, method, data, headers)

		client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
			.thenApply(coroutine::resume)
			.exceptionally { coroutine.resume(errorRequest(client, request, it)) }
			.thenAccept { }
	}

	/* ---------------------------------------------------------
	 *
	 * Internal methods
	 *
	 * --------------------------------------------------------- */

	/**
	 * Create request used only for header info
	 *
	 * @param url Target request url
	 *
	 * @return [HttpRequest] instance
	 */
	private fun makeHeaderRequest(
		url: URI
	): HttpRequest = HttpRequest.newBuilder()
		.uri(url)
		.method("HEAD", HttpRequest.BodyPublishers.noBody())
		.build()

	/**
	 * Create request object depends of method type
	 *
	 * @param url Target request url
	 * @param method Target request method
	 * @param data Target request data. This argument can be null.
	 * @param headers Target custom request headers. This argument can be null.
	 *
	 * @return [HttpRequest] object instance
	 */
	private fun makeRequestObject(
		url: URI,
		method: HttpMethod,
		data: Map<String, String>?,
		headers: Array<String>?
	): HttpRequest {
		// Create builder
		val requestBuilder = HttpRequest.newBuilder()

		// Determine method
		when (method) {
			// Request method GET (Secure method)
			HttpMethod.GET -> requestBuilder.GET().uri(url)
			// Request method DELETE (Secure method)
			HttpMethod.DELETE -> requestBuilder.DELETE().uri(url)
			// Request method POST (Insecure method)
			HttpMethod.POST -> requestBuilder.POST(HttpConnector.mapToHttpData(data ?: emptyMap()))
			// Request method PUT (Insecure method)
			HttpMethod.PUT -> requestBuilder.PUT(HttpConnector.mapToHttpData(data ?: emptyMap()))
			// Request method PATCH (Insecure method)
			HttpMethod.PATCH -> requestBuilder.GET().uri(url)
		}

		// Set object headers
		val fHeaders = headers ?: emptyArray()
		if (fHeaders.isNotEmpty()) requestBuilder.headers(*fHeaders)

		// Check if method accept data object
		if (method.acceptData)
			requestBuilder.header("Content-Type", "application/x-www-form-urlencoded")

		// Make request builder
		return requestBuilder.build()
	}


	/**
	 * Error request default object
	 *
	 * @return [HttpResponse] Error response object
	 */
	private fun errorRequest(
		client: HttpClient,
		request: HttpRequest,
		error: Throwable
	): HttpResponse<String> = object : HttpResponse<String> {

		/**
		 * Return code 404
		 *
		 * @return [Int] status code
		 */
		override fun statusCode(): Int = 404

		/**
		 * Get current request object
		 *
		 * @return [HttpRequest] instance element
		 */
		override fun request(): HttpRequest = request

		/**
		 * Get previous request
		 *
		 * @return [Optional] request object
		 */
		override fun previousResponse(): Optional<HttpResponse<String>> = Optional.empty()

		/**
		 * Get request headers
		 *
		 * @return [HttpHeaders] Header element result
		 */
		override fun headers(): HttpHeaders = request.headers()

		/**
		 * Get body result
		 *
		 * @return [String] body result `null` in this case
		 */
		override fun body(): String? = error.message

		/**
		 * Get request SSL Session
		 *
		 * @return [Optional] ssl session object
		 */
		override fun sslSession(): Optional<SSLSession> = Optional.empty()

		/**
		 * Get request url
		 *
		 * @return [URI] request result url
		 */
		override fun uri(): URI = request.uri()

		/**
		 * Get http client version
		 *
		 * @return [HttpClient.Version] result object
		 */
		override fun version(): HttpClient.Version = client.version()

	}

}
