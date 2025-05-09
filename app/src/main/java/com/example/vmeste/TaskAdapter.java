package com.example.vmeste;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private List<TaskDataModel> tasks;
    private final TaskDao taskDao;

    private static final DiffUtil.ItemCallback<TaskDataModel> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<TaskDataModel>() {
                @Override
                public boolean areItemsTheSame(@NonNull TaskDataModel oldItem, @NonNull TaskDataModel newItem) {
                    return oldItem.getId() == newItem.getId();
                }

                @Override
                public boolean areContentsTheSame(@NonNull TaskDataModel oldItem, @NonNull TaskDataModel newItem) {
                    return oldItem.equals(newItem);
                }
            };

    public TaskAdapter(List<TaskDataModel> tasks, TaskDao taskDao) {
        this.taskDao = taskDao;
        this.tasks = tasks != null ? tasks : new ArrayList<>();
        setHasStableIds(false);
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false); // Используем правильный layout
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        TaskDataModel task = tasks.get(position);
        holder.bind(task);
        holder.checkBox.setOnCheckedChangeListener(null); // Сначала удаляем старый listener
        holder.checkBox.setChecked(task.isCompleted()); // Устанавливаем состояние
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            task.setCompleted(isChecked);

            // Обновляем задачу в БД в фоновом потоке
            AppDatabase.databaseWriteExecutor.execute(()        -> {
                taskDao.update(task);

                // Если нужно обновить UI после изменения (опционально)
                ((Activity) holder.itemView.getContext()).runOnUiThread(() -> {
                    notifyItemChanged(position); // Перерисовываем элемент
                });
            });
        });
    }

    @Override
    public int getItemCount() {
        return tasks == null ? 0 : tasks.size();
    }

    public void setTasks(List<TaskDataModel> newTasks) {
        this.tasks = newTasks != null ? newTasks : new ArrayList<>();
        notifyDataSetChanged();
    }

    private static class TaskDiffCallback extends DiffUtil.Callback {
        private final List<TaskDataModel> oldTasks;
        private final List<TaskDataModel> newTasks;

        public TaskDiffCallback(List<TaskDataModel> oldTasks, List<TaskDataModel> newTasks) {
            this.oldTasks = oldTasks;
            this.newTasks = newTasks;
        }

        @Override
        public int getOldListSize() {
            return oldTasks.size();
        }

        @Override
        public int getNewListSize() {
            return newTasks.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return oldTasks.get(oldItemPosition).getId() == newTasks.get(newItemPosition).getId();
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            TaskDataModel oldTask = oldTasks.get(oldItemPosition);
            TaskDataModel newTask = newTasks.get(newItemPosition);

            return oldTask.isCompleted() == newTask.isCompleted() &&
                        Objects.equals(oldTask.getTitle(), newTask.getTitle()) &&
                    Objects.equals(oldTask.getDescription(), newTask.getDescription());
        }
    }

    class TaskViewHolder extends RecyclerView.ViewHolder {
        private final CheckBox checkBox;
        private final Button taskButton;
        private final TextView commentsTextView;
        private final ImageButton deleteTask;
        private final Context context;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            context = itemView.getContext();
            checkBox = itemView.findViewById(R.id.taskCheckbox);
            taskButton = itemView.findViewById(R.id.taskButton);
            commentsTextView = itemView.findViewById(R.id.commentsCount);
            deleteTask = itemView.findViewById(R.id.deleteTask);

            // Обработчик клика по всей задаче
            taskButton.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    TaskDataModel task = tasks.get(position);
                    openTaskForEditing(task);
                }
            });

            deleteTask.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    TaskDataModel task = tasks.get(position);
                    showDeleteConfirmationDialog(task, position);
                }
            });
        }

        public void bind(TaskDataModel task) {
            checkBox.setOnCheckedChangeListener(null);

            taskButton.setText(task.getTitle());
            checkBox.setChecked(task.isCompleted());
            commentsTextView.setText(getCommentIcons(task.getCommentsCount()));

            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                task.setCompleted(isChecked);
                new Thread(() -> taskDao.update(task)).start();
            });
        }

        private void openTaskForEditing(TaskDataModel task) {
            Context context = itemView.getContext();
            Intent intent = new Intent(context, AddTaskActivity.class);
            intent.putExtra("task_id", task.getId());
            context.startActivity(intent);
        }

        private void showDeleteConfirmationDialog(TaskDataModel task, int position) {
            new AlertDialog.Builder(context)
                    .setTitle("Удаление задачи")
                    .setMessage("Вы уверены, что хотите удалить задачу \"" + task.getTitle() + "\"?")
                    .setPositiveButton("Удалить", (dialog, which) -> {
                        // Удаляем из базы данных
                        new Thread(() -> {
                            taskDao.delete(task);
                            // Удаляем из списка и обновляем UI на главном потоке
                            ((Activity) context).runOnUiThread(() -> {
                                tasks.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, tasks.size());
                            });
                        }).start();
                    })
                    .setNegativeButton("Отмена", null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }

        private String getCommentIcons(int count) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < count; i++) {
                sb.append("💬 ");
            }
            return sb.toString();
        }
    }
}