package org.actionpath.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import org.actionpath.db.RequestType;
import org.actionpath.places.Place;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Acts as a singleton to access static app configuration
 */
public class Config {

    private static final String APP_CONFIG_FILE_NAME = "app-config.json";

    private static final String MODE_PICK_PLACE = "pick-place";
    private static final String MODE_ASSIGN_REQUEST_TYPE = "assign-request-type";

    public static String TAG = Config.class.getName();

    private static Config instance;

    private JSONObject appConfig;

    public Config(Context context) {
        try{
            appConfig = readAssetJsonFile(context, APP_CONFIG_FILE_NAME);
        } catch (Exception e){
            Log.e(TAG,"Couldn't load app config :-( "+e.toString());
        }
    }

    private JSONObject readAssetJsonFile(Context context, String fileName) throws IOException,JSONException {
        AssetManager am = context.getAssets();
        InputStream is = am.open(fileName);
        BufferedReader streamReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        StringBuilder responseStrBuilder = new StringBuilder();

        String inputStr;
        while ((inputStr = streamReader.readLine()) != null) {
            responseStrBuilder.append(inputStr);
        }
        return new JSONObject(responseStrBuilder.toString());
    }

    public static Config getInstance(Context context){
        if(instance==null){
            instance = new Config(context);
        }
        return instance;
    }

    public static Config getInstance(){
        if(instance==null){
            throw new RuntimeException("You gotta initialize this with a context before calling it without one!");
        }
        return instance;
    }

    public String getMode(){
        try {
            return appConfig.getString("mode");
        } catch (JSONException e){
            Log.e(TAG,"couldn't read mode from app config");
            return null;
        }
    }

    public Place getPlace(){
        Place place = new Place();
        try{
            JSONObject placeInfo = appConfig.getJSONObject("place");
            place.id = placeInfo.getInt("id");
            place.name = placeInfo.getString("name");
            place.url = placeInfo.getString("url");
        } catch (JSONException e){
            Log.e(TAG,"couldn't read place from app config");
        }
        return place;
    }

    public boolean isPickPlaceMode(){
        return MODE_PICK_PLACE.equals(getMode());
    }

    public boolean isAssignRequestTypeMode(){
        return MODE_ASSIGN_REQUEST_TYPE.equals(getMode());
    }

    public List<RequestType> getValidRequestTypes(){
        ArrayList<RequestType> requestTypes = new ArrayList<RequestType>();
        try {
            JSONArray validRequestTypes = appConfig.getJSONArray("validRequestTypes");
            for (int i = 0; i < validRequestTypes.length(); i++) {
                JSONObject info = validRequestTypes.getJSONObject(i);
                requestTypes.add(RequestType.fromJSONObject(info));
            }
        } catch(Exception e){
            Log.e(TAG,"Unable to parse request types");
        }
        return requestTypes;
    }

}