package mobilecrowdsourceStudy.nctu.minuku_2.questions.fragments;

/**
 * Created by chiaenchiang on 03/01/2019.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Date;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import labelingStudy.nctu.minuku.DBHelper.appDatabase;
import mobilecrowdsourceStudy.nctu.R;
import mobilecrowdsourceStudy.nctu.minuku_2.questions.QuestionActivity;
import mobilecrowdsourceStudy.nctu.minuku_2.questions.questionmodels.QuestionsItem;

import static labelingStudy.nctu.minuku.config.Constants.At;
import static labelingStudy.nctu.minuku.config.Constants.Enter;
import static labelingStudy.nctu.minuku.config.Constants.Post;
import static labelingStudy.nctu.minuku.config.SharedVariables.appNameForQ;
import static labelingStudy.nctu.minuku.config.SharedVariables.getReadableTime;


public class TextFragment extends Fragment {

    private FragmentActivity mContext;
    private Button nextOrFinishButton;
    private TextView textview_q_title;
    private EditText editText_answer;
    private String qState = "0";
    private appDatabase db;
    String appName="";
    String enterTime = "";
    int relatedId;
    private String questionId = "";
    private int currentPagePosition = 0;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_text, container, false);

        db = appDatabase.getDatabase(getActivity());

        nextOrFinishButton = rootView.findViewById(R.id.nextOrFinishButton);
        //previousButton = rootView.findViewById(R.id.previousButton);
        textview_q_title = (TextView) rootView.findViewById(R.id.textview_q_title);
        editText_answer = (EditText) rootView.findViewById(R.id.editText_answer);

        nextOrFinishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPagePosition == ((QuestionActivity) mContext).getTotalQuestionsSize()) {
                /* Here, You go back from where you started OR If you want to go next Activity just change the Intent*/
                    Intent returnIntent = new Intent();
                    mContext.setResult(Activity.RESULT_OK, returnIntent);
                    mContext.finish();

                } else {

                    ((QuestionActivity) mContext).nextQuestion(1);
                }
            }
        });
        //previousButton.setOnClickListener(view -> mContext.onBackPressed());

        return rootView;
    }
    private void insertAnswerInDatabase(String[] data)
    {
        Observable.just(data)
                .map(new Function<String[], Object>() {
                    @Override
                    public Object apply(String[] data1) throws Exception {
                        return TextFragment.this.insertingInDb(data1);
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
       // insertFinalAnswer(data[1],data[2], data[0],answerTime);
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


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

//        TextView textView = getTextView();
//        textView.setText(choice.getName());
//        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
//        textView.setTextColor(ContextCompat.getColor(mContext, R.color.grey));
//        textView.setPadding(10, 0, 10, 0);
        mContext = (FragmentActivity) getActivity();
        QuestionsItem textTypeQuestion = null;
        String extraInfo = "";
        if (getArguments() != null)
        {
            textTypeQuestion = getArguments().getParcelable("question");
            appName = appNameForQ;
            relatedId = getArguments().getInt("relatedIdF");
            enterTime = getArguments().getString("enterTimeForF");
            questionId = String.valueOf(textTypeQuestion != null ? textTypeQuestion.getId() : 0);
            currentPagePosition = getArguments().getInt("page_position") + 1;
            extraInfo = getArguments().getString("extraInfo");
        }
//        String ans = db.questionWithAnswersDao().isChecked("1","0");
        String apppendText = "";
        if(!enterTime.contains(Post)) {
            if(extraInfo!=null){
                apppendText = " ( " + Enter + " " + appName + " " + At + enterTime +" "+extraInfo +" ) ";
            }
            else
                apppendText = " ( " + Enter + " " + appName + " " + At + enterTime + " ) ";
        }
        else
            apppendText = " ( " +" "+ appName +" "+ enterTime+" ) ";
        textview_q_title.setText(textTypeQuestion != null ? textTypeQuestion.getQuestionName()+'\n'+apppendText:"");
        /*Disable the button until any choice got selected*/
        nextOrFinishButton.setEnabled(false);


        /* If the current question is last in the questionnaire then
        the "Next" button will change into "Finish" button*/
        if (currentPagePosition == ((QuestionActivity) mContext).getTotalQuestionsSize())
        {
            nextOrFinishButton.setText(R.string.finish);
        } else
        {
            nextOrFinishButton.setText(R.string.next);
        }

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

       // editText_answer.requestFocus();
//        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Service.INPUT_METHOD_SERVICE);
//        imm.showSoftInput(editText_answer, 0);


    }


    // 返回時也會儲存當時的資訊
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser)
    {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser)
        {


            String cbPosition = String.valueOf(0);
            String[] data = new String[]{questionId, cbPosition};
            Observable.just(data)
                    .map(this::getTheStateOfSeekBar)
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
//                            seekBar.setProgress(Float.parseFloat(qState));
                            editText_answer.setText(qState);
                            if (!nextOrFinishButton.isEnabled())
                            {
                                nextOrFinishButton.setEnabled(true);
                            }
                        }
                    });

        }
    }
    private String getTheStateOfSeekBar(String[] data)
    {
        return db.questionWithAnswersDao().isChecked(data[0], data[1]);
    }
//    private TextView getTextView() {
//        TextView textView = new TextView(getContext());
//        int padding = dp2px(getContext(), 10);
//        textView.setPadding(padding, padding, padding, 0);
//        return textView;
//    }
}
