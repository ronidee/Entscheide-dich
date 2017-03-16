package de.pauni.entscheide_dich;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.Random;
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
    private DatabaseHelper dbh;
    private int id;
    boolean favoritesOnly = true;
    private Cursor dbCursor;


    public QuestionManager(Context context) {
        //if (SharedPrefs.isFirstStart())
        new DatabaseInitializer(context); // creating the database and table
        dbh = new DatabaseHelper(context);
        dbCursor = dbh.getCursor();
    }


    Question getQuestion() {
        return cursorToQuestion(dbCursor);
    }


    // Cursor movements:

    void selectNext() {
        dbCursor.moveToNext();


        if (dbCursor.isAfterLast()) {
            dbCursor.moveToFirst();
        }

        save_state();

        Log.d("Q_MNGR>>", String.valueOf(dbh.countAllQuestions()));
        Log.d("Q_MNGR>>", String.valueOf(dbh.countFavoredQuestions()));
    }


    void selectNextFavorite() {
        while (cursorToQuestion(dbCursor).favorite) {
            dbCursor.moveToNext();
        }
    }






    void selectPrevious() {
        dbCursor.moveToNext();

        if (dbCursor.isBeforeFirst()){
            dbCursor.moveToFirst();
        }
        save_state();
    }

    void selectRandom() {
        int rand = new Random().nextInt(dbh.getQuestionCount());

        // move cursor random times to next position
        for (int i = 0; i < rand; i++) {
            selectNext();
        }

        save_state();
    }

    void selectNextFavorite() {

    }

    void setFavorite(boolean favorite) {
        Log.d("QManager>>>:","Favorit = " + (favorite?"ja":"nein"));
        dbh.setFavorite(SharedPrefs.getCurrentQuestionId(), favorite);
    }



    private void save_state() {
        // Maybe do more stuff here
        SharedPrefs.saveQuestionId(id);
    }



    int countAllQuestions() {
        return dbh.countAllQuestions();
    }


    int countFavoredQuestions() {
        return dbh.countFavoredQuestions()

    }





    private Question cursorToQuestion(Cursor c) {
        if(c != null) {
            // Todo: Do something here
        }

        // reading the comma seperated lists (potentially single string or empty)
        String keywords_raw = c.getString(c.getColumnIndex(DatabaseHelper.KEY_KEYWORDS));
        String links_raw    = c.getString(c.getColumnIndex(DatabaseHelper.KEY_LINKS));


        String[] keywords;
        String[] links;

        if (keywords_raw.contains(",")) {
            keywords = keywords_raw.split(",");
            links    = links_raw.split(",");
        } else {
            keywords = new String[] {keywords_raw};
            links    = new String[] {links_raw};
        }

        Question question   = new Question();
        question.question   = c.getString(1);
        question.guest      = c.getString(2);
        question.ytlink     = c.getString(3);
        question.favorite   = c.getInt(4) == 1;
        question.clickables = new String[][] {keywords, links};

        return question;
    }


}