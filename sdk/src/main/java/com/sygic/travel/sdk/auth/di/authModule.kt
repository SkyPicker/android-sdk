package com.sygic.travel.sdk.auth.di

import android.content.Context
import android.content.SharedPreferences
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.singleton
import com.google.gson.Gson
import com.sygic.travel.sdk.auth.api.SygicAuthApiClient
import com.sygic.travel.sdk.auth.facade.AuthFacade
import com.sygic.travel.sdk.auth.service.AuthService
import com.sygic.travel.sdk.auth.service.AuthStorageService

internal val authModule = Kodein.Module {
	bind<AuthStorageService>() with singleton {
		AuthStorageService(instance<SharedPreferences>())
	}

	bind<AuthService>() with singleton {
		AuthService(
			instance<SygicAuthApiClient>(),
			instance<AuthStorageService>(),
			instance<String>("clientId"),
			instance<Gson>()
		)
	}

	bind<AuthFacade>() with singleton {
		AuthFacade(
			instance<AuthService>()
		)
	}
}
