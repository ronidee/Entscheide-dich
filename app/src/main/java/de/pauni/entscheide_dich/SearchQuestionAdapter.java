package de.pauni.entscheide_dich;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * This adapter fills up the list from the SearchQuestionActivity with all search-results
 * and provides the share-button for each result (list-item)
 */

class SearchQuestionAdapter extends BaseAdapter {

    private Context context;
    static Question[] questions;

    private static LayoutInflater inflater = null;

    SearchQuestionAdapter (Context context, Question[] q) {
        this.context = context;
        questions = q;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return questions.length;
    }

    @Override
    public Object getItem(int position) {
        return questions[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        View vi = convertView;

        if (vi == null)
            vi = inflater.inflate(R.layout.question_search_result_template, parent, false);

        final TextView tv_question = (TextView)    vi.findViewById(R.id.tv_question);
        TextView tv_guest       = (TextView)    vi.findViewById(R.id.tv_guest);
        ImageButton ib_share    = (ImageButton) vi.findViewById(R.id.ib_share_result);

        tv_question.setText (questions[position].question);
        tv_guest.setText    (questions[position].guest);
        ib_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Utilities.shareContent(context, questions[position].question +
                        " - aus der Sendung mit " + questions[position].guest, "text/plain");
            }
        });
        return  vi;
    }
}