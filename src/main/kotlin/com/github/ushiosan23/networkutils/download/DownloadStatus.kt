package com.github.ushiosan23.networkutils.download

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import kotlin.math.round

/**
 * Data class to manage download status
 */
@Suppress("MemberVisibilityCanBePrivate")
@Serializable
class DownloadStatus {

	/* ---------------------------------------------------------
	 *
	 * Public properties
	 *
	 * --------------------------------------------------------- */

	var totalBytes: Long = -1L
		internal set

	var currentBytes: Long = -1L
		internal set

	@Contextual
	var outputFile: File? = null
		internal set

	var hasError: Boolean = false
		internal set

	@Contextual
	var downloadError: Throwable? = null
		internal set


	/* ---------------------------------------------------------
	 *
	 * Special properties
	 *
	 * --------------------------------------------------------- */

	/**
	 * Check if download is indeterminate
	 */
	val isDownloadIndeterminate: Boolean
		get() = totalBytes == -1L

	/**
	 * Get download percentage
	 */
	val downloadPercentage: Float
		get() = if (isDownloadIndeterminate) -1f else currentBytes.toFloat() * 100f / totalBytes.toFloat()

	/**
	 * Get download percentage rounded
	 */
	val downloadPercentageRound: Int
		get() = round(downloadPercentage).toInt()

	/* ---------------------------------------------------------
	 *
	 * Public methods
	 *
	 * --------------------------------------------------------- */

	/**
	 * Move final download file to new path
	 *
	 * @param newPath Target file location
	 *
	 * @return [Boolean] action result
	 */
	fun moveDownloadTo(newPath: Path): Boolean {
		// Check if output file exists
		if (outputFile == null) return false
		// Try to move file
		return try {
			Files.move(outputFile!!.toPath(), newPath)
			true
		} catch (err: Exception) {
			err.printStackTrace()
			false
		}
	}

}
