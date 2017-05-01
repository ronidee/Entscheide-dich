package de.pauni.entscheide_dich;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * Diese SharedPreferences Helperclass kümmert sich um all den Code zum
 * speichern auslesen und umwandeln von der Favoritenliste und bietet
 * Methoden für einen lockeren, schnell Zugriff. Wow much schnell ._.
 *
 * Diese Klasse ist veraltet und wird durch DatabaseHelper ersetzt werden
 */

//extending activity to be able to call ".getSharedPreferences()"
class SharedPrefs extends Activity {
    private static final String PREFS = "generalPrefs";
    private static SharedPreferences prefs;
    private static SharedPreferences.Editor editor;

    //the keys of the things to save
    private static final String CURRENT_QUESTION_ID = "question_index";
    private static final String FIRST_START  = "first_start";
    private static final String SYNCED_VOTES  = "synced_votes";
    private static final String UPDATED_DB  = "updated_db";

    SharedPrefs(Context c) {
        prefs = c.getSharedPreferences(PREFS, 0);
    }


    static boolean isFirstStart() {
        //If this FIRST_START doesn't exist, return true and change it to false.
        //Else return false
        if( prefs.getBoolean(FIRST_START, true)) {
            editor = prefs.edit();
            editor.putBoolean(FIRST_START, false);
            editor.apply();
            return true;
        } else return false;
    }


    static void setSyncedVotesSuccessfully(boolean synced) {
        editor = prefs.edit();
        editor.putBoolean(SYNCED_VOTES, synced);
        editor.apply();
    }
    static boolean getSyncedVotesSuccessfully() {
        return prefs.getBoolean(SYNCED_VOTES, false);
    }

    static void setUpdatedSuccessfully(boolean synced) {
        editor = prefs.edit();
        editor.putBoolean(UPDATED_DB, synced);
        editor.apply();
    }
    static boolean getUpdatedSuccessfully() {
        return prefs.getBoolean(UPDATED_DB, false);
    }



    static void saveQuestionId(int value) {
        editor = prefs.edit();
        editor.putInt(CURRENT_QUESTION_ID, value);
        editor.apply();
    }
    static int getCurrentQuestionId() {
        return prefs.getInt(CURRENT_QUESTION_ID, 1);
    }
}
