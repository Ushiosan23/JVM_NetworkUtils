package com.github.ushiosan23.networkutils.download.event;

import com.github.ushiosan23.networkutils.download.DownloadStatusEvent;

import java.util.EventListener;

public interface DownloadListener extends EventListener {

	void onDownloadEvent(DownloadStatusEvent event);

}
