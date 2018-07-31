package com.sygic.travel.sdk.places.service

import com.sygic.travel.sdk.common.LogicalOperator
import com.sygic.travel.sdk.common.api.SygicTravelApiClient
import com.sygic.travel.sdk.common.api.checkedExecute
import com.sygic.travel.sdk.places.facade.PlacesQuery
import com.sygic.travel.sdk.places.model.DetailedPlace
import com.sygic.travel.sdk.places.model.Place
import com.sygic.travel.sdk.places.model.media.Medium

internal class PlacesService(
	private val sygicTravelApiClient: SygicTravelApiClient
) {
	fun getPlaces(
		placesQuery: PlacesQuery
	): List<Place> {
		val request = sygicTravelApiClient.getPlaces(
			query = placesQuery.query,
			levels = placesQuery.getLevelsApiQuery(),
			categories = placesQuery.getCategoriesApiQuery(),
			categoriesNot = placesQuery.getCategoriesNotApiQuery(),
			mapTiles = placesQuery.getMapTilesApiQuery(),
			mapSpread = placesQuery.mapSpread,
			bounds = placesQuery.bounds?.toApiQuery(),
			tags = placesQuery.getTagsApiQuery(),
			tagsNot = placesQuery.getTagsNotApiQuery(),
			parents = placesQuery.getParentsApiQuery(),
			starRating = placesQuery.getStarRatingApiQuery(),
			customerRating = placesQuery.getCustomerRatingApiQuery(),
			limit = placesQuery.limit
		)
		val response = request.checkedExecute()
		return response.body()!!.data!!.fromApi()
	}

	fun getPlaceDetailed(id: String): DetailedPlace {
		val request = sygicTravelApiClient.getPlaceDetailed(id)
		return request.checkedExecute().body()!!.data!!.fromApi()
	}

	fun getPlacesDetailed(ids: List<String>): List<DetailedPlace> {
		val queryIds = ids.joinToString(LogicalOperator.OR.apiOperator)
		val request = sygicTravelApiClient.getPlacesDetailed(queryIds)
		return request.checkedExecute().body()!!.data!!.fromApi()
	}

	fun getPlaceMedia(id: String): List<Medium> {
		val request = sygicTravelApiClient.getPlaceMedia(id)
		return request.checkedExecute().body()!!.data!!.fromApi()
	}
}
