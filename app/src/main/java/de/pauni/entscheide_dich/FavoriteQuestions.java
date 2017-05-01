package de.pauni.entscheide_dich;

import android.util.Log;

import java.util.List;

/**
 * This list keeps track of the favedQ's Ids. Read questions from db until
 * you get a faved.Q resulted in lag and crash
 */

class FavoriteQuestions {
    private List<Integer> list;
    private int index = 0; // index goes 1 by 1 through the randomized list

    FavoriteQuestions() {
        list = SharedPrefs.getFavList();
    }

    void add(int id) {
        SharedPrefs.addFavId(id);
        list.add(id);
    }
    boolean remove(int id) {
        SharedPrefs.removeFavId(id);
        return list.remove(Integer.valueOf(id));
    }


    int getNext() {
        Log.d("RandomizedList", "next");
        index += 1;
        if (index >= list.size()) {
            index = 0;
        }
        return list.get(index);
    }
    int getPrevious() {
        Log.d("RandomizedList", "prev");
        index-=1;
        if (index<0) {
            index = list.size() - 1;
        }
        return list.get(index);
    }
}
