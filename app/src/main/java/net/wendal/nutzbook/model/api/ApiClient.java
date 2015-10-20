package net.wendal.nutzbook.model.api;

import net.wendal.nutzbook.BuildConfig;
import net.wendal.nutzbook.util.gson.GsonWrapper;

import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

public final class ApiClient {

    private ApiClient() {}

    private static final String API_HOST = "https://nutz.cn/yvr/api";

    public static final ApiService service = new RestAdapter.Builder()
            .setEndpoint(API_HOST)
            .setConverter(new GsonConverter(GsonWrapper.gson))
            .setRequestInterceptor(new ApiRequestInterceptor())
            .setLogLevel(BuildConfig.DEBUG ? RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.NONE)
            .build()
            .create(ApiService.class);

}
