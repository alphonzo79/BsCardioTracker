package com.bscardiotracker.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.bscardiotracker.R;
import com.google.gson.Gson;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "BsCardioTrackerDB";
    private static final int VERSION = 1;

    Context mContext;

    public DatabaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //create tables
        sqLiteDatabase.beginTransaction();
        try {
            sqLiteDatabase.execSQL(mContext.getString(R.string.create_history_table));
            sqLiteDatabase.setTransactionSuccessful();
        } finally {
            sqLiteDatabase.endTransaction();
        }
        Log.d("BsCardioTracker", "Finished onCreate");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        //todo
    }
}
