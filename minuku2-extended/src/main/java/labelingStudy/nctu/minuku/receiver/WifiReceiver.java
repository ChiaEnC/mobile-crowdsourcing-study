package labelingStudy.nctu.minuku.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import labelingStudy.nctu.minuku.DBHelper.appDatabase;
import labelingStudy.nctu.minuku.Utilities.CSVHelper;
import labelingStudy.nctu.minuku.Utilities.ScheduleAndSampleManager;
import labelingStudy.nctu.minuku.config.Constants;
import labelingStudy.nctu.minuku.model.Annotation;

import static labelingStudy.nctu.minuku.config.Constants.PACKAGE_DIRECTORY_PATH;

/**
 * Created by chiaenchiang on 09/11/2018.
 */


public class WifiReceiver extends BroadcastReceiver {

    private final String TAG = "WifiReceiver";
    appDatabase db;
    private SharedPreferences sharedPrefs;

    private int year,month,day,hour,min;

    private long nowTime = -9999;
    private long startTime = -9999;
    private long endTime = -9999;

    private String currentCondition;

    public static final int HTTP_TIMEOUT = 10000; // millisecond
    public static final int SOCKET_TIMEOUT = 10000; // millisecond

    private static final String postDumpUrl_insert = "http://13.58.134.191:5000/find_latest_and_insert?collection=dump&action=insert&id=";//&action=insert, search
    private static final String postDumpUrl_search = "http://13.58.134.191:5000/find_latest_and_insert?collection=dump&action=search&id=";//&action=insert, search

    private static final String postTripUrl_insert = "http://13.58.134.191:5000/find_latest_and_insert?collection=trip&action=insert&id=";//&action=insert, search
    private static final String postTripUrl_search = "http://13.58.134.191:5000/find_latest_and_insert?collection=trip&action=search&id=";//&action=insert, search

    private static final String postIsAliveUrl_insert = "http://13.58.134.191:5000/find_latest_and_insert?collection=isAlive&action=insert&id=";//&action=insert, search
    private boolean noDataFlag1 = false;
    private boolean noDataFlag2 = false;
    private boolean noDataFlag3 = false;
    private boolean noDataFlag4 = false;
    private boolean noDataFlag5 = false;
    private boolean noDataFlag6 = false;
    private boolean noDataFlag7 = false;


    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d(TAG, "onReceive");
        db = appDatabase.getDatabase(context);
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        //get timzone //prevent the issue when the user start the app in wifi available environment.
        TimeZone tz = TimeZone.getDefault();
        Calendar cal = Calendar.getInstance(tz);
        int mYear = cal.get(Calendar.YEAR);
        int mMonth = cal.get(Calendar.MONTH);
        int mDay = cal.get(Calendar.DAY_OF_MONTH);
        int mHour = cal.get(Calendar.HOUR_OF_DAY);

        sharedPrefs = context.getSharedPreferences(Constants.sharedPrefString, context.MODE_PRIVATE);

        year = sharedPrefs.getInt("StartYear", mYear);
        month = sharedPrefs.getInt("StartMonth", mMonth);
        day = sharedPrefs.getInt("StartDay", mDay);

        Constants.USER_ID = sharedPrefs.getString("userid","NA");
        Constants.GROUP_NUM = sharedPrefs.getString("groupNum","NA");

        hour = sharedPrefs.getInt("StartHour", mHour);
        min = sharedPrefs.getInt("StartMin",0);

        currentCondition = context.getResources().getString(labelingStudy.nctu.minuku.R.string.current_task);

        Log.d(TAG, "year : "+ year+" month : "+ month+" day : "+ day+" hour : "+ hour+" min : "+ min);

        if (Constants.ACTION_CONNECTIVITY_CHANGE.equals(intent.getAction())) {

            if(activeNetwork != null &&
                    activeNetwork.getType() == ConnectivityManager.TYPE_WIFI
                    //assure the situation
                    && activeNetwork.isConnected()
                    ){

                boolean firstTimeToLogCSV_Wifi = sharedPrefs.getBoolean(CSVHelper.CSV_Wifi, true);

                if(firstTimeToLogCSV_Wifi) {
                    CSVHelper.storeToCSV(CSVHelper.CSV_Wifi, "describeContents", "getDetailedState", "getExtraInfo",
                            "getReason", "getState", "getSubtypeName", "getTypeName", "isAvailable", "isConnected",
                            "isConnectedOrConnecting", "isFailover", "isRoaming");

                    sharedPrefs.edit().putBoolean(CSVHelper.CSV_Wifi, false).apply();
                }

                CSVHelper.storeToCSV(CSVHelper.CSV_Wifi, String.valueOf(activeNetwork.describeContents()), activeNetwork.getDetailedState().toString()
                        , activeNetwork.getExtraInfo(), activeNetwork.getReason(), activeNetwork.getState().toString(), String.valueOf(activeNetwork.getSubtypeName())
                        , activeNetwork.getTypeName(), String.valueOf(activeNetwork.isAvailable()),
                        String.valueOf(activeNetwork.isConnected()), String.valueOf(activeNetwork.isConnectedOrConnecting()),
                        String.valueOf(activeNetwork.isFailover()), String.valueOf(activeNetwork.isRoaming()));

                CSVHelper.storeToCSV(CSVHelper.CSV_Wifi, activeNetwork.toString());

                uploadData();
            }
        }
    }

    public boolean sendingDumpData(long startTime, long endTime){

        Log.d(TAG, "sendingDumpData");

        JSONObject dataInJson = new JSONObject();

        try {

            dataInJson.put("device_id", Constants.DEVICE_ID);
           // dataInJson.put("condition", currentCondition);
            dataInJson.put("startTime", String.valueOf(startTime));
            dataInJson.put("endTime", String.valueOf(endTime));
            dataInJson.put("startTimeString", ScheduleAndSampleManager.getTimeString(startTime));
            dataInJson.put("endTimeString", ScheduleAndSampleManager.getTimeString(endTime));
        }catch (JSONException e){

        }

        storeTransporatation(dataInJson);
        storeLocation(dataInJson);
        storeActivityRecognition(dataInJson);
        storeRinger(dataInJson);
        storeConnectivity(dataInJson);
        storeBattery(dataInJson);
        storeAppUsage(dataInJson);
        storeTelephony(dataInJson);
        storeSensor(dataInJson);
        storeAccessibility(dataInJson);
        storeNotification(dataInJson);
        storeMobileCrowdsource(dataInJson);
       // storeActionLog(dataInJson);

        Log.d(TAG,"final dump data : "+ dataInJson.toString());


        String curr = getDateCurrentTimeZone(new Date().getTime());

        String lastTimeInServer;

        try {

            CSVHelper.storeToCSV(CSVHelper.CSV_Wifi, "sending dump data endTime : ", dataInJson.getString("endTime"));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                lastTimeInServer = new HttpAsyncPostJsonTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                        postDumpUrl_insert+ Constants.DEVICE_ID,
                        dataInJson.toString(),
                        "Dump",
                        curr).get();
            else
                lastTimeInServer = new HttpAsyncPostJsonTask().execute(
                        postDumpUrl_insert+ Constants.DEVICE_ID,
                        dataInJson.toString(),
                        "Dump",
                        curr).get();

            //if it was updated successfully, return the end time
            Log.d(TAG, "[show availSite response] Trip lastTimeInServer : " + lastTimeInServer);

            JSONObject lasttimeInServerJson = new JSONObject(lastTimeInServer);

            Log.d(TAG, "[show availSite response] check sent endTime : " + dataInJson.getString("endTime"));
            Log.d(TAG, "[show availSite response] check sent endTime : " + dataInJson.getString("endTime"));
            Log.d(TAG, "[show availSite response] check latest availSite in server's endTime : " + lasttimeInServerJson.getString("endTime"));
            Log.d(TAG, "[show availSite response] check condition : " + dataInJson.getString("endTime").equals(lasttimeInServerJson.getString("endTime")));

            CSVHelper.storeToCSV(CSVHelper.CSV_Wifi, "responded dump endTime : ", lasttimeInServerJson.getString("endTime"));

            if(dataInJson.getString("endTime").equals(lasttimeInServerJson.getString("endTime"))){

                //TODO deprecated
//                //update next time range
//                latestUpdatedTime = endTime;
//
//                startTime = latestUpdatedTime;
//
//                long nextinterval = Constants.MILLISECONDS_PER_HOUR;
//
//                endTime = startTime + nextinterval;
//
//                Log.d(TAG, "[show data response] next iteration startTime : " + startTime);
//                Log.d(TAG, "[show data response] next iteration startTimeString : " + ScheduleAndSampleManager.getTimeString(startTime));
//
//                Log.d(TAG, "[show data response] next iteration endTime : " + endTime);
//                Log.d(TAG, "[show data response] next iteration endTimeString : " + ScheduleAndSampleManager.getTimeString(endTime));
//
//                sharedPrefs.edit().putLong("lastSentStarttime", startTime).apply();

                long lastSentStartTime = Long.valueOf(lasttimeInServerJson.getString("endTime"));

                sharedPrefs.edit().putLong("lastSentStarttime", lastSentStartTime).apply();

                return true;
            }else{

                //if connected fail, stop trying and wait for the next time
                return false;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e){
            e.printStackTrace();
        }

        //default is to not try to send due to it might stuck on the loop
        return false;
    }

    private void uploadData(){

        Constants.DEVICE_ID = sharedPrefs.getString("DEVICE_ID",  Constants.DEVICE_ID);

        Log.d(TAG, "DEVICE_ID : "+ Constants.DEVICE_ID);

        if(!Constants.DEVICE_ID.equals(Constants.INVALID_STRING_VALUE)) {

            setNowTime();

            startTime = sharedPrefs.getLong("lastSentStarttime", Constants.INVALID_TIME_VALUE);
            endTime = getDataStartTime();

            if(startTime != Constants.INVALID_TIME_VALUE){

                endTime = startTime + Constants.MILLISECONDS_PER_HOUR;
            }else{

                startTime = endTime - Constants.MILLISECONDS_PER_HOUR;
            }

            Log.d(TAG, "NowTimeString : " + ScheduleAndSampleManager.getTimeString(nowTime));
            Log.d(TAG, "endTimeString : " + ScheduleAndSampleManager.getTimeString(endTime));
            Log.d(TAG, "now > end ? " + (nowTime > endTime));

            boolean tryToSendData = true;

            //TODO might cause the infinite loop
            while(nowTime > endTime && tryToSendData) {

                Log.d(TAG,"before send dump data NowTimeString : " + ScheduleAndSampleManager.getTimeString(nowTime));

                Log.d(TAG,"before send dump data EndTimeString : " + ScheduleAndSampleManager.getTimeString(endTime));

                //TODO return the boolean value to check if the network is connected
                tryToSendData = sendingDumpData(startTime, endTime);
                labelingStudy.nctu.minuku.logger.Log.d("creationtime Check : ", "startTime : "+startTime);
                labelingStudy.nctu.minuku.logger.Log.d("creationtime Check : ", "endTime : "+endTime);

                //update nowTime
                setNowTime();

                //update endTime
                long lastEndTime = endTime;

                startTime = sharedPrefs.getLong("lastSentStarttime", Constants.INVALID_TIME_VALUE);
                endTime = getDataStartTime();

                if(startTime != Constants.INVALID_TIME_VALUE){

                    endTime = startTime + Constants.MILLISECONDS_PER_HOUR;
                }

                //if the data didn't be sent successfully, don't try to send again
                if(lastEndTime == endTime){
                    break;
                }

                Log.d(TAG, "now > end ? " + (nowTime > endTime));
            }

            // Trip, isAlive
           // sendingTripData(nowTime);

            sendingIsAliveData();
            deleteHaveSentData(startTime,endTime);
        }
    }

    private void deleteHaveSentData(Long startTime,Long endTime ){
        deleteTransporatation(startTime,endTime);
        deleteLocation(startTime,endTime);
        deleteActivityRecognition(startTime,endTime);
        deleteRinger(startTime,endTime);
        deleteConnectivity(startTime,endTime);
        deleteBattery(startTime,endTime);
        deleteAppUsage(startTime,endTime);
        deleteTelephony(startTime,endTime);
        deleteSensor(startTime,endTime);
        deleteAccessibility(startTime,endTime);
        deleteNotification(startTime,endTime);
        deleteMobileCrowdsource(startTime,endTime);

    }
    private long getDataStartTime(){

        long startTime = sharedPrefs.getLong("lastSentStarttime", Constants.INVALID_TIME_VALUE);

        if(startTime == Constants.INVALID_TIME_VALUE) {

            Calendar designatedStartTime = Calendar.getInstance();
            designatedStartTime.set(year, month, day-1, hour, min);

            //get the current time in sharp
            startTime = designatedStartTime.getTimeInMillis();
        }

        Log.d(TAG, "getDataStartTime startTime : "+ScheduleAndSampleManager.getTimeString(startTime));

        return startTime;
    }

    //TODO deprecated
    private void setDataStartEndTime(){

        Log.d(TAG, "setDataStartEndTime");

        long lastSentStarttime = sharedPrefs.getLong("lastSentStarttime", Constants.INVALID_TIME_VALUE);

        if (lastSentStarttime == Constants.INVALID_TIME_VALUE) {

            //if it doesn't response the setting with initialize ones
            //initialize
            Calendar designatedStartTime = Calendar.getInstance();
            designatedStartTime.set(year, month, day, hour, min);

            long startstartTime = designatedStartTime.getTimeInMillis();
            startTime = sharedPrefs.getLong("StartTime", startstartTime); //default
            Log.d(TAG, "StartTimeString : " + ScheduleAndSampleManager.getTimeString(startTime));

            sharedPrefs.edit().putLong("StartTime", startTime).apply();

            long startendTime = startstartTime + Constants.MILLISECONDS_PER_HOUR;
            endTime = sharedPrefs.getLong("EndTime", startendTime);
            Log.d(TAG, "EndTimeString : " + ScheduleAndSampleManager.getTimeString(endTime));

            sharedPrefs.edit().putLong("EndTime", endTime).apply();

        } else {

            //if it do reponse the setting with initialize ones
            startTime = Long.valueOf(lastSentStarttime);
            Log.d(TAG, "StartTimeString : " + ScheduleAndSampleManager.getTimeString(startTime));

            endTime = Long.valueOf(lastSentStarttime) + Constants.MILLISECONDS_PER_HOUR;
            Log.d(TAG, "EndTimeString : " + ScheduleAndSampleManager.getTimeString(endTime));
        }
    }

    private void setNowTime(){

       // nowTime = new Date().getTime() - Constants.MILLISECONDS_PER_DAY;
        nowTime = new Date().getTime() ;

//        nowTime = new Date().getTime(); //TODO for testing
    }

//    private void sendingTripData(long time24HrAgo){
//
//        Log.d(TAG, "sendingTripData");
//
//        ArrayList<JSONObject> datas = getSessionData(time24HrAgo);
//
//        Log.d(TAG, "tripData size : "+datas.size());
//
//        for(int index = 0; index < datas.size(); index++){
//
//            JSONObject data = datas.get(index);
//
//            Log.d(TAG, "[test Trip sending] trip availSite uploading : " + data.toString());
//
//            String curr = getDateCurrentTimeZone(new Date().getTime());
//
//            String lastTimeInServer;
//
//            try {
//
//                CSVHelper.storeToCSV(CSVHelper.CSV_Wifi, "sending trip data createdTime : ", data.getString("createdTime"));
//
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
//                    lastTimeInServer = new HttpAsyncPostJsonTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
//                            postTripUrl_insert + Constants.DEVICE_ID,
//                            data.toString(),
//                            "Trip",
//                            curr).get();
//                else
//                    lastTimeInServer = new HttpAsyncPostJsonTask().execute(
//                            postTripUrl_insert + Constants.DEVICE_ID,
//                            data.toString(),
//                            "Trip",
//                            curr).get();
//
//                //if it was updated successfully, return the end time
//                Log.d(TAG, "[show availSite response] Trip lastTimeInServer : " + lastTimeInServer);
//
//                lastTimeInServer = lastTimeInServer.replace("[","").replace("]","");
//
//                Log.d(TAG, "[show availSite response] Trip get rid of [ & ], lastTimeInServer : " + lastTimeInServer);
//
//                JSONObject lasttimeInServerJson = new JSONObject(lastTimeInServer);
//
//                Log.d(TAG, "[show availSite response] check sent createdTime : " + data.getString("createdTime"));
//                Log.d(TAG, "[show availSite response] check latest availSite in server's createdTime : " + lasttimeInServerJson.getString("createdTime"));
//                Log.d(TAG, "[show availSite response] check condition : " + data.getString("createdTime").equals(lasttimeInServerJson.getString("createdTime")));
//
//                CSVHelper.storeToCSV(CSVHelper.CSV_Wifi, "responded trip data createdTime : ", lasttimeInServerJson.getString("createdTime"));
//
//                if(data.getString("createdTime").equals(lasttimeInServerJson.getString("createdTime"))){
//
//                    //update the sent Session to already be sent
//                    String sentSessionId = data.getString("sessionid");
//                   // DataHandler.updateSession(Integer.valueOf(sentSessionId), Constants.SESSION_IS_ALREADY_SENT_FLAG);
//                } else{
//
//                    //if connected fail, stop trying and wait for the next time
//                    break;
//                }
//
//            } catch (InterruptedException e) {
//                Log.e(TAG,"InterruptedException", e);
//                e.printStackTrace();
//            } catch (ExecutionException e) {
//                Log.e(TAG,"ExecutionException", e);
//                e.printStackTrace();
//            } catch (JSONException e){
//                Log.e(TAG,"JSONException", e);
//                e.printStackTrace();
//            }
//        }
//    }


    private void sendingIsAliveData(){

        //making isAlive
        JSONObject dataInJson = new JSONObject();
        try {
            long currentTime = new Date().getTime();
            String currentTimeString = ScheduleAndSampleManager.getTimeString(currentTime);

            dataInJson.put("time", currentTime);
            dataInJson.put("timeString", currentTimeString);
            dataInJson.put("device_id", Constants.DEVICE_ID);
           // dataInJson.put("condition", currentCondition);

        }catch (JSONException e){
            e.printStackTrace();
        }

        Log.d(TAG, "isAlive availSite uploading : " + dataInJson.toString());

        String curr = getDateCurrentTimeZone(new Date().getTime());

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                new HttpAsyncPostJsonTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                        postIsAliveUrl_insert + Constants.DEVICE_ID,
                        dataInJson.toString(),
                        "isAlive",
                        curr).get();
            else
                new HttpAsyncPostJsonTask().execute(
                        postIsAliveUrl_insert + Constants.DEVICE_ID,
                        dataInJson.toString(),
                        "isAlive",
                        curr).get();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }

    //use HTTPAsyncTask to poHttpAsyncPostJsonTask availSite
    private class HttpAsyncPostJsonTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            String result = null;
            String url = params[0];
            String data = params[1];
            String dataType = params[2];
            String lastSyncTime = params[3];

            try {

                CSVHelper.storeToCSV(CSVHelper.CSV_Wifi, "going to send " + dataType + " data by postJSON, time : ", new JSONObject(data).getString("endTime"));
            }catch (JSONException e){

            }

            result = postJSON(url, data, dataType, lastSyncTime);

            CSVHelper.storeToCSV(CSVHelper.CSV_Wifi, "after sending " + dataType + " data by postJSON, result : ", result);

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d(TAG, "get http post result : " + result);
        }

    }

    public HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {

        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };

    public String postJSON (String address, String json, String dataType, String lastSyncTime) {

        Log.d(TAG, "[postJSON] testbackend post availSite to " + address);

        InputStream inputStream = null;
        String result = "";
        HttpURLConnection conn = null;
        try {

            URL url = new URL(address);
            conn = (HttpURLConnection) url.openConnection();
            Log.d(TAG, "[postJSON] testbackend connecting to " + address);

            if (url.getProtocol().toLowerCase().equals("https")) {
                Log.d(TAG, "[postJSON] [using https]");
                trustAllHosts();
                HttpsURLConnection https = (HttpsURLConnection) url.openConnection();
                https.setHostnameVerifier(DO_NOT_VERIFY);
                conn = https;
            } else {
                conn = (HttpURLConnection) url.openConnection();
            }

            SSLContext sc;
            sc = SSLContext.getInstance("TLS");
            sc.init(null, null, new java.security.SecureRandom());

            //TODO testing to solve the SocketTimeoutException issue
            conn.setReadTimeout(HTTP_TIMEOUT);
            conn.setConnectTimeout(SOCKET_TIMEOUT);

            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            //TODO might need to use long instead of int is for the larger size but restricted to the api level should over 19
            conn.setFixedLengthStreamingMode(json.getBytes().length);
            conn.setRequestProperty("Content-Type","application/json");
            conn.connect();

            OutputStreamWriter wr= new OutputStreamWriter(conn.getOutputStream());
            wr.write(json);
            wr.close();

            Log.d(TAG, "Post:\t" + dataType + "\t" + "for lastSyncTime:" + lastSyncTime);

            int responseCode = conn.getResponseCode();

            if(responseCode != HttpsURLConnection.HTTP_OK){

                CSVHelper.storeToCSV(CSVHelper.CSV_Wifi, "fail to connect to the server, error code: "+responseCode);
                CSVHelper.storeToCSV(CSVHelper.CSV_Wifi, "going to throw IOException");

                throw new IOException("HTTP error code: " + responseCode);
            } else {

                CSVHelper.storeToCSV(CSVHelper.CSV_Wifi, "connected to the server successfully");

                inputStream = conn.getInputStream();
            }
            result = convertInputStreamToString(inputStream);

            Log.d(TAG, "[postJSON] the result response code is " + responseCode);
            Log.d(TAG, "[postJSON] the result is " + result);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            Log.d(TAG, "NoSuchAlgorithmException", e);
        } catch (KeyManagementException e) {
            e.printStackTrace();
            Log.d(TAG, "KeyManagementException", e);
        } catch (ProtocolException e) {
            e.printStackTrace();
            Log.d(TAG, "ProtocolException", e);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.d(TAG, "MalformedURLException", e);
        } catch (java.net.SocketTimeoutException e){

            Log.d(TAG, "SocketTimeoutException EE", e);
        } catch (IOException e) {
            e.printStackTrace();

        //    CSVHelper.storeToCSV(CSVHelper.CSV_Wifi, "IOException", Utils.getStackTrace(e));
        }finally {

            CSVHelper.storeToCSV(CSVHelper.CSV_Wifi, "connection is null ? "+(conn != null));

            if (conn != null) {

                CSVHelper.storeToCSV(CSVHelper.CSV_Wifi, "going to disconnect");

                conn.disconnect();

                CSVHelper.storeToCSV(CSVHelper.CSV_Wifi, "disconnected successfully");
            }
        }

        return result;
    }

    private String convertInputStreamToString(InputStream inputStream) throws IOException{

        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null){
//            Log.d(LOG_TAG, "[syncWithRemoteDatabase] " + line);
            result += line;
        }

        inputStream.close();
        return result;

    }

    private void trustAllHosts() {

        X509TrustManager easyTrustManager = new X509TrustManager() {

            public void checkClientTrusted(
                    X509Certificate[] chain,
                    String authType) throws CertificateException {
            }

            public void checkServerTrusted(
                    X509Certificate[] chain,
                    String authType) throws CertificateException {
            }

            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

        };

        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[] {easyTrustManager};

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("TLS");

            sc.init(null, trustAllCerts, new java.security.SecureRandom());

            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    private ArrayList<JSONObject> getSessionData(long time24HrAgo){
//
//        Log.d(TAG, "getSessionData");
//
//        ArrayList<JSONObject> sessionJsons = new ArrayList<>();
//
//        ArrayList<String> overTimeSessions = DBHelper.querySessions(time24HrAgo);
//
//        Log.d(TAG, "unsentSessions size : "+ overTimeSessions.size());
//
//        for(int index = 0; index < overTimeSessions.size(); index++){
//
//            try {
//
//                JSONObject sessionJson = new JSONObject();
//
//                String eachData = overTimeSessions.get(index);
//
//                Session sessionToSend = SessionManager.convertStringToSession(eachData);
//
//                //we can't call "_id" because of MongoDB, it will have its own ?
//
//                sessionJson.put("_id", Constants.DEVICE_ID+"_"+sessionToSend.getCreatedTime());
//                sessionJson.put("device_id", Constants.DEVICE_ID);
//                sessionJson.put("condition", currentCondition);
//                sessionJson.put("createdTime", String.valueOf(sessionToSend.getCreatedTime()));
//                sessionJson.put("startTime", String.valueOf(sessionToSend.getStartTime()));
//                sessionJson.put("endTime", String.valueOf(sessionToSend.getEndTime()));
//                sessionJson.put("startTimeString", ScheduleAndSampleManager.getTimeString(sessionToSend.getStartTime()));
//                sessionJson.put("endTimeString", ScheduleAndSampleManager.getTimeString(sessionToSend.getEndTime()));
//                sessionJson.put("sessionid", sessionToSend.getId());
//                sessionJson.put("detected_Type", sessionToSend.getType());
//                sessionJson.put("annotations", getAnnotationSetIntoJson(sessionToSend.getAnnotationsSet()));
//
//                sessionJsons.add(sessionJson);
//            }catch (JSONException e){
//
//                Log.e(TAG, "exception", e);
//            }
//        }
//
//        return sessionJsons;
//    }

//    private JSONObject getAnnotationSetIntoJson(AnnotationSet annotationSet) throws JSONException{
//
//
//        JSONObject annotationSetJson = new JSONObject();
//
//        ArrayList<Annotation> detected_transportation = annotationSet.getAnnotationByTag(Constants.ANNOTATION_TAG_DETECTED_TRANSPORTATION_ACTIVITY);
//
//        String detected_transportationInString = getLatestAnnotation(detected_transportation);
//
//        Log.d(TAG, "detected_transportationInString : "+detected_transportationInString);
//
//        annotationSetJson.put(Constants.ANNOTATION_TAG_DETECTED_TRANSPORTATION_ACTIVITY, detected_transportationInString);
//
//
//        ArrayList<Annotation> detected_sitename = annotationSet.getAnnotationByTag(Constants.ANNOTATION_TAG_DETECTED_SITENAME);
//
//        String detected_sitenameInString = getLatestAnnotation(detected_sitename);
//
//        Log.d(TAG, "detected_sitenameInString : "+detected_sitenameInString);
//
//        annotationSetJson.put(Constants.ANNOTATION_TAG_DETECTED_SITENAME, detected_sitenameInString);
//
//
//        ArrayList<Annotation> detected_sitelocation = annotationSet.getAnnotationByTag(Constants.ANNOTATION_TAG_DETECTED_SITELOCATION);
//
//        String detected_sitelocationInString = getLatestAnnotation(detected_sitelocation);
//
//        Log.d(TAG, "detected_sitelocationInString : "+detected_sitelocationInString);
//
//        annotationSetJson.put(Constants.ANNOTATION_TAG_DETECTED_SITELOCATION, detected_sitelocationInString);
//
//
//        ArrayList<Annotation> labels = annotationSet.getAnnotationByTag(Constants.ANNOTATION_TAG_Label);
//
//        JSONArray labelsInJSONArray = getLabelsAnnotation(labels);
//
//        annotationSetJson.put(Constants.ANNOTATION_TAG_Label, labelsInJSONArray);
//
//        Log.d(TAG, "labels in json : "+annotationSetJson);
//
//
//        return annotationSetJson;
//    }

    private String getLatestAnnotation(ArrayList<Annotation> annotationArrayList){

        if(annotationArrayList.size() == 0)
            return "";

        return annotationArrayList.get(annotationArrayList.size()-1).getContent();
    }

    private JSONArray getLabelsAnnotation(ArrayList<Annotation> annotationArrayList) throws JSONException{

        if(annotationArrayList.size() == 0)
            return new JSONArray();

        JSONArray labels = new JSONArray();

        for(Annotation annotation : annotationArrayList) {

            labels.put(new JSONObject(annotation.getContent()));
        }

        Log.d(TAG, "labels : "+labels);

        return labels;
    }

    private void storeTransporatation(JSONObject data){

        Log.d(TAG, "storeTransporatation");

        try {

            JSONObject transportationAndtimestampsJson = new JSONObject();

            JSONArray transportations = new JSONArray();
            JSONArray timestamps = new JSONArray();


            Cursor transCursor =  db.transportationModeDataRecordDao().getRecordBetweenTimes(startTime,endTime);
            int rows = transCursor.getCount();


            if(rows!=0){
                transCursor.moveToFirst();
                for(int i=0;i<rows;i++) {
                    String timestamp = transCursor.getString(1);
                    String transportation = transCursor.getString(2);

                    Log.d(TAG,"transportation : "+transportation+" timestamp : "+timestamp);

                    transportations.put(transportation);
                    timestamps.put(timestamp);

                    transCursor.moveToNext();
                }

                transportationAndtimestampsJson.put("Transportation",transportations);
                transportationAndtimestampsJson.put("timestamps",timestamps);

                data.put("TransportationMode",transportationAndtimestampsJson);

            }else
                noDataFlag1 = true;

        }catch (JSONException e){
            e.printStackTrace();
        }catch(NullPointerException e){
            e.printStackTrace();
        }

        Log.d(TAG,"data : "+ data.toString());

    }

    private void deleteTransporatation(Long startTime, Long endTime) {
        Log.d(TAG, "deleteTransporatation");
        db.transportationModeDataRecordDao().deleteRecordBetweenTimes(startTime,endTime);
    }


    private void storeLocation(JSONObject data){

        try {

            JSONObject locationAndtimestampsJson = new JSONObject();

            JSONArray accuracys = new JSONArray();
            JSONArray longtitudes = new JSONArray();
            JSONArray latitudes = new JSONArray();
            JSONArray timestamps = new JSONArray();
            JSONArray Provider_cols = new JSONArray();

            Cursor transCursor =  db.locationDataRecordDao().getRecordBetweenTimes(startTime,endTime);

            Log.d(TAG,"SELECT * FROM locationDataRecordDao WHERE BETWEEN"+startTime+"' "+"AND"+" '"+endTime+"' ");

            int rows = transCursor.getCount();

            Log.d(TAG, "rows : "+rows);

            if(rows!=0){
                transCursor.moveToFirst();
                for(int i=0;i<rows;i++) {
                    String timestamp = transCursor.getString(1);
                    String latitude = transCursor.getString(3);
                    String longtitude = transCursor.getString(4);
                    String accuracy = transCursor.getString(5);



                   Log.d(TAG,"timestamp : "+timestamp+" latitude : "+latitude+" longtitude : "+longtitude+" accuracy : "+accuracy);

                    accuracys.put(accuracy);
                    longtitudes.put(longtitude);
                    latitudes.put(latitude);
                    timestamps.put(timestamp);

                    transCursor.moveToNext();
                }

                locationAndtimestampsJson.put("Accuracy",accuracys);
                locationAndtimestampsJson.put("Longtitudes",longtitudes);
                locationAndtimestampsJson.put("Latitudes",latitudes);
                locationAndtimestampsJson.put("timestamps",timestamps);
                locationAndtimestampsJson.put("Provider_cols",Provider_cols);

                data.put("Location",locationAndtimestampsJson);

            }else
                noDataFlag2 = true;

        }catch (JSONException e){
            e.printStackTrace();
        }catch(NullPointerException e){
            e.printStackTrace();
        }

        Log.d(TAG,"data : "+ data.toString());

    }

    private void deleteLocation(Long startTime, Long endTime) {
        Log.d(TAG, "deleteLocation");
        db.locationDataRecordDao().deleteRecordBetweenTimes(startTime,endTime);

    }

    private void storeActivityRecognition(JSONObject data){
        try {

            JSONObject arAndtimestampsJson = new JSONObject();

            JSONArray mostProbableActivityz = new JSONArray();
            JSONArray probableActivitiesz = new JSONArray();
            JSONArray pDetectedtimez = new JSONArray();

            JSONArray timestamps = new JSONArray();


            Cursor transCursor = db.activityRecognitionDataRecordDao().getRecordBetweenTimes(startTime,endTime);
            int rows = transCursor.getCount();

            if(rows!=0){
                transCursor.moveToFirst();
                for(int i=0;i<rows;i++) {
                    String timestamp = transCursor.getString(1);
                    String mostProbableActivity = transCursor.getString(2);
                    String probableActivities = transCursor.getString(3);
                    String Detectedtime = transCursor.getString(4);
//                    Log.d(TAG,"timestamp : "+timestamp+" mostProbableActivity : "+mostProbableActivity+" probableActivities : "+probableActivities);

                    mostProbableActivityz.put(mostProbableActivity);
                    probableActivitiesz.put(probableActivities);
                    pDetectedtimez.put(Detectedtime);
                    timestamps.put(timestamp);

                    transCursor.moveToNext();
                }

                arAndtimestampsJson.put("MostProbableActivity",mostProbableActivityz);
                arAndtimestampsJson.put("ProbableActivities",probableActivitiesz);
                arAndtimestampsJson.put("Detectedtime",pDetectedtimez);

                arAndtimestampsJson.put("timestamps",timestamps);

                data.put("ActivityRecognition",arAndtimestampsJson);

            }else
                noDataFlag3 = true;

        }catch (JSONException e){
            e.printStackTrace();
        }catch(NullPointerException e){
            e.printStackTrace();
        }

        Log.d(TAG,"data : "+ data.toString());

    }

    private void deleteActivityRecognition(Long startTime, Long endTime) {

        Log.d(TAG, "deleteActivityRecognition");
        db.activityRecognitionDataRecordDao().deleteRecordBetweenTimes(startTime,endTime);
       }

    private void storeRinger(JSONObject data){

        Log.d(TAG, "storeRinger");

        try {

            JSONObject ringerAndtimestampsJson = new JSONObject();

            JSONArray StreamVolumeSystems = new JSONArray();
            JSONArray StreamVolumeVoicecalls = new JSONArray();
            JSONArray StreamVolumeRings = new JSONArray();
            JSONArray StreamVolumeNotifications = new JSONArray();
            JSONArray StreamVolumeMusics = new JSONArray();
            JSONArray AudioModes = new JSONArray();
            JSONArray RingerModes = new JSONArray();
            JSONArray timestamps = new JSONArray();


            Cursor transCursor = db.ringerDataRecordDao().getRecordBetweenTimes(startTime,endTime);

            int rows = transCursor.getCount();

//            Log.d(TAG, "rows : "+rows);

            if(rows!=0){
                transCursor.moveToFirst();
                for(int i=0;i<rows;i++) {
                    String timestamp = transCursor.getString(1);
                    String RingerMode = transCursor.getString(2);
                    String AudioMode = transCursor.getString(3);
                    String StreamVolumeMusic = transCursor.getString(4);
                    String StreamVolumeNotification = transCursor.getString(5);
                    String StreamVolumeRing = transCursor.getString(6);
                    String StreamVolumeVoicecall = transCursor.getString(7);
                    String StreamVolumeSystem = transCursor.getString(8);

                    Log.d(TAG,"timestamp : "+timestamp+" RingerMode : "+RingerMode+" AudioMode : "+AudioMode+
                            " StreamVolumeMusic : "+StreamVolumeMusic+" StreamVolumeNotification : "+StreamVolumeNotification
                            +" StreamVolumeRing : "+StreamVolumeRing +" StreamVolumeVoicecall : "+StreamVolumeVoicecall
                            +" StreamVolumeSystem : "+StreamVolumeSystem);

                    StreamVolumeSystems.put(StreamVolumeSystem);
                    StreamVolumeVoicecalls.put(StreamVolumeVoicecall);
                    StreamVolumeRings.put(StreamVolumeRing);
                    StreamVolumeNotifications.put(StreamVolumeNotification);
                    StreamVolumeMusics.put(StreamVolumeMusic);
                    AudioModes.put(AudioMode);
                    RingerModes.put(RingerMode);
                    timestamps.put(timestamp);

                    transCursor.moveToNext();
                }

                ringerAndtimestampsJson.put("RingerMode",RingerModes);
                ringerAndtimestampsJson.put("AudioMode",AudioModes);
                ringerAndtimestampsJson.put("StreamVolumeMusic",StreamVolumeMusics);
                ringerAndtimestampsJson.put("StreamVolumeNotification",StreamVolumeNotifications);
                ringerAndtimestampsJson.put("StreamVolumeRing",StreamVolumeRings);
                ringerAndtimestampsJson.put("StreamVolumeVoicecall",StreamVolumeVoicecalls);
                ringerAndtimestampsJson.put("StreamVolumeSystem",StreamVolumeSystems);
                ringerAndtimestampsJson.put("timestamps",timestamps);

                data.put("Ringer",ringerAndtimestampsJson);

            }else
                noDataFlag4 = true;

        }catch (JSONException e){
            e.printStackTrace();
        }catch(NullPointerException e){
            e.printStackTrace();
        }

        Log.d(TAG,"data : "+ data.toString());

    }

    private void deleteRinger(Long startTime, Long endTime) {
        Log.d(TAG, "deleteActivityRecognition");
        db.ringerDataRecordDao().deleteRecordBetweenTimes(startTime,endTime);

    }

    private void storeConnectivity(JSONObject data){

        try {

            JSONObject connectivityAndtimestampsJson = new JSONObject();

            JSONArray IsMobileConnecteds = new JSONArray();
            JSONArray IsWifiConnecteds = new JSONArray();
            JSONArray IsMobileAvailables = new JSONArray();
            JSONArray IsWifiAvailables = new JSONArray();
            JSONArray IsConnecteds = new JSONArray();
            JSONArray IsNetworkAvailables = new JSONArray();
            JSONArray NetworkTypes = new JSONArray();
            JSONArray timestamps = new JSONArray();


            Cursor transCursor = db.connectivityDataRecordDao().getRecordBetweenTimes(startTime,endTime);

            int rows = transCursor.getCount();

            if(rows!=0){
                transCursor.moveToFirst();
                for(int i=0;i<rows;i++) {
                    String timestamp = transCursor.getString(1);
                    String NetworkType = transCursor.getString(2);
                    String IsNetworkAvailable = transCursor.getString(3);
                    String IsConnected = transCursor.getString(4);
                    String IsWifiAvailable = transCursor.getString(5);
                    String IsMobileAvailable = transCursor.getString(6);
                    String IsWifiConnected = transCursor.getString(7);
                    String IsMobileConnected = transCursor.getString(8);
//
//                    Log.d(TAG,"timestamp : "+timestamp+" NetworkType : "+NetworkType+" IsNetworkAvailable : "+IsNetworkAvailable
//                            +" IsConnected : "+IsConnected+" IsWifiAvailable : "+IsWifiAvailable
//                            +" IsMobileAvailable : "+IsMobileAvailable +" IsWifiConnected : "+IsWifiConnected
//                            +" IsMobileConnected : "+IsMobileConnected);

                    IsMobileConnecteds.put(IsMobileConnected);
                    IsWifiConnecteds.put(IsWifiConnected);
                    IsMobileAvailables.put(IsMobileAvailable);
                    IsWifiAvailables.put(IsWifiAvailable);
                    IsConnecteds.put(IsConnected);
                    IsNetworkAvailables.put(IsNetworkAvailable);
                    NetworkTypes.put(NetworkType);
                    timestamps.put(timestamp);

                    transCursor.moveToNext();
                }

                connectivityAndtimestampsJson.put("NetworkType",NetworkTypes);
                connectivityAndtimestampsJson.put("IsNetworkAvailable",IsNetworkAvailables);
                connectivityAndtimestampsJson.put("IsConnected",IsConnecteds);
                connectivityAndtimestampsJson.put("IsWifiAvailable",IsWifiAvailables);
                connectivityAndtimestampsJson.put("IsMobileAvailable",IsMobileAvailables);
                connectivityAndtimestampsJson.put("IsWifiConnected",IsWifiConnecteds);
                connectivityAndtimestampsJson.put("IsMobileConnected",IsMobileConnecteds);
                connectivityAndtimestampsJson.put("timestamps",timestamps);

                data.put("Connectivity",connectivityAndtimestampsJson);

            }else
                noDataFlag5 = true;

        }catch (JSONException e){
            e.printStackTrace();
        }catch(NullPointerException e){
            e.printStackTrace();
        }

        Log.d(TAG,"data : "+ data.toString());

    }

    private void deleteConnectivity(Long startTime, Long endTime) {
        Log.d(TAG, "deleteConnectivity");
        db.connectivityDataRecordDao().deleteRecordBetweenTimes(startTime,endTime);

    }

    private void storeBattery(JSONObject data){

        Log.d(TAG, "storeBattery");

        try {

            JSONObject batteryAndtimestampsJson = new JSONObject();

            JSONArray BatteryLevels = new JSONArray();
            JSONArray BatteryPercentages = new JSONArray();
            JSONArray BatteryChargingStates = new JSONArray();
            JSONArray isChargings = new JSONArray();
            JSONArray timestamps = new JSONArray();


            Cursor transCursor = db.batteryDataRecordDao().getRecordBetweenTimes(startTime,endTime);

            int rows = transCursor.getCount();

//            Log.d(TAG, "rows : "+rows);

            if(rows!=0){
                transCursor.moveToFirst();
                for(int i=0;i<rows;i++) {
                    String timestamp = transCursor.getString(1);
                    String BatteryLevel = transCursor.getString(2);
                    String BatteryPercentage = transCursor.getString(3);
                    String BatteryChargingState = transCursor.getString(4);
                    String isCharging = transCursor.getString(5);

//                    Log.d(TAG,"timestamp : "+timestamp+" BatteryLevel : "+BatteryLevel+" BatteryPercentage : "+
//                            BatteryPercentage+" BatteryChargingState : "+BatteryChargingState+" isCharging : "+isCharging);

                    BatteryLevels.put(BatteryLevel);
                    BatteryPercentages.put(BatteryPercentage);
                    BatteryChargingStates.put(BatteryChargingState);
                    isChargings.put(isCharging);
                    timestamps.put(timestamp);

                    transCursor.moveToNext();
                }

                batteryAndtimestampsJson.put("BatteryLevel",BatteryLevels);
                batteryAndtimestampsJson.put("BatteryPercentage",BatteryPercentages);
                batteryAndtimestampsJson.put("BatteryChargingState",BatteryChargingStates);
                batteryAndtimestampsJson.put("isCharging",isChargings);
                batteryAndtimestampsJson.put("timestamps",timestamps);

                data.put("Battery",batteryAndtimestampsJson);

            }else
                noDataFlag6 = true;

        }catch (JSONException e){
            e.printStackTrace();
        }catch(NullPointerException e){
            e.printStackTrace();
        }

//        Log.d(TAG,"data : "+ data.toString());

    }

    private void deleteBattery(Long startTime, Long endTime) {
//        Log.d(TAG, "deleteBattery");
//        Log.d(TAG, DBHelper.TIME+" BETWEEN"+" '"+startTime+"' "+"AND"+" '"+endTime + "'");
        Log.d(TAG, "deleteBattery");
        db.batteryDataRecordDao().deleteRecordBetweenTimes(startTime,endTime);
    }

    private void storeAppUsage(JSONObject data){

        Log.d(TAG, "storeAppUsage");

        try {

            JSONObject appUsageAndtimestampsJson = new JSONObject();

            JSONArray ScreenStatusz = new JSONArray();
            JSONArray Latest_Used_Apps = new JSONArray();
            JSONArray Latest_Foreground_Activitys = new JSONArray();
            JSONArray timestamps = new JSONArray();


            Cursor transCursor = db.appUsageDataRecordDao().getRecordBetweenTimes(startTime,endTime);
            int rows = transCursor.getCount();

//            Log.d(TAG, "rows : "+rows);

            if(rows!=0){
                transCursor.moveToFirst();
                for(int i=0;i<rows;i++) {
                    String timestamp = transCursor.getString(1);
                    String ScreenStatus = transCursor.getString(2);
                    String Latest_Used_App = transCursor.getString(3);
                    String Latest_Foreground_Activity = transCursor.getString(4);

//                    Log.d(TAG,"timestamp : "+timestamp+" ScreenStatus : "+ScreenStatus+" Latest_Used_App : "+Latest_Used_App+" Latest_Foreground_Activity : "+Latest_Foreground_Activity);

                    ScreenStatusz.put(ScreenStatus);
                    Latest_Used_Apps.put(Latest_Used_App);
                    Latest_Foreground_Activitys.put(Latest_Foreground_Activity);
                    timestamps.put(timestamp);

                    transCursor.moveToNext();
                }

                appUsageAndtimestampsJson.put("ScreenStatus",ScreenStatusz);
                appUsageAndtimestampsJson.put("Latest_Used_App",Latest_Used_Apps);
//                appUsageAndtimestampsJson.put("Latest_Foreground_Activity",Latest_Foreground_Activitys);
                appUsageAndtimestampsJson.put("timestamps",timestamps);

                data.put("AppUsage",appUsageAndtimestampsJson);

            }else
                noDataFlag7 = true;

        }catch (JSONException e){
            e.printStackTrace();
        }catch(NullPointerException e){
            e.printStackTrace();
        }

//        Log.d(TAG,"data : "+ data.toString());

    }

    private void deleteAppUsage(Long startTime, Long endTime) {

        Log.d(TAG, "deleteAppUsage");
        db.appUsageDataRecordDao().deleteRecordBetweenTimes(startTime,endTime);

    }

    private void storeTelephony(JSONObject data){

//        Log.d(TAG, "storeTelephony");

        try {

            JSONObject telephonyAndtimestampsJson = new JSONObject();

            JSONArray NetworkOperatorNames = new JSONArray();
            JSONArray CallStates = new JSONArray();
            JSONArray PhoneSignalTypes = new JSONArray();
            JSONArray GsmSignalStrengths = new JSONArray();
            JSONArray LTESignalStrengths = new JSONArray();
            JSONArray CdmaSignalStrengthLevels = new JSONArray();
            JSONArray timestamps = new JSONArray();


            Cursor transCursor = db.telephonyDataRecordDao().getRecordBetweenTimes(startTime,endTime);

            int rows = transCursor.getCount();

//            Log.d(TAG, "rows : "+rows);

            if(rows!=0){
                transCursor.moveToFirst();
                for(int i=0;i<rows;i++) {
                    String timestamp = transCursor.getString(1);
                    String NetworkOperatorName = transCursor.getString(2);
                    String CallState = transCursor.getString(3);
                    String PhoneSignalType = transCursor.getString(4);
                    String GsmSignalStrength = transCursor.getString(5);
                    String LTESignalStrength = transCursor.getString(6);
                    String CdmaSignalStrengthLevel = transCursor.getString(7);

//                    Log.d(TAG,"timestamp : "+timestamp+" NetworkOperatorName : "+NetworkOperatorName+" CallState : "+CallState+" PhoneSignalType : "+PhoneSignalType+" GsmSignalStrength : "+GsmSignalStrength+" LTESignalStrength : "+LTESignalStrength+" CdmaSignalStrengthLevel : "+CdmaSignalStrengthLevel );


                    NetworkOperatorNames.put(NetworkOperatorName);
                    CallStates.put(CallState);
                    PhoneSignalTypes.put(PhoneSignalType);
                    GsmSignalStrengths.put(GsmSignalStrength);
                    LTESignalStrengths.put(LTESignalStrength);
                    CdmaSignalStrengthLevels.put(CdmaSignalStrengthLevel);
                    timestamps.put(timestamp);
                    transCursor.moveToNext();
                }

                telephonyAndtimestampsJson.put("NetworkOperatorName",NetworkOperatorNames);
                telephonyAndtimestampsJson.put("CallState",CallStates);
                telephonyAndtimestampsJson.put("PhoneSignalType",PhoneSignalTypes);
                telephonyAndtimestampsJson.put("GsmSignalStrength",GsmSignalStrengths);
                telephonyAndtimestampsJson.put("LTESignalStrength",LTESignalStrengths);
                telephonyAndtimestampsJson.put("CdmaSignalStrengthLevel",CdmaSignalStrengthLevels);
                telephonyAndtimestampsJson.put("timestamp",timestamps);

                data.put("Telephony",telephonyAndtimestampsJson);

            }else
                noDataFlag7 = true;

        }catch (JSONException e){
            e.printStackTrace();
        }catch(NullPointerException e){
            e.printStackTrace();
        }

//        Log.d(TAG,"data : "+ data.toString());

    }

    private void deleteTelephony(Long startTime, Long endTime) {
//        Log.d(TAG, "deleteAppUsage");
//        Log.d(TAG, DBHelper.TIME+" BETWEEN"+" '"+startTime+"' "+"AND"+" '"+endTime + "'");
        Log.d(TAG, "deleteTelephony");
        db.telephonyDataRecordDao().deleteRecordBetweenTimes(startTime,endTime);
    }

    private void storeSensor(JSONObject data){

//        Log.d(TAG, "storeSensor");

        try {

            JSONObject sensorAndtimestampsJson = new JSONObject();

            JSONArray ACCELEROMETERs = new JSONArray();
            JSONArray GYROSCOPEs = new JSONArray();
            JSONArray GRAVITYs = new JSONArray();
            JSONArray LINEAR_ACCELERATIONs = new JSONArray();
            JSONArray ROTATION_VECTORs = new JSONArray();
            JSONArray PROXIMITYs = new JSONArray();
            JSONArray MAGNETIC_FIELDs = new JSONArray();
            JSONArray LIGHTs = new JSONArray();
            JSONArray PRESSUREs = new JSONArray();
            JSONArray RELATIVE_HUMIDITYs = new JSONArray();
            JSONArray AMBIENT_TEMPERATUREs = new JSONArray();
            JSONArray timestamps = new JSONArray();


            Cursor transCursor = db.sensorDataRecordDao().getRecordBetweenTimes(startTime,endTime);

            int rows = transCursor.getCount();

//            Log.d(TAG, "rows : "+rows);

            if(rows!=0){
                transCursor.moveToFirst();
                for(int i=0;i<rows;i++) {
                    String timestamp = transCursor.getString(1);
                    String ACCELEROMETER = transCursor.getString(2);
                    String GYROSCOPE = transCursor.getString(3);
                    String LINEAR_ACCELERATION = transCursor.getString(4);
                    String ROTATION_VECTOR = transCursor.getString(5);
                    String PROXIMITY = transCursor.getString(6);
                    String LIGHT = transCursor.getString(7);
                    String PRESSURE = transCursor.getString(8);
                    String RELATIVE_HUMIDITY = transCursor.getString(9);
                    String AMBIENT_TEMPERATURE = transCursor.getString(10);


//
//                    Log.d(TAG,"timestamp : "+timestamp+" ACCELEROMETER : "+ACCELEROMETER+
//                            " GYROSCOPE : "+GYROSCOPE+" LINEAR_ACCELERATION : "+LINEAR_ACCELERATION+
//                            " ROTATION_VECTOR : " +ROTATION_VECTOR+" PROXIMITY : "+PROXIMITY+" MAGNETIC_FIELD : " +MAGNETIC_FIELD +
//                            " LIGHT : " +LIGHT+" PRESSURE : "+PRESSURE+" RELATIVE_HUMIDITY : " +RELATIVE_HUMIDITY+
//                            " AMBIENT_TEMPERATURE : " +AMBIENT_TEMPERATURE
//                    );


                    ACCELEROMETERs.put(ACCELEROMETER);
                    GYROSCOPEs.put(GYROSCOPE);

                    LINEAR_ACCELERATIONs.put(LINEAR_ACCELERATION);
                    ROTATION_VECTORs.put(ROTATION_VECTOR);
                    PROXIMITYs.put(PROXIMITY);

                    LIGHTs.put(LIGHT);
                    PRESSUREs.put(PRESSURE);
                    RELATIVE_HUMIDITYs.put(RELATIVE_HUMIDITY);
                    AMBIENT_TEMPERATUREs.put(AMBIENT_TEMPERATURE);
                    timestamps.put(timestamp);

                    transCursor.moveToNext();
                }

                sensorAndtimestampsJson.put("ACCELEROMETER",ACCELEROMETERs);
                sensorAndtimestampsJson.put("GYROSCOPE",GYROSCOPEs);
                sensorAndtimestampsJson.put("LINEAR_ACCELERATION",LINEAR_ACCELERATIONs);
                sensorAndtimestampsJson.put("ROTATION_VECTOR",ROTATION_VECTORs);
                sensorAndtimestampsJson.put("PROXIMITY",PROXIMITYs);
                sensorAndtimestampsJson.put("LIGHT",LIGHTs);
                sensorAndtimestampsJson.put("PRESSURE",PRESSUREs);
                sensorAndtimestampsJson.put("RELATIVE_HUMIDITY",RELATIVE_HUMIDITYs);
                sensorAndtimestampsJson.put("AMBIENT_TEMPERATURE",AMBIENT_TEMPERATUREs);
                sensorAndtimestampsJson.put("timestamp",timestamps);

                data.put("Sensor",sensorAndtimestampsJson);

            }else
                noDataFlag7 = true;

        }catch (JSONException e){
            e.printStackTrace();
        }catch(NullPointerException e){
            e.printStackTrace();
        }

//        Log.d(TAG,"data : "+ data.toString());

    }

    private void deleteSensor(Long startTime, Long endTime) {
//        Log.d(TAG, "deleteAppUsage");
//        Log.d(TAG, DBHelper.TIME+" BETWEEN"+" '"+startTime+"' "+"AND"+" '"+endTime + "'");
        Log.d(TAG, "deleteSensor");
        db.sensorDataRecordDao().deleteRecordBetweenTimes(startTime,endTime);

    }

    private void storeAccessibility(JSONObject data){

        Log.d(TAG, "storeAppUsage");

        try {

            JSONObject appUsageAndtimestampsJson = new JSONObject();

            JSONArray packs = new JSONArray();
            JSONArray texts = new JSONArray();
            JSONArray types = new JSONArray();
            JSONArray extras = new JSONArray();
            JSONArray contents = new JSONArray();
            JSONArray mcids = new JSONArray();

            JSONArray timestamps = new JSONArray();


            Cursor transCursor =  db.accessibilityDataRecordDao().getRecordBetweenTimes(startTime,endTime);

             int rows = transCursor.getCount();
        //    JSONObject a_data = new JSONObject();
          //  Amplitude.getInstance().logEvent("storeAccessibility", a_data.put("row", rows));

            Log.d(TAG, "rows : "+rows);

            if(rows!=0){
                transCursor.moveToFirst();
                for(int i=0;i<rows;i++) {
                    String timestamp = transCursor.getString(1);
                    String pack = transCursor.getString(2);
                    String text = transCursor.getString(3);
                    String type = transCursor.getString(4);
                    String extra = transCursor.getString(5);
                    String content = transCursor.getString(6);
                    Integer mcid = transCursor.getInt(7);
//                    Log.d(TAG,"timestamp : "+timestamp+" pack : "+pack+" text : "+text+" type : "+type+" extra : "+extra);

                    packs.put(pack);
                    texts.put(text);
                    types.put(type);
                    extras.put(extra);
                    contents.put(content);
                    mcids.put(mcid);

                    timestamps.put(timestamp);

                    transCursor.moveToNext();
                }

                appUsageAndtimestampsJson.put("pack",packs);
                appUsageAndtimestampsJson.put("text",texts);
                appUsageAndtimestampsJson.put("type",types);
                appUsageAndtimestampsJson.put("extra",extras);
//                appUsageAndtimestampsJson.put("Latest_Foreground_Activity",Latest_Foreground_Activitys);
                appUsageAndtimestampsJson.put("content",contents);
                appUsageAndtimestampsJson.put("mcid",mcids);
                appUsageAndtimestampsJson.put("timestamp",timestamps);

                data.put("Accessibility",appUsageAndtimestampsJson);

            }else
                noDataFlag7 = true;

        }catch (JSONException e){
            e.printStackTrace();
        }catch(NullPointerException e){
            e.printStackTrace();
        }

        Log.d(TAG,"data : "+ data.toString());

    }

    private void deleteAccessibility(Long startTime, Long endTime) {
        Log.d(TAG, "deleteAccessibility");
        db.accessibilityDataRecordDao().deleteRecordBetweenTimes(startTime,endTime);
       }

    private void storeNotification(JSONObject data){

        Log.d(TAG, "storeNotification");

        try {

            JSONObject appUsageAndtimestampsJson = new JSONObject();

            JSONArray title_cols = new JSONArray();
            JSONArray n_text_cols = new JSONArray();
            JSONArray subText_cols = new JSONArray();
            JSONArray tickerText_cols = new JSONArray();
            JSONArray app_cols = new JSONArray();
            JSONArray sendForm_cols = new JSONArray();
            JSONArray longitude_cols = new JSONArray();
            JSONArray latitude_cols = new JSONArray();
            JSONArray timestamps = new JSONArray();


            Cursor transCursor = db.notificationDataRecordDao().getRecordBetweenTimes(startTime,endTime);

            int rows = transCursor.getCount();

            Log.d(TAG, "rows : "+rows);

            if(rows!=0){
                transCursor.moveToFirst();
                for(int i=0;i<rows;i++) {
                    String timestamp = transCursor.getString(1);
                    String title_col = transCursor.getString(2);
                    String n_text_col = transCursor.getString(3);
                    String subText_col = transCursor.getString(4);
                    String tickerText_col = transCursor.getString(5);
                    String app_col = transCursor.getString(6);

//                    Log.d(TAG,"timestamp : "+timestamp+" pack : "+pack+" text : "+text+" type : "+type+" extra : "+extra);

                    title_cols.put(title_col);
                    n_text_cols.put(n_text_col);
                    subText_cols.put(subText_col);
                    tickerText_cols.put(tickerText_col);
                    app_cols.put(app_col);
                    timestamps.put(timestamp);

                    transCursor.moveToNext();
                }

                appUsageAndtimestampsJson.put("title_cols",title_cols);
                appUsageAndtimestampsJson.put("n_text_cols",n_text_cols);
                appUsageAndtimestampsJson.put("subText_cols",subText_cols);
                appUsageAndtimestampsJson.put("tickerText_cols",tickerText_cols);
                appUsageAndtimestampsJson.put("app_cols",app_cols);

//                appUsageAndtimestampsJson.put("Latest_Foreground_Activity",Latest_Foreground_Activitys);
                appUsageAndtimestampsJson.put("timestamps",timestamps);

                data.put("Notification",appUsageAndtimestampsJson);

            }else
                noDataFlag7 = true;

        }catch (JSONException e){
            e.printStackTrace();
        }catch(NullPointerException e){
            e.printStackTrace();
        }

        Log.d(TAG,"data : "+ data.toString());

    }

    private void deleteNotification(Long startTime, Long endTime) {
        Log.d(TAG, "deleteNotification");
        db.notificationDataRecordDao().deleteRecordBetweenTimes(startTime,endTime);
     }
    private void storeMobileCrowdsource(JSONObject data){

        Log.d(TAG, "storeMobileCrowdsource");

        try {

            JSONObject mobileCrowdsourceAndtimestampsJson = new JSONObject();

          //  JSONArray tasktype_cols = new JSONArray();
            JSONArray app_cols = new JSONArray();
            JSONArray ifSentNoti_cols = new JSONArray();
            JSONArray startTasktime_cols = new JSONArray();
            JSONArray endTasktime_cols = new JSONArray();
            JSONArray userActions_cols = new JSONArray();
            JSONArray accessId_cols = new JSONArray();
            JSONArray creationTime_cols = new JSONArray();


            Cursor transCursor = db.notificationDataRecordDao().getRecordBetweenTimes(startTime,endTime);

            int rows = transCursor.getCount();

            Log.d(TAG, "rows : "+rows);

            if(rows!=0){
                transCursor.moveToFirst();
                for(int i=0;i<rows;i++) {
                    // columne 1 : id
                    String creationTime = transCursor.getString(1);
                  //  String tasktype = transCursor.getString(2);
                    String app = transCursor.getString(2);
                    String ifSentNoti = transCursor.getString(3);
                    String startTasktime = transCursor.getString(4);
                    String endTasktime = transCursor.getString(5);
                    String userActions = transCursor.getString(6);
                    Integer accessId = transCursor.getInt(7);

//                    Log.d(TAG,"timestamp : "+timestamp+" pack : "+pack+" text : "+text+" type : "+type+" extra : "+extra);

             //       tasktype_cols.put(tasktype);
                    app_cols.put(app);
                    ifSentNoti_cols.put(ifSentNoti);
                    startTasktime_cols.put(startTasktime);
                    endTasktime_cols.put(endTasktime);
                    userActions_cols.put(userActions);
                    accessId_cols.put(accessId);
                    creationTime_cols.put(creationTime);

                    transCursor.moveToNext();
                }

            //    mobileCrowdsourceAndtimestampsJson.put("tasktype_cols",tasktype_cols);
                mobileCrowdsourceAndtimestampsJson.put("app_cols",app_cols);
                mobileCrowdsourceAndtimestampsJson.put("ifSentNoti_cols",ifSentNoti_cols);
                mobileCrowdsourceAndtimestampsJson.put("startTasktime_cols",startTasktime_cols);
                mobileCrowdsourceAndtimestampsJson.put("endTasktime_cols",endTasktime_cols);
                mobileCrowdsourceAndtimestampsJson.put("userActions_cols",userActions_cols);
                mobileCrowdsourceAndtimestampsJson.put("accessId_cols",accessId_cols);
                mobileCrowdsourceAndtimestampsJson.put("creationTime_cols",creationTime_cols);
//
                data.put("MobileCrowdsource",mobileCrowdsourceAndtimestampsJson);

            }else
                noDataFlag7 = true;

        }catch (JSONException e){
            e.printStackTrace();
        }catch(NullPointerException e){
            e.printStackTrace();
        }

        Log.d(TAG,"data : "+ data.toString());

    }

    private void deleteMobileCrowdsource(Long startTime, Long endTime) {
        Log.d(TAG, "deleteMobileCrowdsource");
        db.mobileCrowdsourceDataRecordDao().deleteRecordBetweenTimes(startTime,endTime);

    }









    private long getSpecialTimeInMillis(String givenDateFormat){
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_FORMAT_NOW);
        long timeInMilliseconds = 0;
        try {
            Date mDate = sdf.parse(givenDateFormat);
            timeInMilliseconds = mDate.getTime();
            Log.d(TAG,"Date in milli :: " + timeInMilliseconds);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timeInMilliseconds;
    }

    private long getSpecialTimeInMillis(int year,int month,int date,int hour,int min){
//        TimeZone tz = TimeZone.getDefault(); tz
        Calendar cal = Calendar.getInstance();
//        cal.set(year,month,date,hour,min,0);
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, date);
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, min);
        cal.set(Calendar.SECOND, 0);

        long t = cal.getTimeInMillis();

        return t;
    }

    private void storeTripToLocalFolder(JSONObject completedJson){
        Log.d(TAG, "storeTripToLocalFolder");

        String sFileName = "Trip_"+getTimeString(startTime)+"_"+getTimeString(endTime)+".json";

        Log.d(TAG, "sFileName : "+ sFileName);

        try {
            File root = new File(Environment.getExternalStorageDirectory() + PACKAGE_DIRECTORY_PATH);
            if (!root.exists()) {
                root.mkdirs();
            }

            Log.d(TAG, "root : " + root);

            FileWriter fileWriter = new FileWriter(root+sFileName, true);
            fileWriter.write(completedJson.toString());
            fileWriter.close();
        } catch(IOException e) {
            e.printStackTrace();
        }

    }

    private void storeToLocalFolder(JSONObject completedJson){
        Log.d(TAG, "storeToLocalFolder");

        String sFileName = "Dump_"+getTimeString(startTime)+"_"+getTimeString(endTime)+".json";

        Log.d(TAG, "sFileName : "+ sFileName);

        try {
            File root = new File(Environment.getExternalStorageDirectory() + PACKAGE_DIRECTORY_PATH);
            if (!root.exists()) {
                root.mkdirs();
            }

            Log.d(TAG, "root : " + root);

            FileWriter fileWriter = new FileWriter(root+sFileName, true);
            fileWriter.write(completedJson.toString());
            fileWriter.close();

        } catch(IOException e) {
            e.printStackTrace();
        }

    }
    //TODO remember the format is different from the normal one.
    public static String getTimeString(long time){

        SimpleDateFormat sdf_now = new SimpleDateFormat(Constants.DATE_FORMAT_for_storing);
        String currentTimeString = sdf_now.format(time);

        return currentTimeString;
    }

    public String makingDataFormat(int year,int month,int date,int hour,int min){
        String dataformat= "";

        dataformat = addZero(year)+"/"+addZero(month)+"/"+addZero(date)+" "+addZero(hour)+":"+addZero(min)+":00";
        Log.d(TAG,"dataformat : " + dataformat);

        return dataformat;
    }

    public String getDateCurrentTimeZone(long timestamp) {
        try{
            Calendar calendar = Calendar.getInstance();
            TimeZone tz = TimeZone.getDefault();
            calendar.setTimeInMillis(timestamp);
            calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date currenTimeZone = (Date) calendar.getTime();
            return sdf.format(currenTimeZone);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private String getmillisecondToDateWithTime(long timeStamp){

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeStamp);

        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH)+1;
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);
        int mhour = calendar.get(Calendar.HOUR_OF_DAY);
        int mMin = calendar.get(Calendar.MINUTE);
        int mSec = calendar.get(Calendar.SECOND);

        return addZero(mYear)+"/"+addZero(mMonth)+"/"+addZero(mDay)+" "+addZero(mhour)+":"+addZero(mMin)+":"+addZero(mSec);

    }

    private String addZero(int date){
        if(date<10)
            return String.valueOf("0"+date);
        else
            return String.valueOf(date);
    }

    /**get the current time in milliseconds**/
    private long getCurrentTimeInMillis(){
        //get timzone
        TimeZone tz = TimeZone.getDefault();
        Calendar cal = Calendar.getInstance(tz);
        //get the date of now: the first month is Jan:0
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int Hour = cal.get(Calendar.HOUR);
        int Min = cal.get(Calendar.MINUTE);

        long t = getSpecialTimeInMillis(year,month,day,Hour,Min);
        return t;
    }


}


//public class WifiReceiver extends BroadcastReceiver {
//
//   //  storeAlltoCSV();
//
//    appDatabase db;
//    public String TAG = "WifiReceiver";
//
//
//
//    private void uploadData(){
//
//        sendingDumpData();
//    }
//    public void sendingDumpData(){
//        JSONObject data = null;
//        storeAccessibility(data);
//    }
//    @Override
//    public void onReceive(Context context, Intent intent) {
//        if (Constants.ACTION_CONNECTIVITY_CHANGE.equals(intent.getAction())) {
//            db = appDatabase.getDatabase(context);
//            uploadData();
//        }
//    }
//    private void storeAccessibility (JSONObject data){
//
//        try {
//            List<AccessibilityDataRecord> accessibilityDataRecords = db.accessibilityDataRecordDao().getAll();
//            String json = new Gson().toJson(accessibilityDataRecords);
//            JSONArray array = new JSONArray(json);
////            for (AccessibilityDataRecord a : accessibilityDataRecords) {
////                String a_string = new Gson().toJson(a);
////
////            }
//
//        }catch (JSONException e){
//        }catch(NullPointerException e){
//        }
//    }
//
//
//
//}
