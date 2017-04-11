package com.vest.album;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.vest.album.adapter.MediaFragmentViewPagerAdapter;
import com.vest.album.base.BaseMediaFragment;
import com.vest.album.fragment.MediaAudioFragment;
import com.vest.album.fragment.MediaImageFragment;
import com.vest.album.fragment.MediaVideoFragment;
import com.vest.album.util.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/4/6.
 */
public class MediaChooserActivity extends AppCompatActivity implements View.OnClickListener,
        MediaImageFragment.OnImageSelectedListener,
        MediaVideoFragment.OnVideoSelectedListener,
        MediaAudioFragment.OnAudioSelectedListener {

    private ImageView mMakeMedia;
    private ImageView mBack;
    private TextView mDone;
    public TabLayout tabLayout;
    public ViewPager viewPager;
    private MediaFragmentViewPagerAdapter mViewPagerAdapter;
    public ArrayList<String> urls;
    public boolean imageRefresh = false, videoRefresh = false, audioRefresh = false;
    public String url;

    public static void startMediaChooser(Context context, ArrayList<String> data) {
        Intent intent = new Intent(context, MediaChooserActivity.class);
        Bundle bundle = new Bundle();
        bundle.putStringArrayList(MediaChooserConstants.SELECTED_MEDIA_LIST, data);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_chooser);
        if (savedInstanceState == null) {
            urls = getIntent().getExtras().getStringArrayList(MediaChooserConstants.SELECTED_MEDIA_LIST);
        } else {
            urls = savedInstanceState.getStringArrayList(MediaChooserConstants.SELECTED_MEDIA_LIST);
        }
        if (urls == null || urls.isEmpty()) {
            urls = new ArrayList<String>();
        }
        initView();
        setViewListener();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putStringArrayList(MediaChooserConstants.SELECTED_MEDIA_LIST, urls);
    }

    private void initView() {
        tabLayout = (TabLayout) findViewById(R.id.tabs_MediaChooser);
        viewPager = (ViewPager) findViewById(R.id.viewpager_MediaChooser);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mMakeMedia = (ImageView) toolbar.findViewById(R.id.media_chooser_camera_btn);
        mBack = (ImageView) toolbar.findViewById(R.id.media_chooser_back_btn);
        mDone = (TextView) toolbar.findViewById(R.id.media_chooser_done_btn);
        setSupportActionBar(toolbar);
        mBack.setOnClickListener(this);
        mMakeMedia.setOnClickListener(this);
        mDone.setOnClickListener(this);
        mMakeMedia.setTag(getResources().getString(R.string.audio));
        mMakeMedia.setImageResource(R.drawable.selector_audio_button);
        List<BaseMediaFragment> fragments = new ArrayList<BaseMediaFragment>();
        fragments.add(new MediaAudioFragment());
        fragments.add(new MediaVideoFragment());
        fragments.add(new MediaImageFragment());
        mViewPagerAdapter = new MediaFragmentViewPagerAdapter(getSupportFragmentManager(), viewPager, fragments);
        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(viewPager);
            }
        });
    }

    private void setViewListener() {
        mViewPagerAdapter.setOnExtraPageChangeListener(new MediaFragmentViewPagerAdapter.OnExtraPageChangeListener() {
            @Override
            public void onExtraPageSelected(int position) {
                Fragment fragment = mViewPagerAdapter.getItem(position);
                if (fragment instanceof MediaImageFragment) {
                    MediaImageFragment mediaImageFragment = (MediaImageFragment) fragment;
                    if (mediaImageFragment != null) {
                        mMakeMedia.setImageDrawable(ContextCompat.getDrawable(MediaChooserActivity.this,
                                R.drawable.selector_camera_button));
                        mMakeMedia.setTag(getResources().getString(R.string.image));
                    }
                } else if (fragment instanceof MediaVideoFragment) {
                    MediaVideoFragment mediaVideoFragment = (MediaVideoFragment) fragment;
                    if (mediaVideoFragment != null) {
                        mMakeMedia.setImageDrawable(ContextCompat.getDrawable(MediaChooserActivity.this,
                                R.drawable.selector_video_button));
                        mMakeMedia.setTag(getResources().getString(R.string.video));
                    }
                } else if (fragment instanceof MediaAudioFragment) {
                    MediaAudioFragment mediaAudioFragment = (MediaAudioFragment) fragment;
                    if (mediaAudioFragment != null) {
                        mMakeMedia.setImageResource(R.drawable.selector_audio_button);
                        mMakeMedia.setTag(getResources().getString(R.string.audio));
                    }
                }
            }
        });
    }

    public boolean isContainsUrl(String url) {
        if (urls.contains(url)) {
            urls.remove(url);
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (url != null && !url.equals("")) {
            if (audioRefresh) {
                Fragment f = mViewPagerAdapter.getItem(MediaAudioFragment.class.getSimpleName());
                BaseMediaFragment fragment = (BaseMediaFragment) f;
                fragment.addNewEntry(url, new File(url).getName());
                audioRefresh = false;
            }
            if (videoRefresh) {
                Fragment f = mViewPagerAdapter.getItem(MediaVideoFragment.class.getSimpleName());
                BaseMediaFragment fragment = (BaseMediaFragment) f;
                fragment.addNewEntry(url, new File(url).getName());
                videoRefresh = false;
            }
            if (imageRefresh) {
                Fragment f = mViewPagerAdapter.getItem(MediaImageFragment.class.getSimpleName());
                BaseMediaFragment fragment = (BaseMediaFragment) f;
                fragment.addNewEntry(url, new File(url).getName());
                imageRefresh = false;
            }
        }
    }

    @Override
    public void onImageSelected(int count) {
        if (tabLayout.getTabAt(1) != null) {
            if (count != 0) {
                tabLayout.getTabAt(2).setText(getResources().getString(R.string.image) + "  " + count);
            } else {
                tabLayout.getTabAt(2).setText(getResources().getString(R.string.image));
            }
        }
    }

    @Override
    public void onVideoSelected(int count) {
        if (count != 0) {
            tabLayout.getTabAt(1).setText(getResources().getString(R.string.video) + "  " + count);

        } else {
            tabLayout.getTabAt(1).setText(getResources().getString(R.string.video));
        }
    }

    @Override
    public void onAudioSelected(int count) {
        if (count != 0) {
            tabLayout.getTabAt(0).setText(getResources().getString(R.string.audio) + "  " + count);
        } else {
            tabLayout.getTabAt(0).setText(getResources().getString(R.string.audio));
        }
    }


    private void toCamera(int type) {
        if (type == MediaChooserConstants.MEDIA_TYPE_VIDEO) {
            Intent starter = new Intent();
            starter.setClass(this, CameraActivity.class);
            starter.putExtra(CameraActivity.TYPE, type);
            starter.putExtra(CameraActivity.PATH, FileUtil.getDiskCacheDir(this));
            this.startActivityForResult(starter, CameraActivity.Video_Code);
        }
        if (type == MediaChooserConstants.MEDIA_TYPE_IMAGE) {
            Intent starter = new Intent();
            starter.setClass(this, CameraActivity.class);
            starter.putExtra(CameraActivity.TYPE, type);
            starter.putExtra(CameraActivity.PATH, FileUtil.getDiskCacheDir(this));
            this.startActivityForResult(starter, CameraActivity.Photo_Code);
        }
        if (type == MediaChooserConstants.MEDIA_TYPE_AUDIO) {
            Intent starter = new Intent();
            starter.setClass(this, CameraActivity.class);
            starter.putExtra(CameraActivity.TYPE, type);
            starter.putExtra(CameraActivity.PATH, FileUtil.getDiskCacheDir(this));
            this.startActivityForResult(starter, CameraActivity.Audio_Code);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        if (requestCode == CameraActivity.Photo_Code && resultCode == CameraActivity.Photo_Code) {
            imageRefresh = true;
            url = data.getStringExtra(CameraActivity.PATH);
        }
        if (requestCode == CameraActivity.Video_Code && resultCode == CameraActivity.Video_Code) {
            videoRefresh = true;
            url = data.getStringExtra(CameraActivity.PATH);
        }
        if (requestCode == CameraActivity.Audio_Code && resultCode == CameraActivity.Audio_Code) {
            audioRefresh = true;
            url = data.getStringExtra(CameraActivity.PATH);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

//    private ArrayList<String> evidences;

    private void sendResult() {
        ArrayList<String> files = new ArrayList<String>();
        int count = mViewPagerAdapter.getCount();
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                Fragment fragment = mViewPagerAdapter.getItem(i);
                if (fragment instanceof MediaImageFragment) {
                    MediaImageFragment mediaImageFragment = (MediaImageFragment) fragment;
                    if (mediaImageFragment != null) {
                        if (mediaImageFragment.getSelectedImageList() != null && mediaImageFragment.getSelectedImageList().size() > 0) {
                            files.addAll(mediaImageFragment.getSelectedImageList());
                            // addEvidence(1, );
                        }
                    }
                } else if (fragment instanceof MediaVideoFragment) {
                    MediaVideoFragment mediaVideoFragment = (MediaVideoFragment) fragment;
                    if (mediaVideoFragment != null) {
                        if (mediaVideoFragment.getSelectedVideoList() != null && mediaVideoFragment.getSelectedVideoList().size() > 0) {
                            files.addAll(mediaVideoFragment.getSelectedVideoList());
                            // addEvidence(3, mediaVideoFragment.getSelectedVideoList());
                        }
                    }
                } else if (fragment instanceof MediaAudioFragment) {
                    MediaAudioFragment mediaAudioFragment = (MediaAudioFragment) fragment;
                    if (mediaAudioFragment != null) {
                        if (mediaAudioFragment.getSelectedAudioList() != null && mediaAudioFragment.getSelectedAudioList().size() > 0) {
                            files.addAll(mediaAudioFragment.getSelectedAudioList());
                            //addEvidence(2, mediaAudioFragment.getSelectedAudioList());
                        }
                    }
                }
            }
            if (files == null || files.isEmpty()) {
                showTips(getString(R.string.please_select_file), Toast.LENGTH_LONG);
                return;
            } else {
                Intent intent = new Intent();
                intent.setAction(MediaChooserConstants.ACTION_MEDIA);
                Bundle bundle = new Bundle();
                bundle.putStringArrayList(MediaChooserConstants.RESULT_LIST, files);
                intent.putExtras(bundle);
                sendBroadcast(intent);
            }
            finish();
        }
    }

    public void showTips(String tips, int length) {
        Toast.makeText(this, tips, length).show();
    }


    @Override
    public void onClick(View view) {
        if (view == mMakeMedia) {
            if (view.getTag() != null) {
                if (view.getTag().toString().equals(getResources().getString(R.string.video))) {
                    toCamera(MediaChooserConstants.MEDIA_TYPE_VIDEO);
                } else if (view.getTag().toString().equals(getResources().getString(R.string.image))) {
                    toCamera(MediaChooserConstants.MEDIA_TYPE_IMAGE);
                } else if (view.getTag().toString().equals(getResources().getString(R.string.audio))) {
                    toCamera(MediaChooserConstants.MEDIA_TYPE_AUDIO);
                }
            }
        } else if (view == mDone) {
            sendResult();
        } else if (view == mBack) {
            finish();
        }
    }
}
