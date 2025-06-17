package com.example.vmeste;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class DoneTasksActivity extends BaseActivity {
    private RecyclerView completedTasksRecyclerView;
    private TaskAdapter adapter;
    private TaskDao taskDao;
    private ImageButton pointerButton;

    @Override
    protected int getLayoutResource() {
        return R.layout.done_tasks;
    }

    @Override
    protected void highlightCurrentButton() {
        tasksBtn.setBackgroundResource(R.drawable.rect);
        menuBtn.setImageResource(R.drawable.menucurr);
        homeBtn.setBackgroundResource(R.drawable.rect);
        menuBtn.setBackgroundResource(R.drawable.rectorange);
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

        pointerButton = findViewById(R.id.pointer);

        pointerButton.setOnClickListener(v -> {
            // Завершаем текущую активность и возвращаемся назад
            finish();
        });

        AppDatabase db = AppDatabase.getDatabase(this);
        taskDao = db.taskDao();

        completedTasksRecyclerView = findViewById(R.id.completedTasksRecyclerView);
        completedTasksRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new TaskAdapter(this, new ArrayList<>(), taskDao);
        completedTasksRecyclerView.setAdapter(adapter);

        loadCompletedTasks();
    }

    private void loadCompletedTasks() {
        taskDao.getCompletedTasks().observe(this, tasks -> {
            List<TaskDataModel> completedTasks = new ArrayList<>();
            if (tasks != null) {
                for (TaskDataModel task : tasks) {
                    if (task.isCompleted()) {
                        completedTasks.add(task);
                    }
                }
            }
            adapter.setTasks(completedTasks);
        });
    }
}
