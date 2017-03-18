package de.pauni.entscheide_dich;

/**
 * Jede Frage ist ein Fragenobjekt, das aus Frage, Gast und Favorit-seien besteht.
 * Sie werden derzeit in Listen in der Fragen-Klasse gespeichert.
 */

class Question {
    int    id        = 0;
    String question  = "fobar";
    String guest     = "fobar";
    String ytlink    = "fobar";
    boolean favorite =  false;
    String[][] clickables = new String[0][0];
}
