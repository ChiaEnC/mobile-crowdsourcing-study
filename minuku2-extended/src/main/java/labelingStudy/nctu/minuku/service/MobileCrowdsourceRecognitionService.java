package labelingStudy.nctu.minuku.service;

import android.app.KeyguardManager;
import android.content.Context;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

import labelingStudy.nctu.minuku.config.Constants;

import static labelingStudy.nctu.minuku.config.SharedVariables.pullcontent;

/**
 * Created by chiaenchiang on 09/11/2018.
 */

public class MobileCrowdsourceRecognitionService {

   static String TAG="mc";



    static String[]KeyWords = new String[]{"answer questions",
            "simple yes/no questions", "helpful","rate","recommend","review","share",
            "experience","Q&A","missing","need","verify","add a review","local guide","contribute",
           "image label verification","sentiment evaluation","handwriting recognition",
            "translation","translation validation","landmarks","image capture","be one of the labels for this image",
            "what kine of emotion is expressed in the text below?","type text here","type translation here","are these translations correct?",
            "clearly identificable in the photo?","discover","submit", "yes", "no","edit","skip","有人提出問題","你的每日貢獻","覺得","怎麼樣"," 你的評論","分享","幫助其他人瞭解這個地方","留下評論","評論","相片","回答",
            "協助","幫忙","解答"};
    static String[]blackList = new String[]{
            "chrome","phone"," play","music","manager","facebook","line","weather","clock","meessenger","sound","youtube",
            "-","android.widget.textview","Wi‑Fi","only","switch","off","drag down to dismiss","android.webkit.webview","android.widget","android.view.view","view",
            "android.widget.button","android.widget.image","google search","view","android","widget","textview","button","image","wizard","voicetube","webstorage","progressbar",
            "framelayout","listview","android.support.v4.widget.drawerLayout",":[]","imageview","relativelayout",".support.v7..recycler",
            // for map searching or timeline
            "were you here","am","pm","commute"

    };

    static String[]unwantedPack = new String[]{"systemui","weather","mobilecrowdsourceStudy","settings"};
    public static String[]strictKeyWords = new String[]{
            "helpful","does this place","is there","which photo do you think is more helpful","is this place","thanks for answering",
            "what would you like to help with","new task displayed","is same day delivery","your contribution add so much","is there","can you","share details of your own experience",
            "are these hours correct","help others","what's the official website","phone numbers",
            "taipei","keelung","hsinchu","taichung","tainan","kaohsiung","yilan","taoyuan","miaoli","changhua","nantou","yunlin","chiayi","pingtung","taitung","hualien","penghu",
            "clearly identifiable in the photo","used under","loading task","be one of the labels","what kind of emotion"
    };




    static String[]blackListCH = new String[]{
            "chrome","phone"," play","music","manager","facebook","line","天氣","時鐘","meessenger","sound","youtube",
            "-","android.widget.textview","Wi‑Fi","only","switch","off","drag down to dismiss","android.webkit.webview","android.widget","android.view.view","view",
            "android.widget.button","android.widget.image","google search","view","android","widget","textview","button","image","wizard","voicetube","webstorage","progressbar",
            "framelayout","listview","android.support.v4.widget.drawerLayout",":[]","imageview","relativelayout",".support.v7..recycler","主題","圖片庫","手電筒","[","]",
            "文件","日曆","智能管家","檔案管理","相機","相簿","科學園區行動精靈","簡報","美圖秀秀","聯絡人","計算機","訊息","設定","試算表","遊戲精靈","錄音程式","雲端硬碟",
            "電話","向下拖曳即可移除","在這裡搜尋","餐廳","咖啡"," 景點","更多","餐點價格低廉","本地人常去的用餐地點","適合團體用餐","探索","通勤","為你推薦","出發",
            // for map searching or timeline
            "were you here","am","pm","commute"


    };

    static String[]unwantedPackCH = new String[]{"systemui","天氣","mobilecrowdsourceStudy","設定"};


    public static String[]strictKeyWordsCH = new String[]{
            "你覺得哪一張相片比較有幫助","這裡提供","這個地方","如果你想","是否會選擇這裡","這裡適合","適合","是否有輪椅專用","無障礙停車位","餐飲","受旅客歡迎"
            ,"感謝您的回答","你的貢獻讓","Google 受益良多","這裡","景色優美","你認為","這裡的菜單價格便宜嗎","有洗手間","適合","獨自用餐","室外座位","外帶服務",
            "外送服務","外帶","經常","需要等候","餐桌服務","是否有","輪椅專用","座位","現場音樂演奏","多人聚會","很悠閒","吸引","時尚潮人","這裡可以","預訂","只能付現金",
            "如果","素食","是否選擇","喝到飽","室外","當天出貨服務","健康料理","輪椅族","無障礙入口","快速採買","可以","只點","舉辦","你會來這裡","帶外食","接受","訂位",
            "分享","心得","發佈","這個地點的親身經歷","名稱正確","有這個地點嗎","這個地點","永久停業","營業時間","官方網站","幫助其他人瞭解","這個地方的樣貌","是否可以使用",
            "圖片的標籤","使用方式符合","以下文字傳達的是哪一種情緒","在這裡輸入文字","在這裡輸入譯文","這些譯文正確嗎","在相片中能清楚辨識嗎","大小寫"
    };
    public static String[]NotificationScrollDownKeyWords = new String[]{
            // asus zenfone
            "全部清除" /* asus zenfone*/, "clear all","清除全部","解除鎖定","滑動開啟"
    };
    public static String[]NotificationScrollDownPackKeyWords = new String[]{
            // asus zenfone
            "systemui" /* asus zenfone*/
    };




    MobileCrowdsourceRecognitionService(){
        super();
    }

//    public static boolean ifunwantedPack(String str){
//        str = str.toLowerCase();
//        for(String tmp : unwantedPack) {
//            if(str.contains(tmp)){
//                return true;
//            }
//        }
//        return false;
//
//    }

    public static boolean ifunwantedPack(String str){
        str = str.toLowerCase();
        for(String tmp : unwantedPackCH) {
            if(str.contains(tmp)){
                return true;
            }
        }
        for(String tmp : unwantedPack) {
            if(str.contains(tmp)){
                return true;
            }
        }

        return false;

    }
    public static boolean ifScrollDownNShadePack(String pack){
        pack = pack.toLowerCase();
        for(String tmp : NotificationScrollDownPackKeyWords) {
            if(pack.contains(tmp)){
                return true;
            }
        }
        return false;
    }
    public static boolean ifScrollDownNotificationShade(String content){
        content = content.toLowerCase();
        for(String tmp : NotificationScrollDownKeyWords) {
            if(content.contains(tmp)){
               return true;
            }
        }

        return false;
    }

    public static Integer ifTextContainClickedPack(int packCode){
        String thingsToCompared = "";
        if (packCode == 3 ||packCode == 10 ||packCode == 11||packCode == 12||packCode == 14||packCode == 15||packCode == 1) {
             Log.d("newAccess","packCode : "+packCode);
            thingsToCompared = matchAppName(packCode);
            Log.d("newAccess","thingsToCompared : "+thingsToCompared);
                thingsToCompared = thingsToCompared.toLowerCase();
                for (String tmp : pullcontent) {
                    String lowerCase = tmp.toLowerCase();
                    if (lowerCase.contains(thingsToCompared)) {
                        Log.d("newAccess","return packCode : "+packCode);
                        return packCode;
                    }
                }
                return -1;
        }
         return -1;
    }

//    public static String clearBlackList(String str){
//        str = str.toLowerCase();
//        for (String tmpp : blackList) {
//            for(String temp:strictKeyWords) {
//                //在blacklist裡但不在strictKeywords裡
//                if (str.contains(tmpp)&&!str.contains(temp))
//                    str = str.replace(tmpp, "");
//            }
//        }
//        return str;
//
//    }
    public static String clearBlackList(String str){
        str = str.toLowerCase();
        for (String tmpp : blackListCH) {
            for(String temp:strictKeyWordsCH) {
                //在blacklist裡但不在strictKeywords裡
                if (str.contains(tmpp)&&!str.contains(temp))
                    str = str.replace(tmpp, "");
            }
        }
        for (String tmpp : blackList) {
            for(String temp:strictKeyWords) {
                //在blacklist裡但不在strictKeywords裡
                if (str.contains(tmpp)&&!str.contains(temp))
                    str = str.replace(tmpp, "");
            }
        }
        return str;

    }
    public static boolean ifMobileCrowdsourceTask(Context context,String str){
        //檢查是否為mobile crowdsource task
        str = str.toLowerCase();
        for(String tmp : KeyWords) {
            if (str.contains(tmp)){
                Log.d(TAG,"check ifMobileCrowdsourceTask : "+tmp);
                //showToastMethod(context,"notification : "+tmp);
                return true;

            }
        }
        return false;

    }

//    public static boolean ifDoneStrictInspect(Context context,String str){
//        str = str.toLowerCase();
//        for(String temp:strictKeyWords){
//            if(str.contains(temp)){
//               // showToastMethod(context,"strictKey : "+temp);
//                return true;
//            }
//        }
//        return false;
//    }
    public static boolean ifDoneStrictInspect(Context context,String str){
        str = str.toLowerCase();
        for(String temp:strictKeyWordsCH){
            if(str.contains(temp)){
                // showToastMethod(context,"strictKey : "+temp);
                return true;
            }
        }
        for(String temp:strictKeyWords){
            if(str.contains(temp)){
                // showToastMethod(context,"strictKey : "+temp);
                return true;
            }
        }
        return false;
    }


    //
    public static final class AppCode {
        public static final int FACEBOOK_MESSENGER_CODE = 1;
        public static final int MESSENGER_LITE_CODE = 2;
        public static final int LINE_CODE = 3;
        public static final int GOOGLE_MAP = 4;
        public static final int GOOGLE_CROWDSOURCE = 5;
        public static final int HOME = 6;
        public static final int SYSTEM_UI= 7;
        public static final int WEATHER = 8;
        public static final int MC = 9;
        public static final int GMAIL = 10;
        public static final int INSTAGRAM = 11;
        public static final int YOUTUBE = 12;
        public static final int FACEBOOK_PAGE_CODE = 13;
        public static final int KKBOX = 14;
        public static final int SLACK = 15;
        public static final int GMS = 16;
        public static final int QUICKSEARCH = 17;

        public static final int OTHER_KIND_OF_NOTIFICATION = 20;

    }
    public static final class ApplicationPackageNames {
        public static final String FACEBOOK_MESSENGER_PACK_NAME = "com.facebook.orca";
        public static final String MESSENGER_LITE_PACK_NAME = "com.facebook.mlite";
        public static final String FACEBOOK = "com.facebook.katana";
        public static final String LINE_PACK_NAME = "jp.naver.line.android";
        public static final String LINE2_PACK_NAME = "jp.naver.line.androie";
        public static final String LINE2_LITE_PACK_NAME = "com.linecorp.linelite";
        public static final String GOOGLE_MAP = "google.android.apps.maps";
        public static final String GOOGLE_CROWDSOURCE = "google.android.apps.village.boond";
        public static final String HOME = "launcher";
        public static final String SYSTEM_UI = "systemui ";
        public static final String WEATHER = "weather ";
        public static final String MC = "mobilecrowdsourceStudy.nctu";
        public static final String GMAIL ="com.google.android.gm";
        public static final String INSTAGRAM = "com.instagram.android";
        public static final String KKBOX = "com.skysoft.kkbox.android";
        public static final String YOUTUBE = "com.google.android.youtube";
        public static final String SLACK = "com.Slack";
        public static final String GMS = "com.google.android.gms";
        public static final String QUICKSEARCH = "google.android.googlequicksearchbox";



    }
    public static String matchAppName(int code){
        if(code == 1 ||code == 13){
            return Constants.FACEBOOK;
        }else if(code == 2){
            return Constants.MESSENGER;
        }else if(code == 3){
            return Constants.LINE;
        }else if(code == 4){
            return Constants.GOOGLE_MAP;
        }else if(code == 5){
            return Constants.GOOGLE_CROWDSOURCE;
        }else if(code == 10){
            return Constants.GMAIL;
        }else if(code == 11){
            return Constants.INSTAGRAM;
        }else if(code == 12){
            return Constants.YOUTUBE;
        }else if(code == 14){
            return Constants.KKBOX;
        }else if(code == 15){
            return Constants.SLACK;
        }else return Constants.NON_MC;
    }
    public static int matchAppCode(String pack) {

        if (pack.contains(ApplicationPackageNames.FACEBOOK_MESSENGER_PACK_NAME)) {
            return (AppCode.FACEBOOK_MESSENGER_CODE);
        } else if(pack.contains(ApplicationPackageNames.FACEBOOK)){
            return (AppCode.FACEBOOK_PAGE_CODE);
        } else if (pack.contains(ApplicationPackageNames.LINE_PACK_NAME)) {
            return (AppCode.LINE_CODE);
        } else if (pack.contains(ApplicationPackageNames.LINE2_PACK_NAME)) {
            return (AppCode.LINE_CODE);
        } else if (pack.contains(ApplicationPackageNames.LINE2_LITE_PACK_NAME)) {
            return (AppCode.LINE_CODE);
        } else if (pack.contains(ApplicationPackageNames.MESSENGER_LITE_PACK_NAME)) {
            return (AppCode.MESSENGER_LITE_CODE);
        } else if (pack.contains(ApplicationPackageNames.GOOGLE_MAP)) {
            return (AppCode.GOOGLE_MAP);
        } else if (pack.contains(ApplicationPackageNames.GOOGLE_CROWDSOURCE)) {
            return (AppCode.GOOGLE_CROWDSOURCE);
        }else if (pack.contains(ApplicationPackageNames.HOME)){
            return AppCode.HOME;
        }else if(pack.contains(ApplicationPackageNames.SYSTEM_UI)){
            return AppCode.SYSTEM_UI;
        }else if(pack.contains(ApplicationPackageNames.WEATHER)){
            return AppCode.WEATHER;
        }else if(pack.contains(ApplicationPackageNames.MC)){
            return AppCode.MC;
        }else if (pack.equals(ApplicationPackageNames.GMAIL)){
            return AppCode.GMAIL;
        }else if (pack.contains(ApplicationPackageNames.INSTAGRAM)){
            return AppCode.INSTAGRAM;
        }else if (pack.contains(ApplicationPackageNames.KKBOX)){
            return AppCode.KKBOX;
        }else if (pack.contains(ApplicationPackageNames.YOUTUBE)){
            return AppCode.YOUTUBE;
        }else if (pack.contains(ApplicationPackageNames.SLACK)){
            return AppCode.SLACK;
        }else if (pack.equals(ApplicationPackageNames.GMS)){
            return AppCode.GMS;
        }else if (pack.contains(ApplicationPackageNames.QUICKSEARCH)){
            return AppCode.QUICKSEARCH;
        }
        else
            return AppCode.OTHER_KIND_OF_NOTIFICATION;

    }

    public static boolean ifScreenLight(PowerManager pm){
        boolean isScreenOn = pm.isScreenOn();
        return isScreenOn;
    }
    public static boolean ifScreenLock(KeyguardManager myKM){
        if( myKM.inKeyguardRestrictedInputMode()) {
            return true;
        } else {
            return false;
        }
    }



    public static boolean CheckifaboveThreshold(List<Integer> appStack , Integer[] target, double threshold){
      //  Log.d(TAG,"checkMost");
        double len = appStack.size();
        double count = 0;
        for (Integer i : appStack) {
            for(Integer tar : target){
                if(i==tar) count++;
            }
        }
     //   Log.d(TAG,"len count "+len+" "+count);
        if(len != 0) {
            if (count/len >= threshold) {  //大於60%
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    public static void showToastMethod(Context context,String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }




}
