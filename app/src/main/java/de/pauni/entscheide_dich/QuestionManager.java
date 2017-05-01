package de.pauni.entscheide_dich;


import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


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
    private static DatabaseHelper dbh;
    private static boolean mode_random;
    private static Cursor dbCursor;
    private static RandomList randomList;

    // constructor
    QuestionManager(Context context) {
        dbh      = new DatabaseHelper(context);
        dbCursor = dbh.getCursor();
        dbCursor.moveToFirst();
        mode_random = false;
        randomList = new RandomList(dbh.getQuestionCount());

        DownloadManager downloadManager = new DownloadManager(context);
        downloadManager.updateDatabase();

    }

    /**
     *   Getter methods
     */

    // returns the current question
    static Question getQuestion() {
        return getSelectedQuestion(dbCursor);
    }

    // returns ID of the current question
    static int getId() {
        return getSelectedQuestion(dbCursor).id;
    }

    // returns the bool whether random is activated
    static boolean isRandom() {
        return mode_random;
    }


    /**
    /* Cursor movements:
    */

    // moves the cursor to the next question (by ID)
    static void selectNext() {
        if (mode_random) {
            //randomize();
            selectQuestionById(randomList.getNext());
        }
        moveToNext_save();
    }

    // moves the cursor to the previous question (by ID)
    static void selectPrevious() {
        //moveToPrevious_save();
        selectQuestionById(randomList.getPrevious());
    }

    // moves the cursor to the next favorite (by ID) (could loop 4 ever)
    static void selectNextFavorite() {
        if (mode_random) {
            randomize();
        }

        do {
            moveToNext_save();
        } while (!getSelectedQuestion(dbCursor).favorite);
    }

    // moves the cursor to a specific ID
    static void selectQuestionById(int new_id) {
        Log.d("selectQuestionById: ", ""+new_id);
        while (getSelectedQuestion(dbCursor).id != new_id) {
            moveToNext_save();
        }
    }


    static DatabaseHelper getDbh () {
        return dbh;
    }




    static void setRandom(boolean random) {
        mode_random = random;
    }




    static private void randomize() {
        int rand = new Random().nextInt(dbh.getQuestionCount());

        // move cursor random times to next position
        for (int i = 0; i < rand; i++) {
            moveToNext_save();
        }
        Log.d("qstmng>>>", String.valueOf(getSelectedQuestion(dbCursor).id));
    }

    static void setFavorite(boolean favorite) {
        dbh.setFavorite(getSelectedQuestion(dbCursor).id, favorite);
        refreshCursor();
    }


    // loads a cursor with the current database
    static private void refreshCursor() {
        // saving current id, as getId refers to dbCursor and dbCursor is going to be refreshed
        int i = getId();

        // load the new cursor
        dbCursor = dbh.getCursor();

        // move cursor to first, then to the current id
        dbCursor.moveToFirst();
        selectQuestionById(i);
    }



    static int countFavoredQuestions() {
        return dbh.countFavoredQuestions();

    }



    static private void moveToNext_save() {
        dbCursor.moveToNext();

        if (dbCursor.isAfterLast()) {
            dbCursor.moveToFirst();
        }
    }

    static private void moveToPrevious_save() {
        dbCursor.moveToPrevious();

        if (dbCursor.isBeforeFirst()) {
            dbCursor.moveToLast();
        }
    }


    static Question[] searchQuestion(String searchString) {
        Log.d("QMR: SearchQ>>>", "searching for \'" + searchString + "\'");

        Cursor foundCursor = dbh.searchQuestion(searchString);
        foundCursor.moveToFirst();


        List<Question> foundQuestion = new ArrayList<>();

        while (!foundCursor.isAfterLast()) {

            Question i = getSelectedQuestion(foundCursor);
            foundQuestion.add(i);
            foundCursor.moveToNext();

            //Log.d("searchQuestion>>>", i.question);
        }

        return foundQuestion.toArray(new Question[foundQuestion.size()]);
    }


    static Question getSelectedQuestion(Cursor cursor) {
        // reading the comma seperated lists (potentially single string or empty)

        // Log.d("foo", String.valueOf(cursor.getColumnIndex(DatabaseHelper.KEY_KEYWORDS)));

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
        question.answer_1   = cursor.getString(7); // 5 & 6 = keywords & links
        question.answer_2   = cursor.getString(8);
        question.count_answer_1 = cursor.getInt(9);
        question.count_answer_2 = cursor.getInt(10);
        question.clickables = new String[][] {keywords, links};

        return question;
    }


}