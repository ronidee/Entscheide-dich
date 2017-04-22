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
    boolean favorite =  false;
    String[][] clickables = new String[0][0];
}
