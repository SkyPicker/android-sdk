package com.sygic.travel.sdk.auth.api

import com.sygic.travel.sdk.auth.model.AuthorizationRequest
import com.sygic.travel.sdk.auth.model.UserRegistrationRequest
import com.sygic.travel.sdk.auth.model.UserRegistrationResponse
import com.sygic.travel.sdk.auth.model.UserSession
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface SygicTravelAuthApiClient {
	@Headers("Content-Type: application/json")
	@POST("/oauth2/token")
	fun authorize(
		@Body authorizationRequest: AuthorizationRequest
	): Call<UserSession>

	@Headers("Content-Type: application/json")
	@POST("/user/register")
	fun registerUser(
		@Header("Authorization") accessToken: String,
		@Body userAuthRequestBody: UserRegistrationRequest
	): Call<UserRegistrationResponse>
}
