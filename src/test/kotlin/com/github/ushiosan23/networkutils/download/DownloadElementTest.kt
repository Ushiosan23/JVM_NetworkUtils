package com.github.ushiosan23.networkutils.download

import com.github.ushiosan23.networkutils.extensions.toURI
import org.junit.Test
import java.nio.file.Paths

class DownloadElementTest {

	var currentPercent = 0

	/**
	 * Url download
	 */
	private val urlDownload =
		"https://d.defold.com/archive/editor-alpha/8f3e864464062e1b35c207521dc65dfd77899cdf/editor-alpha/editor2/Defold-x86_64-win32.zip".toURI()

	/**
	 * Download manager item
	 */
	private val downloadItem = DownloadElement(urlDownload)

	/**
	 * Output file location
	 */
	private val outputFile = Paths.get(System.getProperty("user.home"), "Desktop", "output.zip")

	/**
	 * Download status method
	 */
	private fun downloadStatus(status: DownloadStatus) {
		if (status.outputFile != null) status.moveDownloadTo(outputFile)
		if (status.isDownloadIndeterminate) return
		if (status.downloadPercentageRound == currentPercent) return

		currentPercent = status.downloadPercentageRound
		println("Download: ${status.downloadPercentageRound}%")
	}

	/**
	 * Run test method
	 */
	@Test
	fun runTest() {
		if (!downloadItem.exists())
			println("URL \"$urlDownload\" not found")
		else
			downloadItem.download(this::downloadStatus)
	}

}
