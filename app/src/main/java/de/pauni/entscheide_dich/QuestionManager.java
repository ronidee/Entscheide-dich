package de.pauni.entscheide_dich;


import android.content.Context;
import java.util.concurrent.ThreadLocalRandom;


/**
 *
 * Der QuestionManager ist die Schnittstelle zwischen der MainActivity und dem DatabaseHelper
 * Hier werden die Frage-Objekte bereitgestellt und automatisch der nächste Eintrag
 * aus der Datenbank geholt.
 * Außerdem wird der Fragenverlauf (laufzeitlang) protokolliert, damit man in den Fragen
 * auch zurückgehen kann
 *
 */

class QuestionManager {
    private DatabaseHelper dbh = null;

    public QuestionManager(Context context) {
        //if (SharedPrefs.isFirstStart())
            new DatabaseInitializer(context); // creating the database and table

        dbh = new DatabaseHelper(context);
    }

    Question getQuestion() {
        return dbh.getQuestion(SharedPrefs.getCurrentQuestionId());
    }

    void selectNext() {
        //
        int count = 104; //dbh.getQuestionCount();
        int questionId = SharedPrefs.getCurrentQuestionId();

        if (questionId < count) {
            SharedPrefs.saveQuestionId(questionId + 1);
        } else {
            SharedPrefs.saveQuestionId(1);
        }
    }

    void selectPrevious() {
        int questionId = SharedPrefs.getCurrentQuestionId();

        if (questionId > 1) {
            SharedPrefs.saveQuestionId(questionId - 1);
        } else {
            SharedPrefs.saveQuestionId(dbh.getQuestionCount());
        }
    }

    void selectRandom() {
        ThreadLocalRandom.current().nextInt(1, dbh.getQuestionCount() + 1);
    }

    void setFavorite(boolean favorite) {

    }
}