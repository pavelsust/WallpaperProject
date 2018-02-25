package com.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by android on 1/15/2018.
 */

public class DatabaseHelper extends SQLiteOpenHelper{



    public static final String DATABASE_NAME = "WALasasdasddLPAPER.db";
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE_FAVOURITE_WALLPAPER = "favoasdasdasdadurite_wallpaper";


    public static final String TABLE_CREATE_FAVOURITE_WALLPAPER =

            "create table " + TABLE_FAVOURITE_WALLPAPER + "( id "
                    + " integer primary key autoincrement, link text not null, wallpaper_id  int not null );";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE_FAVOURITE_WALLPAPER);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVOURITE_WALLPAPER);
    }
}
