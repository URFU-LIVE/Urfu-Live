package live.urfu.frontend.data.api

import TokenManagerInstance
import live.urfu.frontend.data.manager.InterestManagerInstance
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

abstract class BaseApiService {
    protected val baseUrl = "http://45.144.53.244:7070"

    protected val client: HttpClient by lazy {
        HttpClient(Android) {
            install(ContentNegotiation) {
                json(jsonConfig)
            }
        }
    }

    protected val jsonConfig = Json {
        ignoreUnknownKeys = true
        isLenient = true
        prettyPrint = false
    }

    protected suspend inline fun <reified T> authorizedRequest(
        crossinline requestBlock: suspend () -> HttpResponse
    ): Result<T> {
        return try {
            val response = requestBlock()
            val body = response.bodyAsText()

            when {
                response.status.value == 401 -> Result.failure(Exception("Пользователь не авторизован"))
                response.status.isSuccess() -> Result.success(jsonConfig.decodeFromString(body))
                else -> Result.failure(ResponseException(response, body))
            }
        } catch (e: Exception) {
            println(e.message)
            Result.failure(e)
        }
    }

    protected suspend fun getToken(): String? =
        TokenManagerInstance.getInstance().getAccessTokenBlocking()

    protected suspend fun getRefreshToken(): String? =
        TokenManagerInstance.getInstance().getRefreshTokenBlocking()

    protected suspend fun clearTokens() =
        TokenManagerInstance.getInstance().clearTokens()

    protected suspend fun saveTokens(access: String, refresh: String) =
        TokenManagerInstance.getInstance().saveTokens(access, refresh)

    protected suspend fun saveAccess(access: String) =
        TokenManagerInstance.getInstance().saveAccessToken(access)

    protected suspend fun getInterest(): Set<String> =
        InterestManagerInstance.getInstance().getSelectedInterestsBlocking()
}
