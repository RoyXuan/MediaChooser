package com.vest.album.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.vest.album.R;
import com.vest.album.util.FileUtil;
import com.vest.album.util.SoundMeter;

import java.io.File;


//import android.support.v4.app.

/**
 * Created by Administrator on 2016/9/27.
 */
public class RecordFragment extends Fragment implements View.OnClickListener {


    private static final int NON = 0, START = 1;
    private static final int POLL_INTERVAL = 300;

    private String mFileName;
    private SoundMeter mSensor;
    private TextView timerTxt;

    private int flag = NON;
    private long startVoiceT, endVoiceT;
    private long timeTotalInS = 0;
    private long timeLeftInS = 0;
    private Handler mHandler = new Handler();

    // Container Activity must implement this interface
    public interface afterAudioListener {
        void onAudioSuccess(String path);

        void onAudioError(String message);
    }

    private afterAudioListener mCallback;

    public static RecordFragment newInstance() {
        return new RecordFragment();
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (afterAudioListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement afterAudioSelectedListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_make_audio, container, false);
        view.setOnClickListener(this);
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return false;
            }
        });
        timerTxt = (TextView) view.findViewById(R.id.media_make_audio_text);
        view.findViewById(R.id.media_make_audio_start_btn).setOnClickListener(this);
        view.findViewById(R.id.media_make_audio_stop_btn).setOnClickListener(this);
        view.findViewById(R.id.media_make_audio_back_btn).setOnClickListener(this);
//        addRipple(view, R.id.media_make_audio_start_btn, 5);
//        addRipple(view, R.id.media_make_audio_stop_btn, 5);
//        addRipple(view, R.id.media_make_audio_back_btn, 0);
        return view;
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mSensor = new SoundMeter();
    }

    public String getOutputMediaFile() {
        return "Audio_" + System.currentTimeMillis() + ".mp3";
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.media_make_audio_start_btn: {
                mFileName = getOutputMediaFile();
                startRecording();
                view.setVisibility(View.GONE);
            }
            break;
            case R.id.media_make_audio_stop_btn: {
                stopRecording();
            }
            break;
            case R.id.media_make_audio_back_btn: {
                getActivity().getSupportFragmentManager().beginTransaction().remove(RecordFragment.this).commit();
            }
            break;
        }
    }

    private void startRecording() {
        if (flag == NON) {
            if (!Environment.getExternalStorageDirectory().exists()) {
                ToastShow("SD卡不存在");
                return;
            }
            mHandler.postDelayed(new Runnable() {
                public void run() {
                }
            }, 300);
            startVoiceT = System.currentTimeMillis();
            start(mFileName);
            flag = START;
        }
    }

    private void stopRecording() {
        if (flag == START) {
            stop();
            timerTxt.setText("00:00:00");
            flag = NON;
            ToastShow("录音已保存");
            mCallback.onAudioSuccess(mFileName);
        } else {
            ToastShow("请先开始录音");
        }
    }

    private void initTimer(long total) {
        this.timeTotalInS = total;
        this.timeLeftInS = total;
        if (timeLeftInS <= 0) {
            ToastShow("录音时间到");
//            Toast.makeText(CustomSoundActivity.this, "录音时间到",
//                    Toast.LENGTH_SHORT).show();
            // 录音停止
            stop();
            return;
        }
        timeLeftInS--;
        refreshTimeLeft();
    }

    private void refreshTimeLeft() {
//        this.timedown.setText("录音时间剩余：" + timeLeftInS);
    }

    private Runnable mSleepTask = new Runnable() {
        public void run() {
            stop();
        }
    };
    private Runnable mPollTask = new Runnable() {
        public void run() {
            double amp = mSensor.getAmplitude();
//            updateDisplay(amp);
            endVoiceT = System.currentTimeMillis();
            int time = (int) ((endVoiceT - startVoiceT) / 1000);
            int hour = 0;
            int minute = 0;
            int second = 0;
            hour = time / 3600;
            minute = time % 3600 / 60;
            second = time % 3600 % 60;
            String showTimeStr = "";
            if (minute <= 9) {
                showTimeStr = "0" + hour;
            } else {
                showTimeStr = String.valueOf(hour);
            }

            if (minute <= 9) {
                showTimeStr = showTimeStr + ":0" + minute;
            } else {
                showTimeStr = showTimeStr + ":" + minute;
            }

            if (second <= 9) {
                showTimeStr = showTimeStr + ":0" + second;
            } else {
                showTimeStr = showTimeStr + ":" + second;
            }

            timerTxt.setText(showTimeStr);
            mHandler.postDelayed(mPollTask, POLL_INTERVAL);
        }
    };

//    private void updateDisplay(double signalEMA) {
//        switch ((int) signalEMA) {
//            case 0:
//            case 1:
//                volume.setImageResource(R.mipmap.amp1);
//                break;
//            case 2:
//            case 3:
//                volume.setImageResource(R.mipmap.amp2);
//                break;
//            case 4:
//            case 5:
//                volume.setImageResource(R.mipmap.amp3);
//                break;
//            case 6:
//            case 7:
//                volume.setImageResource(R.mipmap.amp4);
//                break;
//            case 8:
//            case 9:
//                volume.setImageResource(R.mipmap.amp5);
//                break;
//            case 10:
//            case 11:
//                volume.setImageResource(R.mipmap.amp6);
//                break;
//            default:
//                volume.setImageResource(R.mipmap.amp7);
//                break;
//        }
//    }

    private void start(String name) {
        name = FileUtil.getDiskCacheDir(getContext()) + File.separator + name;
        mSensor.start(name);
        mHandler.post(mPollTask);
    }

    private void stop() {
        mSensor.stop();
        mHandler.removeCallbacks(mSleepTask);
        mHandler.removeCallbacks(mPollTask);
    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        mSensor.stop();
        mHandler.removeCallbacks(mSleepTask);
        mHandler.removeCallbacks(mPollTask);
    }

    private void ToastShow(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }

}
