package com.example.QzoneComment.model;

import java.util.ArrayList;

/**
 * 评论对象
 */
public class Moment {

    public String mContent;
    public ArrayList<Comment> mComment; // 评论列表

    public Moment(String mContent,ArrayList<Comment> mComment) {
        this.mComment = mComment;
        this.mContent = mContent;
    }
}
