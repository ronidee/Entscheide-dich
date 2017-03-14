package de.pauni.entscheide_dich;


/**
 * Jede Frage ist ein Fragenobjekt, das aus Frage, Gast und Favorit-seien besteht.
 * Sie werden derzeit in Listen in der Fragen-Klasse gespeichert.
 */

class Question {
    private boolean favorit = false;
    private String  questionString;
    private String  guest = "Der Gleiche";
    private String  ytLink = "kein Link";
    private String info ="nix"; // seperate names with ","

    Question(String text, String sendung) {
        this.questionString = text;
        this.guest    = sendung;
    }

    void setFavorit(Boolean favorit) {
        this.favorit = favorit;
    }
    void setYtLink(String ytLink) {
        this.ytLink = ytLink;
    }
    void setInfo(String info) {
        this.info = info;
    }

    boolean isFavorit() {
        return favorit;
    }
    String getQuestionString() {
        return questionString;
    }
    String getGuestString() {
        return guest;
    }
    String getYTLink() {
        return ytLink;
    }
    String getInfo() {
        return info;
    }
}
