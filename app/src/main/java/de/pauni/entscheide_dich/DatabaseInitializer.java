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

    public DatabaseInitializer(Context c) {
        context = c;
        DatabaseHelper db = new DatabaseHelper(c);

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
                    jsonstr.append('\n');
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

                /*
                String id = c.getString("id");
                String title = c.getString("title");
                String duration = c.getString("duration");
                //use >  int id = c.getInt("duration"); if you want get an int


                // tmp hashmap for single node
                HashMap<String, String> parsedData = new HashMap<String, String>();

                // adding each child node to HashMap key => value
                parsedData.put("id", id);
                parsedData.put("title", title);
                parsedData.put("duration", duration);


                // do what do you want on your interface

                */
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

