package com.example.vmeste;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity(tableName = "tasks")
public class TaskDataModel {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "is_completed")
    private boolean isCompleted;

    @ColumnInfo(name = "comments_count") // Добавим аннотацию для ясности
    private int commentsCount;

    // Конструктор
    public TaskDataModel(String title, String description) {
        this.title = title;
        this.description = description;
        this.isCompleted = false;
        this.commentsCount = 0; // Изначально 0 комментариев
    }

    // Геттер для id
    public int getId() {
        return id;
    }

    // Сеттер для id
    public void setId(int id) {
        this.id = id;
    }

    // Геттер для названия задачи
    public String getTitle() {
        return title;
    }

    // Сеттер для названия задачи
    public void setTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Название задачи не может быть пустым");
        }
        this.title = title;
    }

    // Геттер для описания
    public String getDescription() {
        return description;
    }

    // Сеттер для описания (если нужно)
    public void setDescription(String description) {
        this.description = description;
    }

    // Геттер для статуса выполнения
    public boolean isCompleted() {
        return isCompleted;
    }

    // Сеттер для статуса выполнения
    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    // Геттер для количества комментариев
    public int getCommentsCount() {
        return commentsCount;
    }

    // Сеттер для количества комментариев
    public void setCommentsCount(int commentsCount) {
        this.commentsCount = commentsCount;
    }

    public boolean isNew() {
        return id == 0; // ID автоматически генерируется, поэтому 0 означает новую задачу
    }

    // Метод для добавления комментария
    public void addComment() {
        commentsCount++;
    }

    // Метод для удаления комментария
    public void removeComment() {
        if (commentsCount > 0) {
            commentsCount--;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskDataModel task = (TaskDataModel) o;
        return id == task.id &&
                isCompleted == task.isCompleted &&
                Objects.equals(title, task.title) &&
                Objects.equals(description, task.description);
    }
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
