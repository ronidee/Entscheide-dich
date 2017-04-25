package de.pauni.entscheide_dich;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.animation.ScaleAnimation;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;


/*
*
*/

public class MainActivity extends Activity {
    float displayWidth;
    boolean sessionStart = true;
    boolean searchDialogWasOpen = false;

    private final int MODE_NORMAL   = 1;
    private final int MODE_FAV_ONLY = 2;
    private boolean   animationOn   = true;
    private int MODE = MODE_NORMAL;

    //Declaring the views
    TextView    tv_questionIn   =   null;
    TextView    tv_questionOut  =   null;
    TextView    tv_guest        =   null;
    TextView    tv_answer_1     =   null;
    TextView    tv_answer_2     =   null;
    ImageButton ib_naechste     =   null;
    ImageButton ib_favOnly      =   null;
    ImageButton ib_share        =   null;
    ImageButton ib_favorisieren =   null;
    ImageButton ib_zufaellig    =   null;
    ImageButton ib_youtube      =   null;
    ImageButton ib_search       =   null;
    ImageButton ib_sel_answer_1 =   null;
    ImageButton ib_sel_answer_2 =   null;
    RelativeLayout rl_qu_cont   =   null;

    int h; //height of one line of tv_questionX

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("MainActivity", "created");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // equip classes with required objects
        new SharedPrefs(this); // passing context, for static access
        new QuestionManager(this);
        QuestionManager.setId(SharedPrefs.getCurrentQuestionId());

        initViews();
        regListeners();

        updateFavOnlyButtonState();
    }

    //Saving the current Id in 'any' possible cases.
    @Override
    protected void onPause() {
        super.onPause();
        Log.d("MainActivity", "paused");
        SharedPrefs.saveQuestionId(QuestionManager.getId());
    }
    @Override
    protected void onStop() {
        Log.d("MainActivity", "stopped");
        super.onStop();
        SharedPrefs.saveQuestionId(QuestionManager.getId());
    }
    @Override
    protected void onDestroy() {
        Log.d("MainActivity", "destroyed");
        super.onDestroy();
        SharedPrefs.saveQuestionId(QuestionManager.getId());
    }
    @Override
    protected void onResume() {
        if (searchDialogWasOpen) {
            displayQuestion(QuestionManager.getQuestion(), false);
        }
        searchDialogWasOpen = false;

        Log.d("MainActivity", "resumed");
        super.onResume();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        //display the current question at app-start.
        //Doesn't work with onCreate() for some reasons...

        if (!sessionStart) {
            return;
        }
        sessionStart = false;
        //fixTextviewLayoutSize();

        // calculate line height
        h = tv_questionIn.getHeight()/tv_questionIn.getLineCount();
        displayQuestion(QuestionManager.getQuestion(), false);
    }


    // show's the passed question. Possible to animate the transition
    // don't make it private, as SearchQuestionsActivity would lose access
    void displayQuestion (Question question, boolean animated) {

        // clear selections
        ib_sel_answer_1.setImageResource(R.drawable.answer_unselected);
        ib_sel_answer_2.setImageResource(R.drawable.answer_unselected);

        // transforming the questions.clickables and the .question-string into one SpannableString
        // with clickable words, which will bring the user to a web-search about this word
        SpannableString text = Utilities.getClickableText(this, question.question, question.clickables);

        // we actually don't need do this every time...
        tv_questionIn.setMovementMethod(LinkMovementMethod.getInstance());
        tv_questionIn.setHighlightColor(getResources().getColor(R.color.link_highlight_color));


        String  guest   = "Sendung mit " + question.guest;
        boolean favorit = question.favorite;

        // showing, whether this question is a favorite or not
        if (favorit) {
            ib_favorisieren.setColorFilter(getResources().getColor(R.color.nmr_background));
            ib_favorisieren.setImageResource(R.drawable.ic_favorite_white_24dp);
        } else {
            ib_favorisieren.setColorFilter(getResources().getColor(R.color.icon_color));
            ib_favorisieren.setImageResource(R.drawable.ic_favorite_border_white_24dp);
        }

        // animate the transition between the questions, if wanted
        if (animated) {
            slideQuestionOut();
            slideQuestionIn(text);

            changeViewColor(tv_guest, 550, R.color.icon_color, R.color.cardview_background);
            tv_guest.setText(guest);
            changeViewColor(tv_guest, 550, R.color.cardview_background, R.color.icon_color);
        } else {
            tv_questionIn.setText(text);
            tv_guest.setText(guest);
        }

        tv_answer_1.setText(question.answer_1);
        tv_answer_2.setText(question.answer_2);

    }
    // generates a sliding-out animation for the old question
    private void slideQuestionOut() {
        // preparing textview for animation
        tv_questionOut.setX(tv_questionIn.getX());
        tv_questionOut.setText(tv_questionIn.getText());
        tv_questionOut.setVisibility(View.VISIBLE);
        // slide textview out of the window
        tv_questionOut.animate().translationX(-displayWidth);
    }
    // generates a sliding-in animation for the new question
    private void slideQuestionIn(SpannableString question) {
        tv_questionIn.setX(displayWidth);
        tv_questionIn.setText(question);

        // slide textview to it's original position (0=origin)
        tv_questionIn.animate().translationX(0);

        //rl_qu_cont.getLayoutParams().height = tv_questionIn.getLineCount() * h;
        animateSizeChange(rl_qu_cont.getHeight(), tv_questionIn.getLineCount() * h);

    }
    // generates a size-change animation
    private void animateSizeChange(int fromY, int toY) {
        ResizeAnimation resizeAnimation = new ResizeAnimation(rl_qu_cont, toY, fromY);
        resizeAnimation.setDuration(200);
        rl_qu_cont.startAnimation(resizeAnimation);
    }

    // initializes all views
    private void initViews() {
        tv_questionIn   =   (TextView)    findViewById(R.id.textview_question_in);
        tv_questionOut  =   (TextView)    findViewById(R.id.textview_question_out);
        tv_guest        =   (TextView)    findViewById(R.id.textview_sendung);
        tv_answer_1     =   (TextView)    findViewById(R.id.tv_answer_1);
        tv_answer_2     =   (TextView)    findViewById(R.id.tv_answer_2);

        ib_naechste     =   (ImageButton) findViewById(R.id.imagebutton_naechste);
        ib_favOnly      =   (ImageButton) findViewById(R.id.imagebutton_nur_favoriten);
        ib_share        =   (ImageButton) findViewById(R.id.ib_share_result);
        ib_favorisieren =   (ImageButton) findViewById(R.id.imagebutton_favorit);
        ib_zufaellig    =   (ImageButton) findViewById(R.id.imagebutton_zufaellig);
        ib_youtube      =   (ImageButton) findViewById(R.id.imagebutton_youtube);
        ib_search       =   (ImageButton) findViewById(R.id.imagebutton_search);
        ib_sel_answer_1 =   (ImageButton) findViewById(R.id.ib_select_answer_1);
        ib_sel_answer_2 =   (ImageButton) findViewById(R.id.ib_select_answer_2);

        rl_qu_cont      =   (RelativeLayout) findViewById(R.id.rl_question_container);

        // do little important stuff too here... :S
        displayWidth    =   this.getResources().getDisplayMetrics().widthPixels;

    }
    // register all OnClickListeners
    private void regListeners() {

        // show next question
        ib_naechste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (MODE == MODE_NORMAL) {
                    QuestionManager.selectNext();
                } else {
                    QuestionManager.selectNextFavorite();
                }

                displayQuestion(QuestionManager.getQuestion(), animationOn);

            }
        });

        ib_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SearchQuestionsActivity.class));
                // setting this true causes the onResome to display the current question
                // which might change, as user select an entry from the list
                searchDialogWasOpen = true;
            }
        });

        // toggle favorites-only mode
        ib_favOnly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (QuestionManager.countFavoredQuestions() == 0) {
                    return;
                }

                if (MODE == MODE_NORMAL) {
                    MODE =  MODE_FAV_ONLY;

                    ib_favOnly.setImageResource(R.drawable.selector_bt_all_questions);
                    QuestionManager.selectNextFavorite();
                    displayQuestion(QuestionManager.getQuestion(), animationOn);
                } else {
                    MODE = MODE_NORMAL;
                    ib_favOnly.setImageResource(R.drawable.selector_bt_favorites_only);
                }
            }
        });

        // share current question
        ib_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utilities.shareContent(getApplicationContext(), tv_questionIn.getText().toString(),
                        "text/plain");
            }
        });

        // mark current question as favorite
        ib_favorisieren.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // get fav-state of current question
                boolean favorite = QuestionManager.getQuestion().favorite;

                if (!favorite) {
                    // set current question as favorite
                    QuestionManager.setFavorite(true);
                    // animate color change of view and change image
                    changeViewColor(ib_favorisieren, 200, R.color.icon_color, R.color.nmr_background);
                    ib_favorisieren.setImageResource(R.drawable.ic_favorite_white_24dp);
                } else {
                    QuestionManager.setFavorite(false);
                    changeViewColor(ib_favorisieren, 200, R.color.nmr_background, R.color.icon_color);
                    ib_favorisieren.setImageResource(R.drawable.ic_favorite_border_white_24dp);
                }
                updateFavOnlyButtonState();
            }
        });

        // toggle random mode
        ib_zufaellig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!QuestionManager.isRandom()) {
                    QuestionManager.setRandom(true);
                    changeViewColor(ib_zufaellig, 500, R.color.icon_color, R.color.nmr_background);
                } else {
                    QuestionManager.setRandom(false);
                    changeViewColor(ib_zufaellig, 500, R.color.nmr_background, R.color.icon_color);
                }
            }
        });

        // open youtube video of current question
        ib_youtube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                changeViewColor(ib_youtube, 300, R.color.icon_color, R.color.nmr_background);
                changeViewColor(ib_youtube, 300, R.color.nmr_background, R.color.icon_color);
                startActivity(
                        new Intent(Intent.ACTION_VIEW,
                                Uri.parse(QuestionManager.getQuestion().ytlink)));
            }
        });

        // select answer 1
        ib_sel_answer_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Mainactivity:  ", "answer1");
                ib_sel_answer_1.setImageResource(R.drawable.answer_selected);
                ib_sel_answer_2.setImageResource(R.drawable.answer_unselected);
            }
        });

        // select answer 2
        ib_sel_answer_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Mainactivity:  ", "answer2");
                ib_sel_answer_2.setImageResource(R.drawable.answer_selected);
                ib_sel_answer_1.setImageResource(R.drawable.answer_unselected);
            }
        });

        findViewById(R.id.ll_answer_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ib_sel_answer_1.performClick();
            }
        });

        findViewById(R.id.ll_answer_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ib_sel_answer_2.performClick();
            }
        });


    }


    // enables or disables the favoritesOnly button
    private void updateFavOnlyButtonState() {
        // after the last favorite got "de-favorised" change

        if (QuestionManager.countFavoredQuestions() == 0) {
            ib_favOnly.setImageResource(R.drawable.bt_favorites_only_disabled);
            ib_favOnly.setEnabled(false);
            MODE = MODE_NORMAL;
        } else {

            ib_favOnly.setImageResource(R.drawable.selector_bt_favorites_only);
            ib_favOnly.setEnabled(true);
        }
    }


    // animates color-change of a view. Thanks to Felipe Bari for this method.
    // http://stackoverflow.com/questions/18216285/android-animate-color-change-from-color-to-color
    private void changeViewColor(final ImageButton ib, int duration, int startColor, int endColor) {
        // Load initial and final colors.
        final int initialColor  =   getResources().getColor(startColor);
        final int finalColor    =   getResources().getColor(endColor);

        ValueAnimator anim = ValueAnimator.ofFloat(0, 1);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // Use animation position to blend colors.
                float position = animation.getAnimatedFraction();
                int blended = blendColors(initialColor, finalColor, position);

                // Apply blended color to the view.
                ib.setColorFilter(blended);
            }
        });
        anim.setDuration(duration).start();
    }
    private void changeViewColor(final TextView tv, int duration, int startColor, int endColor) {
        // Load initial and final colors.
        final int initialColor  =   getResources().getColor(startColor);
        final int finalColor    =   getResources().getColor(endColor);

        ValueAnimator anim = ValueAnimator.ofFloat(0, 1);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // Use animation position to blend colors.
                float position = animation.getAnimatedFraction();
                int blended = blendColors(initialColor, finalColor, position);

                // Apply blended color to the view.
                tv.setTextColor(blended);
            }
        });
        anim.setDuration(duration).start();
    }
    private int  blendColors(int from, int to, float ratio) {
        final float inverseRatio = 1f - ratio;

        final float r = Color.red(to) * ratio + Color.red(from) * inverseRatio;
        final float g = Color.green(to) * ratio + Color.green(from) * inverseRatio;
        final float b = Color.blue(to) * ratio + Color.blue(from) * inverseRatio;

        return Color.rgb((int) r, (int) g, (int) b);
    }

}
