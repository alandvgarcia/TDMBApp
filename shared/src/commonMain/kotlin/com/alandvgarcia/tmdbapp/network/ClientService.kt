package com.alandvgarcia.tmdbapp.network

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.utils.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class ClientService {
    val client = HttpClient {
        install(HttpTimeout) {
            requestTimeoutMillis = 30_000
        }


        install(ContentNegotiation) {
            json(Json {
                useAlternativeNames = false
                isLenient = true
                ignoreUnknownKeys = true
                encodeDefaults = true
            })
        }
//        install(Logging) {
//            logger = object : Logger {
//                override fun log(message: String) {
//                    Kermit().d("ClientService") { message }
//                }
//            }
//        }
    }

    suspend inline fun <T, reified E> safeResponse(
        url: String,
        body: T? = null,
        httpMethod: HttpMethod = HttpMethod.Get,
        headers: HashMap<String, String>? = null
    ): ClientServiceResult<E> {
        return try {
            val result = client.request(url) {
                method = httpMethod
                headers?.forEach {
                    headers {
                        append(it.key, it.value)
                    }
                }
                if (httpMethod == HttpMethod.Post || httpMethod == HttpMethod.Put || httpMethod == HttpMethod.Patch) {
                    contentType(ContentType.Application.Json)
                    setBody(body ?: EmptyContent)
                }
            }
            return if (result.status.isSuccess()) {
                ClientServiceResult.Success(result.body())
            } else {
                try {
                    ClientServiceResult.Error(
                        error = "HTTP Response code ${result.status}",
                    )
                } catch (e: Exception) {
                    ClientServiceResult.Error("HTTP Response code ${result.status}")
                }
            }
        } catch (e: Exception) {
            ClientServiceResult.Error(e.message.toString())
        } finally {
            client.close()
        }
    }
}