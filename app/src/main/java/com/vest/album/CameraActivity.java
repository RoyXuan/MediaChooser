/*
 * Copyright 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.vest.album;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.Toast;

import com.vest.album.fragment.CameraBasicFragment;
import com.vest.album.fragment.CameraVideoFragment;
import com.vest.album.fragment.RecordFragment;
import com.vest.album.util.FileUtil;

import java.io.File;


public class CameraActivity extends AppCompatActivity implements
        CameraBasicFragment.onPhotoCallback,
        CameraVideoFragment.onResultCallback,
        RecordFragment.afterAudioListener {

    public static final String TYPE = "Type";
    public static final String PATH = "PATH";
    public static final int Photo_Code = 12;
    public static final int Video_Code = 13;
    public static final int Audio_Code = 14;


    int which;
    String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (savedInstanceState == null) {
            which = getIntent().getIntExtra(TYPE, -1);
            path = getIntent().getStringExtra(PATH);
        } else {
            which = savedInstanceState.getInt(TYPE);
            path = savedInstanceState.getString(PATH);
        }
        if (path == null || path.equals("")) {
            throw new IllegalArgumentException("CameraActivity PATH参数传入错误");
        }
        if (which == -1) {
            throw new IllegalArgumentException("CameraActivity which参数传入错误");
        }
        if (which == MediaChooserConstants.MEDIA_TYPE_VIDEO) {
            CameraVideoFragment f = CameraVideoFragment.newInstance(getVideoFilePath(path));
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, f)
                    .commit();
        } else if (which == MediaChooserConstants.MEDIA_TYPE_IMAGE) {
            CameraBasicFragment f = CameraBasicFragment.newInstance(getPhotoFilePath(path));
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, f)
                    .commit();
        } else if (which == MediaChooserConstants.MEDIA_TYPE_AUDIO) {
            RecordFragment fragment = RecordFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, fragment)
                    .commit();
        } else {
            throw new IllegalArgumentException("CameraActivity TYPE参数传入错误");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt(TYPE, which);
        savedInstanceState.putString(PATH, path);
    }

    public void onPause() {
        super.onPause();
        this.finish();
    }

    public String getVideoFilePath(String path) {
        return path + File.separator + "Video_" + System.currentTimeMillis() + ".mp4";
    }

    private String getPhotoFilePath(String path) {
        return path + File.separator + "Photo_" + System.currentTimeMillis() + ".jpg";
    }

    @Override
    public void onVideoError(String message) {
        if (message == null || message.equals("")) {
            if (!message.equals("1")) {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
            this.finish();
        }
    }

    @Override
    public void onVideoSuccess(String path) {
        scannerScanFile(path);
        CameraActivity.this.setResult(Video_Code, new Intent().putExtra(PATH, path));
        CameraActivity.this.finish();
    }

    @Override
    public void onPhotoError(String message) {
        if (message == null || message.equals("")) {
            if (!message.equals("1")) {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
            this.finish();
        }
    }

    @Override
    public void onPhotoSuccess(String path) {
        scannerScanFile(path);
        CameraActivity.this.setResult(Photo_Code, new Intent().putExtra(PATH, path));
        CameraActivity.this.finish();
    }

    @Override
    public void onAudioError(String message) {

    }

    @Override
    public void onAudioSuccess(String path) {
        final String fileUriString = FileUtil.getDiskCacheDir(this) + File.separator + path;
        scannerScanFile(fileUriString);
        CameraActivity.this.setResult(Audio_Code, new Intent().putExtra(PATH, fileUriString));
        CameraActivity.this.finish();
    }

    private void scannerScanFile(String url) {
        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(url))));
    }

}
