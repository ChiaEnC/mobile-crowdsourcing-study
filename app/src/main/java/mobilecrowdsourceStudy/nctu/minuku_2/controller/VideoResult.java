package mobilecrowdsourceStudy.nctu.minuku_2.controller;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import labelingStudy.nctu.minuku.DBHelper.appDatabase;
import labelingStudy.nctu.minuku.config.Constants;
import mobilecrowdsourceStudy.nctu.R;
import mobilecrowdsourceStudy.nctu.minuku_2.MainActivity;
import mobilecrowdsourceStudy.nctu.minuku_2.service.UserClient;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static labelingStudy.nctu.minuku.config.Constants.URL_SAVE_VIDEO;
import static labelingStudy.nctu.minuku.config.Constants.VIDEO_DIRECTORY_PATH;
import static labelingStudy.nctu.minuku.config.SharedVariables.videoFileName;

/**
 * Created by chiaenchiang on 07/12/2018.
 */

public class VideoResult extends AppCompatActivity {
    String SrcPath="";
    public String TAG =  "VideoResult";
    Button saveButton;
    appDatabase db;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        SrcPath = Environment.getExternalStorageDirectory().getPath()+VIDEO_DIRECTORY_PATH;
        SrcPath = SrcPath+"/"+videoFileName;
        db = appDatabase.getDatabase(getApplicationContext());
        Log.d(TAG, "path: "+SrcPath);
        setContentView(R.layout.video_view);
        VideoView myVideoView = (VideoView)findViewById(R.id.myvideoview);
        File file = new File(SrcPath);
        if(file.exists()){
            try {

                Uri path=Uri.fromFile(new File(SrcPath));

                myVideoView.setVideoURI(path);
                Log.d(TAG, "setVideoURI:  "+path.toString());
                myVideoView.setMediaController(new MediaController(this));
                myVideoView.requestFocus();
            }catch(Exception e){
                Log.d(TAG,"exception");
                e.printStackTrace();
            }
            myVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                // Close the progress bar and play the video
                public void onPrepared(MediaPlayer mp) {

                    myVideoView.start();
                }
            });

            saveButton = findViewById(R.id.save_button);
            saveButton.setClickable(true);
            saveButton.setBackgroundColor(getColor(R.color.brown));
            saveButton.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                public void onClick(View v) {
                    // TODO Auto-generated method stub

                    // get unsync video data
                    Cursor transCursor = null;
                    transCursor = db.videoDataRecordDao().getUnsyncedData(0);
                    Long creationTime = Long.valueOf(0);
                    int allRows = transCursor.getCount();
                    Log.d(TAG,"rows");
                    if (allRows != 0) {
                        transCursor.moveToFirst();
                        for (int i = 0; i < allRows; i++) {
                            JSONObject jsonObject = new JSONObject();
                            creationTime = transCursor.getLong(1);
                            SrcPath = Environment.getExternalStorageDirectory().getPath()+VIDEO_DIRECTORY_PATH;
                            SrcPath = SrcPath + "/" + transCursor.getString(3);
                            int relatedId = transCursor.getInt(2);
                            String startTime = transCursor.getString(4);
                            String endTime = transCursor.getString(5);
                            String app = transCursor.getString(6);


                            try {
                                jsonObject.put("DeviceID", Constants.DEVICE_ID);
                                jsonObject.put("creationTime", creationTime);
                                jsonObject.put("relatedId", relatedId);
                                jsonObject.put("startTime", startTime);
                                jsonObject.put("endTime", endTime);
                                jsonObject.put("app", app);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                            Log.d("video", SrcPath.toString());
                            File videoFile = new File(SrcPath);
//                            uploadVideo(path,jsonObject.toString());
//                            MediaScannerConnection.scanFile(VideoResult.this, new String[] { videoFile.getAbsolutePath() }, null,
//                                    (path, uri) -> uploadVideo(uri,jsonObject.toString())
//                            );

                            MediaScannerConnection.scanFile(VideoResult.this,
                                    new String[] { videoFile.getAbsolutePath() }, null,
                                    new MediaScannerConnection.OnScanCompletedListener() {
                                        public void onScanCompleted(String path, Uri uri) {
                                            Log.d(TAG,"MediaScannerConnection : "+uri);
                                            uploadVideo(uri,jsonObject.toString());
                                        }
                                    });

                            transCursor.moveToNext();
                        }
                    }

//                      Log.d("video","uri : "+ video_uri[0].toString());
                    // uploadVideo(video_uri[0]);
                    saveButton.setBackgroundColor(getColor(R.color.lightbrown));
                    saveButton.setClickable(false);
                }

            });
        }else{
            buildNoVideoAlertDialog().show();

        }
    }
    private AlertDialog buildNoVideoAlertDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("您無法觀看影片");
        alertDialogBuilder.setMessage("尚未錄製影片");
        alertDialogBuilder.setPositiveButton("ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent goBackToMain = new Intent(VideoResult.this, MainActivity.class);
                        startActivity(goBackToMain);
                    }
                });
        return (alertDialogBuilder.create());
    }



    public void uploadVideo(Uri fileuri, String descriptionString) {
        // TODO 加description of the video
        Log.d(TAG,"uploadVideo");
        Log.d(TAG,"fileUri"+fileuri.toString());
            String description = descriptionString;
            File originalFile = mobilecrowdsourceStudy.nctu.minuku_2.AllUtils.FileUtils.getFile(VideoResult.this, fileuri);
            RequestBody descriptionPart = RequestBody.create(MultipartBody.FORM, description);
            Log.d(TAG, "request body : ");
            // get video mime type
            ContentResolver cR = this.getContentResolver();
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            String type = mime.getExtensionFromMimeType(cR.getType(fileuri));
            Log.d(TAG, "file type : " + cR.getType(fileuri).toString());
//        ProgressRequestBody fileBody = new ProgressRequestBody(originalFile,type,VideoResult.this);

            RequestBody filePart = RequestBody.create(MediaType.parse(type),
                    originalFile);

            MultipartBody.Part file = MultipartBody.Part.createFormData("video", originalFile.getName(), filePart);
            Retrofit.Builder builder = new Retrofit.Builder()
                    .baseUrl(URL_SAVE_VIDEO)
                    .addConverterFactory(GsonConverterFactory.create());
            Retrofit retrofit = builder.build();

            UserClient client = retrofit.create(UserClient.class);
            Call<ResponseResult> call = client.uploadVideo(descriptionPart, file);

            call.enqueue(new Callback<ResponseResult>() {
                @Override
                public void onResponse(Call<ResponseResult> call, Response<ResponseResult> response) {
                    String deviceId = "";
                    String fileName = "";
                    if (response.body() != null) {
                        String fields = response.body().getFields();
                        String[] parts = fields.split("-");
                        deviceId = parts[0];

                        String files = response.body().getFiles();
//                    String[] filePart = files.split("/");
                        fileName = fields;

                        Log.d(TAG, "fields : " + fields);
                        Log.d(TAG, "files : " + files);
                        Log.d(TAG, "deviceId : " + deviceId);
                        Log.d(TAG, "fileName : " + fileName);
                        Log.d(TAG,"URL_SAVE_VIDEO : "+URL_SAVE_VIDEO);
                    }
                    Log.d(TAG,"update DeviceId : "+deviceId);
                    if (Constants.DEVICE_ID.equals(deviceId)) {
                        db.videoDataRecordDao().updateDataStatusByFileName(fileName, 1);
                        Log.d(TAG,"update fileName : "+fileName);

                    }
//                if(response!=null) {
//                    Log.d(TAG, "toString :" + response.toString());
//                    Log.d(TAG, "message :" + response.message());
//                }
                    Toast.makeText(VideoResult.this, "uploaded", Toast.LENGTH_LONG);

                }

                @Override
                public void onFailure(Call call, Throwable t) {
                    Toast.makeText(VideoResult.this, "uploaded failed", Toast.LENGTH_LONG);

                }


            });
        }



}
