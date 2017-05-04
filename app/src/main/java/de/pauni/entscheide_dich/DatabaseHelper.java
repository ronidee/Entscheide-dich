package de.pauni.entscheide_dich;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

/**
 *       This DatabaseHelper class provides access to the database and
 *       takes care of all the required operations
 *
 *
 *       THANK YOU VERY MUCH >! ANDROIDHIVE.INFO !<
 *       FOR PROVIDING A MODEL OF A DB-HELPER CLASSS
 *       http://www.androidhive.info/2011/11/android-sqlite-database-tutorial/
 */

class DatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "entscheideDich";

    // Table names
    private static final String TABLE_FRAGEN_LISTE = "TABLE_FRAGEN_LISTE";
    private static final String TABLE_VOTE_BUFFER = "VOTE_BUFFFER";



    private static final String KEY_ID             = "_id";            // Table Columns names
    private static final String KEY_QUES           = "question";       // Der Text der Frage
    private static final String KEY_GUEST          = "guest_name";     // Der Name des Gastes
    private static final String KEY_HASHTAG        = "hashtag";        // Der Hashtag der Sendung
    private static final String KEY_YT             = "youtube_link";   // Der YT-Link zum jew. Video
    private static final String KEY_FAV            = "favorite";       // Favorite ja oder nein (1/0)
    private static final String KEY_KEYWORDS       = "keywords";       // String der zu clickable sein soll
    private static final String KEY_LINKS          = "links";          // Link der aufgerufen wird
    private static final String KEY_ANSWER_1       = "answer1";        // Antwortmöglichkeit 1
    private static final String KEY_ANSWER_2       = "answer2";        // Antwortmöglichkeit 2
    private static final String KEY_LOCALVOTE      = "localvote";      // Antwort des Users
    private static final String KEY_COUNT_ANSWER_1 = "answer_1_count"; // Anzahl der Votes für Antwort 1
    private static final String KEY_COUNT_ANSWER_2 = "answer_2_count"; // Anzahl der Votes für Antwort 2


    private static final String KEY_QUES_ID = "question_id"; // Der Text der Frage
    private static final String KEY_VOTE    = "vote";        // Der Name des Gastes


    DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.d("DatabaseHelper>>>", "konstrukter geladen");
    }


    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_QUESTION_TABLE_STRING =
                "CREATE TABLE " + TABLE_FRAGEN_LISTE +
                    "(" +
                        KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        KEY_QUES + " TEXT NOT NULL," +
                        KEY_GUEST + " TEXT NOT NULL," +
                        KEY_HASHTAG + " TEXT NOT NULL," +
                        KEY_YT + " TEXT NOT NULL," +
                        KEY_FAV + " INTEGER NOT NULL," +
                        KEY_KEYWORDS + " TEXT NOT NULL," +
                        KEY_LINKS + " TEXT NOT NULL," +
                        KEY_ANSWER_1 + " TEXT NOT NULL," +
                        KEY_ANSWER_2 + " TEXT NOT NULL," +
                        KEY_LOCALVOTE + " TEXT NOT NULL," +
                        KEY_COUNT_ANSWER_1 + " TEXT NOT NULL," +
                        KEY_COUNT_ANSWER_2 + " TEXT NOT NULL" +
                    ")";

        String CREATE_BUFFER_TABLE_STRING =
                "CREATE TABLE " + TABLE_VOTE_BUFFER +
                    "(" +
                        KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        KEY_QUES_ID + " TEXT NOT NULL," +
                        KEY_VOTE + " TEXT NOT NULL" +
                    ")";


        db.execSQL(CREATE_QUESTION_TABLE_STRING);
        db.execSQL(CREATE_BUFFER_TABLE_STRING);
        Log.d("DatabaseHelper>>>", "    DB created");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older tables if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FRAGEN_LISTE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VOTE_BUFFER);

        onCreate(db);
    }




    void addVoteToBuffer(int ques_id, int vote) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_QUES_ID, ques_id);
        values.put(KEY_VOTE, vote);

        // Inserting Row
        db.insert(TABLE_VOTE_BUFFER, null, values);
        //db.close();
    }
    int[] getVote(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_VOTE_BUFFER + " WHERE " + KEY_ID + " = \'" + id + "\' " , null);

        if(cursor != null) {
            cursor.moveToFirst();
        }

        int qId     = cursor.getInt(1);
        int ansNum  = cursor.getInt(2);

        cursor.close();
        return new int[] { qId, ansNum };
    }
    int getVoteBufferCount() {
        String countQuery = "SELECT  * FROM " + TABLE_VOTE_BUFFER;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();

        // return count
        return count;
    }
    void clearVotingBuffer() {
        this.getReadableDatabase().delete(TABLE_VOTE_BUFFER, null, null);
    }



    // add single question to db
    void addQuestion(Question question) {
        SQLiteDatabase db = this.getWritableDatabase();

        List<String> word = new ArrayList<>();
        List<String> link = new ArrayList<>();

        for (int i = 0; i < question.clickables.length; i++) {
            word.add(question.clickables[i][0]);
            link.add(question.clickables[i][1]);
        }

        String strings = TextUtils.join(",", word.toArray());
        String links   = TextUtils.join(",", link.toArray());

        ContentValues values = new ContentValues();
        values.put(KEY_QUES, question.question);
        values.put(KEY_GUEST, question.guest);
        values.put(KEY_YT, question.ytlink);
        values.put(KEY_HASHTAG, question.hashtag);
        values.put(KEY_FAV, question.favorite); // isn't his redundant? All favs are false...
        values.put(KEY_KEYWORDS, strings);
        values.put(KEY_LINKS, links);
        values.put(KEY_ANSWER_1, question.answer_1);
        values.put(KEY_ANSWER_2, question.answer_2);
        values.put(KEY_LOCALVOTE, question.localvote);
        values.put(KEY_COUNT_ANSWER_1, question.answer_1_count);
        values.put(KEY_COUNT_ANSWER_2, question.answer_2_count);


        // Inserting Row
        db.insert(TABLE_FRAGEN_LISTE, null, values);
        db.close(); // Closing database connection
    }
    // get single question from db
    Question getQuestion(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_FRAGEN_LISTE + " WHERE " + KEY_ID + " = \'" + id + "\' " , null);

        if(cursor != null) {
            cursor.moveToFirst();
        }

        return getSelectedQuestion(cursor);
    }
    // get current db cursor
    Cursor getCursor() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_FRAGEN_LISTE, null);
    }



    static Question getSelectedQuestion(Cursor cursor) {
        // reading the comma separated lists (potentially single string or empty)
        String keywords_raw = cursor.getString(cursor.getColumnIndex(KEY_KEYWORDS));
        String links_raw    = cursor.getString(cursor.getColumnIndex(KEY_LINKS));


        String[] keywords;
        String[] links;

        // splitting the comma separated lists into arrays
        if (keywords_raw.contains(",")) {
            keywords = keywords_raw.split(",");
            links    = links_raw.split(",");
        } else {
            keywords = new String[] {keywords_raw};
            links    = new String[] {links_raw};
        }

        Question question   = new Question();
        question.id         = cursor.getInt(0);
        question.question   = cursor.getString  (cursor.getColumnIndex(KEY_QUES));
        question.guest      = cursor.getString  (cursor.getColumnIndex(KEY_GUEST));
        question.hashtag    = cursor.getString  (cursor.getColumnIndex(KEY_HASHTAG));
        question.ytlink     = cursor.getString  (cursor.getColumnIndex(KEY_YT));
        question.favorite   = cursor.getInt     (cursor.getColumnIndex(KEY_FAV)) == 1;
        question.answer_1   = cursor.getString  (cursor.getColumnIndex(KEY_ANSWER_1));
        question.answer_2   = cursor.getString  (cursor.getColumnIndex(KEY_ANSWER_2));
        question.answer_1_count = cursor.getInt (cursor.getColumnIndex(KEY_COUNT_ANSWER_1));
        question.answer_2_count = cursor.getInt (cursor.getColumnIndex(KEY_COUNT_ANSWER_2));
        question.clickables = new String[][] {keywords, links};


        //cursor.close();
        return question;
    }

    void setFavorite(int id, boolean favorite) {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("UPDATE " + TABLE_FRAGEN_LISTE + " SET " + KEY_FAV + " = " + (favorite ? (1) : (0)) + " WHERE " + KEY_ID + " = " + id + ";");

        Question quest = getQuestion(id);
        Log.d("dbh", "favorite set: " + quest.favorite);
    }
    void updateQuestion (Question question) {
        Log.d("DatabaseHelper", "updateQuestion aufgerufen");
        Log.d("DatabaseHelper", question.answer_1);
        SQLiteDatabase db = this.getWritableDatabase();


        ContentValues values = new ContentValues();
        values.put(KEY_QUES,            question.question);
        values.put(KEY_GUEST,           question.guest);
        values.put(KEY_ANSWER_1,        question.answer_1);
        values.put(KEY_ANSWER_2,        question.answer_2);
        values.put(KEY_COUNT_ANSWER_1,  question.answer_1_count);
        values.put(KEY_COUNT_ANSWER_2,  question.answer_2_count);
        values.put(KEY_YT,              question.ytlink);

        // updating row
        Log.d("DatabaseHelper wanted", ""+question.id);
        Log.d("DatabaseHelper result:", ""+ db.update (TABLE_FRAGEN_LISTE, values, KEY_ID + " = " + question.id, null));
        //db.close();
    }


    int getQuestionCount() {
        String countQuery = "SELECT  * FROM " + TABLE_FRAGEN_LISTE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();

        // return count
        return count;
    }


    // search database for certain strings in respective columns
    private List<Question> searchDatabase (String searchString, String KEY_COLUMN) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_FRAGEN_LISTE + " WHERE "
                + KEY_COLUMN + " LIKE \'%" + searchString + "%\';", null);
        c.moveToFirst();

        List<Question> questions = new ArrayList<>();

        while (!c.isAfterLast()) {
            questions.add(getSelectedQuestion(c));
            c.moveToNext();
        }
        return questions;
    }
    List<Question> searchQuestions (String searchString) {
        return searchDatabase(searchString, KEY_QUES);
    }
    List<Question> searchGuests (String searchString) {
        return searchDatabase(searchString, KEY_GUEST);
    }
    List<Question> searchHashtag (String searchString) {
        return searchDatabase(searchString, KEY_HASHTAG);
    }



    int countFavoredQuestions() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT COUNT(*) AS count FROM " + TABLE_FRAGEN_LISTE + " WHERE " + KEY_FAV + " = 1" , null);
        c.moveToFirst();

        return c.getInt(c.getColumnIndex("count"));

    }

}