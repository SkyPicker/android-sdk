package com.sygic.travel.sdk.directions.facades

import com.sygic.travel.sdk.directions.model.DirectionRequest
import com.sygic.travel.sdk.directions.model.DirectionResponse
import com.sygic.travel.sdk.directions.services.DirectionsService
import com.sygic.travel.sdk.utils.checkNotRunningOnMainThread

/**
 * Directions facade calls the API for the real navigation polyline and time & distance calculations.
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
class DirectionsFacade internal constructor(
	private val directionsService: DirectionsService
) {
	/**
	 * Queries the API for full directions. Reuses already cached directions.
	 */
	fun getDirection(request: DirectionRequest): DirectionResponse? {
		checkNotRunningOnMainThread()
		return directionsService.getDirections(listOf(request)).first()
	}

	/**
	 * Queries the API for full directions. Reuses already cached directions.
	 */
	fun getDirections(requests: List<DirectionRequest>): List<DirectionResponse?> {
		checkNotRunningOnMainThread()
		return directionsService.getDirections(requests)
	}

	/**
	 * Returns locally cached API full directions.
	 */
	fun getCachedDirections(requests: List<DirectionRequest>): List<DirectionResponse?> {
		checkNotRunningOnMainThread()
		return directionsService.getCachedDirections(requests)
	}
}
