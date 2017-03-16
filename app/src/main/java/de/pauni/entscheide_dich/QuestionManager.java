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
    boolean mode_random;
    private Cursor dbCursor;


    public QuestionManager(Context context) {
        //if (SharedPrefs.isFirstStart())
        new DatabaseInitializer(context); // creating the database and table
        dbh = new DatabaseHelper(context);
        dbCursor = dbh.getCursor();
        mode_random = false;
    }


    Question getQuestion() {
        return cursorToQuestion();
    }


    // Cursor movements:

    void selectNext() {
        if (mode_random)
            randomize();

        dbCursor.moveToNext();

        if (dbCursor.isAfterLast()) {
            dbCursor.moveToFirst();
        }
    }


    void selectPrevious() {
        if (mode_random)
            randomize();

        dbCursor.moveToNext();

        if (dbCursor.isBeforeFirst()) {
            dbCursor.moveToLast();
        }
    }



    void selectNextFavorite() {
        if (mode_random)
            randomize();

        do {
            dbCursor.moveToNext();
        } while (!cursorToQuestion().favorite);
    }


    void selectPreviousFavorite() {
        if (mode_random)
            randomize();

        do {
            dbCursor.moveToNext();
        } while (cursorToQuestion().favorite);
    }








    void setId(int new_id) {
        id = new_id;

        while (cursorToQuestion().id == new_id) {
            dbCursor.moveToNext();
        }
    }


    int getId() {
        return id;
    }



    void setRandom(boolean random) {
        mode_random = random;
    }

    boolean isRandom() {
        return mode_random;
    }




    private void randomize() {
        int rand = new Random().nextInt(dbh.getQuestionCount());

        // move cursor random times to next position
        for (int i = 0; i < rand; i++) {
            // dbCursor.moveToNext();

            if (dbCursor.moveToNext() && dbCursor.isAfterLast()) {
                dbCursor.moveToFirst();
            }
        }
    }

    void setFavorite(boolean favorite) {
        dbh.setFavorite(cursorToQuestion().id, favorite);
    }


    int countAllQuestions() {
        return dbh.countAllQuestions();
    }


    int countFavoredQuestions() {
        return dbh.countFavoredQuestions();

    }





    private Question cursorToQuestion() {
        // reading the comma seperated lists (potentially single string or empty)
        String keywords_raw = dbCursor.getString(dbCursor.getColumnIndex(DatabaseHelper.KEY_KEYWORDS));
        String links_raw    = dbCursor.getString(dbCursor.getColumnIndex(DatabaseHelper.KEY_LINKS));


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
        question.id         = dbCursor.getInt(0);
        question.question   = dbCursor.getString(1);
        question.guest      = dbCursor.getString(2);
        question.ytlink     = dbCursor.getString(3);
        question.favorite   = dbCursor.getInt(4) == 1;
        question.clickables = new String[][] {keywords, links};

        return question;
    }


}