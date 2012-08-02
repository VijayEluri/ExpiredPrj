package com.expired.v1;

import java.util.List;

import com.expired.db.DataHelper;
import com.expired.db.DataPool;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author w200
 */
public class PlaceViewer extends Activity {
  TextView name_display;
  TextView desc_display;
  ImageView place_img;
  Button OkBtn;
  private DataHelper dh;
  @SuppressWarnings("unused")
  private DataPool dp;

  @SuppressWarnings("unused")
  private static final String TAG = "PlaceViewer";
  @SuppressWarnings("unused")
  private String selectedImagePath;
  String place_id;
  String place_name;
  String place_desc;
  private String place_picId;
  @SuppressWarnings("unused")
  private static final int EDIT_PLACE_RESULT = 0;
  @SuppressWarnings("unused")
  private static final int SELECT_PICTURE = 1;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.viewplace);

    name_display = (TextView) findViewById(R.id.PlaceNameEdit);
    // desc_display = (TextView) findViewById(R.id.PlaceDescribeEdit);
    place_img = (ImageView) findViewById(R.id.PlaceImg);
    registerForContextMenu(place_img);
    OkBtn = (Button) findViewById(R.id.OkButton);
    this.dh = DataHelper.getInstance(this);
    this.dp = DataPool.getInstance(this);

    Intent viewIntent = getIntent();
    place_name = viewIntent.getStringExtra("place_name");
    // place_desc = viewIntent.getStringExtra("place_desc");
    place_picId = viewIntent.getStringExtra("place_picId");

    if (place_name != "" && place_desc != "") {

      List<byte[]> picData = this.dh.selectFromImage(Integer
          .parseInt(place_picId));
      Bitmap img = BitmapFactory.decodeByteArray(picData.get(0), 0,
          picData.get(0).length);
      place_img.setImageBitmap(img);

      name_display.setText(place_name);
      // desc_display.setText(place_desc);
    }

    OkBtn.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        finish();
      }
    });
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // MenuInflater inflater = getMenuInflater();
    // inflater.inflate(R.menu.menu_editplace, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // switch (item.getItemId()) {
    // case R.id.EditFromViewPlace:
    // Intent go2PlaceEditor = new Intent(this, PlaceEditor.class);
    // go2PlaceEditor.putExtra("name", place_name);
    // go2PlaceEditor.putExtra("desc", place_desc);
    // startActivityForResult(go2PlaceEditor, EDIT_PLACE_RESULT);
    // break;
    // }
    return super.onOptionsItemSelected(item);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode,
      Intent imageReturnedIntent) {
    switch (requestCode) {
    // case EDIT_PLACE_RESULT:
    // if (resultCode == RESULT_OK) {
    // place_img.setBackgroundResource(R.color.black);
    // Uri selectedImage = imageReturnedIntent.getData();
    // String[] filePathColumn = { MediaStore.Images.Media.DATA };
    //
    // Cursor cursor = getContentResolver().query(selectedImage,
    // filePathColumn, null, null, null);
    // cursor.moveToFirst();
    //
    // int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
    // String filePath = cursor.getString(columnIndex);
    // cursor.close();
    //
    // Bitmap yourSelectedImage = BitmapFactory.decodeFile(filePath);
    // place_img.setImageBitmap(yourSelectedImage);
    // place_img.invalidate();
    // }
    // case RESULT_OK:
    // if (data.getExtras().getString("RESULT") ==
    // "RESULT_NEW_PLACE_OK") {
    // Toast notify = Toast.makeText(getBaseContext(),
    // placeArr.get(info.position) + " deleted!",
    // Toast.LENGTH_LONG);
    // notify.setGravity(Gravity.CENTER, 0, 75);
    // notify.show();
    // }
    // break;
    }

    super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
  }

}
