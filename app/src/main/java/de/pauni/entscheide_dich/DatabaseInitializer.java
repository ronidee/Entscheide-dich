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
 * Created by Roni on 13.03.2017.
 */

public class DatabaseInitializer {
Context context;
DatabaseHelper dbh;

    public DatabaseInitializer(Context c) {
        context = c;
        dbh = new DatabaseHelper(c);
        load_init_data();
    }


    void load_init_data() {
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
                JSONObject question = all_questions.getJSONObject(i);

                Log.d("foo", question.getString("question"));
                Log.d("foo", question.getString("guest"));
                Log.d("foo", question.getString("ytlink"));
                Log.d("foo", "\n");

                Question quest = new Question();

                quest.question = question.getString("question");
                quest.guest    = question.getString("guest");
                quest.ytlink   = question.getString("ytlink");


                JSONArray keywordsobj = question.getJSONArray("keywords");


                String[][] keywords = new String [keywordsobj.length()][1];

                for (int l = 0; l < keywordsobj.length(); l++) {
                    JSONObject keyword = keywordsobj.getJSONObject(j);
                    keywords[l] = new String[]{ keyword.getString("string"), keyword.getString("link")};
                }





            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

