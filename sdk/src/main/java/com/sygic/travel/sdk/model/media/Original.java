package com.sygic.travel.sdk.model.media;

import com.google.gson.annotations.SerializedName;

/**
 * <p>Medium's original parameters.</p>
 */
class Original {
	private static final String WIDTH = "width";
	private static final String HEIGHT = "height";
	private static final String SIZE = "size";

	@SerializedName(WIDTH)
	private int width;

	@SerializedName(HEIGHT)
	private int height;

	@SerializedName(SIZE)
	private int size;

	public Original() {
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}
}
