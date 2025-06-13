package com.example.vmeste;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {TaskDataModel.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {
    public abstract TaskDao taskDao();

    private static volatile AppDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // 1. Добавляем столбец 'isCompleted' в старую таблицу 'tasks'.
            database.execSQL("ALTER TABLE tasks ADD COLUMN isCompleted INTEGER NOT NULL DEFAULT 0");

            // 2. Добавляем столбец 'commentsCount' в старую таблицу 'tasks'.
            database.execSQL("ALTER TABLE tasks ADD COLUMN commentsCount INTEGER NOT NULL DEFAULT 0");

            // 3. Создаем новую таблицу 'tasks_new' с правильной структурой.
            database.execSQL("CREATE TABLE tasks_new (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "title TEXT, " +
                    "description TEXT, " +
                    "is_completed INTEGER NOT NULL DEFAULT 0, " +
                    "comments_count INTEGER NOT NULL DEFAULT 0, " +
                    "importance INTEGER NOT NULL DEFAULT 1, " +
                    "complexity INTEGER NOT NULL DEFAULT 1, " +
                    "base_time INTEGER NOT NULL DEFAULT 30, " +
                    "priority REAL NOT NULL DEFAULT 0)");

            // 4. Копируем данные из старой таблицы 'tasks' в новую 'tasks_new'.
            database.execSQL("INSERT INTO tasks_new (id, title, description, is_completed, comments_count) " +
                    "SELECT id, title, description, isCompleted, commentsCount FROM tasks");

            // 5. Удаляем старую таблицу 'tasks'.
            database.execSQL("DROP TABLE tasks");

            // 6. Переименовываем новую таблицу 'tasks_new' в 'tasks'.
            database.execSQL("ALTER TABLE tasks_new RENAME TO tasks");
        }
    };



    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "task_database")
                            .addMigrations(MIGRATION_1_2)
                            .fallbackToDestructiveMigration() // На случай ошибок
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
