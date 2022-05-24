package com.edu.velibapp

import android.database.sqlite.SQLiteDatabase.CursorFactory
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import android.database.sqlite.SQLiteDatabase
import android.content.ContentValues
import android.annotation.SuppressLint
import android.content.Context
import java.util.ArrayList
import android.database.Cursor

class DBHelper(context: Context?, name: String?, factory: CursorFactory?, version: Int) :
    SQLiteOpenHelper(context, name, factory, version) {
    private val TAG = "DBHelper"

    //歌曲表
    /*表名*/
    private val TABLE_NAME_COLLECT = "_collect"
    private val VALUE_ID = "_id"
    private val VALUE_STATION_ID = "_station_id"
    val CREATE_COLLECT = "create table " + TABLE_NAME_COLLECT + "(" +
            VALUE_ID + " integer primary key," +
            VALUE_STATION_ID + " long " +
            ")"

    constructor(context: Context?) : this(context, "collect.db", null, 1) {
        Log.e(TAG, "-------> DBHelper")
    }

    override fun onCreate(db: SQLiteDatabase) {

        //创建表
        db.execSQL(CREATE_COLLECT)
        Log.e(TAG, "-------> onCreate")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        Log.e(TAG, "-------> onUpgrade  oldVersion = $oldVersion   newVersion = $newVersion")
    }

    fun unCollect(station_id: Long): Boolean {
        //修改model的数据
        val index = writableDatabase.delete(
            TABLE_NAME_COLLECT,
            "$VALUE_STATION_ID=?",
            arrayOf(station_id.toString())
        ).toLong()
        writableDatabase.close()
        return if (index != -1L) {
            true
        } else {
            false
        }
    }

    /**
     * 收藏
     *
     * @return
     */
    fun collect(station_id: Long?): Boolean {
        val values = ContentValues()
        values.put(VALUE_STATION_ID, station_id)
        //添加数据到数据库
        val index = writableDatabase.insert(TABLE_NAME_COLLECT, null, values)
        writableDatabase.close()
        return if (index != -1L) {
            true
        } else {
            false
        }
    }//移动到下一位//移动到首位//查询全部数据

    fun isCollect(station_id: Long):Boolean{
        val cursor =
            writableDatabase.query(TABLE_NAME_COLLECT, null, "$VALUE_STATION_ID=?",
                arrayOf(station_id.toString()), null, null, null, null)
        var isCollect = cursor.count>0
        cursor.close()
        writableDatabase.close()
        return isCollect;
    }

    /**
     * 查询所有收藏
     *
     * @return
     */
    @get:SuppressLint("Range")
    val collectList: ArrayList<Long>
        get() {
            //查询全部数据
            val cursor =
                writableDatabase.query(TABLE_NAME_COLLECT, null, null, null, null, null, null, null)
            val collects = ArrayList<Long>()
            if (cursor.count > 0) {
                //移动到首位
                cursor.moveToFirst()
                for (i in 0 until cursor.count) {
                    val station_id = cursor.getLong(cursor.getColumnIndex(VALUE_STATION_ID))
                    //移动到下一位
                    collects.add(station_id)
                    cursor.moveToNext()
                }
            }
            cursor.close()
            writableDatabase.close()
            return collects
        }

    init {
        Log.e(TAG, "-------> DBHelper")
    }
}