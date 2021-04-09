package com.ag.twisterpm.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Twist implements Serializable
{

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("content")
    @Expose
    private String content;
    @SerializedName("author")
    @Expose
    private String author;
    @SerializedName("date")
    @Expose
    private Long date;
    @SerializedName("comments")
    @Expose
    private List<String> comments;

    private final static long serialVersionUID = 8746722582036254395L;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthor() { return author; }

    public void setAuthor(String author) {
        this.author = author;
    }

    public List<String> getComments() {
        return comments;
    }

    public void setComments(List<String> comments) {
        this.comments = comments;
    }

    public Long getDate(){ return date; }

    public void setDate(Long date) { this.date = date; }

    public Twist(String id, String author, String content, Long date, List<String> comments) {
        setId(id);
        setAuthor(author);
        setContent(content);
        setDate(date);
        setComments(comments);
    }

    public Twist() {}
}