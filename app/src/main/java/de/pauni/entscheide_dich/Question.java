package de.pauni.entscheide_dich;

/**
 * Jede Frage ist ein Fragenobjekt, das aus Frage, Gast und Favorit-sein etc. besteht.
 *
 */

class Question {
    int    id        = 0;
    String question  = "foobar";
    String guest     = "foobar";
    String ytlink    = "foobar";
    String answer_1  = "Antwortmöglichkeit 1";
    String answer_2  = "Antwortmöglichkeit 2";
    int count_answer_1 = 0;
    int count_answer_2 = 0;

    boolean favorite =  false;
    String[][] clickables = new String[0][0];
}
