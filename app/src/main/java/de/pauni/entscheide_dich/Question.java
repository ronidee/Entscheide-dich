package de.pauni.entscheide_dich;

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
    int answer_1_count = 0;
    int answer_2_count = 0;

    boolean favorite =  false;
    String[][] clickables = new String[0][0];

    String getHash() {
        String sum = question + guest + ytlink + answer_1 + answer_2
                + answer_1_count + answer_2_count;

        return Hashing.md5().hashString(sum, Charsets.UTF_8).toString();
    }

}
