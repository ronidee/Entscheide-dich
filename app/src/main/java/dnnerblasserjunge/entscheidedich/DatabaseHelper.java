package dnnerblasserjunge.entscheidedich;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


/*
*        THANK YOU VERY MUCH TO ANDROIDHIVE.INFO
*        FOR PROVIDING A MODEL OF A DB-HELPER CLASSS
 *       http://www.androidhive.info/2011/11/android-sqlite-database-tutorial/
*/

class DatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 2;

    // Database Name
    private static final String DATABASE_NAME = "entscheideDich";

    // Table names
    private static final String LISTE_1     =   "FRAGEN_LISTE_1";
    private static final String LISTE_2     =   "FRAGEN_LISTE_2";
    private static final String LISTE_3     =   "FRAGEN_LISTE_3";
    private static final String LISTE_4     =   "FRAGEN_LISTE_4";
    private static final String LISTE_5     =   "FRAGEN_LISTE_5";
    private static final String LISTE_6     =   "FRAGEN_LISTE_6";
    private static final String LISTE_7     =   "FRAGEN_LISTE_7";
    private static final String LISTE_8     =   "FRAGEN_LISTE_8";
    private static final String LISTE_9     =   "FRAGEN_LISTE_9";
    private static final String LISTE_10    =   "FRAGEN_LISTE_10";
    private static final String LISTE_11    =   "FRAGEN_LISTE_11";
    private static final String LISTE_12    =   "FRAGEN_LISTE_12";
    private static final String LISTE_13    =   "FRAGEN_LISTE_13";
    private static final String LISTE_14    =   "FRAGEN_LISTE_14";


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
    // Namen der zu duckduckgoenden Namen
    private static final String KEY_INFO    = "wissenswertes";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.d("DatabaseHelper>>>","konstrukter geladen");
    }

    // Getting the required table name (LISTE_1 ... LISTE_14)
    private String getTableName(int number) {
        switch (number) {
            case 1:  return LISTE_1;
            case 2:  return LISTE_2;
            case 3:  return LISTE_3;
            case 4:  return LISTE_4;
            case 5:  return LISTE_5;
            case 6:  return LISTE_6;
            case 7:  return LISTE_7;
            case 8:  return LISTE_8;
            case 9:  return LISTE_9;
            case 10: return LISTE_10;
            case 11: return LISTE_11;
            case 12: return LISTE_12;
            case 13: return LISTE_13;
            case 14: return LISTE_14;
        }
        return "wrong input number. Only pass numbers from 1-14.";
    }


    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Erstelle 14 Table mit den Infos die eine Frage jeweils hat
        for(int i=1; i<=14; i++) {
            String CREATE_TABLE_STRING = "CREATE TABLE " + getTableName(i) + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + KEY_QUES + " TEXT," + KEY_GUEST + " TEXT," + KEY_YT + " TEXT,"
                    + KEY_FAV + " TEXT," + KEY_INFO + " TEXT" +  ")";
            db.execSQL(CREATE_TABLE_STRING);
            Log.d("DatabaseHelper>>>","    DB"+ i + "created");
        }
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older tables if existed
        for (int i=1; i<=14; i++) {
            db.execSQL("DROP TABLE IF EXISTS " + getTableName(i));
        }
        // Create tables again
        onCreate(db);
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */

    // Adding new question. Only required at app's 1. start if Database doesn't exist yet
    void addQuestion(int list, String[] questionData) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_QUES,    questionData[0]);
        values.put(KEY_GUEST,   questionData[1]);
        values.put(KEY_YT,      questionData[2]);
        values.put(KEY_FAV,     questionData[3]);
        values.put(KEY_INFO,    questionData[4]);

        // Inserting Row
        db.insert(getTableName(list), null, values);
        db.close(); // Closing database connection
    }

    // Getting single contact
    Question getQuestion(int list, int entry) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(getTableName(list), new String[] { KEY_ID,
                        KEY_QUES, KEY_GUEST, KEY_YT, KEY_FAV, KEY_INFO }, KEY_ID + "=?",
                new String[] { String.valueOf(entry) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Question question= new Question(cursor.getString(0), cursor.getString(1));
        question.setYtLink  (cursor.getString(2));
        question.setFavorit (cursor.getString(3).equals("1"));
        question.setInfo    (cursor.getString(4));

        return question;
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

    // Updating single contact
    public int updateContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, contact.getName());
        values.put(KEY_PH_NO, contact.getPhoneNumber());

        // updating row
        return db.update(TABLE_CONTACTS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(contact.getID()) });
    }

    // Getting contacts Count
    public int getContactsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_CONTACTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    } */

}