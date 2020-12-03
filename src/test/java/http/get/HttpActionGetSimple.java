package http.get;

import com.github.ushiosan23.networkutils.http.HttpRequestAction;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpActionGetSimple {

	private final HttpRequestAction requestAction = new HttpRequestAction("http://127.0.0.1");
	private final Map<String, String> dataQuery = new HashMap<>();

	private void createQuery() {
		dataQuery.put("action", "1");
		dataQuery.put("requestId", "@489182994");
		dataQuery.put("exampleName", "Brian O'Connor");
	}

	private void syncRequest() throws InterruptedException, IOException {
		System.out.println(
			requestAction.get().body()
		);
	}

	private void asyncRequest() {
		requestAction.getAsync(action -> {
			System.out.println(action.body());
			return action;
		});
	}

	@Test
	public void runTest() throws InterruptedException, IOException {
		createQuery();

		requestAction.setQuery(dataQuery);

		asyncRequest();
		syncRequest();

		Thread.sleep(5000);
	}

}
