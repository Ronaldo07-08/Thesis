package com.example.vmeste.api;

import com.example.vmeste.ChatActivity;

import java.util.List;
public class ChatCompletionRequest {
    public String model;
    public List<Message> messages;
    public String apiKey;
}
