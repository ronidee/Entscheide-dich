package de.pauni.entscheide_dich;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Random list, but only type integer. But the name "RandomIntegerList" or "RandomIntList"
 * isn't fancy enough, so  this RandomIntList is just a pretender. In other words:
 * A wannabe RandomList
 */

class RandomList {
    private List<Integer> list = new ArrayList<>();
    private int index;
    private int listsize;

    RandomList(int listsize) {
        this.listsize = listsize;
        index = listsize-1;
        create(listsize);
        Collections.shuffle(this.list);
    }

    private void create (int size) {
        for (int i=1; i<=size; i++) {
            list.add(i);
        }
    }

    int getNext() {
        Log.d("RandomList", "next");
        index += 1;
        if (index>=listsize) {
            index = 1;
        }
        return list.get(index);
    }
    int getPrevious() {
        Log.d("RandomList", "prev");
        index-=1;
        if (index<0) {
            index = listsize;
        }
        return list.get(index);
    }
    void shuffle() {
        Collections.shuffle(list);
    }
    void delete() {
        list = new ArrayList<>();
    }
    void replace(int listsize) {
        this.listsize = listsize;
        create(listsize);
        shuffle();
    }
}
