package com.example.vmeste;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface TaskDao {
    @Insert
    void insert(TaskDataModel task);

    @Update
    void update(TaskDataModel task);

    @Delete
    void delete(TaskDataModel task);

    @Query("SELECT * FROM tasks ORDER BY priority DESC")
    LiveData<List<TaskDataModel>> getAllTasks();

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    TaskDataModel getTaskById(int taskId);
}
