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
 * Created by Roni on 19.03.2017.
 */

public class Utilities {
    static String info = "";

    static SpannableString getClickableText(final Context c, String text, String[][] clickables) {

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
                    super.updateDrawState(ds);
                    ds.setUnderlineText(true);
                    ds.setColor(c.getResources().getColor(R.color.link_color));
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

    static String addInfo(String string) {
        return info += " " + string;
    }
}

