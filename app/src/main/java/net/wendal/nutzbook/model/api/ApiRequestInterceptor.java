package net.wendal.nutzbook.model.api;

import android.content.Context;
import android.os.Build;

import com.google.zxing.common.StringUtils;

import net.wendal.nutzbook.BuildConfig;
import net.wendal.nutzbook.storage.LoginShared;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import retrofit.RequestInterceptor;

public class ApiRequestInterceptor implements RequestInterceptor {

    private static final String APPLICATION_JSON = "application/json";
    private static final String USER_AGENT = "NutzCN/" + BuildConfig.VERSION_NAME + " (Android " + Build.VERSION.RELEASE + "; " + Build.MANUFACTURER + " - " + Build.MODEL + ")";

    public static Context ctx;

    @Override
    public void intercept(RequestFacade request) {
        request.addHeader("Accept", APPLICATION_JSON);
        request.addHeader("User-Agent", USER_AGENT);
        request.addHeader("Accept-Encoding", "gzip, deflate");
        if (ctx != null) {
            String at = LoginShared.getAccessToken(ctx);
            if (at != null && at.trim().length() > 10) {
                try {
                    MessageDigest digest = MessageDigest.getInstance("SHA-1");
                    String uuid = UUID.randomUUID().toString().replaceAll("-", "").toLowerCase();
                    digest.update(uuid.getBytes());
                    digest.update(at.getBytes());
                    byte [] sha1Bytes = digest.digest();
                    StringBuilder sb = new StringBuilder();
                    for (byte b : sha1Bytes) {
                        sb.append(String.format("%02X", b));
                    }
                    request.addHeader("Api-Version", "2");
                    request.addHeader("Api-Uid", LoginShared.getId(ctx));
                    request.addHeader("Api-Nonce", uuid);
                    request.addHeader("Api-Key", sb.toString());
                } catch (NoSuchAlgorithmException e) {
                    // 不可能
                }
            }
        }
    }

}
