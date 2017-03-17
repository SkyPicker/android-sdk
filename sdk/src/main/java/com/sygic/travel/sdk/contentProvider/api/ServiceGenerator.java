package com.sygic.travel.sdk.contentProvider.api;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sygic.travel.sdk.StEnvironment;
import com.sygic.travel.sdk.contentProvider.api.interceptors.AuthorizationInterceptor;
import com.sygic.travel.sdk.contentProvider.api.interceptors.LocaleInterceptor;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.sygic.travel.sdk.contentProvider.api.ApiConstants.API_BASE_URL;
import static com.sygic.travel.sdk.contentProvider.api.ApiConstants.API_BASE_URL_ALPHA;

public class ServiceGenerator {

	public static AuthorizationInterceptor authorizationInterceptor = new AuthorizationInterceptor();
	public static LocaleInterceptor localeInterceptor = new LocaleInterceptor();
	private static Interceptor loggingInterceptor = new HttpLoggingInterceptor()
		.setLevel(getHttpLoggingInterceptorLevel());
	private static OkHttpClient httpClient;

	public static <S> S createService(Class<S> serviceClass, File cacheDir) {
		return buildRetrofit(cacheDir).create(serviceClass);
	}

	public static Retrofit buildRetrofit(File cacheDir){
		return builder.client(getHttpClient(cacheDir)).build();
	}

	private static OkHttpClient getHttpClient(File cacheDir) {
		if(httpClient == null){
			httpClient = new OkHttpClient().newBuilder()
				.addInterceptor(loggingInterceptor)
				.addInterceptor(localeInterceptor)
				.addInterceptor(authorizationInterceptor)
//				.addInterceptor(apiResponseInterceptor)
				.cache(new Cache(cacheDir, 10 * 1024 * 1024))
				.readTimeout(60, TimeUnit.SECONDS)
				.build();
		}
		return httpClient;
	}

	private static Gson apiGson = new GsonBuilder()
		.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
		.create();

	//TO_INTERCEPT is replaced by api version and locale in LocaleInterceptor class

	private static Retrofit.Builder builder = new Retrofit.Builder()
		.baseUrl((StEnvironment.alpha ? API_BASE_URL_ALPHA : API_BASE_URL) + LocaleInterceptor.TO_INTERCEPT + "/")
		.addConverterFactory(GsonConverterFactory.create(apiGson));

	private static HttpLoggingInterceptor.Level getHttpLoggingInterceptorLevel() {
		if (StEnvironment.debug) {
			return HttpLoggingInterceptor.Level.BODY;
		} else {
			return HttpLoggingInterceptor.Level.NONE;
		}
	}
}
