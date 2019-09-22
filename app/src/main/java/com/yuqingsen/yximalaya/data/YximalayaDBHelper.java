package com.yuqingsen.yximalaya.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.yuqingsen.yximalaya.utils.Constants;

public class YximalayaDBHelper extends SQLiteOpenHelper {
    public YximalayaDBHelper(Context context) {
        //name数据库的名字，factory游标工厂，version版本号
        super(context,Constants.DB_NAME,null,Constants.DB_VERSION_CODE);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建数据库
        //订阅相关字段
        //图片、title、描述、播放量、节目数量、作者名称、专辑ID
        String subTbSql = "create table "+Constants.SUB_TB_NAME+"("+
                Constants.SUB_ID+" integer primary key autoincrement,"+
                Constants.SUB_COVER_URL+" varchar,"+
                Constants.SUB_TITLE+" varchar," +
                Constants.SUB_DESCRIPTION+" varchar," +
                Constants.SUB_PLAY_COUNT+" integer," +
                Constants.SUB_TRACKS_COUNT+" integer," +
                Constants.SUB_AUTHOR_NAME+" varchar," +
                Constants.SUB_ALBUM_ID+" integer" +
                ")";
        db.execSQL(subTbSql);

        //创建历史记录表
        String historyTbSql = "create table "+Constants.HISTORY_TB_NAME+"("+
                Constants.HISTORY_ID+" integer primary key autoincrement,"+
                Constants.HISTORY_TRACK_ID+" integer,"+
                Constants.HISTORY_TITLE+" varchar," +
                Constants.HISTORY_PLAY_COUNT+" integer," +
                Constants.HISTORY_DURATION+" integer," +
                Constants.HISTORY_AUTHOR+" varchar," +
                Constants.HISTORY_UPDATE_TIME+" integer," +
                Constants.HISTORY_COVER+" varchar" +
                ")";
        db.execSQL(historyTbSql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
