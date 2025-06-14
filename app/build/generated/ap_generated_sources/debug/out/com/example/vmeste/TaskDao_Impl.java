package com.example.vmeste;

import android.database.Cursor;
import androidx.lifecycle.LiveData;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class TaskDao_Impl implements TaskDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<TaskDataModel> __insertionAdapterOfTaskDataModel;

  private final EntityDeletionOrUpdateAdapter<TaskDataModel> __deletionAdapterOfTaskDataModel;

  private final EntityDeletionOrUpdateAdapter<TaskDataModel> __updateAdapterOfTaskDataModel;

  public TaskDao_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfTaskDataModel = new EntityInsertionAdapter<TaskDataModel>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR ABORT INTO `tasks` (`id`,`title`,`description`,`is_completed`,`comments_count`,`importance`,`complexity`,`base_time`,`priority`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, TaskDataModel value) {
        stmt.bindLong(1, value.getId());
        if (value.getTitle() == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.getTitle());
        }
        if (value.getDescription() == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.getDescription());
        }
        final int _tmp = value.isCompleted() ? 1 : 0;
        stmt.bindLong(4, _tmp);
        stmt.bindLong(5, value.getCommentsCount());
        stmt.bindLong(6, value.getImportance());
        stmt.bindLong(7, value.getComplexity());
        stmt.bindLong(8, value.getBaseTime());
        stmt.bindDouble(9, value.getPriority());
      }
    };
    this.__deletionAdapterOfTaskDataModel = new EntityDeletionOrUpdateAdapter<TaskDataModel>(__db) {
      @Override
      public String createQuery() {
        return "DELETE FROM `tasks` WHERE `id` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, TaskDataModel value) {
        stmt.bindLong(1, value.getId());
      }
    };
    this.__updateAdapterOfTaskDataModel = new EntityDeletionOrUpdateAdapter<TaskDataModel>(__db) {
      @Override
      public String createQuery() {
        return "UPDATE OR ABORT `tasks` SET `id` = ?,`title` = ?,`description` = ?,`is_completed` = ?,`comments_count` = ?,`importance` = ?,`complexity` = ?,`base_time` = ?,`priority` = ? WHERE `id` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, TaskDataModel value) {
        stmt.bindLong(1, value.getId());
        if (value.getTitle() == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.getTitle());
        }
        if (value.getDescription() == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.getDescription());
        }
        final int _tmp = value.isCompleted() ? 1 : 0;
        stmt.bindLong(4, _tmp);
        stmt.bindLong(5, value.getCommentsCount());
        stmt.bindLong(6, value.getImportance());
        stmt.bindLong(7, value.getComplexity());
        stmt.bindLong(8, value.getBaseTime());
        stmt.bindDouble(9, value.getPriority());
        stmt.bindLong(10, value.getId());
      }
    };
  }

  @Override
  public void insert(final TaskDataModel task) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfTaskDataModel.insert(task);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void delete(final TaskDataModel task) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __deletionAdapterOfTaskDataModel.handle(task);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void update(final TaskDataModel task) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __updateAdapterOfTaskDataModel.handle(task);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public LiveData<List<TaskDataModel>> getAllTasks() {
    final String _sql = "SELECT * FROM tasks ORDER BY priority DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return __db.getInvalidationTracker().createLiveData(new String[]{"tasks"}, false, new Callable<List<TaskDataModel>>() {
      @Override
      public List<TaskDataModel> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfIsCompleted = CursorUtil.getColumnIndexOrThrow(_cursor, "is_completed");
          final int _cursorIndexOfCommentsCount = CursorUtil.getColumnIndexOrThrow(_cursor, "comments_count");
          final int _cursorIndexOfImportance = CursorUtil.getColumnIndexOrThrow(_cursor, "importance");
          final int _cursorIndexOfComplexity = CursorUtil.getColumnIndexOrThrow(_cursor, "complexity");
          final int _cursorIndexOfBaseTime = CursorUtil.getColumnIndexOrThrow(_cursor, "base_time");
          final int _cursorIndexOfPriority = CursorUtil.getColumnIndexOrThrow(_cursor, "priority");
          final List<TaskDataModel> _result = new ArrayList<TaskDataModel>(_cursor.getCount());
          while(_cursor.moveToNext()) {
            final TaskDataModel _item;
            final String _tmpTitle;
            if (_cursor.isNull(_cursorIndexOfTitle)) {
              _tmpTitle = null;
            } else {
              _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            }
            final String _tmpDescription;
            if (_cursor.isNull(_cursorIndexOfDescription)) {
              _tmpDescription = null;
            } else {
              _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            }
            _item = new TaskDataModel(_tmpTitle,_tmpDescription);
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            _item.setId(_tmpId);
            final boolean _tmpIsCompleted;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsCompleted);
            _tmpIsCompleted = _tmp != 0;
            _item.setCompleted(_tmpIsCompleted);
            final int _tmpCommentsCount;
            _tmpCommentsCount = _cursor.getInt(_cursorIndexOfCommentsCount);
            _item.setCommentsCount(_tmpCommentsCount);
            final int _tmpImportance;
            _tmpImportance = _cursor.getInt(_cursorIndexOfImportance);
            _item.setImportance(_tmpImportance);
            final int _tmpComplexity;
            _tmpComplexity = _cursor.getInt(_cursorIndexOfComplexity);
            _item.setComplexity(_tmpComplexity);
            final int _tmpBaseTime;
            _tmpBaseTime = _cursor.getInt(_cursorIndexOfBaseTime);
            _item.setBaseTime(_tmpBaseTime);
            final float _tmpPriority;
            _tmpPriority = _cursor.getFloat(_cursorIndexOfPriority);
            _item.setPriority(_tmpPriority);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public TaskDataModel getTaskById(final int taskId) {
    final String _sql = "SELECT * FROM tasks WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, taskId);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
      final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
      final int _cursorIndexOfIsCompleted = CursorUtil.getColumnIndexOrThrow(_cursor, "is_completed");
      final int _cursorIndexOfCommentsCount = CursorUtil.getColumnIndexOrThrow(_cursor, "comments_count");
      final int _cursorIndexOfImportance = CursorUtil.getColumnIndexOrThrow(_cursor, "importance");
      final int _cursorIndexOfComplexity = CursorUtil.getColumnIndexOrThrow(_cursor, "complexity");
      final int _cursorIndexOfBaseTime = CursorUtil.getColumnIndexOrThrow(_cursor, "base_time");
      final int _cursorIndexOfPriority = CursorUtil.getColumnIndexOrThrow(_cursor, "priority");
      final TaskDataModel _result;
      if(_cursor.moveToFirst()) {
        final String _tmpTitle;
        if (_cursor.isNull(_cursorIndexOfTitle)) {
          _tmpTitle = null;
        } else {
          _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
        }
        final String _tmpDescription;
        if (_cursor.isNull(_cursorIndexOfDescription)) {
          _tmpDescription = null;
        } else {
          _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
        }
        _result = new TaskDataModel(_tmpTitle,_tmpDescription);
        final int _tmpId;
        _tmpId = _cursor.getInt(_cursorIndexOfId);
        _result.setId(_tmpId);
        final boolean _tmpIsCompleted;
        final int _tmp;
        _tmp = _cursor.getInt(_cursorIndexOfIsCompleted);
        _tmpIsCompleted = _tmp != 0;
        _result.setCompleted(_tmpIsCompleted);
        final int _tmpCommentsCount;
        _tmpCommentsCount = _cursor.getInt(_cursorIndexOfCommentsCount);
        _result.setCommentsCount(_tmpCommentsCount);
        final int _tmpImportance;
        _tmpImportance = _cursor.getInt(_cursorIndexOfImportance);
        _result.setImportance(_tmpImportance);
        final int _tmpComplexity;
        _tmpComplexity = _cursor.getInt(_cursorIndexOfComplexity);
        _result.setComplexity(_tmpComplexity);
        final int _tmpBaseTime;
        _tmpBaseTime = _cursor.getInt(_cursorIndexOfBaseTime);
        _result.setBaseTime(_tmpBaseTime);
        final float _tmpPriority;
        _tmpPriority = _cursor.getFloat(_cursorIndexOfPriority);
        _result.setPriority(_tmpPriority);
      } else {
        _result = null;
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
