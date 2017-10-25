package com.sygic.travel.sdk.auth.service

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.sygic.travel.sdk.auth.AuthenticationResponseCode
import com.sygic.travel.sdk.auth.RegistrationResponseCode
import com.sygic.travel.sdk.auth.api.SygicAuthApiClient
import com.sygic.travel.sdk.auth.model.AuthenticationRequest
import com.sygic.travel.sdk.auth.model.UserRegistrationRequest
import com.sygic.travel.sdk.auth.model.UserRegistrationResponse
import com.sygic.travel.sdk.auth.model.UserSession
import retrofit2.HttpException
import retrofit2.Response
import java.util.Date

class AuthService(
	private val sygicAuthClient: SygicAuthApiClient,
	private val authStorageService: AuthStorageService,
	private val clientId: String,
	private val gson: Gson
) {
	fun authWithPassword(username: String, password: String): AuthenticationResponseCode {
		return authenticate(AuthenticationRequest(
			clientId = clientId,
			grantType = "password",
			username = username,
			password = password,
			devicePlatform = "android"
		))
	}

	fun authWithGoogleToken(googleToken: String): AuthenticationResponseCode {
		return authenticate(AuthenticationRequest(
			clientId = clientId,
			grantType = "google",
			authorizationCode = googleToken,
			devicePlatform = "android"
		))
	}

	fun authWithFacebookToken(token: String): AuthenticationResponseCode {
		return authenticate(AuthenticationRequest(
			clientId = clientId,
			grantType = "facebook",
			accessToken = token,
			devicePlatform = "android"
		))
	}

	fun authWithJwtToken(token: String): AuthenticationResponseCode {
		return authenticate(AuthenticationRequest(
			clientId = clientId,
			grantType = "external",
			token = token
		))
	}

	fun authWithDeviceId(): AuthenticationResponseCode {
		val deviceId = authStorageService.getDeviceId()
		return authenticate(AuthenticationRequest(
			clientId = clientId,
			grantType = "client_credentials",
			deviceCode = deviceId,
			devicePlatform = "android"
		))
	}

	fun register(email: String, password: String, name: String): RegistrationResponseCode {
		val userRegistrationRequest = UserRegistrationRequest(
			username = email,
			email = email,
			password = password,
			name = name,
			emailIsVerified = true
		)
		var clientSession = authStorageService.getClientSession() ?: initClientSession()

		var response = registerUserRequest(clientSession, userRegistrationRequest)
		if (response.code() == 401) { // client session is expired, let's reinitialize it
			clientSession = initClientSession()
			response = registerUserRequest(clientSession, userRegistrationRequest)
		}

		if (response.isSuccessful) {
			return RegistrationResponseCode.OK

		} else if (response.code() == 409) {
			return RegistrationResponseCode.ERROR_ALREADY_REGISTERED

		} else {
			val responseJson = gson.fromJson<JsonObject?>(response.errorBody()?.string(), JsonObject::class.java)
			return when (responseJson?.get("type")?.asString) {
				"validation.password.min_length" -> RegistrationResponseCode.ERROR_PASSWORD_MIN_LENGTH
				"validation.username.min_length", "validation.email.invalid_format" -> RegistrationResponseCode.ERROR_EMAIL_INVALID_FORMAT
				else -> RegistrationResponseCode.ERROR
			}
		}
	}

	fun getUserSession(): UserSession? {
		val accessToken = authStorageService.getUserSession() ?: return null
		val refreshToken = authStorageService.getRefreshToken() ?: return null
		val refreshTimeExpiration = authStorageService.getSuggestedRefreshTime()

		if (Date().time >= refreshTimeExpiration) {
			refreshToken(refreshToken)
		}

		return UserSession(
			accessToken = accessToken,
			refreshToken = refreshToken,
			expiresIn = refreshTimeExpiration
		)
	}

	fun logout() {
		authStorageService.setUserSession(null)
		authStorageService.setTokenRefreshTime(0)
		authStorageService.setRefreshToken(null)
	}

	private fun authenticate(authRequest: AuthenticationRequest): AuthenticationResponseCode {
		val response = sygicAuthClient.authenticate(authRequest).execute()
		if (response.isSuccessful) {
			val userSession = response.body()!!
			authStorageService.setUserSession(userSession.accessToken)
			authStorageService.setTokenRefreshTime(userSession.expiresIn)
			authStorageService.setRefreshToken(userSession.refreshToken)
			return AuthenticationResponseCode.OK

		} else if (response.code() == 401) {
			return AuthenticationResponseCode.ERROR_INVALID_CREDENTIALS

		} else {
			return AuthenticationResponseCode.ERROR
		}
	}

	private fun registerUserRequest(
		clientSession: String?,
		userRegistrationRequest: UserRegistrationRequest
	): Response<UserRegistrationResponse?> {
		return sygicAuthClient.registerUser(
			"Bearer $clientSession",
			userRegistrationRequest
		).execute()
	}

	private fun refreshToken(refreshToken: String) {
		val authRequest = AuthenticationRequest(
			clientId = clientId,
			grantType = "refresh_token",
			refreshToken = refreshToken
		)
		authenticate(authRequest)
	}

	private fun initClientSession(): String {
		val authRequest = AuthenticationRequest(
			clientId = clientId,
			grantType = "client_credentials"
		)
		val response = sygicAuthClient.authenticate(authRequest).execute()

		return if (response.isSuccessful) {
			val accessToken = response.body()!!.accessToken
			authStorageService.setClientSession(accessToken)
			accessToken
		} else throw HttpException(response)
	}
}
