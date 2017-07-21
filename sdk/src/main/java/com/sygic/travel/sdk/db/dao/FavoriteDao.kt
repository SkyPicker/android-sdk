package com.sygic.travel.sdk.db.dao

import android.arch.persistence.room.*
import com.sygic.travel.sdk.model.place.Favorite

/**
 * Interface containing methods for working with favorite table in database.
 */
@Dao
interface FavoriteDao {
	/**
	 * @return All favorite places' ids.
	 */
	@Query("SELECT * FROM favorite")
	fun loadAll(): List<Favorite>

	/**
	 * Inserts one place's id into favorite table. If the id has already been inserted before, the
	 * insertion of [placeId] is skipped. No error is provided.
	 * @param placeId An id to be stored.
	 * @return A rowid of the inserted place id (row).
	 */
	@Insert(onConflict = OnConflictStrategy.IGNORE)
	fun insert(placeId: Favorite) : Long

	/**
	 * Removes a given place id from the favorite table.
	 * @param placeId An id to be removed.
	 * @return A number of deleted rows.
	 */
	@Delete
	fun delete(placeId: Favorite) : Int
}
