package com.example.vmeste;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vmeste.api.ApiClient;
import com.example.vmeste.api.ChatCompletionRequest;
import com.example.vmeste.api.ChatCompletionResponse;
import com.example.vmeste.api.TogetherApi;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {

    private TogetherApi togetherApi;
    private EditText messageEditText;
    private Button sendButton;
    private RecyclerView messageRecyclerView;
    private MessageAdapter messageAdapter;
    private List<String> messagesList = new ArrayList<>();

    public class Message {
        public String role;
        public String content;

        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }

        public Message() {}
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat); // Убедитесь, что используется правильный layout!

        messageRecyclerView = findViewById(R.id.recycler_view_messages); // Добавьте эту строку
        messageEditText = findViewById(R.id.edit_text_message);
        sendButton = findViewById(R.id.button_send);

        messageEditText = findViewById(R.id.edit_text_message);
        sendButton = findViewById(R.id.button_send);
        messageRecyclerView = findViewById(R.id.recycler_view_messages);

        togetherApi = ApiClient.getInstance().getApi();

        messageRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        messageAdapter = new MessageAdapter(messagesList);
        messageRecyclerView.setAdapter(messageAdapter);

        sendButton.setOnClickListener(v -> {
            String userMessage = messageEditText.getText().toString();
            if (!userMessage.isEmpty()) {
                // Добавляем сообщение пользователя
                messagesList.add("User: " + userMessage);
                messageAdapter.notifyItemInserted(messagesList.size() - 1);
                messageRecyclerView.scrollToPosition(messagesList.size() - 1);
                messageEditText.setText("");

                // Создаем и отправляем запрос к API
                List<Message> messages = new ArrayList<>();
                messages.add(new Message("user", userMessage));

                ChatCompletionRequest request = new ChatCompletionRequest();
                request.model = "deepseek-ai/DeepSeek-R1-Distill-Llama-70B-free";
                request.messages = messages;
                String apiKey = "Bearer " + getString(R.string.together_api_key); // Добавьте "Bearer "
                Call<ChatCompletionResponse> call = togetherApi.getChatCompletion(apiKey, request);
                call.enqueue(new Callback<ChatCompletionResponse>() {
                    @Override
                    public void onResponse(Call<ChatCompletionResponse> call, Response<ChatCompletionResponse> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().choices != null && !response.body().choices.isEmpty()) {
                            // Получаем ответ от ИИ
                            String aiResponse = response.body().choices.get(0).message.content;

                            // Добавляем сообщение от ИИ в список и обновляем RecyclerView
                            messagesList.add("AI: " + aiResponse);
                            messageAdapter.notifyItemInserted(messagesList.size() - 1);
                            messageRecyclerView.scrollToPosition(messagesList.size() - 1);
                        } else {
                            // Обрабатываем ошибку (например, показываем сообщение пользователю)
                            Log.e("API Error", "Error: " + response.code() + " " + response.message());
                            messagesList.add("AI: Error - " + response.message()); // или более дружелюбное сообщение
                            messageAdapter.notifyItemInserted(messagesList.size() - 1);
                            messageRecyclerView.scrollToPosition(messagesList.size() - 1);
                        }
                    }

                    @Override
                    public void onFailure(Call<ChatCompletionResponse> call, Throwable t) {
                        // Обрабатываем ошибку сети или другую ошибку
                        Log.e("API Error", "Failure: " + t.getMessage());
                        messagesList.add("AI: Error - " + t.getMessage()); // или более дружелюбное сообщение
                        messageAdapter.notifyItemInserted(messagesList.size() - 1);
                        messageRecyclerView.scrollToPosition(messagesList.size() - 1);
                    }
                });
            }
        });
    }
}
