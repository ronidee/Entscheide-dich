package de.pauni.entscheide_dich;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

/*
*
*/
public class MainActivity extends Activity {
    float displayWidth;

    Handler handler = new Handler();
    static QuestionManager questionManager;

    private final int MODE_NORMAL   = 1;
    private final int MODE_FAV_ONLY = 2;
    private boolean   animationOn   = true;
    private int MODE = MODE_NORMAL;

    //Declaring the views
    TextView    tv_questionIn   =   null;
    TextView    tv_questionOut  =   null;
    TextView    tv_guest      =   null;
    ImageButton ib_naechste     =   null;
    ImageButton ib_favOnly      =   null;
    ImageButton ib_share        =   null;
    ImageButton ib_favorisieren =   null;
    ImageButton ib_zufaellig    =   null;
    ImageButton ib_youtube      =   null;
    ImageButton ib_search       =   null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("MainActivity", "created");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //passing a context to ShardedPrafs for static access
        new SharedPrefs(this);



        //loading the current question-ID
        questionManager = new QuestionManager(this);
        questionManager.setId(SharedPrefs.getCurrentQuestionId());

        initViews();
        regListeners();

        updateFavOnlyButtonState();
        frageAnzeigen(questionManager.getQuestion(), false);
    }

    //Saving the current Id in 'any' possible cases.
    @Override
    protected void onPause() {
        super.onPause();
        Log.d("MainActivity", "paused");
        SharedPrefs.saveQuestionId(questionManager.getId());
    }

    @Override
    protected void onStop() {
        Log.d("MainActivity", "stopped");
        super.onStop();
        SharedPrefs.saveQuestionId(questionManager.getId());
    }

    @Override
    protected void onDestroy() {
        Log.d("MainActivity", "destroyed");
        super.onDestroy();
        SharedPrefs.saveQuestionId(questionManager.getId());
    }

    @Override
    protected void onResume() {
        frageAnzeigen(questionManager.getQuestion(), false);
        Log.d("MainActivity", "resumed");
        super.onResume();
    }



    void frageAnzeigen(Question question, boolean animated) {

        String  text    = question.question;
        String  guest   = "Sendung mit " + question.guest;
        boolean favorit = question.favorite;

        if (favorit) {
            ib_favorisieren.setColorFilter(getResources().getColor(R.color.nmr_background));
            ib_favorisieren.setImageResource(R.drawable.ic_favorite_white_24dp);
        } else {
            ib_favorisieren.setColorFilter(getResources().getColor(R.color.icon_color));
            ib_favorisieren.setImageResource(R.drawable.ic_favorite_border_white_24dp);
        }

        if (animated) {
            slideQuestionOut();
            changeViewColor(tv_guest, 400, R.color.icon_color, R.color.white);
            changeViewColor(tv_guest, 500, R.color.white, R.color.icon_color);
            slideQuestionIn(text);
        } else {
            tv_questionIn.setText(text);
            tv_guest.setText(guest);
        }
    }



    private void initViews() {
        tv_questionIn   =   (TextView)    findViewById(R.id.textview_question_in);
        tv_questionOut  =   (TextView)    findViewById(R.id.textview_question_out);
        tv_guest      =   (TextView)    findViewById(R.id.textview_sendung);
        ib_naechste     =   (ImageButton) findViewById(R.id.imagebutton_naechste);
        ib_favOnly      =   (ImageButton) findViewById(R.id.imagebutton_nur_favoriten);
        ib_share        =   (ImageButton) findViewById(R.id.imagebutton_share);
        ib_favorisieren =   (ImageButton) findViewById(R.id.imagebutton_favorit);
        ib_zufaellig    =   (ImageButton) findViewById(R.id.imagebutton_zufaellig);
        ib_youtube      =   (ImageButton) findViewById(R.id.imagebutton_youtube);
        ib_search       =   (ImageButton) findViewById(R.id.imagebutton_search);


        // do little important stuff too here... :S
        displayWidth    =   this.getResources().getDisplayMetrics().widthPixels;

        tv_questionIn.setMovementMethod(new ScrollingMovementMethod());

    }
    private void regListeners() {
        ib_naechste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (MODE == MODE_NORMAL) {
                    questionManager.selectNext();
                } else {
                    questionManager.selectNextFavorite();
                }

                frageAnzeigen(questionManager.getQuestion(), animationOn);
            }
        });

        /*ib_vorige.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                questionManager.selectPrevious();
                frageAnzeigen(questionManager.getQuestion());

            }
        }); */

        ib_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),
                        SearchQuestionsActivity.class));
            }
        });

        ib_favOnly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (questionManager.countFavoredQuestions() == 0) {
                    return;
                }

                if (MODE == MODE_NORMAL) {
                    MODE = MODE_FAV_ONLY;

                    ib_favOnly.setImageResource(R.drawable.selector_bt_all_questions);
                    questionManager.selectNextFavorite();
                    frageAnzeigen(questionManager.getQuestion(), animationOn);
                } else {
                    MODE = MODE_NORMAL;
                    ib_favOnly.setImageResource(R.drawable.selector_bt_favorites_only);
                }
            }
        });

        ib_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String frage = tv_questionIn.getText().toString() + "\n\nEntscheide Dich! ist echt eine super App!";
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, frage);
                startActivity(Intent.createChooser(i, "Teile die Frage mit Feinden:"));
            }
        });

        ib_favorisieren.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // get fav-state of current question
                boolean favorite = questionManager.getQuestion().favorite;

                if (!favorite) {
                    // set current question as favorite
                    questionManager.setFavorite(true);
                    // animate color change of view and change image
                    changeViewColor(ib_favorisieren, 200, R.color.icon_color, R.color.nmr_background);
                    ib_favorisieren.setImageResource(R.drawable.ic_favorite_white_24dp);
                } else {
                    questionManager.setFavorite(false);
                    changeViewColor(ib_favorisieren, 200, R.color.nmr_background, R.color.icon_color);
                    ib_favorisieren.setImageResource(R.drawable.ic_favorite_border_white_24dp);
                }
                updateFavOnlyButtonState();
            }
        });

        ib_zufaellig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!questionManager.isRandom()) {
                    questionManager.setRandom(true);
                    changeViewColor(ib_zufaellig, 500, R.color.icon_color, R.color.nmr_background);
                } else {
                    questionManager.setRandom(false);
                    changeViewColor(ib_zufaellig, 500, R.color.nmr_background, R.color.icon_color);
                }
            }
        });

        ib_youtube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                changeViewColor(ib_youtube, 300, R.color.icon_color, R.color.nmr_background);
                changeViewColor(ib_youtube, 300, R.color.nmr_background, R.color.icon_color);
                startActivity(
                        new Intent(Intent.ACTION_VIEW,
                                Uri.parse(questionManager.getQuestion().ytlink)));
            }
        });
    }

    private void updateFavOnlyButtonState() {
        // after the last favorite got "de-favorised" change

        if (questionManager.countFavoredQuestions() == 0) {
            ib_favOnly.setImageResource(R.drawable.bt_favorites_only_disabled);
            ib_favOnly.setEnabled(false);
            MODE = MODE_NORMAL;
        } else {

            ib_favOnly.setImageResource(R.drawable.selector_bt_favorites_only);
            ib_favOnly.setEnabled(true);
        }
    }

    /**
     * ANIMATIONS
     */

    private void slideQuestionOut() {
        // preparing textview for animation
        tv_questionOut.setX(tv_questionIn.getX());
        tv_questionOut.setText(tv_questionIn.getText());
        tv_questionOut.setVisibility(View.VISIBLE);

        tv_questionOut.animate().translationX(-displayWidth);
    }

    private void slideQuestionIn(String question) {
        // preparing textview for animation
        tv_questionIn.setX(displayWidth);
        tv_questionIn.setText(question);
        tv_questionIn.animate().translationX(0);
    }

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
    private int blendColors(int from, int to, float ratio) {
        final float inverseRatio = 1f - ratio;

        final float r = Color.red(to) * ratio + Color.red(from) * inverseRatio;
        final float g = Color.green(to) * ratio + Color.green(from) * inverseRatio;
        final float b = Color.blue(to) * ratio + Color.blue(from) * inverseRatio;

        return Color.rgb((int) r, (int) g, (int) b);
    }


}
