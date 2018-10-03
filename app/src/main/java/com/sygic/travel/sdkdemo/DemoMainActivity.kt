package com.sygic.travel.sdkdemo

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import com.sygic.travel.sdk.StSDK
import com.sygic.travel.sdkdemo.favorites.FavoritesActivity
import com.sygic.travel.sdkdemo.list.PlacesListActivity
import com.sygic.travel.sdkdemo.map.MapsActivity
import com.sygic.travel.sdkdemo.search.SearchActivity
import com.sygic.travel.sdkdemo.tours.ToursActivity

class DemoMainActivity : AppCompatActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_demo_main)

		// Sygic Travel SDK initialization. Must be called on the application start. You can initialize the SDK
		// in your Application class' onCreate() method.
		StSDK.initialize(getString(R.string.api_key), this)
		initUI()
	}

	private fun initUI() {
		findViewById<Button>(R.id.btn_map_activity).setOnClickListener { startMapActivity() }
		findViewById<Button>(R.id.btn_list_activity).setOnClickListener { startListActivity() }
		findViewById<Button>(R.id.btn_search_activity).setOnClickListener { startSearchActivity() }
		findViewById<Button>(R.id.btn_tours_activity).setOnClickListener { startToursActivity() }
		findViewById<Button>(R.id.btn_favorites_activity).setOnClickListener { startFavoritesActivity() }
	}

	private fun startMapActivity() {
		val mapIntent = Intent(this, MapsActivity::class.java)
		startActivity(mapIntent)
	}

	private fun startListActivity() {
		val mapIntent = Intent(this, PlacesListActivity::class.java)
		startActivity(mapIntent)
	}

	private fun startSearchActivity() {
		val searchIntent = Intent(this, SearchActivity::class.java)
		startActivity(searchIntent)
	}

	private fun startToursActivity() {
		val toursIntent = Intent(this, ToursActivity::class.java)
		startActivity(toursIntent)
	}

	private fun startFavoritesActivity() {
		val favoritesIntent = Intent(this, FavoritesActivity::class.java)
		startActivity(favoritesIntent)
	}
}