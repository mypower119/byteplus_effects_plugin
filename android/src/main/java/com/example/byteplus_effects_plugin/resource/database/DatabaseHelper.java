package com.example.byteplus_effects_plugin.resource.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.jetbrains.annotations.Nullable;


public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String TABLE_NAME = "RemoteResItem";

    public static final String CREATE_TABLE = "create table if not exists " + TABLE_NAME + " ("
            + "id integer primary key autoincrement, "
            + "name text, "
            + "md5 text, "
            + "path text)";


    public DatabaseHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public synchronized void updateItem(SQLiteDatabase db, String name, String md5, String path) {
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("md5", md5);
        values.put("path", path);

        Cursor cursor = db.query(TABLE_NAME, null, "name=?", new String[]{name}, null, null, null);
        if (cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            values.put("id", id);
            db.replace(TABLE_NAME, null, values);
        } else {
            db.insert(TABLE_NAME, null, values);
        }
    }


    public String queryResource(SQLiteDatabase db, String name) {
        Cursor cursor = db.query(TABLE_NAME, null, "name=?", new String[]{name}, null, null, null);
        if (cursor.moveToFirst()) {
            return cursor.getString(cursor.getColumnIndex("path"));
        } else {
            return null;
        }
    }

    public synchronized DatabaseManager.DownloadResourceItem queryDownloadResourceItem(SQLiteDatabase db, String name) {
        Cursor cursor = db.query(TABLE_NAME, null, "name=?", new String[]{name}, null, null, null);
        DatabaseManager.DownloadResourceItem downloadResourceItem = null;
        if (cursor.moveToFirst()) {
            String path = cursor.getString(cursor.getColumnIndex("path"));
            String md5 = cursor.getString(cursor.getColumnIndex("md5"));
            downloadResourceItem = new DatabaseManager.DownloadResourceItem(name, md5, path);
        }
        return downloadResourceItem;
    }

    public synchronized void deleteDownloadResourceItem(SQLiteDatabase db, String name) {
        db.delete(TABLE_NAME, "name=?", new String[]{name});
    }

    public String queryMD5(SQLiteDatabase db, String name) {
        Cursor cursor = db.query(TABLE_NAME, null, "name=?", new String[]{name}, null, null, null);
        if (cursor.moveToFirst()) {
            String md5 = cursor.getString(cursor.getColumnIndex("md5"));
            return md5;
        } else {
            return null;
        }
    }

    public synchronized void removeAllResource(SQLiteDatabase db) {
        db.delete(TABLE_NAME, "", new String[]{});
    }

}
