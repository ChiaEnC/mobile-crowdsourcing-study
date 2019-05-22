package mobilecrowdsourceStudy.nctu.minuku_2.questions;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;

import labelingStudy.nctu.minuku.config.Constants;
import mobilecrowdsourceStudy.nctu.R;

import static labelingStudy.nctu.minuku.config.Constants.LEN_PREFIX;
import static labelingStudy.nctu.minuku.config.Constants.VAL_PREFIX;
import static labelingStudy.nctu.minuku.config.Constants.everyDayMrecordString;
import static labelingStudy.nctu.minuku.config.Constants.everyDayNrecordString;
import static labelingStudy.nctu.minuku.config.SharedVariables.dayCount;
import static labelingStudy.nctu.minuku.config.SharedVariables.everyDayMrecord;
import static labelingStudy.nctu.minuku.config.SharedVariables.everyDayNrecord;
import static labelingStudy.nctu.minuku.config.SharedVariables.todayMCount;
import static labelingStudy.nctu.minuku.config.SharedVariables.todayNCount;

public class AnswersActivity extends AppCompatActivity
{
    Context context;
//    LinearLayout resultLinearLayout;
//    List<QuestionDataRecord> questionsList = new ArrayList<>();
//    List<QuestionWithAnswersDataRecord> questionsWithAllChoicesList = new ArrayList<>();
    private LineChart mChart;
    int totalCount;


//    private int dayCount;
//    private  int todayMCount;
//    private int todayNCount;
//    private SharedPreferences sharedPrefs;
    static boolean firstTime = true;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answers);
       // achievementText = findViewById(R.id.answerTextTitle);
        context = this;
//        sharedPrefs = getSharedPreferences(Constants.sharedPrefString, MODE_PRIVATE);
//        todayMCount = sharedPrefs.getInt("todayMCount",0);
//        todayNCount = sharedPrefs.getInt("todayNCount",0);
////        int totalCount = sharedPrefs.getInt("totalCount",0);
//        dayCount = sharedPrefs.getInt("dayCount",0);

        // update everday record

//        everyDayNrecord.add(0,5);

//        everyDayNrecord.add(3,1);
//        everyDayNrecord.add(4,2);
//        everyDayMrecord.add(0,3);
//        everyDayMrecord.add(1,4);
//        everyDayMrecord.add(2,2);
//        everyDayMrecord.add(3,4);
//        dayCount = 4;
        if(firstTime){
            for(int i =0;i<50;i++) {
                everyDayMrecord[i] = 0;
                everyDayNrecord[i]=0;
            }
            firstTime = false;
        }
//        everyDayNrecord.put(1,2);
//        everyDayNrecord.put(1,3);
        Log.d("alarmClock","dayCount : "+dayCount);
        Log.d("alarmClock","todayNCount : "+todayNCount);
        Log.d("alarmClock","todayMCount : "+todayMCount);
        everyDayNrecord[dayCount] = todayNCount;
        everyDayMrecord[dayCount]= todayMCount;
        storePrefIntArray(everyDayNrecordString,everyDayNrecord);
        storePrefIntArray(everyDayMrecordString,everyDayMrecord);

        initiateChart();

    }

    public void storePrefIntArray(String name, int[] array){
        SharedPreferences.Editor edit= context.getSharedPreferences(Constants.sharedPrefString, Context.MODE_PRIVATE).edit();
        edit.putInt(LEN_PREFIX + name, array.length);
        int count = 0;
        for (int i: array){
            edit.putInt(VAL_PREFIX + name + count++, i);
        }
        edit.commit();
    }

//    @Override
//    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
//        super.onSaveInstanceState(outState, outPersistentState);
//
//    }

    @Override
    protected void onResume() {
        super.onResume();



    }

    @SuppressLint("ResourceType")
    private void initiateChart(){
        mChart = (LineChart) findViewById(R.id.lineChart);
//
//        // mChart.setDescription();
//        mChart.setNoDataText("no data");
//        mChart.setDefaultFocusHighlightEnabled(true);
        Description des = mChart.getDescription();
        des.setEnabled(false);
        mChart.setTouchEnabled(true);
        mChart.setDragEnabled(true);
//        mChart.setScaleEnabled(true);
        mChart.setDrawGridBackground(false);
        mChart.setPinchZoom(true);
//        mChart.setBackgroundColor(Color.WHITE);
//        LineData data = new LineData();
//        data.setValueTextColor(Color.WHITE);
//        mChart.setData(data);
        Legend l = mChart.getLegend();
        l.setFormSize(10f); // set the size of the legend forms/shapes
        l.setForm(Legend.LegendForm.CIRCLE); // set what type of form/shape should be used
        l.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);
        l.setTextSize(12f);
        l.setTextColor(Color.BLACK);
        l.setXEntrySpace(2); // set the space between the legend entries on the x-axis
        l.setYEntrySpace(5); // set the space between the legend entries on the y-axis

//        // modify the legend ...
//        l.setForm(Legend.LegendForm.LINE);
////        l.setTypeface(tf);
//        l.setTextColor(Color.WHITE);
//

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);//设置X轴标签显示位置
        xAxis.setDrawGridLines(false);//不绘制格网线
        xAxis.setGranularity(1f);//设置最小间隔，防止当放大时，出现重复标签。
        xAxis.setLabelCount(12);//设置x轴显示的标签个数
        xAxis.setAxisLineWidth(2f);//设置x轴宽度, ...其他样式

        YAxis leftAxis = mChart.getAxisLeft();//取得左侧y轴
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);//y轴标签绘制的位置
        leftAxis.setDrawGridLines(false);//不绘制y轴格网线
        leftAxis.setDrawLabels(false);//不显示坐标轴上的值, ...其他样式

//
        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(false);
        addEntity();
     }
     private void addEntity(){
//            LineData data = mChart.getLineData();

         //TODO add every day result
            ArrayList<Entry>yValues = new ArrayList<>();
            ArrayList<Entry>yValues2 = new ArrayList<>();
            for(int day = 0; day< everyDayMrecord.length; day++){
                if(everyDayMrecord!=null) {
                    Log.d("alarmClock","day : "+day+" amount : "+everyDayMrecord[day]);
                    totalCount += everyDayMrecord[day];
                    if(day<dayCount+3) {
                        yValues.add(new Entry(day, everyDayMrecord[day]));
                    }
                }
                if(everyDayNrecord!=null) {
                    Log.d("alarmClock","day : "+day+" amount : "+everyDayNrecord[day]);
                    totalCount += everyDayNrecord[day];
                    if(day<dayCount+3) {
                        yValues2.add(new Entry(day, everyDayNrecord[day]));
                    }
                }


            }
         TextView stat_1 = findViewById(R.id.stat_1);
         stat_1.setText(String.valueOf(totalCount));
         TextView stat_2 = findViewById(R.id.stat_2);
         stat_2.setText(String.valueOf(todayMCount+todayNCount));
         TextView stat_3 = findViewById(R.id.stat_3);
         stat_3.setText(String.valueOf(dayCount));
         LineDataSet set1,set2;
         set1 = new LineDataSet(yValues,"Mobile crowdsource");
         set1.setColor(Color.RED);
         set1.setDrawCircles(false);
         set1.setLineWidth(3f);
         set2 = new LineDataSet(yValues2,"Non Mobile crowdsource");
         LineData data = new LineData(set1,set2);
         mChart.setData(data);
         mChart.setVisibleXRangeMaximum(6);
         mChart.moveViewToX((data.getXMin()+data.getXMax())/2);
//            yValues.add(new Entry(0,60f));
//            yValues.add(new Entry(1,70f));
//            yValues.add(new Entry(2,80f));
//            yValues.add(new Entry(3,90f));
//            yValues.add(new Entry(4,30f));
//            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
//           // dataSets.add(createSet(yValues));
//            LineData data = new LineData(dataSets);


     }

}