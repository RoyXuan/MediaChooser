package com.vest.album.fragment;

import android.app.Activity;
import android.content.ContentResolver;
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

/**
 * Created by Administrator on 2016/9/26.
 */
public class MediaAudioFragment extends BaseMediaFragment {
    private OnAudioSelectedListener mCallback;
    private ArrayList<String> mSelectedItems = new ArrayList<String>();
    private ArrayList<MediaModel> mGalleryModelList = new ArrayList<>();
    private MediaAdapter mVoiceAdapter;
    private RecyclerView mVoiceGridView;

    // Container Activity must implement this interface
    public interface OnAudioSelectedListener {
        void onAudioSelected(int count);
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnAudioSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnVoiceSelectedListener");
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_media, container, false);
        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mVoiceGridView = (RecyclerView) view.findViewById(R.id.fragment_media_list);
        mVoiceGridView.setLayoutManager(new LinearLayoutManager(getActivity()));
        initVoiceData();
    }

    public void setListener() {
        mVoiceAdapter.setOnItemClickListener(new MediaAdapter.onItemClickListener() {
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
                    mCallback.onAudioSelected(mSelectedItems.size());
                    Intent intent = new Intent();
                    intent.putStringArrayListExtra("list", mSelectedItems);
                    getActivity().setResult(Activity.RESULT_OK, intent);
                }
            }

            @Override
            public void onLongClick(MediaModel model) {
                File file = new File(model.url);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(file), "audio/*");
                startActivity(intent);
            }
        });
    }

    private void initVoiceData() {
        mGalleryModelList = new ArrayList<MediaModel>();
        Uri contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        ContentResolver contentResolver = getActivity().getContentResolver();
        String orderBy = MediaStore.Audio.Media.DATE_ADDED;
        String[] projection = {MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.DISPLAY_NAME};
        Cursor cursor = contentResolver.query(contentUri, projection, null, null, orderBy + " DESC");
        if (cursor == null) {
            Toast.makeText(getActivity(), getActivity().getString(R.string.no_media_file_available), Toast.LENGTH_SHORT).show();
        } else {
            if (cursor.getCount() > 0) {
                int urlCol = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
                int nameCol = cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME);
                for (int i = 0; i < cursor.getCount(); i++) {
                    cursor.moveToPosition(i);
                    String url = cursor.getString(urlCol);
                    String name = cursor.getString(nameCol);
                    if (!((MediaChooserActivity) getActivity()).isContainsUrl(url)) {
                        if (new File(url).exists())
                            mGalleryModelList.add(new MediaModel(url, false, 0, name));
                    }
                }
                setAdapter();
                setListener();
            }
        }
        cursor.close();
    }

    private void setAdapter() {
        if (mVoiceAdapter == null) {
            mVoiceAdapter = new MediaAdapter(getActivity(), mGalleryModelList, 0);
            mVoiceGridView.setLayoutManager(new LinearLayoutManager(getActivity()));
            mVoiceGridView.setAdapter(mVoiceAdapter);
        } else
            mVoiceAdapter.setData(mGalleryModelList);
    }

    public ArrayList<String> getSelectedAudioList() {
        return mSelectedItems;
    }


    public void addNewEntry(String url, String name) {
        if (mVoiceAdapter != null) {
            mVoiceAdapter.addLatestEntry(new MediaModel(url, false, 0, name));
        } else {
            if (mGalleryModelList == null) {
                mGalleryModelList = new ArrayList<MediaModel>();
            }
            mGalleryModelList.add(0, new MediaModel(url, false, 0, name));
            mVoiceAdapter = new MediaAdapter(getActivity(), mGalleryModelList, 0);
        }
    }
}
