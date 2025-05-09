package com.example.vmeste;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends BaseActivity {
    private static final int ADD_TASK_REQUEST = 1;
    private TaskViewModel taskViewModel;
    private TaskAdapter adapter;
    private TextView tasksCountTextView;
    private TextView dateTextView;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_main;
    }

    @Override
    protected void highlightCurrentButton() {
        homeBtn.setBackgroundResource(R.drawable.rectorange);
        homeBtn.setImageResource(R.drawable.homecurr);
        tasksBtn.setBackgroundResource(R.drawable.rect);
        tasksBtn.setImageResource(R.drawable.tasks);
        menuBtn.setBackgroundResource(R.drawable.rect);
        menuBtn.setImageResource(R.drawable.menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);

        // Инициализация RecyclerView
        RecyclerView tasksRecyclerView = findViewById(R.id.tasksRecyclerView);
        tasksRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Инициализация адаптера с передачей taskDao
        adapter = new TaskAdapter(taskViewModel.getAllTasks().getValue(), taskViewModel.getTaskDao());
        tasksRecyclerView.setAdapter(adapter);

        // Подписка на изменения LiveData
        taskViewModel.getAllTasks().observe(this, tasks -> {
            adapter.setTasks(tasks);
            updateTasksCount(tasks.size());
        });

        // Инициализация остальных элементов
        tasksCountTextView = findViewById(R.id.tasksToday);
        dateTextView = findViewById(R.id.CurrDate);
        ImageButton addTaskBtn = findViewById(R.id.addTaskButton);

        setCurrentDate();

        addTaskBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddTaskActivity.class);
            startActivityForResult(intent, ADD_TASK_REQUEST);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_TASK_REQUEST && resultCode == RESULT_OK && data != null) {
            String title = data.getStringExtra("title");
            String description = data.getStringExtra("description");

            TaskDataModel newTask = new TaskDataModel(title, description);
            taskViewModel.insert(newTask);
        }
    }

    private void setCurrentDate() {
        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM", new Locale("ru"));
        dateTextView.setText(dateFormat.format(currentDate));
    }

    private void updateTasksCount(int count) {
        tasksCountTextView.setText("Задач: " + count);
    }
}