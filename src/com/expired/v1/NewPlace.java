package com.expired.v1;

import java.io.File;
import java.io.IOException;

import com.expired.db.DataHelper;
import com.expired.db.DataPool;

import android.app.Activity;
import android.app.AlertDialog;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class NewPlace extends Activity {
  EditText name_editor;
  EditText desc_editor;
  TextView addPlaceTxt;
  ImageView place_img;
  Button OkBtn;
  private DataHelper dh;
  @SuppressWarnings("unused")
  private DataPool dp;
  // private static final int SELECT_PICTURE = 1;
  private static final String TAG = "AddPlace";
  // private String selectedImagePath;
  String place_id;
  String place_name;
  String place_desc;

  // private static final int RESULT_NEW_PLACE_FAIL = 0;
  private static final int RESULT_NEW_PLACE_OK = 1;
  private static final int PICK_IMAGE_REQUEST_CODE = 2;
  private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 3;
  @SuppressWarnings("unused")
  private Uri imageUri;
  private Intent go2Camera;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.newplace);

    this.dh = DataHelper.getInstance(this);
    this.dp = DataPool.getInstance(this);

    name_editor = (EditText) findViewById(R.id.PlaceNameEdit);
    // desc_editor = (EditText) findViewById(R.id.PlaceDescribeEdit);
    addPlaceTxt = (TextView) findViewById(R.id.AddPlaceTxt);
    place_img = (ImageView) findViewById(R.id.PlaceImg);
    place_img.setDrawingCacheEnabled(true);
    registerForContextMenu(place_img);

    OkBtn = (Button) findViewById(R.id.OkButton);

    place_img.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        Toast.makeText(getBaseContext(),
            getResources().getString(R.string.LongClickPlaceImg),
            Toast.LENGTH_LONG).show();
      }
    });

    place_img.setOnLongClickListener(new OnLongClickListener() {
      public boolean onLongClick(View v) {
        // place_img.setBackgroundResource(R.color.orange);
        return false;
      }
    });

    name_editor.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        if (name_editor
            .getText()
            .toString()
            .equalsIgnoreCase(
                getResources().getString(R.string.PlaceNameInput).toString()))
          name_editor.setText("");
      }
    });

    // desc_editor.setOnClickListener(new OnClickListener() {
    // public void onClick(View v) {
    // if (desc_editor
    // .getText()
    // .toString()
    // .equalsIgnoreCase(
    // getResources().getString(R.string.PlaceDescInput).toString()))
    // desc_editor.setText("");
    // }
    // });

    // InputMethodManager ime = (InputMethodManager)
    // getSystemService(Context.INPUT_METHOD_SERVICE);
    // List<InputMethodInfo> list = ime.getEnabledInputMethodList();
    //
    // name_editor.setOnEditorActionListener(new OnEditorActionListener() {
    // @Override
    // public boolean onEditorAction(TextView v, int actionId,
    // KeyEvent event) {
    // if (name_editor.getText().toString().length() > 8) {
    // name_editor.setText(name_editor.getText().toString()
    // .substring(0, 8));
    // }
    // return false;
    // }
    // });
    //
    // desc_editor.setOnEditorActionListener(new OnEditorActionListener() {
    // @Override
    // public boolean onEditorAction(TextView v, int actionId,
    // KeyEvent event) {
    // if (name_editor.getText().toString().length() > 8) {
    // name_editor.setText(name_editor.getText().toString()
    // .substring(0, 8));
    // }
    // return false;
    // }
    // });

    OkBtn.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {

        if (name_editor
            .getText()
            .toString()
            .equalsIgnoreCase(getResources().getString(R.string.PlaceNameInput))
        /*
         * || desc_editor.getText().toString().equalsIgnoreCase(
         * getResources().getString( R.string.PlaceDescInput))
         */) {
          new AlertDialog.Builder(NewPlace.this).setTitle("Warn")
              .setIcon(R.drawable.warning_48x48)
              .setMessage(R.string.add_place_or_stuff_warn)
              .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                  dialog.dismiss();
                }
              }).show();
        } else if (dh.selectFromPlace("name", name_editor.getText().toString())
            .size() > 0) {
          new AlertDialog.Builder(NewPlace.this).setTitle("Warn")
              .setIcon(R.drawable.warning_48x48)
              .setMessage(R.string.place_name_duplicate)
              .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                  name_editor.setSelected(true);
                  dialog.dismiss();
                }
              }).show();
        } else {
          // Update name value...
          Log.i(TAG, "Inserting new record...");
          int rowIdInserted = (int) dh.insert_place(name_editor.getText()
              .toString() /* desc_editor.getText().toString() */, "none",
              place_img.getDrawingCache());
          Log.i(TAG, "Number of row affected: " + rowIdInserted);

          Intent result = new Intent();
          result.putExtra("RESULT", "RESULT_NEW_PLACE_OK");
          setResult(RESULT_NEW_PLACE_OK, result);
          finish();
        }
      }
    });
  }

  @Override
  protected void onStart() {
    // Toast.makeText(getBaseContext(),
    // getResources().getString(R.string.LongClickPlaceImg),
    // Toast.LENGTH_SHORT).show();
    super.onStart();
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
      Bitmap bm = (Bitmap) intent.getExtras().get("data");
      place_img.setImageBitmap(bm); // Display image in the View
      place_img.invalidate();
      break;
    case PICK_IMAGE_REQUEST_CODE:
      Uri selectedImage = intent.getData();
      String[] filePathColumn = { MediaStore.Images.Media.DATA };

      Cursor cursor = getContentResolver().query(selectedImage, filePathColumn,
          null, null, null);
      cursor.moveToFirst();

      int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
      String filePath = cursor.getString(columnIndex);
      cursor.close();

      BitmapFactory.Options options = new BitmapFactory.Options();
      options.inSampleSize = 3;
      Bitmap yourSelectedImage = BitmapFactory.decodeFile(filePath, options);

      if (yourSelectedImage != null) {
        place_img.setImageBitmap(yourSelectedImage);
        place_img.invalidate();
      }
      break;
    }

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
        + "/DCIM";// External.getExternalStorageDirectory() 返�?存儲?��?置�??�稱
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
