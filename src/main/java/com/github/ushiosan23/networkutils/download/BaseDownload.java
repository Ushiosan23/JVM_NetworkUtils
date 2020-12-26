package com.github.ushiosan23.networkutils.download;

import com.github.ushiosan23.networkutils.download.event.*;

import javax.swing.event.EventListenerList;
import java.util.EventListener;
import java.util.EventObject;

public abstract class BaseDownload {

	/* ---------------------------------------------------------
	 *
	 * Properties
	 *
	 * --------------------------------------------------------- */

	/**
	 * Object to synchronize methods
	 */
	protected final Object lock = new Object();

	/**
	 * Check if download is cancelled
	 */
	protected boolean isCancelled;

	/**
	 * Check if download is paused
	 */
	protected boolean isPaused;

	/**
	 * Check if download is finished
	 */
	protected boolean isFinished;

	/**
	 * Check if download is indefinite
	 */
	protected boolean isIndefinite;

	/**
	 * How many bytes download
	 */
	protected long downloadBytes;

	/**
	 * Buffer to save download chunks
	 */
	protected byte[] downloadBuffer;

	/**
	 * Event listener manager
	 */
	protected final EventListenerList listenerList = new EventListenerList();

	/* ---------------------------------------------------------
	 *
	 * Methods
	 *
	 * --------------------------------------------------------- */

	/**
	 * Get if download is finished
	 *
	 * @return Finished status
	 */
	public boolean isFinished() {
		synchronized (lock) {
			return isFinished;
		}
	}

	/**
	 * Get if download is cancelled
	 *
	 * @return Cancel status
	 */
	public boolean isCancelled() {
		synchronized (lock) {
			return isCancelled;
		}
	}

	/**
	 * Pause download element
	 */
	public void paused() {
		if (isFinished()) throw new RuntimeException("Download already finished");

		synchronized (lock) {
			isPaused = true;
		}
	}

	/**
	 * Resume download
	 */
	public void resume() {
		if (isFinished()) throw new RuntimeException("Download already finished");

		synchronized (lock) {
			isPaused = false;
		}
	}

	/**
	 * Cancel download
	 */
	public void cancel() {
		if (isFinished()) throw new RuntimeException("Download already finished");

		synchronized (lock) {
			isCancelled = true;
		}
	}

	/**
	 * Get if download is paused
	 *
	 * @return Pause status
	 */
	public boolean isPaused() {
		synchronized (lock) {
			return isPaused;
		}
	}

	/**
	 * Check if download is indefinite
	 *
	 * @return Indefinite status
	 */
	public boolean isIndefinite() {
		return isIndefinite;
	}

	/* ---------------------------------------------------------
	 *
	 * Event methods
	 *
	 * --------------------------------------------------------- */

	public void addDownloadListener(DownloadListener listener) {
		addListener(listener, DownloadListener.class);
	}

	/* ---------------------------------------------------------
	 *
	 * Fire methods
	 *
	 * --------------------------------------------------------- */

	/**
	 * Dispatch event object
	 *
	 * @param event Target event to send
	 */
	protected void fireListener(DownloadStatusEvent event) {
		DownloadListener[] eventListenerList = listenerList.getListeners(DownloadListener.class);

		for (DownloadListener listener : eventListenerList) {
			listener.onDownloadEvent(event);
		}
	}

	/* ---------------------------------------------------------
	 *
	 * Internal methods
	 *
	 * --------------------------------------------------------- */

	/**
	 * Add any listener to listener list
	 *
	 * @param listener Target object listener
	 * @param tClass   Target type class
	 * @param <T>      Generic event listener type
	 */
	<T extends EventListener> void addListener(T listener, Class<T> tClass) {
		listenerList.add(tClass, listener);
	}

	/**
	 * Remove any listener to listener list
	 *
	 * @param listener Target object listener
	 * @param tClass   Target type class
	 * @param <T>      Generic event listener type
	 */
	<T extends EventListener> void removeListener(T listener, Class<T> tClass) {
		listenerList.remove(tClass, listener);
	}

}
