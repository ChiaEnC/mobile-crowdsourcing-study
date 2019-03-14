package mobilecrowdsourceStudy.nctu.minuku_2.questions.fragments;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.IndicatorStayLayout;
import com.warkiz.widget.IndicatorType;
import com.warkiz.widget.OnSeekChangeListener;
import com.warkiz.widget.SeekParams;
import com.warkiz.widget.TickMarkType;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import labelingStudy.nctu.minuku.DBHelper.appDatabase;
import mobilecrowdsourceStudy.nctu.R;
import mobilecrowdsourceStudy.nctu.minuku_2.questions.QuestionActivity;
import mobilecrowdsourceStudy.nctu.minuku_2.questions.questionmodels.AnswerOptions;
import mobilecrowdsourceStudy.nctu.minuku_2.questions.questionmodels.QuestionsItem;

import static com.warkiz.widget.SizeUtils.dp2px;
import static labelingStudy.nctu.minuku.config.Constants.At;
import static labelingStudy.nctu.minuku.config.SharedVariables.appNameForQ;
import static labelingStudy.nctu.minuku.config.SharedVariables.getReadableTime;
import static labelingStudy.nctu.minuku.config.SharedVariables.pageRecord;
import static labelingStudy.nctu.minuku.config.SharedVariables.questionaireType;

/**
 * Created by chiaenchiang on 16/11/2018.
 */

public class SeekBarsFragment extends Fragment implements SeekBar.OnSeekBarChangeListener {
    private String TAG = "checkSeekBar";
    private final ArrayList<IndicatorSeekBar> seekBarsArrayList = new ArrayList<>();
    private boolean screenVisible = false;
    private QuestionsItem radioButtonTypeQuestion;
    private FragmentActivity mContext;
    private Button nextOrFinishButton;
    //private Button previousButton;
    private TextView questionSKTypeTextView;
    private RadioGroup radioGroupForChoices;
    private SeekBar seekBarGroup;
    private int atLeastOneChecked = 0;
    private List<Boolean> checkedBefore = new ArrayList<>();
    String appName = "";
    String enterTime = "";
    appDatabase db;
    private String questionId = "";
    private int currentPagePosition = 0;
    private int clickedRadioButtonPosition = 0;
    private String qState = "0";
    private LinearLayout seekBarsLinearLayout;
    private TextView distance;
    LinearLayout content;
    int relatedId;
    private int clickedSeekBarPosition = 0;

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_seek_bars, container, false);

        db = appDatabase.getDatabase(getActivity());

        nextOrFinishButton = rootView.findViewById(R.id.nextOrFinishButton);
        questionSKTypeTextView = rootView.findViewById(R.id.questionSKTypeTextView);
        seekBarsLinearLayout = (LinearLayout) rootView.findViewById(R.id.seekBarsLinearLayout);

        nextOrFinishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!pageRecord.contains(currentPagePosition))
                    pageRecord.add(currentPagePosition);
                if (currentPagePosition == ((QuestionActivity) mContext).getTotalQuestionsSize()) {
                /* Here, You go back from where you started OR If you want to go next Activity just change the Intent*/
                    Intent returnIntent = new Intent();
                    mContext.setResult(Activity.RESULT_OK, returnIntent);
                    mContext.finish();
                }
                else
                {
                    ((QuestionActivity) mContext).nextQuestion(1);
                }
            }
        });
        //previousButton.setOnClickListener(view -> mContext.onBackPressed());







        // Inflate the layout for this fragment
        return rootView;

    }
    public Integer check_radio_answer(String questionId){
        String first,second,third,fourth,other;
        first = db.questionWithAnswersDao().isChecked(questionId,"0");
        second = db.questionWithAnswersDao().isChecked(questionId,"1");
        third = db.questionWithAnswersDao().isChecked(questionId,"2");
        fourth = db.questionWithAnswersDao().isChecked(questionId,"3");
        other = db.questionWithAnswersDao().isChecked(questionId,"4");


        if(first!=null) {
            Log.d("qskip"," radioBox first :  "+first);
            if (first.equals("1")) {
                return 0;   // 是
            }
        }
        if(second!=null) {
            Log.d("qskip"," radioBox second :  "+second);
            if (second.equals("1")) {
                return 1;  //否
            }
        }
        if(third!=null) {
            Log.d("qskip"," radioBox third :  "+third);
            if (third.equals("1")) {
                return 2;
            }
        }
        if(fourth!=null) {
            Log.d("qskip"," radioBox fourth :  "+fourth);
            if (fourth.equals("1")) {
                return 3;
            }
        }
        if(other!=null){
            if(other.length()>=1){
                return 4;
            }
        }
        return 0;
    }


    @SuppressLint("ResourceAsColor")
    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        mContext = (FragmentActivity) getActivity();
        String extraInfo = "";
        QuestionsItem seekBarTypeQuestion = null;

        if (getArguments() != null)
        {
            seekBarTypeQuestion = getArguments().getParcelable("question");
            questionId = String.valueOf(seekBarTypeQuestion != null ? seekBarTypeQuestion.getId() : 0);
            currentPagePosition = getArguments().getInt("page_position") + 1;
            appName = appNameForQ;
            enterTime = getArguments().getString("enterTimeForF");
            relatedId = getArguments().getInt("relatedIdF");
            extraInfo = getArguments().getString("extraInfo");
        }

            String apppendText = "";
        if(questionaireType!=0){
                apppendText = " ( "  + appName + " " + At + enterTime +" ( "+extraInfo +" ) "+" ) ";
            }
            else
                apppendText = " ( " + appName + " " + At + enterTime + " ) ";
          //  questionSKTypeTextView.setText(seekBarTypeQuestion != null ? seekBarTypeQuestion.getQuestionName() + '\n' + apppendText : "");
        String title = seekBarTypeQuestion != null ? seekBarTypeQuestion.getQuestionName()+'\n'+apppendText:"";
        //questionRBTypeTextView.setText(radioButtonTypeQuestion.getQuestionName()+'\n'+apppendText);
        setTextWithSpan(title,questionSKTypeTextView);
        /*Disable the button until any choice got selected*/


            List<AnswerOptions> seekBarChoices = Objects.requireNonNull(seekBarTypeQuestion).getAnswerOptions();

            seekBarsArrayList.clear();

            for (AnswerOptions choice : seekBarChoices) {
                IndicatorSeekBar seekBar = new IndicatorSeekBar(mContext);
                TextView textView = getTextView();
                textView.setText(choice.getName());
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                textView.setTextColor(ContextCompat.getColor(mContext, R.color.grey));
                textView.setPadding(10, 0, 10, 0);

                seekBarsLinearLayout.addView(textView);
                seekBar = IndicatorSeekBar
                        .with(getContext())
                        .max(5)
                        .min(0)
                        .tickCount(6)
                        .showTickMarksType(TickMarkType.SQUARE)
                        .tickMarksColor(R.color.color_gray)
                        .indicatorColor(getResources().getColor(R.color.colorPrimaryDark))
                        .indicatorTextColor(Color.parseColor("#ffffff"))
                        .showIndicatorType(IndicatorType.ROUNDED_RECTANGLE)
                        .thumbColor(getResources().getColor(R.color.colorPrimary))
                        .thumbSize(14)
                        .trackProgressColor(getResources().getColor(R.color.colorPrimaryDark, null))
                        .trackProgressSize(6)  //4
                        .trackBackgroundColor(getResources().getColor(R.color.grey, null))
                        .trackBackgroundSize(2)
                        .build();
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                params.leftMargin = 25;

                View view = new View(mContext);
                view.setBackgroundColor(ContextCompat.getColor(mContext, R.color.divider));
                view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1));
                IndicatorStayLayout StayLayout = new IndicatorStayLayout(getContext());
                StayLayout.attachTo(seekBar);
                seekBarsLinearLayout.addView(StayLayout, params);
                seekBarsArrayList.add(seekBar);

                seekBar.setOnSeekChangeListener(new OnSeekChangeListener() {
                    @Override
                    public void onSeeking(SeekParams p) {

                    }

                    @Override
                    public void onStartTrackingTouch(IndicatorSeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(IndicatorSeekBar seekBar) {

                        clickedSeekBarPosition = seekBarsArrayList.indexOf(seekBar);
                        saveActionsOfSeekBar();
                    }
                });


            }
            for(int i=0;i<seekBarsArrayList.size();i++){
                checkedBefore.add(i,false);
            }

            if(allCheckedBefore()){
                nextOrFinishButton.setEnabled(true);
                Log.d(TAG,"on create activity true");

            }else{
                nextOrFinishButton.setEnabled(false);
                Log.d(TAG,"on create activity false");
            }



        /* If the current question is last in the questionnaire then
        the "Next" button will change into "Finish" button*/
            if (currentPagePosition == ((QuestionActivity) mContext).getTotalQuestionsSize()) {
                nextOrFinishButton.setText(R.string.finish);
            } else {
                nextOrFinishButton.setText(R.string.next);
            }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {


    }
// 返回時也會儲存當時的資訊
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser)
    {
        super.setUserVisibleHint(isVisibleToUser);


        if (isVisibleToUser)
        {
            for (int i = 0; i < seekBarsArrayList.size(); i++)
            {
                    IndicatorSeekBar seekBar = seekBarsArrayList.get(i);
                    String cbPosition = String.valueOf(i);

                    String[] data = new String[]{questionId, cbPosition};
                    Observable.just(data)
                            .map(this::getTheStateOfSeekBar)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Observer<String>() {
                                @Override
                                public void onSubscribe(Disposable d) {

                                }

                                @Override
                                public void onNext(String s) {
                                    qState = s;
                                }

                                @Override
                                public void onError(Throwable e) {

                                }

                                @Override
                                public void onComplete() {
                                    seekBar.setProgress(Float.parseFloat(qState));

//                                    if (!nextOrFinishButton.isEnabled()) {
//                                        nextOrFinishButton.setEnabled(true);
//                                    }
                                }
                            });
                }
            }

    }

    private void setTextWithSpan(String questionPosition, TextView tv)
    {
        int slashPosition = questionPosition.indexOf("(");

        Spannable spanText = new SpannableString(questionPosition);
        spanText.setSpan(new RelativeSizeSpan(0.7f), slashPosition, questionPosition.length(), 0);
        tv.setText(spanText);
    }

    private String getTheStateOfSeekBar(String[] data)
    {
        return db.questionWithAnswersDao().isChecked(data[0], data[1]);
    }




    private void saveActionsOfSeekBar(){
        for (int i = 0; i < seekBarsArrayList.size(); i++)
        {
            if (i == clickedSeekBarPosition)
            {
                IndicatorSeekBar seekBar = seekBarsArrayList.get(i);
                if (seekBar.getProgress()!=0) {
                    checkedBefore.set(i,true);
//                    atLeastOneChecked = atLeastOneChecked + 1;
                }
//                else {
//                    atLeastOneChecked = atLeastOneChecked - 1;
//                    if (atLeastOneChecked <= 0)
//                        atLeastOneChecked = 0;
//                }
                    String sbPosition = String.valueOf(seekBarsArrayList.indexOf(seekBar));
                    String result = new Integer(seekBar.getProgress()).toString();
                    String[] data = new String[]{result, questionId, sbPosition};
                    insertAnswerInDatabase(data);
            }
        }

        if (allCheckedBefore())
        {
            nextOrFinishButton.setEnabled(true);

        } else
        {
            nextOrFinishButton.setEnabled(false);
        }
    }
    public boolean allCheckedBefore(){
        int num = 0;
        for(boolean tmp : checkedBefore){
            if(tmp == true){
                num++;
            }
        }
        Log.d(TAG,"number : "+num);
        Log.d(TAG,"size : "+seekBarsArrayList.size());
        if(num == seekBarsArrayList.size()){
            return true;
        }else
            return false;
    }



    private void insertAnswerInDatabase(String[] data)
    {
        Observable.just(data)
                .map(new Function<String[], Object>() {
                    @Override
                    public Object apply(String[] data1) throws Exception {
                        return SeekBarsFragment.this.insertingInDb(data1);
                    }
                })
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    private String insertingInDb(String[] data)
    {   String answerTime = getReadableTime(new Date().getTime());
        db.questionWithAnswersDao().updateQuestionWithChoice(data[0], data[1], data[2]);
       // String LastAnswerTime = db.questionWithAnswersDao().getLastTimeDetectedTime(data[1], data[2]);
        db.questionWithAnswersDao().updateQuestionWithDetectedTime(answerTime ,data[1], data[2]);
        //insertFinalAnswer(data[1],data[2], data[0],answerTime);
        return "";
    }
//    public void insertFinalAnswer(String questionId,String optionId,String selectState,String answerTime){
//        //第幾題、第幾個選項、第幾個解答、回答時間
//        Integer ansId = MapAnswerPositiontoId(Integer.parseInt(questionId),Integer.parseInt(optionId));
//        FinalAnswerDataRecord finalAnswerDataRecord = new FinalAnswerDataRecord();
//        finalAnswerDataRecord.setAnswerChoice(optionId);
//        finalAnswerDataRecord.setAnswerChoiceState(selectState);
//        finalAnswerDataRecord.setanswerId(String.valueOf(ansId));
//        finalAnswerDataRecord.setdetectedTime(answerTime);
//        finalAnswerDataRecord.setQuestionId(questionId);
//        finalAnswerDataRecord.setsyncStatus(0);
//        finalAnswerDataRecord.setRelatedId(relatedId);
//        finalAnswerDataRecord.setcreationIme(new Date().getTime());
//        db.finalAnswerDao().insertAll(finalAnswerDataRecord);
//
//    }


    private TextView getTextView() {
        TextView textView = new TextView(getContext());
        int padding = dp2px(getContext(), 10);
        textView.setPadding(padding, padding, padding, 0);
        return textView;
    }

}