package de.pauni.entscheide_dich;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;


/**
 * Created by Roni on 07.11.2016.
 * adds clipcontent and timestamp to arrays
 * display them via customadapter
 */

public class SearchQuestionsActivity extends Activity{
    QuestionManager qm;
    ListView lv_questions;
    EditText et_search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_questions);

        qm = new QuestionManager(getApplicationContext());
        //buffering displays screen height and width
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        float h = displayMetrics.heightPixels;
        float w = displayMetrics.widthPixels;

        //setting dialogs attributes
        getWindow().setLayout(Math.round(w), Math.round(h*0.525F));
        WindowManager.LayoutParams wlp = getWindow().getAttributes();
        wlp.gravity = Gravity.TOP;
        getWindow().setAttributes(wlp);



        lv_questions = (ListView) findViewById(R.id.lv_result);
        et_search   = (EditText) findViewById(R.id.et_search);

        TextWatcher textWatcher = new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {  }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {   }

            @Override
            public void afterTextChanged(Editable s) {
                updateQuestionList(s.toString());
            }
        };
        et_search.addTextChangedListener(textWatcher);

    }


    void updateQuestionList(String keyword) {
        // #code-pr0n
        lv_questions.setAdapter(new ClipboardHistoryAdapter(this, qm.searchQuestion(keyword)));
    }




}
