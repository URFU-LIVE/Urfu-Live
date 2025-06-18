package live.urfu.frontend.data.api

import io.ktor.client.request.get
import live.urfu.frontend.data.DTOs.CheckerResponse

class CheckerApiService : BaseApiService() {

    suspend fun checkUsername(username: String): Result<CheckerResponse> {
        return authorizedRequest {
            client.get("$baseUrl/check/username") {
                url {
                    parameters.append("username", username)
                }

            }

        }
    }

    suspend fun checkEmail(email: String): Result<CheckerResponse> {
        return authorizedRequest {
            client.get("$baseUrl/check/email") {
                url {
                    parameters.append("email", email)
                }
            }
        }
    }
}