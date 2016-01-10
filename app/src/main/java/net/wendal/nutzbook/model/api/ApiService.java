package net.wendal.nutzbook.model.api;

import net.wendal.nutzbook.model.entity.LoginInfo;
import net.wendal.nutzbook.model.entity.Notification;
import net.wendal.nutzbook.model.entity.Result;
import net.wendal.nutzbook.model.entity.TabType;
import net.wendal.nutzbook.model.entity.Topic;
import net.wendal.nutzbook.model.entity.TopicUpInfo;
import net.wendal.nutzbook.model.entity.TopicWithReply;
import net.wendal.nutzbook.model.entity.User;

import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.mime.TypedFile;

public interface ApiService {

    //=====
    // 主题
    //=====

    @GET("/v1/topics")
    void getTopics(
            @Query("tab") TabType tab,
            @Query("page") Integer page,
            @Query("limit") Integer limit,
            @Query("mdrender") Boolean mdrender,
            Callback<Result<List<Topic>>> callback
    );

    @GET("/v1/topic/{id}")
    void getTopic(
            @Path("id") String id,
            @Query("mdrender") Boolean mdrender,
            Callback<Result<TopicWithReply>> callback
    );

    @FormUrlEncoded
    @POST("/v1/topics")
    void newTopic(
            @Field("accesstoken") String accessToken,
            @Field("tab") TabType tab,
            @Field("title") String title,
            @Field("content") String content,
            Callback<Void> callback
    );

    @FormUrlEncoded
    @POST("/v1/topic/{topicId}/replies")
    void replyTopic(
            @Field("accesstoken") String accessToken,
            @Path("topicId") String topicId,
            @Field("content") String content,
            @Field("reply_id") String replyId,
            Callback<Map<String, String>> callback
    );

    @FormUrlEncoded
    @POST("/v1/reply/{replyId}/ups")
    void upTopic(
            @Field("accesstoken") String accessToken,
            @Path("replyId") String replyId,
            Callback<TopicUpInfo> callback
    );

    //=====
    // 用户
    //=====

    @GET("/v1/user/{loginName}")
    void getUser(
            @Path("loginName") String loginName,
            Callback<Result<User>> callback
    );

    @FormUrlEncoded
    @POST("/v1/accesstoken")
    void accessToken(
            @Field("accesstoken") String accessToken,
            Callback<LoginInfo> callback
    );

    //=========
    // 消息通知
    //=========

    @GET("/v1/message/count")
    void getMessageCount(
            @Query("accesstoken") String accessToken,
            Callback<Result<Integer>> callback
    );

    @GET("/v1/messages")
    void getMessages(
            @Query("accesstoken") String accessToken,
            Callback<Result<Notification>> callback
    );

    @FormUrlEncoded
    @POST("/v1/message/mark_all")
    void markAllMessageRead(
            @Field("accesstoken") String accessToken,
            Callback<Void> callback
    );

    //=========
    // 图片上传
    //=========
    @Multipart
    @POST("/v1/images")
    void uploadImage(
            @Query("accesstoken") String accessToken,
            @Part("file") TypedFile image,
            Callback<Map<String, String>> callback
    );

    /**
     * 获取上传到七牛的token
     */
    @POST("/v1/videos")
    void uploadVideo(
            @Query("accesstoken") String accessToken
    );
}
