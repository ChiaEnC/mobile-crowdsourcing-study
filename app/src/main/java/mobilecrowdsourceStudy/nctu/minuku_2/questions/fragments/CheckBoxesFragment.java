package mobilecrowdsourceStudy.nctu.minuku_2.questions.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import static labelingStudy.nctu.minuku.config.Constants.others;
import static labelingStudy.nctu.minuku.config.SharedVariables.appNameForQ;
import static labelingStudy.nctu.minuku.config.SharedVariables.crowdsource;
import static labelingStudy.nctu.minuku.config.SharedVariables.getReadableTime;
import static labelingStudy.nctu.minuku.config.SharedVariables.map;
import static labelingStudy.nctu.minuku.config.SharedVariables.pageRecord;
import static labelingStudy.nctu.minuku.config.SharedVariables.questionaireType;

/**
 * This fragment provide the Checkbox/Multiple related Options/Choices.
 */
public class CheckBoxesFragment extends Fragment
{
    private final ArrayList<CheckBox> checkBoxArrayList = new ArrayList<>();
    private int atLeastOneChecked = 0;
    private FragmentActivity mContext;
    private Button nextOrFinishButton;
    //private Button previousButton;
    private TextView questionCBTypeTextView;
    private LinearLayout checkboxesLinearLayout;
    private TextView questionCBGroupTextView;
    private appDatabase db;
    private String questionId = "";
    private int currentPagePosition = 0;
    private int clickedCheckBoxPosition = 0;
    private String qState = "0";
    Boolean isMobileCrowdsource ;
    String appName="";
    String enterTime = "";
    int relatedId;
    private EditText editText_answer;
    public CheckBoxesFragment()
    {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_check_boxes, container, false);

        db = appDatabase.getDatabase(getActivity());

        nextOrFinishButton = rootView.findViewById(R.id.nextOrFinishButton);
        //previousButton = rootView.findViewById(R.id.previousButton);
        questionCBTypeTextView = rootView.findViewById(R.id.questionCBTypeTextView);

        checkboxesLinearLayout = rootView.findViewById(R.id.checkboxesLinearLayout);

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
                else if(currentPagePosition == ((QuestionActivity) mContext).getTotalQuestionsSize()-1){
                    if(questionaireType ==0) {
                        if (check_radio_answer("1") == 1) {  // 第一題沒勾
                            Intent returnIntent = new Intent();
                            returnIntent.putExtra("isMobileCrowdsource", isMobileCrowdsource);
                            mContext.setResult(Activity.RESULT_OK, returnIntent);
                            mContext.finish();
                        } else {
                            ((QuestionActivity) mContext).nextQuestion(1);
                        }
                    }else{
                        ((QuestionActivity) mContext).nextQuestion(1);
                    }
                }else if(currentPagePosition == 4){   //19題時
                    if(questionaireType == 2 ||questionaireType == 1) {
                        Log.d("qskip"," radioBox current 4, check 19 ");
                        int which = check_radio_answer("18");
                        if (which == 1) {
                            ((QuestionActivity) mContext).nextQuestion(2);
                        } else {
                            ((QuestionActivity) mContext).nextQuestion(1);
                        }
                    }else{
                        ((QuestionActivity) mContext).nextQuestion(1);
                    }
                }

                else {
                    ((QuestionActivity) mContext).nextQuestion(1);
                }
            }
        });
        //previousButton.setOnClickListener(view -> mContext.onBackPressed());

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
//        List<Long> array =new ArrayList<>();
//        array.add(first_Id);
//        array.add(second_Id);
//        array.add(third_Id);
//        array.add(fourth_Id);
//        array.add(other_id);
//        Log.d("qskip"," radioBox first id:  "+first_Id);
//        Log.d("qskip"," radioBox second id:  "+second_Id);
//        Log.d("qskip"," radioBox third id:  "+third_Id);
//        Log.d("qskip"," radioBox fourth id:  "+fourth_Id);



    }
//    public int getLargestIndex(List<Long> arrayList){
//        if ( arrayList == null || arrayList.size() == 0 ) return -1; // null or empty
//
//        int largest = 0;
//        for ( int i = 1; i < arrayList.size(); i++ )
//        {
//            if ( arrayList.get(i) > arrayList.get(largest) ) largest = i;
//        }
//        Log.d("qskip"," radioBox largest :  "+largest);
//        return largest; // position of the first largest found
//    }

    /*This method get called only when the fragment get visible, and here states of checkbox(s) retained*/
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser)
    {
        super.setUserVisibleHint(isVisibleToUser);

        atLeastOneChecked = 0;

        if (isVisibleToUser)
        {
            for (int i = 0; i < checkBoxArrayList.size(); i++)
            {
                CheckBox checkBox = checkBoxArrayList.get(i);
                String cbPosition = String.valueOf(i);

                String[] data = new String[]{questionId, cbPosition};
                Observable.just(data)
                        .map(this::getTheStateOfCheckBox)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<String>()
                        {
                            @Override
                            public void onSubscribe(Disposable d)
                            {

                            }

                            @Override
                            public void onNext(String s)
                            {
                                qState = s;
                            }

                            @Override
                            public void onError(Throwable e)
                            {

                            }

                            @Override
                            public void onComplete()
                            {
                                if (qState.equals("1"))
                                {
                                    checkBox.setChecked(true);
                                    atLeastOneChecked = atLeastOneChecked + 1;

                                    if (!nextOrFinishButton.isEnabled())
                                    {
                                        nextOrFinishButton.setEnabled(true);
                                    }
                                } else if(qState.equals("0"))
                                {
                                    checkBox.setChecked(false);
                                }else{
                                    editText_answer.setText(qState);
                                    atLeastOneChecked = atLeastOneChecked + 1;
                                }
                            }
                        });
            }
        }
    }


    private String getTheStateOfCheckBox(String[] data)
    {
        return db.questionWithAnswersDao().isChecked(data[0], data[1]);
    }

    private void saveActionsOfCheckBox()
    {
        for (int i = 0; i < checkBoxArrayList.size(); i++)
        {
            if (i == clickedCheckBoxPosition)
            {
                CheckBox checkBox = checkBoxArrayList.get(i);
                if (checkBox.isChecked())
                {
                    atLeastOneChecked = atLeastOneChecked + 1;

                    String cbPosition = String.valueOf(checkBoxArrayList.indexOf(checkBox));

                    String[] data = new String[]{"1", questionId, cbPosition};
                    insertAnswerInDatabase(data);

                } else
                {
                    atLeastOneChecked = atLeastOneChecked - 1;
                    if (atLeastOneChecked <= 0)
                        atLeastOneChecked = 0;

                    String cbPosition = String.valueOf(checkBoxArrayList.indexOf(checkBox));

                    String[] data = new String[]{"0", questionId, cbPosition};
                    insertAnswerInDatabase(data);
                }
            }

        }

        if (atLeastOneChecked != 0)
        {
            nextOrFinishButton.setEnabled(true);
        } else
        {
            nextOrFinishButton.setEnabled(false);
        }
    }

    private void insertAnswerInDatabase(String[] data)
    {
        Observable.just(data)
                .map(new Function<String[], Object>() {
                    @Override
                    public Object apply(String[] data1) throws Exception {
                        return CheckBoxesFragment.this.insertingInDb(data1);
                    }
                })
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    private String insertingInDb(String[] data)
    {
        String answerTime = getReadableTime(new Date().getTime());
        db.questionWithAnswersDao().updateQuestionWithChoice(data[0], data[1], data[2]);
        //String selectState, String questionId, String optionId
        db.questionWithAnswersDao().updateQuestionWithDetectedTime(answerTime ,data[1], data[2]);
        //answerTime
       // insertFinalAnswer(data[1],data[2], data[0],answerTime);

        return "";
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);


        mContext = (FragmentActivity) getActivity();
        QuestionsItem checkBoxTypeQuestion = null;
        String extraInfo = "";
        if (getArguments() != null)
        {
            checkBoxTypeQuestion = getArguments().getParcelable("question");
            appName = appNameForQ;
            relatedId = getArguments().getInt("relatedIdF");
            enterTime = getArguments().getString("enterTimeForF");
            extraInfo = getArguments().getString("extraInfo");
            questionId = String.valueOf(checkBoxTypeQuestion != null ? checkBoxTypeQuestion.getId() : 0);
            currentPagePosition = getArguments().getInt("page_position") + 1;
        }
        String apppendText = "";
        if(questionaireType!=0){
            apppendText = " ( "  + appName + " " + At + enterTime +" ( "+extraInfo +" ) "+" ) ";
        }
        else
            apppendText = " ( " + appName + " " + At + enterTime + " ) ";

        String title = checkBoxTypeQuestion != null ? checkBoxTypeQuestion.getQuestionName()+'\n'+apppendText:"";
        setTextWithSpan(title,questionCBTypeTextView);
        //questionCBTypeTextView.setText(checkBoxTypeQuestion != null ? checkBoxTypeQuestion.getQuestionName()+'\n'+apppendText:"");
        /*Disable the button until any choice got selected*/
        nextOrFinishButton.setEnabled(false);

        List<AnswerOptions> checkBoxChoices = Objects.requireNonNull(checkBoxTypeQuestion).getAnswerOptions();
        checkBoxArrayList.clear();
        if(checkBoxTypeQuestion != null) {
            TextView textView = getTextView();
            textView.setText(checkBoxTypeQuestion.getAnswerOptionsGroup());
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            textView.setTextColor(ContextCompat.getColor(mContext, R.color.grey));
            textView.setPadding(10, 0, 10, 0);
            checkboxesLinearLayout.addView(textView);
        }
        for (AnswerOptions choice : checkBoxChoices)
        {

            Boolean notShowAnswer = false;

            if(appName.equals(map)){

                if(choice.getAnswerId().equals("62")||choice.getAnswerId().equals("63")){
                    notShowAnswer = true;
                }
            }else if(appName.equals(crowdsource)){
                if(choice.getAnswerId().equals("56")||choice.getAnswerId().equals("57")||choice.getAnswerId().equals("58")||
                        choice.getAnswerId().equals("59")||choice.getAnswerId().equals("60") ||choice.getAnswerId().equals("61")){
                    notShowAnswer = true;
                }
            }
            if(choice.getName().equals(others)){
                editText_answer = new EditText(mContext);
                editText_answer.setHint(others);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.leftMargin = 25;
                checkboxesLinearLayout.addView(editText_answer, params);
                editText_answer.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (s.length() >= 1 && !s.toString().equals("0")) {
                            nextOrFinishButton.setEnabled(true);
                        } else {
                            nextOrFinishButton.setEnabled(false);
                        }
                        String[] data = new String[]{editText_answer.getText().toString(), questionId, String.valueOf(0)};
                        insertAnswerInDatabase(data);

                    }
                });
            }else{
                if(!notShowAnswer){
                    CheckBox checkBox = new CheckBox(mContext);
                    checkBox.setText(choice.getName());
                    checkBox.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                    checkBox.setTextColor(ContextCompat.getColor(mContext, R.color.grey));
                    checkBox.setPadding(0, 20, 0, 20);  //10 40 10 40
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.leftMargin = 25;

                    View view = new View(mContext);
                    view.setBackgroundColor(ContextCompat.getColor(mContext, R.color.divider));
                    view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1));
                    checkboxesLinearLayout.addView(checkBox, params);
                    checkboxesLinearLayout.addView(view);
                    checkBoxArrayList.add(checkBox);

                    checkBox.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view1) {
                            CheckBox buttonView = (CheckBox) view1;
                            clickedCheckBoxPosition = checkBoxArrayList.indexOf(buttonView);
                            CheckBoxesFragment.this.saveActionsOfCheckBox();
                        }
                    });
                }
            }


            /*As user comes back for any modification in choices, "setUserVisibleHint" fragment lifecycle method get called, and "checkBox.setChecked(true)"
             * statement will be executed as many times as previously user checked.
             * On that, this below block will get executed automatically,
             * where this method(saveActionsOfCheckBox()) also executed which is unnecessary.
             * That's why we follow "setOnClickListener" instead of "setOnCheckedChangeListener".*/

            /*checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
            {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
                {
                    clickedCheckBoxPosition = checkBoxArrayList.indexOf(buttonView);
                    saveActionsOfCheckBox();
                }
            });*/
        }
        /** edit text **/




        /* If the current question is last in the questionnaire then
        the "Next" button will change into "Finish" button*/
        if (currentPagePosition == ((QuestionActivity) mContext).getTotalQuestionsSize())
        {
            nextOrFinishButton.setText(R.string.finish);
        } else
        {
            nextOrFinishButton.setText(R.string.next);
        }
    }
    private TextView getTextView() {
        TextView textView = new TextView(getContext());
        int padding = dp2px(getContext(), 10);
        textView.setPadding(padding, padding, padding, 0);
        return textView;
    }
    private void setTextWithSpan(String questionPosition, TextView tv)
    {
        int slashPosition = questionPosition.indexOf("(");

        Spannable spanText = new SpannableString(questionPosition);
        spanText.setSpan(new RelativeSizeSpan(0.7f), slashPosition, questionPosition.length(), 0);
        tv.setText(spanText);
    }

//    public void insertFinalAnswer(String questionId,String optionId,String selectState,String answerTime){
//            //第幾題、第幾個選項、第幾個解答、回答時間
//        Integer ansId = MapAnswerPositiontoId(Integer.parseInt(questionId),Integer.parseInt(optionId));
////        final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
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
//    public void updateFinalAnswer(){
//
//        Cursor transCursor = db.questionWithAnswersDao().getAllCursor();
//        List<FinalAnswerDataRecord> finalAnswerDataRecords = new ArrayList<FinalAnswerDataRecord>();
//        SharedPreferences pref =getActivity().getSharedPreferences("edu.nctu.minuku", MODE_PRIVATE);
//        String startAnswerTime = pref.getString("startAnswerTime","");
//        String finishAnswerTime = pref.getString("finishAnswerTime","");
//
//        int relatedId = pref.getInt("relatedIdForQM",-1);
//        int rows = transCursor.getCount();
//        if(rows!=0) {
//            FinalAnswerDataRecord finalAnswerDataRecord = new FinalAnswerDataRecord();
//
//            transCursor.moveToFirst();
//            for (int i = 0; i < rows; i++) {
//                String detectedTime = transCursor.getString(2);
//                String questionId = transCursor.getString(3);
//                String answerChoice = transCursor.getString(4);
//                String answerChoiceState = transCursor.getString(7);
//                if(answerChoice!="0") {
//                    finalAnswerDataRecord.setAnswerChoice(answerChoice);
//                    finalAnswerDataRecord.setAnswerChoiceState(answerChoiceState);
//                    finalAnswerDataRecord.setdetectedTime(detectedTime);
//                    finalAnswerDataRecord.setQuestionId(questionId);
//                    finalAnswerDataRecord.setStartAnswerTime(startAnswerTime);
//                    finalAnswerDataRecord.setfinishAnswerTime(finishAnswerTime);
//                    finalAnswerDataRecord.setRelatedId(relatedId);
//                    finalAnswerDataRecord.setcreationIme(new Date().getTime());
//                }
//                transCursor.moveToNext();
//            }
//        }
//        db.finalAnswerDao().insertAll(finalAnswerDataRecords);
//    }


}