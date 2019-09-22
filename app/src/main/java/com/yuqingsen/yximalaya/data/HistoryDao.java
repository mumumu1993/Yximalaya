package com.yuqingsen.yximalaya.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ximalaya.ting.android.opensdk.model.album.Announcer;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.yuqingsen.yximalaya.base.BaseApplication;
import com.yuqingsen.yximalaya.utils.Constants;
import com.yuqingsen.yximalaya.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class HistoryDao implements IHistoryDao {

    private static final String TAG = "HistoryDao";
    private YximalayaDBHelper mDbHelper = null;
    private IHistoryDaoCallback mCallback = null;

    public HistoryDao(){
        if (mDbHelper == null) {
            mDbHelper = new YximalayaDBHelper(BaseApplication.getAppContext());
        }
    }
    @Override
    public void setCallback(IHistoryDaoCallback callback) {
        this.mCallback = callback;
    }

    @Override
    public synchronized void addHistory(Track track) {
        SQLiteDatabase db = null;
        boolean isAddSuccess = false;
        try {
            db = mDbHelper.getWritableDatabase();
            //先删除旧的
            db.delete(Constants.HISTORY_TB_NAME, Constants.HISTORY_TRACK_ID + "=?", new String[]{track.getDataId() + ""});
            db.beginTransaction();
            //删除以后再添加
            ContentValues values = new ContentValues();
            //封装数据
            values.put(Constants.HISTORY_TRACK_ID,track.getDataId());
            values.put(Constants.HISTORY_TITLE,track.getTrackTitle());
            values.put(Constants.HISTORY_PLAY_COUNT,track.getPlayCount());
            values.put(Constants.HISTORY_DURATION,track.getDuration());
            values.put(Constants.HISTORY_UPDATE_TIME,track.getUpdatedAt());
            values.put(Constants.HISTORY_COVER,track.getCoverUrlLarge());
            values.put(Constants.HISTORY_AUTHOR,track.getAnnouncer().getNickname());
            //插入数据
            db.insert(Constants.HISTORY_TB_NAME,null,values);
            db.setTransactionSuccessful();
            isAddSuccess = true;
        }catch (Exception e){
            isAddSuccess = false;
            e.printStackTrace();
        }finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
            if (mCallback != null) {
                mCallback.onHistoryAdd(isAddSuccess);
            }
        }

    }

    @Override
    public synchronized void delHistory(Track track) {
        SQLiteDatabase db = null;
        boolean isDelSuccess = false;
        try {
            db = mDbHelper.getWritableDatabase();
            db.beginTransaction();
            int delete = db.delete(Constants.HISTORY_TB_NAME, Constants.HISTORY_TRACK_ID + "=?", new String[]{track.getDataId() + ""});
            LogUtil.d(TAG, "delete------->" + delete);
            db.setTransactionSuccessful();
            isDelSuccess = true;
        } catch (Exception e) {
            e.printStackTrace();
            isDelSuccess = false;
        } finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
            if (mCallback != null) {
                mCallback.onHistoryDel(isDelSuccess);
            }
        }
    }

    @Override
    public synchronized void clearHistory() {
        SQLiteDatabase db = null;
        boolean isClearSuccess = false;
        try {
            db = mDbHelper.getWritableDatabase();
            db.beginTransaction();
            db.delete(Constants.HISTORY_TB_NAME,null,null);
            db.setTransactionSuccessful();
            isClearSuccess = true;
        } catch (Exception e) {
            e.printStackTrace();
            isClearSuccess = false;
        } finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
            if (mCallback != null) {
                mCallback.onHistoriesClear(isClearSuccess);
            }
        }
    }

    @Override
    public synchronized void listHistories() {
        SQLiteDatabase db = null;
        List<Track> histories = new ArrayList<>();
        try {
            db = mDbHelper.getWritableDatabase();
            db.beginTransaction();
            Cursor cursor = db.query(Constants.HISTORY_TB_NAME, null, null, null, null, null, "_id desc");
            while (cursor.moveToNext()) {
                Track track = new Track();
                int trackId = cursor.getInt(cursor.getColumnIndex(Constants.HISTORY_TRACK_ID));
                track.setDataId(trackId);
                String trackTitle = cursor.getString(cursor.getColumnIndex(Constants.HISTORY_TITLE));
                track.setTrackTitle(trackTitle);
                int playCount = cursor.getInt(cursor.getColumnIndex(Constants.HISTORY_PLAY_COUNT));
                track.setPlayCount(playCount);
                int duration = cursor.getInt(cursor.getColumnIndex(Constants.HISTORY_DURATION));
                track.setDuration(duration);
                long updateTime = cursor.getLong(cursor.getColumnIndex(Constants.HISTORY_UPDATE_TIME));
                track.setUpdatedAt(updateTime);
                String trackCover = cursor.getString(cursor.getColumnIndex(Constants.HISTORY_COVER));
                track.setCoverUrlLarge(trackCover);
                String author = cursor.getString(cursor.getColumnIndex(Constants.HISTORY_AUTHOR));
                Announcer announcer = new Announcer();
                announcer.setNickname(author);
                track.setAnnouncer(announcer);
                histories.add(track);
            }
            cursor.close();
            db.setTransactionSuccessful();

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
            if (mCallback != null) {
                mCallback.onHistoriesLoaded(histories);
            }
        }

    }
}
