package com.sygic.travel.sdk.di

import android.content.Context
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.singleton
import com.github.salomonbrys.kodein.with
import com.sygic.travel.sdk.BuildConfig

object KodeinSetup {

	fun setupKodein(clientId: String, xApiKey: String, context: Context) = Kodein {
		constant("clientId") with clientId
		constant("apiKey") with xApiKey
		constant("isInDebugMode") with BuildConfig.DEBUG

		bind<Context>() with singleton { context }

		import(authModule)
		import(dbModule)
		import(favoritesModule)
		import(generalModule)
		import(placesModule)
		import(sygicAuthApiModule)
		import(sygicTravelApiModule)
		import(toursModule)
	}
}
