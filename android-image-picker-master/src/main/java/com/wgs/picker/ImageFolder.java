package com.wgs.picker;

import android.text.TextUtils;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by w.gs on 2015/7/15.
 */
public class ImageFolder {

    private String name; //文件夹名称

    private String path; //文件夹路径

    private File coverImage; //封面图片


    private ArrayList<File> imageList = new ArrayList<>(); //文件夹下的所有图片路径

    public ImageFolder(String path) {
        init(new File(path));
    }

    public ImageFolder(File file) {
        init(file);
    }

    private void init(File file) {

        path = file.getAbsolutePath();

        name = path.substring(path.lastIndexOf(File.separator)+1);

        //这种方式获取的图片没有按时间排序
//        String[] images = file.list(new FilenameFilter() {
//            @Override
//            public boolean accept(File dir, String filename) {
//                return filename.endsWith(".jpg") || filename.endsWith(".png") || filename.endsWith(".jpeg");
//            }
//        });
//        if (images != null) {
//            if (images.length > 0) {
//                coverImage = path + File.separator + images[0];
//
//                Log.v("--->>", "cover=" + coverImage);
//                //imageList = new ArrayList<> (Arrays.asList(images));
//                imageList.clear();
//                for (String str : images) {
//                    imageList.add(path.concat(File.separator).concat(str));
//                }
//            }

//        }
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public File getCoverImage() {

        return coverImage == null ? (imageList.size() > 0 ? imageList.get(0) : null) : coverImage;
    }

    public void setCoverImage(File file) {
        coverImage = file;
    }

    public ArrayList<File> getImageList() {
        return imageList;
    }

    public void setImageList(ArrayList<File> list) {
        if (list != null && list.size() > 0) {
            imageList.addAll(list);
        }
    }

    public void addImageFile(File file) {
        imageList.add(file);
    }

    public int getImageCount() {
        return imageList.size();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o instanceof ImageFolder) {
            ImageFolder folder = (ImageFolder) o;
            if (!TextUtils.isEmpty(folder.getPath())) {
                return folder.getPath().equals(this.getPath());
            }
        }

        return false;
    }
}
