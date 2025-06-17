package com.example.vmeste;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(Color.TRANSPARENT);
            getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            );
        }

        AppPreferences appPrefs = new AppPreferences(this);
        if (appPrefs.isFirstRun()) {
            appPrefs.setFirstRun(false);
            startActivity(new Intent(this, ChatBotActivity.class));
            finish();
            return;
        }

        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);

        RecyclerView tasksRecyclerView = findViewById(R.id.tasksRecyclerView);
        tasksRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new TaskAdapter(this, new ArrayList<>(), taskViewModel.getTaskDao());
        tasksRecyclerView.setAdapter(adapter);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String today = sdf.format(new Date());

        taskViewModel.getTasksByDate(today).observe(this, tasks -> {
            adapter.setTasks(tasks != null ? tasks : new ArrayList<>());
            updateTasksCount(tasks != null ? tasks.size() : 0);
        });

        tasksCountTextView = findViewById(R.id.tasksToday);
        dateTextView = findViewById(R.id.CurrDate);
        ImageButton addTaskBtn = findViewById(R.id.addTaskButton);

        setCurrentDate();

        addTaskBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddTaskActivity.class);
            // Устанавливаем дату на сегодня по умолчанию
            intent.putExtra("task_date", today);
            startActivityForResult(intent, ADD_TASK_REQUEST);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_TASK_REQUEST && resultCode == RESULT_OK && data != null) {
            String title = data.getStringExtra("title");
            String description = data.getStringExtra("description");

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String today = sdf.format(new Date());

            TaskDataModel newTask = new TaskDataModel(title, description);
            newTask.setDate(today);
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
