package com.expired.v1;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import com.expired.db.DataHelper;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class PlaceEditor extends Activity {
  EditText name_editor;
  EditText desc_editor;
  TextView editTitle;
  ImageView place_img;
  Button OkBtn;
  private DataHelper dh;

  private static final String TAG = "PlaceEditor";
  @SuppressWarnings("unused")
  private String selectedImagePath;
  String place_id;
  String place_name;
  String place_desc;
  private Uri imageUri;
  private static final int SELECT_PICTURE = 1;
  @SuppressWarnings("unused")
  private static final int DATE_DIALOG_ID = 0;
  private int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 2;

  @Override
  protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    setContentView(R.layout.newplace);

    name_editor = (EditText) findViewById(R.id.PlaceNameEdit);
    // desc_editor = (EditText) findViewById(R.id.PlaceDescribeEdit);
    editTitle = (TextView) findViewById(R.id.AddPlaceTxt);
    editTitle.setText(R.string.PlaceEditing);
    place_img = (ImageView) findViewById(R.id.PlaceImg);
    place_img.setDrawingCacheEnabled(true);
    registerForContextMenu(place_img);

    OkBtn = (Button) findViewById(R.id.OkButton);
    this.dh = DataHelper.getInstance(this);

    Intent editIntent = getIntent();
    place_id = editIntent.getStringExtra("place_id");
    place_name = editIntent.getStringExtra("place_name");
    // place_desc = editIntent.getStringExtra("place_desc");

    if (place_name != "" && place_desc != "") {
      List<String> list = this.dh.selectFromPlace("name", place_name);
      if (list != null) {
        String[] placeRec = list.get(0).split(",");
        String imgId = placeRec[placeRec.length - 1];
        List<byte[]> picData = this.dh.selectFromImage(Integer.parseInt(imgId));
        Bitmap img = BitmapFactory.decodeByteArray(picData.get(0), 0,
            picData.get(0).length);
        place_img.setImageBitmap(img);
      }

      name_editor.setText(place_name);
      // desc_editor.setText(place_desc);
    }

    place_img.setOnLongClickListener(new OnLongClickListener() {
      public boolean onLongClick(View v) {
        // place_img.setBackgroundResource(R.color.orange);
        return false;
      }
    });

    OkBtn.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        if (name_editor.getText().toString() != ""
            && name_editor.getText().toString() != place_name) {
          // Update name value...
          Log.i(TAG, "Updateing name value...");
          int affectedRow = dh.update_place(place_id, new String[] { "name" },
              new String[] { name_editor.getText().toString() });
          Log.i(TAG, "Number of row affected: " + affectedRow);
        }
        // if (desc_editor.getText().toString() != ""
        // && desc_editor.getText().toString() != place_desc) {
        // // Update desc value...
        // Log.i(TAG, "Updateing desc value...");
        // int affectedRow = dh.update_place(place_id, new String[] { "desc" },
        // new String[] { desc_editor.getText().toString() });
        // Log.i(TAG, "Number of row affected: " + affectedRow);
        // }
        place_img.invalidate();
        List<String> list = dh.selectFromPlace("name", name_editor.getText()
            .toString());
        String[] placeRec = list.get(0).split(",");
        String imgId = placeRec[placeRec.length - 1];
        dh.update_image(imgId, place_img.getDrawingCache(), null);
        finish();
      }
    });
  }

  @Override
  public void onCreateContextMenu(ContextMenu menu, View v,
      ContextMenuInfo menuInfo) {
    if (v.getId() == R.id.PlaceImg) {
      // AdapterView.AdapterContextMenuInfo info =
      // (AdapterView.AdapterContextMenuInfo) menuInfo;
      menu.setHeaderTitle(R.string.SelectImgSource);
      String[] menuItems = getResources().getStringArray(R.array.ImgSrc);

      for (int i = 0; i < menuItems.length; i++) {
        menu.add(Menu.NONE, i, i, menuItems[i]);
      }
    }
  }

  @Override
  public boolean onContextItemSelected(MenuItem item) {
    // AdapterView.AdapterContextMenuInfo info =
    // (AdapterView.AdapterContextMenuInfo) item
    // .getMenuInfo();

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
      // new AlertDialog.Builder(PlaceEditor.this).setTitle("Warn")
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
          place_img.setImageBitmap(bitmap);

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
        place_img.setImageBitmap(yourSelectedImage);
        place_img.invalidate();
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
    inflater.inflate(R.menu.menu_editplace, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
    case R.id.placeImg_rotate_left_90:
      place_img.invalidate();
      Bitmap bMap1 = place_img.getDrawingCache();
      Matrix mat1 = new Matrix();
      mat1.postRotate(270);
      Bitmap bMapRotate1 = Bitmap.createBitmap(bMap1, 0, 0, bMap1.getWidth(),
          bMap1.getHeight(), mat1, true);
      place_img.setImageBitmap(bMapRotate1);
      // place_img.invalidate();
      break;
    case R.id.placeImg_rotate_right_90:
      place_img.invalidate();
      Bitmap bMap2 = place_img.getDrawingCache();
      Matrix mat2 = new Matrix();
      mat2.postRotate(90);
      Bitmap bMapRotate2 = Bitmap.createBitmap(bMap2, 0, 0, bMap2.getWidth(),
          bMap2.getHeight(), mat2, true);
      place_img.setImageBitmap(bMapRotate2);
      // place_img.invalidate();
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
