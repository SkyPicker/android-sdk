package com.sygic.travel.sdkdemo.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.sygic.travel.sdkdemo.R;

/**
 * Created by michal.murin on 28.3.2017.
 */

public class Utils {
	private static final String PHOTO_SIZE_PLACEHOLDER = "{size}";

	public static boolean isLandscape(Resources resources){
		return resources.getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
	}

	public static String getPhotoUrl(Context context, String urlTemplate) {
		return urlTemplate.replace(PHOTO_SIZE_PLACEHOLDER, getDetailPhotoSize(context));
	}

	public static String getDetailPhotoSize(Context context) {
		int width, height;
		DisplayMetrics displayMetrics = new DisplayMetrics();
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		display.getMetrics(displayMetrics);

		if(Utils.isLandscape(context.getResources())){
			width = ((int) (displayMetrics.widthPixels * 0.6f));
			height = displayMetrics.heightPixels;
		} else {
			width = displayMetrics.widthPixels;
			height = width;
//			height = (int) ((width >> 3) * 7f);
		}

		return width + "x" + height;
	}

	public static int getGalleryThumbSize(Context context, int spanCount) {
		int spacingInPx = context.getResources().getDimensionPixelSize(R.dimen.gallery_photo_spacing);
		int thumbSize = Math.round(
			(float) (getScreenWidth(context) - ((spanCount - 1) * spacingInPx)) / (float) spanCount
		);
		if(thumbSize % 2 != 0){
			thumbSize++;
		}
		return thumbSize;
	}

	public static int getScreenWidth(Context context) {
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		DisplayMetrics metrics = new DisplayMetrics();
		display.getMetrics(metrics);

		return metrics.widthPixels;
	}
}
