package com.sygic.travel.sdk.synchronization.api.model

internal class ApiChangesResponse(
	val changes: List<ChangeEntry>
) {
	data class ChangeEntry(
		val type: String,
		val id: String?,
		val change: String,
		val version: Int?
	) {
		companion object {
			const val TYPE_TRIP = "trip"
			const val TYPE_FAVORITE = "favorite"
			const val TYPE_SETTINGS = "settings"
			const val CHANGE_UPDATED = "updated"
			const val CHANGE_DELETED = "deleted"
		}
	}
}