package labelingStudy.nctu.minuku.streamgenerator;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;

import org.greenrobot.eventbus.EventBus;

import labelingStudy.nctu.minuku.DBHelper.appDatabase;
import labelingStudy.nctu.minuku.config.Constants;
import labelingStudy.nctu.minuku.logger.Log;
import labelingStudy.nctu.minuku.model.DataRecord.AccessibilityDataRecord;
import labelingStudy.nctu.minuku.service.MobileAccessibilityService;
import labelingStudy.nctu.minuku.stream.AccessibilityStream;
import labelingStudy.nctu.minukucore.exception.StreamAlreadyExistsException;
import labelingStudy.nctu.minukucore.exception.StreamNotFoundException;
import labelingStudy.nctu.minukucore.stream.Stream;

import static labelingStudy.nctu.minuku.manager.MinukuStreamManager.getInstance;

/**
 * Created by chiaenchiang on 08/03/2018.
 */

public class AccessibilityStreamGenerator extends AndroidStreamGenerator<AccessibilityDataRecord> {

    private final String TAG = "AccessibilityStreamGenerator";
    private final String room = "room";
    private Stream mStream;
    private Context mContext;
    MobileAccessibilityService mobileAccessibilityService;

    private String pack;
    private String text;
    private String type;
    private String extra;
    private String content;
    private Integer mcid;
    appDatabase db;



    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public AccessibilityStreamGenerator(Context applicationContext){
        super(applicationContext);
        this.mContext = applicationContext;
        this.mStream = new AccessibilityStream(Constants.DEFAULT_QUEUE_SIZE);

        mobileAccessibilityService = new MobileAccessibilityService(this);
        pack = text = type = extra =content= "";

        db = appDatabase.getDatabase(applicationContext);

        this.register();

    }

    @Override
    public void register() {
        Log.d(TAG, "Registring with StreamManage");

        try {
            getInstance().register(mStream, AccessibilityDataRecord.class, this);
        } catch (StreamNotFoundException streamNotFoundException) {
            Log.e(TAG, "One of the streams on which" +
                    "AccessibilityDataRecord/AccessibilityStream depends in not found.");
        } catch (StreamAlreadyExistsException streamAlreadyExistsException) {
            Log.e(TAG, "Another stream which provides" +
                    " AccessibilityDataRecord/AccessibilityStream is already registered.");
        }
    }

    private void activateAccessibilityService() {

        Log.d(TAG, "testing logging task and requested activateAccessibilityService");
        Intent intent = new Intent(mContext, MobileAccessibilityService.class);
        mContext.startService(intent);

    }


    @Override
    public Stream<AccessibilityDataRecord> generateNewStream() {
        return mStream;
    }

    @Override
    public boolean updateStream() {
        Log.d(TAG, "updateStream called");
        Log.d(TAG, "pack: "+pack+"text: "+text+"extra "+extra+"content"+content+"mcid: "+mcid);

        AccessibilityDataRecord accessibilityDataRecord
                = new AccessibilityDataRecord(pack, text, type, extra,content,mcid);
        content = content.replaceAll("\\[", "").replaceAll("\\]","");
        if(!pack.trim().isEmpty()&&!text.trim().isEmpty()||!content.trim().isEmpty()) {
            mStream.add(accessibilityDataRecord);
            Log.d(TAG, "Accessibility to be sent to event bus" + accessibilityDataRecord);
            Log.d("creationTime : ", "accessData : " + accessibilityDataRecord.getCreationTime());
            // also post an event.
            EventBus.getDefault().post(accessibilityDataRecord);
            try {

                db.accessibilityDataRecordDao().insertAll(accessibilityDataRecord);
//            List<AccessibilityDataRecord> accessibilityDataRecords = db.accessibilityDataRecordDao().getAll();
//
//            for (AccessibilityDataRecord a : accessibilityDataRecords) {
//                Log.d(room, "AccessPack : "+a.getPack());
//
//
//            }


            } catch (NullPointerException e) { //Sometimes no data is normal
                e.printStackTrace();
                return false;
            }
        }
        pack = text = type = extra = "";

        return false;
    }


    @Override
    public long getUpdateFrequency() {
        return 1;
    }

    @Override
    public void sendStateChangeEvent() {

    }



    public void setLatestInAppAction(String pack, String text, String type, String extra,String content,int mcid ){

        this.pack = pack;
        this.text = text;
        this.type = type;
        this.extra = extra;
        this.content = content;
        this.mcid = mcid;
//        Log.d(TAG, "pack, "+pack+"text "+text+"type "+type+"extra "+extra);

    }

    @Override
    public void onStreamRegistration() {

        activateAccessibilityService();

    }

    @Override
    public void offer(AccessibilityDataRecord dataRecord) {

    }

}

