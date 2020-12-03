package http.post;

import com.github.ushiosan23.networkutils.http.HttpRequestAction;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class HttpActionPostBlob {

	private final HttpRequestAction requestAction = new HttpRequestAction("http://127.0.0.1");

	private void asyncRequest() throws IOException {
		HashMap<String, Object> data = new HashMap<>();
		data.put("RequestName", "Asynchronous blob request");
		data.put(
			"FileData",
			new File("C:\\Users\\ushio\\Music\\Vocaloid\\Nebula.mp3")
		);

		requestAction.postAsyncD(action -> {
			System.out.println(action.body());
			return action;
		}, data);
	}

	private void syncRequest() throws InterruptedException, IOException {
		HashMap<String, Object> data = new HashMap<>();
		data.put("RequestName", "Synchronous blob request");
		data.put(
			"FileData",
			new File("C:\\Users\\ushio\\Music\\Vocaloid\\Nebula.mp3")
		);

		System.out.println(requestAction.postD(data).body());
	}

	@Test
	public void runTest() throws InterruptedException, IOException {
		asyncRequest();
		syncRequest();

		Thread.sleep(10000);
	}

}
