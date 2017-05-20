package edorevia.com.instantext;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class InstantextDBHelper extends SQLiteOpenHelper{

    public static final String DATABASE_NAME = "InstantextDBHelper.db";
    private static final int DATABASE_VERSION = 1;
    public static final String TEXT_TABLE_NAME = "texts";
    public static final String TEXT_COLUMN_ID = "_id";
    public static final String TEXT_COLUMN_NAME = "name";
    public static final String TEXT_COLUMN_NUMBER = "number";
    public static final String TEXT_COLUMN_TEXT = "text";
    public static final String TEXT_COLUMN_COLOR = "color";

    public InstantextDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TEXT_TABLE_NAME + "(" +
                        TEXT_COLUMN_ID + " INTEGER PRIMARY KEY, " +
                        TEXT_COLUMN_NAME + " TEXT, " +
                        TEXT_COLUMN_NUMBER + " TEXT, " +
                        TEXT_COLUMN_TEXT + " TEXT, " +
                        TEXT_COLUMN_COLOR + " TEXT)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TEXT_TABLE_NAME);
        onCreate(db);
    }

    public boolean insertText(String name, String number, String message) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TEXT_COLUMN_NAME, name);
        contentValues.put(TEXT_COLUMN_NUMBER, number);
        contentValues.put(TEXT_COLUMN_TEXT, message);
        contentValues.put(TEXT_COLUMN_COLOR, "blue");
        db.insert(TEXT_TABLE_NAME, null, contentValues);
        return true;
    }

    public Integer deleteText(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TEXT_TABLE_NAME,
                TEXT_COLUMN_ID + " = ? ",
                new String[] { Integer.toString(id) });
    }

    public Cursor getText(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery( "SELECT * FROM " + TEXT_TABLE_NAME + " WHERE " +
                TEXT_COLUMN_ID + "=?", new String[] { Integer.toString(id) } );
        return res;
    }
    public Cursor getAllTexts() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery( "SELECT * FROM " + TEXT_TABLE_NAME, null );
        return res;
    }

    public boolean updatePerson(Integer id, String name, String phoneNo, String message) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TEXT_COLUMN_NAME, name);
        contentValues.put(TEXT_COLUMN_NUMBER, phoneNo);
        contentValues.put(TEXT_COLUMN_TEXT, message);
        contentValues.put(TEXT_COLUMN_COLOR, "blue");
        db.update(TEXT_TABLE_NAME, contentValues, TEXT_COLUMN_ID + " = ? ", new String[] { Integer.toString(id) } );
        return true;
    }
}
