package com.sygic.travel.sdk.model.api;

import com.google.gson.annotations.SerializedName;
import com.sygic.travel.sdk.model.media.Medium;
import com.sygic.travel.sdk.model.place.Detail;
import com.sygic.travel.sdk.model.place.Place;

import java.util.List;

/**
 * <p>API response.</p>
 */
public class StResponse {
	public String status;
	public int statusCode;
	public String statusMessage;
	public Data data;
	public Error error;

	public String getStatus() {
		return status;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public String getStatusMessage() {
		return statusMessage;
	}

	public Data getData() {
		return data;
	}

	public Error getError() {
		return error;
	}

	/**
	 * Contains data from an API response. Only one property should be assinged.
	 */
	public class Data {
		public static final String PLACE = "place";
		public static final String MEDIA = "media";
		public static final String PLACES = "places";

		@SerializedName(PLACES)
		private List<Place> places;

		@SerializedName(PLACE)
		private Detail detail;

		@SerializedName(MEDIA)
		private List<Medium> media;

		public List<Place> getPlaces() {
			return places;
		}

		public Detail getDetail() {
			return detail;
		}

		public List<Medium> getMedia() {
			return media;
		}
	}

	/**
	 * Contains an error details - in case that an error has occurred.
	 */
	public class Error {
		private String id;
		private List<String> args;

		public String getId() {
			return id;
		}

		public List<String> getArgs() {
			return args;
		}
	}
}