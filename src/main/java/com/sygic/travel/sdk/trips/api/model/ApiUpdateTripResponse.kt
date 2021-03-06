package com.sygic.travel.sdk.trips.api.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal class ApiUpdateTripResponse(
	val trip: ApiTripItemResponse,
	val conflict_resolution: String?,
	val conflict_info: ConflictInfo?
) {
	companion object {
		const val CONFLICT_RESOLUTION_IGNORED = "ignored"
		const val CONFLICT_RESOLUTION_MERGED = "merged"
		const val CONFLICT_RESOLUTION_OVERRODE = "overrode"
		val NO_CONFLICT = null
	}

	@JsonClass(generateAdapter = true)
	internal class ConflictInfo(
		val last_user_name: String?,
		val last_updated_at: String
	)
}
