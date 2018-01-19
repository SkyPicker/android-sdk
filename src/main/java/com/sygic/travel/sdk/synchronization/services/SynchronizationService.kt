package com.sygic.travel.sdk.synchronization.services

import android.annotation.SuppressLint
import android.content.SharedPreferences
import com.sygic.travel.sdk.common.api.SygicTravelApiClient
import com.sygic.travel.sdk.favorites.api.model.FavoriteRequest
import com.sygic.travel.sdk.favorites.model.Favorite
import com.sygic.travel.sdk.favorites.service.FavoriteService
import com.sygic.travel.sdk.synchronization.api.model.ApiChangesResponse
import com.sygic.travel.sdk.synchronization.model.SynchronizationResult
import com.sygic.travel.sdk.trips.api.TripConverter
import com.sygic.travel.sdk.trips.api.model.ApiTripItemResponse
import com.sygic.travel.sdk.trips.api.model.ApiUpdateTripResponse
import com.sygic.travel.sdk.trips.model.Trip
import com.sygic.travel.sdk.trips.services.TripsService
import com.sygic.travel.sdk.utils.DateTimeHelper

internal class SynchronizationService constructor(
	private val sharedPreferences: SharedPreferences,
	private val apiClient: SygicTravelApiClient,
	private val tripConverter: TripConverter,
	private val tripsService: TripsService,
	private val favoriteService: FavoriteService
) {
	companion object {
		private const val SINCE_KEY = "sync.since"
	}

	@SuppressLint("ApplySharedPref")
	fun synchronize(): SynchronizationResult {
		val since = sharedPreferences.getLong(SINCE_KEY, 0)
		val changesResponse = apiClient.getChanges(
			DateTimeHelper.timestampToDatetime(since)
		).execute().body()!!

		val changedTripIds = mutableListOf<String>()
		val deletedTripIds = mutableListOf<String>()
		val addedFavoriteIds = mutableListOf<String>()
		val deletedFavoriteIds = mutableListOf<String>()

		for (change in changesResponse.data?.changes ?: arrayListOf()) {
			when (change.type) {
				ApiChangesResponse.ChangeEntry.TYPE_TRIP -> {
					if (change.change == ApiChangesResponse.ChangeEntry.CHANGE_UPDATED) {
						changedTripIds.add(change.id!!)
					} else if (change.change == ApiChangesResponse.ChangeEntry.CHANGE_DELETED) {
						deletedTripIds.add(change.id!!)
					}
				}
				ApiChangesResponse.ChangeEntry.TYPE_FAVORITE -> {
					if (change.change == ApiChangesResponse.ChangeEntry.CHANGE_UPDATED) {
						addedFavoriteIds.add(change.id!!)
					} else if (change.change == ApiChangesResponse.ChangeEntry.CHANGE_DELETED) {
						deletedFavoriteIds.add(change.id!!)
					}
				}
				ApiChangesResponse.ChangeEntry.TYPE_SETTINGS -> {
				} // ignore
			}
		}

		val tripsResult = syncTrips(changedTripIds, deletedTripIds)
		val favoritesResult = syncFavorites(addedFavoriteIds, deletedFavoriteIds)

		val changesFetchedAt = DateTimeHelper.datetimeToTimestamp(changesResponse.server_timestamp)!!
		sharedPreferences.edit()
			.putLong(SINCE_KEY, changesFetchedAt)
			.commit()

		return SynchronizationResult(
			trips = tripsResult,
			favorites = favoritesResult
		)
	}

	fun clearUserData() {
		sharedPreferences.edit().remove(SINCE_KEY).apply()
	}

	private fun syncTrips(changedTripIds: MutableList<String>, deletedTripIds: MutableList<String>): SynchronizationResult.TripsResult {
		val changedTrips = if (changedTripIds.isNotEmpty()) {
			apiClient.getTrips(
				changedTripIds.joinToString("|")
			).execute().body()!!.data?.trips ?: arrayListOf()
		} else {
			mutableListOf()
		}

		val added = mutableListOf<String>()
		val updated = mutableListOf<String>()

		for (trip in changedTrips) {
			val isAdded = syncApiChangedTrip(trip)
			when (isAdded) {
				true -> added.add(trip.id)
				false -> updated.add(trip.id)
			}
		}

		for (deletedTripId in deletedTripIds) {
			syncApiDeletedTrip(deletedTripId)
		}

		val created = syncLocalChangedTrips(changedTripIds, deletedTripIds)

		return SynchronizationResult.TripsResult(
			added = added,
			updated = updated,
			removed = deletedTripIds,
			createdOnServerIdMap = created
		)
	}

	private fun syncFavorites(addedFavoriteIds: MutableList<String>, deletedFavoriteIds: MutableList<String>): SynchronizationResult.FavoritesResult {
		for (favoriteId in addedFavoriteIds) {
			favoriteService.addPlace(favoriteId)
		}
		for (favoriteId in deletedFavoriteIds) {
			favoriteService.removePlace(favoriteId)
		}
		for (favorite in favoriteService.getFavoritesForSynchronization()) {
			if (favorite.state == Favorite.STATE_TO_ADD) {
				val response = apiClient.createFavorite(FavoriteRequest(favorite.id)).execute()
				if (response.isSuccessful) {
					favoriteService.markAsSynchronized(favorite)
				}
			} else if (favorite.state == Favorite.STATE_TO_REMOVE) {
				val response = apiClient.deleteFavorite(FavoriteRequest(favorite.id)).execute()
				if (response.isSuccessful) {
					favoriteService.markAsSynchronized(favorite)
				}
			}
		}

		return SynchronizationResult.FavoritesResult(
			added = addedFavoriteIds,
			removed = deletedFavoriteIds
		)
	}

	private fun syncApiChangedTrip(apiTrip: ApiTripItemResponse): Boolean {
		var apiTripData: ApiTripItemResponse? = apiTrip
		var localTrip = tripsService.getTrip(apiTrip.id)
		val isAdded = localTrip == null

		if (localTrip?.isChanged == true) {
			apiTripData = updateTrip(localTrip)
			if (apiTripData == null) {
				// do nothing and let user choose when he will use the app
				return isAdded
			}
		}

		localTrip = tripConverter.fromApi(apiTripData!!)
		tripsService.saveTrip(localTrip)
		return isAdded
	}

	private fun showDialogAndGetResponse(conflictInfo: ApiUpdateTripResponse.ConflictInfo): Boolean? {
		return false // todo
	}

	private fun syncApiDeletedTrip(deletedTripId: String) {
		tripsService.deleteTrip(deletedTripId)
	}

	private fun syncLocalChangedTrips(apiChangedTripIds: List<String>, apiDeletedTripIds: List<String>): Map<String, String> {
		val createdTripIdsMap = mutableMapOf<String, String>()
		val changedTrips = tripsService.findAllChangedExceptApiChanged(apiChangedTripIds)
		for (changedTrip in changedTrips) {
			syncLocalChangedTrip(changedTrip, apiDeletedTripIds.contains(changedTrip.id), createdTripIdsMap)
		}
		return createdTripIdsMap
	}

	private fun syncLocalChangedTrip(trip: Trip, deletedOnApi: Boolean, createdTripIdsMap: MutableMap<String, String>) {
		if (trip.isLocal()) {
			val createResponse = apiClient.createTrip(
				tripConverter.toApi(trip)
			).execute().body()!!
			createdTripIdsMap[trip.id] = createResponse.data!!.trip.id
			tripsService.replaceTripId(trip, createResponse.data.trip.id)
			val updatedTrip = tripConverter.fromApi(createResponse.data.trip)
			tripsService.saveTrip(updatedTrip)

		} else if (!deletedOnApi) {
			val apiTripData = updateTrip(trip)
			val updatedTrip = tripConverter.fromApi(apiTripData!!)
			tripsService.saveTrip(updatedTrip)

		} else {
			tripsService.deleteTrip(trip.id)
		}
	}

	private fun updateTrip(localTrip: Trip): ApiTripItemResponse? {
		val updateResponse = apiClient.updateTrip(
			localTrip.id,
			tripConverter.toApi(localTrip)
		).execute().body()!!

		when (updateResponse.data?.conflictResolution) {
			ApiUpdateTripResponse.CONFLICT_RESOLUTION_IGNORED -> {
				val override = showDialogAndGetResponse(updateResponse.data.conflictInfo!!)
				if (override == null) {
					// do nothing and let user choose when he will use the app
					return null
				} else if (override) {
					localTrip.updatedAt = DateTimeHelper.now()
					// if request fails, user will not have to do the decision again
					tripsService.saveTrip(localTrip)
					val repeatedUpdateResponse = apiClient.updateTrip(
						localTrip.id,
						tripConverter.toApi(localTrip)
					).execute().body()!!
					return repeatedUpdateResponse.data!!.trip
				}
			}
		}

		return updateResponse.data!!.trip
	}
}
