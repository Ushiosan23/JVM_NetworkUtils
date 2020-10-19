package com.github.ushiosan23.networkutils.http

import com.github.ushiosan23.networkutils.extensions.toURI
import kotlinx.coroutines.launch
import org.junit.Before
import org.junit.Test

class HttpRequestActionsTest {

	/**
	 * Target request url
	 */
	private val url = "http://127.0.0.1:8080".toURI()

	/**
	 * Url headers
	 */
	private val urlHeaders
		get() = HttpRequestActions.urlHeaders(url)

	/**
	 * Url content size
	 */
	private val urlContentSize
		get() = HttpRequestActions.urlContentLength(url)

	/**
	 * Synchronous request
	 */
	private fun requestURI() {
		val response = HttpRequestActions.requestURI(url)
		println("Request synchronous: ${response.statusCode()}")
	}

	/**
	 * Async request by callback
	 */
	private fun requestCallback() {
		HttpRequestActions.requestURI(url) {
			println("Request Callback: ${it.statusCode()}")
		}
	}

	/**
	 * Async request by coroutine
	 */
	private fun requestCoroutine() = HttpRequestActions.launch {
		val response = HttpRequestActions.requestURIAsync(url)
		println("Request Coroutine: ${response.statusCode()}")
	}

	/**
	 * Check if connection exists
	 */
	@Before
	fun checkConnection() {
		println("Connection Exists: ${HttpRequestActions.urlExists(url)}")
	}

	/**
	 * Run all test methods
	 */
	@Test
	fun runTest() = HttpRequestActions.urlExistsAction(url) {
		println("Request URL: $url")
		// URL headers
		println("URL Content Size: $urlContentSize")
		println("URL Headers: $urlHeaders")
		println("URL Real Size: ${urlHeaders.firstValue("content-length")}")
		// Call methods
		requestCoroutine()
		requestCallback()
		requestURI()

		// Sleep thread until finish request methods
		Thread.sleep(5000)
	}

}
