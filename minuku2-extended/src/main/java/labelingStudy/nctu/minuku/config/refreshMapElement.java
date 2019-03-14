package labelingStudy.nctu.minuku.config;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import labelingStudy.nctu.minuku.Utilities.CSVHelper;

/**
 * Created by chiaenchiang on 17/12/2018.
 */

public class refreshMapElement {

    Timer elementTimer;
    private static long EXPIRED_TIME_IN_SEC = 5l*60;
    public static Map<Integer, ArrayList<Date>> elementMap = new HashMap<>();



    public refreshMapElement(int seconds) {
        elementTimer = new Timer();
        elementTimer.schedule(new Reminder(), 0, seconds * 1000);
    }

    class Reminder extends TimerTask {
        public void run() {

            // We are checking for expired element from map every second
            clearExipredElementsFromMap(elementMap);

            // We are adding element every second
//            addElement();

        }
    }

    public void addElement(int element) {
        addElementToMap(element , elementMap);
    }

    // Check for element's expired time. If element is > 5 seconds old then remove it
    private static void clearExipredElementsFromMap(Map<Integer, ArrayList<Date>> map) {
        Date currentTime = new Date();
        Date actualExpiredTime = new Date();

        // if element time stamp and current time stamp difference is 5 second then delete element
        actualExpiredTime.setTime(currentTime.getTime() - EXPIRED_TIME_IN_SEC * 1000l);
        System.out.println("Map size:" + map.size());

        Iterator<Map.Entry<Integer, ArrayList<Date>>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, ArrayList<Date>> entry = iterator.next();
            ArrayList<Date> element = entry.getValue();

            while (element.size() > 0
                    && element.get(0).compareTo(actualExpiredTime) < 0) {
                storeToMapCSV(entry.getKey(),currentTime.toString(),"DeleteRefreshMap.csv");

                log("----------- Element Deleted: " + entry.getKey());

                element.remove(0);
            }

            if (element.size() == 0) {
                iterator.remove();
            }
        }
    }
    public static Boolean checkIfExist(Integer elementToCompare){
        Iterator<Map.Entry<Integer, ArrayList<Date>>> iterator = elementMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, ArrayList<Date>> entry = iterator.next();

            if(entry.getKey() == elementToCompare) {
                return true;
            }
        }
        return false;
    }


    // Adding new element to map with current timestamp
    private static void addElementToMap(Integer digit, Map<Integer, ArrayList<Date>> myMap) {
        ArrayList<Date> list = new ArrayList<>();
        myMap.put(digit, list);
        Date date = new Date();
        list.add(date);

        storeToMapCSV(digit,date.toString(),"AddRefreshMap.csv");
        log("+++++++++++ Element added:" + digit + "\n");
    }

    private static void log(String string) {
        System.out.println(string);

    }
    public static void storeToMapCSV(int ele,String time,String name){
        JSONObject object = new JSONObject();
        try {
            object.put("pack",ele);
            object.put("time",time);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        CSVHelper.storeToCSV(name ,object.toString());
    }




}
