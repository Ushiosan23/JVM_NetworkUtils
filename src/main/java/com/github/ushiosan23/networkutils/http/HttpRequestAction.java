package com.github.ushiosan23.networkutils.http;

import com.github.ushiosan23.networkutils.CoroutineElement;
import com.github.ushiosan23.networkutils.http.response.HttpAction;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Request action class.
 * This class manage all request actions
 */
public class HttpRequestAction extends CoroutineElement {

	/* ---------------------------------------------------------
	 *
	 * Properties
	 *
	 * --------------------------------------------------------- */

	/**
	 * Request url
	 */
	private final URI requestURI;

	/**
	 * Request headers
	 */
	private Map<String, String> requestHeaders;

	/* ---------------------------------------------------------
	 *
	 * Constructor
	 *
	 * --------------------------------------------------------- */

	/**
	 * Create action request with uri.
	 *
	 * @param uri Target uri to make request
	 */
	public HttpRequestAction(URI uri) {
		requestURI = uri;
	}

	/**
	 * Create action request with uri.
	 *
	 * @param uri Target uri to make request
	 */
	public HttpRequestAction(String uri) {
		this(URI.create(uri));
	}

	/* ---------------------------------------------------------
	 *
	 * Public Methods
	 *
	 * --------------------------------------------------------- */

	/**
	 * Verify if request url has a valid response.
	 *
	 * @param attachHeaders Set request defined headers
	 * @return {@link Boolean} status {@code false} if url page not exists
	 */
	public boolean exists(boolean attachHeaders) {
		// Catch error exception
		try {
			// Get http client
			HttpClient client = HttpConnector.getHttpClient();
			HttpRequest.Builder builder = getBuilder()
				.uri(requestURI)
				.method("HEAD", HttpRequest.BodyPublishers.noBody());
			// Attach headers only if param is defined
			if (attachHeaders) builder.headers(makeHeaders());
			// Build request
			HttpRequest request = builder.build();
			// Start request
			HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
			// Return request status
			return response.statusCode() == HttpURLConnection.HTTP_OK;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Verify if request url has a valid response.<br>
	 * This method by default set attachHeaders to false.
	 *
	 * @return {@link Boolean} status {@code false} if url page not exists
	 */
	public boolean exists() {
		return exists(false);
	}

	/**
	 * Check if exists request url and catch error if exists
	 *
	 * @param attachHeaders Attach headers to request
	 * @param runnable      Action to execute. Only if not has an error.
	 * @return {@link Throwable} error or {@code null} if don't have error
	 */
	@Nullable
	public Throwable existsCatching(boolean attachHeaders, @Nullable Runnable runnable) {
		// Catch error exception
		try {
			// Get http client
			HttpClient client = HttpConnector.getHttpClient();
			HttpRequest.Builder builder = getBuilder()
				.uri(requestURI)
				.method("HEAD", HttpRequest.BodyPublishers.noBody());
			// Attach headers only if param is defined
			if (attachHeaders) builder.headers(makeHeaders());
			// Build request
			HttpRequest request = builder.build();
			// Start request
			HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
			// Return request status
			if (response.statusCode() == HttpURLConnection.HTTP_OK) {
				// Check runnable
				if (runnable != null) runnable.run();
				// Return null
				return null;
			}

			HttpConnector.HttpStatus httpStatus = HttpConnector.HttpStatus.getStatusFromCode(response.statusCode());
			return new Exception(String.format("Status %d: %s", response.statusCode(), httpStatus.statusMessage));
		} catch (Exception e) {
			return e;
		}
	}

	/**
	 * Check if exists request url and catch error if exists
	 *
	 * @param runnable Action to execute. Only if not has an error.
	 * @return {@link Throwable} error or {@code null} if don't have error
	 */
	@Nullable
	public Throwable existsCatching(@Nullable Runnable runnable) {
		return existsCatching(false, runnable);
	}

	/**
	 * Set request headers
	 *
	 * @param headers Set request headers
	 * @return {@link HttpRequestAction} current instance
	 */
	public HttpRequestAction setRequestHeaders(Map<String, String> headers) {
		requestHeaders = headers;
		return this;
	}

	/**
	 * Send GET http request.
	 *
	 * @return {@link HttpResponse} Request result
	 * @throws IOException          if an I/O error occurs when sending or receiving
	 * @throws InterruptedException if the operation is interrupted
	 */
	public HttpResponse<String> get() throws IOException, InterruptedException {
		HttpClient client = HttpConnector.getHttpClient();
		HttpRequest request = getBuilder()
			.uri(requestURI)
			.GET()
			.headers(makeHeaders())
			.build();

		return client.send(request, HttpResponse.BodyHandlers.ofString());
	}

	/**
	 * Get request asynchronously
	 *
	 * @param action Target async action.
	 */
	public void getAsync(@NotNull HttpAction<HttpResponse<String>> action) {
		HttpClient client = HttpConnector.getHttpClient();
		HttpRequest request = getBuilder()
			.uri(requestURI)
			.GET()
			.headers(makeHeaders())
			.build();

		client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
			.thenApply(action::invoke)
			.exceptionally(e -> action.exceptionally(e, request))
			.thenAccept(action::thenAccept);
	}

	/**
	 * Send POST http request.
	 *
	 * @param data Data to send
	 * @return {@link HttpResponse} Request result.
	 * @throws IOException          if an I/O error occurs when sending or receiving
	 * @throws InterruptedException if the operation is interrupted
	 */
	public HttpResponse<String> post(@Nullable Map<String, String> data) throws IOException, InterruptedException {
		HttpClient client = HttpConnector.getHttpClient();
		if (data == null) data = Collections.emptyMap();
		HttpRequest request = getBuilder()
			.uri(requestURI)
			.POST(HttpConnector.makeBodyPublisher(data))
			.header("Content-Type", "application/x-www-form-urlencoded")
			.headers(makeHeaders())
			.build();

		return client.send(request, HttpResponse.BodyHandlers.ofString());
	}

	/**
	 * Send POST http request with data (files and documents).
	 *
	 * @param data Data to send.
	 * @return {@link HttpResponse} request response result.
	 * @throws IOException          if an I/O error occurs when sending or receiving
	 * @throws InterruptedException if the operation is interrupted
	 */
	public HttpResponse<String> postD(@Nullable Map<String, Object> data) throws IOException, InterruptedException {
		if (data == null) data = Collections.emptyMap();
		HttpClient client = HttpConnector.getHttpClient();
		HttpRequestMultipartFormData.Builder formDataBuilder = HttpRequestMultipartFormData.newBuilder();

		for (Map.Entry<String, Object> entry : data.entrySet()) {
			if (entry.getValue() instanceof File) {
				formDataBuilder.addFile((File) entry.getValue());
			} else if (entry.getValue() instanceof Path) {
				formDataBuilder.addFile(((Path) entry.getValue()).toFile());
			} else {
				formDataBuilder.addText(entry.getKey(), (String) entry.getValue());
			}
		}

		// Build multipart
		HttpRequestMultipartFormData multipartFormData = formDataBuilder.build();

		HttpRequest request = getBuilder()
			.uri(requestURI)
			.POST(multipartFormData.getBodyPublisher())
			.header("Content-Type", multipartFormData.getContentType())
			.headers(makeHeaders())
			.build();

		return client.send(request, HttpResponse.BodyHandlers.ofString());
	}

	/**
	 * Send POST http request.
	 *
	 * @param action Async action request.
	 * @param data   Data to send
	 */
	public void postAsync(@NotNull HttpAction<HttpResponse<String>> action, @Nullable Map<String, String> data) {
		if (data == null) data = Collections.emptyMap();
		HttpClient client = HttpConnector.getHttpClient();
		HttpRequest request = getBuilder()
			.uri(requestURI)
			.POST(HttpConnector.makeBodyPublisher(data))
			.header("Content-Type", "application/x-www-form-urlencoded")
			.headers(makeHeaders())
			.build();

		client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
			.thenApply(action::invoke)
			.exceptionally(e -> action.exceptionally(e, request))
			.thenAccept(action::thenAccept);
	}

	/**
	 * Send POST http request with data (files and documents).
	 *
	 * @param action Async action request.
	 * @param data   Data to send
	 */
	public void postAsyncD(@NotNull HttpAction<HttpResponse<String>> action, @Nullable Map<String, Object> data) throws IOException {
		if (data == null) data = Collections.emptyMap();
		HttpClient client = HttpConnector.getHttpClient();
		HttpRequestMultipartFormData.Builder formDataBuilder = HttpRequestMultipartFormData.newBuilder();

		for (Map.Entry<String, Object> entry : data.entrySet()) {
			if (entry.getValue() instanceof File) {
				formDataBuilder.addFile((File) entry.getValue());
			} else if (entry.getValue() instanceof Path) {
				formDataBuilder.addFile(((Path) entry.getValue()).toFile());
			} else {
				formDataBuilder.addText(entry.getKey(), (String) entry.getValue());
			}
		}

		// Build multipart
		HttpRequestMultipartFormData multipartFormData = formDataBuilder.build();

		HttpRequest request = getBuilder()
			.uri(requestURI)
			.POST(multipartFormData.getBodyPublisher())
			.header("Content-Type", multipartFormData.getContentType())
			.headers(makeHeaders())
			.build();

		client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
			.thenApply(action::invoke)
			.exceptionally(e -> action.exceptionally(e, request))
			.thenAccept(action::thenAccept);
	}

	/* ---------------------------------------------------------
	 *
	 * Private Methods
	 *
	 * --------------------------------------------------------- */

	/**
	 * Get headers in specific format.
	 *
	 * @return {@link String} array with headers
	 * @see HttpRequest.Builder#headers(String...)
	 */
	private String[] makeHeaders() {
		// Create array list
		ArrayList<String> headersFormat = new ArrayList<>();

		// Check if headers is null and return empty array
		if (requestHeaders == null) {
			requestHeaders = new HashMap<>();
		}

		requestHeaders.put(
			"User-Agent",
			String.format("Java-http-client/%s", System.getProperty("java.version"))
		);
		// Iterate all map entries
		for (Map.Entry<String, String> entry : requestHeaders.entrySet()) {
			headersFormat.add(entry.getKey());
			headersFormat.add(entry.getValue());
		}

		// Return array with headers
		return headersFormat.toArray(String[]::new);
	}

	/**
	 * Create new request builder instance
	 *
	 * @return {@link HttpRequest.Builder} Instance object
	 */
	@NotNull
	@Contract(value = " -> new", pure = true)
	private HttpRequest.Builder getBuilder() {
		return HttpRequest.newBuilder();
	}

}
