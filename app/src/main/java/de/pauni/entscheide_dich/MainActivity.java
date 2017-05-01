package de.pauni.entscheide_dich;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;


/*
*
*/

public class MainActivity extends Activity {
    float displayWidth;

    static  boolean appInBackground = false;
    private boolean searchDialogWasOpen = false;
    private boolean sessionStart = true;
    private boolean animationOn  = true;
    private boolean blocked = false;
    private boolean favoritesonly = false;

    static private Handler handler = new Handler();



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

    View        statistic_bar_1 =   null;
    View        statistic_bar_2 =   null;

    RelativeLayout rl_qu_cont   =   null;
    LinearLayout ll_answering_statistic = null;
    int h; //height of one line of tv_questionX

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        appInBackground = false;
        Log.d("MainActivity", "created");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new SharedPrefs(this); // passing context, for static access

        if (SharedPrefs.isFirstStart()) {
            new DatabaseInitializer(this);
        }

        new QuestionManager(this);

        QuestionManager.selectQuestionById(SharedPrefs.getCurrentQuestionId());
        Utilities.scale = getResources().getDisplayMetrics().density;

        initViews();
        regListeners();

        updateFavOnlyButtonState(QuestionManager.getQuestion().favorite);

    }

    //Saving the current Id in 'any' possible cases.
    @Override
    protected void onPause() {
        appInBackground = true;
        super.onPause();
        Log.d("MainActivity", "paused");
        SharedPrefs.saveQuestionId(QuestionManager.getId());
    }
    @Override
    protected void onStop() {
        appInBackground = true;
        Log.d("MainActivity", "stopped");
        super.onStop();
        SharedPrefs.saveQuestionId(QuestionManager.getId());
    }
    @Override
    protected void onDestroy() {
        appInBackground = true;
        Log.d("MainActivity", "destroyed");
        super.onDestroy();
        SharedPrefs.saveQuestionId(QuestionManager.getId());
    }
    @Override
    protected void onResume() {
        appInBackground = false;
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


    // don't make it private, as SearchQuestionsActivity would lose access
    void displayQuestion (Question question, boolean animated) {
        /**
         * Steps of this method:
         * 1) Make certain words of the question clickable
         * 2) Update the favour-icon
         * 3) Update the question
         * 4) Update the guest
         * 5) Update the answers
         * 6) Prepare the statistic
         */

        /**STEP 1)*/
        // transforming the questions.clickables and the .question-string into one SpannableString
        // with clickable words, which will bring the user to a web-search about this word
        SpannableString questionText = Utilities.getClickableText(this, question.question, question.clickables);
        String  guest = "Sendung mit "+question.guest;

        /**STEP 2)*/
        if (question.favorite) {
            ib_favorisieren.setColorFilter(getResources().getColor(R.color.nmr_background));
            ib_favorisieren.setImageResource(R.drawable.ic_favorite_white_24dp);
        } else {
            ib_favorisieren.setColorFilter(getResources().getColor(R.color.icon_color));
            ib_favorisieren.setImageResource(R.drawable.ic_favorite_border_white_24dp);
        }

        /**STEP 3 & 4)*/
        if (animated) {
            slideQuestionOutLeft();
            slideQuestionInRight(questionText);
            setGuestAnimated(guest);
        } else {
            tv_questionIn.setText(questionText);
            tv_guest.setText(guest);
        }
        tv_questionIn.setMovementMethod(new MovementMethod(this));

        /**STEP 5)*/
        tv_answer_1.setText(question.answer_1);
        tv_answer_2.setText(question.answer_2);

        /**STEP 6)*/
        prepareStatistic(question.count_answer_1, question.count_answer_2);
    }
    void displayPrevQuestion (Question question, boolean animated) {
       SpannableString questionText = Utilities.getClickableText(this, question.question, question.clickables);
        String  guest = "Sendung mit "+question.guest;
        if (question.favorite) {
            ib_favorisieren.setColorFilter(getResources().getColor(R.color.nmr_background));
            ib_favorisieren.setImageResource(R.drawable.ic_favorite_white_24dp);
        } else {
            ib_favorisieren.setColorFilter(getResources().getColor(R.color.icon_color));
            ib_favorisieren.setImageResource(R.drawable.ic_favorite_border_white_24dp);
        }
        if (animated) {
            slideQuestionOutRight();
            slideQuestionInLeft(questionText);
            setGuestAnimated(guest);
        } else {
            tv_questionIn.setText(questionText);
            tv_guest.setText(guest);
        }
        tv_questionIn.setMovementMethod(new MovementMethod(this));
        tv_answer_1.setText(question.answer_1);
        tv_answer_2.setText(question.answer_2);
        prepareStatistic(question.count_answer_1, question.count_answer_2);
    }

    // generates a sliding-out animation for the old question
    private void slideQuestionOutLeft() {
        // preparing textview for animation
        tv_questionOut.setX(tv_questionIn.getX());
        tv_questionOut.setText(tv_questionIn.getText());
        tv_questionOut.setVisibility(View.VISIBLE);
        // slide textview out of the window
        tv_questionOut.animate().translationX(-displayWidth);
    }
    private void slideQuestionOutRight() {
        // preparing textview for animation
        tv_questionOut.setX(tv_questionIn.getX());
        tv_questionOut.setText(tv_questionIn.getText());
        tv_questionOut.setVisibility(View.VISIBLE);
        // slide textview out of the window
        tv_questionOut.animate().translationX(+displayWidth);
    }
    // generates a sliding-in animation for the new question
    private void slideQuestionInRight(SpannableString question) {
        tv_questionIn.setX(displayWidth);
        tv_questionIn.setText(question);

        // slide textview to it's original position (0=origin)
        tv_questionIn.animate().translationX(0);
        //rl_qu_cont.getLayoutParams().height = tv_questionIn.getLineCount() * h;
        animateSizeChange(rl_qu_cont.getHeight(), tv_questionIn.getLineCount() * h);
    }
    private void slideQuestionInLeft(SpannableString question) {
        tv_questionIn.setX(-displayWidth);
        tv_questionIn.setText(question);

        // slide textview to it's original position (0=origin)
        tv_questionIn.animate().translationX(0);
        //rl_qu_cont.getLayoutParams().height = tv_questionIn.getLineCount() * h;
        animateSizeChange(rl_qu_cont.getHeight(), tv_questionIn.getLineCount() * h);
    }
    // generates a fade in/out animation for the new guest
    private void setGuestAnimated(String guest) {
        // if the guest didn't change, do nothing
        if (tv_guest.getText().toString().equals(guest)) {
            return;
        }

        // fade out the textview (same color as background)
        changeViewColor(tv_guest, 550, R.color.icon_color, R.color.cardview_background);
        // update the guest
        tv_guest.setText(guest);
        // fade in the textview
        changeViewColor(tv_guest, 550, R.color.cardview_background, R.color.icon_color);
    }

    // generates a size-change animation
    private void animateSizeChange(int fromY, int toY) {
        ResizeAnimation resizeAnimation = new ResizeAnimation(rl_qu_cont, toY, fromY);
        resizeAnimation.setDuration(200);
        rl_qu_cont.startAnimation(resizeAnimation);
    }

    private void prepareStatistic(int count1, int count2) {
        // remove the previous selection and diagram
        ll_answering_statistic.setVisibility(View.INVISIBLE);
        ib_sel_answer_1.setImageResource(R.drawable.answer_unselected);
        ib_sel_answer_2.setImageResource(R.drawable.answer_unselected);

        // calculate the percentage
        int percent1 = 50;
        int percent2 = 50;

        if (count1+count2 != 0) {
            percent1 = (100*count1)/(count1+count2);
            percent2 = 100-percent1;
        }



        // layout_weight are set to the percent of each answer, the weightSum is 100
        statistic_bar_1.setLayoutParams(new TableRow.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                percent1));

        statistic_bar_2.setLayoutParams(new TableRow.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                percent2));

        ll_answering_statistic.getLayoutParams().height = (int) Utilities.convertDpsToPixels(7);
    }
    // shows the statistics and set the respective colours
    private void showStatistic(int answer) {
        // the bar corresponding to the users choice will get nmr_background color
        int selectedColor = getResources().getColor(R.color.selected_answer);
        int unselectedColor = getResources().getColor(R.color.semitransparent);

        statistic_bar_1.setBackgroundColor(unselectedColor);
        statistic_bar_2.setBackgroundColor(unselectedColor);

        // the bar from the answer the user selected, Also will be cyan, the other remains grey
        switch (answer) {
            case 1:
                statistic_bar_1.setBackgroundColor(selectedColor);
                break;
            case 2:
                statistic_bar_2.setBackgroundColor(selectedColor);
                break;
        }

        // make the layout visible
        ll_answering_statistic.setVisibility(View.VISIBLE);
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

        statistic_bar_1 =   findViewById(R.id.statistic_bar_1);
        statistic_bar_2 =   findViewById(R.id.statistic_bar_2);

        rl_qu_cont      =   (RelativeLayout) findViewById(R.id.rl_question_container);
        ll_answering_statistic = (LinearLayout) findViewById(R.id.ll_answering_statistic);


        // do little important stuff too here... :S
        displayWidth    =   this.getResources().getDisplayMetrics().widthPixels;
        // make the textview clickable and set link_color
        tv_questionIn.setHighlightColor(getResources().getColor(R.color.link_highlight_color));

    }
    // register all OnClickListeners
    private void regListeners() {

        // show next question
        ib_naechste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (blocked)
                    return;

                // block the button for 0.2 seconds
                blocked = true;
                (new Handler()).postDelayed(unblockButton, 150);


                if (favoritesonly) {
                    QuestionManager.selectNextFavorite();
                } else {
                    QuestionManager.selectNext();
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

                if (!favoritesonly) {
                    favoritesonly = true;
                    ib_favOnly.setImageResource(R.drawable.selector_bt_all_questions);

                    if (!(QuestionManager.getQuestion().favorite)) {
                        QuestionManager.selectNextFavorite();
                        displayQuestion(QuestionManager.getQuestion(), animationOn);
                    }

                } else {
                    favoritesonly = false;
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
                    if (favoritesonly && QuestionManager.countFavoredQuestions()>0) {
                        Log.d("ib_favorisieren", "skipped to nxt fav");
                        QuestionManager.selectNextFavorite();
                        displayQuestion(QuestionManager.getQuestion(), true);
                    } else {
                        changeViewColor(ib_favorisieren, 200, R.color.nmr_background, R.color.icon_color);
                        ib_favorisieren.setImageResource(R.drawable.ic_favorite_border_white_24dp);
                    }
                }

                // invert, because after non-fav Q get clicked, Q becomes fav
                updateFavOnlyButtonState(!favorite);
            }
        });

        // toggle random mode
        /*ib_zufaellig.setOnClickListener(new View.OnClickListener() {
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
        });*/

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
                Log.d("Mainactivity:  ", "select answer1");
                showStatistic(1);
                // add the vote to the "global" statistics
                DownloadManager.plusOne(QuestionManager.getId(), 1);
                ib_sel_answer_1.setImageResource(R.drawable.answer_selected);
                ib_sel_answer_2.setImageResource(R.drawable.answer_unselected);
            }
        });

        // select answer 2
        ib_sel_answer_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Mainactivity:  ", "select answer2");
                showStatistic(2);
                // add the vote to the "global" statistics
                DownloadManager.plusOne(QuestionManager.getId(), 2);
                ib_sel_answer_2.setImageResource(R.drawable.answer_selected);
                ib_sel_answer_1.setImageResource(R.drawable.answer_unselected);
            }
        });

        // referring to their respective selectore_button
        findViewById(R.id.ll_answer_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ib_sel_answer_1.callOnClick();
            }
        });

        findViewById(R.id.ll_answer_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ib_sel_answer_2.callOnClick();
            }
        });


    }

    Runnable unblockButton = new Runnable() {
        @Override
        public void run() {
            blocked = false;
        }
    };




    // enables or disables the favoritesOnly button
    private void updateFavOnlyButtonState(boolean questionIsFavorite) {

        // if user liked current Q, enable the favOnly button; redundant, but ok...
        if (questionIsFavorite) {
            Log.d("updateFavButton", "liked");
            ib_favOnly.setImageResource(R.drawable.selector_bt_favorites_only);
            ib_favOnly.setEnabled(true);

            // if user de-liked current Q, do nothing, except...
        } else if (QuestionManager.countFavoredQuestions() > 0) {
            Log.d("updateFavButton", "de-liked");


            // ...if user de-liked the last favorite, disable favOnly button
        } else {
            Log.d("updateFavButton", "de-liked last fav");
            ib_favOnly.setImageResource(R.drawable.bt_favorites_only_disabled);
            ib_favOnly.setEnabled(false);
            favoritesonly = false;
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

    static Handler getHandler() {
        return handler;
    }

}
