package com.expired.db;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.Facebook;

/**
 * @author w200
 */
public class DataPool {
  //
  public static String locale = "";

  // Facebook config
  public static final String APP_ID = "190635290953874";
  public static Facebook mFacebook;
  public static AsyncFacebookRunner mAsyncRunner;
  private static DataPool instance;
  private DataHelper dh;

  public List<String> placeList;

  public List<String> placeIdArr;

  public List<String> placeNameArr;

  // public List<String> placeDescArr;

  public List<String> placePositionArr;

  public List<String> placePicIdArr;

  public List<String> stuffList;

  public List<String> stuffIdArr;

  public List<String> stuffNameArr;

  // public List<String> stuffDescArr;

  public List<String> stuffExpiredateArr;

  public List<String> stuffPlaceIdArr;

  public List<String> stuffPicIdArr;
  @SuppressWarnings("unused")
  private static Context context;

  private static final String TABLE_PLACE = "place";
  @SuppressWarnings("unused")
  private static final String TABLE_STUFF = "stuff";
  @SuppressWarnings("unused")
  private static final String TABLE_IMAGE = "images";

  public SharedPreferences prefs;

  private DataPool(Context context) {
    DataPool.context = context;
    // OpenHelper openHelper = new OpenHelper(this.context);
    this.dh = DataHelper.getInstance(context);
    placeList = new ArrayList<String>();
    placeIdArr = new ArrayList<String>();
    placeNameArr = new ArrayList<String>();
    // placeDescArr = new ArrayList<String>();
    placePositionArr = new ArrayList<String>();
    placePicIdArr = new ArrayList<String>();

    stuffList = new ArrayList<String>();
    stuffIdArr = new ArrayList<String>();
    stuffNameArr = new ArrayList<String>();
    // stuffDescArr = new ArrayList<String>();
    stuffExpiredateArr = new ArrayList<String>();
    stuffPlaceIdArr = new ArrayList<String>();
    stuffPicIdArr = new ArrayList<String>();

    mFacebook = new Facebook(APP_ID);
    mAsyncRunner = new AsyncFacebookRunner(mFacebook);

    prefs = context.getSharedPreferences("prefs", 0);

  }

  public static DataPool getInstance(Context context) {
    if (instance == null)
      instance = new DataPool(context);
    return instance;
  }

  public void RefreshPlaceContent(String order) {
    placeList = this.dh.selectAll(TABLE_PLACE, order);
    // this.placeList = placeList;
    if (placeList.size() == 0) {
      // Log.i(TAG, "table place is empty!");
      // for (int i = 0; i < input_placeArr.length; i++) {
      // this.dh.insert_place(input_placeArr[i], input_descArr[i], none,
      // null);
      // }
    } else {

      for (String place : placeList) {
        String[] placeInfo = place.split(",");
        this.placeIdArr.add(placeInfo[0]);
        this.placeNameArr.add(placeInfo[1]);
        // this.placeDescArr.add(placeInfo[2]);
        this.placePositionArr.add(placeInfo[2]);
        this.placePicIdArr.add(placeInfo[3]);
      }
    }
  }

  public void ResetPlaceContent() {
    placeList.removeAll(placeList);
    placeIdArr.removeAll(placeIdArr);
    placeNameArr.removeAll(placeNameArr);
    // placeDescArr.removeAll(placeDescArr);
    placePositionArr.removeAll(placePositionArr);
    placePicIdArr.removeAll(placePicIdArr);
  }

  public void RefreshStuffContent(String order) {
    stuffList = this.dh.selectAll("stuff", order);
    // this.stuffList = stuffList;
    if (stuffList.size() == 0) {
      // Log.i(TAG, "table stuff is empty!");
      // for (int i = 0; i < input_placeArr.length; i++) {
      // this.dh.insert_place(input_placeArr[i], input_descArr[i], none,
      // null);
      // }
    } else {
      for (String stuff : stuffList) {
        String[] stuffInfo = stuff.split(",");
        this.stuffIdArr.add(stuffInfo[0]);
        this.stuffNameArr.add(stuffInfo[1]);
        // this.stuffDescArr.add(stuffInfo[2]);
        this.stuffExpiredateArr.add(stuffInfo[2]);
        this.stuffPlaceIdArr.add(stuffInfo[3]);
        this.stuffPicIdArr.add(stuffInfo[4]);
      }

    }

  }

  public void ResetStuffContent() {
    this.stuffList.removeAll(stuffList);
    this.stuffIdArr.removeAll(stuffIdArr);
    this.stuffNameArr.removeAll(stuffNameArr);
    // this.stuffDescArr.removeAll(stuffDescArr);
    this.stuffExpiredateArr.removeAll(stuffExpiredateArr);
    this.stuffPlaceIdArr.removeAll(stuffPlaceIdArr);
    this.stuffPicIdArr.removeAll(stuffPicIdArr);
  }

}
