package de.pauni.entscheide_dich;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;

/**
 * This activity loads at start. Shows a welcome message and initializes all needed classes
 * Once done all this, switches to MainActivity
 */

public class WelcomeActivity extends Activity {

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("Welcome", "Destoyed");
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);


        new SharedPrefs(this);


        final Context context = this;
        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                if (SharedPrefs.isFirstStart()) {
                    new DatabaseInitializer(context);
                }

                //((WelcomeActivity) context).finish();
                startActivity(new Intent(context, MainActivity.class));
                overridePendingTransition(R.anim.no_animation, R.anim.no_animation);
            }
        };

        new Thread(runnable).start();

    }
}
