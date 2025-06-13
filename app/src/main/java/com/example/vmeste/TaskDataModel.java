package com.example.vmeste;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

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

    @ColumnInfo(name = "comments_count")
    private int commentsCount;

    @ColumnInfo(name = "importance")
    private int importance = 1;

    @ColumnInfo(name = "complexity")
    private int complexity = 1;

    @ColumnInfo(name = "base_time")
    private int baseTime = 30;

    @ColumnInfo(name = "priority")
    private float priority = 0;

    public TaskDataModel(String title, String description) {
        this.title = title;
        this.description = description;
    }

    // Геттеры и сеттеры
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isCompleted() { return isCompleted; }
    public void setCompleted(boolean completed) { isCompleted = completed; }

    public int getCommentsCount() { return commentsCount; }
    public void setCommentsCount(int commentsCount) { this.commentsCount = commentsCount; }

    public int getImportance() { return importance; }
    public void setImportance(int importance) {
        this.importance = Math.max(1, Math.min(3, importance));
    }

    public int getComplexity() { return complexity; }
    public void setComplexity(int complexity) {
        this.complexity = Math.max(1, Math.min(5, complexity));
    }

    public int getBaseTime() { return baseTime; }
    public void setBaseTime(int baseTime) { this.baseTime = baseTime; }

    public float getPriority() { return priority; }
    public void setPriority(float priority) { this.priority = priority; }

    // Метод для расчета приоритета
    public void calculatePriority(int userSkillLevel) {
        double complexityFactor = 0.5 + (complexity * 0.25);
        double skillMultiplier = getSkillMultiplier(userSkillLevel);
        double finalTime = baseTime * (complexityFactor / skillMultiplier);
        this.priority = (float) (importance / (complexity * 0.5 + finalTime * 0.2));
    }

    private double getSkillMultiplier(int skillLevel) {
        switch (skillLevel) {
            case 1: return 0.333;
            case 2: return 0.5;
            case 3: return 1.0;
            case 4: return 1.428;
            case 5: return 2.0;
            default: return 1.0;
        }
    }
}
