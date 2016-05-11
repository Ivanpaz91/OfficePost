
package com.officewall.database;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBAdapter {

    // database instances
    private static DatabaseHelper DBHelper;
    private static SQLiteDatabase SQLiteDB;

    // database properties
    public static final String DB_PATH = "/data/data/com.officewall.activities/databases/db_officewall.sql";
    public static final String DB_NAME = "db_officewall.sql";
    private static final int DB_VERSION = 1;

    /**
     * TABLES in the database.
     */
    private static final String TBL_USER_WALL = "tblUserWall";
    private static final String TBL_POST = "tblPost";
    private static final String TBL_COMMENT = "tblComment";

    /**
     * COLUMNS in the table User Wall.
     */
    private static final String COL_WALL_USER_ID = "userId";
    private static final String COL_WALL_WALL_ID = "wallId";
    private static final String COL_WALL_WALL_NAME = "wallName";
    private static final String COL_WALL_WALL_DOMAIN = "wallDomain";
    private static final String COL_WALL_USER_EMAIL = "userEmail";
    private static final String COL_WALL_NEW_ITEMS = "newItems";

    /**
     * COLUMNS in the table Post.
     */
    private static final String COL_POST_WALL_ID = "wallId";
    private static final String COL_POST_POST_ID = "postId";
    private static final String COL_POST_TEXT = "text";
    private static final String COL_POST_IMAGE = "image";
    private static final String COL_POST_BG_COLOR = "bgColor";
    private static final String COL_POST_IS_NEW = "isNew";
    private static final String COL_POST_UP_VOTE_COUNT = "upVoteCount";
    private static final String COL_POST_DOWN_VOTE_COUNT = "downVoteCount";
    private static final String COL_POST_TOTAL_COMMENT = "totalComment";
    private static final String COL_POST_NEW_COMMENT = "newComment";
    private static final String COL_POST_VOTE = "vote";
    private static final String COL_POST_REPORT = "report";
    private static final String COL_POST_STATUS = "status";

    /**
     * COLUMNS in the table Comment.
     */
    private static final String COL_COMMENT_POST_ID = "postId";
    private static final String COL_COMMENT_COMMENT_ID = "commentId";
    private static final String COL_COMMENT_TEXT = "text";
    private static final String COL_COMMENT_IMAGE = "image";
    private static final String COL_COMMENT_IS_NEW = "isNew";
    private static final String COL_COMMENT_UP_VOTE_COUNT = "upVoteCount";
    private static final String COL_COMMENT_DOWN_VOTE_COUNT = "downVoteCount";
    private static final String COL_COMMENT_VOTE = "vote";
    private static final String COL_COMMENT_REPORT = "report";

    /**
     * constructor
     * 
     * @param context
     */
    public DBAdapter(Context context) {
        // TODO Auto-generated constructor stub
        DBHelper = new DatabaseHelper(context);
    }

    /**
     * sqlite open helper class
     */
    public static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase mSQLiteDB) {
            // create database
        }

        @Override
        public void onUpgrade(SQLiteDatabase mSQLiteDB, int oldVersion, int newVersion) {
            onCreate(mSQLiteDB);
        }
    }

    /**
     * open database to write into
     */
    public void openToWrite() throws SQLException {
        SQLiteDB = DBHelper.getWritableDatabase();
    }

    /**
     * open database to read from
     */
    public void openToFetch() throws SQLException {
        SQLiteDB = DBHelper.getReadableDatabase();
    }

    /**
     * close databse
     */
    public void close() {
        DBHelper.close();
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
        // build query
        String query = "insert or replace into " + TBL_USER_WALL + "(" + COL_WALL_USER_ID + ","
                + COL_WALL_WALL_ID + "," + COL_WALL_WALL_NAME + "," + COL_WALL_WALL_DOMAIN + ","
                + COL_WALL_USER_EMAIL + "," + COL_WALL_NEW_ITEMS + ") values('" + userId + "','"
                + wallId + "','" + wallName + "','" + wallDomain + "','" + userEmail + "','"
                + newItems + "')";
        printQuery(query);

        // execute query
        SQLiteDB.execSQL(query);
    }

    /**
     * print query
     * 
     * @param query
     */
    private void printQuery(String query) {
        // TODO Auto-generated method stub
        Log.i("DB Query..", query);
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
        // build query
        String query = "insert or replace into " + TBL_POST + "(" + COL_POST_WALL_ID + ","
                + COL_POST_POST_ID + "," + COL_POST_TEXT + "," + COL_POST_IMAGE + ","
                + COL_POST_BG_COLOR + "," + COL_POST_IS_NEW + "," + COL_POST_UP_VOTE_COUNT + ","
                + COL_POST_DOWN_VOTE_COUNT + "," + COL_POST_TOTAL_COMMENT + ","
                + COL_POST_NEW_COMMENT + "," + COL_POST_VOTE + "," + COL_POST_REPORT + ","
                + COL_POST_STATUS + ") values('" + wallId + "','" + postId + "','" + text + "','"
                + image + "','" + bgColor + "','" + isNew + "','" + upVoteCount + "','"
                + downVoteCount + "','" + totalComment + "','" + newComment + "','" + vote + "','"
                + report + "','" + status + "')";
        printQuery(query);

        // execute query
        SQLiteDB.execSQL(query);
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
        // build query
        String query = "insert or replace into " + TBL_COMMENT + "(" + COL_COMMENT_POST_ID + ","
                + COL_COMMENT_COMMENT_ID + "," + COL_COMMENT_TEXT + "," + COL_COMMENT_IMAGE + ","
                + COL_COMMENT_IS_NEW + "," + COL_COMMENT_UP_VOTE_COUNT + ","
                + COL_COMMENT_DOWN_VOTE_COUNT + "," + COL_COMMENT_VOTE + "," + COL_COMMENT_REPORT
                + ") values('" + postId + "','" + commentId + "','" + text + "','" + image + "','"
                + isNew + "','" + upVoteCount + "','" + downVoteCount + "','" + vote + "','"
                + report + "')";
        printQuery(query);

        // execute query
        SQLiteDB.execSQL(query);
    }

    /**
     * Fetch Queries
     * *****************************************************************
     */

    /**
     * get user walls
     * 
     * @param userId
     * @return cursor
     */
    public Cursor getUserWalls(String userId) {
        // TODO Auto-generated method stub
        // build query
        String query = "select *from " + TBL_USER_WALL + " where " + COL_WALL_USER_ID + "=" + "'"
                + userId + "'";
        printQuery(query);

        // return result
        return SQLiteDB.rawQuery(query, null);
    }

    /**
     * get posts
     * 
     * @param wallId
     * @param startFrom
     * @param maxToLoad
     * @return cursor
     */
    public Cursor getPosts(String wallId, int startFrom, int maxToLoad) {
        // TODO Auto-generated method stub
        // build query
        String query = "select *from " + TBL_POST + " where " + COL_POST_WALL_ID + "=" + "'"
                + wallId + "'" + " LIMIT " + maxToLoad + " OFFSET " + startFrom;
        printQuery(query);

        // return result
        return SQLiteDB.rawQuery(query, null);
    }

    /**
     * get comments
     * 
     * @param postId
     * @param startFrom
     * @param maxToLoad
     * @return cursor
     */
    public Cursor getComments(String postId, int startFrom, int maxToLoad) {
        // TODO Auto-generated method stub
        // build query
        String query = "select *from " + TBL_COMMENT + " where " + COL_COMMENT_POST_ID + "=" + "'"
                + postId + "'" + " LIMIT " + maxToLoad + " OFFSET " + startFrom;
        printQuery(query);

        // return result
        return SQLiteDB.rawQuery(query, null);
    }

}
