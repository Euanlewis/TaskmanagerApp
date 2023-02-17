package com.example.taskmanager.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.recyclerview.widget.RecyclerView;

import com.example.taskmanager.Tasks.CreateNewTask;
import com.example.taskmanager.MainActivity;
import com.example.taskmanager.Model.TaskModels;
import com.example.taskmanager.R;
import com.example.taskmanager.Utility.DatabaseHandler;

import java.util.List;

// Function responsible for creating and calling layout of new tasks.

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder>{
    private List<TaskModels> taskList;
    private MainActivity activity;
    private DatabaseHandler db;

    public TaskAdapter(DatabaseHandler db, MainActivity activity){
        this.db = db;
        this.activity = activity;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_layout, parent, false);
        return new ViewHolder(itemView);
    }

    public void onBindViewHolder(ViewHolder holder, int position){
        db.openDatabase();
        TaskModels item = taskList.get(position);
        holder.task.setText(item.getTask());
        holder.task.setChecked(intToBoolean(item.getStatus()));
        holder.task.setOnCheckedChangeListener((compoundButton, b) -> {
            if(b){
                db.updateStatus(item.getId(), 1);
            }else{
                db.updateStatus(item.getId(), 0);
            }
        });
    }

    public int getItemCount (){
        return taskList.size();
    }

    private boolean intToBoolean (int x){
        return x != 0;
    }

    public void setTask(List<TaskModels> taskList){
        this.taskList = taskList;
        notifyDataSetChanged();
    }

    public void deleteItem(int position){
        TaskModels item = taskList.get(position);
        db.deleteTask(item.getId());
        taskList.remove(position);
        notifyItemRemoved(position);
    }

    public void editItem(int position){
        TaskModels item = taskList.get(position);
        Bundle bundle = new Bundle();
        bundle.putInt("id", item.getId());
        bundle.putString("task", item.getTask());
        CreateNewTask fragment = new CreateNewTask();
        fragment.setArguments(bundle);
        fragment.show(activity.getSupportFragmentManager(), CreateNewTask.TAG);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        CheckBox task;
        ViewHolder(View view){
            super(view);
            task = view.findViewById(R.id.taskCheckBox);
        }
    }

    public Context getContext(){
        return activity;
    }
}
