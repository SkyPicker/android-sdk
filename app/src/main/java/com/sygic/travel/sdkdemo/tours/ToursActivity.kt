package com.sygic.travel.sdkdemo.tours

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.Toast
import com.sygic.travel.sdk.Sdk
import com.sygic.travel.sdk.places.model.query.ToursQuery
import com.sygic.travel.sdk.tours.model.Tour
import com.sygic.travel.sdkdemo.Application
import com.sygic.travel.sdkdemo.R
import com.sygic.travel.sdkdemo.detail.PlaceDetailActivity
import com.sygic.travel.sdkdemo.detail.ReferenceActivity
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class ToursActivity : AppCompatActivity(), ToursAdapter.ListItemClickListener {
	private lateinit var sdk: Sdk
	private var rvTours: RecyclerView? = null
	private var toursAdapter: ToursAdapter? = null
	private var tours: List<Tour>? = null


	override fun onListItemClick(clickedItemIndex: Int) {
		val referenceIntent = Intent(this, ReferenceActivity::class.java)
		val clickedTour = tours?.get(clickedItemIndex)

		if (clickedTour?.url != null && clickedTour.url != "") {
			referenceIntent.putExtra(PlaceDetailActivity.REFERENCE_URL, clickedTour.url)
			referenceIntent.putExtra(PlaceDetailActivity.REFERENCE_TITLE, clickedTour.title)
		} else Toast.makeText(this, "No reference URL.", Toast.LENGTH_LONG).show()

		startActivity(referenceIntent)
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_tours_list)
		sdk = (application as Application).sdk

		initRecycler()
		loadTours()
	}

	private fun loadTours() {
		val tourQuery = ToursQuery(
			destinationId = "city:1",
			page = 1,
			sortBy = ToursQuery.SortBy.RATING,
			sortDirection = ToursQuery.SortDirection.ASC
		)

		Single.fromCallable { sdk.toursFacade.getTours(tourQuery) }
			.subscribeOn(Schedulers.io())
			.observeOn(AndroidSchedulers.mainThread())
			.subscribe({ data ->
				if (data != null) {
					tours = data
					bindTours()
				}
			}, { exception ->
				Toast.makeText(this@ToursActivity, exception.message, Toast.LENGTH_LONG).show()
				exception.printStackTrace()
			})
	}

	// Recycler view initialization - list with dividers
	private fun initRecycler() {
		rvTours = findViewById(R.id.rv_tours)
		rvTours!!.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
		rvTours!!.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
		toursAdapter = ToursAdapter(this)
		rvTours!!.adapter = toursAdapter
	}

	private fun bindTours() {
		toursAdapter?.setTours(tours)
		toursAdapter?.notifyDataSetChanged()
	}
}
