package http.post;

import com.github.ushiosan23.networkutils.http.HttpRequestAction;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;

public class HttpActionPostSimple {

	private final HttpRequestAction requestAction = new HttpRequestAction("http://127.0.0.1");

	private void asyncRequest() {
		HashMap<String, String> data = new HashMap<>();
		data.put("RequestName", "Asynchronous simple request");

		requestAction.postAsync(action -> {
			System.out.println(action.body());
			return action;
		}, data);
	}

	private void syncRequest() throws InterruptedException, IOException {
		HashMap<String, String> data = new HashMap<>();
		data.put("RequestName", "Synchronous simple request");

		System.out.println(requestAction.post(data).body());
	}

	@Test
	public void runTest() throws InterruptedException, IOException {
		asyncRequest();
		syncRequest();
		Thread.sleep(5000);
	}

}
