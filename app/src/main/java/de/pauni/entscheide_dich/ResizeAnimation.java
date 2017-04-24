package de.pauni.entscheide_dich;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * This class animates resizing a view.
 * Thanks to the_prole and longilong for the answer: http://stackoverflow.com/a/33095268
 */

class ResizeAnimation extends Animation {
    private final int targetHeight;
    private View view;
    private int startHeight;

    ResizeAnimation(View view, int targetHeight, int startHeight) {
        this.view = view;
        this.targetHeight = targetHeight;
        this.startHeight = startHeight;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        //int newHeight = (int) (targetHeight * interpolatedTime);
        //to support decent animation, change new heigt as Nico S. recommended in comments
        view.getLayoutParams().height =
                (int) (startHeight + (targetHeight - startHeight) * interpolatedTime);
        view.requestLayout();
    }

    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
    }

    @Override
    public boolean willChangeBounds() {
        return true;
    }
}