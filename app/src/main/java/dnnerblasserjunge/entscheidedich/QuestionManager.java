package dnnerblasserjunge.entscheidedich;

import java.util.concurrent.ThreadLocalRandom;
import java.util.Arrays;
import java.util.List;

/**
 *
 * Der QuestionManager ist die Schnittstelle zwischen der MainActivity und dem DatabaseHelper
 * Hier werden die Frage-Objekte bereitgestellt und automatisch der nächste Eintrag
 * aus der Datenbank geholt.
 * Außerdem wird der Fragenverlauf (laufzeitlang) protokolliert, damit man in den Fragen
 * auch zurückgehen kann
 *
 */

public class QuestionManager {
    private List<int[]> previousQuestions;

    private static String youtubeLink;

    public QuestionManager() {
        //I'm only using the constructor to init things. Bad habit?
    }

    Question getCurrent() {
        return new Question();
    }

    void Next() {
        // TODO: load next question and save to sahred preferences
    }

    void Previous() {
        // TODO: load prvious question and save to sahred preferences
    }

    void Randomize() {
        // Todo: load a random questin
    }

    void setFavorite(boolean favorite) {
        getCurrent().favorite = favorite;
    }
}
