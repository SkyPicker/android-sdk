package com.sygic.travel.sdk.contentProvider.api;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import static com.sygic.travel.sdk.contentProvider.api.ApiConstants.API_VERSION;

/**
 * Created by michal.murin on 16.2.2017.
 */

public class LocaleInterceptor implements Interceptor{
	public static final String TO_INTERCEPT = "[api_version_and_locale]";

	private String locale;

	public LocaleInterceptor(){
		updateLocale();
	}

	public void updateLocale(){
		locale = SupportedLanguages.getActualLocale();
	}

	@Override
	public Response intercept(Interceptor.Chain chain) throws IOException {
		Request original = chain.request();
		String url = original.url().toString();

		url = url.replace(TO_INTERCEPT, API_VERSION + "/" + locale);

		Request request = original.newBuilder()
			.url(url)
			.method(original.method(), original.body())
			.build();

		return chain.proceed(request);
	}
}
