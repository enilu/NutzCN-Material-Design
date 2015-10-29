package net.wendal.nutzbook.util;

import android.content.Context;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public final class DocumentUtils {

    private DocumentUtils() {}

    public static String getString(Context context, int docRawId) {
        InputStream ins = null;
        try {
            ins = context.getResources().openRawResource(docRawId);
            BufferedReader reader = new BufferedReader(new InputStreamReader(ins, "utf-8"));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            sb.deleteCharAt(sb.length() - 1);
            return sb.toString();
        } catch (IOException e) {
            return "文档读取失败。";
        } finally {
            try {
                if (ins != null)
                    ins.close();
            } catch (Exception e){}
        }
    }

    static public String readTextFile(InputStream inputStream) {
        //context.getAssets().open(tempate)
        //bubble = readTextFile(getAssets().open("bubble"));
        //bubble.replace("${webview_content}", mMaopaoObject.content);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte buf[] = new byte[1024];
        int len;
        try {
            while ((len = inputStream.read(buf)) != -1) {
                outputStream.write(buf, 0, len);
            }
            outputStream.close();
            inputStream.close();

        } catch (IOException e) {
           e.printStackTrace();
        }

        return outputStream.toString();
    }

}
