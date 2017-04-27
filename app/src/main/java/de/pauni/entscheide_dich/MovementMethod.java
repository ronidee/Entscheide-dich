package de.pauni.entscheide_dich;

import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextView;

/**
 * Created by roni on 27.04.17.
 * Custom Movementclass to solve issues with clickableSpan (like dragging finger to clickable
 * span, release and causing a click. Tho touch started not at clickable)
 */

class MovementMethod extends LinkMovementMethod {
    private float x = -1;
    private float y = -1;
    private float tolerance = Utilities.convertDpsToPixels(5);

    @Override
    public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
        int action = event.getAction();

        if (action == MotionEvent.ACTION_DOWN) {
            x = (int) event.getX();
            y = (int) event.getY();
        }

        float dx = x - event.getX();
        float dy = y - event.getY();
        float d  = (float) Math.sqrt((dx*dx)+(dy*dy));

        // if dragged distance is bigger than the tolerance (in dp), spam cancle events hehe
        if (d>tolerance) {
            MotionEvent motionEvent = MotionEvent.obtain(10, 20, MotionEvent.ACTION_UP, 0, 0, 0);
            return super.onTouchEvent(widget, buffer, motionEvent);
        }

        return super.onTouchEvent(widget, buffer, event);
    }
}