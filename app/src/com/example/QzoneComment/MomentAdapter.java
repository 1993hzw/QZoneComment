package com.example.QzoneComment;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.QzoneComment.model.Moment;

import java.util.ArrayList;

/**
 * Created by huangziwei on 16-4-12.
 */
public class MomentAdapter extends BaseAdapter {

    public static final int VIEW_HEADER = 0;
    public static final int VIEW_MOMENT = 1;


    private ArrayList<Moment> mList;
    private Context mContext;
    private CustomTagHandler mTagHandler;

    public MomentAdapter(Context context, ArrayList<Moment> list, CustomTagHandler tagHandler) {
        mList = list;
        mContext = context;
        mTagHandler = tagHandler;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? VIEW_HEADER : VIEW_MOMENT;
    }

    @Override
    public int getCount() {
        // heanderView
        return mList.size() + 1;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            if (position == 0) {
                convertView = View.inflate(mContext, R.layout.item_header, null);
            } else {
                convertView = View.inflate(mContext, R.layout.item_moment, null);
                ViewHolder holder = new ViewHolder();
                holder.mCommentList = (LinearLayout) convertView.findViewById(R.id.comment_list);
                holder.mBtnInput = convertView.findViewById(R.id.btn_input_comment);
//                holder.mContent = (TextView) convertView.findViewById(R.id.content);
                convertView.setTag(holder);
            }
        }
        //防止ListView的OnItemClick与item里面子view的点击发生冲突
        ((ViewGroup) convertView).setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
        if (position == 0) {

        } else {
            int index = position - 1;
            ViewHolder holder = (ViewHolder) convertView.getTag();
//            holder.mContent.setText(mList.get(index).mContent);
            CommentFun.parseCommentList(mContext, mList.get(index).mComment,
                    holder.mCommentList, holder.mBtnInput, mTagHandler);
        }
        return convertView;
    }

    private static class ViewHolder {
        LinearLayout mCommentList;
        View mBtnInput;
        TextView mContent;
    }
}
