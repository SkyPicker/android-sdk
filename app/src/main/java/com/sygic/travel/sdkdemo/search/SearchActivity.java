package com.sygic.travel.sdkdemo.search;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.sygic.travel.sdk.StSDK;
import com.sygic.travel.sdk.contentProvider.api.Callback;
import com.sygic.travel.sdk.model.place.Place;
import com.sygic.travel.sdk.model.query.Query;
import com.sygic.travel.sdkdemo.PlaceDetailActivity;
import com.sygic.travel.sdkdemo.R;
import com.sygic.travel.sdkdemo.list.PlacesAdapter;
import com.sygic.travel.sdkdemo.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
	private static final String TAG = SearchActivity.class.getSimpleName();
	private static final String GUID = "guid";

	private RecyclerView rvPlaces;
	private PlacesAdapter placesAdapter;
	private List<Place> places;
	private String lastQuery;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);

		initRecycler();
		loadPlaces(null);
	}

	@Override
	protected void onPause() {
		super.onPause();

		// Observables need to be unsubscribed, when the activity comes to background
		StSDK.getInstance().unsubscribeObservable();
	}

	// Recycler view initialization - list with dividers
	private void initRecycler() {
		rvPlaces = (RecyclerView) findViewById(R.id.rv_result_places);
		rvPlaces.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
		rvPlaces.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
		placesAdapter = new PlacesAdapter(getOnPlaceClick(), Utils.getDetailPhotoSize(this));
		rvPlaces.setAdapter(placesAdapter);
	}

	// On a place click listener. Opens it's detail.
	private PlacesAdapter.ViewHolder.PlaceClick getOnPlaceClick() {
		return new PlacesAdapter.ViewHolder.PlaceClick() {
			@Override
			public void onPlaceClick(int position) {
				Intent placeDetailIntent = new Intent(SearchActivity.this, PlaceDetailActivity.class);
				placeDetailIntent.putExtra(GUID, places.get(position).getGuid());
				startActivity(placeDetailIntent);
			}
		};
	}

	// Use the SDK to load places
	private void loadPlaces(String searchQuery) {
		Query query = new Query(searchQuery, null, null, null, null, null, null, "city:1", 128);
		StSDK.getInstance().getPlaces(query, getPlacesCallback());
	}

	private void renderPlacesList(List<Place> places) {
		this.places = places;
		placesAdapter.setPlaces(places);
		placesAdapter.notifyDataSetChanged();
	}

	private Callback<List<Place>> getPlacesCallback() {
		return new Callback<List<Place>>() {
			@Override
			public void onSuccess(List<Place> places) {
				// Places are sorted by rating, best rated places are at the top of the list
				Collections.sort(places, new Comparator<Place>() {
					@Override
					public int compare(Place p1, Place p2) {
						if(p1.getRating() == p2.getRating()){
							return 0;
						} else {
							return p1.getRating() > p2.getRating() ? -1 : 1;
						}
					}
				});
				renderPlacesList(places);
			}

			@Override
			public void onFailure(Throwable t) {
				Log.d(TAG, "Places: onFailure");
				t.printStackTrace();
			}
		};
	}

	// SEARCH

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.search, menu);
		MenuItem searchItem = menu.findItem(R.id.action_search);
		setSearchListeners(searchItem);
		return true;
	}

	// Sets listners for search edit text.
	private void setSearchListeners(MenuItem searchItem) {
		SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
		EditText searchEdit = (EditText) searchView.findViewById(R.id.search_src_text);

		searchEdit.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
		searchEdit.setOnEditorActionListener(getOnKeyboardEnterClickListener());
		searchView.setOnQueryTextListener(getOnQueryTextListener());
	}

	private TextView.OnEditorActionListener getOnKeyboardEnterClickListener() {
		return new TextView.OnEditorActionListener() {
			public boolean onEditorAction(TextView tv, int actionId, KeyEvent event) {
				if(actionId == EditorInfo.IME_ACTION_SEARCH) {
					search(tv.getText().toString());
				}
				return false;
			}
		};
	}

	private SearchView.OnQueryTextListener getOnQueryTextListener() {
		return new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String query) {
				search(query);
				return true;
			}

			@Override
			public boolean onQueryTextChange(String query) {
				search(query);
				return true;
			}
		};
	}

	public void search(String query) {
		if(!query.equals(lastQuery)) {
			lastQuery = query;
			loadPlaces(query);
		}
	}
}
