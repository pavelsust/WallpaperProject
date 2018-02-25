package com.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.pojo.WallpaperItem;

import java.util.ArrayList;

/**
 * Created by android on 1/15/2018.
 */

public class DatabaseManager {

    public SQLiteDatabase sqLiteDatabase;
    public DatabaseHelper databaseHelper;
    public WallpaperItem wallpaperItem;


    public DatabaseManager(Context context){
        databaseHelper = new DatabaseHelper(context);
        wallpaperItem = new WallpaperItem();
        sqLiteDatabase = databaseHelper.getReadableDatabase();


    }


    public void open() throws SQLiteException{
        sqLiteDatabase = databaseHelper.getWritableDatabase();

    }

    public void close() throws SQLiteException{
        sqLiteDatabase.close();
    }

    public WallpaperItem createWallpaperList (final WallpaperItem wallpaperItem){
        open();
        ContentValues contentValues = new ContentValues();
        contentValues.put("link" , wallpaperItem.getUrl());
        contentValues.put("wallpaper_id" , wallpaperItem.getId());
        sqLiteDatabase.insert(DatabaseHelper.TABLE_FAVOURITE_WALLPAPER , null, contentValues);
        return wallpaperItem;
    }


    public void addIntoDatabase(String link, int id ){

        wallpaperItem.setUrl(link);
        wallpaperItem.setId(id);
        createWallpaperList(wallpaperItem);
    }

    public ArrayList<WallpaperItem> getAllFavouriteWallpaperData(){
        open();
        final ArrayList<WallpaperItem> wallpaperItems = new ArrayList<>();
        final Cursor cursor = sqLiteDatabase.rawQuery("select * from " + DatabaseHelper.TABLE_FAVOURITE_WALLPAPER, null);

        cursor.moveToLast();
        while (!cursor.isBeforeFirst()){
            final WallpaperItem wallpaperItem = new WallpaperItem();
            wallpaperItem.setId(cursor.getInt(0));
            wallpaperItem.setUrl(cursor.getString(1));
            wallpaperItems.add(wallpaperItem);
            cursor.moveToPrevious();
        }

        return wallpaperItems;
    }


    public void deleteWallpaper(final WallpaperItem wallpaperItem){
        sqLiteDatabase.delete(DatabaseHelper.TABLE_FAVOURITE_WALLPAPER , "link = '" + wallpaperItem.getUrl() + "'", null);
    }

}
