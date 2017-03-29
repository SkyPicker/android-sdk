package com.sygic.travel.sdk.model.media;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MainMedia {
	public static final String USAGE = "usage";
	public static final String MEDIA = "media";

	@SerializedName(USAGE)
	private Usage usage;
	@SerializedName(MEDIA)
	private List<Media> media;

	public Usage getUsage() {
		return usage;
	}

	public List<Media> getMedia() {
		return media;
	}
}
