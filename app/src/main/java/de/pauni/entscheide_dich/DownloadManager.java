package de.pauni.entscheide_dich;

import android.content.Context;
import android.database.Cursor;
import android.os.SystemClock;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;


/**
 * Created by roni on 26.04.17.
 * Manages all statistic related stuff like down- and uploading.
 */



class DownloadManager {
    private static String CT_JSON = "application/json";
    private static String URL_UPDATE = "http://anito.org:5000/api/update_questions";
    private static String URL_GETALL = "http://0x000.net/api/all_questions";
    private static String URL_VOTE   = "http://anito.org:5000/api/vote";
    private static DatabaseHelper dbh;
    private Context context;

    DownloadManager(final Context context) {
        this.context = context;
        dbh = new DatabaseHelper(context);

        Runnable voteSynchronizer = new Runnable() {
            @Override
            public void run() {
                while (true) {
                    SharedPrefs.setSyncedVotesSuccessfully(false);

                    int voteCount = dbh.getVoteBufferCount();
                    if (voteCount != 0) {
                        int questionId;
                        int ansNum;
                        Question question;

                        final JSONObject mainObj = new JSONObject();
                        JSONArray ja = new JSONArray();

                        for (int i = 1; i <= voteCount; i++) {
                            int[] vote = dbh.getVote(i);
                            questionId = vote[0];
                            ansNum = vote[1];

                            // pasting the information from table_vote_buffer to table_questions
                            question = dbh.getQuestion(questionId);
                            question.localvote = ansNum;
                            dbh.updateQuestion(question);

                            // generate a json object with the votes
                            JSONObject jo = new JSONObject();

                            try {
                                jo.put("id", questionId);
                                jo.put("answer", ansNum);
                                ja.put(jo);
                            } catch (JSONException e) { e.printStackTrace(); }
                        }

                        try {
                            mainObj.put("device_id", SharedPrefs.getUID());
                            mainObj.put("i_vote", ja);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        // send json object to the server, save success-state
                        String inputLine = sendToServer(mainObj.toString(), CT_JSON, URL_VOTE);
                        Log.d("DWNmgr", mainObj.toString());
                        if (inputLine != null && inputLine.equals("ok")) {
                            SharedPrefs.setSyncedVotesSuccessfully(true);

                            // server received the votes, they're safe to delete now
                            dbh.clearVotingBuffer();
                        }

                        // toast the result
                        Utilities.toast(SharedPrefs.getSyncedVotesSuccessfully()
                                ? "Deine Antworten wurden erfolgreich abgeschickt"
                                : "Deine Antworten konnten nicht abgeschickt werden", context);
                    }

                    SystemClock.sleep(1000 * 10); //sleep 5 Minutes
                }
            }
        };
        Thread voteSynchronizerThread = new Thread(voteSynchronizer);
        voteSynchronizerThread.start();
    }



    // Send inventory sheet to server, receive update, save update to db
    void updateDatabase() {
        Log.d("DownloadManager", "updateDatabase aufgerufen");

        final String success = "Update erfolgreich";    // Toast messages
        final String fail    = "Update fehlgeschlagen";

        Runnable networkRunnable = new Runnable() {
            @Override
            public void run() {

                SharedPrefs.setUpdatedSuccessfully(false);
                String jsonString = sendToServer(getInventorySheet(), CT_JSON, URL_UPDATE);
                writeInputTodatabase(jsonString);
                Utilities.toast(SharedPrefs.getUpdatedSuccessfully() ? success : fail, context);

            }
        };
        new Thread(networkRunnable).start();
    }
    // save the of the user into a secondary table in the db
    static void plusOne(int questionId, int ansNum) {
        // save vote in local database
        dbh.addVoteToBuffer(questionId, ansNum);

    }




    // writes json containing Questions objects to the db
    private static void writeInputTodatabase(String jsonstring) {
        Log.d("DownloadManager", "writeInputTodatabase aufgerufen");

        if (jsonstring == null) {
            return; //error alert is handled at method call
        }


        // create question-lists from the respective jsonarrays

        JSONObject      rootobj;
        List<Question>  add;
        List<Question>  update;
        List<Question>  delete;

        try {
            rootobj = new JSONObject(jsonstring);
            //add     = Utilities.toQuestionList(rootobj.getJSONArray("add"   ));
            update  = Utilities.toQuestionList(rootobj.getJSONArray("update"));
            delete  = Utilities.toQuestionList(rootobj.getJSONArray("delete"));
        } catch (JSONException e) {
            e.printStackTrace();
            return; //error alert is handled at method call
        }

        // add all questions of the list to the db
        /*for (int i=0; i<add.size(); i++) {          // IF CRASH HERE, INIT LIST AFTER DECLARE
            dbh.addQuestion(add.get(i));
        }*/

        // update all questions of the list in the db
        for (int i=0; i<update.size(); i++) {       // IF CRASH HERE, INIT LIST AFTER DECLARE
            dbh.updateQuestion(update.get(i));
        }

        // delete all questions of the list from the db
        /*for (int i=0; i<delete.size(); i++) {
            dbh.deleteQuestion(update.get(i));
        }*/

        SharedPrefs.setUpdatedSuccessfully(true);
    }

    // generates a inventory sheet of all questions'(id + md5-checksum. Format: json)
    private static String getInventorySheet() {
        Log.d("DownloadManager", "getInventorySheet aufgerufen");

        Cursor cursor = dbh.getCursor();
        cursor.moveToFirst();

        JSONObject mainObj = new JSONObject();
        JSONArray  ja = new JSONArray();
        try {
            while (!cursor.isAfterLast()) {
                // get the current question, which the cursor selects
                Question question = DatabaseHelper.getSelectedQuestion(cursor);
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

    // returns null, if failed
    private static String sendToServer(final String output, final String contentType, String address) {
        Log.d("DownloadManager", "sendToServer aufgerufen");

        URL url;
        try {
            url = new URL(address);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }

        final HttpURLConnection httpCon;
        try {
            Log.d("DownloadManager", "pass1");
            httpCon = (HttpURLConnection) url.openConnection();
            Log.d("DownloadManager", "pass2");
            httpCon.setDoOutput(true);
            Log.d("DownloadManager", "pass3");
            httpCon.setRequestProperty("Content-Type", contentType);
            httpCon.setRequestProperty("X-APIKEY", "123456789");
            Log.d("DownloadManager", "pass4");
            httpCon.setRequestMethod("POST");
            Log.d("DownloadManager", "pass4.1");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        String inputLine = "";
        try {
            OutputStream outputStream = httpCon.getOutputStream();
            Log.d("DownloadManager", "pass5");
            OutputStreamWriter out = new OutputStreamWriter(outputStream);
            Log.d("DownloadManager", "pass5.1");
            out.write(output);
            out.close();
            BufferedReader input = new BufferedReader(new InputStreamReader(
                    httpCon.getInputStream()));

            Log.d("DownloadManager", "pass6");
            String buffer;
            while ((buffer = input.readLine()) != null) {
                inputLine += buffer;
                Log.d("DownloadManager", "pass7...");
            }
            input.close();
            Log.d("DlMgr/inutline", inputLine);

        } catch (IOException e) {
            e.printStackTrace();
            return "download failed";
        }

        return inputLine;
    }

}
