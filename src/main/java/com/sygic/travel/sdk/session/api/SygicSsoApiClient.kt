package com.sygic.travel.sdk.session.api

import com.sygic.travel.sdk.session.api.model.AuthenticationRequest
import com.sygic.travel.sdk.session.api.model.ResetPasswordRequest
import com.sygic.travel.sdk.session.api.model.SessionResponse
import com.sygic.travel.sdk.session.api.model.UserRegistrationRequest
import com.sygic.travel.sdk.session.api.model.UserRegistrationResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

internal interface SygicSsoApiClient {
	@Headers("Content-Type: application/json")
	@POST("/oauth2/token")
	fun authenticate(
		@Body authenticationRequest: AuthenticationRequest
	): Call<SessionResponse>

	@Headers("Content-Type: application/json")
	@POST("/user/register")
	fun registerUser(
		@Header("Authorization") accessToken: String,
		@Body userAuthRequestBody: UserRegistrationRequest
	): Call<UserRegistrationResponse>

	@Headers("Content-Type: application/json")
	@POST("/user/reset-password")
	fun resetPassword(
		@Header("Authorization") accessToken: String,
		@Body resetPasswordRequest: ResetPasswordRequest
	): Call<Void>
}
