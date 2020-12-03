package com.github.ushiosan23.networkutils.http;

import kotlin.Pair;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.net.http.HttpRequest;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Class to manage multipart request.
 * Send data like files, buffers, streams, etc.
 */
public final class HttpRequestMultipartFormData {

	/* ---------------------------------------------------------
	 *
	 * Properties
	 *
	 * --------------------------------------------------------- */

	/**
	 * Request boundary data.
	 */
	private String boundary;

	/**
	 * Data publisher.
	 */
	private HttpRequest.BodyPublisher bodyPublisher;

	/* ---------------------------------------------------------
	 *
	 * Constructors
	 *
	 * --------------------------------------------------------- */

	/**
	 * This class cannot be instanced.
	 */
	private HttpRequestMultipartFormData() {
	}

	/* ---------------------------------------------------------
	 *
	 * Methods
	 *
	 * --------------------------------------------------------- */

	/**
	 * Get request content type.
	 *
	 * @return content type request.
	 */
	public String getContentType() {
		return String.format("multipart/form-data; boundary=%s", boundary);
	}

	/**
	 * Get body publisher request.
	 *
	 * @return body publisher.
	 */
	public HttpRequest.BodyPublisher getBodyPublisher() {
		return bodyPublisher;
	}

	/**
	 * Create request builder.
	 *
	 * @return {@link HttpRequestMultipartFormData.Builder}
	 */
	@NotNull
	@Contract(" -> new")
	public static Builder newBuilder() {
		return new Builder();
	}

	/* ---------------------------------------------------------
	 *
	 * Builder section
	 *
	 * --------------------------------------------------------- */

	/**
	 * Builder class.
	 */
	public static class Builder {

		/**
		 * Storage mime data.
		 */
		private final List<ResourceData> dataList = new ArrayList<ResourceData>();

		/**
		 * Text data.
		 */
		private final Map<String, String> dataTexts = new LinkedHashMap<>();

		/**
		 * Builder boundary.
		 */
		private String boundary;

		/**
		 * Request charset.
		 */
		private Charset charset = StandardCharsets.UTF_8;

		/* ---------------------------------------------------------
		 *
		 * Constructors
		 *
		 * --------------------------------------------------------- */

		/**
		 * Create empty builder.
		 */
		private Builder() {
			boundary = new BigInteger(128, new Random()).toString();
		}

		/* ---------------------------------------------------------
		 *
		 * Methods
		 *
		 * --------------------------------------------------------- */

		/**
		 * Change charset request.
		 *
		 * @param c Charset element.
		 * @return Current builder instance.
		 */
		public Builder withCharset(Charset c) {
			charset = c;
			return this;
		}

		/**
		 * Set builder boundary.
		 *
		 * @param b Boundary value.
		 * @return Current builder instance.
		 */
		public Builder withBoundary(String b) {
			boundary = b;
			return this;
		}

		/**
		 * Add resource request file.
		 *
		 * @param file Target file to send.
		 * @return Current builder instance.
		 * @throws IOException if an I/O error occurs when sending or receiving
		 */
		public Builder addFile(@NotNull File file) throws IOException {
			if (!file.exists()) throw new IOException(String.format("File \"%s\" not exists.", file));
			dataList.add(new ResourceData(file));
			return this;
		}

		/**
		 * Add resource request file.
		 *
		 * @param location Target file to send.
		 * @return Current builder instance.
		 * @throws IOException if an I/O error occurs when sending or receiving
		 */
		public Builder addFile(String location) throws IOException {
			return addFile(new File(location));
		}

		/**
		 * Add key pair to request.
		 *
		 * @param name  Key name.
		 * @param value Key value.
		 * @return Current builder instance.
		 */
		public Builder addText(String name, String value) {
			dataTexts.put(name, value);
			return this;
		}

		/**
		 * Add key pair to request.
		 *
		 * @param pair Key to add
		 * @return Current builder instance.
		 */
		public Builder addText(@NotNull Pair<String, String> pair) {
			dataTexts.put(pair.getFirst(), pair.getSecond());
			return this;
		}

		/**
		 * Build request data.
		 *
		 * @return {@link HttpRequestMultipartFormData} Instance request result.
		 * @throws IOException if an I/O error occurs.
		 */
		public HttpRequestMultipartFormData build() throws IOException {
			HttpRequestMultipartFormData multipartFormData = new HttpRequestMultipartFormData();
			multipartFormData.boundary = boundary;

			byte[] newLine = "\r\n".getBytes(charset);
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			// Iterate all resources
			for (ResourceData dataItem : dataList) {
				stream.write(String.format("--%s", boundary).getBytes(charset));
				stream.write(newLine);
				stream.write(String.format(
					"Content-Disposition: form-data; name=\"%s\"; filename=\"%s\"",
					dataItem.getResourceName(),
					dataItem.getResourcePath().getFileName()
				).getBytes(charset));
				stream.write(newLine);
				stream.write(String.format("Content-Type: %s", dataItem.getMimeType()).getBytes(charset));
				stream.write(newLine);
				stream.write(newLine);
				stream.write(Files.readAllBytes(dataItem.getResourcePath()));
				stream.write(newLine);
			}
			// Iterate all text params
			for (Map.Entry<String, String> entry : dataTexts.entrySet()) {
				stream.write(String.format("--%s", boundary).getBytes(charset));
				stream.write(newLine);
				stream.write(String.format(
					"Content-Disposition: form-data; name=\"%s\"",
					entry.getKey()
				).getBytes(charset));
				stream.write(newLine);
				stream.write(newLine);
				stream.write(entry.getValue().getBytes(charset));
				stream.write(newLine);
			}

			stream.write(String.format("--%s--", boundary).getBytes(charset));
			multipartFormData.bodyPublisher = HttpRequest.BodyPublishers.ofByteArray(stream.toByteArray());
			return multipartFormData;
		}

	}

	/* ---------------------------------------------------------
	 *
	 * Mime data class
	 *
	 * --------------------------------------------------------- */

	/**
	 * Resource data to send
	 */
	public static class ResourceData {

		/**
		 * File resource.
		 */
		private final File fileResource;

		/**
		 * Basic constructor
		 *
		 * @param resource File resource.
		 */
		public ResourceData(File resource) {
			fileResource = resource;
		}

		/**
		 * Location constructor
		 *
		 * @param resource File resource location
		 */
		public ResourceData(String resource) {
			fileResource = new File(resource);
		}

		/**
		 * Get file mime type.
		 *
		 * @return File mime type
		 */
		public String getMimeType() {
			try {
				Path path = fileResource.toPath();
				return Files.probeContentType(path);
			} catch (Exception e) {
				e.printStackTrace();
				return "plain/text";
			}
		}

		/**
		 * Get file location path.
		 *
		 * @return File path.
		 */
		public Path getResourcePath() {
			return fileResource.toPath();
		}

		/**
		 * Get file resource name.
		 *
		 * @return Resource name.
		 */
		public String getResourceName() {
			return fileResource.getName();
		}

	}


}
