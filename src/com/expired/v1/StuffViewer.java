package com.expired.v1;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.expired.db.DataHelper;
import com.expired.db.DataPool;
import com.expired.fbconnect.BaseDialogListener;
import com.expired.fbconnect.BaseRequestListener;
import com.expired.fbconnect.LoginButton;
import com.expired.fbconnect.SessionEvents;
import com.expired.fbconnect.SessionEvents.AuthListener;
import com.expired.fbconnect.SessionEvents.LogoutListener;
import com.expired.fbconnect.SessionStore;
import com.facebook.android.FacebookError;
import com.facebook.android.Util;

public class StuffViewer extends Activity {
  private ScrollView scrollView;
  @SuppressWarnings("unused")
  private TextView title;
  private TextView name_display;
  // private TextView desc_display;
  private ImageView stuff_img;
  private TextView date_display;
  private TextView place_display;
  private Button OkBtn;
  private LoginButton mLoginButton;

  private DataHelper dh;
  @SuppressWarnings("unused")
  private DataPool dp;
  private static final String TAG = "StuffViewer";
  String stuff_id;
  String stuff_name;
  String stuff_desc;
  String stuff_expiredate;
  String stuff_placeAt;
  String stuff_picId;

  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.viewstuff);
    scrollView = (ScrollView) findViewById(R.id.ScrollView);
    title = (TextView) findViewById(R.id.StuffViewer);
    name_display = (TextView) findViewById(R.id.StuffName);
    // desc_display = (TextView) findViewById(R.id.StuffDescribe);
    stuff_img = (ImageView) findViewById(R.id.StuffImg);
    registerForContextMenu(stuff_img);
    mLoginButton = (LoginButton) findViewById(R.id.login);
    OkBtn = (Button) findViewById(R.id.OkButton);
    date_display = (TextView) findViewById(R.id.StuffExpiredDate);
    place_display = (TextView) findViewById(R.id.PlaceSelector);

    this.dh = DataHelper.getInstance(this);
    this.dp = DataPool.getInstance(this);
    Calendar.getInstance();
    Intent viewIntent = getIntent();
    stuff_name = viewIntent.getStringExtra("stuff_name");
    stuff_desc = viewIntent.getStringExtra("stuff_desc");
    stuff_expiredate = viewIntent.getStringExtra("stuff_expiredate");
    stuff_placeAt = viewIntent.getStringExtra("stuff_placeAt");
    stuff_picId = viewIntent.getStringExtra("stuff_picId");

    if (stuff_name != "" && stuff_desc != "") {
      List<byte[]> picData = this.dh.selectFromImage(Integer
          .parseInt(stuff_picId));
      Bitmap img = BitmapFactory.decodeByteArray(picData.get(0), 0,
          picData.get(0).length);
      stuff_img.setImageBitmap(img);
      // }
      name_display.setText(stuff_name);
      // desc_display.setText(stuff_desc);

      String date = (new Date(stuff_expiredate).getYear() + 1900) + "/"
          + (new Date(stuff_expiredate).getMonth() + 1) + "/"
          + new Date(stuff_expiredate).getDate();
      date_display.setText(getString(R.string.StuffExpiredDate, 
          new Date(date)));
      String anwser = dh.selectFromPlaceById(stuff_placeAt);
      place_display.setText("@ " + anwser.split(",")[1]);
    }

    OkBtn.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        finish();
      }
    });

    LinearLayout linearLayout = (LinearLayout) findViewById(R.id.LinearLayout);
    Log.i(TAG,
        "linearLayout.getHeight(): " + String.valueOf(linearLayout.getHeight()));

    Log.i(TAG,
        "scrollView.getScrollY(): " + String.valueOf(scrollView.getScrollY()));

    Log.i(TAG,
        "scrollView.getHeight(): " + String.valueOf(scrollView.getHeight()));

    SessionStore.restore(DataPool.mFacebook, this);
    SessionEvents.addAuthListener(new SampleAuthListener());
    SessionEvents.addLogoutListener(new SampleLogoutListener());
    mLoginButton.init(this, DataPool.mFacebook);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    DataPool.mFacebook.authorizeCallback(requestCode, resultCode, data);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    return super.onOptionsItemSelected(item);
  }

  public class SampleAuthListener implements AuthListener {

    public void onAuthSucceed() {
      // mText.setText("You have logged in! ");
      // mRequestButton.setVisibility(View.VISIBLE);
      // mUploadButton.setVisibility(View.VISIBLE);
      // mPostButton.setVisibility(View.VISIBLE);
      // Toast.makeText(getBaseContext(), "Login Success",
      // Toast.LENGTH_SHORT);
      DataPool.mFacebook.dialog(StuffViewer.this, "feed",
          new SampleDialogListener());
    }

    public void onAuthFail(String error) {
      Toast.makeText(getBaseContext(), "Login Failed: " + error,
          Toast.LENGTH_SHORT);
      // mText.setText();
    }
  }

  public class SampleLogoutListener implements LogoutListener {
    public void onLogoutBegin() {
      // mText.setText("Logging out...");
    }

    public void onLogoutFinish() {
      // mText.setText("You have logged out! ");
      // mRequestButton.setVisibility(View.INVISIBLE);
      // mUploadButton.setVisibility(View.INVISIBLE);
      // mPostButton.setVisibility(View.INVISIBLE);
    }
  }

  public class SampleRequestListener extends BaseRequestListener {

    public void onComplete(final String response) {
      try {
        // process the response here: executed in background thread
        Log.d("Facebook-Connect", "Response: " + response.toString());
        JSONObject json = Util.parseJson(response);
        json.getString("name");

        // then post the processed result back to the UI thread
        // if we do not do this, an runtime exception will be generated
        // e.g. "CalledFromWrongThreadException: Only the original
        // thread that created a view hierarchy can touch its views."
        // Example.this.runOnUiThread(new Runnable() {
        // public void run() {
        // mText.setText("Hello there, " + name + "!");
        // }
        // });
      } catch (JSONException e) {
        Log.w("Facebook-Connect", "JSON Error in response");
      } catch (FacebookError e) {
        Log.w("Facebook-Connect", "Facebook Error: " + e.getMessage());
      }
    }
  }

  public class SampleUploadListener extends BaseRequestListener {

    public void onComplete(final String response) {
      try {
        // process the response here: (executed in background thread)
        Log.d("Facebook-Example", "Response: " + response.toString());
        JSONObject json = Util.parseJson(response);
        json.getString("src");

        // then post the processed result back to the UI thread
        // if we do not do this, an runtime exception will be generated
        // e.g. "CalledFromWrongThreadException: Only the original
        // thread that created a view hierarchy can touch its views."
        // Example.this.runOnUiThread(new Runnable() {
        // public void run() {
        // mText.setText("Hello there, photo has been uploaded at \n"
        // + src);
        // }
        // });
      } catch (JSONException e) {
        Log.w("Facebook-Example", "JSON Error in response");
      } catch (FacebookError e) {
        Log.w("Facebook-Example", "Facebook Error: " + e.getMessage());
      }
    }
  }

  public class WallPostRequestListener extends BaseRequestListener {

    public void onComplete(final String response) {
      Log.d("Facebook-Example", "Got response: " + response);
      @SuppressWarnings("unused")
      String message = "<empty>";
      try {
        JSONObject json = Util.parseJson(response);
        message = json.getString("message");
      } catch (JSONException e) {
        Log.w("Facebook-Example", "JSON Error in response");
      } catch (FacebookError e) {
        Log.w("Facebook-Example", "Facebook Error: " + e.getMessage());
      }
    }
  }

  public class WallPostDeleteListener extends BaseRequestListener {

    public void onComplete(final String response) {
      if (response.equals("true")) {
        Log.d("Facebook-Example", "Successfully deleted wall post");
        // Example.this.runOnUiThread(new Runnable() {
        // public void run() {
        // mDeleteButton.setVisibility(View.INVISIBLE);
        // mText.setText("Deleted Wall Post");
        // }
        // });
      } else {
        Log.d("Facebook-Example", "Could not delete wall post");
      }
    }
  }

  public class SampleDialogListener extends BaseDialogListener {

    public void onComplete(Bundle values) {
      final String postId = values.getString("post_id");
      if (postId != null) {
        Log.d("Facebook-Example", "Dialog Success! post_id=" + postId);
        DataPool.mAsyncRunner.request(postId, new WallPostRequestListener());
        // mDeleteButton.setOnClickListener(new OnClickListener() {
        // public void onClick(View v) {
        // mAsyncRunner.request(postId, new Bundle(), "DELETE",
        // new WallPostDeleteListener());
        // }
        // });
        // mDeleteButton.setVisibility(View.VISIBLE);
      } else {
        Log.d("Facebook-Example", "No wall post made");
      }
    }
  }
}
