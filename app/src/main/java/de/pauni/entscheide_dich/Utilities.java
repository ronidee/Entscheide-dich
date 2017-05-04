package de.pauni.entscheide_dich;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Ansammlung von einigen häufiger genutzten oder sperrigen Funktionen
 */

class Utilities {
    private static String info = "";
    static float scale = 0;


    // returns a spannablestring with the passed keywords(clickables) being clickable
    static SpannableString getClickableText(final Context c, String text, String[][] clickables) {
        // splitting the text at the word "oder". (result: aaaaa \n or \n bbbbb)

        SpannableString ss = new SpannableString(text);

        //
        if (clickables[0][0].equals("")) {
            Log.d("clickable", "empty");
            return ss;
        }
        final String[] keywords = clickables[0];
        final String[] links    = clickables[1];

        for (int i = 0; i < clickables[0].length; i++) {
            info = "";
            int indexStart = text.lastIndexOf(keywords[i]);
            int indexEnd = indexStart + keywords[i].length();

            final String link = links [i];

            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(View textView) {
                    Log.d("Utilities","clickableSpan onClick");
                    c.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(link)));
                }


                @Override
                public void updateDrawState(TextPaint ds) {
                    ds.setUnderlineText(true);
                }
            };
            ss.setSpan(clickableSpan, indexStart, indexEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            addInfo(""+indexStart);
            addInfo(""+indexEnd);
            addInfo(keywords[i]);
            addInfo(link);
            Log.d("Utilities", info);

        }
        return ss;
    }
    // Methode of getClickableText
    private static String addInfo(String string) {
        return info += " " + string;
    }

    static float convertDpsToPixels(int dps) {
        return (dps * scale + 0.5f);
    }

    // for sharing txt via other applications
    static void shareContent (Context c, String message, String mimetype) {
        //Toast.makeText(context, "Frage wird verpackt...", Toast.LENGTH_SHORT).show();

        Intent intent   = new Intent(Intent.ACTION_SEND);
        intent.setType  (mimetype);
        intent.putExtra (Intent.EXTRA_TEXT, message +  "\n\nEntscheide Dich! Von Roni und Paul");
        c.startActivity (Intent.createChooser(intent, "Teile die Frage mit Feinden:"));
    }

    static List<Question> toQuestionList(JSONArray jsonArray) {
        List<Question> questionList = new ArrayList<>();

        // looping through All nodes
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject questionobj = jsonArray.getJSONObject(i);

                // load links and keywords from one question and put them into one "clickables"
                org.json.JSONArray clickablesJArray = questionobj.getJSONArray("keywords"); // KEYWORDS ARE ACTUALLY INFOLINKS/CLICKABLES IF YOU READ THIS AND YOU THINK WHAT, THIS DOESN'T MAKE ANY SENSE, DON'T FEEL ALONE MY FRIEND YOU'RE NOT ALINE. I THINK SO TOO BUT PAUL IS LAZY AND CLOSE MINDED.
                String[][] clickables = new String[clickablesJArray.length()][1];

                for (int l = 0; l < clickablesJArray.length(); l++) {
                    JSONObject keyword = clickablesJArray.getJSONObject(l);
                    clickables[l] = new String[]{keyword.getString("keyword"), keyword.getString("link")};
                }

                Question question   = new Question();
                question.id         = Integer.valueOf(questionobj.getString("id"));
                question.question   = questionobj.getString("question");
                question.guest      = questionobj.getString("guest");
                question.ytlink     = questionobj.getString("youtube_link");
                question.answer_1   = questionobj.getString("answer_1");
                question.answer_2   = questionobj.getString("answer_2");
                question.answer_1_count = questionobj.getInt("answer_1_count");
                question.answer_2_count = questionobj.getInt("answer_2_count");
                question.clickables = clickables;

                questionList.add(question);
            } catch (JSONException e) {
                e.printStackTrace();
                return new ArrayList<>();
            }
        }
        return questionList;
    }

    // generate a unique id for the device
    static String generateUniqueId() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }


    static void toast(final String message, final Context c) {
        MainActivity.getHandler().post(new Runnable() {
            @Override
            public void run() {
                if (MainActivity.appInBackground) {
                    Log.d("Utilities", "blocked  toast, because App is in background");
                    return;
                }
                Toast.makeText(c, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

}

