package com.ag.twisterpm.services;

import com.ag.twisterpm.models.Comment;
import com.ag.twisterpm.models.Twist;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface TwisterService {
    @GET("Messages")
    Call<List<Twist>> getAllTwists();

    @POST("Messages")
    Call<Twist> postTwist(@Body Twist twist);

    @DELETE("Messages/{id}")
    Call<Twist> deleteTwist(@Path("id") String messageID);

    @GET("Messages/{messageId}/comments")
    Call<List<Comment>> getAllComments(@Path("messageId") String messageID);

    @POST("Messages/{messageId}/comments")
    Call<Comment> postComment(@Path("messageId") String messageID, @Body Comment comment);

    @DELETE("Messages/{messageId}/comments/{commentId}")
    Call<Comment> deleteComment(@Path("messageId") String messageID, @Path("commentId") String commentID);
}
