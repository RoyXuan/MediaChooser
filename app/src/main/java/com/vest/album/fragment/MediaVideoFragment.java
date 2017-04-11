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

public class MediaVideoFragment extends BaseMediaFragment {

    private MediaAdapter mVideoAdapter;
    private RecyclerView mVideoGridView;
    private ArrayList<String> mSelectedItems = new ArrayList<String>();
    private ArrayList<MediaModel> mGalleryModelList = new ArrayList<MediaModel>();
    private OnVideoSelectedListener mCallback;
    private Context context;

    // Container Activity must implement this interface
    public interface OnVideoSelectedListener {
        void onVideoSelected(int count);
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        context = activity;
        try {
            mCallback = (OnVideoSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnVideoSelectedListener");
        }
    }

    public MediaVideoFragment() {
        setRetainInstance(true);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_media, container, false);
        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mVideoGridView = (RecyclerView) view.findViewById(R.id.fragment_media_list);
        mVideoGridView.setLayoutManager(new LinearLayoutManager(getActivity()));
        setAdapter();
        setListener();
        initVideos();
    }

    public void setListener() {
        mVideoAdapter.setOnItemClickListener(new MediaAdapter.onItemClickListener() {
            @Override
            public void onClick(MediaModel model) {
                if (model.status) {
                    mSelectedItems.add(model.url);
                    MediaChooserConstants.SELECTED_MEDIA_COUNT++;
                } else {
                    mSelectedItems.remove(model.url.trim());
                    MediaChooserConstants.SELECTED_MEDIA_COUNT--;
                }
                if (mCallback != null) {
                    mCallback.onVideoSelected(mSelectedItems.size());
                    Intent intent = new Intent();
                    intent.putStringArrayListExtra("list", mSelectedItems);
                    getActivity().setResult(Activity.RESULT_OK, intent);
                }
            }

            @Override
            public void onLongClick(MediaModel model) {
                File file = new File(model.url);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(file), "video/*");
                startActivity(intent);
            }
        });
    }

//    private void checkData() {
//        LogUtil.Error(this, "checkData", "Video mGalleryModelList size:" + mGalleryModelList.size());
//        getAdapter().checkData();
//    }

//    public void notifyVideos() {
//        try {
//            final String orderBy = MediaStore.Video.Media.DATE_TAKEN;
//            //Here we set up a string array of the thumbnail ID column we want to get back
//            String[] proj = {MediaStore.Video.Media.DATA, MediaStore.Video.Media.DISPLAY_NAME};
//            Cursor mCursor = getActivity().getContentResolver().query(MEDIA_EXTERNAL_CONTENT_URI, proj, null, null, orderBy + " DESC");
//            int count = mCursor.getCount();
//            if (count > 0) {
//                mDataColumnIndex = mCursor.getColumnIndex(MEDIA_DATA);
//                //move position to first element
//                mCursor.moveToFirst();
//                mGalleryModelList = new ArrayList<MediaModel>();
//                for (int i = 0; i < count; i++) {
//                    mCursor.moveToPosition(i);
//                    String url = mCursor.getString(mDataColumnIndex);
//                    String name = mCursor.getString(mCursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));
//                    if (!((MediaChooserActivity) getActivity()).isContainsUrl(url)) {
//                        if (new File(url).exists())
//                            mGalleryModelList.add(new MediaModel(url, false, 1, name));
//                    }
//                }
//            } else {
//                Toast.makeText(getActivity(), getActivity().getString(R.string.no_media_file_available), Toast.LENGTH_SHORT).show();
//            }
//            mCursor.close();
//            mVideoAdapter.setData(mGalleryModelList);
//        } catch (Exception e) {
//            Log.e("init", "Exception:" + e.toString());
//            e.printStackTrace();
//        }
//    }

    private void initVideos() {
        final Uri MEDIA_EXTERNAL_CONTENT_URI = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        final String orderBy = MediaStore.Video.Media.DATE_TAKEN;
        String[] proj = {MediaStore.Video.Media.DATA, MediaStore.Video.Media.DISPLAY_NAME};
        Cursor mCursor = getActivity().getContentResolver().query(MEDIA_EXTERNAL_CONTENT_URI, proj, null, null, orderBy + " DESC");
        int count = mCursor.getCount();
        if (count > 0) {
            int mDataColumnIndex = mCursor.getColumnIndex(MediaStore.Video.Media.DATA);
            int mNameColumnIndex = mCursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME);
            mCursor.moveToFirst();
            mGalleryModelList = new ArrayList<MediaModel>();
            for (int i = 0; i < count; i++) {
                mCursor.moveToPosition(i);
                String url = mCursor.getString(mDataColumnIndex);
                String name = mCursor.getString(mNameColumnIndex);
                if (!((MediaChooserActivity) getActivity()).isContainsUrl(url)) {
                    if (new File(url).exists())
                        mGalleryModelList.add(new MediaModel(url, false, 1, name));
                }
            }
            mVideoAdapter.setData(mGalleryModelList);
        } else {
            Toast.makeText(getActivity(), getActivity().getString(R.string.no_media_file_available), Toast.LENGTH_SHORT).show();
        }
        mCursor.close();
    }

    private void setAdapter() {
        if (mVideoAdapter == null) {
            mVideoAdapter = new MediaAdapter(getActivity(), mGalleryModelList, 1);
            mVideoGridView.setAdapter(mVideoAdapter);
        } else
            mVideoAdapter.setData(mGalleryModelList);
    }


    public ArrayList<String> getSelectedVideoList() {
        return mSelectedItems;
    }


    public void addNewEntry(String url, String name) {
        if (mVideoAdapter != null) {
            mVideoAdapter.addLatestEntry(new MediaModel(url, false, 1, name));
        } else {
            if (mGalleryModelList == null) {
                mGalleryModelList = new ArrayList<MediaModel>();
            }
            mGalleryModelList.add(0, new MediaModel(url, false, 1, name));
            mVideoAdapter = new MediaAdapter(context, mGalleryModelList, 1);
        }
    }


}

