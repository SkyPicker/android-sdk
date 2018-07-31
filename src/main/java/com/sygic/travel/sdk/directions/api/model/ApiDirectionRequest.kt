package com.sygic.travel.sdk.directions.api.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal class ApiDirectionRequest(
	val origin: Location,
	val destination: Location,
	val avoid: List<String>,
	val waypoints: List<Location>
) {
	companion object {
		const val AVOID_FERRIES = "ferries"
		const val AVOID_HIGHWAYS = "highways"
		const val AVOID_TOLLS = "tolls"
		const val AVOID_UNPAVED = "unpaved"
	}

	@JsonClass(generateAdapter = true)
	internal class Location(
		val lat: Float,
		val lng: Float
	)
}
