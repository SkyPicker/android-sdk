package com.sygic.travel.sdk.common.api

import com.sygic.travel.sdk.places.responseWrappers.MediaResponse
import com.sygic.travel.sdk.places.responseWrappers.PlaceDetailedResponse
import com.sygic.travel.sdk.places.responseWrappers.PlacesResponse
import com.sygic.travel.sdk.tours.responseWrapper.TourResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 *
 * Contains available API requests.
 * @see [API Documentation](http://docs.sygictravelapi.com/1.0)
 */
interface SygicTravelApiClient {

	/*-----------------------------------------------------------------------------*/
	/*                                     GET                                     */
	/*-----------------------------------------------------------------------------*/

	@GET("places/list")
	fun getPlaces(
		@Query("query") query: String?,
		@Query(encoded = true, value = "levels") levels: String?,
		@Query(encoded = true, value = "categories") categories: String?,
		@Query(encoded = true, value = "map_tiles") mapTiles: String?,
		@Query("map_spread") mapSpread: Int?,
		@Query("bounds") bounds: String?,
		@Query(encoded = true, value = "tags") tags: String?,
		@Query(encoded = true, value = "parents") parents: String?,
		@Query("limit") limit: Int?
	): Call<PlacesResponse>

	@GET("places/{id}")
	fun getPlaceDetailed(
		@Path("id") id: String
	): Call<PlaceDetailedResponse>

	@GET("places")
	fun getPlacesDetailed(
		@Query(encoded = true, value = "ids") ids: String
	): Call<PlacesResponse>

	@GET("places/{id}/media")
	fun getPlaceMedia(
		@Path("id") id: String
	): Call<MediaResponse>

	@GET("tours")
	fun getTours(
		@Query("destination_id") destinationId: String?,
		@Query("page") page: Int?,
		@Query("sort_by") sortBy: String?,
		@Query("sort_direction") sortDirection: String?
	): Call<TourResponse>
}
