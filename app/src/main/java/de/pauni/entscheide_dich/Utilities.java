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

/**
 * Ansammlung von einigen häufiger genutzten oder sperrigen Funktionen
 */

class Utilities {
    private static String info = "";

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
                    c.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(link)));
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    ds.setUnderlineText(true);
                    //ds.setColor(c.getResources().getColor(R.color.link_color));
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

    static int convertDpsToPixels(Context c, int dps) {
        final float scale = c.getResources().getDisplayMetrics().density;
        return (int) (dps * scale + 0.5f);
    }

    // for sharing txt via other applications
    static void shareContent (Context c, String message, String mimetype) {
        //Toast.makeText(context, "Frage wird verpackt...", Toast.LENGTH_SHORT).show();

        Intent intent   = new Intent(Intent.ACTION_SEND);
        intent.setType  (mimetype);
        intent.putExtra (Intent.EXTRA_TEXT, message +  "\n\nEntscheide Dich! Von Roni und Paul");
        c.startActivity (Intent.createChooser(intent, "Teile die Frage mit Feinden:"));
    }


}

