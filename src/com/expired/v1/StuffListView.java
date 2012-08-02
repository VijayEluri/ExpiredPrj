package com.expired.v1;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.expired.adapter.StuffAdapter;
import com.expired.db.DataHelper;
import com.expired.db.DataPool;

public class StuffListView extends Activity {

  ListView stuffListView;
  TextView footer;
  TextView Title;

  private DataHelper dh;
  private DataPool dp;

  String NONE = "none";
  public List<String> stuffList;
  public List<String> idArr;
  public List<String> nameArr;
  public List<String> descArr;
  public List<String> expiredateArr;
  public List<String> placeIdArr;
  public List<String> picIdArr;
  private static final String TAG = "PlaceListView";
  private AdapterView.AdapterContextMenuInfo info;
  @SuppressWarnings("unused")
  private static final String TABLE_PLACE = "place";
  private static final String TABLE_STUFF = "stuff";
  private static final String TABLE_IMAGE = "images";
  private SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
  private List<Bitmap> bitmapList;
  private ArrayList<String> intervalList = null;
  private Calendar someCal = new GregorianCalendar();

  SharedPreferences prefs;
  public String str_stuff_date_order;
  private Editor ed;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.stuff_main);

    prefs = getSharedPreferences("prefs", 0);
    ed = prefs.edit();
    str_stuff_date_order = prefs.getString("stuff_order", "desc");

    stuffListView = (ListView) findViewById(R.id.StuffListView);
    footer = (TextView) findViewById(R.id.stuff_footer);
    Title = (TextView) findViewById(R.id.MyStuffTitle);

    this.dh = DataHelper.getInstance(this);
    this.dp = DataPool.getInstance(this);
    RefreshListContent(str_stuff_date_order);

    registerForContextMenu(stuffListView);

    // Toast.makeText(this,
    // getResources().getConfiguration().locale.getDisplayName(),
    // Toast.LENGTH_SHORT).show();

    stuffListView.setOnItemClickListener(new OnItemClickListener() {
      public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
          long arg3) {
        // Toast.makeText(getBaseContext(), "item " + arg2 + " clicked",
        // Toast.LENGTH_LONG).show();
        // arg1.setBackgroundColor(R.color.orange);
        Intent go2StuffViewer = new Intent(getBaseContext(), StuffViewer.class);
        go2StuffViewer.putExtra("stuff_id", dp.stuffIdArr.get(arg2));
        go2StuffViewer.putExtra("stuff_name", dp.stuffNameArr.get(arg2));
        // go2StuffViewer.putExtra("stuff_desc", dp.stuffDescArr.get(arg2));
        go2StuffViewer.putExtra("stuff_expiredate",
            dp.stuffExpiredateArr.get(arg2));
        go2StuffViewer.putExtra("stuff_placeAt", dp.stuffPlaceIdArr.get(arg2));
        go2StuffViewer.putExtra("stuff_picId", dp.stuffPicIdArr.get(arg2));
        startActivity(go2StuffViewer);
      }
    });

    stuffListView.setOnFocusChangeListener(new OnFocusChangeListener() {
      public void onFocusChange(View v, boolean hasFocus) {
        v.setBackgroundColor(R.color.orange);
      }
    });
  }

  public void RefreshListContent(String order) {

    dp.ResetStuffContent();
    dp.RefreshStuffContent(order);

    List<byte[]> data = null;
    bitmapList = new ArrayList<Bitmap>();
    for (String picId : dp.stuffPicIdArr) {
      data = dh.selectFromImage(Integer.valueOf(picId));
      bitmapList.add(getResizedBitmap(
          BitmapFactory.decodeByteArray(data.get(0), 0, data.get(0).length),
          128, 128));
    }

    intervalList = new ArrayList<String>();
    for (String date : dp.stuffExpiredateArr) {
      intervalList.add(getInterval(date));
    }

    stuffListView.setAdapter(new StuffAdapter(getBaseContext(), bitmapList,
        dp.stuffNameArr/* , dp.stuffDescArr */, intervalList));
    // List<Bitmap> indicatorList = new ArrayList<Bitmap>();
    // Date expiredate = null;
    // for (String date : dp.stuffExpiredateArr) {
    //
    // String composedDate = (new Date(date).getYear() + 1900) + "/"
    // + (new Date(date).getMonth() + 1) + "/"
    // + new Date(date).getDate();
    // try {
    // expiredate = sdf.parse(composedDate);
    // } catch (ParseException e) {
    // e.printStackTrace();
    // }
    // //indicatorList.add(getDaysBeforeExpired(expiredate));
    // }

    // registerForContextMenu(stuffListView);
    dp.ResetPlaceContent();
    dp.RefreshPlaceContent(order);
    int stuffNum = dp.stuffNameArr.size();
    // int placeNum = dp.placeNameArr.size();
    String msg = "";
    if (stuffNum > 0) {
      // Toast.makeText(getBaseContext(),
      // getResources().getString(R.string.LongClickToMoreOptions),
      // Toast.LENGTH_SHORT).show();

      msg = getBaseContext().getResources().getString(R.string.total)
          + ": "
          + stuffNum
          + " "
          + getBaseContext().getResources().getString(
              R.string.home_footerNotice_stuff);

      footer.setText(msg);
    } else {
      footer.setText(getBaseContext().getResources().getString(
          R.string.NoStuffRightNow));
    }

  }

  private String getInterval(String thatDate) {
    String dateStr = (new Date(thatDate).getYear() + 1900) + "/"
        + (new Date(thatDate).getMonth() + 1) + "/"
        + new Date(thatDate).getDate();

    someCal.set(Calendar.HOUR_OF_DAY, 0);
    someCal.set(Calendar.MINUTE, 0);
    someCal.set(Calendar.SECOND, 0);
    someCal.set(Calendar.MILLISECOND, 0);

    Date theDate = null;
    try {
      theDate = sdf.parse(dateStr);
    } catch (ParseException e) {
      e.printStackTrace();
      Log.e(TAG, e.toString());
    }

    Date today = someCal.getTime();
    if (today.compareTo(theDate) == 0)
      return new String("0");
    else {
      long diff = theDate.getTime() - today.getTime();
      long days = diff / (24 * 3600 * 1000);
      int interval = (days > 0 && days < 1) ? 1 : (int) days;
      return String.valueOf(interval);
    }
  }

  // public Bitmap getDaysBeforeExpired(Date expiredate) {
  // Date today = Calendar.getInstance().getTime();
  // int interval = (int) (expiredate.getTime() - today.getTime())
  // / (24 * 3600 * 1000);
  // if (interval < 3 && interval > 0)
  // return BitmapFactory.decodeResource(getResources(),
  // R.drawable.circle_red_32x32);
  // if (interval <= 5 && interval > 0)
  // return BitmapFactory.decodeResource(getResources(),
  // R.drawable.circle_yellow_32x32);
  // if (interval >= 7)
  // return BitmapFactory.decodeResource(getResources(),
  // R.drawable.circle_green_32x32);
  // if (interval < 0)
  // return BitmapFactory.decodeResource(getResources(),
  // R.drawable.circle_grey_32x32);
  // else
  // return BitmapFactory.decodeResource(getResources(),
  // R.drawable.circle_grey_32x32);
  // }

  public static Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {

    int width = bm.getWidth();
    int height = bm.getHeight();
    // Constrain to given size but keep aspect ratio
    float scaleFactor = Math.min(((float) newWidth) / width,
        ((float) newHeight) / height);
    // float scaleWidth = ((float) newWidth) / width;
    // float scaleHeight = ((float) newHeight) / height;
    // create a matrix for the manipulation
    Matrix matrix = new Matrix();
    // resize the bit map
    matrix.postScale(scaleFactor, scaleFactor);
    // recreate the new Bitmap
    Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix,
        false);
    return resizedBitmap;
  }

  @Override
  protected void onResume() {
    RefreshListContent(str_stuff_date_order);
    super.onResume();
  }

  @Override
  protected void onRestart() {
    RefreshListContent(str_stuff_date_order);
    super.onRestart();
  }

  @Override
  protected void onPause() {
    ed.commit();
    super.onPause();
  }

  @Override
  public void onCreateContextMenu(ContextMenu menu, View v,
      ContextMenuInfo menuInfo) {
    if (v.getId() == R.id.StuffListView) {
      info = (AdapterView.AdapterContextMenuInfo) menuInfo;
      menu.setHeaderTitle(dp.stuffNameArr.get(info.position));
      String[] menuItems = getResources().getStringArray(R.array.Actions);

      for (int i = 0; i < menuItems.length; i++) {
        menu.add(Menu.NONE, i, i, menuItems[i]);
      }
    }
  }

  @Override
  public boolean onContextItemSelected(MenuItem item) {
    info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

    int menuItemIndex = item.getItemId();
    switch (menuItemIndex) {

    case 0:// Edit
      Intent go2StuffEditor = new Intent(this, StuffEditor.class);
      List<String> list = this.dh.selectFromStuff("name",
          dp.stuffNameArr.get(info.position));
      if (list != null) {
        go2StuffEditor.putExtra("stuff_id", dp.stuffIdArr.get(info.position));// list.get(0).split(",")[0]);
        go2StuffEditor.putExtra("stuff_name",
            dp.stuffNameArr.get(info.position));
        // go2StuffEditor.putExtra("stuff_desc",
        // dp.stuffDescArr.get(info.position));
        go2StuffEditor.putExtra("stuff_expiredate",
            dp.stuffExpiredateArr.get(info.position));
        go2StuffEditor.putExtra("stuff_placeAt",
            dp.stuffPlaceIdArr.get(info.position));
        go2StuffEditor.putExtra("stuff_picId",
            dp.stuffPicIdArr.get(info.position));
        startActivityForResult(go2StuffEditor, 1);
      }
      break;
    case 1:// Delete
      List<String> list1 = this.dh.selectFromStuff("name",
          dp.stuffNameArr.get(info.position));
      String[] stuffRec = list1.get(0).split(",");
      String idTobeDelete = stuffRec[0];
      String picId = stuffRec[stuffRec.length - 1];
      this.dh.deleteFromTable(TABLE_IMAGE, picId);
      this.dh.deleteFromTable(TABLE_STUFF, idTobeDelete);
      RefreshListContent(str_stuff_date_order);

      // new AlertDialog.Builder(getBaseContext()).setTitle(.set
      break;
    case 2:// Find it
      break;
    }

    // String[] menuItems = getResources().getStringArray(R.array.Actions);
    // String menuItemName = menuItems[menuItemIndex];
    // String listItemName = placeArr.get(info.position);
    //
    // footer.setText(String.format("Selected %s for item %s", menuItemName,
    // listItemName));
    return true;
  }

  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.menu_stuff, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
    case R.id.newstuff:
      Intent go2NewStuff = new Intent(this, NewStuff.class);
      startActivity(go2NewStuff);
      break;

    case R.id.stuff_order:
      /*
       * if (dp.stuffList.size() == 0) { Toast.makeText(getBaseContext(),
       * getString(R.string.NoStuffRightNow), Toast.LENGTH_LONG) .show();
       * Log.i(TAG, "No Stuff now!"); break; } else {
       */
      if (dp.stuffList.size() != 0) {

        str_stuff_date_order = prefs.getString("stuff_order", "desc");

        if (str_stuff_date_order.equalsIgnoreCase("desc")) {
          // Log.i(TAG, "stuff_order : desc=> asc");
          ed.putString("stuff_order", "asc");
          ed.apply();
          item.setTitle(R.string.stuff_order_desc);
          item.setIcon(R.drawable.ic_menu_sort_desc);
          Title.setText(R.string.MyStuff);
          Title.append(" : " + this.getString(R.string.stuff_order_asc));
          RefreshListContent("asc");
          break;
        }

        str_stuff_date_order = prefs.getString("stuff_order", "desc");
        if (str_stuff_date_order.equalsIgnoreCase("asc")) {
          // Log.i(TAG, "stuff_order : asc => desc");
          ed.putString("stuff_order", "desc");
          ed.apply();

          item.setTitle(R.string.stuff_order_asc);
          item.setIcon(R.drawable.ic_menu_sort_asc);
          Title.setText(R.string.MyStuff);
          Title.append(" : " + this.getString(R.string.stuff_order_desc));
          RefreshListContent("desc");
          break;
        }
      }
      break;
    case R.id.home:
      Intent GoHome = new Intent(this, Home.class);
      startActivity(GoHome);
    }

    return super.onOptionsItemSelected(item);
  }
}
