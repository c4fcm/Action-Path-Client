package org.actionpath.issues;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.actionpath.DatabaseManager;
import org.actionpath.MainActivity;
import org.actionpath.logging.LoggerService;
import org.actionpath.util.Installation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InvalidObjectException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Acts as a singleton wrapper around list of issues.  Call getInstance() to get one.
 * Created by rahulb on 5/26/15.
 */
public class IssueDatabase {

    public String TAG = IssueDatabase.class.getName();

    public static IssueDatabase dbInstance = null;

    private IssueDatabase(){
    }

    public static final IssueDatabase getInstance(){
        if(dbInstance==null){
            dbInstance = new IssueDatabase();
        }
        return dbInstance;
    }

    public void add(Issue issue) {
        Log.d(TAG,"Adding issue "+issue.id);
        DatabaseManager db = DatabaseManager.getInstance();
        db.insertIssue(issue);
        db.close();
    }

    public void add(ArrayList<Issue> issueList) {
        DatabaseManager db = DatabaseManager.getInstance();
        for(Issue i:issueList){
            db.insertIssue(i);
        }
        db.close();
    }

    public static ArrayList<Issue> getAll(){
        DatabaseManager db = DatabaseManager.getInstance();
        ArrayList<Issue> issues = db.getAllIssues();
        db.close();
        return issues;
    }

    public static Issue getById(int id){
        DatabaseManager db = DatabaseManager.getInstance();
        Issue issue = db.getIssue(id);
        db.close();
        return issue;
    }

    public void loadNewIssues(){
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    URL u = new URL(MainActivity.SERVER_BASE_URL + "/places/9841/issues/");
                    InputStream in = u.openStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                    parseResult(result.toString());
                    Log.i(TAG, "Successfully pulled new issues from " + MainActivity.SERVER_BASE_URL + "/places/9841/issues/");
                } catch (MalformedURLException ex) {
                    Log.e(TAG, "Failed to pull new issues from " + MainActivity.SERVER_BASE_URL + "/places/9841/issues/ | " + ex.toString());
                } catch (IOException ex){
                    Log.e(TAG, "Failed to pull new issues from " + MainActivity.SERVER_BASE_URL + "/places/9841/issues/ | " + ex.toString());
                }
            }
        });
        thread.start();
    }

    // parse result from server and send info to create geofences
    public void parseResult(String result){
        //TODO: replace with a real JSON parser (http://stackoverflow.com/questions/9605913/how-to-parse-json-in-android)
        int newIssueCount = 0;
        List<String> items = Arrays.asList(result.split("\\{"));
        ArrayList<Issue> newIssues = new ArrayList<Issue>();
        for (int i=1; i< items.size(); i++){
            String single_issue = items.get(i);
            List<String> contents = Arrays.asList(single_issue.split(",\"(.*?)\":"));
            int id = Integer.parseInt(contents.get(0).substring(5));
            String status = contents.get(1).replace("\"", "");
            String summary = contents.get(2).replace("\"", "");
            String description = contents.get(3).replace("\"", "");
            double latitude = Double.parseDouble(contents.get(4).replace("\"", ""));
            double longitude = Double.parseDouble(contents.get(5).replace("\"", ""));
            String address = contents.get(6).replace("\"", "");
            String picture = contents.get(7).replace("\"", "");
            if( (picture==null) || (picture.equals("null")) ) { picture = ""; };
            String dtCreate = contents.get(8).replace("\"", "");
            String dtUpdate = contents.get(9).replace("\"", "");
            // TODO: STRING --> DATE DOESN'T WORK -- do we need to convert these to dateformat? -EG
            Date created_at = stringToDate(dtCreate,"yyyy-MM-dd'T'HH:mm:ss'Z'");
            Date updated_at = stringToDate(dtUpdate,"yyyy-MM-dd'T'HH:mm:ss'Z'");
            int place_id = Integer.parseInt(contents.get(10).substring(0, contents.get(10).length() - 2));
            Issue newIssue = new Issue(id, status, summary, description, latitude, longitude, address, picture, created_at, updated_at, place_id);
            Log.d(TAG, "  AddedIssue " + newIssue);
            newIssues.add(newIssue);
            newIssueCount++;
        }
        this.add(newIssues);
        Log.d(TAG, "Added " + newIssueCount + " geofence");

    }

    // TODO: stringToDate doesn't work
    private Date stringToDate(String aDate,String aFormat) {

        if(aDate==null) return null;
        ParsePosition pos = new ParsePosition(0);
        SimpleDateFormat simpledateformat = new SimpleDateFormat(aFormat);
        Date stringDate = simpledateformat.parse(aDate, pos);
        return stringDate;

    }


}
