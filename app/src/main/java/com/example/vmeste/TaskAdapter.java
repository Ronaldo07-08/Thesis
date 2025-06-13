package com.example.vmeste;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vmeste.api.ApiClient;
import com.example.vmeste.api.ChatCompletionRequest;
import com.example.vmeste.api.ChatCompletionResponse;
import com.example.vmeste.api.Message;
import com.example.vmeste.api.TogetherApi;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private final Context context;
    private List<TaskDataModel> tasks;
    private final TaskDao taskDao;
    private TogetherApi togetherApi;

    public TaskAdapter(Context context, List<TaskDataModel> tasks, TaskDao taskDao) {
        this.context = context;
        this.taskDao = taskDao;
        this.tasks = tasks != null ? tasks : new ArrayList<>();
        initializeApiClient();
    }

    private void initializeApiClient() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.together.xyz/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        togetherApi = retrofit.create(TogetherApi.class);
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        TaskDataModel task = tasks.get(position);
        holder.bind(task);
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public void setTasks(List<TaskDataModel> newTasks) {
        this.tasks = newTasks != null ? newTasks : new ArrayList<>();
        notifyDataSetChanged();
    }

    class TaskViewHolder extends RecyclerView.ViewHolder {
        private final CheckBox checkBox;
        private final Button taskButton;
        private final TextView commentsTextView;
        private final ImageButton deleteTask;
        private final ImageButton timeToTask;

        private final ImageButton pointerBtn;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.taskCheckbox);
            taskButton = itemView.findViewById(R.id.taskButton);
            commentsTextView = itemView.findViewById(R.id.commentsCount);
            deleteTask = itemView.findViewById(R.id.deleteTask);
            timeToTask = itemView.findViewById(R.id.timeToTask);
            pointerBtn = itemView.findViewById(R.id.otherToDoWTask);


            pointerBtn.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    TaskDataModel task = tasks.get(position);
                    showPriorityDialog(task);
                }
            });

            checkBox.setOnCheckedChangeListener(null);
        }

        private void setupClickListeners() {
            taskButton.setOnClickListener(v -> handleTaskClick());
            deleteTask.setOnClickListener(v -> handleDeleteClick());
            timeToTask.setOnClickListener(v -> handleTimeEstimationClick());
        }

        private void handleTaskClick() {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                TaskDataModel task = tasks.get(position);
                openTaskForEditing(task);
            }
        }

        private void handleDeleteClick() {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                TaskDataModel task = tasks.get(position);
                showDeleteConfirmationDialog(task, position);
            }
        }

        private void handleTimeEstimationClick() {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                TaskDataModel task = tasks.get(position);
                showTimeEstimationDialog(task);
            }
        }


        public void bind(TaskDataModel task) {
            // 1. Сначала удаляем старый listener
            checkBox.setOnCheckedChangeListener(null);

            // 2. Устанавливаем состояние чекбокса
            checkBox.setChecked(task.isCompleted());

            // 3. Устанавливаем новый listener
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && position < tasks.size()) {
                    TaskDataModel currentTask = tasks.get(position);
                    if (currentTask.isCompleted() != isChecked) {
                        currentTask.setCompleted(isChecked);
                        updateTaskInDatabase(currentTask);
                    }
                }
            });

            taskButton.setText(task.getTitle());
            checkBox.setChecked(task.isCompleted());
            commentsTextView.setText(getCommentIcons(task.getCommentsCount()));

            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                task.setCompleted(isChecked);
                updateTaskInDatabase(task);
            });
        }

        private void updateTaskInDatabase(TaskDataModel task) {
            AppDatabase.databaseWriteExecutor.execute(() -> {
                taskDao.update(task);
                ((Activity) context).runOnUiThread(() ->
                        notifyItemChanged(getAdapterPosition())
                );
            });
        }

        private void openTaskForEditing(TaskDataModel task) {
            Intent intent = new Intent(context, AddTaskActivity.class);
            intent.putExtra("task_id", task.getId());
            context.startActivity(intent);
        }

        private void showDeleteConfirmationDialog(TaskDataModel task, int position) {
            new AlertDialog.Builder(context)
                    .setTitle("Удаление задачи")
                    .setMessage("Вы уверены, что хотите удалить задачу \"" + task.getTitle() + "\"?")
                    .setPositiveButton("Удалить", (dialog, which) -> deleteTask(task, position))
                    .setNegativeButton("Отмена", null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }


        private void deleteTask(TaskDataModel task, int position) {
            AppDatabase.databaseWriteExecutor.execute(() -> {
                taskDao.delete(task);
                ((Activity) context).runOnUiThread(() -> {
                    tasks.remove(position);
                    notifyItemRemoved(position);
                });
            });
        }

        private void showTimeEstimationDialog(TaskDataModel task) {
            AlertDialog loadingDialog = createLoadingDialog();
            loadingDialog.show();

            estimateTaskTime(task.getTitle(), task.getDescription(), new TimeEstimationCallback() {
                @Override
                public void onEstimated(int baseTime, int complexity) {
                    loadingDialog.dismiss();
                    // Передаем timeToTask как anchorView
                    showTimeEstimationResult(timeToTask, task.getTitle(), baseTime, complexity);
                }

                @Override
                public void onError(String error) {
                    loadingDialog.dismiss();
                    showError(error);
                }
            });
        }

        private AlertDialog createLoadingDialog() {
            return new AlertDialog.Builder(context)
                    .setTitle("Оценка времени выполнения")
                    .setView(R.layout.dialog_loading)
                    .setCancelable(false)
                    .create();
        }

        private void showTimeEstimationResult(View anchorView, String taskTitle, int baseTime, int complexity) {
            // Получаем уровень навыка пользователя
            int skillLevel = UserSkills.getSkillLevel(context);
            double finalTime = calculateFinalTime(baseTime, complexity, skillLevel);

            // Создаем кастомное сообщение
            String message = String.format(Locale.getDefault(),
                    "       —  %.0f мин\n\nСложность задачи: %d/5",
                    finalTime, complexity);

            // Создаем кастомный диалог
            Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.custom_time_estimation_dialog); // Ваш кастомный layout

            // Настраиваем фон
            Window window = dialog.getWindow();
            if (window != null) {
                window.setBackgroundDrawableResource(R.drawable.dialog_background); // Ваша картинка для фона
                window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                // Позиционируем диалог возле кнопки
                int[] location = new int[2];
                anchorView.getLocationOnScreen(location);
                WindowManager.LayoutParams params = window.getAttributes();
                params.x = location[0]; // X координата
                params.y = location[1] + anchorView.getHeight(); // Y координата + высота кнопки
                params.gravity = Gravity.TOP | Gravity.START;
                window.setAttributes(params);
            }

            // Находим элементы в кастомном layout
            TextView messageText = dialog.findViewById(R.id.message_text);
            ImageButton closeButton = dialog.findViewById(R.id.close_button);

            // Устанавливаем текст сообщения
            messageText.setText(message);

            // Настраиваем кнопку закрытия
            closeButton.setOnClickListener(v -> dialog.dismiss());

            // Показываем диалог
            dialog.show();
        }

        private void showError(String error) {
            Toast.makeText(context, "Ошибка: " + error, Toast.LENGTH_LONG).show();
        }

        private double calculateFinalTime(int baseTime, int complexity, int skillLevel) {
            double complexityFactor = 0.5 + (complexity * 0.25);
            double skillMultiplier = getSkillMultiplier(skillLevel);
            return baseTime * (complexityFactor / skillMultiplier);
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

        private String getCommentIcons(int count) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < count; i++) {
                sb.append("💬 ");
            }
            return sb.toString();
        }

        private void estimateTaskTime(String title, String description, TimeEstimationCallback callback) {
            String prompt = createPrompt(title, description);
            ChatCompletionRequest request = createRequest(prompt);
            String apiKey = "Bearer " + context.getString(R.string.together_api_key);

            togetherApi.getChatCompletion(apiKey, request).enqueue(new Callback<ChatCompletionResponse>() {
                @Override
                public void onResponse(Call<ChatCompletionResponse> call, Response<ChatCompletionResponse> response) {
                    if (response.isSuccessful()) {
                        handleSuccessfulResponse(response, callback);
                    } else {
                        handleApiError(response, callback);
                    }
                }

                @Override
                public void onFailure(Call<ChatCompletionResponse> call, Throwable t) {
                    callback.onError("Ошибка соединения: " + t.getMessage());
                }
            });
        }

        private String createPrompt(String title, String description) {
            return "Оцени время выполнения задачи и ее сложность. " +
                    "Задача: " + title + "\nОписание: " + description + "\n\n" +
                    "Ответь строго в формате JSON: {\"base_time\": X, \"complexity\": Y} " +
                    "где X - время в минутах (только число), Y - сложность от 1 до 5 (только число)";
        }

        private ChatCompletionRequest createRequest(String prompt) {
            List<Message> apiMessages = new ArrayList<>();
            apiMessages.add(new Message("user", prompt));

            ChatCompletionRequest request = new ChatCompletionRequest();
            request.model = "deepseek-ai/DeepSeek-R1-Distill-Llama-70B-free";
            request.messages = apiMessages;
            return request;
        }

        private void handleSuccessfulResponse(Response<ChatCompletionResponse> response, TimeEstimationCallback callback) {
            String content = response.body().choices.get(0).message.content;
            String jsonString = extractJson(content);

            if (jsonString == null) {
                callback.onError("Не удалось извлечь JSON из ответа ИИ");
                return;
            }

            try {
                JSONObject json = new JSONObject(jsonString);
                int baseTime = json.getInt("base_time");
                int complexity = json.getInt("complexity");
                callback.onEstimated(baseTime, complexity);
            } catch (Exception e) {
                callback.onError("Неверный формат JSON: " + e.getMessage());
            }
        }

        private String extractJson(String text) {
            String regex = "\\{.*?\\}";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(text);
            if (matcher.find()) {
                return matcher.group(0);
            }
            return null;
        }


        private void handleApiError(Response<ChatCompletionResponse> response, TimeEstimationCallback callback) {
            String errorMsg = "Ошибка API: " + response.code();
            try {
                if (response.errorBody() != null) {
                    errorMsg += " - " + response.errorBody().string();
                }
            } catch (Exception e) {
                errorMsg += " - Неизвестная ошибка";
            }
            callback.onError(errorMsg);
        }
    }

    private void showPriorityDialog(TaskDataModel task) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_priority, null);
        builder.setView(dialogView);

        Spinner importanceSpinner = dialogView.findViewById(R.id.importanceSpinner);
        Button calculateButton = dialogView.findViewById(R.id.calculateButton);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                context,
                R.array.importance_levels,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        importanceSpinner.setAdapter(adapter);

        AlertDialog dialog = builder.create();

        calculateButton.setOnClickListener(v -> {
            int importance = importanceSpinner.getSelectedItemPosition() + 1;
            calculateTaskPriority(task, importance);
            dialog.dismiss();
        });

        dialog.show();
    }

    private void calculateTaskPriority(TaskDataModel task, int importance) {
        task.setImportance(importance);
        int skillLevel = UserSkills.getSkillLevel(context);

        if (task.getComplexity() == 1 && task.getBaseTime() == 30) {
            // Если сложность и время не установлены, запрашиваем у ИИ
            estimateTaskComplexityAndTime(task, skillLevel);
        } else {
            // Если уже установлены, просто пересчитываем
            task.calculatePriority(skillLevel);
            updateTaskInDatabase(task);
            Toast.makeText(context, "Приоритет обновлен", Toast.LENGTH_SHORT).show();
        }
    }

    interface TimeEstimationCallback {
        void onEstimated(int baseTime, int complexity);
        void onError(String error);
    }

    private void estimateTaskComplexityAndTime(TaskDataModel task, int skillLevel) {
        AlertDialog loadingDialog = new AlertDialog.Builder(context)
                .setTitle("Оценка параметров задачи")
                .setView(R.layout.dialog_loading) // Создайте этот layout или используйте свой
                .setCancelable(false)
                .create();
        loadingDialog.show();

        estimateTaskTime(task.getTitle(), task.getDescription(), new TimeEstimationCallback() {
            @Override
            public void onEstimated(int baseTime, int complexity) {
                task.setBaseTime(baseTime);
                task.setComplexity(complexity);
                task.calculatePriority(skillLevel);
                updateTaskInDatabase(task);
                loadingDialog.dismiss();
                Toast.makeText(context,
                        "Приоритет рассчитан: " + String.format("%.2f", task.getPriority()),
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error) {
                loadingDialog.dismiss();
                Toast.makeText(context, "Ошибка: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateTaskInDatabase(TaskDataModel task) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            taskDao.update(task);
            ((Activity) context).runOnUiThread(() ->
                    notifyItemChanged(tasks.indexOf(task))
            );
        });
    }


    private void estimateTaskTime(String title, String description, TimeEstimationCallback callback) {
        String prompt = "Оцени время выполнения задачи и ее сложность. " +
                "Задача: " + title + "\nОписание: " + description + "\n\n" +
                "Ответь строго в формате JSON: {\"base_time\": X, \"complexity\": Y} " +
                "где X - время в минутах (только число), Y - сложность от 1 до 5 (только число)";

        List<Message> apiMessages = new ArrayList<>();
        apiMessages.add(new Message("user", prompt));

        ChatCompletionRequest request = new ChatCompletionRequest();
        request.model = "deepseek-ai/DeepSeek-R1-Distill-Llama-70B-free";
        request.messages = apiMessages;
        String apiKey = "Bearer " + context.getString(R.string.together_api_key);

        togetherApi.getChatCompletion(apiKey, request).enqueue(new Callback<ChatCompletionResponse>() {
            @Override
            public void onResponse(Call<ChatCompletionResponse> call, Response<ChatCompletionResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String content = response.body().choices.get(0).message.content;
                        // Извлекаем JSON из ответа
                        String jsonString = content.substring(content.indexOf("{"), content.lastIndexOf("}") + 1);
                        JSONObject json = new JSONObject(jsonString);
                        int baseTime = json.getInt("base_time");
                        int complexity = json.getInt("complexity");
                        callback.onEstimated(baseTime, complexity);
                    } catch (Exception e) {
                        callback.onError("Ошибка парсинга ответа: " + e.getMessage());
                    }
                } else {
                    callback.onError("Ошибка API: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ChatCompletionResponse> call, Throwable t) {
                callback.onError("Ошибка соединения: " + t.getMessage());
            }
        });
    }
}
