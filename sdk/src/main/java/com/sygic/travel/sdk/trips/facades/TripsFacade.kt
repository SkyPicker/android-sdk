package com.sygic.travel.sdk.trips.facades

import com.sygic.travel.sdk.trips.model.Trip
import com.sygic.travel.sdk.trips.model.TripInfo
import com.sygic.travel.sdk.trips.services.TripsService
import com.sygic.travel.sdk.utils.checkNotRunningOnMainThread

@Suppress("unused")
class TripsFacade internal constructor(
	private val tripsService: TripsService
) {
	fun getAllTrips(): List<TripInfo> {
		checkNotRunningOnMainThread()
		return tripsService.getTrips(null, null)
	}

	fun getTrips(from: Long?, to: Long?): List<TripInfo> {
		checkNotRunningOnMainThread()
		return tripsService.getTrips(from, to)
	}

	fun getUnscheduledTrips(): List<TripInfo> {
		checkNotRunningOnMainThread()
		return tripsService.getUnscheduledTrips()
	}

	fun getDeletedTrips(): List<TripInfo> {
		checkNotRunningOnMainThread()
		return tripsService.getDeletedTrips()
	}

	fun getTrip(id: String): Trip? {
		checkNotRunningOnMainThread()
		return tripsService.getTrip(id)
	}

	fun saveTrip(trip: Trip) {
		checkNotRunningOnMainThread()
		return tripsService.saveTrip(trip)
	}

	fun saveTrip(trip: TripInfo) {
		checkNotRunningOnMainThread()
		return tripsService.saveTrip(trip)
	}

	fun deleteTrip(trip: TripInfo) {
		checkNotRunningOnMainThread()
		return tripsService.deleteTrip(trip.id)
	}

	fun emptyTripTrash() {
		checkNotRunningOnMainThread()
		return tripsService.emptyTrash()
	}

	internal fun clearUserData() {
		checkNotRunningOnMainThread()
		tripsService.clearUserData()
	}
}
