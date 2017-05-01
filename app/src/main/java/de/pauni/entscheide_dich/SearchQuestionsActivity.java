package de.pauni.entscheide_dich;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;


/**
 * Created by Roni on 07.11.2016.
 * adds clipcontent and timestamp to arrays
 * display them via customadapter
 */

public class SearchQuestionsActivity extends Activity{
    ListView    lv_questions;
    EditText    et_search;
    TextView    tv_resultcount;
    Question[]  questions;
    SearchQuestionAdapter adapter;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_questions);
        adjustWindowLayout();

        lv_questions    = (ListView) findViewById(R.id.lv_result);
        tv_resultcount  = (TextView) findViewById(R.id.tv_resultcount);
        et_search       = (EditText) findViewById(R.id.et_search);
        et_search.requestFocus();
        tv_resultcount.setText("Ergebnisse: 0");

        questions   = new Question[0];
        adapter     = new SearchQuestionAdapter(this, questions);

        lv_questions.setAdapter(adapter);
        lv_questions.setOnItemClickListener(clickListener);

        et_search.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {  }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {  }

            @Override
            public void afterTextChanged(Editable s) {
                // only search words with at least 3 characters
                if (s.length()>=3) {
                    SearchQuestionAdapter.questions =
                            QuestionManager.searchQuestion(s.toString());
                    adapter.notifyDataSetChanged();
                    tv_resultcount.setText("Ergebnisse: " + lv_questions.getAdapter().getCount());
                    return;
                }
            }
        });
    }


    AdapterView.OnItemClickListener clickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.d("SearchQActiv: ", "onitemclick");
            // selectQuestionById to the id of the question, the user selected
            QuestionManager.selectQuestionById(SearchQuestionAdapter.questions[position].id);

            // close the dialog
            finish();
        }
    };


    void adjustWindowLayout() {
        //buffering displays screen height and width
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        float h = displayMetrics.heightPixels;
        float w = displayMetrics.widthPixels;

        //setting dialogs attributes
        getWindow().setLayout(Math.round(w), Math.round(h*0.7F));
        WindowManager.LayoutParams wlp = getWindow().getAttributes();
        wlp.gravity = Gravity.TOP;
        getWindow().setAttributes(wlp);
    }
}
