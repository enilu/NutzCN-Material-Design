package net.wendal.nutzbook.model.api;

import android.content.Context;
import android.os.Build;

import com.google.zxing.common.StringUtils;
import com.xiaomi.mistatistic.sdk.MiStatInterface;

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
            String loginname = LoginShared.getLoginName(ctx);
            if (at != null && at.trim().length() > 10) {
                try {
                    MiStatInterface.recordStringPropertyEvent("apicall", "loginname", loginname);
                    long time = System.currentTimeMillis();
                    MessageDigest digest = MessageDigest.getInstance("SHA-1");
                    String uuid = UUID.randomUUID().toString().toLowerCase();
                    digest.update(at.getBytes());
                    digest.update(",".getBytes());
                    digest.update(loginname.getBytes());
                    digest.update(",".getBytes());
                    digest.update(uuid.getBytes());
                    digest.update(",".getBytes());
                    digest.update((""+time).getBytes());
                    byte [] sha1Bytes = digest.digest();
                    StringBuilder sb = new StringBuilder();
                    for (byte b : sha1Bytes) {
                        sb.append(String.format("%02x", b));
                    }
                    request.addHeader("Api-Version", "2");
                    request.addHeader("Api-Loginname", loginname);
                    request.addHeader("Api-Nonce", uuid);
                    request.addHeader("Api-Key", sb.toString());
                    request.addHeader("Api-Time", ""+time);
                } catch (NoSuchAlgorithmException e) {
                    // 不可能
                }
            }
        }
    }

}
