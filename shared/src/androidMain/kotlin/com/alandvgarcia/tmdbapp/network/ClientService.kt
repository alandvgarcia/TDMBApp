package com.alandvgarcia.tmdbapp.network

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.client.utils.*
import io.ktor.http.*

class ClientService {
    val client = HttpClient {
        install(HttpTimeout) {
            requestTimeoutMillis = 30_000
        }
        install(JsonFeature) {
            serializer = KotlinxSerializer(json = kotlinx.serialization.json.Json {
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
            val result = client.request<HttpResponse>(url) {
                method = httpMethod
                headers?.forEach {
                    headers {
                        append(it.key, it.value)
                    }
                }
                if (httpMethod == HttpMethod.Post || httpMethod == HttpMethod.Put || httpMethod == HttpMethod.Patch) {
                    contentType(ContentType.Application.Json)
                    this.body = body ?: EmptyContent
                }
            }
            return if (result.status.isSuccess()) {
                ClientServiceResult.Success(result.receive())
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