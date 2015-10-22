package net.wendal.nutzbook.model.api;

import com.squareup.okhttp.OkHttpClient;

import net.wendal.nutzbook.BuildConfig;
import net.wendal.nutzbook.util.gson.GsonWrapper;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import retrofit.RestAdapter;
import retrofit.client.Client;
import retrofit.client.OkClient;
import retrofit.client.Request;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;
import retrofit.mime.TypedByteArray;

public final class ApiClient {

    private ApiClient() {}

    private static final String API_HOST = "https://nutz.cn/yvr/api";

    public static final ApiService service;

    static {
        OkHttpClient client = new OkHttpClient();
        service = new RestAdapter.Builder()
                .setEndpoint(API_HOST)
                .setConverter(new GsonConverter(GsonWrapper.gson))
                .setRequestInterceptor(new ApiRequestInterceptor())
                .setLogLevel(BuildConfig.DEBUG ? RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.NONE)
                .setClient(new GzippedClient(new OkClient(client)))
                .build()
                .create(ApiService.class);
    }
}

class GzippedClient implements Client {

    private Client wrappedClient;

    public GzippedClient(Client wrappedClient) {
        this.wrappedClient = wrappedClient;
    }

    @Override
    public Response execute(Request request) throws IOException {
        Response response = wrappedClient.execute(request);

        boolean gzipped = false;
        for (retrofit.client.Header h : response.getHeaders()) {
            if (h.getName() != null && h.getName().toLowerCase().equals("content-encoding") && h.getValue() != null && h.getValue().toLowerCase().equals("gzip")) {
                gzipped = true;
                break;
            }
        }

        Response r = null;
        if (gzipped) {
            InputStream is = null;
            ByteArrayOutputStream bos = null;

            try {
                is = new BufferedInputStream(new GZIPInputStream(response.getBody().in()));
                bos = new ByteArrayOutputStream();

                int b;
                while ((b = is.read()) != -1) {
                    bos.write(b);
                }

                TypedByteArray body = new TypedByteArray(response.getBody().mimeType(), bos.toByteArray());
                r = new Response(response.getUrl(), response.getStatus(), response.getReason(), response.getHeaders(), body);
            } finally {
                if (is != null) {
                    is.close();
                }
                if (bos != null) {
                    bos.close();
                }
            }
        } else {
            r = response;
        }
        return r;
    }

}
