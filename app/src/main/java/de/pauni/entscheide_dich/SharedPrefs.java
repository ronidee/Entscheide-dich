package de.pauni.entscheide_dich;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Arrays;
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
    private static final String FIRST_START   = "first_start";
    private static final String SYNCED_VOTES  = "synced_votes";
    private static final String UPDATED_DB    = "updated_db";
    private static final String FAV_IDS        = "fav_ids";

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



    /**
     * Store and read state of syncing local votes with server
     */
    static void     setSyncedVotesSuccessfully(boolean synced) {
        editor = prefs.edit();
        editor.putBoolean(SYNCED_VOTES, synced);
        editor.apply();
    }
    static boolean  getSyncedVotesSuccessfully() {
        return prefs.getBoolean(SYNCED_VOTES, false);
    }



    /**
     * Store and read state of updating db
     */
    static void     setUpdatedSuccessfully(boolean synced) {
        editor = prefs.edit();
        editor.putBoolean(UPDATED_DB, synced);
        editor.apply();
    }
    static boolean  getUpdatedSuccessfully() {
        return prefs.getBoolean(UPDATED_DB, false);
    }



    /**
     * Store and read question id
     */
    static void saveQuestionId(int value) {
        editor = prefs.edit();
        editor.putInt(CURRENT_QUESTION_ID, value);
        editor.apply();
    }
    static int  getCurrentQuestionId() {
        return prefs.getInt(CURRENT_QUESTION_ID, 1);
    }



    /**
     * Store and read list of favorite's ids
     */
    static void addFavId(int id) {
        String favIdListid = prefs.getString(FAV_IDS, null);
        String saving;
        if(favIdListid == null) {
            saving = id + ",";
        } else {
            saving = favIdListid + id + ",";
        }
        editor = prefs.edit();
        editor.putString(FAV_IDS, saving);
        editor.apply();
    }
    static void removeFavId(int id) {
        String favIdListid = prefs.getString(FAV_IDS, null);
        if (favIdListid == null) {
            return;
        }

        String saving = favIdListid.replace(id+",", "");
        editor = prefs.edit();
        editor.putString(FAV_IDS, saving);
        editor.apply();
    }
    static List<Integer> getFavList() {
        String favIdListid = prefs.getString(FAV_IDS, "");

        if (favIdListid.equals("")) {
            return new ArrayList<>();
        }

        List<String> favIds = Arrays.asList(favIdListid.split(","));
        List<Integer> favList = new ArrayList<>();

        for (int i=0; i<favIds.size(); i++) {
            favList.add(Integer.valueOf(favIds.get(i)));
        }

        return favList;
    }

    static String getUID() {
        return prefs.getString("uid", Utilities.generateUniqueId());

    }
}
