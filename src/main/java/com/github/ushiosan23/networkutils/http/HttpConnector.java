package com.github.ushiosan23.networkutils.http;

import org.jetbrains.annotations.NotNull;

import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * HttpConnector class.
 * Manage http connections and clients instances.
 */
final class HttpConnector {

	/* ---------------------------------------------------------
	 *
	 * Properties
	 *
	 * --------------------------------------------------------- */

	/**
	 * Http client instance
	 */
	private static HttpClient httpClient = null;

	/* ---------------------------------------------------------
	 *
	 * Constructors
	 *
	 * --------------------------------------------------------- */

	/**
	 * This class cannot be instantiated.
	 */
	private HttpConnector() {
	}

	/* ---------------------------------------------------------
	 *
	 * Methods
	 *
	 * --------------------------------------------------------- */

	/**
	 * Get HttpClient instance object.
	 *
	 * @param createNew     Create new client if is true
	 * @param replaceClient Replace current instance object
	 * @return {@link HttpClient} Instance object
	 */
	public static HttpClient getHttpClient(boolean createNew, boolean replaceClient) {
		if (httpClient == null || createNew) {
			if (replaceClient) {
				httpClient = createHttpClient();
			} else {
				return createHttpClient();
			}
		}

		return httpClient;
	}

	/**
	 * Get HttpClient without params
	 *
	 * @return {@link HttpClient} Instance object
	 */
	public static HttpClient getHttpClient() {
		return getHttpClient(false, true);
	}

	/**
	 * Create specific data to send via HttpRequest
	 *
	 * @param data Target data to make
	 * @return {@link HttpRequest.BodyPublisher} Instance result
	 */
	@NotNull
	static HttpRequest.BodyPublisher makeBodyPublisher(@NotNull Map<String, ?> data) {
		// Make string builder
		StringBuilder builder = new StringBuilder();

		// Iterate data
		for (Map.Entry<?, ?> entry : data.entrySet()) {
			// Check if data is empty
			// Add add "&" to separate map data
			if (builder.length() != 0) builder.append("&");

			// Insert data to string builder
			builder.append(URLEncoder.encode((String) entry.getKey(), StandardCharsets.UTF_8));
			builder.append("=");
			builder.append(URLEncoder.encode(entry.getValue().toString(), StandardCharsets.UTF_8));
		}

		// Create body publisher
		return HttpRequest.BodyPublishers.ofString(builder.toString());
	}

	/* ---------------------------------------------------------
	 *
	 * Private method
	 *
	 * --------------------------------------------------------- */

	/**
	 * Create HttpClient instance object
	 *
	 * @return {@link HttpClient} New instance
	 */
	private static HttpClient createHttpClient() {
		return HttpClient.newBuilder()
			.version(HttpClient.Version.HTTP_1_1)
			.build();
	}

	/* ---------------------------------------------------------
	 *
	 * Error Status
	 *
	 * --------------------------------------------------------- */

	/**
	 * Enumerate all response status
	 */
	public enum HttpStatus {
		/* Information status */
		STATUS_100(100, "Continue"),
		STATUS_101(101, "Switching Protocol"),
		STATUS_102(102, "Processing"),
		STATUS_103(103, "Early Hints"),
		/* Great status */
		STATUS_200(200, "Ok"),
		STATUS_201(201, "Created"),
		STATUS_202(202, "Accepted"),
		STATUS_203(203, "Non-Authoritative Information"),
		STATUS_204(204, "No Content"),
		STATUS_205(205, "Reset Content"),
		STATUS_206(206, "Partial Content"),
		STATUS_207(207, "Multi-Status"),
		STATUS_208(208, "Already Reported"),
		STATUS_226(226, "IM Used"),
		/* Redirection status */
		STATUS_300(300, "Multiple Choice"),
		STATUS_301(301, "Moved Permanently"),
		STATUS_302(302, "Found"),
		STATUS_303(303, "See Other"),
		STATUS_304(304, "Not Modified"),
		STATUS_305(305, "Use Proxy"),
		STATUS_306(306, "Unused"),
		STATUS_307(307, "Temporary Redirect"),
		STATUS_308(300, "Permanent Redirect"),
		/* Error status */
		STATUS_400(400, "Bad Request"),
		STATUS_401(401, "Unauthorized"),
		STATUS_402(402, "Payment Required"),
		STATUS_403(403, "Forbidden"),
		STATUS_404(404, "Not Found"),
		STATUS_405(405, "Method Not Allowed"),
		STATUS_406(406, "Not Acceptable"),
		STATUS_407(407, "Proxy Authentication Required"),
		STATUS_408(408, "Request Timeout"),
		STATUS_409(409, "Conflict"),
		STATUS_410(410, "Gone"),
		STATUS_411(411, "Length Required"),
		STATUS_412(412, "Precondition Failed"),
		STATUS_413(413, "Payload Too Large"),
		STATUS_414(414, "URI Too Long"),
		STATUS_415(415, "Unsupported Media Type"),
		STATUS_416(416, "Range Not Satisfiable"),
		STATUS_417(417, "Expectation Failed"),
		STATUS_418(418, "I'm a teapot"),
		STATUS_421(421, "Misdirected Request"),
		STATUS_422(422, "Unprocessable Entity"),
		STATUS_423(423, "Locked"),
		STATUS_424(424, "Failed Dependency"),
		STATUS_425(425, "Too Early"),
		STATUS_426(426, "Upgrade Required"),
		STATUS_428(428, "Precondition Required"),
		STATUS_429(429, "Too Many Request"),
		STATUS_431(431, "Request Header Fields Too Large"),
		STATUS_451(451, "Unavailable For Legal Reasons"),
		/* Server status */
		STATUS_500(500, "Internal Server Error"),
		STATUS_501(501, "Not Implemented"),
		STATUS_502(502, "Bad Gateway"),
		STATUS_503(503, "Service Unavailable"),
		STATUS_504(504, "Gateway Timeout"),
		STATUS_505(505, "HTTP Version Not Supported"),
		STATUS_506(506, "Variant Also Negotiates"),
		STATUS_507(507, "Insufficient Storage"),
		STATUS_508(508, "Loop Detected"),
		STATUS_510(510, "Not Extended"),
		STATUS_511(511, "Network Authentication Required"),
		/* Undefined */
		STATUS_UNKNOWN(-1, "Status Unknown");

		/**
		 * Status response code
		 */
		public int statusCode;

		/**
		 * Status message
		 */
		public String statusMessage;

		/**
		 * Constructor enum
		 *
		 * @param code    Status code
		 * @param message Status message
		 */
		HttpStatus(int code, String message) {
			statusCode = code;
			statusMessage = message;
		}

		/**
		 * Get status from code
		 *
		 * @param code Target code to search
		 * @return {@link HttpStatus} enum.
		 */
		public static HttpConnector.HttpStatus getStatusFromCode(int code) {
			for (HttpStatus status : HttpStatus.values()) {
				if (status.statusCode == code) return status;
			}

			return STATUS_UNKNOWN;
		}

	}

}
