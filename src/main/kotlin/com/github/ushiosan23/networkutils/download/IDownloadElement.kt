package com.github.ushiosan23.networkutils.download

import java.net.http.HttpHeaders

/**
 * Download model class
 */
interface IDownloadElement {

	/**
	 * Check if url exists
	 *
	 * @return [Boolean] result
	 */
	fun exists(): Boolean

	/**
	 * Get url headers
	 *
	 * @return [HttpHeaders] result or null if url not exists
	 */
	fun getHeaders(): HttpHeaders?

	/**
	 * Get download content length
	 *
	 * @return [Long] download total size
	 */
	fun getContentLength(): Long

	/**
	 * Download file chunk by chunk
	 *
	 * @param info Download status
	 */
	fun download(info: (status: DownloadStatus) -> Unit)

	/**
	 * Pause download file
	 *
	 * @param info Last download information data
	 */
	fun pause(info: (status: DownloadStatus) -> Unit) {
		throw UnsupportedOperationException("This operation is not supported.")
	}

	/**
	 * Resume download file
	 */
	fun resume() {
		throw UnsupportedOperationException("This operation is not supported.")
	}

	/**
	 * Cancel download
	 */
	fun cancel()

}
