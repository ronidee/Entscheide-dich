package de.pauni.entscheide_dich;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by roni on 01.05.17.
 */

public class JSONArray extends org.json.JSONArray {

    List<Question> questionList() {
        List<Question> questionList = new ArrayList<>();

        // looping through All nodes
        for (int i = 0; i < this.length(); i++) {
            try {
            JSONObject questionobj = this.getJSONObject(i);

            // load links and keywords from one question and put them into one "clickables"
            org.json.JSONArray clickablesobj = questionobj.getJSONArray("clickable");
            String[][] clickables = new String[clickablesobj.length()][1];

            for (int l = 0; l < clickablesobj.length(); l++) {
                JSONObject keyword = clickablesobj.getJSONObject(l);
                clickables[l] = new String[]{keyword.getString("keyword"), keyword.getString("link")};
            }

            Question question = new Question();
            question.question = questionobj.getString("question");
            question.guest = questionobj.getString("guest");
            question.ytlink = questionobj.getString("ytlink");
            question.answer_1 = questionobj.getString("answer1");
            question.answer_2 = questionobj.getString("answer2");
            question.clickables = clickables;

            questionList.add(question);
            } catch (JSONException e) {
                e.printStackTrace();
                return new ArrayList<>();
            }
        }
    return questionList;
    }
}
