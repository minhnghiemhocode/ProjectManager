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

    @POST("group/invite")
    Call<GeneralResponse> inviteMemberToGroup(
            @Body InviteRequest request,
            @Header("Authorization") String token
    );


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

    @GET("tasks/by-board-android")
    Call<List<Task>> getTasksByBoardAndroid(@Query("boardId") int boardId);

    // CHECKLIST

    @GET("checklist/{ma_cv}")
    Call<List<Checklist>> getChecklistByTask(@Path("ma_cv") int taskId);

    @POST("checklist")
    Call<GeneralResponse> addChecklistItem(@Body Checklist item);

    @PUT("checklist/{ma_item}")
    Call<GeneralResponse> updateChecklistStatus(@Path("ma_item") int id, @Body Checklist item);

    @DELETE("checklist/{ma_item}")
    Call<GeneralResponse> deleteChecklistItem(@Path("ma_item") int id);

    @PUT("checklist/{ma_item}/content")
    Call<GeneralResponse> updateChecklistContent(@Path("ma_item") int id, @Body Checklist item);

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


    // STATISTICS (ĐÃ CHUẨN HÓA THEO StatisticsActivity)

    @GET("statistics/overview")
    Call<Statistic> getOverview(
            @Query("userId") int userId,
            @Query("startDate") String startDate,
            @Query("endDate") String endDate
    );

    @GET("statistics/by-group")
    Call<List<Statistic>> getStatsByGroup(
            @Query("groupId") int groupId,
            @Query("startDate") String startDate,
            @Query("endDate") String endDate
    );

    @GET("statistics/by-user")
    Call<List<Statistic>> getStatsByUser(
            @Query("userId") int userId,
            @Query("startDate") String startDate,
            @Query("endDate") String endDate
    );


    @GET("statistics/total")
    Call<List<Statistic>> getTotalStats(@Query("userId") int userId);

    @GET("statistics/suggestions")
    Call<String> getSmartSuggestions(
            @Query("userId") int userId,
            @Query("startDate") String startDate,
            @Query("endDate") String endDate
    );

    @GET("statistics/user-detail")
    Call<Statistic> getUserDetailStats(
            @Query("userId") int userId,
            @Query("startDate") String startDate,
            @Query("endDate") String endDate
    );

    @GET("statistics/ai-data") // ✅ đúng
    Call<List<Task>> getAIData(@Query("userId") int userId);

    @PUT("user/{id}")
    Call<GeneralResponse> updateUserInfo(@Path("id") int userId, @Body User updatedUser);

    @POST("project/safe")
    Call<Project> createProjectSafe(@Header("Authorization") String token, @Body Project project);

    // ✅ Gửi lời mời nếu người dùng chưa đăng ký (không cần token)
    @POST("group/invite")
    Call<GeneralResponse> inviteUserWithoutToken(@Body InviteRequest request);

    // ✅ Gửi lời mời + thêm vào nhóm nếu đã có tài khoản (cần token)
    @POST("group/invite-auth")
    Call<GeneralResponse> inviteUserWithToken(
            @Body InviteRequest request,
            @Header("Authorization") String token
    );

    // ✅ Gửi mail nếu người được giao chưa có tài khoản
    @POST("group/assign")
    Call<GeneralResponse> assignUserWithoutToken(@Body AssignRequest request);

    // ✅ Gửi mail + phân công nếu người đó đã có tài khoản (cần token)
    @POST("group/assign-auth")
    Call<GeneralResponse> assignUserWithToken(
            @Body AssignRequest request,
            @Header("Authorization") String token
    );

    // ✅ Upload file đính kèm
    @Multipart
    @POST("attachments/upload")
    Call<GeneralResponse> uploadAttachment(
            @Part MultipartBody.Part file,
            @Part("taskId") RequestBody taskId,
            @Part("userId") RequestBody userId
    );

    // ✅ Lấy danh sách tệp đính kèm theo công việc
    @GET("attachments/{ma_cv}")
    Call<List<Attachment>> getAttachmentsByTask(@Path("ma_cv") int taskId);

    @GET("project/counts/{userId}")
    Call<List<Project>> getProjectCountsByUser(@Path("userId") int userId);

    @GET("board/group/{groupId}/with-task-counts")
    Call<List<Board>> getBoardsWithTaskCounts(@Path("groupId") int groupId);

}
