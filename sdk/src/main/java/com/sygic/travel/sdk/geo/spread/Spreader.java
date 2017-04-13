package com.sygic.travel.sdk.geo.spread;

import android.graphics.Point;

import com.sygic.travel.sdk.geo.GeoUtils;
import com.sygic.travel.sdk.model.geo.BoundingBox;
import com.sygic.travel.sdk.model.place.Place;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by michal.murin on 13.4.2017.
 */

public class Spreader {
	public SpreadResult spread(
		List<Place> places,
		List<SpreadSizeConfig> sizeConfigs,
		BoundingBox bounds,
		CanvasSize canvasSize
	){
		List<SpreadedPlace> visiblePlaces = new ArrayList<>();
		List<Place> hiddenPlaces = new ArrayList<>();

		for(Place place : places) {
			if(!place.hasLocation()){
				hiddenPlaces.add(place);
				continue;
			}

			Point canvasCoors = GeoUtils.locationToCanvasCoors(place.getLocation(), bounds, canvasSize);
			if(
				canvasCoors.x < 0 ||
				canvasCoors.y < 0 ||
				canvasCoors.x > canvasSize.width ||
				canvasCoors.y > canvasSize.height
			) {
				hiddenPlaces.add(place);
				continue;
			}

			for(SpreadSizeConfig sizeConfig : sizeConfigs) {
				if(sizeConfig.isPhotoRequired() && !place.hasThumbnailUrl()) {
					continue;
				}
				if(sizeConfig.getMinimalRating() > 0f &&
					place.getRating() <= sizeConfig.getMinimalRating()
				) {
					continue;
				}
				if(!intersects(sizeConfig, canvasCoors, visiblePlaces)) {
					visiblePlaces.add(new SpreadedPlace(place, canvasCoors, sizeConfig));
				}
			}
		}

		return new SpreadResult(visiblePlaces, hiddenPlaces);
	}

	private boolean intersects(
		SpreadSizeConfig sizeConfig,
		Point canvasCoors,
		List<SpreadedPlace> spreadedPlaces
	) {
		boolean intersects;
		int r2;
		int r = sizeConfig.getRadius() + sizeConfig.getMargin();

		for(SpreadedPlace spreadedPlace : spreadedPlaces) {
			int dX = canvasCoors.x - spreadedPlace.getCanvasCoors().x;
			int dY = canvasCoors.y - spreadedPlace.getCanvasCoors().y;

			r2 = spreadedPlace.getSizeConfig().getRadius() + spreadedPlace.getSizeConfig().getMargin();
			intersects = (Math.pow(dX, 2) +	Math.pow(dY, 2)) <=	Math.pow(r + r2, 2);
			if(intersects) {
				return true;
			}
		}
		return false;
	}
}
