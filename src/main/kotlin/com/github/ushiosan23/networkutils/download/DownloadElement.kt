package com.github.ushiosan23.networkutils.download

import com.github.ushiosan23.networkutils.extensions.toHexString
import com.github.ushiosan23.networkutils.extensions.toURI
import com.github.ushiosan23.networkutils.http.HttpRequestActions
import java.io.BufferedInputStream
import java.io.File
import java.net.URI
import java.net.URL
import java.net.URLConnection
import java.net.http.HttpHeaders
import java.util.*

/**
 * Download class element
 */
class DownloadElement : IDownloadElement {

	/* ---------------------------------------------------------
	 *
	 * Public properties
	 *
	 * --------------------------------------------------------- */

	/**
	 * Determine if download was cancelled
	 */
	@Volatile
	var isCancelled: Boolean = false
		private set

	/**
	 * Determine if download was paused
	 */
	@Volatile
	var isPaused: Boolean = false
		private set

	/* ---------------------------------------------------------
	 *
	 * Internal properties
	 *
	 * --------------------------------------------------------- */

	/**
	 * Download url element
	 */
	private val downloadURI: URI

	/**
	 * Download url element
	 */
	private val downloadURL: URL

	/**
	 * URL connection
	 */
	private var connection: URLConnection? = null

	/**
	 * Temporal download file
	 */
	private var tmpOutputFile: File? = null

	/**
	 * Download status object
	 */
	@Volatile
	private var lastDownloadStatus: DownloadStatus? = null

	/* ---------------------------------------------------------
	 *
	 * Constructors
	 *
	 * --------------------------------------------------------- */

	/**
	 * URI constructor
	 *
	 * @param url Target url to download. Valid shcemes (http, https)
	 *
	 * @throws IllegalArgumentException if url scheme is not valid
	 */
	constructor(url: URI) {
		// Validate shceme
		if (url.scheme != "https" && url.scheme != "http")
			throw IllegalArgumentException("\"$url\" has not valid http scheme.")
		// Set properties
		downloadURI = url
		downloadURL = downloadURI.toURL()
	}

	/**
	 * String constructor
	 *
	 * @param url Target url string
	 *
	 * @throws IllegalArgumentException if url scheme is not valid
	 * @throws java.net.URISyntaxException if url is not valid
	 */
	constructor(url: String) : this(url.toURI())

	/**
	 * URL constructor
	 *
	 * @param url Target url object
	 *
	 * @throws IllegalArgumentException if url scheme is not valid
	 * @throws java.net.URISyntaxException if url is not valid
	 */
	constructor(url: URL) : this(url.toURI())

	/* ---------------------------------------------------------
	 *
	 * Implemented methods
	 *
	 * --------------------------------------------------------- */

	/**
	 * Check if url exists
	 *
	 * @return [Boolean] download status
	 */
	override fun exists(): Boolean =
		HttpRequestActions.urlExists(downloadURI)

	/**
	 * Get download url headers
	 *
	 * @return [HttpHeaders] result headers
	 *
	 * @see HttpRequestActions.urlHeaders
	 */
	override fun getHeaders(): HttpHeaders? = if (exists())
		HttpRequestActions.urlHeaders(downloadURI)
	else
		null

	/**
	 * Get document total size
	 *
	 * @return [Long] with total document size or `-1` if size is not defined
	 *
	 * @see HttpRequestActions.urlContentLength
	 */
	override fun getContentLength(): Long =
		HttpRequestActions.urlContentLength(downloadURI)

	/**
	 * Download file chunk by chunk
	 *
	 * @param info Download status
	 */
	override fun download(info: (status: DownloadStatus) -> Unit) {
		// Generate download status and temporal file
		tmpOutputFile = generateTemporalFile(downloadURI)
		lastDownloadStatus = DownloadStatus()

		// Start download section
		try {
			// Create local vars
			var iterationRead = 0
			val outputStream = tmpOutputFile!!.outputStream()
			val httpStream = BufferedInputStream(downloadURL.openStream())
			val buffer = ByteArray(1024)
			// Set initial download status values
			lastDownloadStatus!!.totalBytes = getContentLength()
			lastDownloadStatus!!.currentBytes = 0L

			// Read data
			do {
				// Check if download is cancelled
				if (isCancelled) {
					outputStream.flush()
					outputStream.close()
					httpStream.close()
					throw InterruptedException("Download was cancelled")
				}

				// Store read buffer size
				iterationRead = httpStream.read(buffer, 0, buffer.size)

				// Check download iteration
				if (iterationRead != -1) {
					outputStream.write(buffer, 0, iterationRead)
					lastDownloadStatus!!.currentBytes += iterationRead
					info.invoke(lastDownloadStatus!!)
				} else {
					outputStream.flush()
					outputStream.close()
					httpStream.close()
					// Set new status data
					lastDownloadStatus!!.outputFile = tmpOutputFile!!
					info.invoke(lastDownloadStatus!!)
				}

			} while (iterationRead != -1)


		} catch (err: Exception) {
			err.printStackTrace()
			// Delete temporal file
			if (tmpOutputFile?.exists()!!)
				tmpOutputFile?.delete()
			// Set download failed
			lastDownloadStatus!!.hasError = true
			lastDownloadStatus!!.downloadError = err
			// Invoke method
			info.invoke(lastDownloadStatus!!)
		}

	}

	/**
	 * Cancel download
	 */
	override fun cancel() = synchronized(this) {
		isCancelled = true
	}

	/**
	 * Pause current download
	 *
	 * @param info download status
	 */
	override fun pause(info: (status: DownloadStatus) -> Unit) = synchronized(this) {
		super.pause(info)
		/*lastDownloadStatus?.let(info::invoke)
		isPaused = true*/
	}

	/**
	 * Resume download
	 */
	override fun resume() = synchronized(this) {
		super.resume()
		/*isPaused = false*/
	}

	/**
	 * Companion object element
	 */
	companion object {

		/**
		 * Get current time in milliseconds
		 *
		 * @return [Long] with date milliseconds
		 */
		private fun getCurrentTime(): Long = Calendar.getInstance().timeInMillis

		/**
		 * Get file url name
		 *
		 * @param url Target url to check
		 *
		 * @return [String] with filename result
		 */
		private fun getURIFileDownload(url: URI): String {
			val path = url.path
			val lastIndex = path.lastIndexOf("/")
			val result = if (lastIndex != -1) path.substring(lastIndex) else path

			return if (result.isEmpty()) path else result
		}

		/**
		 * Generate temporal file
		 *
		 * @param url Target url generator file
		 */
		private fun generateTemporalFile(url: URI): File {
			val hexString = getCurrentTime().toHexString()
			val file = getURIFileDownload(url)

			return File.createTempFile("${file}.${hexString}", ".tmpdownload")
		}

	}

}
