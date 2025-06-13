package com.example.vmeste;


import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

public class AddTaskActivity extends AppCompatActivity {
    private TextInputEditText titleEditText;
    private TextInputEditText descriptionEditText;
    private int taskId = -1; // -1 означает новую задачу

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_task);

        titleEditText = findViewById(R.id.titleEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);

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
