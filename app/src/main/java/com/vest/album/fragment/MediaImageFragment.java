
package com.vest.album.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.vest.album.MediaChooserActivity;
import com.vest.album.MediaChooserConstants;
import com.vest.album.MediaModel;
import com.vest.album.R;
import com.vest.album.adapter.MediaAdapter;
import com.vest.album.base.BaseMediaFragment;

import java.io.File;
import java.util.ArrayList;


public class MediaImageFragment extends BaseMediaFragment {
    private ArrayList<String> mSelectedItems = new ArrayList<String>();
    private ArrayList<MediaModel> mGalleryModelList = new ArrayList<MediaModel>();
    private RecyclerView mImageGridView;
    private OnImageSelectedListener mCallback;
    private MediaAdapter mImageAdapter;


    // Container Activity must implement this interface
    public interface OnImageSelectedListener {
        void onImageSelected(int count);
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnImageSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnImageSelectedListener");
        }
    }

    public MediaImageFragment() {
        setRetainInstance(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_media, container, false);
        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mImageGridView = (RecyclerView) view.findViewById(R.id.fragment_media_list);
        mImageGridView.setLayoutManager(new LinearLayoutManager(getActivity()));
        setAdapter();
        setListener();
        initPhoneImages();
    }

//    public void onResume() {
//        super.onResume();
//        MediaChooserActivity activity = (MediaChooserActivity) getActivity();
////        LogUtil.Info(this, "onResume", "imageRefresh:" + activity.imageRefresh);
//        if (activity.imageRefresh) {
//            addNewEntry(activity.url, new File(activity.url).getName());
//            activity.imageRefresh = false;
//        }
//    }

//    public void notifyPhoneImages() {
//        mGalleryModelList.clear();
//        final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
//        final String[] columns = {MediaStore.Images.Media.DATA, MediaStore.Images.Media.DISPLAY_NAME};
//        Cursor mImageCursor = getActivity().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, orderBy + " DESC");
//        if (mImageCursor.getCount() > 0) {
//            for (int i = 0; i < mImageCursor.getCount(); i++) {
//                mImageCursor.moveToPosition(i);
//                int dataColumnIndex = mImageCursor.getColumnIndex(MediaStore.Images.Media.DATA);
//                int nameCol = mImageCursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME);
//                String url = mImageCursor.getString(dataColumnIndex);
//                if (!((MediaChooserActivity) getActivity()).isContainsUrl(url)) {
//                    if (new File(url).exists()) {
//                        String name = mImageCursor.getString(nameCol);
//                        MediaModel galleryModel = new MediaModel(url, false, 2, name);
//                        mGalleryModelList.add(galleryModel);
//                    }
//                }
//            }
//            mImageAdapter.setData(mGalleryModelList);
//        } else {
//            Toast.makeText(getActivity(), getActivity().getString(R.string.no_media_file_available), Toast.LENGTH_SHORT).show();
//        }
//        mImageCursor.close();
//    }

    private void setListener() {
        mImageAdapter.setOnItemClickListener(new MediaAdapter.onItemClickListener() {
            @Override
            public void onClick(MediaModel galleryModel) {
                if (galleryModel.status) {
                    mSelectedItems.add(galleryModel.url);
                    MediaChooserConstants.SELECTED_MEDIA_COUNT++;
                } else {
                    mSelectedItems.remove(galleryModel.url.trim());
                    MediaChooserConstants.SELECTED_MEDIA_COUNT--;
                }
                if (mCallback != null) {
                    mCallback.onImageSelected(mSelectedItems.size());
                    Intent intent = new Intent();
                    intent.putStringArrayListExtra("list", mSelectedItems);
                    getActivity().setResult(Activity.RESULT_OK, intent);
                }
            }

            @Override
            public void onLongClick(MediaModel model) {
                File file = new File(model.url);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(file), "image/*");
                startActivity(intent);
//                ImageCheckActivity.goImageCheck(MediaImageFragment.this.getContext(), model.url);
            }
        });
    }

    private void initPhoneImages() {
        final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
        final String[] columns = {MediaStore.Images.Media.DATA, MediaStore.Images.Media.DISPLAY_NAME};
        Cursor mImageCursor = getActivity().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, orderBy + " DESC");
        if (mImageCursor.getCount() > 0) {
            mGalleryModelList = new ArrayList<MediaModel>();
            for (int i = 0; i < mImageCursor.getCount(); i++) {
                mImageCursor.moveToPosition(i);
                int dataColumnIndex = mImageCursor.getColumnIndex(MediaStore.Images.Media.DATA);
                int nameCol = mImageCursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME);
                String url = mImageCursor.getString(dataColumnIndex);
                if (!((MediaChooserActivity) getActivity()).isContainsUrl(url)) {
                    if (new File(url).exists()) {
                        String name = mImageCursor.getString(nameCol);
                        MediaModel galleryModel = new MediaModel(url, false, 2, name);
                        mGalleryModelList.add(galleryModel);
                    }
                }
            }
            mImageAdapter.setData(mGalleryModelList);
        } else {
            Toast.makeText(getActivity(), getActivity().getString(R.string.no_media_file_available), Toast.LENGTH_SHORT).show();
        }
        mImageCursor.close();
    }

//    private void checkData() {
//        LogUtil.Error(this, "checkData", "Image mGalleryModelList size:" + mGalleryModelList.size());
//        getAdapter().checkData();
//    }

    private void setAdapter() {
//        LogUtil.Info(this, "setAdapter", "Image checkData mGalleryModelList size:" + mGalleryModelList.size());
        if (mImageAdapter == null) {
            mImageAdapter = new MediaAdapter(getActivity(), mGalleryModelList, 2);
            mImageGridView.setLayoutManager(new LinearLayoutManager(getActivity()));
            mImageGridView.setAdapter(mImageAdapter);
        } else
            mImageAdapter.setData(mGalleryModelList);
//        checkData();
//        LogUtil.Info(this, "setAdapter", "checkData");
//        mImageAdapter.checkData();
    }

    public ArrayList<String> getSelectedImageList() {
        return mSelectedItems;
    }

    public MediaAdapter getAdapter() {
        return mImageAdapter;
    }

    public void addNewEntry(String url, String name) {
        if (mImageAdapter != null) {
            mImageAdapter.addLatestEntry(new MediaModel(url, false, 2, name));
        } else {
            if (mGalleryModelList == null) {
                mGalleryModelList = new ArrayList<MediaModel>();
            }
            mGalleryModelList.add(0, new MediaModel(url, false, 2, name));
            mImageAdapter = new MediaAdapter(getActivity(), mGalleryModelList, 2);
        }
    }
}
