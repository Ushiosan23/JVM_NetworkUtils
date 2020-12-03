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


/* ---------------------------------------------------------
 *
 * Internal methods
 *
 * --------------------------------------------------------- */

/**
 * Create response error result.
 *
 * @param throwable Exception error
 * @param request Source request
 *
 * @return [HttpResponse] result request.
 */
private fun makeResponseError(throwable: Throwable, request: HttpRequest): HttpResponse<String> =
	object : HttpResponse<String> {

		/**
		 * Return status code
		 *
		 * @return status code
		 */
		override fun statusCode(): Int = 404

		/**
		 * Get current request
		 *
		 * @return [HttpRequest] current request
		 */
		override fun request(): HttpRequest = request

		/**
		 * Return empty previous response
		 *
		 * @return [Optional] empty response.
		 */
		override fun previousResponse(): Optional<HttpResponse<String>> = Optional.empty()

		/**
		 * Get request headers
		 *
		 * @return [HttpHeaders] current request headers
		 */
		override fun headers(): HttpHeaders? = request.headers()

		/**
		 * Get request body
		 *
		 * @return request body string
		 */
		override fun body(): String? = throwable.message

		/**
		 * Get empty ssl session
		 *
		 * @return [Optional] empty ssl session
		 */
		override fun sslSession(): Optional<SSLSession> = Optional.empty()

		/**
		 * Get request uri
		 *
		 * @return [URI] request uri
		 */
		override fun uri(): URI? = request.uri()

		/**
		 * Get [HttpClient] version
		 *
		 * @return [HttpClient.Version] client version
		 */
		override fun version(): HttpClient.Version? = HttpConnector.getHttpClient().version()
	}


/* ---------------------------------------------------------
 *
 * Extension methods
 *
 * --------------------------------------------------------- */


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
			coroutine.resume(makeResponseError(throwable, source as HttpRequest))
			return super.exceptionally(throwable, source)
		}

	})
}

/**
 * Make coroutine request action.
 *
 * @param data Data to send
 * @return [HttpResponse] request result.
 *
 * @see HttpRequestAction.postAsync
 */
suspend fun HttpRequestAction.postAsyncC(data: Map<String, String>? = null): HttpResponse<String> =
	suspendCancellableCoroutine { coroutine ->
		postAsync(object : HttpAction<HttpResponse<String>> {

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
				coroutine.resume(makeResponseError(throwable, source as HttpRequest))
				return super.exceptionally(throwable, source)
			}

		}, data)
	}

/**
 * Make coroutine request action.
 *
 * @param data Data to send
 * @return [HttpResponse] request result.
 *
 * @see HttpRequestAction.postAsyncD
 */
suspend fun HttpRequestAction.postAsyncCD(data: Map<String, Any>? = null): HttpResponse<String> =
	suspendCancellableCoroutine { coroutine ->
		postAsyncD(object : HttpAction<HttpResponse<String>> {

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
				coroutine.resume(makeResponseError(throwable, source as HttpRequest))
				return super.exceptionally(throwable, source)
			}

		}, data)
	}

/**
 * Make coroutine request action.
 *
 * @return [HttpResponse] request result.
 */
suspend fun HttpRequestAction.putAsyncC(): HttpResponse<String> = suspendCancellableCoroutine { coroutine ->
	putAsync(object : HttpAction<HttpResponse<String>> {

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
			coroutine.resume(makeResponseError(throwable, source as HttpRequest))
			return super.exceptionally(throwable, source)
		}

	})
}

/**
 * Make coroutine request action.
 *
 * @return [HttpResponse] request result.
 */
suspend fun HttpRequestAction.deleteAsyncC(): HttpResponse<String> = suspendCancellableCoroutine { coroutine ->
	deleteAsync(object : HttpAction<HttpResponse<String>> {

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
			coroutine.resume(makeResponseError(throwable, source as HttpRequest))
			return super.exceptionally(throwable, source)
		}

	})
}
