package com.sygic.travel.sdk.geo.spread;

import android.content.res.Resources;
import android.graphics.Point;
import android.util.Log;

import com.sygic.travel.sdk.model.geo.Bounds;
import com.sygic.travel.sdk.model.geo.Location;
import com.sygic.travel.sdk.model.place.Place;

import java.util.LinkedList;
import java.util.List;

/**
 * <p>Performs the main spreading algorithm for places.</p>
 */
public class Spreader {
	private Resources resources;

	/**
	 * <p>Performs the main spreading algorithm for places.</p>
	 * @param resources Resources needed to get markers' dimensions.
	 */
	public Spreader(Resources resources) {
		this.resources = resources;
	}

	/**
	 * <p>Generates a list of spread places.</p>
	 * @param places Places to spread.
	 * @param bounds Map bounds withing which the places are supposed to be spread.
	 * @param canvasSize Map canvas (view) size in pixels.
	 * @return Spread places as {@link SpreadResult}.
	 */
	public SpreadResult spreadPlacesOnMap(
		List<Place> places,
		Bounds bounds,
		CanvasSize canvasSize
	){
		LinkedList<SpreadedPlace> visiblePlaces = new LinkedList<>();
		LinkedList<Place> hiddenPlaces = new LinkedList<>();

		List<SpreadSizeConfig> sizeConfigs = SpreadConfigGenerator.getSpreadSizeConfigs(resources, bounds, canvasSize);

		for(Place place : places) {
			if(!place.hasLocation()){
				hiddenPlaces.add(0, place);
				continue;
			}

			Point canvasCoords = locationToCanvasCoords(place.getLocation(), bounds, canvasSize);
			if(
				canvasCoords.x < 0 ||
				canvasCoords.y < 0 ||
				canvasCoords.x > canvasSize.width ||
				canvasCoords.y > canvasSize.height
			) {
				hiddenPlaces.add(0, place);
				continue;
			}

			for(SpreadSizeConfig sizeConfig : sizeConfigs) {
				if(sizeConfig.isPhotoRequired() && !place.hasThumbnailUrl()) {
					continue;
				}
				if(sizeConfig.getMinimalRating() > 0f &&
					place.getRating() >= sizeConfig.getMinimalRating()
				) {
					continue;
				}
				if(!intersects(sizeConfig, canvasCoords, visiblePlaces)) {
					visiblePlaces.add(0, new SpreadedPlace(place, canvasCoords, sizeConfig));
				}
			}
		}

		return new SpreadResult(visiblePlaces, hiddenPlaces);
	}

	/**
	 * <p>Determines a given size configuration intersects with any of already spreaded places.</p>
	 * @param sizeConfig Size configuration of a place.
	 * @param canvasCoords Place's coordinates on canvas.
	 * @param spreadedPlaces Already spreaded places.
	 * @return
	 * <b>True</b> if place with given size configuration would intersect with any of already spreaded places.<br/>
	 * <b>False</b> otherwise - place can be displayed on a map with given {@param sizeConfig}
	 */
	private boolean intersects(
		SpreadSizeConfig sizeConfig,
		Point canvasCoords,
		List<SpreadedPlace> spreadedPlaces
	) {
		boolean intersects;
		int r2;
		int r = sizeConfig.getRadius() + sizeConfig.getMargin();

		for(SpreadedPlace spreadedPlace : spreadedPlaces) {
			int dX = canvasCoords.x - spreadedPlace.getCanvasCoords().x;
			int dY = canvasCoords.y - spreadedPlace.getCanvasCoords().y;

			r2 = spreadedPlace.getSizeConfig().getRadius() + spreadedPlace.getSizeConfig().getMargin();
			intersects = (Math.pow(dX, 2) +	Math.pow(dY, 2)) <=	Math.pow(r + r2, 2);
			if(intersects) {
				return true;
			}
		}
		return false;
	}

	/**
	 * <p>Converts given location within given bounds to {@code x, y} coordinates within given canvas.</p>
	 * @param location Location which is supposed be converted to {@code x, y} coordinates.
	 * @param bounds Bounds the location lies within.
	 * @param canvasSize Size of a canvas the coordinates are supposed to lie within.
	 * @return {@link Point} on a canvas.
	 */
	private Point locationToCanvasCoords(
		Location location,
		Bounds bounds,
		CanvasSize canvasSize
	){
		double south, west, north, east;
		south = bounds.getSouth();
		west = bounds.getWest();
		north = bounds.getNorth();
		east = bounds.getEast();

		double latDiff = north - location.getLat();
		double lngDiff = location.getLng() - west;

		double latRatio = canvasSize.height / Math.abs(south - north);
		double lngRatio;

		if (west > east){ //date border
			lngRatio = canvasSize.width / Math.abs(180 - west + 180 + east);
			if(location.getLng() < 0 && location.getLng() < east){
				lngDiff = 180 - west + 180 + location.getLng();
			}
			if(location.getLng() > 0 && location.getLng() < west){
				lngDiff = 180 - west + 180 + location.getLng();
			}
		} else {
			lngRatio = canvasSize.width / Math.abs(west - east);
		}

		return new Point(
			(int) (lngDiff * lngRatio),
			(int) (latDiff * latRatio)
		);
	}
}
