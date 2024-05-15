package com.example.finalproject;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {
    @POST("/user/login")
    Call<Detail> postLoginInfo(@Query("username") String username, @Query("password") String password);

    @POST("/user/add")
    Call<Detail> postSignUpInfo(@Query("username") String username, @Query("email") String email, @Query("phoneNumber") String phoneNumber, @Query("password") String password);

    @POST("/user/password_recovery")
    Call<Detail> postRecoverPasswordInfo(@Query("username") String username, @Query("email") String email, @Query("phoneNumber") String phoneNumber);
}
