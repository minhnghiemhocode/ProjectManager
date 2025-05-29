package com.mnghiem.projectmanager.api;

import com.mnghiem.projectmanager.models.*;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.*;

public interface MyAPI {

    // AUTHENTICATION

    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("auth/register")
    Call<RegisterResponse> register(@Body RegisterRequest request);

    @POST("auth/google-login")
    Call<LoginResponse> loginWithGoogle(@Body GoogleLoginRequest request);

    @POST("auth/verify-otp")
    Call<GeneralResponse> verifyOtp(@Body VerifyOtpRequest request);

    @POST("auth/resend-otp")
    Call<GeneralResponse> resendOtp(@Body ResendOtpRequest request);


    // USER

    @GET("user/by-id")
    Call<User> getUserById(@Query("userId") int userId);

    @POST("user/change-password")
    @FormUrlEncoded
    Call<GeneralResponse> changePassword(
            @Field("userId") int userId,
            @Field("oldPassword") String oldPassword,
            @Field("newPassword") String newPassword
    );

    @Multipart
    @POST("user/upload-avatar")
    Call<GeneralResponse> uploadAvatar(
            @Part MultipartBody.Part file,
            @Part("userId") RequestBody userId
    );


    // PROJECT

    @GET("project/personal/{userId}")
    Call<List<Project>> getPersonalProjects(@Path("userId") int userId);

    @GET("project/joined/{userId}")
    Call<List<Project>> getJoinedProjects(@Path("userId") int userId);

    @POST("project")
    Call<Project> createProject(@Header("Authorization") String token, @Body Project project);

    @PUT("project/{id}")
    Call<GeneralResponse> updateGroup(@Path("id") int groupId, @Body Project request);

    @DELETE("project/{id}")
    Call<GeneralResponse> deleteGroup(@Path("id") int groupId);

    @PUT("project/color/{id}")
    Call<GeneralResponse> updateGroupColor(@Path("id") int id, @Body ColorRequest request);

    @POST("project/invite")
    Call<GeneralResponse> inviteUser(@Body InviteRequest request);

    @GET("project/creator/{groupId}")
    Call<Integer> getGroupCreator(@Path("groupId") int groupId);

    @GET("project/boards")
    Call<List<Board>> getBoardsByGroup(@Query("ma_nhom") int groupId);


    // BOARD

    @POST("board")
    Call<GeneralResponse> createBoard(@Body Board board);

    @PUT("board/color/{id}")
    Call<GeneralResponse> updateBoardColor(@Path("id") int boardId, @Body ColorRequest request);

    @PUT("board/pin/{id}")
    Call<GeneralResponse> updateBoardPin(@Path("id") int boardId, @Query("isPinned") boolean isPinned);

    @PUT("board/{id}")
    Call<GeneralResponse> updateBoard(@Path("id") int id, @Body Board board);

    @DELETE("board/{id}")
    Call<GeneralResponse> deleteBoard(@Path("id") int id);



    // TASK

    @GET("tasks/deadlines")
    Call<List<Task>> getTasksWithDeadlines(@Query("userId") int userId);

    @POST("tasks")
    Call<GeneralResponse> createTask(@Body Task task);

    @PUT("tasks/{ma_cv}")
    Call<GeneralResponse> updateTask(@Path("ma_cv") int id, @Body Task task);

    @DELETE("tasks/{ma_cv}")
    Call<GeneralResponse> deleteTask(@Path("ma_cv") int id);

    @PUT("tasks/{ma_cv}/color")
    Call<GeneralResponse> updateTaskColor(@Path("ma_cv") int id, @Body ColorRequest request);

    @PUT("tasks/{ma_cv}/status")
    Call<GeneralResponse> updateTaskStatus(@Path("ma_cv") int id, @Body Task task);

    @PUT("tasks/{ma_cv}/priority")
    Call<GeneralResponse> updateTaskPriority(@Path("ma_cv") int id, @Body Task task);

    @PUT("tasks/{ma_cv}/deadline")
    Call<GeneralResponse> updateTaskDeadline(@Path("ma_cv") int id, @Body Task task);

    @GET("tasks/by-user")
    Call<List<Task>> getTasksByUser(@Query("userId") int userId);

    @GET("tasks/by-board")
    Call<List<Task>> getTasksByBoard(@Query("boardId") int boardId);

    @GET("tasks/{ma_cv}")
    Call<Task> getTaskById(@Path("ma_cv") int ma_cv);



    // CHECKLIST

    @GET("checklist/{ma_cv}")
    Call<List<Checklist>> getChecklistByTask(@Path("ma_cv") int taskId);

    @POST("checklist")
    Call<GeneralResponse> addChecklistItem(@Body Checklist item);

    @PUT("checklist/{ma_item}")
    Call<GeneralResponse> updateChecklistStatus(@Path("ma_item") int id, @Body Checklist item);

    @DELETE("checklist/{ma_item}")
    Call<GeneralResponse> deleteChecklistItem(@Path("ma_item") int id);


    // NOTIFICATION

    @GET("notifications")
    Call<List<Notification>> getNotifications(@Query("userId") int userId);

    @PUT("notifications/read/{ma_tb}")
    Call<GeneralResponse> markNotificationAsRead(@Path("ma_tb") int notificationId);


    // USER SETTINGS

    @GET("settings/{userId}")
    Call<Setting> getUserSettings(@Path("userId") int userId);

    @POST("settings/theme")
    Call<GeneralResponse> updateTheme(@Body Setting setting);

    @POST("settings/language")
    Call<GeneralResponse> updateLanguage(@Body Setting setting);

    // ATTACHMENT

    @GET("attachments/{ma_cv}")
    Call<List<Attachment>> getAttachmentsByTask(@Path("ma_cv") int taskId);

    @PUT("checklist/{ma_item}/content")
    Call<GeneralResponse> updateChecklistContent(@Path("ma_item") int id, @Body Checklist item);


}
