package com.sygic.travel.sdk.session.api.model

import com.google.gson.annotations.SerializedName

internal data class UserRegistrationRequest(
	@SerializedName("username")
	val username: String,

	@SerializedName("password")
	val password: String,

	@SerializedName("email")
	val email: String,

	@SerializedName("name")
	val name: String,

	@SerializedName("email_is_verified")
	val emailIsVerified: Boolean
)
