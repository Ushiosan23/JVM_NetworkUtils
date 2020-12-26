package com.github.ushiosan23.networkutils.download;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EventObject;

/**
 * Download event object
 */
public final class DownloadStatusEvent extends EventObject implements Serializable, Cloneable {

	/* ---------------------------------------------------------
	 *
	 * Properties
	 *
	 * --------------------------------------------------------- */

	/**
	 * Current download size status
	 */
	long downloaded;

	/**
	 * Check if is indefinite download
	 */
	boolean isIndefinite;

	/**
	 * Check if download finished
	 */
	boolean isFinished;

	/**
	 * Check if download is paused
	 */
	boolean isPaused;

	/**
	 * Check if download is cancelled
	 */
	boolean isCancelled;

	/**
	 * Download progress percentage
	 */
	float downloadProgress;

	/**
	 * Current download size
	 */
	long downloadSize;

	/**
	 * Download total size
	 */
	long totalSize;

	/**
	 * Get temporal file result
	 */
	File tmpFile = null;

	/* ---------------------------------------------------------
	 *
	 * Constructors
	 *
	 * --------------------------------------------------------- */

	/**
	 * Constructs a prototypical Event.
	 *
	 * @param source the object on which the Event initially occurred
	 * @throws IllegalArgumentException if source is null
	 */
	public DownloadStatusEvent(Object source) {
		super(source);
	}

	/* ---------------------------------------------------------
	 *
	 * Methods
	 *
	 * --------------------------------------------------------- */

	/**
	 * Get default status object
	 *
	 * @param source Source event
	 * @return {@link DownloadStatusEvent} Status object event
	 */
	@NotNull
	static DownloadStatusEvent getDefault(@NotNull Object source) {
		DownloadStatusEvent statusEvent = new DownloadStatusEvent(source);

		statusEvent.isIndefinite = false;
		statusEvent.isFinished = false;
		statusEvent.isCancelled = false;
		statusEvent.isPaused = false;

		statusEvent.downloadSize = 0L;
		statusEvent.totalSize = -1L;
		statusEvent.downloadProgress = 0f;
		statusEvent.downloaded = 0L;

		return statusEvent;
	}

	/**
	 * Get total data downloaded
	 *
	 * @return Downloaded size
	 */
	public long getDownloaded() {
		return downloaded;
	}

	/**
	 * Check if download is indefinite
	 *
	 * @return Indefinite result
	 */
	public boolean isIndefinite() {
		return isIndefinite;
	}

	/**
	 * Check if download is finished
	 *
	 * @return Finished result
	 */
	public boolean isFinished() {
		return isFinished;
	}

	/**
	 * Check if download is paused
	 *
	 * @return Paused status
	 */
	public boolean isPaused() {
		return isPaused;
	}

	/**
	 * Check if download was cancelled
	 *
	 * @return Cancelled status
	 */
	public boolean isCancelled() {
		return isCancelled;
	}

	/**
	 * Download progress (percentage)
	 *
	 * @return Download progress status
	 */
	public float getDownloadProgress() {
		return downloadProgress;
	}

	/**
	 * Step download size
	 *
	 * @return Download size status
	 */
	public long getDownloadSize() {
		return downloadSize;
	}

	/**
	 * Total download size
	 *
	 * @return Total download size status
	 */
	public long getTotalSize() {
		return totalSize;
	}

	/**
	 * Get location of temporal download file
	 *
	 * @return {@link File} download file
	 */
	@Nullable
	public File getTmpFile() {
		return tmpFile;
	}

	/**
	 * Move download to specific location
	 *
	 * @param path Target location
	 * @return Final path
	 * @throws IOException Error if download is not finished or path is not valid
	 */
	public Path moveDownloadFile(@NotNull Path path) throws IOException {
		// Check if download finished
		if (getTmpFile() == null)
			throw new IOException("Download is not finished yet.");
		// Move file to target path
		return Files.move(getTmpFile().toPath(), path);
	}

	/* ---------------------------------------------------------
	 *
	 * Implemented methods
	 *
	 * --------------------------------------------------------- */

	/**
	 * Object string representation
	 *
	 * @return Object string representation
	 */
	@Override
	public String toString() {
		return String.format("%.2f", downloadProgress);
	}

	/**
	 * Clone current object
	 *
	 * @return A cloned instance
	 * @throws CloneNotSupportedException Error if clone is not supported
	 */
	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	/**
	 * Clone current object
	 *
	 * @return A cloned instance
	 * @throws CloneNotSupportedException Error if clone is not supported
	 */
	DownloadStatusEvent cloneSelf() throws CloneNotSupportedException {
		return (DownloadStatusEvent) clone();
	}

}
