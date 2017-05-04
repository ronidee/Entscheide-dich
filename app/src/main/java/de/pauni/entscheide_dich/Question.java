package de.pauni.entscheide_dich;

import android.util.Log;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;


/**
 * Jede Frage ist ein Fragenobjekt, das aus Frage, Gast und Favorit-sein etc. besteht.
 *
 */

class Question {
    int    id        = 0;
    String question  = "foobar";
    String guest     = "foobar";
    String hashtag   = "HASHTAG NOCH NICHT HINZUGEFÜGT";
    String ytlink    = "foobar";
    String answer_1  = "Antwortmöglichkeit 1";
    String answer_2  = "Antwortmöglichkeit 2";
    int localvote = 0;
    int count_answer_1 = 0;
    int count_answer_2 = 0;

    boolean favorite =  false;
    String[][] clickables = new String[0][0];

    String getHash() {
        String sum = id + question + guest + ytlink + answer_1 + answer_2
                + count_answer_1 + count_answer_2;

        String hash = Hashing.md5().hashString(sum, Charsets.UTF_8).toString();
        //Log.d("Question/getHash()", hash);
        return hash;
    }

}
