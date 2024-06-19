package com.example.docsavior;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {
    @POST("/user/login")
    Call<Detail> postLoginInfo(@Query("username") String username, @Query("password") String password);

    @POST("/user/add")
    Call<Detail> postSignUpInfo(@Query("username") String username, @Query("email") String email, @Query("phoneNumber") String phoneNumber, @Query("password") String password, @Query("fullName") String fullName, @Query("birthDay") String birthDay, @Query("gender") boolean gender);

    @POST("/user/password_recovery")
    Call<Detail> postRecoverPasswordInfo(@Query("username") String username, @Query("email") String email, @Query("phoneNumber") String phoneNumber);

    @GET("/newsfeed/all")
    Call<List<NewsFeed>> getAllPosts();

    @POST("/newsfeed/add")
    Call<Detail> postNewsfeed(@Query("username") String username, @Query("postDescription") String postDescription, @Query("postContent") String postContent, @Query("newsfeedFileData") String newsfeedFileData, @Query("newsfeedFileExtension") String newsfeedFileExtension);

    @POST("/friend/add")
    Call<Detail> postNewFriend(@Query("username") String username, @Query("usernameFriend") String usernameFriend);

    @GET("/friend_request/all")
    Call<Requester> getAllFriendRequests(@Query("username") String username);

    @DELETE("/friend_request/delete")
    Call<Detail> deleteFriendRequest(@Query("username") String username, @Query("requester") String requester);
}
