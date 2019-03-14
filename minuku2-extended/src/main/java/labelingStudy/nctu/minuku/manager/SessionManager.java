package labelingStudy.nctu.minuku.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.ArrayList;

import labelingStudy.nctu.minuku.Utilities.CSVHelper;
import labelingStudy.nctu.minuku.Utilities.ScheduleAndSampleManager;
import labelingStudy.nctu.minuku.Utilities.Utils;
import labelingStudy.nctu.minuku.config.Constants;
import labelingStudy.nctu.minuku.model.DataRecord.LocationDataRecord;
import labelingStudy.nctu.minuku.model.Session;

/**
 * Created by Lawrence on 2018/3/13.
 */

public class SessionManager {

    private final static String TAG = "SessionManager";

    public static final String ANNOTATION_PROPERTIES_ANNOTATION = "Annotation";
    public static final String ANNOTATION_PROPERTIES_ID = "Id";
    public static final String ANNOTATION_PROPERTIES_NAME= "Name";
    public static final String ANNOTATION_PROPERTIES_START_TIME = "Start_time";
    public static final String ANNOTATION_PROPERTIES_END_TIME = "End_time";
    public static final String ANNOTATION_PROPERTIES_IS_ENTIRE_SESSION = "Entire_session";
    public static final String ANNOTATION_PROPERTIES_CONTENT = "Content";
    public static final String ANNOTATION_PROPERTIES_TAG = "Tag";

    public static final String SESSION_DISPLAY_ONGOING = "Ongoing...";
    public static final String SESSION_DISPLAY_NO_ANNOTATION = "No Label";

    public static final String SESSION_LONGENOUGH_THRESHOLD_DISTANCE = "distance";

    public static final long SESSION_MIN_INTERVAL_THRESHOLD_TRANSPORTATION = 2 * Constants.MILLISECONDS_PER_MINUTE;
    public static final long SESSION_MIN_DURATION_THRESHOLD_TRANSPORTATION = 3 * Constants.MILLISECONDS_PER_MINUTE;
    public static final long SESSION_MIN_DISTANCE_THRESHOLD_TRANSPORTATION = 100;  // meters;

    public static int SESSION_DISPLAY_RECENCY_THRESHOLD_HOUR = 24;


    private ArrayList<LocationDataRecord> LocationToTrip;

    private static Context mContext;

    private static SessionManager instance;

    private static ArrayList<Integer> mOngoingSessionIdList;
    private static ArrayList<Integer> mEmptyOngoingSessionIdList;

    public static boolean sessionIsWaiting;


    public SessionManager(Context context) {

        this.mContext = context;

        mOngoingSessionIdList = new ArrayList<Integer>();

        mEmptyOngoingSessionIdList = new ArrayList<Integer>();

        sessionIsWaiting = false;
    }

    public static SessionManager getInstance() {
        if(SessionManager.instance == null) {
            try {
//                SessionManager.instance = new SessionManager();
                Log.d(TAG,"getInstance without mContext.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return SessionManager.instance;
    }

    public static SessionManager getInstance(Context context) {
        if(SessionManager.instance == null) {
            try {
                SessionManager.instance = new SessionManager(context);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return SessionManager.instance;
    }

    public static int getOngoingSessionId(){

        int session_id = -1;

        int countOfOngoingSession = mOngoingSessionIdList.size();

        //if there exists an ongoing session
        if (countOfOngoingSession > 0){
            session_id = mOngoingSessionIdList.get(0);
        }

        return session_id;
    }

    public static boolean isSessionOngoing(int sessionId, SharedPreferences sharedPrefs) {

        Log.d(TAG, " [test combine] tyring to see if the session is ongoing:" + sessionId);

//        for (int i = 0; i< mOngoingSessionIdList.size(); i++){
//            //       Log.d(LOG_TAG, " [getCurRecordingSession] looping to " + i + "th session of which the id is " + mCurRecordingSessions.get(i).getId());
//
//            if (mOngoingSessionIdList.get(i)==sessionId){
//                return true;
//            }
//        }

        int ongoingSessionid = sharedPrefs.getInt("ongoingSessionid", Constants.INVALID_INT_VALUE);

        if(ongoingSessionid == sessionId){

            return true;
        }

        Log.d(TAG, " [test combine] the session is not ongoing:" + sessionId);

        return false;
    }

    /**
     * In current structure, there would only be one ongoingSession in the list, while the ongoingSession could be considered as the current recording session
     * @return
     */
    public static ArrayList<Integer> getOngoingSessionIdList() {
        return mOngoingSessionIdList;
    }

    public static boolean isSessionEmptyOngoing(int sessionId, SharedPreferences sharedPrefs) {

        Log.d(TAG, " [test combine] tyring to see if the session is ongoing:" + sessionId);

//        for (int i = 0; i< mEmptyOngoingSessionIdList.size(); i++){
//
//            if (mEmptyOngoingSessionIdList.get(i)==sessionId){
//                return true;
//            }
//        }

        int ongoingSessionid = sharedPrefs.getInt("emptyOngoingSessionid", Constants.INVALID_INT_VALUE);

        if(ongoingSessionid == sessionId){

            return true;
        }

        Log.d(TAG, " [test combine] the session is not ongoing:" + sessionId);

        return false;
    }

    public static void setEmptyOngoingSessionIdList(ArrayList<Integer> mEmptyOngoingSessionIdList) {
        SessionManager.mEmptyOngoingSessionIdList = mEmptyOngoingSessionIdList;
    }

    public static ArrayList<Integer> getEmptyOngoingSessionIdList() {
        return mEmptyOngoingSessionIdList;
    }

    public static void addEmptyOngoingSessionid(int id) {
        Log.d(TAG, "test combine: adding ongonig session " + id );
        mEmptyOngoingSessionIdList.add(id);
    }

    /**
     * This function convert Session String retrieved from the DB to Object Session
     * @param sessionStr
     * @return
     */
    public static Session convertStringToSession(String sessionStr) {

        Session session = null;

        //split each row into columns
        String[] separated = sessionStr.split(Constants.DELIMITER);

        /** get properties of the session **/
//        int id = Integer.parseInt(separated[DBHelper.COL_INDEX_SESSION_ID]);
//        long startTime = Long.parseLong(separated[DBHelper.COL_INDEX_SESSION_START_TIME]);

//
//        /** 1. create sessions from the properies obtained **/
//        session = new Session(id, startTime);
//
//        /**2. get end time (or time of the last record) of the sesison**/
//        long endTime = 0;
//
//        //the session could be still ongoing..so we need to check where's endTime
//        Log.d(TAG, "[test show trip] separated[DBHelper.COL_INDEX_SESSION_END_TIME] " + separated[DBHelper.COL_INDEX_SESSION_END_TIME]);
//
//        if (!separated[DBHelper.COL_INDEX_SESSION_END_TIME].equals("null") && !separated[DBHelper.COL_INDEX_SESSION_END_TIME].equals("")){
//
//            endTime = Long.parseLong(separated[DBHelper.COL_INDEX_SESSION_END_TIME]);
//        }
//        //there 's no end time of the session, we take the time of the last record
//        else {
//
//            endTime = getLastRecordTimeinSession(session.getId());
//        }
//
//        Log.d(TAG, "[test show trip] testgetdata the end time is now:  " + ScheduleAndSampleManager.getTimeString(endTime));
//
//        long createdTime = Long.parseLong(separated[DBHelper.COL_INDEX_SESSION_CREATED_TIME]);
//        session.setCreatedTime(createdTime);
//
//        int isUserPress;
//        int isModified;
//
//        if (!separated[DBHelper.COL_INDEX_SESSION_USERPRESSORNOT_FLAG].equals("null") && !separated[DBHelper.COL_INDEX_SESSION_USERPRESSORNOT_FLAG].equals("")){
//
////            isUserPress = Boolean.parseBoolean(separated[DBHelper.COL_INDEX_SESSION_USERPRESSORNOT_FLAG]);
//            isUserPress = Integer.parseInt(separated[DBHelper.COL_INDEX_SESSION_USERPRESSORNOT_FLAG]);
//
//            Log.d(TAG, "[test show trip] testgetdata isUserPress is now:  " + isUserPress);
//
//            if(isUserPress == 1){
//
//                session.setUserPressOrNot(true);
//            }else {
//
//                session.setUserPressOrNot(false);
//            }
//        }
//
//        if (!separated[DBHelper.COL_INDEX_SESSION_MODIFIED_FLAG].equals("null") && !separated[DBHelper.COL_INDEX_SESSION_MODIFIED_FLAG].equals("")){
//
////            isModified = Boolean.parseBoolean(separated[DBHelper.COL_INDEX_SESSION_MODIFIED_FLAG]);
//            isModified = Integer.parseInt(separated[DBHelper.COL_INDEX_SESSION_MODIFIED_FLAG]);
//
//            Log.d(TAG, "[test show trip] testgetdata isModified is now:  " + isModified);
//
//            if(isModified == 1){
//
//                session.setModified(true);
//            }else {
//
//                session.setModified(false);
//            }
//        }
//
//        if (!separated[DBHelper.COL_INDEX_SESSION_SENTORNOT_FLAG].equals("null") && !separated[DBHelper.COL_INDEX_SESSION_SENTORNOT_FLAG].equals("")) {
//
//            session.setIsSent(Integer.valueOf(separated[DBHelper.COL_INDEX_SESSION_SENTORNOT_FLAG]));
//        }
//
//        if (!separated[DBHelper.COL_INDEX_SESSION_TYPE].equals("null") && !separated[DBHelper.COL_INDEX_SESSION_TYPE].equals("")) {
//
//            session.setType(separated[DBHelper.COL_INDEX_SESSION_TYPE]);
//        }
//
//        if (!separated[DBHelper.COL_INDEX_SESSION_HIDEDORNOT].equals("null") && !separated[DBHelper.COL_INDEX_SESSION_HIDEDORNOT].equals("")) {
//
//            session.setHidedOrNot(Integer.valueOf(separated[DBHelper.COL_INDEX_SESSION_HIDEDORNOT]));
//        }
//
//        //set end time
//        session.setEndTime(endTime);
//
//        /** 3. get annotaitons associated with the session **/
//        JSONObject annotationSetJSON = null;
//        JSONArray annotateionSetJSONArray = null;
//        try {
//
//            if (!separated[DBHelper.COL_INDEX_SESSION_ANNOTATION_SET].equals("null")){
//
//                annotationSetJSON = new JSONObject(separated[DBHelper.COL_INDEX_SESSION_ANNOTATION_SET]);
//                annotateionSetJSONArray = annotationSetJSON.getJSONArray(ANNOTATION_PROPERTIES_ANNOTATION);
//            }
//        } catch (JSONException e) {
//            Log.e(TAG, "JSONException", e);
//            e.printStackTrace();
//        }
//
//        //set annotationset if there is one
//        if (annotateionSetJSONArray!=null){
//
//            AnnotationSet annotationSet = toAnnorationSet(annotateionSetJSONArray);
//            session.setAnnotationSet(annotationSet);
//        }

        return session;
    }



//    public static Session getLastSession() {
//
//        Session session = null;
//
//        ArrayList<String> sessions = DBHelper.queryLastSession();
//        if(sessions.size()!=0) {
//            String sessionStr = sessions.get(0);
//            Log.d(TAG, "test show trip lastsession " + sessionStr);
//            session = convertStringToSession(sessionStr);
////        Log.d(TAG, " test show trip  testgetdata id " + session.getId() + " startTime " + session.getStartTime() + " end time " + session.getEndTime() + " annotation " + session.getAnnotationsSet().toJSONObject().toString());
//            Log.d(TAG, " test show trip  testgetdata id " + session.getId() + " startTime " + ScheduleAndSampleManager.getTimeString(session.getStartTime()) + " end time " + ScheduleAndSampleManager.getTimeString(session.getEndTime()) + " annotation " + session.getAnnotationsSet().toJSONObject().toString());
//        }else{
//
//            session = new Session(1);
//        }
//
//        return session;
//    }




    public static Session getSession (int sessionId) {

        Log.d(TAG, "sessionId : "+sessionId);
        Session session = null;
        try {
//
//            String sessionStr = DBHelper.querySession(sessionId).get(0);
//            Log.d(TAG, "query session from LocalDB is " + sessionStr);
//            session = convertStringToSession(sessionStr);
            Log.d(TAG, " testgetdata id " + session.getId() + " startTime " + session.getStartTime() + " end time " + session.getEndTime() + " annotation " + session.getAnnotationsSet().toJSONObject().toString());
        }catch (IndexOutOfBoundsException e){
            CSVHelper.storeToCSV(CSVHelper.CSV_CHECK_SESSION, "sessionId : "+sessionId+" no data in DB");
            CSVHelper.storeToCSV(CSVHelper.CSV_CHECK_SESSION, Utils.getStackTrace(e));
            getSession(sessionId-1);
        }
        return session;
    }



    /**
     *
     * @param sessionId
     * @param endTime
     * @param userPressOrNot
     */

    /**
     *
     * @param session
     */
//    public static void startNewSession(Session session) {
//
//        //InstanceManager add ongoing session for the new activity
////        SessionManager.getInstance().addOngoingSessionid(session.getId());
//
//        Log.d(TAG, "startNewSession id " + session.getId() + " startTime " + ScheduleAndSampleManager.getTimeString(session.getStartTime()) + " end time " + ScheduleAndSampleManager.getTimeString(session.getEndTime()) + " annotation " + session.getAnnotationsSet().toJSONObject().toString());
//
//        DBHelper.insertSessionTable(session);
//
//    }

    /**
     * Combine sessions if
     * newSession and lastSecond are same
     * or
     * newSession and lastSecond are same while last is static
     */



    /**
     * Combine secondLast  session and new session
     */


    /**
     *
     * @param session
     */
    public static void endCurSession(Session session) {

        Log.d(TAG, "test show trip: end cursession Id : " + session.getId());
        Log.d(TAG, "test show trip: end cursession EndTime : " + ScheduleAndSampleManager.getTimeString(session.getEndTime()));
        Log.d(TAG, "test show trip: end cursession isUserPress : " + session.isUserPress());

        //remove the ongoing session
//        mOngoingSessionIdList.remove(Integer.valueOf(session.getId()));

        //update session with end time and long enough flag.
        //updateCurSessionEndInfoTo(session.getId(),session.getEndTime(),session.isUserPress());
    }


}