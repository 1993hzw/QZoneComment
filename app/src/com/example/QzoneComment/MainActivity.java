package com.example.QzoneComment;

import android.app.Activity;
import android.os.Bundle;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.example.QzoneComment.model.Comment;
import com.example.QzoneComment.model.Moment;
import com.example.QzoneComment.model.User;

import java.util.ArrayList;

public class MainActivity extends Activity {

    public static User sUser = new User(1, "走向远方"); // 当前登录用户

    private ListView mListView;
    private MomentAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.main);
        mListView = (ListView) findViewById(R.id.list_moment);

        // 模拟数据
        ArrayList<Moment> moments = new ArrayList<Moment>();
        for (int i = 0; i < 20; i++) {
            ArrayList<Comment> comments = new ArrayList<Comment>();
            comments.add(new Comment(new User(i + 2, "用户" + i), "评论" + i, null));
            comments.add(new Comment(new User(i + 100, "用户" + (i + 100)), "评论" + i, new User(i + 200, "用户" + (i + 200))));
            comments.add(new Comment(new User(i + 200, "用户" + (i + 200)), "评论" + i, null));
            comments.add(new Comment(new User(i + 300, "用户" + (i + 300)), "评论" + i, null));
            moments.add(new Moment("动态 " + i, comments));
        }

        mAdapter = new MomentAdapter(this, moments, new CustomTagHandler(this, new CustomTagHandler.OnCommentClickListener() {
            @Override
            public void onCommentatorClick(View view, User commentator) {
                Toast.makeText(getApplicationContext(), commentator.mName, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onReceiverClick(View view, User receiver) {
                Toast.makeText(getApplicationContext(), receiver.mName, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onContentClick(View view, User commentator, User receiver) {
                if (commentator != null && commentator.mId == sUser.mId) { // 不能回复自己的评论
                    return;
                }
                inputComment(view, commentator);
            }
        }));

        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(),"click "+position,Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void inputComment(final View v) {
        inputComment(v, null);
    }

    public void inputComment(final View v, User receiver) {
        CommentFun.inputComment(MainActivity.this, mListView, v, receiver, new CommentFun.InputCommentListener() {
            @Override
            public void onCommitComment() {
                mAdapter.notifyDataSetChanged();
            }
        });
    }
}
