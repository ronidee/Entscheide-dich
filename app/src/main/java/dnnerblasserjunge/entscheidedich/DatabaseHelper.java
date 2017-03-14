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
    private static final String TABLE_NAME     =   "FRAGEN_LISTE_1";



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



    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Erstelle 14 Table mit den Infos die eine Frage jeweils hat

        String CREATE_TABLE_STRING =
            "CREATE TABLE " + TABLE_NAME +
                "(" +
                    KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    KEY_QUES + " TEXT," +
                    KEY_GUEST + " TEXT," +
                    KEY_YT + " TEXT," +
                    KEY_FAV + " INTEGER," +
                    KEY_INFO + " TEXT" +
                ")";
        
            db.execSQL(CREATE_TABLE_STRING);
            Log.d("DatabaseHelper>>>","    DB created");

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
    void addQuestion(String[] questionData) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_QUES,    questionData[0]);
        values.put(KEY_GUEST,   questionData[1]);
        values.put(KEY_YT,      questionData[2]);
        values.put(KEY_FAV,     questionData[3]);
        values.put(KEY_INFO,    questionData[4]);

        // Inserting Row
        db.insert(TABLE_NAME, null, values);
        db.close(); // Closing database connection
    }

    // Getting single contact
    Question getQuestion(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME, new String[] { KEY_ID,
                        KEY_QUES, KEY_GUEST, KEY_YT, KEY_FAV, KEY_INFO }, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
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