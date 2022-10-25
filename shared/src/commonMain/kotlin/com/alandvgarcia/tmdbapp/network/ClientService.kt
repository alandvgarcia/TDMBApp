package com.alandvgarcia.tmdbapp.network

import co.touchlab.kermit.Logger
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.*
import io.ktor.client.utils.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json


interface ClientServiceImplementation {
    val client: HttpClient
}
class ClientService: ClientServiceImplementation {
    override val client = HttpClient {
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
        install(Logging) {
            LogLevel.ALL
            logger = object : io.ktor.client.plugins.logging.Logger {
                override fun log(message: String) {
                    Logger.d(message)
                }

            }
        }
    }
}
suspend inline fun <T, reified E> ClientServiceImplementation.safeResponse(
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