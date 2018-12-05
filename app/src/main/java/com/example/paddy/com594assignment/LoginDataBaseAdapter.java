package com.example.paddy.com594assignment;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LoginDataBaseAdapter {
    //Database Version
    private static final int DATABASE_VERSION = 1;
    //Database name
    private static final String DATABASE_NAME = "login.db";

    //Variable to hold the database instance
    private static SQLiteDatabase db;
    //Variable to hold the database instance
    private static DataBaseHelper dbHelper;

    //Constructor
    public LoginDataBaseAdapter(Context context) {
        dbHelper = new DataBaseHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //Method to open the Database
    public LoginDataBaseAdapter open() throws SQLException
    {
        db = dbHelper.getWritableDatabase();
        return this;
    }
    //Method to close the Database
    public void close()
    {
        db.close();
    }
    //Method returns an Instance of the Database
    public SQLiteDatabase getDatabaseInstance()
    {
        return db;
    }

    //Method to get the password of userName
    public String getSingleEntry(String userName)
    {
        db = dbHelper.getReadableDatabase();
        Cursor cursor=db.query("user", null, "userName=?",
                new String[]{userName}, null, null, null);
        if(cursor.getCount()<1) //Username doesn't exist
            return "NOT EXIST";
        cursor.moveToFirst();
        String getPassword= cursor.getString(cursor.getColumnIndex("userPassword"));
        return getPassword;
    }

    public void insertEntry(String userName, String password)
    {
        ContentValues newValues = new ContentValues();
        //Assign values for each row
        newValues.put("userName", userName);
        newValues.put("userPassword", password);

        //Insert the row into your table
        db.insert("user", null, newValues);
    }
}


