package com.sygic.travel.sdk.trips.api.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal class ApiCreateTripResponse(
	val trip: ApiTripItemResponse
)
