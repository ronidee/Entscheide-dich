package de.pauni.entscheide_dich;


import android.content.Context;

import java.util.concurrent.ThreadLocalRandom;
import java.util.Arrays;
import java.util.List;

/**
 *
 * Der QuestionManager ist die Schnittstelle zwischen der MainActivity und dem DatabaseHelper
 * Hier werden die Frage-Objekte bereitgestellt und automatisch der nächste Eintrag
 * aus der Datenbank geholt.
 * Außerdem wird der Fragenverlauf (laufzeitlang) protokolliert, damit man in den Fragen
 * auch zurückgehen kann
 *
 */

public class QuestionManager {
    private List<int[]> previousQuestions;
    DatabaseHelper dbh = null;
    private static String youtubeLink;

    public QuestionManager(Context context) {
        dbh = new DatabaseHelper(context);
    }

    Question getQuestion() {
        return dbh.getQuestion(SharedPrefs.getCurrentQuestionId());
    }

    void selectNext() {
        //
        int count = dbh.getQuestionCount();
        int questionId = SharedPrefs.getCurrentQuestionId();

        if (questionId < count) {
            SharedPrefs.saveQuestionId(count + 1);
        } else {
            SharedPrefs.saveQuestionId(1);
        }
    }

    void selectPrevious() {
        int count = dbh.getQuestionCount();
        int questionId = SharedPrefs.getCurrentQuestionId();

        if (count > 1) {
            SharedPrefs.saveQuestionId(count - 1);
        } else {
            SharedPrefs.saveQuestionId(dbh.getQuestionCount());
        }
    }

    void selectRandom() {
        //TODO: select random number
    }

    void setFavorite(boolean favorite) {

    }
}