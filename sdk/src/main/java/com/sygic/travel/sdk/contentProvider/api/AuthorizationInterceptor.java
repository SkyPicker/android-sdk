package com.sygic.travel.sdk.contentProvider.api;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthorizationInterceptor implements Interceptor {
	private static final String X_API_KEY = "x-api-key";

	private String xApiKey;

	public void updateXApiKey(String xApiKey){
		this.xApiKey = xApiKey;
	}

	@Override
	public Response intercept(Chain chain) throws IOException {
		Request original = chain.request();
		if(xApiKey != null && !xApiKey.equals("")) {
			Request request = original.newBuilder()
				.addHeader(X_API_KEY, xApiKey)
				.method(original.method(), original.body())
				.build();
			return chain.proceed(request);
		} else {
			return chain.proceed(original);
		}
	}
}
