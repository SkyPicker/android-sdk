package com.sygic.travel.sdk.synchronization.model

class SynchronizationResult(
	val changedTripIds: MutableSet<String> = mutableSetOf(),
	val changedFavoriteIds: MutableSet<String> = mutableSetOf(),
	val createdTripIdsMapping: MutableMap<String, String> = mutableMapOf(),
	var success: Boolean = true,
	var exception: Exception? = null
)
