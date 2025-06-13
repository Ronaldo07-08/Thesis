package com.example.vmeste;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vmeste.api.ApiClient;
import com.example.vmeste.api.ChatCompletionRequest;
import com.example.vmeste.api.ChatCompletionResponse;
import com.example.vmeste.api.Message;
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
    private List<MessageAdapter.Message> messagesList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Инициализация элементов UI
        messageRecyclerView = findViewById(R.id.recycler_view_messages);
        messageEditText = findViewById(R.id.edit_text_message);
        sendButton = findViewById(R.id.button_send);

        // Инициализация API
        togetherApi = ApiClient.getInstance().getApi();

        // Настройка RecyclerView
        messageRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        messageAdapter = new MessageAdapter(messagesList);
        messageRecyclerView.setAdapter(messageAdapter);

        sendButton.setOnClickListener(v -> {
            String userMessage = messageEditText.getText().toString().trim();
            if (!userMessage.isEmpty()) {
                // Добавляем сообщение пользователя
                messagesList.add(new MessageAdapter.Message(userMessage, true));
                messageAdapter.notifyItemInserted(messagesList.size() - 1);
                messageRecyclerView.smoothScrollToPosition(messagesList.size() - 1);
                messageEditText.setText("");

                // Подготовка и отправка запроса к API
                sendToAI(userMessage);
            }
        });
    }

    private void sendToAI(String userMessage) {
        List<Message> apiMessages = new ArrayList<>();
        apiMessages.add(new Message("user", userMessage));

        ChatCompletionRequest request = new ChatCompletionRequest();
        request.model = "deepseek-ai/DeepSeek-R1-Distill-Llama-70B-free";
        request.messages = apiMessages;
        String apiKey = "Bearer " + getString(R.string.together_api_key);

        togetherApi.getChatCompletion(apiKey, request).enqueue(new Callback<ChatCompletionResponse>() {
            @Override
            public void onResponse(Call<ChatCompletionResponse> call, Response<ChatCompletionResponse> response) {
                if (response.isSuccessful() && response.body() != null &&
                        response.body().choices != null && !response.body().choices.isEmpty()) {

                    String aiResponse = response.body().choices.get(0).message.content;
                    messagesList.add(new MessageAdapter.Message(aiResponse, false));
                } else {
                    String errorMsg = "Error: " + (response.errorBody() != null ?
                            response.errorBody().toString() : response.message());
                    messagesList.add(new MessageAdapter.Message(errorMsg, false));
                    Log.e("API Error", errorMsg);
                }
                messageAdapter.notifyItemInserted(messagesList.size() - 1);
                messageRecyclerView.smoothScrollToPosition(messagesList.size() - 1);
            }


            @Override
            public void onFailure(Call<ChatCompletionResponse> call, Throwable t) {
                String errorMsg = "Connection error: " + t.getMessage();
                messagesList.add(new MessageAdapter.Message(errorMsg, false));
                messageAdapter.notifyItemInserted(messagesList.size() - 1);
                messageRecyclerView.smoothScrollToPosition(messagesList.size() - 1);
                Log.e("API Failure", errorMsg, t);
            }
        });
    }


    // Адаптер для RecyclerView
    public static class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
        private List<Message> messages;

        public static class Message {
            public String text;
            public boolean isUser;

            public Message(String text, boolean isUser) {
                this.text = text;
                this.isUser = isUser;
            }
        }

        public MessageAdapter(List<Message> messages) {
            this.messages = messages;
        }

        @NonNull
        @Override
        public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.message_item, parent, false);
            return new MessageViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
            Message message = messages.get(position);
            if (message.isUser) {
                holder.userMessage.setText(message.text);
                holder.userMessage.setVisibility(View.VISIBLE);
                holder.aiMessage.setVisibility(View.GONE);
            } else {
                holder.aiMessage.setText(message.text);
                holder.aiMessage.setVisibility(View.VISIBLE);
                holder.userMessage.setVisibility(View.GONE);
            }
        }

        @Override
        public int getItemCount() {
            return messages.size();
        }

        public static class MessageViewHolder extends RecyclerView.ViewHolder {
            TextView userMessage;
            TextView aiMessage;

            public MessageViewHolder(@NonNull View itemView) {
                super(itemView);
                userMessage = itemView.findViewById(R.id.text_view_message_user);
                aiMessage = itemView.findViewById(R.id.text_view_message_ai);
            }
        }
    }
}
