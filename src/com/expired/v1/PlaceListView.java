package com.expired.v1;

import java.util.ArrayList;
import java.util.List;

import com.expired.adapter.PlaceAdapter;
import com.expired.db.DataHelper;
import com.expired.db.DataPool;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author w200
 */
public class PlaceListView extends Activity {

  @SuppressWarnings("unused")
  private static final String TAG = "PlaceListView";
  private DataHelper dh;
  private DataPool dp;
  private AdapterView.AdapterContextMenuInfo info;
  private static final String TABLE_PLACE = "place";
  private static final String TABLE_IMAGE = "images";
  private static final String TABLE_STUFF = "stuff";

  String NONE = "none";
  List<String> placeList;
  List<String> idArr;
  List<String> nameArr;
  List<String> descArr;
  List<String> positionArr;
  List<String> picIdArr;
  ListView placeListView;
  TextView footer;
  TextView Title;

  SharedPreferences prefs;
  public String str_place_name_order;
  private Editor ed;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.place_main);

    prefs = getSharedPreferences("prefs", 0);
    ed = prefs.edit();
    str_place_name_order = prefs.getString("place_order", "asc");

    placeListView = (ListView) findViewById(R.id.PlaceListView);
    footer = (TextView) findViewById(R.id.place_footer);
    Title = (TextView) findViewById(R.id.PlaceMgmtTitle);

    this.dh = DataHelper.getInstance(this);
    this.dp = DataPool.getInstance(this);
    RefreshListContent(str_place_name_order);

    placeListView.setOnItemClickListener(new OnItemClickListener() {
      public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
          long arg3) {
        // Toast.makeText(getBaseContext(), "item " + arg2 + " clicked",
        // Toast.LENGTH_LONG).show();
        // arg1.setBackgroundColor(R.color.orange);
        Intent go2PlaceViewer = new Intent(getBaseContext(), PlaceViewer.class);
        go2PlaceViewer.putExtra("place_name", dp.placeNameArr.get(arg2));
        // go2PlaceViewer.putExtra("place_desc", dp.placeDescArr.get(arg2));
        go2PlaceViewer.putExtra("place_picId", dp.placePicIdArr.get(arg2));
        startActivity(go2PlaceViewer);
      }
    });

    placeListView.setOnFocusChangeListener(new OnFocusChangeListener() {
      public void onFocusChange(View v, boolean hasFocus) {
        v.setBackgroundColor(R.color.orange);
      }
    });

  }

  public void RefreshListContent(String order) {
    dp.ResetPlaceContent();
    dp.RefreshPlaceContent(order);
    dp.ResetStuffContent();
    dp.RefreshStuffContent(order);

    List<byte[]> data = null;
    List<Bitmap> bitmapList = new ArrayList<Bitmap>();
    for (String picId : dp.placePicIdArr) {
      data = dh.selectFromImage(Integer.valueOf(picId));
      bitmapList.add(getResizedBitmap(
          BitmapFactory.decodeByteArray(data.get(0), 0, data.get(0).length),
          56, 56));
    }
    placeListView
        .setAdapter(new PlaceAdapter(this, bitmapList, dp.placeNameArr/*
                                                                       * , dp.
                                                                       * placeDescArr
                                                                       */));
    registerForContextMenu(placeListView);

    //

    // int stuffNum = dp.stuffNameArr.size();
    int placeNum = dp.placeNameArr.size();
    String msg = "";
    if (placeNum > 0) {
      // Toast.makeText(getBaseContext(),
      // getResources().getString(R.string.LongClickToMoreOptions),
      // Toast.LENGTH_SHORT).show();

      msg = getBaseContext().getResources().getString(R.string.total)
          + ": "
          + placeNum
          + " "
          + getBaseContext().getResources().getString(
              R.string.home_footerNotice_place);

      footer.setText(msg);
    } else {
      footer.setText(getBaseContext().getResources().getString(
          R.string.NoPlaceRightNow));
    }
  }

  public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {

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
    RefreshListContent(str_place_name_order);
    super.onResume();
  }

  @Override
  protected void onRestart() {
    super.onRestart();
    RefreshListContent(str_place_name_order);
  }

  @Override
  protected void onPause() {
    ed.commit();
    super.onPause();
  }

  @Override
  public void onCreateContextMenu(ContextMenu menu, View v,
      ContextMenuInfo menuInfo) {
    if (v.getId() == R.id.PlaceListView) {
      info = (AdapterView.AdapterContextMenuInfo) menuInfo;
      menu.setHeaderTitle(dp.placeNameArr.get(info.position));
      String[] menuItems = getResources().getStringArray(R.array.PlaceActions);

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
      Intent go2Editor = new Intent(this, PlaceEditor.class);
      String stuffItem = dp.placeNameArr.get(info.position);
      List<String> list = this.dh.selectFromPlace("name", stuffItem);
      if (list != null && list.size() > 0) {
        go2Editor.putExtra("place_id", list.get(0).split(",")[0]);
        go2Editor.putExtra("place_name", dp.placeNameArr.get(info.position));
        // go2Editor.putExtra("place_desc", dp.placeDescArr.get(info.position));
        startActivityForResult(go2Editor, 1);
      }
      break;
    case 1:// Delete
      new AlertDialog.Builder(PlaceListView.this)
          .setTitle(getResources().getString(R.string.Attention))
          .setIcon(R.drawable.ic_menu_info_details)
          .setMessage(getResources().getString(R.string.DeletePlaceAttention))
          .setPositiveButton(R.string.DelAnyway,
              new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                  List<String> list1 = dh.selectFromPlace("name",
                      dp.placeNameArr.get(info.position));
                  String[] placeRec = list1.get(0).split(",");
                  String idTobeDelete = placeRec[0];
                  String picId = placeRec[placeRec.length - 1];
                  dh.deleteFromTable(TABLE_IMAGE, picId);

                  dh.deleteFromTable(TABLE_PLACE, idTobeDelete);

                  List<String> stuffList = dh
                      .selectFromStuffByPlaceId(dp.placeIdArr
                          .get(info.position));
                  if (stuffList.size() > 0) {
                    for (int i = 0; i < stuffList.size(); i++) {
                      dh.deleteFromTable(TABLE_STUFF,
                          stuffList.get(i).split(",")[0]);
                    }
                  }
                  RefreshListContent(str_place_name_order);
                }
              })
          .setNegativeButton(getResources().getString(R.string.Cancel),
              new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                  dialog.dismiss();
                }
              }).show();

      break;
    case 2:// Find What's in it
      List<String> stuffList = this.dh.selectFromStuffByPlaceId(dp.placeIdArr
          .get(info.position));
      if (stuffList.size() > 0) {
        String[] content = new String[stuffList.size()];
        for (int i = 0; i < stuffList.size(); i++) {
          content[i] = stuffList.get(i).split(",")[1];
        }
        new AlertDialog.Builder(PlaceListView.this)
            .setTitle(getResources().getStringArray(R.array.PlaceActions)[2])
            .setIcon(R.drawable.ic_menu_help)
            .setSingleChoiceItems(content, 0,
                new DialogInterface.OnClickListener() {
                  public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                  }
                }).show();
      } else {
        new AlertDialog.Builder(PlaceListView.this)
            .setTitle(getResources().getString(R.string.Oops))
            .setMessage(getResources().getString(R.string.NothingInIt))
            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
              }
            }).setIcon(R.drawable.ic_menu_help).show();
      }
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
    inflater.inflate(R.menu.menu_place, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
    case R.id.newplace:
      Intent go2NewPlace = new Intent(this, NewPlace.class);
      startActivityForResult(go2NewPlace, 0);
      break;
    case R.id.place_order:
      /*
       * if (dp.placeList.size() == 0) { Toast.makeText(getBaseContext(),
       * getString(R.string.NoPlaceRightNow), Toast.LENGTH_LONG) .show();
       * Log.i(TAG, "No Place now!"); break; } else {
       */
      if (dp.placeList.size() != 0) {
        // place_order default is asc
        str_place_name_order = prefs.getString("place_order", "asc");
        if (str_place_name_order.equalsIgnoreCase("asc")) {
          // Log.i(TAG, "place_order : asc=> desc");
          ed.putString("place_order", "desc");
          ed.apply();
          // after changed to desc, menu sort title set to asc
          item.setTitle(R.string.place_order_asc);
          item.setIcon(R.drawable.ic_menu_sort_asc);
          Title.setText(R.string.PlaceManagement);
          Title.append(" : " + this.getString(R.string.place_order_desc));
          RefreshListContent("desc");
          break;
        }

        str_place_name_order = prefs.getString("place_order", "asc");
        if (str_place_name_order.equalsIgnoreCase("desc")) {
          // Log.i(TAG, "place_order : desc=> asc");
          ed.putString("place_order", "asc");
          ed.apply();
          // after changed to asc, menu sort title set to desc
          item.setTitle(R.string.place_order_desc);
          item.setIcon(R.drawable.ic_menu_sort_desc);
          Title.setText(R.string.PlaceManagement);
          Title.append(" : " + this.getString(R.string.place_order_asc));
          RefreshListContent("asc");
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

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    switch (resultCode) {
    case RESULT_OK:
      if (data.getExtras().getString("RESULT") == "RESULT_NEW_PLACE_OK") {
        Toast notify = Toast.makeText(getBaseContext(),
            nameArr.get(info.position) + " deleted!", Toast.LENGTH_LONG);
        notify.setGravity(Gravity.CENTER, 0, 75);
        notify.show();
      }
      break;
    }
    super.onActivityResult(requestCode, resultCode, data);
  }
}