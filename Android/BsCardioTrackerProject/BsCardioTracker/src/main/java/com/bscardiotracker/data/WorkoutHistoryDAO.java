package com.bscardiotracker.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class WorkoutHistoryDAO extends DatabaseHelper {
    public WorkoutHistoryDAO(Context context) {
        super(context);
    }

    public boolean recordNewWorkout(WorkoutDataEntity workout) {
        SQLiteDatabase db = getWritableDatabase();

        String json = new Gson().toJson(workout);
        Log.d("BsCardioTracker", json);

        SQLiteStatement stmt = db.compileStatement("INSERT INTO history (date, duration, distance, pace, json) VALUES (?, ?, ?, ?, ?);");
        stmt.bindLong(1, workout.getWorkoutDate());
        stmt.bindLong(2, workout.getDuration());
        stmt.bindDouble(3, workout.getDistance());
        stmt.bindLong(4, workout.getPace());
        stmt.bindString(5, json);

        boolean result = false;
        db.beginTransaction();
        try {
            stmt.execute();
            result = true;
            db.setTransactionSuccessful();
        } catch(SQLiteException e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            stmt.clearBindings();
            stmt.close();
            db.close();
        }

        return result;
    }

    public WorkoutDataEntity getWorkout(int id) {
        SQLiteDatabase db = getReadableDatabase();

        String stmt = String.format("SELECT json FROM history WHERE _id = '%d';", id);

        Cursor rs = db.rawQuery(stmt, null);
        String json = "";
        try {
            rs.moveToFirst();
            json = rs.getString(0);
        } catch(SQLiteException e) {
            e.printStackTrace();
        } finally {
            rs.close();
            db.close();
        }

        WorkoutDataEntity result = new Gson().fromJson(json, WorkoutDataEntity.class);

        return result;
    }

    public List<WorkoutDataEntity> getRecentWorkouts() {
        SQLiteDatabase db = getReadableDatabase();

        String stmt = String.format("SELECT json FROM history ORDER BY date DESC LIMIT 25");

        Cursor rs = db.rawQuery(stmt, null);
        List<WorkoutDataEntity> result = null;
        try {
            rs.moveToFirst();
            result = new ArrayList<WorkoutDataEntity>();
            do {
                String json = rs.getString(0);
                result.add(new Gson().fromJson(json, WorkoutDataEntity.class));
            } while(rs.moveToNext());
        } catch(SQLiteException e) {
            e.printStackTrace();
        } finally {
            rs.close();
            db.close();
        }

        return result;
    }
}
