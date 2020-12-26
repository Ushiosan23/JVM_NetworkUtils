package http.download;

import com.github.ushiosan23.networkutils.download.DownloadElement;
import com.github.ushiosan23.networkutils.download.DownloadStatusEvent;
import com.github.ushiosan23.networkutils.download.event.DownloadListener;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.net.URI;

public class DownloadElementTest implements DownloadListener {

	// Download url test
	URI uri = URI.create("https://downloads.tuxfamily.org/godotengine/3.2.3/mono/Godot_v3.2.3-stable_mono_win64.zip");
	DownloadElement downloadElement;

	private void testDownload() throws InterruptedException {
		downloadElement = new DownloadElement(uri);
		downloadElement.addDownloadListener(this);

		System.out.println(downloadElement.getDownloadFileName());
		System.out.println(downloadElement.downloadExists());
		System.out.println(downloadElement.downloadHeaders());

//		downloadElement.startDownload();
//		downloadElement.join();
	}

	@Test
	public void runTest() throws InterruptedException {
		testDownload();
	}

	@Override
	public void onDownloadEvent(@NotNull DownloadStatusEvent event) {
		if (event.isFinished())
			System.out.println(event.getTmpFile());
		else
			System.out.printf("%.2f%n", event.getDownloadProgress());
	}


}
