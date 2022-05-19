package com.example.byteplus_effects_plugin.resource.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class DatabaseManager {
    private static Context mContext = null;
    private final DatabaseHelper mDatabaseHelper;
    SQLiteDatabase db;

    private volatile static DatabaseManager mDatabaseManager;

    private DatabaseManager(Context context) {
        mDatabaseHelper = new DatabaseHelper(context.getApplicationContext(), "RemoteResConfig.db", null, 1);
        db = mDatabaseHelper.getWritableDatabase();
    }

    public static void init(Context context) {
        mContext = context;
    }

    public static DatabaseManager getInstance() {
        if (mDatabaseManager == null) {
            if (mContext == null) {
                throw new IllegalStateException("must call init(Context) first");
            }
            synchronized (DatabaseManager.class) {
                if (mDatabaseManager == null) {
                    mDatabaseManager = new DatabaseManager(mContext);
                    mContext = null;
                }
            }
        }
        return mDatabaseManager;
    }

    public synchronized DownloadResourceItem resourceItem(String name) {
        return mDatabaseHelper.queryDownloadResourceItem(db, name);
    }

    public void addResourceItem(String name, String md5, String path) {
        mDatabaseHelper.updateItem(db, name, md5, path);
    }

    public void deleteResourceItem(String name) {
        mDatabaseHelper.deleteDownloadResourceItem(db, name);
    }

    public void removeAllResourceItem() {
        mDatabaseHelper.removeAllResource(db);
    }

    public static class DownloadResourceItem {
        public String name;
        public String md5;
        public String path;

        DownloadResourceItem(String name, String md5, String path) {
            this.name = name;
            this.md5 = md5;
            this.path = path;
        }
    }

}
