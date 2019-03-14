package mobilecrowdsourceStudy.nctu.minuku_2.questions;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import labelingStudy.nctu.minuku.DBHelper.appDatabase;
import labelingStudy.nctu.minuku.model.DataRecord.QuestionDataRecord;
import labelingStudy.nctu.minuku.model.DataRecord.QuestionWithAnswersDataRecord;
import mobilecrowdsourceStudy.nctu.R;
import mobilecrowdsourceStudy.nctu.minuku_2.questions.adapters.ViewPagerAdapter;
import mobilecrowdsourceStudy.nctu.minuku_2.questions.fragments.CheckBoxesFragment;
import mobilecrowdsourceStudy.nctu.minuku_2.questions.fragments.RadioBoxesFragment;
import mobilecrowdsourceStudy.nctu.minuku_2.questions.fragments.SeekBarsFragment;
import mobilecrowdsourceStudy.nctu.minuku_2.questions.fragments.TextFragment;
import mobilecrowdsourceStudy.nctu.minuku_2.questions.questionmodels.AnswerOptions;
import mobilecrowdsourceStudy.nctu.minuku_2.questions.questionmodels.QuestionDataModel;
import mobilecrowdsourceStudy.nctu.minuku_2.questions.questionmodels.QuestionsItem;

import static labelingStudy.nctu.minuku.config.SharedVariables.appNameForQ;
import static labelingStudy.nctu.minuku.config.SharedVariables.pageRecord;
import static labelingStudy.nctu.minuku.config.SharedVariables.timeForQ;

public class QuestionActivity extends AppCompatActivity
{
    public static String TAG = "QuestionActivity";
    final ArrayList<Fragment> fragmentArrayList = new ArrayList<>();
    List<QuestionsItem> questionsItems = new ArrayList<>();
    appDatabase db;
    //private TextView questionToolbarTitle;
    private TextView questionPositionTV;
    private String totalQuestions = "1";
    private Gson gson;
    private ViewPager questionsViewPager;
    public  String appName = "";

    public  String usertaskType = "";

    public String startAnswerTime;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        SharedPreferences pref = getSharedPreferences("edu.nctu.minuku", MODE_PRIVATE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        toolBarInit();

        db = appDatabase.getDatabase(QuestionActivity.this);
        gson = new Gson();

        if (getIntent().getExtras() != null)
        {

            appName = appNameForQ;
            usertaskType = pref.getString("usertaskTypeForQ","non");

            Bundle bundle = getIntent().getExtras();
            parsingData(bundle,appName);
        }

        Log.d(TAG,"appName : "+appName);
        Log.d(TAG,"usertaskType : "+usertaskType);
//        Log.d(TAG,"relatedId : "+relatedId);
    }

    private void toolBarInit()
    {
        Toolbar questionToolbar = findViewById(R.id.questionToolbar);
        questionToolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        questionToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                QuestionActivity.this.onBackPressed();
            }
        });

        //questionToolbarTitle = questionToolbar.findViewById(R.id.questionToolbarTitle);
        questionPositionTV = questionToolbar.findViewById(R.id.questionPositionTV);

        //questionToolbarTitle.setText("Questions");
    }

    /*This method decides how many Question-Screen(s) will be created and
    what kind of (Multiple/Single choices) each Screen will be.*/
    private void parsingData(Bundle bundle,String appName)
    {
        QuestionDataModel questionDataModel = new QuestionDataModel();

        questionDataModel = gson.fromJson(bundle.getString("json_questions"), QuestionDataModel.class);
        String extraInfo = bundle.getString("extraInfo","");
        Integer relatedIdForQ = bundle.getInt("relatedIdForQ",-1);

        questionsItems = questionDataModel.getData().getQuestions();

        totalQuestions = String.valueOf(questionsItems.size());
        String questionPosition = "1/" + totalQuestions;
        setTextWithSpan(questionPosition);

        preparingQuestionInsertionInDb(questionsItems);
        Log.d(TAG,"parsingData :"+relatedIdForQ)
;        preparingInsertionInDb(questionsItems,relatedIdForQ);

        for (int i = 0; i < questionsItems.size(); i++)
        {


            QuestionsItem question = questionsItems.get(i);


            if (question.getQuestionTypeName().equals("CheckBox"))
            {
                CheckBoxesFragment checkBoxesFragment = new CheckBoxesFragment();
                Bundle checkBoxBundle = new Bundle();
                checkBoxBundle.putParcelable("question", question);
                checkBoxBundle.putInt("page_position", i);
                checkBoxBundle.putString("appNameForF",appName);
                checkBoxBundle.putInt("relatedIdF",relatedIdForQ);
                checkBoxBundle.putString("enterTimeForF",timeForQ);
                checkBoxBundle.putString("extraInfo",extraInfo);
                checkBoxesFragment.setArguments(checkBoxBundle);
                fragmentArrayList.add(checkBoxesFragment);
            }

            if (question.getQuestionTypeName().equals("Radio"))
            {
                RadioBoxesFragment radioBoxesFragment = new RadioBoxesFragment();
                Bundle radioButtonBundle = new Bundle();
                radioButtonBundle.putParcelable("question", question);
                radioButtonBundle.putInt("page_position", i);
                radioButtonBundle.putString("appNameForF",appName);
                radioButtonBundle.putInt("relatedIdF",relatedIdForQ);
                radioButtonBundle.putString("enterTimeForF",timeForQ);
                radioButtonBundle.putString("extraInfo",extraInfo);
                radioBoxesFragment.setArguments(radioButtonBundle);
                fragmentArrayList.add(radioBoxesFragment);
            }
            if (question.getQuestionTypeName().equals("SeekBar"))
            {
                SeekBarsFragment seekBarsFragment = new SeekBarsFragment();
                Bundle seekBarBundle = new Bundle();
                seekBarBundle.putParcelable("question", question);
                seekBarBundle.putInt("page_position", i);
                seekBarBundle.putString("appNameForF",appName);
                seekBarBundle.putInt("relatedIdF",relatedIdForQ);
                seekBarBundle.putString("enterTimeForF",timeForQ);
                seekBarBundle.putString("extraInfo",extraInfo);
                seekBarsFragment.setArguments(seekBarBundle);
                fragmentArrayList.add(seekBarsFragment);
            }
            if (question.getQuestionTypeName().equals("Text"))
            {
                TextFragment textFragment = new TextFragment();
                Bundle seekBarBundle = new Bundle();
                seekBarBundle.putParcelable("question", question);
                seekBarBundle.putInt("page_position", i);
                seekBarBundle.putString("appNameForF",appName);
                seekBarBundle.putInt("relatedIdF",relatedIdForQ);
                seekBarBundle.putString("enterTimeForF",timeForQ);
                seekBarBundle.putString("extraInfo",extraInfo);
                textFragment.setArguments(seekBarBundle);
                fragmentArrayList.add(textFragment);
            }
           //i = skipConditions(i);

        }
        questionsViewPager = findViewById(R.id.pager);
        questionsViewPager.setOffscreenPageLimit(1);
        ViewPagerAdapter mPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), fragmentArrayList);
        questionsViewPager.setAdapter(mPagerAdapter);

    }

    public void nextQuestion(int offset) //default 1
    {
        int item = questionsViewPager.getCurrentItem() + offset;
        questionsViewPager.setCurrentItem(item);

        String currentQuestionPosition = String.valueOf(item + offset);

        String questionPosition = currentQuestionPosition + "/" + totalQuestions;
        setTextWithSpan(questionPosition);
    }
    public void prevQuestion(int offset) //default 1
    {
        int item = questionsViewPager.getCurrentItem() - offset;
        questionsViewPager.setCurrentItem(item);

        String currentQuestionPosition = String.valueOf(item - offset);

        String questionPosition = currentQuestionPosition + "/" + totalQuestions;
        setTextWithSpan(questionPosition);
    }


    public int getTotalQuestionsSize()
    {
        return questionsItems.size();
    }

    private void preparingQuestionInsertionInDb(List<QuestionsItem> questionsItems)
    {
        List<QuestionDataRecord> questionEntities = new ArrayList<>();

        for (int i = 0; i < questionsItems.size(); i++)
        {
            QuestionDataRecord questionDataRecord = new QuestionDataRecord();
            questionDataRecord.setQuestionId(questionsItems.get(i).getId());
            questionDataRecord.setQuestion(questionsItems.get(i).getQuestionName());

            questionEntities.add(questionDataRecord);
        }
        insertQuestionInDatabase(questionEntities);
    }

    private void insertQuestionInDatabase(List<QuestionDataRecord> questionEntities)
    {
        Observable.just(questionEntities)
                .map(this::insertingQuestionInDb)
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    /*First, clear the table, if any previous data saved in it. Otherwise, we get repeated data.*/
    private String insertingQuestionInDb(List<QuestionDataRecord> questionEntities)
    {
        db.questionDao().deleteAllQuestions();
        db.questionDao().insertAllQuestions(questionEntities);
        return "";
    }

    private void preparingInsertionInDb(List<QuestionsItem> questionsItems,int relatedId)
    {
        ArrayList<QuestionWithAnswersDataRecord> questionWithChoicesEntities = new ArrayList<>();

        for (int i = 0; i < questionsItems.size(); i++)
        {
            List<AnswerOptions> answerOptions = questionsItems.get(i).getAnswerOptions();

            for (int j = 0; j < answerOptions.size(); j++)
            {
                QuestionWithAnswersDataRecord questionWithAnswersDataRecord = new QuestionWithAnswersDataRecord();
                questionWithAnswersDataRecord.setQuestionId(String.valueOf(questionsItems.get(i).getId()));
                questionWithAnswersDataRecord.setAnswerChoice(answerOptions.get(j).getName());
                questionWithAnswersDataRecord.setAnswerChoicePosition(String.valueOf(j));
                questionWithAnswersDataRecord.setAnswerChoiceId(answerOptions.get(j).getAnswerId());
                questionWithAnswersDataRecord.setAnswerChoiceState("0");
                questionWithAnswersDataRecord.setcreationIme(new Date().getTime());
                questionWithAnswersDataRecord.setdetectedTime("0");
                questionWithAnswersDataRecord.setRelatedId(relatedId);

                questionWithChoicesEntities.add(questionWithAnswersDataRecord);
            }
        }

        insertQuestionWithChoicesInDatabase(questionWithChoicesEntities);
    }

    private void insertQuestionWithChoicesInDatabase(List<QuestionWithAnswersDataRecord> questionWithChoicesEntities)
    {
        Observable.just(questionWithChoicesEntities)
                .map(this::insertingQuestionWithChoicesInDb)
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    /*First, clear the table, if any previous data saved in it. Otherwise, we get repeated data.*/
    private String insertingQuestionWithChoicesInDb(List<QuestionWithAnswersDataRecord> questionWithChoicesEntities)
    {
        db.questionWithAnswersDao().deleteAllChoicesOfQuestion();
        db.questionWithAnswersDao().insertAllChoicesOfQuestion(questionWithChoicesEntities);
        return "";
    }


    @Override
    public void onBackPressed()
    {
        if (questionsViewPager.getCurrentItem() == 0)
        {
            super.onBackPressed();
            pageRecord.clear();
        } else
        {

            int item = questionsViewPager.getCurrentItem() - 1;
            int  itemCheck = -1 ;
            if(pageRecord.contains(item+1)) {
                questionsViewPager.setCurrentItem(item);
                itemCheck = item;
            }else{
                if(pageRecord.size()!=0) {
                    questionsViewPager.setCurrentItem(pageRecord.get(pageRecord.size() - 1)-1);
                    itemCheck = pageRecord.get(pageRecord.size() - 1);
                }else{
                    super.onBackPressed();
                }

            }
            String currentQuestionPosition = String.valueOf(itemCheck + 1);
            String questionPosition = currentQuestionPosition + "/" + totalQuestions;
            setTextWithSpan(questionPosition);
            for(int tmp : pageRecord){
                Log.d("qskip","pageRecord" + tmp);
            }
            Log.d("qskip","pageRecord size" + pageRecord.size());
            if(pageRecord.size()!=0) {
                pageRecord.remove(pageRecord.size() - 1);
            }
        }
    }

    private void setTextWithSpan(String questionPosition)
    {
        int slashPosition = questionPosition.indexOf("/");

        Spannable spanText = new SpannableString(questionPosition);
        spanText.setSpan(new RelativeSizeSpan(0.7f), slashPosition, questionPosition.length(), 0);
        questionPositionTV.setText(spanText);
    }




}