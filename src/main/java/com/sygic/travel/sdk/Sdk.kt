package com.sygic.travel.sdk

import android.content.Context
import com.github.salomonbrys.kodein.instance
import com.sygic.travel.sdk.auth.facade.AuthFacade
import com.sygic.travel.sdk.common.di.KodeinSetup
import com.sygic.travel.sdk.directions.facades.DirectionsFacade
import com.sygic.travel.sdk.events.facades.EventsFacade
import com.sygic.travel.sdk.favorites.facade.FavoritesFacade
import com.sygic.travel.sdk.places.facade.PlacesFacade
import com.sygic.travel.sdk.synchronization.facades.SynchronizationFacade
import com.sygic.travel.sdk.tours.facade.ToursFacade
import com.sygic.travel.sdk.trips.facades.TripsFacade

/**
 * Provides public methods for requesting API.
 */
class Sdk(
	applicationContext: Context,
	sdkConfig: SdkConfig
) {
	val authFacade: AuthFacade by lazy { kodein.instance<AuthFacade>() }
	val directionsFacade: DirectionsFacade by lazy { kodein.instance<DirectionsFacade>() }
	val eventsFacade: EventsFacade by lazy { kodein.instance<EventsFacade>() }
	val favoritesFacade: FavoritesFacade by lazy { kodein.instance<FavoritesFacade>() }
	val placesFacade: PlacesFacade by lazy { kodein.instance<PlacesFacade>() }
	val synchronizationFacade: SynchronizationFacade by lazy { kodein.instance<SynchronizationFacade>() }
	val toursFacade: ToursFacade by lazy { kodein.instance<ToursFacade>() }
	val tripsFacade: TripsFacade by lazy { kodein.instance<TripsFacade>() }
	private val kodein = KodeinSetup.setupKodein(applicationContext, sdkConfig)
}
