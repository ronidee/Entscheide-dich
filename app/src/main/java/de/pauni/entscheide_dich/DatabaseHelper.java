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
    private static final String TABLE_NAME = "FRAGEN_LISTE";



    private static final String KEY_ID      = "_id";            // Table Columns names
    private static final String KEY_QUES    = "question";       // Der Text der Frage
    private static final String KEY_GUEST   = "guest_name";     // Der Name des Gastes
    private static final String KEY_YT      = "youtube_link";   // Der YT-Link zum jew. Video
    private static final String KEY_FAV     = "favorite";       // Favorite ja oder nein (1/0)
    static final String KEY_KEYWORDS        = "keywords";       // String der zu clickable sein soll
    static final String KEY_LINKS           = "links";          // Link der aufgerufen wird
    private static final String KEY_ANSWER_1 = "answer1";       // Antwortmöglichkeit 1
    private static final String KEY_ANSWER_2 = "answer2";       // Antwortmöglichkeit 2
    private static final String KEY_LOCALVOTE = "localvote";      // Antwort des Users
    private static final String KEY_COUNT_ANSWER_1 = "count_answer_1"; // Anzahl der Votes für Antwort 1
    private static final String KEY_COUNT_ANSWER_2 = "count_answer_2"; // Anzahl der Votes für Antwort 2



    DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.d("DatabaseHelper>>>", "konstrukter geladen");
    }


    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Erstelle eine Tabelle mit den Infos die eine Frage jeweils hat

        String CREATE_TABLE_STRING =
                "CREATE TABLE " + TABLE_NAME +
                    "(" +
                        KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        KEY_QUES + " TEXT NOT NULL," +
                        KEY_GUEST + " TEXT NOT NULL," +
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

        db.execSQL(CREATE_TABLE_STRING);
        Log.d("DatabaseHelper>>>", "    DB created");

    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older tables if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        // Create tables again
        onCreate(db);
    }

    /*
     * All CRUD(Create, Read, Update, Delete) Operations
     */




    // Adding new question. Only required at app's 1. start if Database doesn't exist yet
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
        values.put(KEY_FAV, question.favorite); // isn't his redundant? All favs are false...
        values.put(KEY_KEYWORDS, strings);
        values.put(KEY_LINKS, links);
        values.put(KEY_ANSWER_1, question.answer_1);
        values.put(KEY_ANSWER_2, question.answer_2);
        values.put(KEY_LOCALVOTE,        question.localvote);
        values.put(KEY_COUNT_ANSWER_1, question.count_answer_1);
        values.put(KEY_COUNT_ANSWER_2, question.count_answer_2);


        // Inserting Row
        db.insert(TABLE_NAME, null, values);
        db.close(); // Closing database connection
    }




    // Getting single question
    Question getQuestion(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + KEY_ID + " = \'" + id + "\' " , null);

        if(cursor != null) {
            cursor.moveToFirst();
        }



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
        question.ytlink     = cursor.getString  (cursor.getColumnIndex(KEY_YT));
        question.favorite   = cursor.getInt     (cursor.getColumnIndex(KEY_FAV)) == 1;
        question.clickables = new String[][] {keywords, links};

        Log.d("getQuestion ", "answer1 = " + question.answer_1);
        cursor.close();
        return question;
    }

    int getQuestionCount() {
        String countQuery = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();

        // return count
        return count;
    }



    void setFavorite(int id, boolean favorite) {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("UPDATE " + TABLE_NAME + " SET " + KEY_FAV + " = " + (favorite ? (1) : (0)) + " WHERE " + KEY_ID + " = " + id + ";");

        Question quest = getQuestion(id);
        Log.d("dbh", "favorite set: " + quest.favorite);
    }

    // Paul, das ist Deins ;)...
    void updateQuestion (Question question) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_QUES,            question.question);
        values.put(KEY_GUEST,           question.guest);
        values.put(KEY_ANSWER_1,        question.answer_1);
        values.put(KEY_ANSWER_2,        question.answer_2);
        values.put(KEY_LOCALVOTE,        question.localvote);
        values.put(KEY_COUNT_ANSWER_1,  question.count_answer_1);
        values.put(KEY_COUNT_ANSWER_2,  question.count_answer_2);
        values.put(KEY_YT,              question.ytlink);

        // updating row
        db.update(TABLE_NAME, values, KEY_ID + " = ?",
                new String[] { String.valueOf(question.id) });
    }


    Cursor getCursor() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
    }

    Cursor getCursorFavorites() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + KEY_FAV + " = 1" , null);
    }





    int countAllQuestions() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT count() AS count FROM " + TABLE_NAME , null);
        c.moveToFirst();

        return c.getInt(c.getColumnIndex("count"));
    }


    int countFavoredQuestions() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT COUNT(*) AS count FROM " + TABLE_NAME + " WHERE " + KEY_FAV + " = 1" , null);
        c.moveToFirst();

        return c.getInt(c.getColumnIndex("count"));

    }


    Cursor searchQuestion(String searchString) {
        SQLiteDatabase db = this.getReadableDatabase();
        //Log.d("dbh>>>", "SELECT * FROM " + TABLE_NAME + " WHERE " + KEY_QUES + " LIKE \'" + searchString + "\';");
        //Cursor c = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + KEY_ID + " < \'" + searchString + "\';", null);

        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + KEY_QUES + " LIKE \'%" + searchString + "%\';", null);
        c.moveToFirst();

        Log.d("dbh>>>", String.valueOf(c.getCount()));
        return c;
    }
}