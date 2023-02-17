package com.example.taskmanager;

import static android.app.PendingIntent.FLAG_IMMUTABLE;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;


import com.example.taskmanager.Adapter.TaskAdapter;
import com.example.taskmanager.DailyNotification.NotificationReceiver;
import com.example.taskmanager.Model.TaskModels;
import com.example.taskmanager.Tasks.CreateNewTask;
import com.example.taskmanager.Tasks.DialogCloseListener;
import com.example.taskmanager.Tasks.RecyclerItemTouchHelper;
import com.example.taskmanager.Utility.DatabaseHandler;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;


// Main page functions

public class MainActivity extends AppCompatActivity implements DialogCloseListener {

    private RecyclerView tasksRecyclerView;
    private TaskAdapter taskAdapter;
    private FloatingActionButton fab;

    private List<TaskModels> taskList;
    private DatabaseHandler db;

    Switch notificationSwitch;
    private Object SharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Objects.requireNonNull(getSupportActionBar()).hide();

        db = new DatabaseHandler(this);
        db.openDatabase();

        tasksRecyclerView = findViewById(R.id.tasksRecyclerView);
        tasksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        taskAdapter = new TaskAdapter(db, this);
        tasksRecyclerView.setAdapter(taskAdapter);

        fab = findViewById(R.id.floatingActionButton);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new RecyclerItemTouchHelper(taskAdapter));
        itemTouchHelper.attachToRecyclerView(tasksRecyclerView);

        taskList = db.getAllTasks();
        Collections.reverse(taskList);
        taskAdapter.setTask(taskList);

        fab.setOnClickListener(view -> CreateNewTask.newInstance().show(getSupportFragmentManager(), CreateNewTask.TAG));

//Daily Notification function
        notificationSwitch = findViewById(R.id.notificationSwitch);

// Shared preferences used to remember state of switch.
        boolean value = false;
        final SharedPreferences sharedPreferences = getSharedPreferences("b", 0);

        value = sharedPreferences.getBoolean("b", value);
        notificationSwitch.setChecked(value);

        notificationSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b){
                sharedPreferences.edit().putBoolean("b", true).apply();
                prepAlarm();
            } else {
                sharedPreferences.edit().putBoolean("b", false).apply();
            }
        });

    }

// Function responsible for retrieving the list from the database and for sorting into correct order.
    @Override
    public void handleDialogClose(DialogInterface dialog){
        taskList = db.getAllTasks();
        Collections.reverse(taskList);
        taskAdapter.setTask(taskList);
        taskAdapter.notifyDataSetChanged();
    }

// Method for the daily notification function.
// Method that create the time for and the alarm instance.
    public void prepAlarm(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 21);
        calendar.set(Calendar.MINUTE, 00);
        calendar.set(Calendar.SECOND, 0);

        if (calendar.getTime().compareTo(new Date()) < 0) calendar.add(Calendar.DAY_OF_MONTH, 1);

        Intent intent = new Intent(getApplicationContext(), NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 200, intent, FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        if(alarmManager != null){
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        }

    }


}