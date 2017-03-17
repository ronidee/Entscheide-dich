package de.pauni.entscheide_dich;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;




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
            vi = inflater.inflate(R.layout.question_template, parent, false);

        TextView tv_question    = (TextView) vi.findViewById(R.id.tv_question);
        TextView tv_guest       = (TextView) vi.findViewById(R.id.tv_guest);


        tv_question.setText (questions[position].question);
        tv_guest.setText    (questions[position].guest);

        return  vi;
    }
}