package com.example.todo_app;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todo_app.Adapter.TodoAdapter;
import com.example.todo_app.Model.TodoModel;
import com.example.todo_app.Utils.DatabaseHandler;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements DialogCloseListener {
    private DatabaseHandler db;

    private TodoAdapter tasksAdapter;

    private List<TodoModel> taskList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DatabaseHandler(this);
        db.openDatabase();

        taskList = new ArrayList<>();

        RecyclerView tasksRecyclerView = findViewById(R.id.tasksRecyclerView);
        tasksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        tasksAdapter = new TodoAdapter(db, this);
        tasksRecyclerView.setAdapter(tasksAdapter);

        FloatingActionButton fab = findViewById(R.id.fab);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new RecyclerItemTouchHelper(tasksAdapter));
        itemTouchHelper.attachToRecyclerView(tasksRecyclerView);

        taskList = db.getAllTasks();
        Collections.reverse(taskList);
        tasksAdapter.setTasks(taskList);

        fab.setOnClickListener(v -> AddNewTask.newInstance().show(getSupportFragmentManager(), AddNewTask.TAG));
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void handleDialogClose(DialogInterface dialog) {
        refreshTaskList();
    }

    private void refreshTaskList() {
        taskList.clear();
        taskList.addAll(db.getAllTasks());
        Collections.reverse(taskList);
        tasksAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.main_menu_add_task) {
            // Open dialog or perform any action to add a new task
            AddNewTask.newInstance().show(getSupportFragmentManager(), AddNewTask.TAG);
            return true;
        } else if (itemId == R.id.main_menu_clear_all) {
            // Handle the clear all tasks menu item
            showDeleteConfirmationDialog();
            return true;
        } else if (itemId == R.id.main_menu_clear_all_completed) {
            // Handle the clear all completed tasks menu item
            showDeleteCompletedConfirmationDialog();
            return true;
        } else if (itemId == R.id.main_menu_logout) {
            // Handle the logout menu item
            SharedPreferences sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
            sharedPreferences.edit().clear().apply();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Tasks");
        builder.setMessage("Are you sure you want to delete all tasks?");
        builder.setPositiveButton("Delete", (dialog, which) -> {
            db.deleteAllTasks();
            refreshTaskList();
        });
        builder.setNegativeButton("Cancel", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void showDeleteCompletedConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Completed Tasks");
        builder.setMessage("Are you sure you want to delete all completed tasks?");
        builder.setPositiveButton("Delete", (dialog, which) -> {
            db.deleteCompletedTasks();
            refreshTaskList();
        });
        builder.setNegativeButton("Cancel", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}