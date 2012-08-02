package com.expired.v1;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.expired.commons.DateUtil;
import com.expired.db.DataHelper;
import com.expired.db.DataPool;

/**
 * @author w200
 */
public class NewStuff extends Activity {

  private static final String TAG = "AddStuff";
  EditText name_editor;
  EditText desc_editor;
  TextView addPlaceTxt;
  ImageView stuff_img;
  Button dateSelectBtn;
  Button placeSelectBtn;
  Button OkBtn;
  int RESULT_NEW_STUFF_OK = 1;
  int RESULT_NEW_STUFF_FAIL = 0;
  @SuppressWarnings("unused")
  private AdapterView.AdapterContextMenuInfo info;
  private DataHelper dh;
  private DataPool dp;
  // date and time
  private int mYear;
  private int mMonth;
  private int mDay;

  private int idx;
  private String dateSelected;
  private String placeNameSelected;
  private static final int DATE_DIALOG_ID = 0;
  private static final int PICK_IMAGE_REQUEST_CODE = 1;
  private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 2;
  // private ArrayList<String> placeToBeSelect = new ArrayList<String>();
  // private Uri imageUri;
  private Intent go2Camera;

  @Override
  protected void onStart() {
    // Toast.makeText(getBaseContext(),
    // getResources().getString(R.string.LongClickStuffImg),
    // Toast.LENGTH_SHORT).show();
    super.onStart();
  }

  @Override
  protected void onResume() {
    super.onResume();
    // stuff_img.setImageResource(R.drawable.stuff);
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    // outState.putParcelable("imageUri", imageUri);
    super.onSaveInstanceState(outState);
  }

  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.newstuff);
    this.dh = DataHelper.getInstance(this);
    this.dp = DataPool.getInstance(this);

    name_editor = (EditText) findViewById(R.id.StuffNameEdit);
    // desc_editor = (EditText) findViewById(R.id.StuffDescribeEdit);
    stuff_img = (ImageView) findViewById(R.id.StuffImg);
    stuff_img.setDrawingCacheEnabled(true);
    registerForContextMenu(stuff_img);

    OkBtn = (Button) findViewById(R.id.OkButton);
    dateSelectBtn = (Button) findViewById(R.id.StuffExpiredDate);
    dateSelectBtn.setText(getString(R.string.StuffExpiredDate,
        Calendar.getInstance()));
    placeSelectBtn = (Button) findViewById(R.id.PlaceSelector);

    name_editor.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        if (name_editor
            .getText()
            .toString()
            .equalsIgnoreCase(
                getResources().getString(R.string.StuffNameInput).toString()))
          name_editor.setText("");
      }
    });

    // desc_editor.setOnClickListener(new OnClickListener() {
    // public void onClick(View v) {
    // if (desc_editor
    // .getText()
    // .toString()
    // .equalsIgnoreCase(
    // getResources().getString(R.string.StuffDescInput).toString()))
    // desc_editor.setText("");
    // }
    // });

    stuff_img.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        Toast.makeText(getBaseContext(),
            getResources().getString(R.string.LongClickStuffImg),
            Toast.LENGTH_LONG).show();
      }
    });

    stuff_img.setOnLongClickListener(new OnLongClickListener() {
      public boolean onLongClick(View v) {
        // stuff_img.setBackgroundResource(R.color.orange);
        return false;
      }
    });

    dateSelectBtn.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        showDialog(DATE_DIALOG_ID);

      }
    });

    placeSelectBtn.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {

        String stuff_order = getSharedPreferences("prefs", 0).getString(
            "stuff_order", "desc");

        dp.ResetPlaceContent();
        dp.RefreshPlaceContent(stuff_order);
        if (dp.placeNameArr.size() == 0) {
          new AlertDialog.Builder(NewStuff.this)
              .setTitle(getResources().getString(R.string.Oops))
              .setIcon(R.drawable.ic_menu_help)
              .setMessage(
                  getResources().getString(R.string.AddPlaceBeforeAddStuff))
              .setPositiveButton(
                  getResources().getString(R.string.PlaceAddTitle),
                  new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                      Intent go2AddPlace = new Intent(getBaseContext(),
                          NewPlace.class);
                      startActivity(go2AddPlace);
                    }
                  })
              .setNegativeButton(getResources().getString(R.string.Cancel),
                  new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                      dialog.dismiss();
                    }
                  }).show();
        } else {

          String[] content = new String[dp.placeNameArr.size()];
          new AlertDialog.Builder(NewStuff.this)
              .setTitle(R.string.SelectPlace)
              .setIcon(R.drawable.ic_menu_add)
              .setSingleChoiceItems(dp.placeNameArr.toArray(content), 0,
                  new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                      placeNameSelected = dp.placeNameArr.get(which);
                      idx = which;

                      dialog.dismiss();
                      placeSelectBtn.setText(getResources().getString(
                          R.string.StoredAt)
                          + placeNameSelected + " >");
                    }
                  }).show();

        }
      }
    });

    OkBtn.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        if (name_editor.getText().toString() == ""
            /* || desc_editor.getText().toString() == "" */
            || dateSelectBtn.getText().toString() == getBaseContext()
                .getString(R.string.StuffExpiredDate)
            || placeSelectBtn.getText().toString() == getBaseContext()
                .getString(R.string.StuffAt)
            || stuff_img.getDrawingCache().equals(
                getBaseContext().getResources().getDrawable(R.drawable.place))) {
          new AlertDialog.Builder(NewStuff.this).setTitle("Warn")
              .setIcon(R.drawable.warning_48x48)
              .setMessage(R.string.add_place_or_stuff_warn)
              .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                  dialog.dismiss();
                }
              }).show();
        } else if (dh.selectFromStuff("name", name_editor.getText().toString())
            .size() > 0) {
          new AlertDialog.Builder(NewStuff.this).setTitle("Warn")
              .setIcon(R.drawable.warning_48x48)
              .setMessage(R.string.stuff_name_duplicate)
              .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                  name_editor.setSelected(true);
                  dialog.dismiss();
                }
              }).show();
        } else {

          stuff_img.invalidate();
          Log.i(TAG, "Inserting new stuff...");
          if (dateSelected != null) {
            dateSelected = dateSelected.replaceAll("-", "/");
            int rowIdInserted = (int) dh.insert_stuff(name_editor.getText()
                .toString(), /* desc_editor.getText().toString(), */new Date(
                dateSelected), dp.placeIdArr.get(idx), stuff_img
                .getDrawingCache(true));
            Log.i(TAG, "Number of row affected: " + rowIdInserted);
          } else {
            // String today = DateUtil.toDateString(new Date().getYear(),
            // new Date().getMonth(), new Date().getDate());
            int rowIdInserted = (int) dh.insert_stuff(name_editor.getText()
                .toString(), Calendar.getInstance().getTime(), dp.placeIdArr
                .get(idx), stuff_img.getDrawingCache(true));
            Log.i(TAG, "Number of row affected: " + rowIdInserted);
          }

          Intent result = new Intent();
          result.putExtra("RESULT", "RESULT_NEW_PLACE_OK");
          setResult(RESULT_NEW_STUFF_OK, result);
          finish();
        }
      }
    });
  }

  @Override
  protected Dialog onCreateDialog(int id) {
    switch (id) {
    case DATE_DIALOG_ID:
      Calendar c = Calendar.getInstance();
      mYear = c.get(Calendar.YEAR);
      mMonth = c.get(Calendar.MONTH);
      mDay = c.get(Calendar.DATE);
      return new DatePickerDialog(NewStuff.this,
          new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                int dayOfMonth) {
              dateSelected = DateUtil.toDateString(year, monthOfYear + 1,
                  dayOfMonth);

              dateSelectBtn.setText(getBaseContext().getResources().getString(
                  R.string.expirydate)
                  + dateSelected + " >");
            }

          }, mYear, mMonth, mDay);
    }
    return null;
  }

  @Override
  public void onCreateContextMenu(ContextMenu menu, View v,
      ContextMenuInfo menuInfo) {
    if (v.getId() == R.id.StuffImg) {
      info = (AdapterView.AdapterContextMenuInfo) menuInfo;
      menu.setHeaderTitle(R.string.SelectImgSource);
      String[] menuItems = getResources().getStringArray(R.array.ImgSrc);
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

    case 0:// From file
      // To open up a gallery browser
      Intent getFileIntent = new Intent();
      getFileIntent.setType("image/*");
      getFileIntent.setAction(Intent.ACTION_GET_CONTENT);
      startActivityForResult(
          Intent.createChooser(getFileIntent, "Select Picture"),
          PICK_IMAGE_REQUEST_CODE);

      break;
    case 1:// From Camera
      go2Camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
      go2Camera.putExtra(MediaStore.EXTRA_OUTPUT,
          MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString());
      startActivityForResult(go2Camera, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);

      /*
       * Intent go2Camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
       * go2Camera.setAction(android.content.Intent.ACTION_CAMERA_BUTTON);
       * startActivity(go2Camera); define the file-name to save photo taken by
       * Camera activity if (checkFsWritable()) {
       * 
       * String fileName = String.valueOf(System.currentTimeMillis()) + ".jpg";
       * // create parameters for Intent with filename ContentValues values =
       * new ContentValues(); values.put(MediaStore.Images.Media.TITLE,
       * fileName); values.put(MediaStore.Images.Media.DESCRIPTION,
       * "Image capture by camera"); // imageUri is the current activity
       * attribute, define and save // it // for later usage (also in
       * onSaveInstanceState) imageUri = getContentResolver().insert(
       * MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values); // create new
       * Intent
       * 
       * Intent go2Camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); // if
       * (hasImageCaptureBug()) { // go2Camera.putExtra(MediaStore.EXTRA_OUTPUT,
       * Uri // .fromFile(new File("/sdcard/DCIM"))); // } else { //
       * go2Camera.putExtra(MediaStore.EXTRA_OUTPUT, imageUri); //
       * go2Camera.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1); // }
       * startActivityForResult(go2Camera, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
       * } else { new AlertDialog.Builder(NewStuff.this).setTitle("Warn")
       * .setIcon(R.drawable.warning_48x48).setMessage(
       * R.string.SDCardWarning).setPositiveButton("Ok", new
       * DialogInterface.OnClickListener() { public void onClick(DialogInterface
       * dialog, int which) { dialog.dismiss(); } }).show(); }
       */
      break;
    }

    return true;
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

    super.onActivityResult(requestCode, resultCode, intent);

    if (resultCode == RESULT_CANCELED) {
      showToast(this, "Activity cancelled");
      return;
    }
    switch (requestCode) {
    case CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE:
      Bitmap bm1 = (Bitmap) intent.getExtras().get("data");
      stuff_img.setImageBitmap(bm1); // Display image in the View
      stuff_img.invalidate();
      /*
       * Bundle b = intent.getExtras(); if (b != null &&
       * b.containsKey(MediaStore.EXTRA_OUTPUT)) { // large // image?
       * showToast(this, "Large image"); // Shouldn't have to do this ... but
       * MediaStore.Images.Media.insertImage(getContentResolver(), bm, null,
       * null);
       * 
       * 
       * } else { showToast(this, "Small image");
       * MediaStore.Images.Media.insertImage(getContentResolver(), bm, null,
       * null); }
       */
      break;
    case PICK_IMAGE_REQUEST_CODE:
      if (resultCode == RESULT_OK) {

        // Bitmap bm2 = (Bitmap) intent.getExtras().get("data");
        // stuff_img.setImageBitmap(bm2); // Display image in the View
        // stuff_img.invalidate();

        Uri selectedImage = intent.getData();
        String[] filePathColumn = { MediaStore.Images.Media.DATA };

        Cursor cursor = getContentResolver().query(selectedImage,
            filePathColumn, null, null, null);
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String filePath = cursor.getString(columnIndex);
        cursor.close();

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 3;
        Bitmap yourSelectedImage = BitmapFactory.decodeFile(filePath, options);

        if (yourSelectedImage != null) {
          stuff_img.setImageBitmap(yourSelectedImage);
          stuff_img.invalidate();
        }
      }
      break;
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.menu_editstuff, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
    case R.id.stuffImg_rotate_left_90:
      stuff_img.invalidate();
      Bitmap bMap1 = stuff_img.getDrawingCache();
      Matrix mat1 = new Matrix();
      mat1.postRotate(270);
      Bitmap bMapRotate1 = Bitmap.createBitmap(bMap1, 0, 0, bMap1.getWidth(),
          bMap1.getHeight(), mat1, true);
      stuff_img.setImageBitmap(bMapRotate1);
      // place_img.invalidate();
      break;
    case R.id.stuffImg_rotate_right_90:
      stuff_img.invalidate();
      Bitmap bMap2 = stuff_img.getDrawingCache();
      Matrix mat2 = new Matrix();
      mat2.postRotate(90);
      Bitmap bMapRotate2 = Bitmap.createBitmap(bMap2, 0, 0, bMap2.getWidth(),
          bMap2.getHeight(), mat2, true);
      stuff_img.setImageBitmap(bMapRotate2);
      // place_img.invalidate();
      break;
    case R.id.newplacefromstuffsection:
      Intent go2NewPlace = new Intent(this, NewPlace.class);
      startActivityForResult(go2NewPlace, 0);
      break;
    }
    return super.onOptionsItemSelected(item);
  }

  public boolean hasImageCaptureBug() {

    // list of known devices that have the bug
    ArrayList<String> devices = new ArrayList<String>();
    devices.add("android-devphone1/dream_devphone/dream");
    devices.add("generic/sdk/generic");
    devices.add("vodafone/vfpioneer/sapphire");
    devices.add("tmobile/kila/dream");
    devices.add("verizon/voles/sholes");
    devices.add("google_ion/google_ion/sapphire");

    return devices.contains(android.os.Build.BRAND + "/"
        + android.os.Build.PRODUCT + "/" + android.os.Build.DEVICE);

  }

  private boolean checkFsWritable() {
    // Create a temporary file to see whether a volume is really writeable.
    // It's important not to put it in the root directory which may have a
    // limit on the number of files.
    String directoryName = Environment.getExternalStorageDirectory().toString()
        + "/DCIM";// External.getExternalStorageDirectory()
    // 返回存儲卡位置的名稱
    File directory = new File(directoryName);
    if (!directory.isDirectory()) {
      if (!directory.mkdirs()) {
        return false;
      }
    }
    File f = new File(directoryName, ".probe");
    try {
      // Remove stale file if any
      if (f.exists()) {
        f.delete();
      }
      if (!f.createNewFile()) {
        return false;
      }
      f.delete();
      return true;
    } catch (IOException ex) {
      return false;
    }
  }

  public boolean hasStorage(boolean requireWriteAccess) {
    // TODO: After fix the bug, add "if (VERBOSE)" before logging errors.

    String state = Environment.getExternalStorageState();
    Log.v(TAG, "storage state is " + state);

    if (Environment.MEDIA_MOUNTED.equals(state)) {
      if (requireWriteAccess) {
        boolean writable = checkFsWritable();
        Log.v(TAG, "storage writable is " + writable);
        return writable;
      } else {
        return true;
      }
    } else if (!requireWriteAccess
        && Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
      return true;
    }
    return false;
  }

  private void showToast(Context mContext, String text) {
    Toast.makeText(mContext, text, Toast.LENGTH_SHORT).show();
  }
}
