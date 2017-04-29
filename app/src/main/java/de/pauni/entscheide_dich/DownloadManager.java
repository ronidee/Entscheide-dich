package de.pauni.entscheide_dich;

import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * Created by roni on 26.04.17.
 * Manages all statistic related stuff like down- and uploading.
 */



class DownloadManager {
    private static String CT_JSON = "application/json";



    // returns null, if failed
    private static String sendToServer(final String output, final String contentType) {
        URL url;
        try {
            url = new URL("http://0x000.net/api");
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }

        final HttpURLConnection httpCon;
        try {
            httpCon = (HttpURLConnection) url.openConnection();
            httpCon.setDoOutput(true);
            httpCon.setRequestProperty("Content-Type", contentType);
            httpCon.setRequestProperty("X-APIKEY", "123456789");
            httpCon.setRequestMethod("PUT");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        String inputLine;
        try {
            OutputStreamWriter out = new OutputStreamWriter(httpCon.getOutputStream());

            out.write(output);
            out.close();
            BufferedReader input = new BufferedReader(new InputStreamReader(
                    httpCon.getInputStream()));

            while ((inputLine = input.readLine()) != null)
                Log.d("DlMgr/inutline", inputLine);
            input.close();

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return inputLine;
    }


    void updateDatabase() {

        Runnable networkRunnable = new Runnable() {
            @Override
            public void run() {


                String jsonString = sendToServer(getInventorySheet(), CT_JSON);
                if (jsonString != null) {
                    writeInputTodatabase(jsonString);
                }


            }
        };
        new Thread(networkRunnable).start();
    }



    private static String getInventorySheet() {
        DatabaseHelper dbh = QuestionManager.getDbh();
        Cursor cursor = dbh.getCursor();
        cursor.moveToFirst();

        JSONObject mainObj = new JSONObject();
        JSONArray  ja = new JSONArray();
        try {
            while (!cursor.isAfterLast()) {
                // get the current question, which the cursor selected
                Question question = QuestionManager.getSelectedQuestion(cursor);
                cursor.moveToNext();

                // Gen. a JObj with id and hashed question
                JSONObject jo = new JSONObject();
                jo.put("id", question.id);
                jo.put("hash", question.getHash());
                //add it to the JArray
                ja.put(jo);
            }

            mainObj.put("i_have", ja);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        return mainObj.toString();
    }
    private static void writeInputTodatabase(String jsonstring) {
        if (jsonstring == null) {
            Log.d("DlMgr/WITd", "null");
        }

        try {



            DatabaseHelper dbh = QuestionManager.getDbh();

            JSONArray all_questions = new JSONArray(jsonstring);



            // looping through All nodes
            for (int i = 0; i < all_questions.length(); i++) {
                JSONObject questionobj = all_questions.getJSONObject(i);

                Log.d("DatabaseInitializer", questionobj.getString("question"));
                Log.d("DatabaseInitializer", questionobj.getString("guest"));
                Log.d("DatabaseInitializer", questionobj.getString("ytlink"));
                Log.d("DatabaseInitializer", questionobj.getString("answer1"));
                Log.d("DatabaseInitializer", questionobj.getString("answer2"));

                // load links and keywords from one question and put them into one "clickables"
                JSONArray clickablesobj = questionobj.getJSONArray("clickable");
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

                dbh.updateQuestion(question);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    void plusOne(Context c, int questionId, int ansNum) {
        // save vote in local database

        DatabaseHelper dbh = new DatabaseHelper(c);

        Question q = dbh.getQuestion(questionId); //read the current question from the db
        q.localvote = ansNum;                      // change the localvote
        dbh.updateQuestion(q);                    // save the modified question to the db

        // send it to the server
        final JSONObject mainObj = new JSONObject();
        JSONArray ja = new JSONArray();
        JSONObject jo = new JSONObject();

        try {
            jo.put("id", questionId);
            jo.put("answer", ansNum);

            ja.put(jo);

            mainObj.put("i_vote", ja);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Runnable networkRunnable = new Runnable() {
            @Override
            public void run() {
                String inputLine = sendToServer(mainObj.toString(), CT_JSON);
                if (inputLine != null) {
                    SharedPrefs.setSyncedLocalvotes(false);
                    return;
                }

                SharedPrefs.setSyncedLocalvotes(true);
                final Handler handler = new Handler();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("Dlm", "trying..");
                        handler.postDelayed(this, 500);
                    }
                });
            }
        };

        new Thread(networkRunnable).start();
    }

}
