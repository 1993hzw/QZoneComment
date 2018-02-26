package com.example.QzoneComment;

import android.content.Context;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.text.style.TextAppearanceSpan;
import android.view.View;
import com.example.QzoneComment.model.User;
import org.xml.sax.XMLReader;

import java.util.HashMap;

/**
 * 显示评论的自定义Html标签解析器
 */
public class CustomTagHandler implements Html.TagHandler {
    //自定义标签
    public static final String TAG_COMMENTATOR = "commentator"; // 评论者
    public static final String TAG_RECEIVER = "receiver"; // 评论接收者，即对谁评论
    public static final String TAG_CONTENT = "content"; // 评论内容

    public static final int KEY_COMMENTATOR = -2016;
    public static final int KEY_RECEIVER = -20162;

    public static final int KEY_COMMENTATOR_START = 1;
    public static final int KEY_RECEIVER_START = 11;
    public static final int KEY_CONTENT_START = 21;

    private HashMap<Integer, Integer> mMaps = new HashMap<Integer, Integer>();

    private ClickableSpan mCommentatorSpan, mReceiverSpan, mContentSpan;

    private Context mContext;

    public CustomTagHandler(final Context context, final OnCommentClickListener listener) {
        mContext = context;
        mCommentatorSpan = new BaseClickableSpan() {
            @Override
            public void onClick(View widget) {
                if (listener != null) {
                    User user = (User) widget.getTag(KEY_COMMENTATOR);
                    listener.onCommentatorClick(widget, user);
                }
            }
        };
        mReceiverSpan = new BaseClickableSpan() {
            @Override
            public void onClick(View widget) {
                if (listener != null) {
                    User user = (User) widget.getTag(KEY_RECEIVER);
                    listener.onReceiverClick(widget, user);
                }
            }
            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setColor(0xFF436B9C);//接收者字体颜色
                ds.setUnderlineText(false);
            }
        };
        mContentSpan = new BaseClickableSpan() {
            @Override
            public void onClick(View widget) {
                if (listener != null) {
                    User commentator = (User) widget.getTag(KEY_COMMENTATOR);
                    User receiver = (User) widget.getTag(KEY_RECEIVER);
                    listener.onContentClick(widget, commentator, receiver);
                }
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setColor(0xff000000);
                ds.setUnderlineText(false);
            }
        };
    }

    /**
     * 解析自定义标签
     */
    @Override
    public void handleTag(boolean opening, String tag, final Editable output, XMLReader xmlReader) {
        if (!tag.toLowerCase().equals(TAG_COMMENTATOR) && !tag.toLowerCase().equals(TAG_RECEIVER)
                && !tag.toLowerCase().equals(TAG_CONTENT)) {
            return;
        }
        if (opening) {  //开始标签
            // 记录标签内容的起始索引
            int mStart = output.length();
            if (tag.toLowerCase().equals(TAG_COMMENTATOR)) {
                mMaps.put(KEY_COMMENTATOR_START, mStart);
            } else if (tag.toLowerCase().equals(TAG_RECEIVER)) {
                mMaps.put(KEY_RECEIVER_START, mStart);
            } else if (tag.toLowerCase().equals(TAG_CONTENT)) {
                mMaps.put(KEY_CONTENT_START, mStart);
            }
        } else { // 结束标签
            int mEnd = output.length(); //标签内容的结束索引

            if (tag.toLowerCase().equals(TAG_COMMENTATOR)) {
                int mStart = mMaps.get(KEY_COMMENTATOR_START);
                output.setSpan(new TextAppearanceSpan(mContext, R.style.Comment),
                        mStart, mEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                output.setSpan(mCommentatorSpan, mStart, mEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            } else if (tag.toLowerCase().equals(TAG_RECEIVER)) {
                int mStart = mMaps.get(KEY_RECEIVER_START);
                output.setSpan(new TextAppearanceSpan(mContext, R.style.Comment),
                        mStart, mEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                output.setSpan(mReceiverSpan, mStart, mEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            } else if (tag.toLowerCase().equals(TAG_CONTENT)) {
                int mStart = mMaps.get(KEY_CONTENT_START);
                output.setSpan(new TextAppearanceSpan(mContext, R.style.Comment),
                        mStart, mEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                output.setSpan(mContentSpan, mStart, mEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
    }

    /**
     *
     */
    abstract class BaseClickableSpan extends ClickableSpan {
        public BaseClickableSpan() {

        }

        @Override
        public abstract void onClick(View widget);

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setColor(ds.linkColor); //默认颜色
            ds.setUnderlineText(false);
        }
    }

    public interface OnCommentClickListener {
        // 点击评论者
        void onCommentatorClick(View view, User commentator);

        // 点击接收者
        void onReceiverClick(View view, User receiver);

        //　点击评论内容
        void onContentClick(View view, User commentator, User receiver);

    }
}

