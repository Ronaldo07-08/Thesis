package com.example.vmeste.api;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Header;

public interface TogetherApi {

    @Headers({"Content-Type: application/json"})
    @POST("chat/completions")
    Call<ChatCompletionResponse> getChatCompletion(
            @Header("Authorization") String apiKey,
            @Body ChatCompletionRequest request
    );
}

