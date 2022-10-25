import com.alandvgarcia.tmdbapp.network.ApiSettings
import com.alandvgarcia.tmdbapp.network.ClientServiceResult
import io.ktor.server.testing.testApplication
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals


class MoviesApiServiceTest {

    @Test
    fun `test request movies paging success`() = testApplication {

        val page = 1

        val movieApiTest = MovieApiTest()
        val result = movieApiTest.movieApi.getPopularMovies(page)

        println(result)
        assertEquals(true, result is ClientServiceResult.Success)
        assertEquals(true, (result as ClientServiceResult.Success).result.results.isNotEmpty())
        assertEquals(true, result.result.page == page)
    }


    @Test
    fun `test request movies empty paging`() = testApplication {

        val page = 3

        val movieApiTest = MovieApiTest()
        val result = movieApiTest.movieApi.getPopularMovies(page)

        println(result)
        assertEquals(true, result is ClientServiceResult.Error)
    }

}