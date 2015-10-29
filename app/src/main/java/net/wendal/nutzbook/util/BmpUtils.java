package net.wendal.nutzbook.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class BmpUtils {

    //临时文件夹
    public static String SD_TEMP_PATH = Environment.getExternalStorageDirectory()
            + "/NutzCn/temp/";

    //创建临时目录
    public static String createTempDir() {
        File file = new File(SD_TEMP_PATH);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file.getAbsolutePath();
    }

    public static void deleteTempDir(){
        deleteDirectory(SD_TEMP_PATH);
    }

    public static boolean deleteFile(String sPath) {
        boolean flag = false;
        File file = new File(sPath);
        // 路径为文件且不为空则进行删除
        if (file.isFile() && file.exists()) {
            file.delete();
            flag = true;
        }
        return flag;
    }

    //清空临时目录
    public static boolean deleteDirectory(String sPath) {
        boolean flag = false;
        //如果sPath不以文件分隔符结尾，自动添加文件分隔符
        if (!sPath.endsWith(File.separator)) {
            sPath = sPath + File.separator;
        }
        File dirFile = new File(sPath);
        //如果dir对应的文件不存在，或者不是一个目录，则退出
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        flag = true;
        //删除文件夹下的所有文件(包括子目录)
        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            //删除子文件
            if (files[i].isFile()) {
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag) break;
            } //删除子目录
            else {
                flag = deleteDirectory(files[i].getAbsolutePath());
                if (!flag) break;
            }
        }
        if (!flag) return false;
        //删除当前目录
        return dirFile.delete();
    }


    //保存图片到临时文件夹
    public static String revisionImageSize(String path) {
        createTempDir();

        File file = new File(path);
        BufferedInputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(file));

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(in, null, options);
            in.close();
            in = null;

            int i = 0;
            Bitmap bitmap;
            while (true) {
                if ((options.outWidth >> i <= 1500) && (options.outHeight >> i <= 1500)) {
                    in = new BufferedInputStream(new FileInputStream(new File(path)));
                    options.inSampleSize = (int) Math.pow(2.0D, i);
                    options.inJustDecodeBounds = false;
                    bitmap = BitmapFactory.decodeStream(in, null, options);
                    in.close();
                    in = null;
                    break;
                }
                i += 1;
            }

            String filePath = SD_TEMP_PATH + file.getName();
            if (saveBitmap(bitmap, filePath)) {
                return filePath;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(in != null){
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static boolean saveBitmap(Bitmap bmp, String filePath) {
        File f = new File(filePath);
        try {
            if (f.exists()) {
                f.delete();
            } else {
                f.createNewFile();
            }
            FileOutputStream out = new FileOutputStream(f);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

    }

}
