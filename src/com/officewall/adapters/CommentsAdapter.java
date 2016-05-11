
package com.officewall.adapters;

import java.util.List;

import com.officewall.activities.R;
import com.officewall.constants.DefaultConstants;
import com.officewall.imageloader.AsyncImageLoader;
import com.officewall.pojo.core.Comment;
import com.officewall.utils.Utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class CommentsAdapter extends BaseAdapter {

    private Context mContext;
    private List<Comment> listComment;

    // enum Row item
    public static enum Row {
        ITEM_UP_VOTE, ITEM_DOWN_VOTE, ITEM_REPORT
    }

    // Comment number background lists
    private TypedArray typedArrCommentNumberDrawables, typedArrNewCommentNumberDrawables;

    /**
     * Constructor
     * 
     * @param context
     * @param list
     */
    public CommentsAdapter(Context context, List<Comment> list) {
        mContext = context;
        listComment = list;
        typedArrCommentNumberDrawables = context.getResources().obtainTypedArray(
                R.array.typedArrCommentNumberDrawables);
        typedArrNewCommentNumberDrawables = context.getResources().obtainTypedArray(
                R.array.typedArrNewCommentNumberDrawables);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return listComment.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return listComment.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        // TODO Auto-generated method stub
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.row_comment, null);
            holder = new ViewHolder();
            holder.tvNumber = (TextView)convertView.findViewById(R.id.tv_comment_number);
            holder.tvText = (TextView)convertView.findViewById(R.id.tv_comment_text);
            holder.ivImage = (ImageView)convertView.findViewById(R.id.iv_comment_image);
            holder.tvUpVoteCount = (TextView)convertView
                    .findViewById(R.id.tv_comment_up_vote_count);
            holder.tvDownVoteCount = (TextView)convertView
                    .findViewById(R.id.tv_comment_down_vote_count);
            holder.ivUpVoteState = (ImageView)convertView
                    .findViewById(R.id.iv_comment_up_vote_state);
            holder.ivDownVoteState = (ImageView)convertView
                    .findViewById(R.id.iv_comment_down_vote_state);
            holder.ivReportState = (ImageView)convertView
                    .findViewById(R.id.iv_comment_report_state);
            holder.llUpVote = (LinearLayout)convertView.findViewById(R.id.ll_comment_up_vote);
            holder.llDownVote = (LinearLayout)convertView.findViewById(R.id.ll_comment_down_vote);
            holder.llReport = (LinearLayout)convertView.findViewById(R.id.ll_comment_report);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        /**/
        /* set Comment data */
        Comment comment = (Comment)getItem(position);

        /* Comment number and status */
        holder.tvNumber.setText(String.valueOf(position + 1));
        int drawable = 0;
        int index = position % 7;
        Integer isNew = comment.getNew();
        if (isNew != null && isNew != DefaultConstants.DEFAULT_INTEGER) {
            drawable = typedArrNewCommentNumberDrawables.getResourceId(index, 0);
        } else {
            drawable = typedArrCommentNumberDrawables.getResourceId(index, 0);
        }
        Utils.setViewBackground(mContext, holder.tvNumber, drawable);

        /* Image */
        String taskId = String.valueOf(comment.getCommentId());
        String base64Image = comment.getImage();
        if (base64Image != null && !base64Image.equals("")) {
            holder.ivImage.setVisibility(View.VISIBLE);
            base64Image = base64Image.substring(base64Image.indexOf(","));
        } else {
            holder.ivImage.setVisibility(View.GONE);
        }
        AsyncImageLoader.loadBitmap(mContext, taskId, base64Image, holder.ivImage);

        /* Text */
        String text = comment.getText();
        if (text != null && !text.equals("")) {
            holder.tvText.setText(text);
        } else {
            holder.tvText.setText("");
        }

        /* Vote */
        Integer vote = comment.getVote();
        if (vote != null && vote == DefaultConstants.VOTE_UP) {
            holder.ivUpVoteState.setImageResource(R.drawable.ic_comment_like_select);
            holder.ivDownVoteState.setImageResource(R.drawable.ic_comment_dislike);
        } else if (vote != null && vote == DefaultConstants.VOTE_DOWN) {
            holder.ivUpVoteState.setImageResource(R.drawable.ic_comment_like);
            holder.ivDownVoteState.setImageResource(R.drawable.ic_comment_dislike_select);
        } else {
            holder.ivUpVoteState.setImageResource(R.drawable.ic_comment_like);
            holder.ivDownVoteState.setImageResource(R.drawable.ic_comment_dislike);
        }
        Integer upVotes = comment.getUpvotes();
        if (upVotes != null && upVotes != DefaultConstants.DEFAULT_INTEGER) {
            holder.tvUpVoteCount.setText(String.valueOf(comment.getUpvotes()));
        } else {
            holder.tvUpVoteCount.setText("");
        }
        Integer downVotes = comment.getDownvotes();
        if (downVotes != null && downVotes != DefaultConstants.DEFAULT_INTEGER) {
            holder.tvDownVoteCount.setText(String.valueOf(comment.getDownvotes()));
        } else {
            holder.tvDownVoteCount.setText("");
        }

        /**/
        /* handle Actions */

        /* handle Up vote button click */
        holder.llUpVote.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                ((ListView)parent).performItemClick(v, position, Row.ITEM_UP_VOTE.ordinal());
            }
        });

        /* handle Down vote button click */
        holder.llDownVote.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                ((ListView)parent).performItemClick(v, position, Row.ITEM_DOWN_VOTE.ordinal());
            }
        });

        /* handle Report button click */
        holder.ivReportState.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                ((ListView)parent).performItemClick(v, position, Row.ITEM_REPORT.ordinal());
            }
        });

        return convertView;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    /**
     * view holder
     */
    static class ViewHolder {
        TextView tvNumber, tvText, tvUpVoteCount, tvDownVoteCount;
        ImageView ivImage, ivUpVoteState, ivDownVoteState, ivReportState;
        LinearLayout llUpVote, llDownVote, llReport;
    }

    /**
     * notify listview on dataset changed
     * 
     * @param list
     */
    public void notifyDataSetChanged(List<Comment> list) {
        // TODO Auto-generated method stub
        listComment = list;
        super.notifyDataSetChanged();
    }
}
