package com.example.docsavior;

import java.sql.Blob;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {
    @POST("/user/login")
    Call<Detail> postLoginInfo(@Query("username") String username, @Query("password") String password);
    @POST("/user/logout")
    Call<Detail> postLogout(@Query("username") String username);
    @POST("/user/add")
    Call<Detail> postSignUpInfo(@Query("username") String username, @Query("email") String email, @Query("phoneNumber") String phoneNumber, @Query("password") String password, @Query("fullName") String fullName, @Query("birthDay") String birthDay, @Query("gender") boolean gender);
    @POST("/user/password_recovery")
    Call<Detail> postRecoverPasswordInfo(@Query("username") String username, @Query("email") String email, @Query("phoneNumber") String phoneNumber);
    @POST("/user/password_change")
    Call<Detail> postChangePassword(@Query("username") String username, @Query("oldPassword") String oldPassword, @Query("newPassword") String newPassword);
    @GET("/user/me")
    Call<User> getUserInfo(@Query("username") String username);
    @POST("/user/avatar")
    Call<Detail> postAvatar(@Query("username") String username, @Query("avatarData") String avatarData, @Query("avatarName") String avatarName, @Query("avatarExtension") String avatarExtension);
    @POST("/user/look_up")
    Call<FoundUsers> postUserLookUp(@Query("lookUpInfo") String lookUpInfo);
    @GET("/user/avatar_data")
    Call<Detail> getAvatarData(@Query("username") String username);
    @POST("/user/update_user_info")
    Call<Detail> postUserInfo(@Query("username") String username, @Query("fullName") String fullName, @Query("email") String email, @Query("gender") Boolean gender, @Query("birthDate") String birthDate, @Query("phoneNumber") String phoneNumber);
    @GET("/user/status")
    Call<Detail> getUserStatus(@Query("username") String username);



    @POST("/otp/create_or_refresh")
    Call<Detail> postCreateOrRefreshOTP(@Query("username") String username, @Query("email") String email);
    @POST("/otp/check")
    Call<Detail> postCheckOTP(@Query("username") String username, @Query("otp") String otp);



    @GET("/newsfeed/all")
    Call<List<NewsFeed>> getAllPosts();
    @GET("/newsfeed/post")
    Call<List<NewsFeed>> getSequenceOfPost(@Query("page") Integer page, @Query("pageSize") Integer pageSize);
    @POST("/newsfeed/add")
    Call<Detail> postNewsfeed(@Query("username") String username, @Query("postDescription") String postDescription, @Query("postContent") String postContent, @Query("fileData") String fileData, @Query("fileName") String fileName, @Query("fileExtension") String fileExtension);
    @POST("/newsfeed/like")
    Call<Detail> postLike(@Query("id") Integer id);
    @POST("/newsfeed/unlike")
    Call<Detail> postUnlike(@Query("id") Integer id);
    @POST("/newsfeed/dislike")
    Call<Detail> postDislike(@Query("id") Integer id);
    @POST("/newsfeed/undislike")
    Call<Detail> postUndislike(@Query("id") Integer id);
    @GET("/newsfeed/me")
    Call<List<NewsFeed>> getMyPost(@Query("username") String username);
    @POST("/newsfeed/look_up")
    Call<FoundNewsfeeds> postPostLookUp(@Query("lookUpInfo") String lookUpInfo);
    @GET("/newsfeed/id")
    Call<NewsFeed> getNewsfeedById(@Query("id") Integer id);
    @POST("/newsfeed/comment")
    Call<Detail> postComment(@Query("id") Integer id);
    @POST("/newsfeed/uncomment")
    Call<Detail> postUncomment(@Query("id") Integer id);
    @GET("/newsfeed/size")
    Call<Integer> getNumberOfPosts();


    @POST("/user_interact/interact")
    Call<Detail> postInteract(@Query("username") String username, @Query("idPost") Integer idPost, @Query("type") Boolean type);
    @GET("/user_interact/likeordislike")
    Call<Detail> getInteract(@Query("username") String username, @Query("idPost") Integer idPost);



    @POST("/friend/add")
    Call<Detail> postNewFriend(@Query("username") String username, @Query("usernameFriend") String usernameFriend);
    @GET("/friend/all")
    Call<Friends> getAllFriends(@Query("username") String username);
    @DELETE("/friend/delete")
    Call<Detail> deleteFriend(@Query("username") String username, @Query("usernameFriend") String usernameFriend);



    @GET("/friend_request/all")
    Call<Requester> getAllFriendRequests(@Query("username") String username);
    @POST("/friend_request/add")
    Call<Detail> postFriendRequest(@Query("username") String username, @Query("requester") String requester);
    @DELETE("/friend_request/delete")
    Call<Detail> deleteFriendRequest(@Query("username") String username, @Query("requester") String requester);



    @GET("/notification/me")
    Call<List<Notification>> getAllMyNotifications(@Query("username") String username);
    @POST("/notification/add")
    Call<Detail> postNotification(@Query("username") String username, @Query("type") Integer type,  @Query("idPost") Integer idPost, @Query("interacter") String interacter);
    @DELETE("/notification/delete")
    Call<Detail> deleteNotification(@Query("username") String username, @Query("type") Integer type, @Query("idPost") Integer idPost, @Query("interacter") String interacter);



    @POST("/look_up_history/add")
    Call<Detail> postLookUpHistory(@Query("username") String username, @Query("lookUpInfo") String lookUpInfo, @Query("lookUpType") Integer lookUpType);
    @GET("/look_up_history/me/post")
    Call<LookUpInfos> getPostLookUpHistory(@Query("username") String username);
    @GET("/look_up_history/me/friend")
    Call<LookUpInfos> getFriendLookUpHistory(@Query("username") String username);
    @DELETE("/look_up_history/delete")
    Call<Detail> deleteLookUpHistory(@Query("username") String username, @Query("lookUpInfo") String lookUpInfo, @Query("lookUpType") Integer lookUpType);



    @POST("/comment/add")
    Call<Detail> postComment(@Query("username") String username, @Query("idPost") Integer idPost, @Query("commentContent") String commentContent);
    @GET("/comment/comment_on_post")
    Call<List<Comment>> getPostComment(@Query("idPost") Integer idPost);
    @DELETE("/comment/delete")
    Call<Detail> deleteComment(@Query("idComment") Integer idComment);



    @GET("/message/all")
    Call<List<Message>> getAllMyMessage(@Query("username") String username, @Query("sender") String sender);
    @POST("/message/add")
    Call<Detail> postMessage(@Query("username") String username, @Query("sender") String sender, @Query("content") String content);
    @GET("/message/messaged_user")
    Call<MessagedUsernames> getMessagedUsernames(@Query("username") String username);
    @GET("/message/latest")
    Call<Message> getLatestMessage(@Query("username") String username, @Query("sender") String sender);
    @GET("/message/unseen")
    Call<List<Message>> getUnseenMessage(@Query("username") String username, @Query("sender") String sender);
    @POST("/message/seen_to_true")
    Call<Detail> postSeenToTrue(@Query("id") Integer id);
}