package pt.ulisboa.tecnico.cmu.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class UserQuizDBHandler extends SQLiteOpenHelper{

    public static final String DATABASE_NAME = "UsersQuiz.db";
    public static final String TABLE_NAME = "UsersQuiz";
    public static final String USER_NAME = "USER_NAME";
    public static final String QUIZ_NAME = "QUIZ_NAME";
    public static final String QUIZ_SCORE = "QUIZ_SCORE";
    public static String userName;
    public static String monumentName;

    public UserQuizDBHandler(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" + USER_NAME + " TEXT,"
                + QUIZ_NAME + " TEXT," + QUIZ_SCORE + " INTEGER, PRIMARY KEY(" + USER_NAME + "," + QUIZ_NAME + "))";
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public void insertNameandMonumentName(String userName, String monumentName){
        this.userName = userName;
        this.monumentName = monumentName;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(USER_NAME, userName);
        values.put(QUIZ_NAME, monumentName);
        db.insert(TABLE_NAME, null, values);
    }

    public void insertAnswers(int numberOfQuestion, String answer){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN QUESTION_" + numberOfQuestion + " TEXT");
        ContentValues values = new ContentValues();
        values.put("QUESTION_" + numberOfQuestion, answer);
        db.update(TABLE_NAME, values, USER_NAME + " = ? AND " + QUIZ_NAME + " = ? ",
                                        new String[]{userName, monumentName});
    }

    public void updateScore(int quizScore){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(QUIZ_SCORE, quizScore);
        db.update(TABLE_NAME, values, USER_NAME + " = ? AND " + QUIZ_NAME + " = ? ",
                new String[]{userName, monumentName});
    }

    public String getScoreByUserAndQuiz(String userName, String monumentName){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[]{QUIZ_SCORE},
                USER_NAME + " = ? AND " + QUIZ_NAME + " = ?", new String[]{userName, monumentName},
                null, null, null, null);
        if(cursor.getCount() == 0)
            return null;
        if(cursor != null)
            cursor.moveToFirst();

        return cursor.getString(0);
    }

    public String getQuestionAnswerByUserAndQuiz(String userName, String monumentName, int numberOfQuestion){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[]{"QUESTION_"+numberOfQuestion},
                USER_NAME + " = ? AND " + QUIZ_NAME + " = ?", new String[]{userName, monumentName},
                null, null, null, null);
        if(cursor.getCount() == 0)
            return null;
        if(cursor != null)
            cursor.moveToFirst();

        return cursor.getString(0);
    }
}
