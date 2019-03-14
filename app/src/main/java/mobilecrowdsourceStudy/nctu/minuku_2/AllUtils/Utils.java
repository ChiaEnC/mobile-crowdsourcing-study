package mobilecrowdsourceStudy.nctu.minuku_2.AllUtils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


import labelingStudy.nctu.minuku.streamgenerator.TransportationModeStreamGenerator;

/**
 * Created by chiaenchiang on 01/11/2018.
 */

public class Utils {

    public static String TAG = "UtilityClass";

    /**
     * Given an email Id as string, encodes it so that it can go into the firebase object without
     * any issues.
     *
     * @param unencodedEmail
     * @return
     */
    public static final String encodeEmail(String unencodedEmail) {
        if (unencodedEmail == null) return null;
        return unencodedEmail.replace(".", ",");
    }

    public static String getStackTrace(final Throwable throwable) {
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw, true);
        throwable.printStackTrace(pw);
        return sw.getBuffer().toString();
    }

//    public static String toPythonTuple(Tuple tuple){
//
//        String pythontuple = "("+tuple.toString().replace("[","").replace("]","")+")";
//
//        return pythontuple;
//    }
//
//    public static boolean isEnglish(String charaString){
//
//        return charaString.matches("^[a-zA-Z]*");
//
//    }
//
//    public static String tupleConcat(Tuple tuple1, Tuple tuple2){
//
//        String pythontuple = "(" +tuple1.toString().replace("[","").replace("]","")+
//                ", "+tuple2.toString().replace("[","").replace("]","")+
//                ")";
//
//        return pythontuple;
//    }

    public static boolean haveNetworkConnection(Context context) {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    public static String getActivityStringType(String activityString) {

        switch (activityString) {

            case TransportationModeStreamGenerator.TRANSPORTATION_MODE_NAME_ON_FOOT:
                return "walk";
            case TransportationModeStreamGenerator.TRANSPORTATION_MODE_NAME_ON_BICYCLE:
                return "bike";
            case TransportationModeStreamGenerator.TRANSPORTATION_MODE_NAME_IN_VEHICLE:
                return "car";
            case TransportationModeStreamGenerator.TRANSPORTATION_MODE_NAME_NO_TRANSPORTATION:
                return "static";
            default:
                return "";
        }
    }

    protected boolean areDatesEqual(String LOG_TAG, long currentTime, long previousTime) {
        Log.d(LOG_TAG, "Checking if the both dates are the same");

        Calendar currentDate = Calendar.getInstance();
        Calendar previousDate = Calendar.getInstance();

        currentDate.setTimeInMillis(currentTime);
        previousDate.setTimeInMillis(previousTime);
        Log.d(LOG_TAG, "Current Year:" + currentDate.get(Calendar.YEAR) + " Previous Year:" + previousDate.get(Calendar.YEAR));
        Log.d(LOG_TAG, "Current Day:" + currentDate.get(Calendar.DAY_OF_YEAR) + " Previous Day:" + previousDate.get(Calendar.DAY_OF_YEAR));
        Log.d(LOG_TAG, "Current Month:" + currentDate.get(Calendar.MONTH) + " Previous Month:" + previousDate.get(Calendar.MONTH));

        boolean sameDay = (currentDate.get(Calendar.YEAR) == previousDate.get(Calendar.YEAR)) &&
                (currentDate.get(Calendar.DAY_OF_YEAR) == previousDate.get(Calendar.DAY_OF_YEAR)) &&
                (currentDate.get(Calendar.MONTH) == previousDate.get(Calendar.MONTH));

        if (sameDay)
            Log.d(LOG_TAG, "it is the same day, should not create a new object");
        else
            Log.d(LOG_TAG, "it is not the same day - a new day, should create a new object");
        return sameDay;
    }

    public static String getDateCurrentTimeZone(long timestamp) {
        try {
            Calendar calendar = Calendar.getInstance();
            TimeZone tz = TimeZone.getDefault();
            calendar.setTimeInMillis(timestamp);
            calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date currenTimeZone = (Date) calendar.getTime();
            return sdf.format(currenTimeZone);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }




}