package com.wgs.picker.framework;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.LruCache;
import android.widget.ImageView;

import java.lang.ref.SoftReference;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by w.gs on 2015/7/17.
 */
public class LocalImageLoader {

    /**
     * 任务队列
     **/
    private final LinkedList<LoadImageTask> mTask = new LinkedList<>();

    /**
     * 内存缓存
     **/
    private LruCache<String, SoftReference<Bitmap>> mLruCache;

    /**
     * 线程池
     **/
    private ExecutorService mExecutorService;

    private static LocalImageLoader instance = new LocalImageLoader();

    private LocalImageLoader() {
        mExecutorService = Executors.newFixedThreadPool(1);
        // 获取应用程序最大可用内存
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory / 8;
        mLruCache = new LruCache<String, SoftReference<Bitmap>>(cacheSize) {
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight();
            }
        };
        mExecutor.start();
    }

    public static LocalImageLoader getInstance() {
        return instance;
    }

    private Thread mExecutor = new Thread() {
        public void run() {
            while (true) {
                while (mTask.size() > 0) {
                    LoadImageTask task = getTask();
                    ImageView view = task.view;
                    int width = view.getWidth();
                    int height = view.getHeight();
                    if (width == 0 || height == 0) {
                        //这里的大小设置仅适配该图片选择器 非通用设置
                        width = Density.getSceenWidth(view.getContext()) / 3;
                        height = width;
                    }
                    Bitmap bitmap = decodeSampledBitmapFromResource(task.url, width, height);
                    if (bitmap != null) {
                        mLruCache.put(task.url, new SoftReference<Bitmap>(bitmap));
                        task.onFinished(bitmap);
                    }
                }

                synchronized (this) {
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    };


    private LoadImageTask getTask() {
        //任务队列只保存最近30条任务  之前的任务丢弃
        while (mTask.size() > 30) {
            mTask.removeFirst();
        }
        return mTask.removeFirst();
    }

    public void displayImage(final String url, final ImageView view) {
        view.setTag(url);
        SoftReference<Bitmap> softReference = mLruCache.get(url);
        final Bitmap bitmap = softReference == null ? null : softReference.get();
        if (bitmap != null) {
            view.post(new Runnable() {
                public void run() {
                    if (view.getTag().equals(url))
                        view.setImageBitmap(bitmap);
                }
            });
        } else {

            LoadImageTask task = new LoadImageTask(url, view);
            if (!mTask.contains(task)) {
                mTask.add(task);
            }
            synchronized (mExecutor) {
                mExecutor.notify();
            }
        }
    }

    public void displayImage(final String url, final ImageView view, int default_img) {
        view.setImageResource(default_img);
        displayImage(url, view);
    }

    public void displaySingleImage(final String path, final ImageView view, final int width, final int height) {
        new Thread() {
            public void run() {
                final Bitmap bitmap = decodeSampledBitmapFromResource(path, width, height);
                view.post(new Runnable() {
                    public void run() {
                        view.setImageBitmap(bitmap);
                    }
                });
            }
        }.start();
    }

    class LoadImageTask {
        public LoadImageTask(String url, ImageView view) {
            this.url = url;
            this.view = view;
        }

        ImageView view;
        String url;

        public void onFinished(final Bitmap bitmap) {
            view.post(new Runnable() {
                public void run() {
                    if (url.equals(view.getTag())) {
                        view.setImageBitmap(bitmap);
                    }
                }
            });
        }

        public boolean equals(Object o) {
            LoadImageTask task = (LoadImageTask) o;
            return url.equals(task.url);
        }
    }


    /**
     * 根据计算的inSampleSize，得到压缩后图片
     **/
    private Bitmap decodeSampledBitmapFromResource(String pathName, int reqWidth, int reqHeight) {
        // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName, options);
        // 调用上面定义的方法计算inSampleSize值
        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight);
        // 使用获取到的inSampleSize值再次解析图片
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(pathName, options);

        return bitmap;
    }

    /**
     * 计算inSampleSize，用于压缩图片
     **/
    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // 源图片的宽度
        int width = options.outWidth;
        int height = options.outHeight;
        int inSampleSize = 1;

        if (width > reqWidth && height > reqHeight) {
            // 计算出实际宽度和目标宽度的比率
            int widthRatio = Math.round((float) width / (float) reqWidth);
            int heightRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = Math.max(widthRatio, heightRatio);
        }
        return inSampleSize;
    }


}
