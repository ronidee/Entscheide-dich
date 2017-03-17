package de.pauni.entscheide_dich;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.Collections;
import java.util.List;
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
    boolean mode_random;
    private Cursor dbCursor;


    public QuestionManager(Context context) {
        if (SharedPrefs.isFirstStart())
            new DatabaseInitializer(context); // creating the database and table
        dbh = new DatabaseHelper(context);
        dbCursor = dbh.getCursor();
        dbCursor.moveToFirst();
        mode_random = false;
    }


    Question getQuestion() {
        return cursorToQuestion(dbCursor);
    }


    // Cursor movements:

    void selectNext() {
        if (mode_random)
            randomize();

        moveToNext_save();
    }



    void selectNextFavorite() {
        if (mode_random)
            randomize();

        do {
            moveToNext_save();
        } while (!cursorToQuestion(dbCursor).favorite);
    }





    void setId(int new_id) {
        while (cursorToQuestion(dbCursor).id != new_id) {
            moveToNext_save();
        }
    }


    int getId() {
        return cursorToQuestion(dbCursor).id;
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
        dbh.setFavorite(cursorToQuestion(dbCursor).id, favorite);
    }


    int countAllQuestions() {
        return dbh.countAllQuestions();
    }


    int countFavoredQuestions() {
        return dbh.countFavoredQuestions();

    }



    private void moveToNext_save() {
        dbCursor.moveToNext();

        if (dbCursor.isAfterLast()) {
            dbCursor.moveToFirst();
        }
    }

    private void moveToPrevious_save() {
        dbCursor.moveToPrevious();

        if (dbCursor.isBeforeFirst()) {
            dbCursor.moveToLast();
        }
    }


    Question[] searchQuestion(String searchString) {
        Log.d("searchQuestion>>>", "searching for \'" + searchString + "\'");

        Cursor foundCursor = dbh.searchQuestion(searchString);
        foundCursor.moveToFirst();

        List<Question> foundQuestion = Collections.emptyList();

        while (!foundCursor.isAfterLast()) {
            Question i =cursorToQuestion(foundCursor);
            foundQuestion.add(i);
            foundCursor.moveToNext();

            Log.d("searchQuestion>>>", i.question);
        }

        return foundQuestion.toArray(new Question[foundQuestion.size()]);
    }


    private Question cursorToQuestion(Cursor cursor) {
        // reading the comma seperated lists (potentially single string or empty)

        Log.d("foo", String.valueOf(cursor.getColumnIndex(DatabaseHelper.KEY_KEYWORDS)));

        String keywords_raw = cursor.getString(cursor.getColumnIndex(DatabaseHelper.KEY_KEYWORDS));
        String links_raw    = cursor.getString(cursor.getColumnIndex(DatabaseHelper.KEY_LINKS));


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
        question.id         = cursor.getInt(0);
        question.question   = cursor.getString(1);
        question.guest      = cursor.getString(2);
        question.ytlink     = cursor.getString(3);
        question.favorite   = cursor.getInt(4) == 1;
        question.clickables = new String[][] {keywords, links};

        return question;
    }


}