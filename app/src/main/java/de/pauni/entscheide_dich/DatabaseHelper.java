package de.pauni.entscheide_dich;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/*
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


    // Table Columns names
    private static final String KEY_ID      = "_id";
    // Der Text der Frage
    private static final String KEY_QUES    = "question";
    // Der Namame des Gastes
    private static final String KEY_GUEST   = "guest_name";
    // Der YT-Link zum jew. Video
    private static final String KEY_YT      = "youtube_link";
    // Favorite ja oder nein (1/0)
    private static final String KEY_FAV     = "favorite";
    // String der zu clickable sein soll
    private static final String KEY_KEYWORDS = "keywords";
    // Link der aufgerufen wird
    private static final String KEY_LINKS   = "links";

    public DatabaseHelper(Context context) {
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
                        KEY_LINKS + " TEXT NOT NULL" +
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

    /**
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
        values.put(KEY_FAV, question.favorite);
        values.put(KEY_KEYWORDS, strings);
        values.put(KEY_LINKS, links);


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



        // reading the comma seperated lists (potentially single string or empty)
        String keywords_raw = cursor.getString(cursor.getColumnIndex(KEY_KEYWORDS));
        String links_raw    = cursor.getString(cursor.getColumnIndex(KEY_LINKS));


        String[] keywords;
        String[] links;

        if (keywords_raw.contains(",")) {
            keywords = keywords_raw.split(",");
            links    = links_raw.split(",");
        } else {
            keywords = new String[] {keywords_raw};
            links    = new String[] {links_raw};
        }

        Question question   = new Question();
        question.question   = cursor.getString(1);
        question.guest      = cursor.getString(2);
        question.ytlink     = cursor.getString(3);
        question.favorite   = cursor.getInt(4) == 1;
        question.clickables = new String[][] {keywords, links};

        cursor.close();
        return question;
    }

    public int getQuestionCount() {
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
        db.execSQL("UPDATE " + TABLE_NAME + " SET " + KEY_FAV + " = " +
                (favorite ? (1) : (0)) + " WHERE " + KEY_ID + " = " + id);
        Log.d("DBH>>>:","Ronis SQL-Befehl ausgeführt! ^-^");
    }

//BIS HIER WURDE UMGEBAUT ABER NICHT GETESTET
//AB HIER WURDE NOCH NICHTS GEMACHT


    /*
    // Getting All Contacts
    public List<Question> getAllQuestions() {
        List<Question> contactList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Contact contact = new Contact();
                contact.setID(Integer.parseInt(cursor.getString(0)));
                contact.setName(cursor.getString(1));
                contact.setPhoneNumber(cursor.getString(2));
                // Adding contact to list
                contactList.add(contact);
            } while (cursor.moveToNext());
        }

        // return contact list
        return contactList;
    }


    */
}