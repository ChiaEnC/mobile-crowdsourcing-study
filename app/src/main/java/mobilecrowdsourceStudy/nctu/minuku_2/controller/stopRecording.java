package mobilecrowdsourceStudy.nctu.minuku_2.controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import mobilecrowdsourceStudy.nctu.R;

import static labelingStudy.nctu.minuku.config.Constants.STOP_RECORDING;
import static labelingStudy.nctu.minuku.config.SharedVariables.ifRecordingRightNow;

/**
 * Created by chiaenchiang on 09/12/2018.
 */

public class stopRecording extends AppCompatActivity{
    ImageView stopRecording;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stop_recording);
        stopRecording = (ImageView) findViewById(R.id.stop_record);
        stopRecording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopRecording.setClickable(false);
                stopRecording();
            }
        });

    }
    public void stopRecording(){

        if(ifRecordingRightNow) {
            Intent broadCastIntent = new Intent();
            broadCastIntent.setAction(STOP_RECORDING);
//        sendBroadcast(broadCastIntent);
            Log.d("checkBroadcast", "stopRecording");
            Toast.makeText(this, "stopRecording", Toast.LENGTH_LONG);
            LocalBroadcastManager.getInstance(this).sendBroadcast(broadCastIntent);
        }else{
            Toast.makeText(this, "已經停止錄製", Toast.LENGTH_LONG);
        }
    }
}
