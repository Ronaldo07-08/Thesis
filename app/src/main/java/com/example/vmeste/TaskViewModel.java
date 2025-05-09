package com.example.vmeste;


import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class TaskViewModel extends AndroidViewModel {
    private final TaskDao taskDao;
    private final LiveData<List<TaskDataModel>> allTasks;

    public TaskViewModel(Application application) {
        super(application);
        AppDatabase db = AppDatabase.getDatabase(application);
        taskDao = db.taskDao();
        allTasks = taskDao.getAllTasks();
    }

    public LiveData<List<TaskDataModel>> getAllTasks() {
        return allTasks;
    }

    public TaskDao getTaskDao() {
        return taskDao;
    }

    public void insert(TaskDataModel task) {
        AppDatabase.databaseWriteExecutor.execute(() -> taskDao.insert(task));
    }
}
