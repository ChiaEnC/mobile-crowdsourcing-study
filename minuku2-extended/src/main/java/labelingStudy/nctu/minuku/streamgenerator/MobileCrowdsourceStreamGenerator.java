package labelingStudy.nctu.minuku.streamgenerator;

import android.content.Context;

import org.greenrobot.eventbus.EventBus;

import labelingStudy.nctu.minuku.DBHelper.appDatabase;
import labelingStudy.nctu.minuku.config.Constants;
import labelingStudy.nctu.minuku.manager.MinukuStreamManager;
import labelingStudy.nctu.minuku.model.DataRecord.MobileCrowdsourceDataRecord;
import labelingStudy.nctu.minuku.stream.MobileCrowdsourceStream;
import labelingStudy.nctu.minukucore.exception.StreamAlreadyExistsException;
import labelingStudy.nctu.minukucore.exception.StreamNotFoundException;
import labelingStudy.nctu.minukucore.stream.Stream;

/**
 * Created by chiaenchiang on 04/11/2018.
 */

public class MobileCrowdsourceStreamGenerator extends AndroidStreamGenerator<MobileCrowdsourceDataRecord> {
    private Context mContext;
    String TAG = "MobileCrowdsourceStreanGenerator";
    private final String room = "room";
    private MobileCrowdsourceStream mStream;
    appDatabase db;
    public static String mApp = "";
    public static boolean mifSentNoti = false;
    public static long mstartTasktime = 0;
    public static  long mendTasktime =0;
    public static String muserActions;
    public static int maccessId = -1;

    public MobileCrowdsourceStreamGenerator(Context applicationContext){
        super(applicationContext);
        this.mStream = new MobileCrowdsourceStream(Constants.DEFAULT_QUEUE_SIZE);
        mContext = applicationContext;


        db = appDatabase.getDatabase(applicationContext);
        this.register();
        mApp = "Default";
        mifSentNoti = false;
        mstartTasktime = 0;
        mendTasktime =0;
        muserActions ="Default";
        maccessId = -1;
    }
    @Override
    public void register() {
        labelingStudy.nctu.minuku.logger.Log.d(TAG, "Registering with Stream Manager");
        try {
            MinukuStreamManager.getInstance().register(mStream, MobileCrowdsourceDataRecord.class, this);
        } catch (StreamNotFoundException streamNotFoundException) {
            labelingStudy.nctu.minuku.logger.Log.e(TAG, "One of the streams on which MobileCrowdsourceDataRecord/MobileCrowdsourceStream depends in not found.");
        } catch (StreamAlreadyExistsException streamAlreadyExsistsException) {
            labelingStudy.nctu.minuku.logger.Log.e(TAG, "Another stream which provides MobileCrowdsourceDataRecord/MobileCrowdsourceStream is already registered.");
        }

    }

    @Override
    public Stream<MobileCrowdsourceDataRecord> generateNewStream() {
        return null;
    }

    @Override
    public boolean updateStream() {

        MobileCrowdsourceDataRecord mobileCrowdsourceDataRecord =
                new MobileCrowdsourceDataRecord( mApp,mifSentNoti
            ,mstartTasktime, mendTasktime, muserActions,maccessId);

        mStream.add(mobileCrowdsourceDataRecord);
        labelingStudy.nctu.minuku.logger.Log.d(TAG, "Check mobileCrowdsourceDataRecord to be sent to event bus" + mobileCrowdsourceDataRecord);
        // also post an event.
        labelingStudy.nctu.minuku.logger.Log.d("creationTime : ", "mobileCData : "+mobileCrowdsourceDataRecord.getCreationTime());

        EventBus.getDefault().post(mobileCrowdsourceDataRecord);

        try {

            db.mobileCrowdsourceDataRecordDao().insertAll(mobileCrowdsourceDataRecord);
//
//            List<MobileCrowdsourceDataRecord> MobileCrowdsourceDataRecords = db.mobileCrowdsourceDataRecordDao().getAll();
//            for (MobileCrowdsourceDataRecord m : MobileCrowdsourceDataRecords) {
//                Log.d(room, "MCdataRecord taskType :"+String.valueOf(m.getTasktype()));
//            }



        } catch (NullPointerException e) { //Sometimes no data is normal
            e.printStackTrace();
            return false;
        }
        return false;


    }


    @Override
    public long getUpdateFrequency() {
        return -1;
    }

    @Override
    public void sendStateChangeEvent() {

    }

    @Override
    public void onStreamRegistration() {

    }


    public void setMCDataRecord(String mApp,boolean mifSentNoti,Long mstartTasktime,Long mendTasktime,String muserActions,int accessId){
        this.mApp = mApp;
        this.mifSentNoti = mifSentNoti;
        this.mstartTasktime = mstartTasktime;
        this.mendTasktime = mendTasktime;
        this.muserActions = muserActions;
        this.maccessId = accessId;

    }

    @Override
    public void offer(MobileCrowdsourceDataRecord dataRecord) {

    }
}
