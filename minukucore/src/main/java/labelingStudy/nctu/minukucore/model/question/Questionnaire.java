/*
 * Copyright (c) 2016.
 *
 * DReflect and Minuku Libraries by Shriti Raj (shritir@umich.edu) and Neeraj Kumar(neerajk@uci.edu) is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License.
 * Based on a work at https://github.com/Shriti-UCI/Minuku-2.
 *
 *
 * You are free to (only if you meet the terms mentioned below) :
 *
 * Share — copy and redistribute the material in any medium or format
 * Adapt — remix, transform, and build upon the material
 *
 * The licensor cannot revoke these freedoms as long as you follow the license terms.
 *
 * Under the following terms:
 *
 * Attribution — You must give appropriate credit, provide a link to the license, and indicate if changes were made. You may do so in any reasonable manner, but not in any way that suggests the licensor endorses you or your use.
 * NonCommercial — You may not use the material for commercial purposes.
 * ShareAlike — If you remix, transform, or build upon the material, you must distribute your contributions under the same license as the original.
 * No additional restrictions — You may not apply legal terms or technological measures that legally restrict others from doing anything the license permits.
 */

package labelingStudy.nctu.minukucore.model.question;

import java.util.List;

/**
 * Created by shriti on 8/2/16.
 */
public class Questionnaire {

    private int ID;

    private List<Question> questionnaire;

    public Questionnaire(int ID, List<Question> questionnaire) {
        this.ID = ID;
        this.questionnaire = questionnaire;
    }

    public int getID() {
        return ID;
    }

    public List<Question> getQuestionnaire() {
        return questionnaire;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public void setQuestionnaire(List<Question> questionnaire) {
        this.questionnaire = questionnaire;
    }
}
