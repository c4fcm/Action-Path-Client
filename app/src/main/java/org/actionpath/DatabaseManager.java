package org.actionpath;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.actionpath.issues.Issue;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by rahulb on 7/10/15.
 */
public class DatabaseManager {

    public static DatabaseManager instance;

    public String TAG = this.getClass().getName();

    public static final int VERSION = 2;

    public static final String DATABASE_PATH = "/data/data/org.actionpath/databases/actionpath.db";
    private static final int INVALID_VERSION = -1;
    public static final String VERSION_TABLE_NAME = "version";
    public static final String LOGS_TABLE_NAME = "logs";
    public static final String ISSUES_TABLE_NAME = "issues";

    private static final String ISSUE_COL_NAMES = "id,status,summary,description,latitude,longitude,address,imageUrl,created_at,updated_at, place_id";

    public static final Integer LOG_STATUS_NEW = 0;
    public static final Integer LOG_STATUS_SYNCING = 1;
    public static final Integer LOG_STATUS_DID_NOT_SYNC = 2;

    private SQLiteDatabase db;

    public DatabaseManager(Context context){
        if(context!=null) {
            this.db = context.openOrCreateDatabase(DATABASE_PATH, context.MODE_PRIVATE, null);
            createVersionTable();
            if (getVersion() != VERSION) {
                dropAllTables();
                createAllTables();
                updateVersion();
            }
            Log.d(TAG, "Opened DB (version " + getVersion() + ")");
        } else {
            this.db = SQLiteDatabase.openOrCreateDatabase(DATABASE_PATH, null, null);
        }
    }

    private void createAllTables(){
        createLogsTable();
        createIssueTable();
    }

    private void updateVersion(){
        this.db.execSQL("INSERT OR REPLACE INTO " + VERSION_TABLE_NAME + " VALUES (" + VERSION + ")");
    }

    private int getVersion() {
        String searchQuery = "SELECT  * FROM " + VERSION_TABLE_NAME;
        Cursor cursor = this.db.rawQuery(searchQuery, null);
        ArrayList<Integer> logIds = new ArrayList<Integer>();
        cursor.moveToFirst();
        int versionInDb;
        try {
            versionInDb = cursor.getInt(0);
            cursor.close();
        } catch(CursorIndexOutOfBoundsException e){
            Log.w(TAG,"No version in db");
            versionInDb = INVALID_VERSION;
        }
        return versionInDb;
    }

    public int getIssueCount() {
        String searchQuery = "SELECT  count(*) FROM " + ISSUES_TABLE_NAME;
        Cursor cursor = this.db.rawQuery(searchQuery, null);
        ArrayList<Integer> logIds = new ArrayList<Integer>();
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        return count;
    }

    private void dropAllTables(){
        this.db.execSQL("DROP TABLE IF EXISTS " + LOGS_TABLE_NAME);
        this.db.execSQL("DROP TABLE IF EXISTS " + ISSUES_TABLE_NAME);
    }

    private void createIssueTable(){
        Log.i(TAG, "Creating Issues Table");
        this.db.execSQL("CREATE TABLE IF NOT EXISTS "
                + ISSUES_TABLE_NAME
                + " (id INT PRIMARY KEY, status VARCHAR, summary VARCHAR, description VARCHAR, latitude DOUBLE, longitude DOUBLE, " +
                  " address VARCHAR, imageUrl VARCHAR, created_at INTEGER, updated_at INTEGER, place_id INT, " +
                  " favorited INTEGER DEFAULT 0, geofence_created INTEGER DEFAULT 0);");
    }

    public void insertIssue(Issue i){
        Long createdTime = (i.getCreatedAt()!=null) ? i.getCreatedAt().getTime() : null;
        Long updatedTime= (i.getUpdatedAt()!=null) ? i.getUpdatedAt().getTime() : null;
        this.db.execSQL("INSERT OR REPLACE INTO " + ISSUES_TABLE_NAME
                + "("+ISSUE_COL_NAMES+") "
                + "VALUES (" + i.getId() + ","
                + DatabaseUtils.sqlEscapeString(i.getStatus()) + ","
                + DatabaseUtils.sqlEscapeString(i.getIssueSummary()) + ","
                + DatabaseUtils.sqlEscapeString(i.getIssueDescription())+ ","
                + i.getLatitude() + "," + i.getLongitude() + ", "
                + DatabaseUtils.sqlEscapeString(i.getIssueAddress()) + ","
                + DatabaseUtils.sqlEscapeString(i.getImageUrl()) + ","
                + createdTime + "," + updatedTime + "," + i.getPlaceId() + ")");
    }

    private void createLogsTable(){
        Log.i(TAG,"Creating Logs Table");
        this.db.execSQL("CREATE TABLE IF NOT EXISTS "
                + LOGS_TABLE_NAME
                + " (timestamp VARCHAR, installID VARCHAR, issueID VARCHAR, lat VARCHAR, long VARCHAR, " +
                "actionType VARCHAR, status INT,id integer primary key autoincrement);");
    }

    public void insertLog(ArrayList<String> splitAction, String latitude, String longitude) {
        this.db.execSQL("INSERT INTO "
                + LOGS_TABLE_NAME + "(timestamp, installID, issueID, lat, long, actionType, status) "
                + " VALUES ("
                + DatabaseUtils.sqlEscapeString(splitAction.get(0)) + ","
                + DatabaseUtils.sqlEscapeString(splitAction.get(1)) + ","
                + DatabaseUtils.sqlEscapeString(splitAction.get(2)) + ","
                + DatabaseUtils.sqlEscapeString(latitude) + ","
                + DatabaseUtils.sqlEscapeString(longitude) + ","
                + DatabaseUtils.sqlEscapeString(splitAction.get(3)) + ","
                + LOG_STATUS_NEW + ");");
    }

    public Cursor getLogsToSyncCursor(){
        String searchQuery = "SELECT  * FROM " + LOGS_TABLE_NAME +
                " where status="+LOG_STATUS_NEW+" OR status="+LOG_STATUS_DID_NOT_SYNC;
        return this.db.rawQuery(searchQuery, null);
    }

    public void updateLogStatus(int logId, Integer logStatusSyncing) {
        String updateSql = "UPDATE "+ LOGS_TABLE_NAME +
                " SET status="+logStatusSyncing+" WHERE id="+logId;
        this.db.execSQL(updateSql);
    }

    public void deleteLog(int logId) {
        String updateSql = "DELETE FROM "+ LOGS_TABLE_NAME + " WHERE id="+logId;
        this.db.execSQL(updateSql);
    }

    private void createVersionTable(){
        this.db.execSQL("CREATE TABLE IF NOT EXISTS " + VERSION_TABLE_NAME + " (version INT);");
    }

    public void close(){
        Log.d(TAG,"Closing DB");
        this.db.close();
    }

    public static DatabaseManager getInstance(Context context){
        if(instance==null){
            instance = new DatabaseManager(context);
        }
        return instance;
    }

    public static DatabaseManager getInstance(){
        if(instance==null){
            instance = new DatabaseManager(null);
        }
        return instance;
    }

    public void updateIssueFavorited(int issueId, boolean isFavorited) {
        String updateSql = "UPDATE "+ ISSUES_TABLE_NAME +
                " SET favorited="+(isFavorited?1:0)+" WHERE id="+issueId;
        this.db.execSQL(updateSql);
    }

    public Issue getIssue(int id) {
        String searchQuery = "SELECT "+ISSUE_COL_NAMES+" FROM " + ISSUES_TABLE_NAME +" WHERE id="+id;
        Cursor cursor = this.db.rawQuery(searchQuery, null);
        ArrayList<Integer> logIds = new ArrayList<Integer>();
        cursor.moveToFirst();
        Issue issue = new Issue();
        try {
            //(id,status,summary,description,latitude,longitude,address,imageUrl,created_at,updated_at, place_id)
            issue = Issue.fromCursor(cursor);
            cursor.close();
        } catch(CursorIndexOutOfBoundsException e){
            cursor.close();
            Log.w(TAG,"No issue "+id+" in database");
            issue = null;
        }
        return issue;
    }

    public ArrayList<Issue> getAllIssues() {
        String searchQuery = "SELECT "+ISSUE_COL_NAMES+" FROM " + ISSUES_TABLE_NAME;
        Cursor cursor = this.db.rawQuery(searchQuery, null);
        ArrayList<Issue> issues = new ArrayList<Issue>();
        cursor.moveToFirst();
        while (cursor.isAfterLast() == false) {
            Issue issue = Issue.fromCursor(cursor);
            issues.add(issue);
            cursor.moveToNext();
        }
        cursor.close();
        return issues;
    }

}
