package com.github.ushiosan23.networkutils.download;

import com.github.ushiosan23.networkutils.http.HttpRequestAction;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpHeaders;
import java.util.Calendar;
import java.util.OptionalLong;

/**
 * Download element object
 */
public final class DownloadElement extends BaseDownload {

	/* ---------------------------------------------------------
	 *
	 * Properties
	 *
	 * --------------------------------------------------------- */

	/**
	 * Target url to download
	 */
	private final URI downloadURL;

	/**
	 * Save download headers (download once)
	 */
	private HttpHeaders downloadHeaders;

	/**
	 * Download status event
	 */
	private DownloadStatusEvent lastStatus;

	/**
	 * Download thread
	 */
	private Thread downloadThread;

	/* ---------------------------------------------------------
	 *
	 * Constructors
	 *
	 * --------------------------------------------------------- */

	/**
	 * Create download element from uri object
	 *
	 * @param url Target url to download
	 */
	public DownloadElement(@NotNull URI url) {
		// Check url scheme
		if (!url.getScheme().equals("http") && !url.getScheme().equals("https"))
			throw new IllegalArgumentException(String.format("\"%s\" has not valid http scheme.", url));

		// Set properties
		downloadURL = url;
		isPaused = false;
		isCancelled = false;
		isFinished = false;
		downloadBytes = 0;
		isIndefinite = false;
	}

	/**
	 * Create download element from url string
	 *
	 * @param url Target url to download
	 */
	public DownloadElement(String url) {
		this(URI.create(url));
	}

	/* ---------------------------------------------------------
	 *
	 * Methods
	 *
	 * --------------------------------------------------------- */

	/**
	 * Check if download exists
	 *
	 * @return Request result
	 */
	public boolean downloadExists() {
		return HttpRequestAction.exists(downloadURL);
	}

	/**
	 * Get download headers
	 *
	 * @return {@link HttpHeaders} request headers result
	 */
	public HttpHeaders downloadHeaders() {
		if (downloadHeaders == null)
			downloadHeaders = HttpRequestAction.getHeaders(downloadURL);

		return downloadHeaders;
	}

	/**
	 * Check download size
	 *
	 * @return Download size in bytes. If download size is -1 this download is indefinite
	 */
	public long downloadSize() {
		HttpHeaders localHeaders = downloadHeaders();
		OptionalLong optionalLong = localHeaders.firstValueAsLong("content-length");

		if (optionalLong.isPresent()) {
			isIndefinite = false;
			return optionalLong.getAsLong();
		} else {
			isIndefinite = true;
			return -1L;
		}
	}

	/**
	 * Start download method
	 */
	public void startDownload() {
		downloadThread = new Thread(downloadRunnable());
		downloadThread.start();
	}

	/**
	 * Waits for this thread to die.
	 *
	 * @throws InterruptedException if any thread has interrupted the current thread.
	 *                              The interrupted status of the current thread is cleared when this exception is thrown.
	 */
	public void join() throws InterruptedException {
		if (downloadThread != null)
			downloadThread.join();
	}

	/**
	 * Get download file name. Return url if download is not a file
	 *
	 * @return Download file name
	 */
	@NotNull
	public String getDownloadFileName() {
		String path = downloadURL.getPath();
		int lastIndex = path.lastIndexOf("/");
		String result;

		if (lastIndex != -1) {
			result = path.substring(lastIndex + 1);
		} else {
			result = path;
		}

		return result.isEmpty() ? path : result;
	}

	/* ---------------------------------------------------------
	 *
	 * Internal methods
	 *
	 * --------------------------------------------------------- */

	/**
	 * Runnable process download
	 *
	 * @return {@link Runnable} Task process download
	 */
	@NotNull
	@Contract(pure = true)
	private Runnable downloadRunnable() {
		// Runnable download
		return () -> {
			try {
				downloadProcess();
			} catch (Exception e) {
				e.printStackTrace();
			}
		};
	}

	/**
	 * Download process
	 *
	 * @throws Exception Any download error
	 */
	private void downloadProcess() throws Exception {
		int downloadRead;
		// Initialize properties
		File tmpFile = generateTemporalFile(downloadURL);
		FileOutputStream fOutput = new FileOutputStream(tmpFile);
		BufferedInputStream buffered = new BufferedInputStream(downloadURL.toURL().openStream());
		lastStatus = DownloadStatusEvent.getDefault(DownloadElement.this);
		downloadBuffer = new byte[1024];
		// Configure event
		lastStatus.isIndefinite = isIndefinite();
		lastStatus.totalSize = downloadSize();

		// Iterate all download data
		while (
			(downloadRead = buffered.read(downloadBuffer, 0, downloadBuffer.length)) != -1 &&
				!isCancelled() &&
				!isPaused()
		) {
			// Write download data
			fOutput.write(downloadBuffer);
			// Change download status
			lastStatus.downloaded += downloadRead;
			if (!lastStatus.isIndefinite)
				lastStatus.downloadProgress = lastStatus.downloaded * 100f / lastStatus.totalSize;
			// Fire events
			fireListener(lastStatus.cloneSelf());
		}

		// Close resources
		fOutput.flush();
		fOutput.close();
		buffered.close();
		// Configure status
		lastStatus.isCancelled = isCancelled();
		lastStatus.isPaused = isPaused();
		lastStatus.isFinished = !isPaused() && !isCancelled();
		lastStatus.tmpFile = lastStatus.isFinished ? tmpFile : null;

		fireListener(lastStatus.cloneSelf());
	}

	@NotNull
	private static String getFileDownload(@NotNull URI uri) {
		String path = uri.getPath();
		int lastIndex = path.lastIndexOf("/");
		String result;

		if (lastIndex != -1) {
			result = path.substring(lastIndex);
		} else {
			result = path;
		}

		return result.isEmpty() ? path : result;
	}

	@NotNull
	private static File generateTemporalFile(URI uri) throws IOException {
		long timeInMillis = Calendar.getInstance().getTimeInMillis();
		String hexString = Long.toHexString(timeInMillis);
		String file = getFileDownload(uri);

		return File.createTempFile(String.format("%s.%s", file, hexString), ".tmpdownload");
	}

}
