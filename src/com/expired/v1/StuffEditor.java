package com.expired.v1;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import com.expired.db.DataHelper;
import com.expired.db.DataPool;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author w200
 */
public class StuffEditor extends Activity {
  TextView title;
  EditText name_editor;
  EditText desc_editor;
  ImageView stuff_img;
  Button dateSelectBtn;
  Button placeSelectBtn;
  Button OkBtn;

  private DataHelper dh;
  private DataPool dp;
  // private AdapterView.AdapterContextMenuInfo info;
  private static final String TAG = "StuffEditor";
  // private String selectedImagePath;
  private String dateSelected;
  private String placeNameSelected;
  String stuff_id;
  String stuff_name;
  String stuff_desc;
  String stuff_expiredate;
  String stuff_placeAt;
  String stuff_picId;
  // private static final int EDIT_PLACE_RESULT = 0;
  private static final int SELECT_PICTURE = 1;
  private static final int DATE_DIALOG_ID = 0;
  private int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 2;
  private Uri imageUri;
  private int mYear;
  private int mMonth;
  private int mDay;

  SharedPreferences prefs;
  public String str_stuff_date_order;
  private String placeName;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.newstuff);

    prefs = getSharedPreferences("prefs", 0);
    str_stuff_date_order = prefs.getString("stuff_order", "asc");

    this.dh = DataHelper.getInstance(this);
    this.dp = DataPool.getInstance(this);

    title = (TextView) findViewById(R.id.NewStuffTitle);
    title.setText(R.string.StuffEditing);
    name_editor = (EditText) findViewById(R.id.StuffNameEdit);
    // desc_editor = (EditText) findViewById(R.id.StuffDescribeEdit);
    stuff_img = (ImageView) findViewById(R.id.StuffImg);
    registerForContextMenu(stuff_img);
    stuff_img.setDrawingCacheEnabled(true);

    OkBtn = (Button) findViewById(R.id.OkButton);
    dateSelectBtn = (Button) findViewById(R.id.StuffExpiredDate);
    placeSelectBtn = (Button) findViewById(R.id.PlaceSelector);

    this.dh = DataHelper.getInstance(this);

    Intent viewIntent = getIntent();
    stuff_id = viewIntent.getStringExtra("stuff_id");
    stuff_name = viewIntent.getStringExtra("stuff_name");
    stuff_desc = viewIntent.getStringExtra("stuff_desc");
    stuff_expiredate = viewIntent.getStringExtra("stuff_expiredate");
    stuff_placeAt = viewIntent.getStringExtra("stuff_placeAt");
    stuff_picId = viewIntent.getStringExtra("stuff_picId");

    if (stuff_name != "") {

      List<byte[]> picData = dh.selectFromImage(Integer.parseInt(stuff_picId));
      Bitmap img = BitmapFactory.decodeByteArray(picData.get(0), 0,
          picData.get(0).length);
      stuff_img.setImageBitmap(img);
      // }
      name_editor.setText(stuff_name);
      // desc_editor.setText(stuff_desc);
      String date = (new Date(stuff_expiredate).getYear() + 1900) + "/"
          + (new Date(stuff_expiredate).getMonth() + 1) + "/"
          + new Date(stuff_expiredate).getDate();
      // String tmp = getBaseContext().getString(R.string.StuffExpiredDate);
      // String tmp1 = tmp.replace("YYYY/MM/DD", date);
      dateSelectBtn
          .setText(getString(R.string.StuffExpiredDate, new Date(date)));
      if (stuff_placeAt != null) {
        String rec = dh.selectFromPlaceById(stuff_placeAt); 
        placeName = rec.split(",")[1];
        placeSelectBtn.setText(placeName + " >");
      }
    }

    stuff_img.setOnLongClickListener(new OnLongClickListener() {
      public boolean onLongClick(View v) {
        // stuff_img.setBackgroundResource(R.color.orange);
        return false;
      }
    });

    mYear = new Date(stuff_expiredate).getYear() + 1900;
    mMonth = new Date(stuff_expiredate).getMonth() + 1;
    mDay = new Date(stuff_expiredate).getDate();

    dateSelectBtn.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        showDialog(DATE_DIALOG_ID);
        // new DatePickerDialog(StuffEditor.this,
        // new DatePickerDialog.OnDateSetListener() {
        // @Override
        // public void onDateSet(DatePicker view, int year,
        // int monthOfYear, int dayOfMonth) {
        // dateSelected = String.valueOf(year) + "/"
        // + String.valueOf(monthOfYear + 1) + "/"
        // + String.valueOf(dayOfMonth);
        //
        // dateSelectBtn.setText("Expired Data: "
        // + String.valueOf(year) + "/"
        // + String.valueOf(monthOfYear + 1) + "/"
        // + String.valueOf(dayOfMonth));
        // }
        // }, mYear, mMonth, mDay).show();
      }
    });

    placeSelectBtn.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        dp.ResetPlaceContent();
        dp.RefreshPlaceContent(str_stuff_date_order);
        if (dp.placeNameArr.size() == 0) {
          new AlertDialog.Builder(StuffEditor.this)
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
          new AlertDialog.Builder(StuffEditor.this)
              .setTitle(R.string.SelectPlace)
              .setIcon(R.drawable.ic_menu_add)
              .setSingleChoiceItems(dp.placeNameArr.toArray(content), 0,
                  new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                      placeNameSelected = dp.placeNameArr.get(which);
                      // which = which;

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
        if ((name_editor.getText().toString() != "" && name_editor.getText()
            .toString() != stuff_name)
        /*
         * && (desc_editor.getText().toString() != "" && desc_editor.getText()
         * .toString() != stuff_desc)
         */) {
          // Update name value...
          Log.i(TAG, "Updating name value...");
          String[] column_names = new String[5];
          String[] column_values = new String[5];
          column_names[0] = "name";
          column_values[0] = name_editor.getText().toString();
          // column_names[1] = "desc";
          // column_values[1] = desc_editor.getText().toString();
          column_names[1] = "expiredate";
          column_values[1] = dateSelected;

          // Get the current selected place name's placeId
          String newPlace = placeSelectBtn.getText().toString()
              .replace(">", "").trim();
          List<String> placeRec = dh.selectFromPlace("name", newPlace);
          String placeId = "0";
          if (placeRec != null && placeRec.size() > 0)
            placeId = placeRec.get(0).split(",")[0];
          column_names[2] = "placeId";
          column_values[2] = placeId;

          stuff_img.invalidate();
          // update image
          List<String> list = dh.selectFromStuff("name", stuff_name);
          String[] stuffRec = list.get(0).split(",");
          String imgId = stuffRec[stuffRec.length - 1];
          dh.update_image(imgId, stuff_img.getDrawingCache(), null);

          // update name
          dh.update_stuff(stuff_id, new String[] { "name" },
              new String[] { column_values[0] });
          // update desc
          // dh.update_stuff(stuff_id, new String[] { column_names[1] },
          // new String[] { column_values[1] });

          // update expiredate
          dh.update_stuff(stuff_id, new String[] { column_names[1] },
              new String[] { column_values[1] });

          // update placeId
          dh.update_stuff(stuff_id, new String[] { column_names[2] },
              new String[] { column_values[2] });

          finish();
        }

      }
    });
  }

  @Override
  protected Dialog onCreateDialog(int id) {
    switch (id) {
    case DATE_DIALOG_ID:
      mYear = new Date(stuff_expiredate).getYear() + 1900;
      mMonth = new Date(stuff_expiredate).getMonth();
      mDay = new Date(stuff_expiredate).getDate();
      return new DatePickerDialog(StuffEditor.this,
          new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                int dayOfMonth) {
              dateSelected = String.valueOf(year) + "/"
                  + String.valueOf(monthOfYear + 1) + "/"
                  + String.valueOf(dayOfMonth);

              dateSelectBtn.setText(getBaseContext().getResources().getString(
                  R.string.expirydate)
                  + String.valueOf(year)
                  + "/"
                  + String.valueOf(monthOfYear + 1)
                  + "/"
                  + String.valueOf(dayOfMonth) + " >");
            }

          }, mYear, mMonth, mDay);
    }
    return null;
  }

  @Override
  public void onCreateContextMenu(ContextMenu menu, View v,
      ContextMenuInfo menuInfo) {
    if (v.getId() == R.id.StuffImg) {
      // info = (AdapterView.AdapterContextMenuInfo) menuInfo;
      menu.setHeaderTitle(R.string.SelectImgSource);
      String[] menuItems = getResources().getStringArray(R.array.ImgSrc);

      for (int i = 0; i < menuItems.length; i++) {
        menu.add(Menu.NONE, i, i, menuItems[i]);
      }
    }
  }

  @Override
  public boolean onContextItemSelected(MenuItem item) {
    // info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

    int menuItemIndex = item.getItemId();
    switch (menuItemIndex) {

    case 0:// From file
      // To open up a gallery browser
      Intent intent = new Intent();
      intent.setType("image/*");
      intent.setAction(Intent.ACTION_GET_CONTENT);
      startActivityForResult(Intent.createChooser(intent, "Select Picture"),
          SELECT_PICTURE);

      break;
    case 1:// From Camera
      // Intent go2Camera = new
      // Intent(MediaStore.ACTION_IMAGE_CAPTURE);
      // go2Camera.setAction(android.content.Intent.ACTION_CAMERA_BUTTON);
      // startActivity(go2Camera);
      // define the file-name to save photo taken by Camera activity
      // if (hasStorage(true)) {
      String fileName = "new-photo-name.jpg";
      // create parameters for Intent with filename
      ContentValues values = new ContentValues();
      values.put(MediaStore.Images.Media.TITLE, fileName);
      values
          .put(MediaStore.Images.Media.DESCRIPTION, "Image capture by camera");
      // imageUri is the current activity attribute, define and save
      // it
      // for later usage (also in onSaveInstanceState)
      imageUri = getContentResolver().insert(
          MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
      // create new Intent
      Intent go2Camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
      go2Camera.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
      go2Camera.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
      startActivityForResult(go2Camera, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
      // } else {
      // new AlertDialog.Builder(StuffEditor.this).setTitle("Warn")
      // .setIcon(R.drawable.warning_48x48).setMessage(
      // R.string.SDCardWarning).setPositiveButton("Ok",
      // new DialogInterface.OnClickListener() {
      // public void onClick(DialogInterface dialog,
      // int which) {
      // dialog.dismiss();
      // }
      // }).show();
      // }
      break;
    }

    return true;
  }

  // To handle when an image is selected from the browser, add the following
  // to your Activity
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    // stuff_img.setBackgroundResource(R.color.black);
    if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
      if (resultCode == RESULT_OK) {
        // use imageUri here to access the image
        try {
          Bitmap bitmap = Media.getBitmap(getContentResolver(), imageUri);
          stuff_img.setImageBitmap(bitmap);

        } catch (FileNotFoundException e) {
          e.printStackTrace();
        } catch (IOException e) {
          e.printStackTrace();
        }

      } else if (resultCode == RESULT_CANCELED) {
        Toast.makeText(this, "Picture was not taken", Toast.LENGTH_SHORT);
      } else {
        Toast.makeText(this, "Picture was not taken", Toast.LENGTH_SHORT);
      }
    } else if (requestCode == SELECT_PICTURE) {
      if (resultCode == RESULT_OK) {
        Uri selectedImage = data.getData();
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
    }
  }

  // And to convert the image URI to the direct file system path of the image
  // file
  public String getPath(Uri uri) {
    String[] projection = { MediaStore.Images.Media.DATA };
    Cursor cursor = managedQuery(uri, projection, null, null, null);
    int column_index = cursor
        .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
    cursor.moveToFirst();
    return cursor.getString(column_index);
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

  private boolean checkFsWritable() {
    // Create a temporary file to see whether a volume is really writeable.
    // It's important not to put it in the root directory which may have a
    // limit on the number of files.
    String directoryName = Environment.getExternalStorageDirectory().toString()
        + "/DCIM";// External.getExternalStorageDirectory() 返回存儲卡位置的名稱
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
}
