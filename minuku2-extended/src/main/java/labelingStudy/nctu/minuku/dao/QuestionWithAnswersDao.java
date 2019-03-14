package labelingStudy.nctu.minuku.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.database.Cursor;

import java.util.List;

import labelingStudy.nctu.minuku.model.DataRecord.QuestionWithAnswersDataRecord;

@Dao
public interface QuestionWithAnswersDao
{
    @Insert
    void insertAllChoicesOfQuestion(List<QuestionWithAnswersDataRecord> choices);

    @Query("UPDATE answer_choices SET  ans_choice_state = :selectState WHERE question_id = :questionId AND ans_choice_pos =:optionId")
    void updateQuestionWithChoice(String selectState, String questionId, String optionId);

    @Query("UPDATE answer_choices SET  detected_time = :answerTime WHERE question_id = :questionId AND ans_choice_pos =:optionId")
    void updateQuestionWithDetectedTime(String answerTime, String questionId, String optionId);



    @Query("SELECT ans_choice_state FROM answer_choices WHERE question_id = :questionId AND ans_choice_pos =:optionId ORDER BY id DESC LIMIT 1")
    String isChecked(String questionId, String optionId);
//    @Query("SELECT ans_choice_state FROM FinalAnswer WHERE question_id = :questionId AND answer_choice_pos =:optionId ORDER BY id DESC LIMIT 1")
//    String isCheckedFA(String questionId, String optionId);
//
//    @Query("SELECT id FROM FinalAnswer WHERE question_id = :questionId AND answer_choice_pos =:optionId ORDER BY id DESC LIMIT 1")
//    Long latestIdFA(String questionId, String optionId);

//    @Query("SELECT detected_time FROM answer_choices WHERE question_id = :questionId AND ans_choice_pos =:optionId")
//    String getLastTimeDetectedTime(String questionId, String optionId);

    @Query("SELECT * FROM answer_choices WHERE ans_choice_state >:selected")
    Cursor getAllQuestionsWithChoices(String selected);

    @Query("SELECT * FROM answer_choices")
    List<QuestionWithAnswersDataRecord> getAll();

    @Query("SELECT * FROM answer_choices")
    Cursor getAllCursor();


    @Query("DELETE FROM answer_choices")
    void deleteAllChoicesOfQuestion();
}
