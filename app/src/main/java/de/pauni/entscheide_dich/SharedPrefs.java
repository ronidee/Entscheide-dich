package de.pauni.entscheide_dich;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;


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
    private static final String INDEX_NUMBER = "question_index";
    private static final String FIRST_START  = "first_start";

    SharedPrefs(Context c) {
        //Maybe I will need the context variable later...
        prefs = c.getSharedPreferences(PREFS, 0);
    }




    static void saveQuestionId(int value) {
        editor = prefs.edit();
        editor.putInt(INDEX_NUMBER, value);
        editor.apply();
    }
    static int getCurrentQuestionId() {
        return prefs.getInt(INDEX_NUMBER, 1);
    }
}
