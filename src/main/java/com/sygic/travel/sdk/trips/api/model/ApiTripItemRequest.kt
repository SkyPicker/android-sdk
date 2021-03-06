package com.sygic.travel.sdk.trips.api.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal class ApiTripItemRequest(
	val name: String?,
	val base_version: Int,
	val updated_at: String,
	val privacy_level: String,
	val starts_on: String?,
	val is_deleted: Boolean,
	val destinations: List<String>,
	val days: List<ApiTripItemResponse.Day>
)
