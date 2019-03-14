package labelingStudy.nctu.minuku.service;

/**
 * Created by chiaenchiang on 29/11/2018.
 */

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.File;
import java.util.Date;

import labelingStudy.nctu.minuku.DBHelper.appDatabase;
import labelingStudy.nctu.minuku.R;
import labelingStudy.nctu.minuku.config.Constants;
import labelingStudy.nctu.minuku.model.DataRecord.VideoDataRecord;

import static labelingStudy.nctu.minuku.config.Constants.START_RECORDING;
import static labelingStudy.nctu.minuku.config.Constants.STOP_RECORDING;
import static labelingStudy.nctu.minuku.config.Constants.VIDEO_DIRECTORY_PATH;
import static labelingStudy.nctu.minuku.config.SharedVariables.getReadableTime;
import static labelingStudy.nctu.minuku.config.SharedVariables.ifRecordingRightNow;
import static labelingStudy.nctu.minuku.config.SharedVariables.relatedId;
import static labelingStudy.nctu.minuku.config.SharedVariables.videoCount;
import static labelingStudy.nctu.minuku.config.SharedVariables.videoFileName;
import static labelingStudy.nctu.minuku.config.SharedVariables.visitedApp;

public class BackgroundScreeenRecorderActivity extends Activity{
    private static final int REQUEST_CODE = 1;
    private MediaProjectionManager mMediaProjectionManager;
    private BackgroundScreenRecorderService mRecorder;
    private static final int RECORD_REQUEST_CODE = 1;
    private WindowManager mWindowManager;
    private View mOverlayView;
    VideoDataRecord videoDataRecord;
    appDatabase db;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mOverlayView = LayoutInflater.from(this).inflate(R.layout.transparent, null);
        IntentFilter filter = new IntentFilter();
        filter.addAction(START_RECORDING);
        filter.addAction(STOP_RECORDING);
        LocalBroadcastManager.getInstance(this).registerReceiver(Recording,filter);
        //noinspection ResourceType
        mMediaProjectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
        Intent captureIntent = mMediaProjectionManager.createScreenCaptureIntent();
        startActivityForResult(captureIntent, REQUEST_CODE);
        db = appDatabase.getDatabase(getApplicationContext());

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CODE) {
            MediaProjection mediaProjection = null;
            if (data != null)
                mediaProjection = mMediaProjectionManager.getMediaProjection(resultCode, (Intent) data.clone());
            if (mediaProjection == null) {
                Log.e("@@", "media projection is null");
                return;
            }
            // video size
            final int width = 1280;
            final int height = 720;
            videoFileName = Constants.DEVICE_ID + "-" + System.currentTimeMillis() + ".mp4";

            File root = new File(Environment.getExternalStorageDirectory() + VIDEO_DIRECTORY_PATH);
            if (!root.exists()) {
                root.mkdirs();
            }
            File file = new File(Environment.getExternalStorageDirectory() + VIDEO_DIRECTORY_PATH,
                    videoFileName);


            videoCount += 1;

            videoDataRecord = new VideoDataRecord(relatedId, videoFileName, visitedApp);
            videoDataRecord.setStartTime(getReadableTime(new Date().getTime()));


            final int bitrate = 600000;
            mRecorder = new BackgroundScreenRecorderService(width, height, bitrate, 1, mediaProjection, file.getAbsolutePath());
            mRecorder.start();
            ifRecordingRightNow = true;
            Toast toast = Toast.makeText(this, "Screen recorder is running...", Toast.LENGTH_SHORT);
            View view = toast.getView();
            view.getBackground().setColorFilter(Color.DKGRAY, PorterDuff.Mode.SRC_IN);
            toast.setView(view);
            toast.show();
            moveTaskToBack(true);
        }

    }
//
//    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//    @Override
//    public void onClick(View v) {
//        if (mRecorder != null) {
//            mRecorder.quit();
//            mRecorder = null;
//            mButton.setText("Restart recorder");
//            finish();
//        } else {
//            Intent captureIntent = mMediaProjectionManager.createScreenCaptureIntent();
//            startActivityForResult(captureIntent, REQUEST_CODE);
//        }
//    }
//    private final BroadcastReceiver stopRecording = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if (mRecorder != null) {
//                mRecorder.quit();
//                mRecorder = null;
////                mButton.setText("Restart recorder");
//                finish();
//            }
//        }
//    };

    // for recording
    private BroadcastReceiver Recording = new BroadcastReceiver() {

        @SuppressLint("LongLogTag")
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("checkBroadcast","in on receive");
            Log.d("checkBroadcast","intent :"+intent.getAction().toString());

            if(intent.getAction().equals(STOP_RECORDING)){
                Log.d("FloatingActionButtonService","stop in activty ");
                if (mRecorder != null) {
                    mRecorder.quit();
                    mRecorder = null;

                    // save to video data record
                    videoDataRecord.setEndTime(getReadableTime(new Date().getTime()));
                    db.videoDataRecordDao().insertAll(videoDataRecord);

                    Toast toast = Toast.makeText(getApplicationContext(), "Screen recorder stops", Toast.LENGTH_SHORT);
                    View view = toast.getView();
                    view.getBackground().setColorFilter(Color.DKGRAY, PorterDuff.Mode.SRC_IN);
                    toast.setView(view);
                    toast.show();
                    ifRecordingRightNow = false;
                    finish();

                }

            } else if(intent.getAction().equals(START_RECORDING)){
                mMediaProjectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
                Intent captureIntent = mMediaProjectionManager.createScreenCaptureIntent();
                startActivityForResult(captureIntent, RECORD_REQUEST_CODE);
                Log.d("checkBroadcast","receive start recording");
            }


        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(Recording);
//        if(mRecorder != null){
//            mRecorder.quit();
//            mRecorder = null;
//        }
//        if (mOverlayView != null)
//            mWindowManager.removeView(mOverlayView);
    }

}
