package com.example.paddy.com594assignment;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DataBaseHelper extends SQLiteOpenHelper {

    public DataBaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Create table sql query
        String CREATE_USER_TABLE =
                "create table user (userId integer primary key autoincrement, userName text, " +
                        "userPassword text);";
        try{
            db.execSQL(CREATE_USER_TABLE);
        }
        catch(Exception er){
            Log.e("Error : ", "exception");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Drop the table if exist
        db.execSQL("DROP TABLE IF EXISTS " + "user");
        //Create a new one
        onCreate(db);

    }
}
