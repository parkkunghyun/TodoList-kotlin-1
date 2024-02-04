package org.techtown.todolist_1

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class ToDoDB(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    private var db: SQLiteDatabase? = null
    companion object {
        private const val DATABASE_NAME = "todo_list.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "todo_list"
        private const val ID = "id"
        private const val TASK = "task"
        private const val STATUS = "status"
    }

    // DB생성
    override fun onCreate(db: SQLiteDatabase?) {
        val query = "CREATE TABLE $TABLE_NAME (" +
                "$ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$TASK TEXT, $STATUS INTEGER)"
        db?.execSQL(query)
    }

    // DB  업그레이드
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    /**
     * DB쓰기
     */
    fun openDatabase() {
        db = this.writableDatabase
    }

    /**
     * 할일 전체 가져오기
     */
    fun getAllTasks() :ArrayList<ToDoModel> {
        val taskList: ArrayList<ToDoModel> = ArrayList()
        var cursor: Cursor? = null
        var query = "SELECT * FROM $TABLE_NAME"
        // 읽기
        db = this.readableDatabase
        if(db != null) {
            cursor = db!!.rawQuery(query, null)
            while(cursor.moveToNext()) {
                // 담기
                val task = ToDoModel(cursor.getInt(0), cursor.getString(1), cursor.getInt(2))

                // 리스트에 추가
                taskList.add(task)
            }
        }
        return taskList
    }

    // 할일 추가
    fun addTask(task: ToDoModel) {
        openDatabase()
        val cv = ContentValues()
        cv.put(TASK, task.task)
        cv.put(STATUS, 0)
        db!!.insert(TABLE_NAME, null, cv)
    }

    // 할일 상태 수정
    fun updateStatus(id: Int, status: Int) {
        openDatabase()
        val cv = ContentValues()
        cv.put(STATUS, status)
        db!!.update(TABLE_NAME, cv, "id=?", arrayOf(id.toString()))
    }

    // 할일 수정
    fun updateTask(id: Int, task: String) {
        openDatabase()
        val cv = ContentValues()
        cv.put(TASK, task)
        db!!.update(TABLE_NAME, cv, "id=?", arrayOf(id.toString()))
    }

    // 할일 삭제
    fun deleteTask(id: Int) {
        openDatabase()
        db!!.delete(TABLE_NAME, "id=?", arrayOf(id.toString()))
    }

}