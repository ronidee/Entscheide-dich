package dnnerblasserjunge.entscheidedich;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static android.R.id.list;

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
    private static final String LIST_NUMBER     = "ListNumber";
    private static final String ENTRY_NUMBER    = "EntryNumber";
    private static final String FAVORITES       = "Favorites";

    SharedPrefs(Context c) {
        //Maybe I will need the context variable later...
        prefs = c.getSharedPreferences(PREFS, 0);
    }


    //Setter and getter method

    static void saveListNumber(int value) {
        editor = prefs.edit();
        editor.putInt(LIST_NUMBER, value);
        editor.apply();
    }
    static int getListNumber() {
        return prefs.getInt(LIST_NUMBER, 1);
    }


    static void saveEntryNumber(int value) {
        editor = prefs.edit();
        editor.putInt(ENTRY_NUMBER, value);
        editor.apply();
    }
    static int getEntryNumber() {
        return prefs.getInt(ENTRY_NUMBER, 0);
    }

    static void addFavorite() {
        String currentFavs  = prefs.getString(FAVORITES, "");
        String newFav       = SharedPrefs.getListNumber()+"/"+SharedPrefs.getEntryNumber()+",";

        editor = prefs.edit();
        editor.putString(FAVORITES, currentFavs+newFav);
        editor.apply();
    }

    static void removeFavorite() {
        String favs = prefs.getString(FAVORITES, "");
        String removeFav = SharedPrefs.getListNumber()+"/"+SharedPrefs.getEntryNumber()+",";
        favs = favs.replace(removeFav, "");

        editor = prefs.edit();
        editor.putString(FAVORITES, favs);
        editor.apply();
    }


    static int[][] getFavorites() {
        //rawFavorites are formatted like this: "2/5,4/9,13/3" etc.
        //1. number: listnum. | 2. number: entrynum. | slash: numdevider | comma: entrydevider
        String rawFavorites = prefs.getString(FAVORITES, "");

        if (rawFavorites.equals("")) {
            return null;
        }
        Log.d("SharedPrefs:    ", rawFavorites);
        //removing the redundant and potentially error causing "," at the end
        //rawFavorites = rawFavorites.substring(0, rawFavorites.length()-1);

        //rawListEntries[] are formatted like this: "2/5" "4/9" "13/3" etc.
        String[] rawListEntries = rawFavorites.split(",");


        //those two arrays will only contain listnumbers or entrynumbers
        int arraySize           = rawListEntries.length;
        int[] listNumbers    = new int[arraySize];
        int[] entryNumbers   = new int[arraySize];

        String[] tmpSplitter = {"1", "1"};
        for (int i=0; i<arraySize; i++) {
            //tmpSplitter will contain the listnummber at index=0 and entrynumber at index=1
            //from the current index of rawListEntries. It will be then put into listNumbers
            //and entryNumbers

            tmpSplitter = rawListEntries[i].split("/");
            listNumbers[i]  = Integer.valueOf(tmpSplitter[0]);
            entryNumbers[i] = Integer.valueOf(tmpSplitter[1]);
        }

        if (tmpSplitter != null) {
            Log.d("SharedPrefs:    ", "N:   "+ Arrays.toString(listNumbers));
            Log.d("SharedPrefs:    ", "E:   "+ Arrays.toString(entryNumbers));
        }

        //Returning two arrays at the same time. Alternative solution? I don't like this one -_-
        return new int[][] {listNumbers, entryNumbers};
    }

    static void saveFragen(String fragen) {
        editor = prefs.edit();
        editor.putString("Fragen", fragen);
        editor.apply();
    }
    static String[] getFragen() {
        String [] fragen = null;
        return fragen;
    }
}
