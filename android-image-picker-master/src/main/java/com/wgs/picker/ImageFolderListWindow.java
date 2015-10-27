package com.wgs.picker;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.wgs.picker.framework.ArrayListAdapter;
import com.wgs.picker.framework.BasePopupWindow;
import com.wgs.picker.framework.Density;
import com.wgs.picker.framework.LocalImageLoader;
import com.wgs.picker.framework.ViewHolder;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by w.gs on 2015/7/15.
 */
public class ImageFolderListWindow extends BasePopupWindow implements AdapterView.OnItemClickListener {

    private ListView mListView;

    private ArrayList<ImageFolder> mImageFolderList;

    private ImageFolderListAdapter mImageFolderListAdapter;

    private OnImageFolderSelectListener mOnImageFolderSelectListener;

    public ImageFolderListWindow(Context context) {
        super(context);
    }

    public void setDataList(ArrayList<ImageFolder> list){
        mImageFolderList = list;
        mImageFolderListAdapter.setList(mImageFolderList);
    }

    @Override
    protected void onCreate() {
        setContentView(R.layout.pw_imgdir_list, Density.getSceenWidth(mContext), (int)(Density.getSceenHeight(mContext)*0.7));
        mListView = (ListView) findViewById(R.id.lv_img_dir);
        mListView.setOnItemClickListener(this);

        mImageFolderListAdapter = new ImageFolderListAdapter((Activity) mContext);
        mListView.setAdapter(mImageFolderListAdapter);
        mImageFolderListAdapter.setSelectedItem(0);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mOnImageFolderSelectListener != null) {
            mImageFolderListAdapter.setSelectedItem(position);
            mOnImageFolderSelectListener.onSelected(mImageFolderList.get(position));
        }
    }

    public void setOnImageFolderSelectListener(OnImageFolderSelectListener listener) {
        mOnImageFolderSelectListener = listener;
    }

    public interface OnImageFolderSelectListener {
        public void onSelected(ImageFolder folder);
    }
}

class ImageFolderListAdapter extends ArrayListAdapter<ImageFolder> {

    public ImageFolderListAdapter(Activity context) {
        super(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.pw_imgdir_list_item, null);
        }
        ImageFolder imageFolder = mList.get(position);
        if (imageFolder != null) {
            ImageView iv_cover = ViewHolder.get(convertView, R.id.iv_cover);

            File coverFile = imageFolder.getCoverImage();
            if(coverFile!=null) {
                LocalImageLoader.getInstance().displayImage(coverFile.getAbsolutePath(), iv_cover);
            }

            ((TextView) ViewHolder.get(convertView, R.id.tv_name)).setText(imageFolder.getName());
           ((TextView) ViewHolder.get(convertView, R.id.tv_size)).setText(imageFolder.getImageCount()+"张");

            ImageView iv_check = ViewHolder.get(convertView,R.id.iv_check);

            if(selectedItem==position){
                iv_check.setVisibility(View.VISIBLE);
            }else{
                iv_check.setVisibility(View.GONE);
            }

        }

        return convertView;
    }
}
