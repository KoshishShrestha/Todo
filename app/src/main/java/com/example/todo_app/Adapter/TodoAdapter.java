package com.example.todo_app.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todo_app.AddNewTask;
import com.example.todo_app.MainActivity;
import com.example.todo_app.Model.TodoModel;
import com.example.todo_app.R;
import com.example.todo_app.Utils.DatabaseHandler;

import java.util.List;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.ViewHolder> {

    private List<TodoModel> todoList;
    private final MainActivity activity;
    private final DatabaseHandler db;

    public TodoAdapter(DatabaseHandler db, MainActivity activity){
        this.db = db;
        this.activity = activity;
    }

    @NonNull
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
         View itemView = LayoutInflater.from(parent.getContext())
                 .inflate(R.layout.task_layout, parent, false);
         return new ViewHolder(itemView);
    }

    public void onBindViewHolder(ViewHolder holder,int position){
        db.openDatabase();
        TodoModel item = todoList.get(position);
        holder.task.setText(item.getTask());
        holder.task.setChecked(toBoolean(item.getStatus()));
        holder.task.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                db.updateStatus(item.getId(), 1);
            }
            else{
                db.updateStatus(item.getId(), 0);
            }
        });
    }

    public int getItemCount(){
        return todoList.size();
    }

    private boolean toBoolean(int n){
        return n!=0;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setTasks(List<TodoModel> todoList){
        this.todoList = todoList;
        notifyDataSetChanged();
    }

    public Context getContext(){
        return activity;
    }

    public void deleteItem(int position){
        TodoModel item = todoList.get(position);
        db.deleteTask(item.getId());
        todoList.remove(position);
        notifyItemRemoved(position);
    }

    public void editItem(int position){
        TodoModel item = todoList.get(position);
        Bundle bundle = new Bundle();
        bundle.putInt("id",item.getId());
        bundle.putString("task",item.getTask());
        AddNewTask fragment = new AddNewTask();
        fragment.setArguments(bundle);
        fragment.show(activity.getSupportFragmentManager(), AddNewTask.TAG);
    }
    public static class ViewHolder extends RecyclerView.ViewHolder{
        CheckBox task;

        ViewHolder(View view){
            super(view);
            task = view.findViewById(R.id.todoCheckBox);
        }

    }
}
