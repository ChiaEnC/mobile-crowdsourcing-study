package mobilecrowdsourceStudy.nctu.minuku_2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import labelingStudy.nctu.minuku.DBHelper.appDatabase;
import labelingStudy.nctu.minuku.Utilities.CSVHelper;
import labelingStudy.nctu.minuku.config.Constants;

import static labelingStudy.nctu.minuku.config.Constants.DATA_SYNCED_WITH_SERVER;
import static labelingStudy.nctu.minuku.config.Constants.MY_SOCKET_TIMEOUT_MS;
import static labelingStudy.nctu.minuku.config.Constants.URL_SAVE_DUMP;
import static labelingStudy.nctu.minuku.config.Constants.URL_SAVE_USER;
import static labelingStudy.nctu.minuku.config.SharedVariables.allMCount;
import static labelingStudy.nctu.minuku.config.SharedVariables.allNCount;
import static labelingStudy.nctu.minuku.config.SharedVariables.getReadableTimeLong;
import static labelingStudy.nctu.minuku.config.SharedVariables.startAppHour;
import static labelingStudy.nctu.minuku.config.SharedVariables.todayMCount;
import static labelingStudy.nctu.minuku.config.SharedVariables.todayNCount;
import static labelingStudy.nctu.minuku.config.SharedVariables.videoCount;

/**
 * Created by chiaenchiang on 23/11/2018.
 */

public class NetworkStateChecker extends BroadcastReceiver {
    private final String TAG = "NetworkChecker";
    //context and database helper object
    private Context context;
    private appDatabase db;
    private  SharedPreferences sharedPrefs;
    Long lastSentHour;
    Long currentHour;
    Long nowSentHour;
    @Override
    public void onReceive(Context context, Intent intent) {

        this.context = context;

        db = appDatabase.getDatabase(context);
        Log.d(TAG,"on receive");
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        sharedPrefs = context.getSharedPreferences(Constants.sharedPrefString, context.MODE_PRIVATE);
        //if there is a network
        if (activeNetwork != null) {
            //if connected to wifi or mobile data plan
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI || activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                Log.d(TAG,"ready to send dump data");
                //getting all the unsynced names
                if(db.isOpen()) {
                    sendingDumpData();
                    sendingUserData();
                }


            }
        }
    }

    /*
    * method taking two arguments
    * name that is to be saved and id of the name from SQLite
    * if the name is successfully sent
    * we will update the status as synced in SQLite
    * */
    private void saveData(JSONObject multipleRows) {
//        StringRequest stringRequest = new StringRequest(Request.Method.POST, MainActivity.URL_SAVE_DUMP,
//                new ResponseResult.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        try {
//                            JSONObject obj = new JSONObject(response);
//                            if (!obj.getBoolean("error")) {
//                                //updating the status in sqlite
//                                db.accessibilityDataRecordDao().updateDataStatus(creationTime, MainActivity.DATA_SYNCED_WITH_SERVER);
//
//                                //sending the broadcast to refresh the list
//                                context.sendBroadcast(new Intent(MainActivity.DATA_SAVED_BROADCAST));
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                },
//                new ResponseResult.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//
//                    }
//                }) {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> params = new HashMap<>();
//              //  params.put("name", name);
//                return params;
//            }
//        };

        //Log.d(TAG," one row : "+multipleRows.toString());

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, URL_SAVE_DUMP, multipleRows, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                if(response!=null) {
                    String device_id = "";
                    try {
                        device_id = response.getString("device_id");
                        Log.d(TAG,"id : "+device_id);
//                        String access_time = response.getString("Accessibility");
//                        Log.d(TAG,"access_time : "+access_time);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.d(TAG,"device Id : "+device_id.toString());
                    Log.d(TAG,"device Id constant : "+Constants.DEVICE_ID);

                    if(device_id.toString().contains(Constants.DEVICE_ID)) {
                        Log.d(TAG, " repsonse : " + response.toString());

                        updateAllData(response);
                    }
                }
//                //TODO: handle success

//                try {
//                    JSONObject obj = response;
//                    Log.d(TAG," repsonse : "+response.toString());
//
//                    if (obj.getString("error")=="false") {  //沒有錯誤
//                        //updating the status in sqlite
//                        Log.d(TAG," repsonse : error = false");
//
//                        db.accessibilityDataRecordDao().updateDataStatus(creationTime.get(0), DATA_SYNCED_WITH_SERVER);
//
//                        //sending the broadcast to refresh the list
//                        context.sendBroadcast(new Intent(DATA_SAVED_BROADCAST));
//                    }else{
//                        db.accessibilityDataRecordDao().updateDataStatus(creationTime.get(0), DATA_NOT_SYNCED_WITH_SERVER);
//
//                    }
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }

               // refreshAllContent(60*1000*10); // TODO 10min->1hr
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                //TODO: handle failure
            }
        });
        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                0,  //0
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        VolleySingleton.getInstance(context).addToRequestQueue(jsonRequest);
    }





    @RequiresApi(api = Build.VERSION_CODES.N)
    public List<Long> getLongList(String creationTime) {

        String[] target = {"[", "]", "-", "\""};
        for (String temp : target) {
            creationTime = creationTime.replace(temp, "");
        }
        Log.d(TAG, " create : " + creationTime);
        if (creationTime != "") {
            final List<Long> longs = Arrays
                    .stream(creationTime.split(","))
                    .map(Long::parseLong)
                    .collect(Collectors.toList());
            for (Long tmp : longs) {
                Log.d(TAG, " long : " + tmp);
            }
            return longs;
        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void updateAllData(JSONObject response){
        // have inserted creationTime
        String HIAccessibilty = "";
        String HIActivityRecognition = "";
        String HIBattery = "";
        String HIConnectivity = "";
        String HILocation = "";
        String HIMobileCrowdsource = "";
        String HINotification = "";
        String HIRinger = "";
        String HITransportation= "";
        String HIAppusage = "";
        String HITelephony = "";
        String HISensor = "";
        String HIFinal = "";
        String HIResponse = "";
        Iterator keysToCopyIterator = response.keys();
        List<String> keysList = new ArrayList<String>();
        while(keysToCopyIterator.hasNext()) {
            String key = (String) keysToCopyIterator.next();
            keysList.add(key);
            Log.d(TAG,"key  : "+key);
        }
        for(String key : keysList) {
            try {
                String result = response.getString(key);
                if(key.equals("Accessibility"))HIAccessibilty = result;
                else if(key.equals("ActivityRecognition"))HIActivityRecognition = result;
                else if(key.equals("Battery"))HIBattery = result;
                else if(key.equals("Connectivity"))HIConnectivity = result;
                else if(key.equals("Location"))HILocation = result;
                else if(key.equals("MobileCrowdsource"))HIMobileCrowdsource = result;
                else if(key.equals("Notification"))HINotification = result;
                else if(key.equals("Ringer"))HIRinger = result;
                else if(key.equals("TransportationMode"))HITransportation = result;
                else if(key.equals("AppUsage"))HIAppusage = result;
                else if(key.equals("Telephony"))HITelephony = result;
                else if(key.equals("Sensor"))HISensor = result;
                else if(key.equals("QuestionnaireAns"))HIFinal = result;
                else if(key.equals("Response"))HIResponse = result;



//                HIAccessibilty = response.getString("Accessibility");
//                HIActivityRecognition = response.getString("ActivityRecognition");
//                HIBattery = response.getString("Battery");
//                HIConnectivity = response.getString("Connectivity");
//                HILocation = response.getString("Location");
//                HIMobileCrowdsource = response.getString("MobileCrowdsource");
//                HINotification = response.getString("Notification");
//                HIRinger = response.getString("Ringer");
//                HITransportation = response.getString("TransportationMode");
//                HIAppusage = response.getString("Appusage");
//                HITelephony = response.getString("Telephony");
//                HISensor = response.getString("Sensor");
//                HIFinal = response.getString("QuestionnaireAns");
//            Log.d(TAG,"HIAccess : "+HIAccessibilty);
//            Log.d(TAG,"HIActivityRecognition : "+HIActivityRecognition);
//            Log.d(TAG,"HIBattery : "+HIBattery);
//            Log.d(TAG,"HIConnectivity : "+HIConnectivity);
//            Log.d(TAG,"HILocation : "+HILocation);
//            Log.d(TAG,"HIMobileCrowdsource : "+HIMobileCrowdsource);
//            Log.d(TAG,"HINotification : "+HINotification);
//            Log.d(TAG,"HIRinger : "+HIRinger);
//            Log.d(TAG,"HITransportation : "+HITransportation);
//            Log.d(TAG,"HIAppusage : "+HIAppusage);
//            Log.d(TAG,"HITelephony : "+HITelephony);
//            Log.d(TAG,"HISensor : "+HISensor);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if(HIAccessibilty!="")
            updateAccessibilityDataRecord(HIAccessibilty);
        if(HIActivityRecognition!="")
            updateActivityRecognitionDataRecord(HIActivityRecognition);
        if(HIAppusage!="")
            updateAppUsageDataRecord(HIAppusage);
        if(HIBattery!="")
            updateBatteryDataRecord(HIBattery);
        if(HIConnectivity!="")
            updateConnectivityDataRecord(HIConnectivity);
        if(HIRinger!="")
            updateRingerDataRecord(HIRinger);
        if(HILocation!="")
            updateLocationDataRecord(HILocation);
        if(HITransportation!="")
            updateTransportationModeDataRecord(HITransportation);
        if(HITelephony!="")
            updateTelephonyDataRecord(HITelephony);
        if(HISensor!="")
            updateSensorDataRecord(HISensor);
        if(HINotification!="")
            updateNotificationDataRecord(HINotification);
        if(HIMobileCrowdsource!="")
            updateMobileCrowdsourceDataRecord(HIMobileCrowdsource);
        if(HIFinal!="")
            updateFinalAnswerDataRecord(HIFinal);
        if(HIResponse!=""){
            updateResponseDataRecord(HIResponse);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void updateActivityRecognitionDataRecord(String creationTime){
        try {
            List<Long> creationTimeLongList = getLongList(creationTime);
            if(creationTimeLongList!=null) {
                for (Long temp : creationTimeLongList) {
                    Log.d(TAG, "Activityrecog : " + temp.toString());
                    if (temp != null) {
                        db.activityRecognitionDataRecordDao().updateDataStatus(temp, DATA_SYNCED_WITH_SERVER);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void updateAccessibilityDataRecord(String creationTime){
        try {
            List<Long> creationTimeLongList = getLongList(creationTime);
            if(creationTimeLongList!=null) {
                for (Long temp : creationTimeLongList) {
                    if (temp != null) {
                        db.accessibilityDataRecordDao().updateDataStatus(temp, DATA_SYNCED_WITH_SERVER);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void updateAppUsageDataRecord(String creationTime){
        try {
            List<Long> creationTimeLongList = getLongList(creationTime);
            if(creationTimeLongList!=null) {
                for (Long temp : creationTimeLongList) {
                    if (temp != null) {
                        db.appUsageDataRecordDao().updateDataStatus(temp, DATA_SYNCED_WITH_SERVER);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void updateBatteryDataRecord(String creationTime){
        try {
            List<Long> creationTimeLongList = getLongList(creationTime);
            if(creationTimeLongList!=null) {
                for (Long temp : creationTimeLongList) {
                    if (temp != null) {
                        db.batteryDataRecordDao().updateDataStatus(temp, DATA_SYNCED_WITH_SERVER);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void updateConnectivityDataRecord(String creationTime){
        try {
            List<Long> creationTimeLongList = getLongList(creationTime);
            if(creationTimeLongList!=null) {
                for (Long temp : creationTimeLongList) {
                    if (temp != null) {
                        db.connectivityDataRecordDao().updateDataStatus(temp, DATA_SYNCED_WITH_SERVER);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void updateRingerDataRecord(String creationTime){
        try {
            List<Long> creationTimeLongList = getLongList(creationTime);
            if(creationTimeLongList!=null) {
                for (Long temp : creationTimeLongList) {
                    if (temp != null) {
                        db.ringerDataRecordDao().updateDataStatus(temp, DATA_SYNCED_WITH_SERVER);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void updateLocationDataRecord(String creationTime){
        try {
            List<Long> creationTimeLongList = getLongList(creationTime);
            if(creationTimeLongList!=null) {
                for (Long temp : creationTimeLongList) {
                    if (temp != null) {
                        db.locationDataRecordDao().updateDataStatus(temp, DATA_SYNCED_WITH_SERVER);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void updateTransportationModeDataRecord(String creationTime){
        try {
            List<Long> creationTimeLongList = getLongList(creationTime);
            if(creationTimeLongList!=null) {
                for (Long temp : creationTimeLongList) {
                    if (temp != null) {
                        db.transportationModeDataRecordDao().updateDataStatus(temp, DATA_SYNCED_WITH_SERVER);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void updateResponseDataRecord(String creationTime){
        try {
            List<Long> creationTimeLongList = getLongList(creationTime);
            if(creationTimeLongList!=null) {
                for (Long temp : creationTimeLongList) {
                    if (temp != null) {
                        db.repsonseDataRecordDao().updateDataStatus(temp, DATA_SYNCED_WITH_SERVER);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public void updateSensorDataRecord(String creationTime){
        try {
            List<Long> creationTimeLongList = getLongList(creationTime);
            if(creationTimeLongList!=null) {
                for (Long temp : creationTimeLongList) {
                    if (temp != null) {
                        db.sensorDataRecordDao().updateDataStatus(temp, DATA_SYNCED_WITH_SERVER);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void updateTelephonyDataRecord(String creationTime){
        try {
            List<Long> creationTimeLongList = getLongList(creationTime);
            if(creationTimeLongList!=null) {
                for (Long temp : creationTimeLongList) {
                    if (temp != null) {
                        db.telephonyDataRecordDao().updateDataStatus(temp, DATA_SYNCED_WITH_SERVER);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void updateNotificationDataRecord(String creationTime){
        try {
            List<Long> creationTimeLongList = getLongList(creationTime);
            if(creationTimeLongList!=null) {
                for (Long temp : creationTimeLongList) {
                    if (temp != null) {
                        db.notificationDataRecordDao().updateDataStatus(temp, DATA_SYNCED_WITH_SERVER);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void updateMobileCrowdsourceDataRecord(String creationTime){
        try {
            List<Long> creationTimeLongList = getLongList(creationTime);
            if(creationTimeLongList!=null) {
                for (Long temp : creationTimeLongList) {
                    if (temp != null) {
                        db.mobileCrowdsourceDataRecordDao().updateDataStatus(temp, DATA_SYNCED_WITH_SERVER);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void updateFinalAnswerDataRecord(String creationTime){
        try {
            List<Long> creationTimeLongList = getLongList(creationTime);
            if(creationTimeLongList!=null) {
                for (Long temp : creationTimeLongList) {
                    if (temp != null) {
                        db.finalAnswerDao().updateDataStatus(temp, DATA_SYNCED_WITH_SERVER);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean sendingUserData(){
        JSONObject UserJson = new JSONObject();
//        Integer todayMCount = sharedPrefs.getInt("todayMCount",0);
//        Integer todayNCount = sharedPrefs.getInt("todayNCount",0);
//        Integer allMCount = sharedPrefs.getInt("allMCount",0);
//        Integer allNCount = sharedPrefs.getInt("allNCount",0);


        try {
            UserJson.put("device_id",Constants.DEVICE_ID);
            UserJson.put("todayMCount",todayMCount);
            UserJson.put("todayNCount",todayNCount);
            UserJson.put("allMCount",allMCount);
            UserJson.put("allNCount",allNCount);
            UserJson.put("videoCount",videoCount);
            UserJson.put("total",allNCount+allMCount);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(TAG,"user :"+UserJson.toString());
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, URL_SAVE_USER, UserJson, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG," repsonse : "+response.toString());
                //updateAllData(response);
//                //TODO: handle success

//                try {
//                    JSONObject obj = response;
//                    Log.d(TAG," repsonse : "+response.toString());
//
//                    if (obj.getString("error")=="false") {  //沒有錯誤
//                        //updating the status in sqlite
//                        Log.d(TAG," repsonse : error = false");
//
//                        db.accessibilityDataRecordDao().updateDataStatus(creationTime.get(0), DATA_SYNCED_WITH_SERVER);
//
//                        //sending the broadcast to refresh the list
//                        context.sendBroadcast(new Intent(DATA_SAVED_BROADCAST));
//                    }else{
//                        db.accessibilityDataRecordDao().updateDataStatus(creationTime.get(0), DATA_NOT_SYNCED_WITH_SERVER);
//
//                    }
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
                refreshUser(60*1000*60*2); // TODO 2hr
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();

                //TODO: handle failure
            }
        });
        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                0,  //0
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        VolleySingleton.getInstance(context).addToRequestQueue(jsonRequest);

        return true;
    }



    public boolean sendingDumpData(){
        Log.d(TAG,"in dump data");
        JSONObject dataInJson = new JSONObject();
//        Integer atMostnumberOfRows = 10;
//        storeAccessibility(dataInJson,atMostnumberOfRows);

        Log.d(TAG,"nowSentHour : "+nowSentHour);
        Long nowTime = new Date().getTime() ;
        currentHour = getReadableTimeLong(nowTime);

        Log.d(TAG,"startAppHour : "+startAppHour);
        if(lastSentHour==null||lastSentHour<startAppHour) {
            sharedPrefs.edit().putLong("lastSentHour", startAppHour).apply();
            lastSentHour = startAppHour;
            nowSentHour = lastSentHour;
        }
        else {
            lastSentHour = sharedPrefs.getLong("lastSentHour", startAppHour);
            // error handling
            if(nowSentHour/1000000L>0) {
                nowSentHour = lastSentHour + 1;
                nowSentHour = nowSentHour % 100 >= 24 ? currentHour : nowSentHour;
            }else{
                nowSentHour = currentHour;
            }
        }


        Log.d(TAG,"currentHour : "+currentHour);
        CSVHelper.storeToCSV("netWork.csv","nowSentHour :" +nowSentHour);
        CSVHelper.storeToCSV("netWork.csv","currentHour :" +currentHour);
        if(nowSentHour < currentHour) {

            storeAccessibility(dataInJson);
            storeTransporatation(dataInJson);
            storeLocation(dataInJson);
            storeActivityRecognition(dataInJson);
            storeRinger(dataInJson);
            storeConnectivity(dataInJson);
            storeBattery(dataInJson);
            storeAppUsage(dataInJson);
            storeTelephony(dataInJson);
            storeSensor(dataInJson);
            storeNotification(dataInJson);
            storeMobileCrowdsource(dataInJson);
            storeQuestionnaireAnswer(dataInJson);
            storeResponse(dataInJson);
            sharedPrefs.edit().putLong("lastSentHour",nowSentHour).apply();

            try {
                dataInJson.put("device_id", Constants.DEVICE_ID);
            } catch (JSONException e) {
                e.printStackTrace();
            }

//        String jsonResp = dataInJson.toString();
//        Log.d("JSON","string before"+jsonResp);
//        jsonResp = jsonResp.replace("[{", "{").replace("}]", "}");
//        Log.d("JSON","string after"+jsonResp);
//        JSONObject obj = null;
//        try {
//
//            obj = new JSONObject(jsonResp);
//
//            Log.d("My App", obj.toString());
//
//        } catch (Throwable t) {
//            Log.e("My App", "Could not parse malformed JSON: \"" + jsonResp + "\"");
//        }
            if(Constants.DEVICE_ID!="NA"||dataInJson.length() != 0)
                saveData(dataInJson);
            return true;
        }

        return false;
    }

    private void storeAccessibility(JSONObject data){

        Log.d(TAG, "storeAccessibility");

        try {

            JSONArray multiRows = new JSONArray();
            Cursor transCursor = null;

            transCursor = db.accessibilityDataRecordDao().getUnsyncedData(0,nowSentHour);

            int allRows = transCursor.getCount();

            Log.d(TAG, "rows : "+allRows);

            if(allRows!=0){
                transCursor.moveToFirst();
                for(int i=0;i<allRows;i++) {
                    JSONObject oneRow = new JSONObject();
                    Long timestamp = transCursor.getLong(1);
                    String pack = transCursor.getString(2);
                    String text = transCursor.getString(3);
                    String type = transCursor.getString(4);
                    String extra = transCursor.getString(5);
                    String content = transCursor.getString(6);
                    Integer mcid = transCursor.getInt(7);

                    oneRow.put("pack",pack);
                    oneRow.put("text",text);
                    oneRow.put("type",type);
                    oneRow.put("extra",extra);
                    oneRow.put("content",content);
                    oneRow.put("related_id",mcid);

                    oneRow.put("timestamp",timestamp);
                    oneRow.put("readable",getReadableTimeLong(timestamp));
                    multiRows.put(oneRow);
                    transCursor.moveToNext();
                }

                data.put("Accessibility",multiRows);

            }else{

            }
        } catch(JSONException e){
            e.printStackTrace();
        }
        catch(NullPointerException e){
            e.printStackTrace();
        }


    }

    private void storeMobileCrowdsource(JSONObject data){

        Log.d(TAG, "storeMobileCrowdsource");

        try {
            JSONArray multiRows = new JSONArray();
            Cursor transCursor = null;
            transCursor = db.mobileCrowdsourceDataRecordDao().getUnsyncedData(0,nowSentHour);

            int rows = transCursor.getCount();

            Log.d(TAG, "rows : "+rows);

            if(rows!=0){
                transCursor.moveToFirst();
                for(int i=0;i<rows;i++) {
                    // columne 1 : id
                    JSONObject oneRow = new JSONObject();
                    Long creationTime = transCursor.getLong(1);
                    String app = transCursor.getString(2);

                    String ifSentNoti = transCursor.getString(3);
                    String startTasktime = transCursor.getString(4);
                    String endTasktime = transCursor.getString(5);
                    String userActions = transCursor.getString(6);
                    Integer accessId = transCursor.getInt(7);


                    oneRow.put("timestamp",creationTime);
                    oneRow.put("app",app);
                    oneRow.put("if_clicked_noti",ifSentNoti);
                    oneRow.put("start_task_time",startTasktime);
                    oneRow.put("end_task_time",endTasktime);
                    oneRow.put("user_actions",userActions);
                    oneRow.put("related_id",accessId);
                    oneRow.put("readable",getReadableTimeLong(creationTime));

                    multiRows.put(oneRow);
                    transCursor.moveToNext();
                }

                //    mobileCrowdsourceAndtimestampsJson.put("tasktype_cols",tasktype_cols);

                data.put("MobileCrowdsource",multiRows);

            }

        }catch (JSONException e){
            e.printStackTrace();
        }catch(NullPointerException e){
            e.printStackTrace();
        }

        Log.d(TAG,"data : "+ data.toString());

    }

    private void storeTransporatation(JSONObject data){

        Log.d(TAG, "storeTransporatation");

        try {

            JSONArray multiRows = new JSONArray();
            Cursor transCursor = null;

            transCursor = db.transportationModeDataRecordDao().getUnsyncedData(0,nowSentHour);

            int rows = transCursor.getCount();


            if(rows!=0){
                transCursor.moveToFirst();
                for(int i=0;i<rows;i++) {
                    JSONObject oneRow = new JSONObject();
                    Long timestamp = transCursor.getLong(1);
                    String transportation = transCursor.getString(2);

                    Log.d(TAG,"transportation : "+transportation+" timestamp : "+timestamp);


                    oneRow.put("transportation",transportation);
                    oneRow.put("timestamp",timestamp);
                    oneRow.put("readable",getReadableTimeLong(timestamp));
                    multiRows.put(oneRow);
                    transCursor.moveToNext();
                }

                data.put("TransportationMode",multiRows);

            }
        }catch (JSONException e){
            e.printStackTrace();
        }catch(NullPointerException e){
            e.printStackTrace();
        }

        Log.d(TAG,"data : "+ data.toString());

    }

    private void storeLocation(JSONObject data){

        try {


            JSONArray multiRows = new JSONArray();
            Cursor transCursor = null;
            transCursor = db.locationDataRecordDao().getUnsyncedData(0,nowSentHour);

            int rows = transCursor.getCount();

            Log.d(TAG, "rows : "+rows);

            if(rows!=0){
                transCursor.moveToFirst();
                for(int i=0;i<rows;i++) {
                    JSONObject oneRow = new JSONObject();
                    Long timestamp = transCursor.getLong(1);
                    String latitude = transCursor.getString(3);
                    String longtitude = transCursor.getString(4);
                    String accuracy = transCursor.getString(5);

                    Log.d(TAG,"timestamp : "+timestamp+" latitude : "+latitude+" longtitude : "+longtitude+" accuracy : "+accuracy);
                    oneRow.put("accuracy",accuracy);
                    oneRow.put("latitude",latitude);
                    oneRow.put("timestamp",timestamp);
                    oneRow.put("readable",getReadableTimeLong(timestamp));
                    multiRows.put(oneRow);
                    transCursor.moveToNext();
                }

                data.put("Location",multiRows);

            }
        }catch (JSONException e){
            e.printStackTrace();
        }catch(NullPointerException e){
            e.printStackTrace();
        }

        Log.d(TAG,"data : "+ data.toString());

    }

    private void storeActivityRecognition(JSONObject data){
        try {


            JSONArray multiRows = new JSONArray();

            Cursor transCursor = null;

            transCursor = db.activityRecognitionDataRecordDao().getUnsyncedData(0,nowSentHour);
            int rows = transCursor.getCount();

            if(rows!=0){
                transCursor.moveToFirst();
                for(int i=0;i<rows;i++) {
                    JSONObject oneRow = new JSONObject();
                    Long timestamp = transCursor.getLong(1);
                    if(timestamp>0) {
                        String mostProbableActivity = transCursor.getString(2);
                        String probableActivities = transCursor.getString(3);
                        String Detectedtime = transCursor.getString(4);

                        oneRow.put("most_probable_activity",mostProbableActivity);
                        oneRow.put("probable_activities",probableActivities);
                        oneRow.put("detected_time",Detectedtime);
                        oneRow.put("timestamp",timestamp);
                        oneRow.put("readable",getReadableTimeLong(timestamp));

                        multiRows.put(oneRow);

                    }else{
                        db.activityRecognitionDataRecordDao().updateDataStatus(timestamp, DATA_SYNCED_WITH_SERVER);
                    }

                    transCursor.moveToNext();
                }

                data.put("ActivityRecognition",multiRows);

            }

        }catch (JSONException e){
            e.printStackTrace();
        }catch(NullPointerException e){
            e.printStackTrace();
        }

        Log.d(TAG,"data : "+ data.toString());

    }

    private void storeRinger(JSONObject data){

        Log.d(TAG, "storeRinger");

        try {
            JSONArray multiRows = new JSONArray();

            Cursor transCursor = null;
            transCursor = db.ringerDataRecordDao().getUnsyncedData(0,nowSentHour);
            int rows = transCursor.getCount();

//            Log.d(TAG, "rows : "+rows);

            if(rows!=0){
                transCursor.moveToFirst();
                for(int i=0;i<rows;i++) {
                    JSONObject oneRow = new JSONObject();

                    Long timestamp = transCursor.getLong(1);
                    String RingerMode = transCursor.getString(2);
                    String AudioMode = transCursor.getString(3);


                    oneRow.put("ringer_mode",RingerMode);
                    oneRow.put("audio_mode",AudioMode);
                    oneRow.put("timestamp",timestamp);
                    oneRow.put("readable",getReadableTimeLong(timestamp));
                    multiRows.put(oneRow);
                    transCursor.moveToNext();
                }
                data.put("Ringer",multiRows);

            }
        }catch (JSONException e){
            e.printStackTrace();
        }catch(NullPointerException e){
            e.printStackTrace();
        }

        Log.d(TAG,"data : "+ data.toString());

    }

    private void storeConnectivity(JSONObject data){

        try {

            JSONArray multiRows = new JSONArray();


            Cursor transCursor = null;
            transCursor = db.connectivityDataRecordDao().getUnsyncedData(0,nowSentHour);

            int rows = transCursor.getCount();

            if(rows!=0){
                transCursor.moveToFirst();
                for(int i=0;i<rows;i++) {
                    JSONObject oneRow = new JSONObject();
                    Long timestamp = transCursor.getLong(1);
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

                    oneRow.put("is_mobile_connected",IsMobileConnected);
                    oneRow.put("is_wifi_connected",IsWifiConnected);
                    oneRow.put("is_mobile_available",IsMobileAvailable);
                    oneRow.put("is_wifi_available",IsWifiAvailable);
                    oneRow.put("is_connected",IsConnected);
                    oneRow.put("is_network_available",IsNetworkAvailable);
                    oneRow.put("network_type",NetworkType);
                    oneRow.put("timestamp",timestamp);
                    oneRow.put("readable",getReadableTimeLong(timestamp));
                    multiRows.put(oneRow);

                    transCursor.moveToNext();
                }

                data.put("Connectivity",multiRows);

            }
        }catch (JSONException e){
            e.printStackTrace();
        }catch(NullPointerException e){
            e.printStackTrace();
        }

        Log.d(TAG,"data : "+ data.toString());

    }

    private void storeBattery(JSONObject data){

        Log.d(TAG, "storeBattery");

        try {


            JSONArray multiRows = new JSONArray();


            Cursor transCursor = null;
            transCursor = db.batteryDataRecordDao().getUnsyncedData(0,nowSentHour);

            int rows = transCursor.getCount();

            if(rows!=0){
                transCursor.moveToFirst();
                for(int i=0;i<rows;i++) {
                    JSONObject oneRow = new JSONObject();
                    Long timestamp = transCursor.getLong(1);
                    String BatteryLevel = transCursor.getString(2);
                    String BatteryPercentage = transCursor.getString(3);
                    String BatteryChargingState = transCursor.getString(4);
                    String isCharging = transCursor.getString(5);

                    oneRow.put("timestamp",timestamp);
                    oneRow.put("battery_level",BatteryLevel);
                    oneRow.put("battery_percentage",BatteryPercentage);
                    oneRow.put("battery_charging_state",BatteryChargingState);
                    oneRow.put("is_charging",isCharging);
                    oneRow.put("readable",getReadableTimeLong(timestamp));
                    multiRows.put(oneRow);
                    transCursor.moveToNext();
                }


                data.put("Battery",multiRows);

            }
        }catch (JSONException e){
            e.printStackTrace();
        }catch(NullPointerException e){
            e.printStackTrace();
        }

//        Log.d(TAG,"data : "+ data.toString());

    }

    private void storeAppUsage(JSONObject data){

        Log.d(TAG, "storeAppUsage");

        try {

            JSONArray multiRows = new JSONArray();

            Cursor transCursor = null;
            transCursor = db.appUsageDataRecordDao().getUnsyncedData(0,nowSentHour);
            int rows = transCursor.getCount();

//            Log.d(TAG, "rows : "+rows);

            if(rows!=0){
                transCursor.moveToFirst();
                for(int i=0;i<rows;i++) {
                    JSONObject oneRow = new JSONObject();
                    Long timestamp = transCursor.getLong(1);
                    String ScreenStatus = transCursor.getString(2);
                    String Latest_Used_App = transCursor.getString(3);
                    String Latest_Used_App_Time = transCursor.getString(4);
//                    Log.d(TAG,"timestamp : "+timestamp+" ScreenStatus : "+ScreenStatus+" Latest_Used_App : "+Latest_Used_App+" Latest_Foreground_Activity : "+Latest_Foreground_Activity);

                    oneRow.put("timestamp",timestamp);
                    oneRow.put("screen_status",ScreenStatus);
                    oneRow.put("latest_used_app",Latest_Used_App);
                    oneRow.put("latest_used_app_time",Latest_Used_App_Time);
                    oneRow.put("readable",getReadableTimeLong(timestamp));
                    multiRows.put(oneRow);
                    transCursor.moveToNext();
                }


                data.put("AppUsage",multiRows);

            }
        }catch (JSONException e){
            e.printStackTrace();
        }catch(NullPointerException e){
            e.printStackTrace();
        }

//        Log.d(TAG,"data : "+ data.toString());

    }

    private void storeTelephony(JSONObject data){
        try {

            JSONArray multiRows = new JSONArray();
            JSONObject telephonyAndtimestampsJson = new JSONObject();

            Cursor transCursor = null;
            transCursor = db.telephonyDataRecordDao().getUnsyncedData(0,nowSentHour);


            int rows = transCursor.getCount();


            if(rows!=0){
                transCursor.moveToFirst();
                for(int i=0;i<rows;i++) {
                    JSONObject oneRow = new JSONObject();
                    Long timestamp = transCursor.getLong(1);
                    String NetworkOperatorName = transCursor.getString(2);
                    String CallState = transCursor.getString(3);
                    String PhoneSignalType = transCursor.getString(4);
                    String GsmSignalStrength = transCursor.getString(5);
                    String LTESignalStrength = transCursor.getString(6);
                    String CdmaSignalStrengthLevel = transCursor.getString(7);

//                    Log.d(TAG,"timestamp : "+timestamp+" NetworkOperatorName : "+NetworkOperatorName+" CallState : "+CallState+" PhoneSignalType : "+PhoneSignalType+" GsmSignalStrength : "+GsmSignalStrength+" LTESignalStrength : "+LTESignalStrength+" CdmaSignalStrengthLevel : "+CdmaSignalStrengthLevel );

                    oneRow.put("network_operator_name",NetworkOperatorName);
                    oneRow.put("call_state",CallState);
                    oneRow.put("phone_signal_type",PhoneSignalType);
                    oneRow.put("lte_signal_strength",LTESignalStrength);
                    oneRow.put("timestamp",timestamp);
                    oneRow.put("readable",getReadableTimeLong(timestamp));
                    multiRows.put(oneRow);
                    transCursor.moveToNext();
                }

                data.put("Telephony",multiRows);

            }
        }catch (JSONException e){
            e.printStackTrace();
        }catch(NullPointerException e){
            e.printStackTrace();
        }

//        Log.d(TAG,"data : "+ data.toString());

    }

    private void storeSensor(JSONObject data){

//        Log.d(TAG, "storeSensor");

        try {


            JSONArray multiRows = new JSONArray();
            Cursor transCursor = null;

            transCursor = db.sensorDataRecordDao().getUnsyncedData(0,nowSentHour);
            int rows = transCursor.getCount();

//            Log.d(TAG, "rows : "+rows);

            if(rows!=0){
                transCursor.moveToFirst();
                for(int i=0;i<rows;i++) {
                    JSONObject oneRow = new JSONObject();
                    Long timestamp = transCursor.getLong(1);
                    String PROXIMITY = transCursor.getString(6);
                    String LIGHT = transCursor.getString(7);

                    oneRow.put("light",LIGHT);
                    oneRow.put("proximity",PROXIMITY);
                    oneRow.put("timestamp",timestamp);
                    oneRow.put("readable",getReadableTimeLong(timestamp));

                    multiRows.put(oneRow);
                    transCursor.moveToNext();
                }

                data.put("Sensor",multiRows);

            }
        }catch (JSONException e){
            e.printStackTrace();
        }catch(NullPointerException e){
            e.printStackTrace();
        }

//        Log.d(TAG,"data : "+ data.toString());

    }

    private void storeNotification(JSONObject data){

        Log.d(TAG, "storeNotification");

        try {

            JSONArray multiRows = new JSONArray();

            Cursor transCursor = null;
            transCursor = db.notificationDataRecordDao().getUnsyncedData(0,nowSentHour);

            int rows = transCursor.getCount();

            Log.d(TAG, "rows : "+rows);

            if(rows!=0){
                transCursor.moveToFirst();
                for(int i=0;i<rows;i++) {
                    JSONObject oneRow = new JSONObject();
                    Long timestamp = transCursor.getLong(1);
                    String title_col = transCursor.getString(2);
                    String n_text_col = transCursor.getString(3);
                    String subText_col = transCursor.getString(4);
                    String tickerText_col = transCursor.getString(5);
                    String app_col = transCursor.getString(6);
                    Integer relatedId = transCursor.getInt(7);
                    String reason = transCursor.getString(8);

                    oneRow.put("timestamp",timestamp);
                    oneRow.put("title",title_col);
                    oneRow.put("text",n_text_col);
                    oneRow.put("subtext",subText_col);
                    oneRow.put("tickertext",tickerText_col);
                    oneRow.put("app",app_col);
                    oneRow.put("related_id",relatedId);
                    oneRow.put("timestamp",timestamp);
                    oneRow.put("reason",reason);
                    oneRow.put("readable",getReadableTimeLong(timestamp));
                    multiRows.put(oneRow);

                    transCursor.moveToNext();
                }

                data.put("Notification",multiRows);

            }
        }catch (JSONException e){
            e.printStackTrace();
        }catch(NullPointerException e){
            e.printStackTrace();
        }

    }


    private void storeQuestionnaireAnswer(JSONObject data){
        try {


            JSONArray multiRows = new JSONArray();

            Cursor transCursor = null;

            transCursor = db.finalAnswerDao().getUnsyncedData(0,nowSentHour);

            int rows = transCursor.getCount();

            Log.d(TAG, "rows : "+rows);

            if(rows!=0){
                transCursor.moveToFirst();
                for(int i=0;i<rows;i++) {
                    JSONObject oneRow = new JSONObject();
                    Long timestamp = transCursor.getLong(1);
                    String relatedId = transCursor.getString(2);
                    String questionId = transCursor.getString(3);
                    String answerId = transCursor.getString(4);
                    String answerChoicePossForCheck = transCursor.getString(5);
                    String answerChoiceState = transCursor.getString(6);
                    String detectedTime = transCursor.getString(7);
                    String ansChoice = transCursor.getString(8);
                    oneRow.put("question_id",questionId);
                    oneRow.put("answer_id",answerId);
                    oneRow.put("answerpos_for_check",answerChoicePossForCheck);
                    oneRow.put("answer_choice_state",answerChoiceState);
                    oneRow.put("detected_time",detectedTime);
                    oneRow.put("related_id",relatedId);
                    oneRow.put("timestamp",timestamp);
                    oneRow.put("answer_choice",ansChoice);
                    oneRow.put("readable",getReadableTimeLong(timestamp));
                    multiRows.put(oneRow);

                    transCursor.moveToNext();
                }
                data.put("QuestionnaireAns",multiRows);

            }
        }catch (JSONException e){
            e.printStackTrace();
        }catch(NullPointerException e){
            e.printStackTrace();
        }
    }
    private void storeResponse(JSONObject data){
        try {

            JSONArray multiRows = new JSONArray();
            JSONObject responseAndtimestampsJson = new JSONObject();

            Cursor transCursor = null;
            transCursor = db.repsonseDataRecordDao().getUnsyncedData(0,nowSentHour);


            int rows = transCursor.getCount();


            if(rows!=0){
                transCursor.moveToFirst();
                for(int i=0;i<rows;i++) {
                    JSONObject oneRow = new JSONObject();

                    Long timestamp = transCursor.getLong(1);
                    String relatedId = transCursor.getString(2);
                    String qGenerateTime = transCursor.getString(3);
                    String  type = transCursor.getString(4);
                    String startAnswerTime = transCursor.getString(5);
                    String finishTime = transCursor.getString(6);
                    String ifComplete = transCursor.getString(7);

//                    Log.d(TAG,"timestamp : "+timestamp+" NetworkOperatorName : "+NetworkOperatorName+" CallState : "+CallState+" PhoneSignalType : "+PhoneSignalType+" GsmSignalStrength : "+GsmSignalStrength+" LTESignalStrength : "+LTESignalStrength+" CdmaSignalStrengthLevel : "+CdmaSignalStrengthLevel );

                    oneRow.put("related_id",relatedId);
                    oneRow.put("q_generate_time",qGenerateTime);
                    oneRow.put("type",type);
                    oneRow.put("start_answer_time",startAnswerTime);
                    oneRow.put("finish_time",finishTime);
                    oneRow.put("if_complete",ifComplete);
                    oneRow.put("readable",getReadableTimeLong(timestamp));
                    oneRow.put("timestamp",timestamp);

                    multiRows.put(oneRow);
                    transCursor.moveToNext();
                }

                data.put("Response",multiRows);

            }
        }catch (JSONException e){
            e.printStackTrace();
        }catch(NullPointerException e){
            e.printStackTrace();
        }

//        Log.d(TAG,"data : "+ data.toString());

    }


    public void refreshAllContent(final long timetoupdate) {
        new CountDownTimer(timetoupdate, 1000) {
            public void onTick(long millisUntilFinished) {
            }
            public void onFinish() {
                Log.i("SCROLLS ", "UPDATE CONTENT HERE ");
                sendingDumpData();
            }
        }.start();
    }
    public void refreshUser(final long timetoupdate) {
        new CountDownTimer(timetoupdate, 1000) {
            public void onTick(long millisUntilFinished) {
            }
            public void onFinish() {
                Log.i("SCROLLS ", "UPDATE CONTENT HERE ");
                sendingUserData();
            }
        }.start();
    }





}