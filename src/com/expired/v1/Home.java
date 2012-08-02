package com.expired.v1;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.expired.commons.CommonSettings;
import com.expired.db.DataHelper;
import com.expired.db.DataPool;

public class Home extends Activity {
  private static final String TAG = "ExpireHOME";
  @SuppressWarnings("unused")
  private DataHelper dh;
  private DataPool dp;
  TextView footerInfo;
  Button myStuffBTN;
  Button newStuffBTN;
  Button placeMgrBTN;
  Button newPlaceBTN;

  SharedPreferences prefs;
  public String str_stuff_date_order;
  public String str_place_name_order;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.home_layout);

    
    
    CommonSettings.xdpi = getResources().getDisplayMetrics().xdpi;
    Log.i(TAG, String.valueOf(CommonSettings.xdpi));
    CommonSettings.ydpi = getResources().getDisplayMetrics().ydpi;
    Log.i(TAG, String.valueOf(CommonSettings.ydpi));

    prefs = getSharedPreferences("prefs", 0);
    str_stuff_date_order = prefs.getString("stuff_order", "desc");
    str_place_name_order = prefs.getString("place_order", "asc");

    Log.i(TAG, android.os.Build.BRAND + "/" + android.os.Build.PRODUCT + "/"
        + android.os.Build.DEVICE);
    myStuffBTN = (Button) findViewById(R.id.Button01);
    // myStuffBTN.setBackgroundDrawable(getResources().getDrawable(R.drawable.icon));
    newStuffBTN = (Button) findViewById(R.id.Button02);
    placeMgrBTN = (Button) findViewById(R.id.Button03);
    newPlaceBTN = (Button) findViewById(R.id.Button04);
    footerInfo = (TextView) findViewById(R.id.home_footer);

    this.dh = DataHelper.getInstance(this);
    this.dp = DataPool.getInstance(this);
    // RefreshListContent();

    myStuffBTN.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        Intent mystuff = new Intent(getBaseContext(), StuffListView.class);
        startActivity(mystuff);
      }
    });

    newStuffBTN.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        Intent newstuff = new Intent(getBaseContext(), NewStuff.class);
        startActivity(newstuff);
      }
    });

    placeMgrBTN.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        Intent locationLv = new Intent(getBaseContext(), PlaceListView.class);
        startActivity(locationLv);
      }
    });

    newPlaceBTN.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        Intent newplace = new Intent(getBaseContext(), NewPlace.class);
        startActivity(newplace);
      }
    });

  }

  public void RefreshListContent() {

    dp.ResetPlaceContent();
    dp.RefreshPlaceContent(str_place_name_order);
    dp.ResetStuffContent();
    dp.RefreshStuffContent(str_stuff_date_order);

    int stuffNum = dp.stuffNameArr.size();
    int placeNum = dp.placeNameArr.size();
    String msg = "";
    if (stuffNum > 0) {
      msg = getBaseContext().getResources().getString(R.string.total)
          + ": "
          + stuffNum
          + " "
          + getBaseContext().getResources().getString(
              R.string.home_footerNotice_stuff)
          + " "
          + getBaseContext().getResources().getString(
              R.string.home_footerNotice_storedIn)
          + " "
          + placeNum
          + " "
          + getBaseContext().getResources().getString(
              R.string.home_footerNotice_place);

      footerInfo.setText(msg);
    } else {
      footerInfo.setText(getBaseContext().getResources().getString(
          R.string.NoStuffRightNow));
    }
  }

  @Override
  protected void onResume() {
    Log.i(TAG, this.getLocalClassName() + " onResume()");
    RefreshListContent();
    super.onResume();
  }

  @Override
  protected void onRestart() {
    Log.i(TAG, this.getLocalClassName() + " onRestart()");
    // RefreshListContent();
    super.onRestart();
  }

  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.menu_home, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
    case R.id.app_about:

      HelperDialog.create(this).show();
      // new
      // AlertDialog.Builder(Home.this).setIcon(R.drawable.ic_menu_help)
      // .setTitle("About").setMessage(R.string.about_content)
      // .setPositiveButton("Ok",
      // new DialogInterface.OnClickListener() {
      // @Override
      // public void onClick(DialogInterface dialog,
      // int which) {
      // dialog.dismiss();
      // }
      // }).show();
      break;

    }

    return super.onOptionsItemSelected(item);
  }

  public static class HelperDialog {
    public static AlertDialog create(Context context) {
      final TextView message = new TextView(context);
      // i.e.: R.string.dialog_message =>
      // "Test this dialog following the link to dtmilano.blogspot.com"
      final SpannableString s = new SpannableString(
          context.getText(R.string.about_content));
      Linkify.addLinks(s, Linkify.EMAIL_ADDRESSES);
      message.setTextSize(TypedValue.COMPLEX_UNIT_DIP,
          CommonSettings.font_size_help_content);
      message.setText(s);
      message.setMovementMethod(LinkMovementMethod.getInstance());

      return new AlertDialog.Builder(context)
          .setTitle(context.getText(R.string.About)).setCancelable(true)
          .setIcon(android.R.drawable.ic_dialog_info)
          .setPositiveButton(context.getText(R.string.ok), null)
          .setView(message).create();
    }
  }
}
