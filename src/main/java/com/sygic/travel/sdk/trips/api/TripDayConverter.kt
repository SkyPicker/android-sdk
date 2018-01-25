package com.sygic.travel.sdk.trips.api

import com.sygic.travel.sdk.trips.api.model.ApiTripItemResponse
import com.sygic.travel.sdk.trips.model.Trip
import com.sygic.travel.sdk.trips.model.TripDay

internal class TripDayConverter constructor(
	private val tripDayItemConverter: TripDayItemConverter
) {
	fun fromApi(apiDay: ApiTripItemResponse.Day, trip: Trip): TripDay {
		val localDay = TripDay(trip)
		localDay.note = apiDay.note
		localDay.itinerary = apiDay.itinerary.map { tripDayItemConverter.fromApi(it, localDay) }
		return localDay
	}

	fun toApi(localDay: TripDay): ApiTripItemResponse.Day {
		return ApiTripItemResponse.Day(
			itinerary = localDay.itinerary.map { tripDayItemConverter.toApi(it) },
			note = localDay.note
		)
	}
}
