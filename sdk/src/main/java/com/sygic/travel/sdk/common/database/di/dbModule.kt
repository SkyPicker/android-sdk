package com.sygic.travel.sdk.common.database.di

import android.arch.persistence.room.Room
import android.content.Context
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.singleton
import com.sygic.travel.sdk.common.database.Database
import com.sygic.travel.sdk.favorites.model.daos.FavoriteDao

internal val dbModule = Kodein.Module {
	bind<Database>() with singleton {
		Room.databaseBuilder(
			instance<Context>(),
			Database::class.java,
			"st-sdk-db"
		).build()
	}

	bind<FavoriteDao>() with singleton {
		instance<Database>().favoriteDao()
	}
}
