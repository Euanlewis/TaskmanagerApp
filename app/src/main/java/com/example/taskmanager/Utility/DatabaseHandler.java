package com.example.taskmanager.Utility;


import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import com.example.taskmanager.Model.TaskModels;

import java.net.Inet4Address;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

// Class that creates and handles the SQL database for storing and editing the tasks.

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String NAME = "taskListDatabase.db";
    private static final String TASK_TABLE = "task";
    private static final String ID = "id";
    private static final String TASK = "task";
    private static final String STATUS = "status";
    private static final String PRIORITY = "priorityRating";


    private SQLiteDatabase db;
    private SQLiteOpenHelper helper;

    public DatabaseHandler(Context context){
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        String createTable = " CREATE TABLE " + TASK_TABLE + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + TASK + " TEXT, " + STATUS + " INTEGER, " + PRIORITY + " INTEGER)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade (SQLiteDatabase db, int oldVer, int newVer){
        // Checks for existing table.
        db.execSQL(" DROP TABLE IF EXISTS " + TASK_TABLE);
        onCreate(db);
    }

    public void openDatabase(){
        db = this.getWritableDatabase();
    }

    public void insertTask(TaskModels task){
        int maxID = 0;
        SQLiteDatabase db2 = this.getWritableDatabase();
        Cursor cur4 = db2.rawQuery("SELECT MAX(id) FROM task", null);
        if(cur4.moveToFirst()){
            do {
                maxID = cur4.getInt(0);
            }while (cur4.moveToNext());
        }
        cur4.close();

        ContentValues cv = new ContentValues();
        cv.put(ID, (maxID + 1));
        cv.put(TASK, task.getTask());
        cv.put(STATUS, 0);
        cv.put(PRIORITY, 0);
        db.insert(TASK_TABLE, null, cv);
    }


// Function that get tasks to send to main activity.
    public List<TaskModels> getAllTasks() {
        List<TaskModels> taskList = new ArrayList<>();
        Cursor cur = null;
        db.beginTransaction();
        try {
            cur = db.query(TASK_TABLE, null, null, null, null, null, null, null);
            if (cur != null) {
                if (cur.moveToFirst()) {
                    do {
                        TaskModels task = new TaskModels();
                        task.setId(cur.getInt(cur.getColumnIndex(ID)));
                        task.setTask(cur.getString(cur.getColumnIndex(TASK)));
                        task.setStatus(cur.getInt(cur.getColumnIndex(STATUS)));
                        taskList.add(task);
                    } while (cur.moveToNext());
                }
            }
        } finally {
            db.endTransaction();
            cur.close();
        }

        return taskList;
    }

    public void updateStatus(int id, int status){
        ContentValues cv = new ContentValues();
        cv.put(STATUS, status);
        db.update(TASK_TABLE, cv, ID + "=?", new String[] {String.valueOf(id)});

    }

    public void updateTask(int id, String task){
        ContentValues cv = new ContentValues();
        cv.put(TASK, task);
        db.update(TASK_TABLE, cv, ID + "=?", new String[] {String.valueOf(id)});
    }

    public void deleteTask(int id){
        openDatabase();
        db.delete(TASK_TABLE, ID + "=?", new String[] {String.valueOf(id)});
    }


// Function to take entries in task field and return them for AI function
    public ArrayList<String> getTasksInDb(Context context, String task) {
        ArrayList<String> tasksInDb = new ArrayList<>();

        db = context.openOrCreateDatabase("taskListDatabase.db", Context.MODE_PRIVATE, null);
        Cursor cur2 = db.rawQuery("SELECT " + task + " FROM task", null);


        if (cur2.moveToFirst()) {
            do {
                String stringHolder = cur2.getString(cur2.getColumnIndex(task));
                tasksInDb.add(stringHolder);
            } while (cur2.moveToNext());
        }

        cur2.close();
        db.close();

        return tasksInDb;
    }

// Function to add priority rating to list of tasks.
    public void insertPriorityRating(ArrayList<Integer> arrayOfPR, SQLiteDatabase db, String tableName, String columnName){

        //Get current values in column
        Cursor cur3 = db.query(tableName, new String[] {columnName}, null, null, null, null, null);
        int colIndex = cur3.getColumnIndex(columnName);
        ArrayList<Integer> curVals = new ArrayList<>();
        while (cur3.moveToNext()){
            curVals.add(cur3.getInt(colIndex));
        }
        cur3.close();

        //Update column with int array
        int valIndex = 0;
        String [] columns = new String[]{"id", columnName};
        cur3 = db.query(tableName, columns, null, null, null, null, null);
        while (cur3.moveToNext()){
            int rowId = cur3.getInt(cur3.getColumnIndex("id"));
            int currentVal = cur3.getInt(cur3.getColumnIndex(columnName));
            int newVal = arrayOfPR.get(valIndex);

            ContentValues cv = new ContentValues();
            cv.put(columnName, newVal);

            db.update(tableName, cv, "id IN (SELECT id FROM " + tableName + " WHERE " + columnName + " = ? LIMIT 1)", new String[] {String.valueOf(currentVal)});
            valIndex = (valIndex + 1) % arrayOfPR.size();
        }
    }

    // Method that orders the tasklist after the priority function
    public void sortTasksAfterPFunction(SQLiteDatabase db, String tableName, String columnName){
        String sortOrder = columnName + " ASC";
        String orderBy = "ORDER BY " + sortOrder;

        db.execSQL("CREATE TEMPORARY TABLE tmp_table AS SELECT * FROM " + tableName + " " + orderBy);
        db.execSQL("DROP TABLE " + tableName);
        db.execSQL("CREATE TABLE " + tableName + " AS SELECT * FROM tmp_table");
        db.execSQL("DROP TABLE tmp_table");
    }

}
