package http.get

import com.github.ushiosan23.networkutils.http.HttpRequestAction
import com.github.ushiosan23.networkutils.http.getJson
import com.github.ushiosan23.networkutils.http.isValidJson
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import org.junit.Test
import java.net.HttpURLConnection
import java.net.URI

class HttpActionGetSimpleKtTest {

	private val url = URI.create("http://localhost/engines?platform=windows&type=all")
	private val action = HttpRequestAction(url)

	private fun initialize() {
		val response = action.get()

		if (response.statusCode() != HttpURLConnection.HTTP_OK) {
			println("Error ${response.statusCode()}")
			return
		}

		if (!response.isValidJson) {
			println(response.body())
			return
		}

		val jsonItem = response.getJson()!!
		val responseX = Json.decodeFromJsonElement<Response>(jsonItem)

		println(responseX)
	}

	@Test
	fun runTest() {
		initialize()
	}

	@Serializable
	data class Response(
		val has_error: Boolean,
		val response: List<EngineData>
	)

	@Serializable
	data class EngineData(
		val gd_id: Int,
		val gd_name: String,
		val gd_type: String,
		val gd_url: String,
		val gd_version: String,
		val gd_x64: Boolean,
		val gd_platform: String
	)
}
