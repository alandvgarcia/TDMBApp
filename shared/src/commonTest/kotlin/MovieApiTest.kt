import com.alandvgarcia.tmdbapp.network.ClientServiceImplementation
import com.alandvgarcia.tmdbapp.network.MovieApi
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class MovieApiTest() : ClientServiceImplementation {

    val movieApi = MovieApi(clientService = this@MovieApiTest)

    private val mockEngine = MockEngine { request ->
        if (request.url.pathSegments.contains("popular") || request.url.pathSegments.contains("top_rated") || request.url.pathSegments.contains(
                "latest"
            )
        ) {
            try {
                val page = (request.url.parameters["page"]?.toInt()?.minus(1)) ?: 0
                respond(
                    content = resultMoviesPaging[page],
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, "application/json")
                )
            } catch (e: Exception) {
                respond(
                    content = e.toString(),
                    status = HttpStatusCode.NotFound,
                    headers = headersOf(HttpHeaders.ContentType, "application/json")
                )
            }
        } else {
            respond(
                content = "",
                status = HttpStatusCode.NotFound,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
    }

    override val client = HttpClient(
        engine = mockEngine
    ) {
        install(ContentNegotiation) {
            json(Json {
                useAlternativeNames = false
                isLenient = true
                ignoreUnknownKeys = true
                encodeDefaults = true
            })
        }
    }
}