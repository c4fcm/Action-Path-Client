package org.actionpath.logging;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

public class LogSyncService extends IntentService{

    public LogSyncService(){
        super("LogSyncService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // TODO Auto-generated method stub
        String logType = intent.getStringExtra("syncType");
        if(logType.equals("send")) {
            Log.d("LogSyncService", "Request to send new logs");
            sendSQL();
        }
    }

    private JSONArray getResults(){
        SQLiteDatabase myDataBase = SQLiteDatabase.openDatabase(LoggerService.DATABASE_PATH, null, SQLiteDatabase.OPEN_READONLY);
        String searchQuery = "SELECT  * FROM " + LoggerService.DB_TABLE_NAME;
        Cursor cursor = myDataBase.rawQuery(searchQuery, null );
        JSONArray resultSet = new JSONArray();
        cursor.moveToFirst();
        while (cursor.isAfterLast() == false) {
            int totalColumn = cursor.getColumnCount();
            JSONObject rowObject = new JSONObject();
            for( int i=0 ;  i< totalColumn ; i++ ){
                if( cursor.getColumnName(i) != null ){
                    try{
                        if( cursor.getString(i) != null ){
                            //Log.d("LogSyncService", "  "+cursor.getString(i));
                            rowObject.put(cursor.getColumnName(i), cursor.getString(i));
                        }else{
                            rowObject.put( cursor.getColumnName(i) ,  "" );
                        }
                    }catch( Exception e ){
                        Log.e("LogSyncService", e.getMessage()  );
                    }
                }
            }
            resultSet.put(rowObject);
            cursor.moveToNext();
        }
        cursor.close();
        Log.d("LogSyncService", "JSON TO UPLOAD: "+resultSet.toString());
        return resultSet;
    }


    public void sendSQL() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                JSONArray sendJSON = getResults();
                // Create a new HttpClient and Post Header
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("https://api.dev.actionpath.org/logs");
                try {
                    // Add your data
                    httppost.setEntity(new ByteArrayEntity(sendJSON.toString().getBytes("UTF8")));
                    // Execute HTTP Post Request
                    HttpResponse response = httpclient.execute(httppost);
                } catch (ClientProtocolException e) {
                    // TODO Auto-generated catch block
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                }
            }
        });
        thread.start();
    }


}
