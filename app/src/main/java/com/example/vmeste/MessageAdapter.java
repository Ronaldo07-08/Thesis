package com.example.vmeste;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private List<Message> messagesList;

    public static class Message {
        public String text;
        public boolean isUser;

        public Message(String text, boolean isUser) {
            this.text = text;
            this.isUser = isUser;
        }
    }

    public MessageAdapter(List<Message> messagesList) {
        this.messagesList = messagesList;
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
        Message message = messagesList.get(position);
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
        return messagesList.size();
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
