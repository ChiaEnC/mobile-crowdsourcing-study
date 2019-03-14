package labelingStudy.nctu.minuku.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import labelingStudy.nctu.minuku.R;

import static labelingStudy.nctu.minuku.config.Constants.STOP_RECORDING;
import static labelingStudy.nctu.minuku.config.SharedVariables.ifClickedFAB;
import static labelingStudy.nctu.minuku.config.SharedVariables.ifRecordingRightNow;
import static labelingStudy.nctu.minuku.config.SharedVariables.ifUserStop;


/**
 * Created by chiaenchiang on 29/11/2018.
 */

public class FloatingActionButtonService extends Service  {

    private final String TAG = "FloatingActionButtonService";
    private WindowManager mWindowManager;
    private View mOverlayView;
    int mWidth;
    private ImageView counterFab, mButtonClose;
    boolean floating_appear;



    @SuppressLint("LongLogTag")
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG,"onbind");
        return null;
    }


    @SuppressLint("LongLogTag")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null) {
            floating_appear = intent.getBooleanExtra("floating_appear", false);

        }

        if (mOverlayView == null) {
            Log.d(TAG,"onstartCommand");
            mOverlayView = LayoutInflater.from(this).inflate(R.layout.overlay_layout, null);
            int LAYOUT_FLAG;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            } else {
                LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
            }

            final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    LAYOUT_FLAG,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);


            //Specify the view position
            params.gravity = Gravity.TOP ;        //Initially view will be added to top-left corner
            params.x = 0;
            params.y = 150;


            mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

            mWindowManager.addView(mOverlayView, params);

            Display display = mWindowManager.getDefaultDisplay();
            final Point size = new Point();
            display.getSize(size);

            counterFab = (ImageView) mOverlayView.findViewById(R.id.fabHead);
            mButtonClose = (ImageView) mOverlayView.findViewById(R.id.closeButton);


            final RelativeLayout layout = (RelativeLayout) mOverlayView.findViewById(R.id.layout);
            ViewTreeObserver vto = layout.getViewTreeObserver();
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    layout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    int width = layout.getMeasuredWidth();

                    //To get the accurate middle of the screen we subtract the width of the floating widget.
                    mWidth = size.x - width;

                }
            });
            mButtonClose.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("LongLogTag")
                @Override
                public void onClick(View v) {

                    Log.d(TAG,"click close");
                    if(ifRecordingRightNow)
                        stopRecording();
                    stopSelf();
                    Log.d(TAG,"button on click close stopself ");
                }
            });

//            counterFab.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    stopSelf();
//                }
//            });




            counterFab.setOnTouchListener(new View.OnTouchListener() {
                private int initialX;
                private int initialY;
                private float initialTouchX;
                private float initialTouchY;

                @SuppressLint("LongLogTag")
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:

                            //remember the initial position.
                            initialX = params.x;
                            initialY = params.y;


                            //get the touch location
                            initialTouchX = event.getRawX();
                            initialTouchY = event.getRawY();

                            return true;
                        case MotionEvent.ACTION_UP:

                            //Only start the activity if the application is in background. Pass the current badge_count to the activity
                            if(floating_appear){
//                                float xDiff = event.getRawX() - initialTouchX;
//                                float yDiff = event.getRawY() - initialTouchY;
////
//                                if ((Math.abs(xDiff) < 5) && (Math.abs(yDiff) < 5)) {
////                                    Intent intent = new Intent(getBaseContext(), MainActivity.class);
////                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////                                    startActivity(intent);
//                                   // Log.d(TAG,"smaller");
//
//                                    //close the service and remove the fab view
//                                }
//                                stopSelf();

                                if(!ifRecordingRightNow) {
                                    ifClickedFAB = true;
                                    Intent Record = new Intent(getApplicationContext(), BackgroundScreeenRecorderActivity.class);
                                    startActivity(Record);
                                    updateAlpha(counterFab);
                                    floating_appear = false;

                                }
                                Log.d(TAG,"stop self after recording");
                            }

                            //Logic to auto-position the widget based on where it is positioned currently w.r.t middle of the screen.
//                            int middle = mWidth / 2;
//                            float nearestXWall = params.x >= middle ? mWidth : 0;
//                            params.x = (int) nearestXWall;
//
//                            mWindowManager.updateViewLayout(mOverlayView, params);

                            return true;
                        case MotionEvent.ACTION_MOVE:


//                            int xDiff2 = Math.round(event.getRawX() - initialTouchX);
//                            int yDiff2 = Math.round(event.getRawY() - initialTouchY);
//
//
//                            //Calculate the X and Y coordinates of the view.
//                            params.x = initialX + xDiff2;
//                            params.y = initialY + yDiff2;
//
//                            //Update the layout with new X & Y coordinates
//                            mWindowManager.updateViewLayout(mOverlayView, params);
//
//
//                            return true;
                    }
                    return false;
                }
            });
        }


        return super.onStartCommand(intent, flags, startId);


    }
    void updateAlpha(View v)
    {

        v.setAlpha(0);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        setTheme(R.style.AppTheme);


    }

    @SuppressLint("LongLogTag")
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"onDestroy");
        if (mOverlayView != null)
            mWindowManager.removeView(mOverlayView);
    }
    public void stopRecording(){

        Intent broadCastIntent = new Intent();

        broadCastIntent.setAction(STOP_RECORDING);
        ifUserStop = true;

//        sendBroadcast(broadCastIntent);
        Log.d("checkBroadcast","stopRecording");
        Toast.makeText(this,"stopRecording",Toast.LENGTH_LONG);
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadCastIntent);
    }

}
