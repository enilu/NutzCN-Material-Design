package com.wgs.picker;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.wgs.picker.framework.ArrayListAdapter;
import com.wgs.picker.framework.DateTimeUtil;
import com.wgs.picker.framework.Density;
import com.wgs.picker.framework.ImagePicker;
import com.wgs.picker.framework.LocalImageLoader;
import com.wgs.picker.framework.ViewHolder;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * Created by w.gs on 2015/7/15.
 */
public class ImagePickerActivity extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private ProgressDialog mProgressDialog;

    private GridView gv_image;
    private TextView tv_image_folder;
    private TextView tv_count;
    private Button btn_ok;
    private View mBottomBar;

    //图片文件夹列表界面
    private ImageFolderListWindow mImageFolderListWindow;

    private ImageListAdapter mImageListAdapter;

    //图片文件夹数据
    private ArrayList<ImageFolder> mImageFolderList = new ArrayList<>();

    //保存用户选中的图片
    private ArrayList<String> mCheckedImageList = new ArrayList<>();

    private ImageFolder mCurrentImageFolder;

    ImagePicker mPickOptions;

    private Uri mCurrentUri;

    private Handler mHanlder = new Handler() {
        public void handleMessage(Message msg) {
            if (mImageFolderList.size() > 0) {
                mImageFolderListWindow.setDataList(mImageFolderList);
                refreshView(mImageFolderList.get(0));
            } else {
                Toast.makeText(ImagePickerActivity.this, "未找到图片", Toast.LENGTH_SHORT).show();
            }
            mProgressDialog.dismiss();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_picker);

        //得到options
        mPickOptions = (EventBus.getDefault().getStickyEvent(ImagePicker.OnPublishPickOptionsEvent.class)).options;

        mImageFolderList = getAllImageFolder();

        gv_image = (GridView) findViewById(R.id.gv_image);
        mImageListAdapter = new ImageListAdapter(this);
        gv_image.setAdapter(mImageListAdapter);

        tv_count = (TextView) findViewById(R.id.tv_count);
        tv_image_folder = (TextView) findViewById(R.id.tv_image_folder);
        btn_ok = (Button) findViewById(R.id.btn_ok);

        mBottomBar = findViewById(R.id.fl_bottom_wrapper);
        mImageFolderListWindow = new ImageFolderListWindow(this);

        mImageFolderListWindow.setOnImageFolderSelectListener(new ImageFolderListWindow.OnImageFolderSelectListener() {
            @Override
            public void onSelected(ImageFolder folder) {
                refreshView(folder);
                mImageFolderListWindow.dismiss();
            }
        });
        tv_image_folder.setOnClickListener(this);

        gv_image.setOnItemClickListener(this);

        btn_ok.setOnClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        File file = (File) parent.getAdapter().getItem(position);
        if (file.getAbsolutePath().endsWith("paizhao")) {
            //是否超过最大允许上传的数量(max == 0 代表无限制)
            if(mPickOptions.max > 0 && mCheckedImageList.size() >= mPickOptions.max){
                Toast.makeText(mPickOptions.context, "最多选择" + mPickOptions.max + "张图片", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, getOutPutUriIfTakePicture());
            startActivityForResult(intent, 1);
        } else {
            Intent intent = new Intent(this, ImagePreview.class);
            intent.putExtra("img_file", file);
            startActivity(intent);
        }
    }

    private Uri getOutPutUriIfTakePicture() {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString()
                + File.separator + System.currentTimeMillis() + ".jpg");
        Uri uri = Uri.fromFile(file);
        mCurrentUri = uri;

        return uri;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            mCheckedImageList.add(mCurrentUri.getPath());
            MediaScannerConnection.scanFile(this,
                    new String[]{mCurrentUri.getPath()},
                    null, null);
            // TODO: 15/10/27 替换为listener
            //Intent intent = new Intent();
            //intent.putExtra("data", mCheckedImageList);
            //setResult(RESULT_OK, intent);
            mPickOptions.pickListener.onPickedSuccessfully(mCheckedImageList);
            ImagePickerActivity.this.finish();
        }
    }

    private void refreshView(ImageFolder folder) {
        mCurrentImageFolder = folder;
        mImageListAdapter.setList(folder.getImageList());
        tv_count.setText(folder.getImageCount() + "张");
        tv_image_folder.setText(folder.getName());
    }

    @Override
    public void onClick(View v) {
        if (v == tv_image_folder) {
            mImageFolderListWindow.showAsDropDown(mBottomBar, 0, 0);
        } else if (v == btn_ok) {
            mPickOptions.pickListener.onPickedSuccessfully(mCheckedImageList);
            finish();
        }
    }

    private ArrayList<ImageFolder> getAllImageFolder() {
        final ArrayList<ImageFolder> imageFolderList = new ArrayList<>();
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, "无外部存储", Toast.LENGTH_SHORT).show();
            return null;
        }
        mProgressDialog = ProgressDialog.show(this, null, "正在加载...");
        new Thread(new Runnable() {

            ArrayList<String> tempDirList = new ArrayList<String>();
            private HashMap<String, ImageFolder> mImageFolderMap = new HashMap<>();

            //将同一文件夹下的图片归并
            private void addImageToFolder(File image_file) {
                File parent_file = image_file.getParentFile();
                if (parent_file == null) {
                    return;
                }
                String parent_path = parent_file.getAbsolutePath();
                ImageFolder image_folder = mImageFolderMap.get(parent_path);
                if (image_folder == null) {
                    ImageFolder folder = new ImageFolder(parent_file);
                    folder.addImageFile(image_file);
                    mImageFolderMap.put(parent_path, folder);
                } else {
                    image_folder.addImageFile(image_file);
                }
            }

            public void run() {
                Uri imgage_uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                ContentResolver contentResolver = ImagePickerActivity.this.getContentResolver();

                //只查询JPEG和PNG格式的图片 并按图片修改时间降序排序
                Cursor cursor = contentResolver.query(imgage_uri, null, MediaStore.Images.Media.MIME_TYPE
                                + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=?",
                        new String[]{"image/jpeg", "image/png"}, MediaStore.Images.Media.DATE_MODIFIED + " DESC");

                ArrayList<File> allImageList = new ArrayList<>();
                while (cursor.moveToNext()) {
                    String image_path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                    File image_file = new File(image_path);

                    //忽略小于10k的图片
                    if (image_file.length() < 1024 * 10) {
                        continue;
                    }

                    allImageList.add(image_file);
                    addImageToFolder(image_file);

                }
                //拍照
                File paizhao = new File("paizhao");
                allImageList.add(0, paizhao);
                if (allImageList.size() > 1) {
                    paizhao.setLastModified(allImageList.get(1).lastModified());
                    //增加 所有图片 文件夹 逻辑增加 不存在物理存储
                    ImageFolder folder_all = new ImageFolder(File.separator + "所有图片");
                    folder_all.setImageList(allImageList);
                    folder_all.setCoverImage(allImageList.get(1));
                    imageFolderList.add(0, folder_all);

                    Iterator it = mImageFolderMap.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry<String, ImageFolder> entry = (Map.Entry) it.next();
                        imageFolderList.add(entry.getValue());
                    }
                }
                //释放内存
                cursor.close();
                tempDirList = null;
                mImageFolderMap = null;
                mHanlder.sendEmptyMessage(0);
            }
        }).start();

        return imageFolderList;
    }

    private void refreshCheckedImage() {
        btn_ok.setText(String.format("完成(%d)", mCheckedImageList.size()));
    }

    class ImageListAdapter extends ArrayListAdapter<File> {


        public ImageListAdapter(Activity context) {
            super(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            File file = mList.get(position);
            final String image_path = file.getAbsolutePath();

            //所有图片第一项是拍照
            if (image_path.endsWith("paizhao")) {
                View view = LayoutInflater.from(mContext).inflate(R.layout.activity_image_picker_item_paizhao, null);
                view.setLayoutParams(new AbsListView.LayoutParams(Density.getSceenWidth(mContext) / 3, Density.getSceenWidth(mContext) / 3));
                view.setId(R.id.picker_image_item_paizhao);
                return view;
            }

            if (convertView == null || convertView.getId() != R.id.picker_image_item) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.activity_image_picker_item, null);
                convertView.setId(R.id.picker_image_item);
            }

            ImageView iv_item = ViewHolder.get(convertView, R.id.iv_item_img);
            iv_item.setLayoutParams(new FrameLayout.LayoutParams(Density.getSceenWidth(mContext) / 3, Density.getSceenWidth(mContext) / 3));
            iv_item.setImageResource(R.drawable.img_default);
            final ImageButton ibtn_check = ViewHolder.get(convertView, R.id.ibtn_check);
            TextView tv_date_time = ViewHolder.get(convertView, R.id.tv_date_time);


            //ImageLoader.getInstance().loadImage(image_path, iv_item);
            LocalImageLoader.getInstance().displayImage(image_path, iv_item);
            tv_date_time.setText(DateTimeUtil.formatDate(file.lastModified()));
            ibtn_check.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (mCheckedImageList.contains(image_path)) {
                        mCheckedImageList.remove(image_path);
                        ibtn_check.setImageResource(R.mipmap.img_check);
                    } else {
                        //是否超过最大允许上传的数量(max == 0 代表无限制)
                        if(mPickOptions.max > 0 && mCheckedImageList.size() >= mPickOptions.max){
                            Toast.makeText(mPickOptions.context, "最多选择" + mPickOptions.max + "张图片", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        mCheckedImageList.add(image_path);
                        ibtn_check.setImageResource(R.mipmap.img_checked);
                    }
                    refreshCheckedImage();
                }
            });

            if (mCheckedImageList.contains(image_path)) {
                ibtn_check.setImageResource(R.mipmap.img_checked);
            } else {
                ibtn_check.setImageResource(R.mipmap.img_check);
            }

            return convertView;
        }


    }

}

