package com.example.vmeste;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddTaskActivity extends AppCompatActivity {
    private TextInputEditText titleEditText;
    private TextInputEditText descriptionEditText;
    private int taskId = -1; // -1 означает новую задачу
    private String taskDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_task);

        titleEditText = findViewById(R.id.titleEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);

        // Получаем дату из интента (если передана)
        if (getIntent().hasExtra("task_date")) {
            taskDate = getIntent().getStringExtra("task_date");
        } else {
            // Устанавливаем текущую дату по умолчанию
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            taskDate = sdf.format(new Date());
        }

        // Проверяем, переданы ли данные задачи для редактирования
        if (getIntent().hasExtra("task_id")) {
            taskId = getIntent().getIntExtra("task_id", -1);
            loadTaskData(taskId);
        }

        ImageButton pointerEllipse = findViewById(R.id.pointerEllipse);
        pointerEllipse.setOnClickListener(v -> finish());

        ImageButton saveBtn = findViewById(R.id.saveButton);
        saveBtn.setOnClickListener(v -> saveTask());
    }

    private void loadTaskData(int taskId) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            TaskDataModel task = AppDatabase.getDatabase(this).taskDao().getTaskById(taskId);
            runOnUiThread(() -> {
                if (task != null) {
                    titleEditText.setText(task.getTitle());
                    descriptionEditText.setText(task.getDescription());
                    taskDate = task.getDate();
                }
            });
        });
    }

    private void saveTask() {
        String title = titleEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();

        if (title.isEmpty()) {
            Toast.makeText(this, "Введите название задачи", Toast.LENGTH_SHORT).show();
            return;
        }

        TaskDataModel task = new TaskDataModel(title, description);
        task.setDate(taskDate); // Устанавливаем дату задачи

        if (taskId != -1) {
            task.setId(taskId); // Для обновления существующей задачи
        }

        AppDatabase.databaseWriteExecutor.execute(() -> {
            if (taskId == -1) {
                AppDatabase.getDatabase(this).taskDao().insert(task);
            } else {
                AppDatabase.getDatabase(this).taskDao().update(task);
            }
            finish();
        });
    }
}