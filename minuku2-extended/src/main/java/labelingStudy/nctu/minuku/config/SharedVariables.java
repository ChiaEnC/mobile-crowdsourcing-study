package labelingStudy.nctu.minuku.config;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by chiaenchiang on 15/12/2018.
 */

public class SharedVariables {
    // communication between notification Listener Service  and Accessibility Service
    public static Integer nhandle_or_dismiss = -1;

    public static Boolean ifClickedNoti = false;
    public static String notiTitle= "NA";
    public static String notiSubText = "NA";
    public static String notiText = "NA";
    public static String notiTickerText = "NA";
    public static String notiPack = "NA";
    public static String notiReason = "NA";
    public static String notiTitleForRandom = "";
    public static String notiTextForRandom = "";
    public static String notiPackForRandom = "";
    public static Long notiPostedTimeForRandom = Long.valueOf(0);

    public  static String extraForQ = "";
//    public static class nPostInfo {
//        public long postedTime = 0;
//        public Integer nPost = -1;
//        public void setPostedTime(long t){
//            this.postedTime = t;
//        }
//        public void setPostedNum(int num){
//            this.nPost = num;
//        }
//        public long getPostedTime(){
//            return this.postedTime;
//        }
//        public long getPostedNum(){
//            return this.nPost;
//        }
//
//    }
//
//
//    public static Boolean ifPreviousShown(long nowTime,int targetPackCode){
//
//        for(nPostInfo tmp : nPostArray){
//            // previous 1 minutes
//            if(tmp.postedTime<nowTime && tmp.postedTime>nowTime-( 60 * 1000)){
//                    if(targetPackCode == tmp.nPost){
//                        return true;
//                    }
//            }
//        }
//        return false;
//    }
    public static refreshMapElement notiList = new refreshMapElement(2*60);  //每兩分鐘refresh一次 // 存的資料為前五分鐘得資料



//    public static void cleanPostArray(){
//        nPostArray.clear();
//    }
//    public static List<nPostInfo> nPostArray = new ArrayList<>();

    // communication between Accessibility Service and FloatingButton Activities
    public static Boolean ifClickedFAB = false;
    public static Boolean ifUserStop = false;
    // communication between Background Recording Activity  and Others
    public static Boolean ifRecordingRightNow = false;

    // communitcation between recording Activity and others
    public static String videoFileName = Constants.DEVICE_ID;
    public static Integer videoCount = 0;

    // alarm and others
    public static boolean canSentNoti = true;  //with accessibility service
    public static boolean canSentNotiMC = true;
    public static boolean canSentNotiMCNoti = true;
    public static ArrayList<String> pullcontent = new ArrayList<String>();
    public static boolean NSHasPulledDown = false;

    public static final String RESET = "RESET";
    public static final String SURVEYALARM = "SURVEYALARM";
    public static final String RANDOMSURVEYALARM = "RANDOMSURVEY";
    public static final String SURVEYDELETEALARM = "SURVEYDELETEALARM";

    public static final String MCNOTIALAEM = "MCNOTIALAEM";
    // AnswerActivity and others
    public static Integer todayMCount = 0;
    public static Integer todayNCount = 0;
    public static Integer dayCount = 1;
    public static Integer allMCount = 0;
    public static Integer allNCount = 0;
    public static String todayMCountString = "todayMCountString";
    public static String todayNCountString = "todayNCountString";
    public static String dayCountString = "dayCountString" ;
    public static String allMCountString ="allMCountString";
    public static String allNCountString = "allNCountString";


    static public int []everyDayMrecord =  new int[50];
    static public int []everyDayNrecord =  new int[50];


    // NetworkStateChecker
    public static Long startAppHour = Long.valueOf(0);


    // Questionnaires and repsonse record
 //   public static Long generateTimeLong = Long.valueOf(0);
//    public  static Long  startAnswerTime = Long.valueOf(0);
//    public  static Long  finishAnswerTime = Long.valueOf(0);
//    public  static boolean  ifComplete = false;
  //  public static Long qGenerateTime = Long.valueOf(0);


    //Accessiblity Service and Questionnaires
    public static String appNameForQ = "NA";
    public static String timeForQ = "";
    public static Integer questionaireType = -1;
    public static Boolean canFillQuestionnaire = false;
    // Accessiblity Service shared to others
    public static  String visitedApp = "NA";
    public static Integer relatedId = 0;

   //questionnaire
    public static ArrayList<Integer> pageRecord = new ArrayList();

   //noti and questionnaire
    public static String NotiInfoForQ="";
    // alarm
    public static Boolean resetFire = false;
    public static Boolean survey1Fire = false; //8
    public static Boolean survey2Fire = false;  //10
    public static Boolean survey3Fire = false;  //12
    public static Boolean survey4Fire = false;  //14
    public static Boolean survey5Fire = false;  //16
    public static Boolean survey6Fire = false;  //18
    public static Boolean survey7Fire = false;  //20
    public static Boolean survey8Fire = false;  //22
    public static Boolean survey9Fire = false;
    public static Boolean survey10Fire = false;
   // public static Boolean survey11Fire = false;

//    public static Boolean survey11Fire = false;  //for random


    public static int[] hour = {8,9 ,11,13,15,16,18,19,21,22};
    public static int[] min =  {0,30,0 ,30,0,30,0,30,0,30};
    public static int nextTimeAlarmRanHourStart = -1;
    public static int nextTimeAlarmRanMinStart = -1;
    public static int nextTimeRadomAlarmNumber = -1;

    // shared functions
    public static String getReadableTime(long time){

        SimpleDateFormat sdf_now = new SimpleDateFormat(Constants.DATE_FORMAT_for_storing);
        String currentTimeString = sdf_now.format(time);
        return currentTimeString;
    }

    // accessiblity
    public static final String map = "map(地圖)";
    public static final String crowdsource = "crowdsource";


    // NetworkStateChecker
    public static Long getReadableTimeLong(long time){

        SimpleDateFormat sdf_now = new SimpleDateFormat(Constants.DATE_FORMAT_for_storing);
        String currentTimeString = sdf_now.format(time);
        String[] splited = currentTimeString.split("\\s+");
        String[]day = splited[0].split("-");
        Long year = Long.valueOf(day[0]);
        Long month = Long.valueOf(day[1]);
        Long dayLong = Long.valueOf(day[2]);



        String[]timeString = splited[1].split(":");
        Long hour = Long.valueOf(timeString[0]);
        Long minute = Long.valueOf(timeString[1]);
        Long second = Long.valueOf(timeString[2]);
        Long finalTime = year*1000000+month*10000+dayLong*100+hour;

        return finalTime;
    }

    // fragments

//    public static int MapAnswerPositiontoId(int questionid,int pos){
//        int[]questionSize = {0,2,1,4,11,19,4,1,9,13,  4,11,19,4,1,9,    2,2,5,6,1,4,11,19,4,1,9};
//        int answerId = 1;
//        for(int i=1;i<questionid ;i++){
//
//            answerId+= questionSize[i];
////            System.out.println(questionSize[i]);
//        }
//        answerId+=pos;
//        if(questionid>=10){
//            answerId+=35;// offset
//        }
//        if(questionid>=16){
//            answerId+=51;
//        }
//
//
//        return answerId;
//    }





}
