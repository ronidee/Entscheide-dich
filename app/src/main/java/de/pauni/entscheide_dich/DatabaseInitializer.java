package de.pauni.entscheide_dich;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * This class is executed only once to generate a database containing
 * all the question-objects from the json file
 */

class DatabaseInitializer {
    private Context context;
    private DatabaseHelper dbh;

    DatabaseInitializer(Context c) {
        Log.d("DBI", "Konstruktor geladen");
        context = c;
        dbh = new DatabaseHelper(c);
        load_init_data();
    }


    private void load_init_data() {
        // TODO: Load all the question from a json file
        // do what do you want on your interface


        try {
            // File yourFile = new File("path/to/the/file/inside_the_sdcard/textarabics.txt");
            // FileInputStream stream = new FileInputStream(yourFile);

            InputStream stream = context.getResources().openRawResource(
                    context.getResources().getIdentifier("questions", "raw", context.getPackageName()));


            InputStreamReader inputreader = new InputStreamReader(stream);
            BufferedReader buffreader = new BufferedReader(inputreader);
            StringBuilder jsonstr = new StringBuilder();

            try {
                String line;
                while ((line = buffreader.readLine()) != null) {
                    jsonstr.append(line);
                }
            } catch (IOException e) {
                // Do something
            }

            /*
            {
                "data": [
                    {
                        "id": "1",
                        "title": "Farhan Shah",
                        "duration": 10
                    },
                    {
                        "id": "2",
                        "title": "Noman Shah",
                        "duration": 10
                    }
                ]
            }
            */


            JSONArray all_questions = new JSONArray(jsonstr.toString());



            // looping through All nodes
            for (int i = 0; i < all_questions.length(); i++) {
                JSONObject questionobj = all_questions.getJSONObject(i);

                Log.d("DatabaseInitializer", "create entry " + questionobj.getString("guest"));


                // load links and keywords from one question and put them into one "clickables"
                JSONArray clickablesobj = questionobj.getJSONArray("clickable");
                String[][] clickables = new String [clickablesobj.length()][1];

                for (int l = 0; l < clickablesobj.length(); l++) {
                    JSONObject keyword = clickablesobj.getJSONObject(l);
                    clickables[l] = new String[]{ keyword.getString("keyword"), keyword.getString("link")};
                }


                Question question = new Question();

                question.question = questionobj.getString("question");
                question.guest    = questionobj.getString("guest");
                question.hashtag  = questionobj.getString("hashtag");
                question.ytlink   = questionobj.getString("ytlink");
                question.answer_1 = questionobj.getString("answer1");
                question.answer_2 = questionobj.getString("answer2");
                question.clickables = clickables;

                dbh.addQuestion(question);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

