package com.example.QzoneComment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.*;
import com.example.QzoneComment.model.Comment;
import com.example.QzoneComment.model.User;

import java.util.ArrayList;

/**
 * 评论相关方法
 */
public class CommentFun {
    public static final int KEY_COMMENT_SOURCE_COMMENT_LIST = -200162;

    /**
     * 在页面中显示评论列表
     *
     * @param context
     * @param mCommentList
     * @param commentList
     * @param btnComment
     * @param tagHandler
     */
    public static void parseCommentList(Context context, ArrayList<Comment> mCommentList, LinearLayout commentList,
                                        View btnComment, Html.TagHandler tagHandler) {
        if (btnComment != null) {
            btnComment.setTag(KEY_COMMENT_SOURCE_COMMENT_LIST, mCommentList);
        }
        TextView textView;
        Comment comment;
        int i;
        String content;
        for (i = 0; i < mCommentList.size(); i++) {
            comment = mCommentList.get(i);
            textView = (TextView) commentList.getChildAt(i);
            if (textView == null) { // 创建评论Videw
                textView = (TextView) View.inflate(context, R.layout.view_comment_list_item, null);
                commentList.addView(textView);
            }
            textView.setVisibility(View.VISIBLE);
            if (comment.mReceiver == null) {
                content = String.format("<html><%s>%s</%s>: <%s>%s</%s></html>", CustomTagHandler.TAG_COMMENTATOR,
                        comment.mCommentator.mName, CustomTagHandler.TAG_COMMENTATOR,
                        CustomTagHandler.TAG_CONTENT, comment.mContent, CustomTagHandler.TAG_CONTENT);
            } else {
                content = String.format("<html><%s>%s</%s> 回复 <%s>%s</%s>: <%s>%s</%s><html>",
                        CustomTagHandler.TAG_COMMENTATOR, comment.mCommentator.mName, CustomTagHandler.TAG_COMMENTATOR,
                        CustomTagHandler.TAG_RECEIVER, comment.mReceiver.mName, CustomTagHandler.TAG_RECEIVER,
                        CustomTagHandler.TAG_CONTENT, comment.mContent, CustomTagHandler.TAG_CONTENT);
            }
            textView.setText(Html.fromHtml(content, null, tagHandler));
            textView.setClickable(true);
            textView.setMovementMethod(LinkMovementMethod.getInstance());
            textView.setTag(CustomTagHandler.KEY_COMMENTATOR, comment.mCommentator);
            textView.setTag(CustomTagHandler.KEY_RECEIVER, comment.mReceiver);

            textView.setTag(KEY_COMMENT_SOURCE_COMMENT_LIST, mCommentList);
        }
        for (; i < commentList.getChildCount(); i++) {
            commentList.getChildAt(i).setVisibility(View.GONE);
        }
        if (mCommentList.size() > 0) {
            commentList.setVisibility(View.VISIBLE);
        } else {
            commentList.setVisibility(View.GONE);
        }
    }


    /**
     * 弹出评论对话框
     */
    public static void inputComment(final Activity activity, final ListView listView,
                                    final View btnComment, final User receiver,
                                    final InputCommentListener listener) {

        final ArrayList<Comment> commentList = (ArrayList) btnComment.getTag(KEY_COMMENT_SOURCE_COMMENT_LIST);

        String hint;
        if (receiver != null) {
            if (receiver.mId == MainActivity.sUser.mId) {
                hint = "我也说一句";
            } else {
                hint = "回复 " + receiver.mName;
            }
        } else {
            hint = "我也说一句";
        }
        // 获取评论的位置,不要在CommentDialogListener.onShow()中获取，onShow在输入法弹出后才调用，
        // 此时btnComment所属的父布局可能已经被ListView回收
        final int[] coord = new int[2];
        if (listView != null) {
            btnComment.getLocationOnScreen(coord);
        }

        showInputComment(activity, hint, new CommentDialogListener() {
            @Override
            public void onClickPublish(final Dialog dialog, EditText input, final TextView btn) {
                final String content = input.getText().toString();
                if (content.trim().equals("")) {
                    Toast.makeText(activity, "评论不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                btn.setClickable(false);
                final long receiverId = receiver == null ? -1 : receiver.mId;
                Comment comment = new Comment(MainActivity.sUser, content, receiver);
                commentList.add(comment);
                if (listener != null) {
                    listener.onCommitComment();
                }
                dialog.dismiss();
                Toast.makeText(activity, "评论成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onShow(int[] inputViewCoordinatesInScreen) {
                if (listView != null) {
                    // 点击某条评论则这条评论刚好在输入框上面，点击评论按钮则输入框刚好挡住按钮
                    int span = btnComment.getId() == R.id.btn_input_comment ? 0 : btnComment.getHeight();
                    listView.smoothScrollBy(coord[1] + span - inputViewCoordinatesInScreen[1], 1000);
                }
            }

            @Override
            public void onDismiss() {

            }
        });

    }

    public static class InputCommentListener {
        //　评论成功时调用
        public void onCommitComment() {

        }
    }


    /**
     * 弹出评论对话框
     */
    private static Dialog showInputComment(Activity activity, CharSequence hint, final CommentDialogListener listener) {
        final Dialog dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.setContentView(R.layout.view_input_comment);
        dialog.findViewById(R.id.input_comment_dialog_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (listener != null) {
                    listener.onDismiss();
                }
            }
        });
        final EditText input = (EditText) dialog.findViewById(R.id.input_comment);
        input.setHint(hint);
        final TextView btn = (TextView) dialog.findViewById(R.id.btn_publish_comment);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClickPublish(dialog, input, btn);
                }
            }
        });
        dialog.setCancelable(true);
        dialog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (listener != null) {
                    int[] coord = new int[2];
                    dialog.findViewById(R.id.input_comment_container).getLocationOnScreen(coord);
                    // 传入 输入框距离屏幕顶部（不包括状态栏）的长度
                    listener.onShow(coord);
                }
            }
        }, 300);
        return dialog;
    }

    public interface CommentDialogListener {
        void onClickPublish(Dialog dialog, EditText input, TextView btn);

        void onShow(int[] inputViewCoordinatesOnScreen);

        void onDismiss();
    }

}
