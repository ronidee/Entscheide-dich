package de.pauni.entscheide_dich;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Der QuestionManager ist die Schnittstelle zwischen der MainActivity und dem DatabaseHelper
 * Außerdem wird der Fragenverlauf (laufzeitlang) protokolliert, damit man in den Fragen
 * auch zurückgehen kann
 *
 */



class QuestionManager {
    private static DatabaseHelper dbh;
    private static RandomizedList randomizedList;
    private static int currentQuestionId;
    private static FavoriteQuestions favoriteQuestions = new FavoriteQuestions();

    // constructor
    QuestionManager(Context context) {
        dbh      = new DatabaseHelper(context);
        randomizedList = new RandomizedList(dbh.getQuestionCount());
        currentQuestionId = randomizedList.getNext();

        DownloadManager downloadManager = new DownloadManager(context);
        downloadManager.updateDatabase();
    }

    /**
     *   Getter methods x)
     */
    // returns the current question
    static Question getQuestion() {
        return dbh.getQuestion(currentQuestionId);
    }

    // returns ID of the current question
    static int getId() {
        return currentQuestionId;
    }



    /**
     * Methods for selecting!
     */
    // moves the cursor to the next question (by ID)
    static void selectNext() {
        //moveToNext_save();
        currentQuestionId = randomizedList.getNext();
    }
    // moves the cursor to the previous question (by ID)
    static void selectPrevious() {
        //moveToPrevious_save();
        currentQuestionId = randomizedList.getPrevious();

    }
    // select a question by its Id
    static void selectQuestionById(int new_id) {
        if (new_id>dbh.getQuestionCount()){
            Log.d("selectQuestionById", "id groesser als Questioncount");
            return;
        }

        while (currentQuestionId != new_id) {
            currentQuestionId = randomizedList.getNext();
        }
    }



    /**
     * Methods about favorites <3
     */
    // set current question as favorite
    static void setFavorite(boolean favorite) {
        if (favorite)
            favoriteQuestions.add(currentQuestionId);
        else
            favoriteQuestions.remove(currentQuestionId);

        dbh.setFavorite(currentQuestionId, favorite);
    }
    // moves the cursor to the next favorite (by ID) (could loop 4 ever)
    static void selectNextFavorite() {
        currentQuestionId = favoriteQuestions.getNext();
    }
    // counts the favored questions
    static int countFavoredQuestions() {
        return dbh.countFavoredQuestions();

    }



    /**
     * Method for searching o.o
     */
    // search al questions for a certain string
    static Question[] searchQuestion(String searchString) {
        Log.d("QMR: SearchQ>>>", "searching for \'" + searchString + "\'");

        List<Question> foundQuestions = dbh.searchQuestions(searchString);

        return foundQuestions.toArray(new Question[foundQuestions.size()]);
    }
}