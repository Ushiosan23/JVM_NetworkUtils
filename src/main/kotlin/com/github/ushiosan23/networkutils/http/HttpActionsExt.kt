package com.github.ushiosan23.networkutils.http

import com.github.ushiosan23.networkutils.http.response.HttpAction
import kotlinx.coroutines.suspendCancellableCoroutine
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpHeaders
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.*
import javax.net.ssl.SSLSession
import kotlin.coroutines.resume

/**
 * Make coroutine request action.
 *
 * @return [HttpResponse] request result.
 */
suspend fun HttpRequestAction.getAsyncC(): HttpResponse<String> = suspendCancellableCoroutine { coroutine ->
	getAsync(object : HttpAction<HttpResponse<String>> {

		/**
		 * Called when request is valid.
		 */
		override fun invoke(item: HttpResponse<String>): HttpResponse<String> {
			coroutine.resume(item)
			return item
		}

		/**
		 * Called when request failed.
		 *
		 * @param throwable Error exception
		 */
		override fun exceptionally(throwable: Throwable, source: Any): HttpResponse<String>? {
			coroutine.resume(errorAction(throwable, source as HttpRequest))
			return super.exceptionally(throwable, source)
		}

		/**
		 * Create error request.
		 *
		 * @return [HttpResponse] fake request result.
		 */
		fun errorAction(throwable: Throwable, request: HttpRequest) = object : HttpResponse<String> {

			override fun statusCode(): Int = -1

			override fun request(): HttpRequest = request

			override fun previousResponse(): Optional<HttpResponse<String>> = Optional.empty()

			override fun headers(): HttpHeaders? = request.headers()

			override fun body(): String? = throwable.message

			override fun sslSession(): Optional<SSLSession> = Optional.empty()

			override fun uri(): URI? = request.uri()

			override fun version(): HttpClient.Version? = HttpConnector.getHttpClient().version()

		}

	})
}
