package org.actionpath.logging;

import android.content.ContentValues;
import android.database.Cursor;

import java.io.Serializable;

public class LogMsg implements Serializable {

    public static final int INVALID_ID = -1;

    public static final Integer LOG_STATUS_READY_TO_SYNC = 0;
    public static final Integer LOG_STATUS_SYNCING = 1;
    public static final Integer LOG_STATUS_DID_NOT_SYNC = 2;
    public static final Integer LOG_STATUS_NEEDS_LOCATION = 3;

    public static final String ACTION_NEWS_FEED_CLICK = "NewsfeedClick";
    public static final String ACTION_NOTIFICATION_IGNORE_CLICK = "NotificationIgnoreClick";
    public static final String ACTION_NOTIFICATION_RESPOND_CLICK = "NotificationRespondClick";
    public static final String ACTION_SURVEY_RESPONSE = "SurveyResponse";
    public static final String ACTION_THANKS_DISMISSED = "ThanksDismissed";
    public static final String ACTION_UNFOLLOWED_ISSUE = "UnfollowedIssue";
    public static final String ACTION_ENTERED_GEOFENCE = "EnteredGeofence";
    public static final String ACTION_LOADED_LATEST_ISSUES = "LoadedLatestIssues";
    public static final String ACTION_INSTALLED_APP = "InstalledApp";
    public static final String ACTION_ADDED_GEOFENCE = "AddedGeofence";

    public static final Integer NO_ISSUE = -1;

    // Fields
    public int id = INVALID_ID;
    public String actionType;
    public String installationId;
    public int issueId;
    public long timestamp;
    public double latitude;
    public double longitude;
    public int status;

    public LogMsg(String actionType, String installationId, int issueId, long timestamp, double latitude, double longitude, int status) {
        this(INVALID_ID, actionType, installationId, issueId, timestamp, latitude, longitude, status);
    }

    public LogMsg(int id, String actionType, String installationId, int issueId, long timestamp, double latitude, double longitude, int status) {
        this.id = id;
        this.actionType = actionType;
        this.installationId = installationId;
        this.issueId = issueId;
        this.timestamp = timestamp;
        this.latitude = latitude;
        this.longitude = longitude;
        this.status = status;
    }

    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(LogsDbHelper.LOGS_ACTION_TYPE_COL, actionType);
        values.put(LogsDbHelper.LOGS_INSTALLATION_ID_COL, installationId);
        values.put(LogsDbHelper.LOGS_ISSUE_ID_COL, issueId);
        values.put(LogsDbHelper.LOGS_TIMESTAMP_COL, timestamp);
        values.put(LogsDbHelper.LOGS_LATITUDE_COL, latitude);
        values.put(LogsDbHelper.LOGS_LONGITUDE_COL, longitude);
        values.put(LogsDbHelper.LOGS_STATUS_COL, status);
        return values;
    }

    public static LogMsg fromCursor(Cursor c) {
        int id = c.getInt(c.getColumnIndex(LogsDbHelper.LOGS_ID_COL));
        String actionType = c.getString(c.getColumnIndex(LogsDbHelper.LOGS_ACTION_TYPE_COL));
        String installationId = c.getString(c.getColumnIndex(LogsDbHelper.LOGS_INSTALLATION_ID_COL));
        int issueId = c.getInt(c.getColumnIndex(LogsDbHelper.LOGS_ISSUE_ID_COL));
        long timestamp = c.getLong(c.getColumnIndex(LogsDbHelper.LOGS_TIMESTAMP_COL));
        Double latitude = c.getDouble(c.getColumnIndex(LogsDbHelper.LOGS_LATITUDE_COL));
        Double longitude = c.getDouble(c.getColumnIndex(LogsDbHelper.LOGS_LONGITUDE_COL));
        int status = c.getInt(c.getColumnIndex(LogsDbHelper.LOGS_STATUS_COL));

        return new LogMsg(id,actionType,installationId,issueId,timestamp,latitude,longitude,status);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LogMsg log = (LogMsg) o;
        if (id != log.id) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = (id+timestamp+actionType+issueId).hashCode();
        return result;
    }

    @Override
    public String toString() {
        return id + " (" + actionType+ " on "+issueId+")";
    }

}