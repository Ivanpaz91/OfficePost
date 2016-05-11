
package com.officewall.database;

import java.util.ArrayList;
import java.util.List;

import com.officewall.pojo.core.Comment;
import com.officewall.pojo.core.Post;
import com.officewall.pojo.core.UserWall;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;

/**
 * The class DBHandler handles query request from front end and send to back end
 */
public class DBHandler {

    private Context mContext;

    /**
     * constructor
     * 
     * @param context
     */
    public DBHandler(Context context) {
        // TODO Auto-generated constructor stub
        mContext = context;
    }

    /**
     * @param context
     * @return DBHandler instance
     */
    public static DBHandler getInstance(Context context) {
        // TODO Auto-generated method stub
        return new DBHandler(context);
    }

    /**
     * Write Queries
     * *****************************************************************
     */

    /**
     * insert into table User Wall
     * 
     * @param userId
     * @param wallId
     * @param wallName
     * @param wallDomain
     * @param userEmail
     * @param newItems
     */
    public void insertIntoUserWall(String userId, String wallId, String wallName,
            String wallDomain, String userEmail, String newItems) {
        // TODO Auto-generated method stub
        try {
            // instantiate DBHelper
            DBAdapter dbAdapter = new DBAdapter(mContext);
            // open database
            dbAdapter.openToWrite();
            // query database
            dbAdapter.insertIntoUserWall(userId, wallId, wallName, wallDomain, userEmail, newItems);
            // close database
            dbAdapter.close();
        } catch (SQLException e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    /**
     * insert into table Post
     * 
     * @param wallId
     * @param postId
     * @param text
     * @param image
     * @param bgColor
     * @param isNew
     * @param upVoteCount
     * @param downVoteCount
     * @param totalComment
     * @param newComment
     * @param vote
     * @param report
     * @param status
     */
    public void insertIntoPost(String wallId, int postId, String text, String image,
            String bgColor, int isNew, int upVoteCount, int downVoteCount, int totalComment,
            int newComment, int vote, int report, int status) {
        // TODO Auto-generated method stub
        try {
            // instantiate DBHelper
            DBAdapter dbAdapter = new DBAdapter(mContext);
            // open database
            dbAdapter.openToWrite();
            // query database
            dbAdapter.insertIntoPost(wallId, postId, text, image, bgColor, isNew, upVoteCount,
                    downVoteCount, totalComment, newComment, vote, report, status);
            // close database
            dbAdapter.close();
        } catch (SQLException e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    /**
     * insert into table Comment
     * 
     * @param postId
     * @param commentId
     * @param text
     * @param image
     * @param isNew
     * @param upVoteCount
     * @param downVoteCount
     * @param vote
     * @param report
     */
    public void insertIntoComment(int postId, int commentId, String text, String image, int isNew,
            int upVoteCount, int downVoteCount, int vote, int report) {
        // TODO Auto-generated method stub
        try {
            // instantiate DBHelper
            DBAdapter dbAdapter = new DBAdapter(mContext);
            // open database
            dbAdapter.openToWrite();
            // query database
            dbAdapter.insertIntoComment(postId, commentId, text, image, isNew, upVoteCount,
                    downVoteCount, vote, report);
            // close database
            dbAdapter.close();
        } catch (SQLException e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    /**
     * Fetch Queries
     * *****************************************************************
     */

    /**
     * get user walls
     * 
     * @param userId
     * @return wall list
     */
    public List<UserWall> getUserWalls(String userId) {
        // TODO Auto-generated method stub
        // wall list
        List<UserWall> listWall = null;

        try {
            // instantiate DBHelper
            DBAdapter dbAdapter = new DBAdapter(mContext);
            // open database
            dbAdapter.openToFetch();
            // query database
            Cursor cursor = dbAdapter.getUserWalls(userId);
            if (cursor != null) {
                int count = cursor.getCount();
                if (count > 0) {
                    // move cursor to start
                    cursor.moveToFirst();
                    // create list
                    listWall = new ArrayList<UserWall>(cursor.getCount());
                    do {
                        UserWall wall = new UserWall();
                        wall.setWallId(cursor.getString(1));
                        wall.setWallName(cursor.getString(2));
                        wall.setWallDomain(cursor.getString(3));
                        wall.setUserEmail(cursor.getString(4));
                        wall.setNewItems(cursor.getString(5));
                        listWall.add(wall);
                    } while (cursor.moveToNext());
                }
            }
            // close database
            dbAdapter.close();
        } catch (SQLException e) {
            // TODO: handle exception
            e.printStackTrace();
        }

        // return result
        return listWall;
    }

    /**
     * get posts
     * 
     * @param wallId
     * @param startFrom
     * @param maxToLoad
     * @return list
     */
    public List<Post> getPosts(String wallId, int startFrom, int maxToLoad) {
        // TODO Auto-generated method stub
        // wall list
        List<Post> listPost = null;

        try {
            // instantiate DBHelper
            DBAdapter dbAdapter = new DBAdapter(mContext);
            // open database
            dbAdapter.openToFetch();
            // query database
            Cursor cursor = dbAdapter.getPosts(wallId, startFrom, maxToLoad);
            if (cursor != null) {
                int count = cursor.getCount();
                if (count > 0) {
                    // move cursor to start
                    cursor.moveToFirst();
                    // create list
                    listPost = new ArrayList<Post>(cursor.getCount());
                    do {
                        Post post = new Post();
                        post.setPostId(cursor.getInt(1));
                        post.setText(cursor.getString(2));
                        post.setImage(cursor.getString(3));
                        post.setBgColor(cursor.getString(4));
                        post.setNew(cursor.getInt(5));
                        post.setUpvotes(cursor.getInt(6));
                        post.setDownvotes(cursor.getInt(7));
                        post.setTotalComments(cursor.getInt(8));
                        post.setNewComments(cursor.getInt(9));
                        post.setVote(cursor.getInt(10));
                        post.setReport(cursor.getInt(11));
                        post.setStatus(cursor.getInt(12));
                        listPost.add(post);
                    } while (cursor.moveToNext());
                }
            }
            // close database
            dbAdapter.close();
        } catch (SQLException e) {
            // TODO: handle exception
            e.printStackTrace();
        }

        // return result
        return listPost;
    }

    /**
     * get comments
     * 
     * @param postId
     * @param startFrom
     * @param maxToLoad
     * @return list
     */
    public List<Comment> getComments(String postId, int startFrom, int maxToLoad) {
        // TODO Auto-generated method stub
        // wall list
        List<Comment> listComment = null;

        try {
            // instantiate DBHelper
            DBAdapter dbAdapter = new DBAdapter(mContext);
            // open database
            dbAdapter.openToFetch();
            // query database
            Cursor cursor = dbAdapter.getComments(postId, startFrom, maxToLoad);
            if (cursor != null) {
                int count = cursor.getCount();
                if (count > 0) {
                    // move cursor to start
                    cursor.moveToFirst();
                    // create list
                    listComment = new ArrayList<Comment>(cursor.getCount());
                    do {
                        Comment comment = new Comment();
                        comment.setCommentId(cursor.getInt(1));
                        comment.setText(cursor.getString(2));
                        comment.setImage(cursor.getString(3));
                        comment.setNew(cursor.getInt(4));
                        comment.setUpvotes(cursor.getInt(5));
                        comment.setDownvotes(cursor.getInt(6));
                        comment.setVote(cursor.getInt(7));
                        comment.setReport(cursor.getInt(8));
                        listComment.add(comment);
                    } while (cursor.moveToNext());
                }
            }
            // close database
            dbAdapter.close();
        } catch (SQLException e) {
            // TODO: handle exception
            e.printStackTrace();
        }

        // return result
        return listComment;
    }

}
