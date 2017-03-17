package de.pauni.entscheide_dich;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;



class ClipboardHistoryAdapter extends BaseAdapter {

    private Context context;
    private Question[] questions;

    private static LayoutInflater inflater = null;

    ClipboardHistoryAdapter(Context context, Question[] questions) {
        this.context = context;
        this.questions = questions;

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

    public void removeItem(int position) {
        //converting array to list, to remove certain item, and converting it back
        ArrayList<Question> list = new ArrayList<>(Arrays.asList(questions));
        list.remove(position);
        questions = list.toArray(new Question[0]);
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

    private void setCardviewColor(CardView c, int color) {
        c.setBackgroundColor(context.getResources().getColor(color));
    }

}