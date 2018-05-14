package pt.ulisboa.tecnico.cmu.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class UsersScoreDBHandler extends SQLiteOpenHelper{

    public static final String DATABASE_NAME = "UsersScore.db";
    public static final String TABLE_NAME = "UsersScore";
    public static final String USER_NAME = "USER_NAME";
    public static final String USER_SCORE = "USER_SCORE";


    public UsersScoreDBHandler(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" + USER_NAME + " TEXT PRIMARY KEY,"
                                + USER_SCORE + " INTEGER" + ")";
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public void insertName(String userName){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(USER_NAME, userName);
        db.insert(TABLE_NAME, null, values);
    }

    public void insertScore(String userName, int score){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(USER_SCORE, score);
        db.update(TABLE_NAME, values, USER_NAME + " = ?", new String[]{userName});
    }

    public int getScoreByUser(String userName){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[]{USER_SCORE},
                USER_NAME + " = ? ", new String[]{userName},
                null, null, null, null);
        if(cursor.getCount() == 0)
            return -1;
        if(cursor != null)
            cursor.moveToFirst();

        return cursor.getInt(0);
    }
}
