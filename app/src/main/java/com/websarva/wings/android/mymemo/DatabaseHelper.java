package com.websarva.wings.android.mymemo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    //データベースファイル名の定数
    private static final String DATABASE_NAME = "mymemo.db";
    //バージョン情報
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE memos  (");
        sb.append("_id TEXT PRIMARY KEY,");
        sb.append("title TEXT,");
        sb.append("content TEXT,");
        sb.append("upddate TEXT");
        sb.append(");");
        String sql = sb.toString();

        //クエリ実行
        db.execSQL(sql);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
    }
}
