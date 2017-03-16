package de.pauni.entscheide_dich;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

/*
*   Die MainActivity verarbeitet die Eingaben des Nutzers und die Ausgabe der Frage
*   über die UI und ist die Schnittstelle zwischen UI und dem questionManager. Über den
*   questionManager holt sie sich die entspr. Einträge oder markiert Fragen als Favoriten.
*
*/
public class MainActivity extends AppCompatActivity {
    QuestionManager questionManager;
    Handler handler;

    boolean zufaellig           =   false;
    //Declaring the views
    TextView    tv_Fragen       =   null;
    TextView    tv_Sendung      =   null;

    ImageButton ib_naechste     =   null;
    ImageButton ib_vorige       =   null;
    ImageButton ib_favOnly      =   null;
    ImageButton ib_share        =   null;
    ImageButton ib_favorit      =   null;
    ImageButton ib_zufaellig    =   null;
    ImageButton ib_youtube      =   null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new SharedPrefs(this);


        handler         =   new Handler();
        questionManager =   new QuestionManager(this);

        initViews();
        regListeners();
        //frageAnzeigen(questionManager.getQuestion());
    }

    private void frageAnzeigen(Question question) {
        String  text    = question.question;
        String  guest   = question.guest;
        boolean favorit = question.favorite;

        tv_Fragen.setText(text);
        tv_Sendung.setText("Sendung mit "+guest);
        if (favorit) {
            ib_favorit.setColorFilter(getResources().getColor(R.color.nmr_background));
            ib_favorit.setImageResource(R.drawable.ic_favorite_white_24dp);
        } else {
            ib_favorit.setColorFilter(getResources().getColor(R.color.icon_color));
            ib_favorit.setImageResource(R.drawable.ic_favorite_border_white_24dp);
        }
    }


    private void initViews() {
        tv_Fragen       =   (TextView)    findViewById(R.id.textview_Fragen);
        tv_Sendung      =   (TextView)    findViewById(R.id.textview_sendung);

        ib_naechste     =   (ImageButton) findViewById(R.id.imagebutton_naechste);
        ib_favOnly      =   (ImageButton) findViewById(R.id.imagebutton_nur_favoriten);
        ib_share        =   (ImageButton) findViewById(R.id.imagebutton_share);
        ib_vorige       =   (ImageButton) findViewById(R.id.imagebutton_zurueck);
        ib_favorit      =   (ImageButton) findViewById(R.id.imagebutton_favorit);
        ib_zufaellig    =   (ImageButton) findViewById(R.id.imagebutton_zufaellig);
        ib_youtube      =   (ImageButton) findViewById(R.id.imagebutton_youtube);

        tv_Fragen.setMovementMethod(new ScrollingMovementMethod());

    }
    private void regListeners() {
        ib_naechste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (zufaellig) {
                    questionManager.selectRandom();
                } else {
                    questionManager.selectNext();
                }

                frageAnzeigen(questionManager.getQuestion());
            }
        });

        ib_vorige.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                questionManager.selectPrevious();
                frageAnzeigen(questionManager.getQuestion());

            }
        });

        ib_favOnly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!questionManager.favoritesOnly) {
                    questionManager.favoritesOnly = true;
                    ib_favOnly.setImageResource(R.drawable.bt_all_questions);
                } else {
                    questionManager.favoritesOnly = false;
                    ib_favOnly.setImageResource(R.drawable.bt_favorites_only);
                }
            }
        });

        ib_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String frage = tv_Fragen.getText().toString() + "\n\nEntscheide Dich! ist echt eine super App!";
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, frage);
                startActivity(Intent.createChooser(i, "Teile die Frage mit Feinden:"));
            }
        });

        ib_favorit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //getCurrentQuestion returns String[] = {"text", "sendung", "1" or "0"}
                boolean favorite = questionManager.getQuestion().favorite;

                if (!favorite) {
                    questionManager.setFavorite(true);
                    changeViewColor(ib_favorit, 200, R.color.icon_color, R.color.nmr_background);
                    ib_favorit.setImageResource(R.drawable.ic_favorite_white_24dp);
                } else {
                    questionManager.setFavorite(false);
                    changeViewColor(ib_favorit, 200, R.color.nmr_background, R.color.icon_color);
                    ib_favorit.setImageResource(R.drawable.ic_favorite_border_white_24dp);
                }
            }
        });

        ib_zufaellig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!zufaellig) {
                    zufaellig = true;
                    changeViewColor(ib_zufaellig, 500, R.color.icon_color, R.color.nmr_background);
                } else {
                    zufaellig = false;
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
    private int blendColors(int from, int to, float ratio) {
        final float inverseRatio = 1f - ratio;

        final float r = Color.red(to) * ratio + Color.red(from) * inverseRatio;
        final float g = Color.green(to) * ratio + Color.green(from) * inverseRatio;
        final float b = Color.blue(to) * ratio + Color.blue(from) * inverseRatio;

        return Color.rgb((int) r, (int) g, (int) b);
    }


}
