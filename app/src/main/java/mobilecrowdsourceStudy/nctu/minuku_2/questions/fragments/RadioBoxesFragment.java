package mobilecrowdsourceStudy.nctu.minuku_2.questions.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

import static labelingStudy.nctu.minuku.config.Constants.At;
import static labelingStudy.nctu.minuku.config.Constants.others;
import static labelingStudy.nctu.minuku.config.SharedVariables.appNameForQ;
import static labelingStudy.nctu.minuku.config.SharedVariables.getReadableTime;
import static labelingStudy.nctu.minuku.config.SharedVariables.pageRecord;
import static labelingStudy.nctu.minuku.config.SharedVariables.questionaireType;

/**
 * This fragment provide the RadioButton/Single Options.
 */
public class RadioBoxesFragment extends Fragment
{
    private final ArrayList<RadioButton> radioButtonArrayList = new ArrayList<>();
    private boolean screenVisible = false;
    private QuestionsItem radioButtonTypeQuestion;
    private FragmentActivity mContext;
    private Button nextOrFinishButton;
    //private Button previousButton;
    private TextView questionRBTypeTextView;
    private RadioGroup radioGroupForChoices;
    private boolean atLeastOneChecked = false;
    appDatabase db;
    private String questionId = "";
    private int currentPagePosition = 0;
    private int clickedRadioButtonPosition = 0;
    private String qState = "0";
    private String TAG ="Radio";
    int relatedId;
    String appName = "";
    String enterTime ="";
    private EditText editText_answer;
    public RadioBoxesFragment()
    {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_radio_boxes, container, false);

        db = appDatabase.getDatabase(getActivity());

        nextOrFinishButton = rootView.findViewById(R.id.nextOrFinishButton);
        //previousButton = rootView.findViewById(R.id.previousButton);
        questionRBTypeTextView = rootView.findViewById(R.id.questionRBTypeTextView);
        radioGroupForChoices = rootView.findViewById(R.id.radioGroupForChoices);

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
                else if(currentPagePosition == 1){
                    if(questionaireType == 0) {
//                        String ans = db.questionWithAnswersDao().isChecked("1", "1");
                        if(check_radio_answer("1") == 1) {
                            ((QuestionActivity) mContext).nextQuestion(2);
                        }else{
                            ((QuestionActivity) mContext).nextQuestion(1);
                        }
                    }else{
                        ((QuestionActivity) mContext).nextQuestion(1);
                    }
                } else if(currentPagePosition == 2){
                    if(questionaireType == 2 ||questionaireType == 1) {
                        Log.d("qskip"," radioBox current 2, check 18 ");
//                        String ans = db.questionWithAnswersDao().isChecked("1", "1");
                        if(check_radio_answer("17") == 1) {
                            ((QuestionActivity) mContext).nextQuestion(4);
                        }else{
                            ((QuestionActivity) mContext).nextQuestion(1);
                        }
                    }else{
                        ((QuestionActivity) mContext).nextQuestion(1);
                    }
                }else if(currentPagePosition == 3){
                    if(questionaireType == 2 ||questionaireType == 1) {
                        Log.d("qskip"," radioBox current 3, check 19 ");
                        int which = check_radio_answer("18");
                        if (which == 0) {
                            ((QuestionActivity) mContext).nextQuestion(3);
                        } else if (which == 1 || which == 3) {
                            ((QuestionActivity) mContext).nextQuestion(1);
                        } else if (which == 2 || which == 4) {
                            ((QuestionActivity) mContext).nextQuestion(2);
                        } else {
                            ((QuestionActivity) mContext).nextQuestion(3);
                        }
                    }else{
                        ((QuestionActivity) mContext).nextQuestion(1);
                    }
                }
//                else if(currentPagePosition == 4){   //19
//                    if(questionaireType == 2) {
//                        Log.d("qskip"," radioBox current 4, check 19 ");
//                        int which = check_radio_answer("18");
//                        if (which == 1) {
//                            ((QuestionActivity) mContext).nextQuestion(2);
//                        } else {
//                            ((QuestionActivity) mContext).nextQuestion(1);
//                        }
//                    }else{
//                        ((QuestionActivity) mContext).nextQuestion(1);
//                    }
//                }
                else {
                    ((QuestionActivity) mContext).nextQuestion(1);
                }
            }
        });
        //previousButton.setOnClickListener(view -> mContext.onBackPressed());

        return rootView;
    }

    /*This method get called only when the fragment get visible, and here states of Radio Button(s) retained*/
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser)
    {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser)
        {
            screenVisible = true;
            for (int i = 0; i < radioButtonArrayList.size(); i++)
            {
                RadioButton radioButton = radioButtonArrayList.get(i);
                String cbPosition = String.valueOf(i);

                String[] data = new String[]{questionId, cbPosition};
                Observable.just(data)
                        .map(this::getTheStateOfRadioBox)
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
                                    radioButton.setChecked(true);
                                } else if(qState.equals("0"))
                                {
                                    radioButton.setChecked(false);
                                } else{
                                    editText_answer.setText(qState);
                                }
                            }
                        });
            }
        }
    }


    private String getTheStateOfRadioBox(String[] data)
    {
        return db.questionWithAnswersDao().isChecked(data[0], data[1]);
    }

    private void saveActionsOfRadioBox()
    {
        for (int i = 0; i < radioButtonArrayList.size(); i++)
        {
            if (i == clickedRadioButtonPosition)
            {
                RadioButton radioButton = radioButtonArrayList.get(i);
                if (radioButton.isChecked())
                {
                    atLeastOneChecked = true;

                    String cbPosition = String.valueOf(radioButtonArrayList.indexOf(radioButton));

                    String[] data = new String[]{"1", questionId, cbPosition};
                    insertChoiceInDatabase(data);

                } else
                {
                    atLeastOneChecked = true;
                    String cbPosition = String.valueOf(radioButtonArrayList.indexOf(radioButton));

                    String[] data = new String[]{"0", questionId, cbPosition};
                    insertChoiceInDatabase(data);

                }

            }
        }

        if (atLeastOneChecked)
        {
            nextOrFinishButton.setEnabled(true);
        } else
        {
            nextOrFinishButton.setEnabled(false);
        }
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




    private void insertChoiceInDatabase(String[] data)
    {
        Observable.just(data)
                .map(new Function<String[], Object>() {
                    @Override
                    public Object apply(String[] data1) throws Exception {
                        return RadioBoxesFragment.this.insertingInDb(data1);
                    }
                })
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    private String insertingInDb(String[] data)
    {
        String answerTime = getReadableTime(new Date().getTime());
        db.questionWithAnswersDao().updateQuestionWithChoice(data[0], data[1], data[2]);
        Log.d("qskip"," insertingInDb : selectionState : "+data[0] +"; questionId : "+data[1]+"; optionId : "+ data[2]);
        db.questionWithAnswersDao().updateQuestionWithDetectedTime(answerTime ,data[1], data[2]);
       // updateFinalAnswer(data[1],data[2], data[0],answerTime);
        return "";
    }

//    public void updateFinalAnswer(String questionId, String optionId, String selectState, String answerTime){
//        //第幾題、第幾個選項、第幾個解答、回答時間
//        Integer ansId = MapAnswerPositiontoId(Integer.parseInt(questionId),Integer.parseInt(optionId));
//
//
//
//        FinalAnswerDataRecord finalAnswerDataRecord = new FinalAnswerDataRecord();
//        finalAnswerDataRecord.setAnswerChoice(optionId);
//        finalAnswerDataRecord.setanswerId(String.valueOf(ansId));
//        finalAnswerDataRecord.setAnswerChoiceState(selectState);
//        finalAnswerDataRecord.setdetectedTime(answerTime);
//        finalAnswerDataRecord.setQuestionId(questionId);
//        finalAnswerDataRecord.setsyncStatus(0);
//        finalAnswerDataRecord.setRelatedId(relatedId);
//        finalAnswerDataRecord.setcreationIme(new Date().getTime());
//        db.finalAnswerDao().insertAll(finalAnswerDataRecord);
//
//    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        String extraInfo = "";
        mContext = (FragmentActivity) getActivity();
        if (getArguments() != null)
        {
            radioButtonTypeQuestion = getArguments().getParcelable("question");
            questionId = String.valueOf(radioButtonTypeQuestion != null ? radioButtonTypeQuestion.getId() : 0);
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
        String title = radioButtonTypeQuestion != null ? radioButtonTypeQuestion.getQuestionName()+'\n'+apppendText:"";
       //questionRBTypeTextView.setText(radioButtonTypeQuestion.getQuestionName()+'\n'+apppendText);
        setTextWithSpan(title,questionRBTypeTextView);
        List<AnswerOptions> choices = radioButtonTypeQuestion.getAnswerOptions();
        radioButtonArrayList.clear();

        for (AnswerOptions choice : choices)
        {


            if(choice.getName().equals(others)){
                editText_answer = new EditText(mContext);
                editText_answer.setHint(others);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.leftMargin = 25;
                radioGroupForChoices.addView(editText_answer, params);
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
                        insertChoiceInDatabase(data);

                    }
                });
            }else{
                RadioButton rb = new RadioButton(mContext);
                rb.setText(choice.getName());
                rb.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                rb.setTextColor(ContextCompat.getColor(mContext, R.color.grey));
                rb.setPadding(10, 40, 10, 40);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.leftMargin = 25;
                rb.setLayoutParams(params);

                View view = new View(mContext);
                view.setBackgroundColor(ContextCompat.getColor(mContext, R.color.divider));
                view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1));

                radioGroupForChoices.addView(rb);
                radioGroupForChoices.addView(view);
                radioButtonArrayList.add(rb);

                rb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (screenVisible) {
                            clickedRadioButtonPosition = radioButtonArrayList.indexOf(buttonView);
                            RadioBoxesFragment.this.saveActionsOfRadioBox();
                        }
                    }
                });
            }


        }

        if (atLeastOneChecked)
        {
            nextOrFinishButton.setEnabled(true);
        } else
        {
            nextOrFinishButton.setEnabled(false);
        }

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
    private void setTextWithSpan(String questionPosition, TextView tv)
    {
        int slashPosition = questionPosition.indexOf("(");

        Spannable spanText = new SpannableString(questionPosition);
        spanText.setSpan(new RelativeSizeSpan(0.7f), slashPosition, questionPosition.length(), 0);
        tv.setText(spanText);
    }

}