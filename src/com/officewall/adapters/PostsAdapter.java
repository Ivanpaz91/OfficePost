
package com.officewall.adapters;

import java.util.Arrays;
import java.util.List;

import com.officewall.activities.R;
import com.officewall.constants.DefaultConstants;
import com.officewall.imageloader.AsyncImageLoader;
import com.officewall.pojo.core.Post;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class PostsAdapter extends BaseAdapter {

    private Context mContext;
    private List<Post> listPost;

    // enum Row item
    public static enum Row {
        ROW, ITEM_COMMENT, ITEM_MORE_OPTIONS, ITEM_UP_VOTE, ITEM_DOWN_VOTE
    }

    // post bg lists
    private List<String> listColorCodes;
    private TypedArray typedArrColorDrawables;

    /**
     * Constructor
     * 
     * @param context
     * @param list
     */
    public PostsAdapter(Context context, List<Post> list) {
        mContext = context;
        listPost = list;
        listColorCodes = Arrays.asList(context.getResources().getStringArray(
                R.array.arrPostColorCodes));
        typedArrColorDrawables = context.getResources().obtainTypedArray(
                R.array.typedArrPostColorDrawables);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return listPost.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return listPost.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        // TODO Auto-generated method stub
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.row_post, null);
            holder = new ViewHolder();
            holder.ivBgColor = (ImageView)convertView.findViewById(R.id.iv_post_bg_color);
            holder.ivImage = (ImageView)convertView.findViewById(R.id.iv_post_image);
            holder.ivNewPost = (ImageView)convertView.findViewById(R.id.iv_post_new);
            holder.ivMoreOptions = (ImageView)convertView.findViewById(R.id.iv_post_more_options);
            holder.ivCommentState = (ImageView)convertView.findViewById(R.id.iv_post_comment_state);
            holder.ivUpVoteState = (ImageView)convertView.findViewById(R.id.iv_post_up_vote_state);
            holder.ivDownVoteState = (ImageView)convertView
                    .findViewById(R.id.iv_post_down_vote_state);
            holder.tvText = (TextView)convertView.findViewById(R.id.tv_post_text);
            holder.tvCommentCount = (TextView)convertView.findViewById(R.id.tv_post_comment_count);
            holder.tvUpVoteCount = (TextView)convertView.findViewById(R.id.tv_post_up_vote_count);
            holder.tvDownVoteCount = (TextView)convertView
                    .findViewById(R.id.tv_post_down_vote_count);
            holder.llComment = (LinearLayout)convertView.findViewById(R.id.ll_post_comment);
            holder.llUpVote = (LinearLayout)convertView.findViewById(R.id.ll_post_up_vote);
            holder.llDownVote = (LinearLayout)convertView.findViewById(R.id.ll_post_down_vote);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        /**/
        /* set Post data */
        final Post post = (Post)getItem(position);

        /* Background color */
        String bgColor = post.getBgColor();
        if (bgColor != null && !bgColor.equals("")) {
            int index = listColorCodes.indexOf(bgColor);
            if (index != -1) {
                holder.ivBgColor.setImageResource(typedArrColorDrawables.getResourceId(index, 0));
            } else {
                holder.ivBgColor.setImageResource(0);
            }
        } else {
            holder.ivBgColor.setImageResource(0);
        }

        /* Post Image */
        String taskId = String.valueOf(post.getPostId());
        String base64Image = post.getImage();
        if (base64Image != null && !base64Image.equals("")) {
            base64Image = base64Image.substring(base64Image.indexOf(","));
        }
        AsyncImageLoader.loadBitmap(mContext, taskId, base64Image, holder.ivImage);

        /* Text */
        String text = post.getText();
        if (text != null && !text.equals("")) {
            holder.tvText.setText(text);
            holder.tvText.setVisibility(View.VISIBLE);
        } else {
            holder.tvText.setText("");
            holder.tvText.setVisibility(View.GONE);
        }

        /* New post */
        Integer isNew = post.getNew();
        if (isNew != null && isNew != DefaultConstants.DEFAULT_INTEGER) {
            holder.ivNewPost.setVisibility(View.VISIBLE);
        } else {
            holder.ivNewPost.setVisibility(View.INVISIBLE);
        }

        /* Comment */
        Integer newComments = post.getNewComments();
        if (newComments != null && newComments != DefaultConstants.DEFAULT_INTEGER) {
            holder.ivCommentState.setImageResource(R.drawable.ic_wall_tile_chat_select);
        } else {
            holder.ivCommentState.setImageResource(R.drawable.ic_wall_tile_chat);
        }
        Integer totalComments = post.getTotalComments();
        if (totalComments != null && totalComments != DefaultConstants.DEFAULT_INTEGER) {
            holder.tvCommentCount.setText(String.valueOf(post.getTotalComments()));
        } else {
            holder.tvCommentCount.setText("");
        }

        /* Vote */
        Integer vote = post.getVote();
        if (vote != null && vote == DefaultConstants.VOTE_UP) {
            holder.ivUpVoteState.setImageResource(R.drawable.ic_wall_tile_like_select);
            holder.ivDownVoteState.setImageResource(R.drawable.ic_wall_tile_dislike);
        } else if (vote != null && vote == DefaultConstants.VOTE_DOWN) {
            holder.ivUpVoteState.setImageResource(R.drawable.ic_wall_tile_like);
            holder.ivDownVoteState.setImageResource(R.drawable.ic_wall_tile_dislike_select);
        } else {
            holder.ivUpVoteState.setImageResource(R.drawable.ic_wall_tile_like);
            holder.ivDownVoteState.setImageResource(R.drawable.ic_wall_tile_dislike);
        }
        Integer upVotes = post.getUpvotes();
        if (upVotes != null && upVotes != DefaultConstants.DEFAULT_INTEGER) {
            holder.tvUpVoteCount.setText(String.valueOf(post.getUpvotes()));
        } else {
            holder.tvUpVoteCount.setText("");
        }
        Integer downVotes = post.getDownvotes();
        if (downVotes != null && downVotes != DefaultConstants.DEFAULT_INTEGER) {
            holder.tvDownVoteCount.setText(String.valueOf(post.getDownvotes()));
        } else {
            holder.tvDownVoteCount.setText("");
        }

        /**/
        /* handle Actions */

        /* handle Row click */
        convertView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                ((ListView)parent).performItemClick(v, position, Row.ROW.ordinal());
            }
        });

        /* handle Comment button click */
        holder.llComment.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                ((ListView)parent).performItemClick(v, position, Row.ITEM_COMMENT.ordinal());
            }
        });

        /* handle More button click */
        holder.ivMoreOptions.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                ((ListView)parent).performItemClick(v, position, Row.ITEM_MORE_OPTIONS.ordinal());
            }
        });

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
        ImageView ivBgColor, ivImage, ivNewPost, ivMoreOptions, ivCommentState, ivUpVoteState,
                ivDownVoteState;
        TextView tvText, tvCommentCount, tvUpVoteCount, tvDownVoteCount;
        LinearLayout llComment, llUpVote, llDownVote;
    }

    /**
     * notify listview on dataset changed
     * 
     * @param list
     */
    public void notifyDataSetChanged(List<Post> list) {
        // TODO Auto-generated method stub
        listPost = list;
        super.notifyDataSetChanged();
    }
}
