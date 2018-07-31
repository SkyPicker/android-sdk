package com.sygic.travel.sdk.trips.api

import com.sygic.travel.sdk.trips.api.model.ApiTripItemResponse
import com.sygic.travel.sdk.trips.model.Trip
import com.sygic.travel.sdk.trips.model.TripDay

internal class TripDayConverter constructor(
	private val tripDayItemConverter: TripDayItemConverter
) {
	fun fromApi(apiDay: ApiTripItemResponse.Day): TripDay {
		return TripDay(
			note = apiDay.note,
			itinerary = apiDay.itinerary.map { tripDayItemConverter.fromApi(it) }
		)
	}

	fun toApi(localDay: TripDay): ApiTripItemResponse.Day {
		return ApiTripItemResponse.Day(
			itinerary = localDay.itinerary.map { tripDayItemConverter.toApi(it) },
			note = localDay.note
		)
	}
}
