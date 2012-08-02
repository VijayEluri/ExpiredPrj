package com.expired.db;

import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.util.Log;

/**
 * @author w200
 */
public class DataHelper {
  private static final String TAG = "DataHelper";
  private static final String DATABASE_NAME = "ExpriedPrj.db";
  private static final int DATABASE_VERSION = 1;
  private static final String TABLE_PLACE = "place";
  private static final String TABLE_STUFF = "stuff";
  private static final String TABLE_IMAGE = "images";

  // Table Place columns
  private static final String PLACE_ID = "id";
  private static final String PLACE_NAME = "name";
  // private static final String PLACE_DESC = "desc";
  private static final String PLACE_POSITION = "position";
  private static final String PLACE_PICID = "picId";

  // Table Stuff columns
  private static final String STUFF_ID = "id";
  private static final String STUFF_NAME = "name";
  // private static final String STUFF_DESC = "desc";
  private static final String STUFF_EXPIREDATE = "expiredate";
  private static final String STUFF_PLACEID = "placeId"; // connect to place
  // id
  private static final String STUFF_PICID = "picId";
  private static final String STUFF_SHARED = "beenShared";

  // Table Images columns
  private static final String IMG_ID = "id";
  private static final String IMG_DATA = "data";
  private static final String IMG_HASH = "hash";

  private Context context;
  private SQLiteDatabase db;
  @SuppressWarnings("unused")
  private SharedPreferences prefs;

  private SQLiteStatement insertStmt_place;
  private SQLiteStatement insertStmt_stuff;
  private SQLiteStatement insertStmt_image;
  private static final String INSERT_PLACE = "insert into " + TABLE_PLACE + "("
      + PLACE_NAME + ", " + PLACE_POSITION + ", " + PLACE_PICID
      + ") values (?,?,?)";
  private static final String INSERT_STUFF = "insert into " + TABLE_STUFF + "("
      + STUFF_NAME + ", " + STUFF_EXPIREDATE + ", " + STUFF_PLACEID + ", "
      + STUFF_PICID + ", " + STUFF_SHARED + ") values (?,?,?,?,?)";
  private static final String INSERT_IMAGE = "insert into " + TABLE_IMAGE + "("
      + IMG_DATA + ", " + IMG_HASH + ") values (?,?)";

  /**
   * @uml.property name="instance"
   * @uml.associationEnd
   */
  private static DataHelper instance;

  private DataHelper(Context context) {
    this.context = context;
    OpenHelper openHelper = new OpenHelper(this.context);
    this.db = openHelper.getWritableDatabase();
    this.insertStmt_place = this.db.compileStatement(INSERT_PLACE);
    this.insertStmt_stuff = this.db.compileStatement(INSERT_STUFF);
    this.insertStmt_image = this.db.compileStatement(INSERT_IMAGE);

    prefs = this.context.getSharedPreferences("pref", 0);

  }

  /**
   * This is for Singleton Pattern.
   * 
   * @param context
   * @return DataHelper instance
   */
  public static DataHelper getInstance(Context context) {
    if (instance == null) {
      instance = new DataHelper(context);
    }
    return instance;
  }

  /**
   * Update TABLE image. Used for changing image source or after rotating image.
   * 
   * @param id
   * @param data
   * @param hash
   * @return number of rows affected
   */
  public int update_image(String id, Bitmap data, Bitmap hash) {
    int rowAffected = 0;
    if (data == null) {
      ContentValues cv = new ContentValues();
      cv.put("hash", getBitmapAsByteArray(hash));
      rowAffected += this.db.update(TABLE_IMAGE, cv, "id=(?)",
          new String[] { id });
    }
    if (hash == null) {
      ContentValues cv = new ContentValues();
      cv.put("data", getBitmapAsByteArray(data));
      rowAffected += this.db.update(TABLE_IMAGE, cv, "id=(?)",
          new String[] { id });
    }
    return rowAffected;
  }

  /**
   * Update some specific place record. Given id, then update the column's
   * value.
   * 
   * @param id
   * @param column_name
   * @param column_value
   * @return number of rows affected
   */
  public int update_place(String id, String[] column_names,
      String[] column_values) {
    try {
      ContentValues cv = new ContentValues();

      for (int i = 0; i < column_names.length; i++) {
        cv.put(column_names[i], column_values[i]);
      }
      return this.db.update(TABLE_PLACE, cv, "id=(?)", new String[] { id });

    } catch (SQLException e) {
      e.printStackTrace();
      return 0;
    }
  }

  /**
   * Update some specific stuff record. Given id, then update the column's
   * value.
   * 
   * @param id
   * @param column_name
   * @param column_value
   * @return number of rows affected
   */
  public int update_stuff(String id, String[] column_names,
      String[] column_values) {
    try {
      ContentValues cv = new ContentValues();

      for (int j = 0; j < column_names.length; j++) {
        cv.put(column_names[j], column_values[j]);
      }
      return this.db.update(TABLE_STUFF, cv, "id=(?)", new String[] { id });

    } catch (SQLException e) {
      e.printStackTrace();
      return 0;
    }
  }

  public String selectFromPlaceById(String _id) {
    List<String> list = new ArrayList<String>();

    try {
      Cursor cursor = this.db.query(TABLE_PLACE, new String[] { PLACE_ID,
          PLACE_NAME, PLACE_POSITION, PLACE_PICID }, "id = (?)",
          new String[] { _id }, null, null, null);
      if (cursor.moveToFirst()) {
        do {
          String id = String.valueOf(cursor.getInt(cursor
              .getColumnIndex(PLACE_ID)));
          String name = cursor.getString(cursor.getColumnIndex(PLACE_NAME));
          // String desc = cursor.getString(cursor.getColumnIndex(PLACE_DESC));
          String pos = cursor.getString(cursor.getColumnIndex(PLACE_POSITION));
          int pic = cursor.getInt(cursor.getColumnIndex(PLACE_PICID));
          list.add(id + "," + name + "," + pos + "," + String.valueOf(pic));
        } while (cursor.moveToNext());
      }
      if (cursor != null && !cursor.isClosed()) {
        cursor.close();
      }
      return list.get(0).toString();
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * To query some specific place record by given name column, and corresponding
   * name value.
   * 
   * @param column_name
   * @param name_value
   * @return List of place record with all columns. Only one record.
   */
  public List<String> selectFromPlace(String column_name, String name_value) {

    List<String> list = new ArrayList<String>();
    try {
      // Cursor cursor = this.db.rawQuery("SELECT * FROM " + TABLE_NAME
      // + " WHERE " + column_name + " LIKE '%" + name_value
      // + "%' AND " + column_desc + " LIKE '%" + desc_value + "%'",
      // null);

      Cursor cursor = this.db.query(TABLE_PLACE, new String[] { PLACE_ID,
          PLACE_NAME, PLACE_POSITION, PLACE_PICID }, column_name+" = (?)",
          new String[] { name_value }, null, null, null);
      if (cursor.moveToFirst()) {
        do {
          String id = String.valueOf(cursor.getInt(cursor
              .getColumnIndex(PLACE_ID)));
          String name = cursor.getString(cursor.getColumnIndex(PLACE_NAME));
          // String desc = cursor.getString(cursor.getColumnIndex(PLACE_DESC));
          String pos = cursor.getString(cursor.getColumnIndex(PLACE_POSITION));
          int pic = cursor.getInt(cursor.getColumnIndex(PLACE_PICID));
          list.add(id + "," + name + "," + pos + "," + String.valueOf(pic));
        } while (cursor.moveToNext());
      }
      if (cursor != null && !cursor.isClosed()) {
        cursor.close();
      }
      return list;
    } catch (SQLException e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * Query some specific stuff record by given name column, and corresponding
   * name values.
   * 
   * @param column_name
   * @param name_value
   * @return List of stuff record with all columns. only one record.
   */
  public List<String> selectFromStuff(String column_name, String name_value) {
    List<String> list = new ArrayList<String>();
    try {

      Cursor cursor = this.db.query(TABLE_STUFF, new String[] { STUFF_ID,
          STUFF_NAME, STUFF_EXPIREDATE, STUFF_PLACEID, STUFF_PICID },
          "name = (?)", new String[] { name_value }, null, null,
          STUFF_EXPIREDATE + " ASC");
      if (cursor.moveToFirst()) {
        do {
          String id = String.valueOf(cursor.getInt(cursor
              .getColumnIndex(STUFF_ID)));
          String name = cursor.getString(cursor.getColumnIndex(STUFF_NAME));
          // String desc = cursor.getString(cursor.getColumnIndex(STUFF_DESC));
          String expiredate = cursor.getString(cursor
              .getColumnIndex(STUFF_EXPIREDATE));
          String placeId = cursor.getString(cursor
              .getColumnIndex(STUFF_PLACEID));
          int picId = cursor.getInt(cursor.getColumnIndex("picId"));
          list.add(id + "," + name + "," + expiredate + "," + placeId + ","
              + String.valueOf(picId));
        } while (cursor.moveToNext());
      }
      if (cursor != null && !cursor.isClosed()) {
        cursor.close();
      }
      return list;
    } catch (SQLException e) {
      e.printStackTrace();
      return null;
    }

  }

  public List<String> selectFromStuffByPlaceId(String placeId) {
    List<String> list = new ArrayList<String>();
    try {
      Cursor cursor = this.db.rawQuery("SELECT * FROM " + TABLE_STUFF
          + " WHERE placeId = (?)", new String[] { placeId });
      if (cursor.moveToFirst()) {
        do {
          String id = String.valueOf(cursor.getInt(cursor
              .getColumnIndex(STUFF_ID)));
          String name = cursor.getString(cursor.getColumnIndex(STUFF_NAME));
          // String desc = cursor.getString(cursor.getColumnIndex(STUFF_DESC));
          String expiredate = cursor.getString(cursor
              .getColumnIndex(STUFF_EXPIREDATE));
          String placeid = cursor.getString(cursor
              .getColumnIndex(STUFF_PLACEID));
          int picId = cursor.getInt(cursor.getColumnIndex("picId"));
          list.add(id + "," + name + "," + expiredate + "," + placeid + ","
              + String.valueOf(picId));
        } while (cursor.moveToNext());
      }
      if (cursor != null && !cursor.isClosed()) {
        cursor.close();
      }
      return list;
    } catch (SQLException e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * Query some image by id
   * 
   * @param id
   * @return img data & its hash value. 2 row of data as a record. 0 as data, 1
   *         as hash.
   */
  public List<byte[]> selectFromImage(int id) {
    List<byte[]> list = new ArrayList<byte[]>();
    Cursor cursor = this.db.query(TABLE_IMAGE, new String[] { IMG_ID, IMG_DATA,
        IMG_HASH }, "id = (?)", new String[] { String.valueOf(id) }, null,
        null, null);
    if (cursor.moveToFirst()) {
      do {
        byte[] picData = cursor.getBlob(cursor.getColumnIndex(IMG_DATA));
        byte[] hashData = cursor.getBlob(cursor.getColumnIndex(IMG_HASH));
        list.add(picData);
        list.add(hashData);
      } while (cursor.moveToNext());
    }
    if (cursor != null && !cursor.isClosed()) {
      cursor.close();
    }
    return list;
  }

  /**
   * Add one image record
   * 
   * @param pic
   * @return the row ID of the last row inserted, if this insert is successful.
   *         -1 otherwise.
   */
  public long insert_image(Bitmap pic) {
    byte[] byteArr = getBitmapAsByteArray(pic);
    this.insertStmt_image.bindBlob(1, byteArr);
    byte[] hashArr = null;
    try {
      hashArr = getHashCode(byteArr);
      this.insertStmt_image.bindBlob(2, hashArr);
    } catch (SQLiteException sqlite) {
      Log.e(TAG, sqlite.getMessage().toString());
    } catch (Exception e) {
      Log.e(TAG, e.getMessage().toString());
    }
    return this.insertStmt_image.executeInsert();
  }

  /**
   * Add one place record
   * 
   * @param name
   * @param desc
   * @param position
   * @param pic
   * @return the row ID of the last row inserted, if this insert is successful.
   *         -1 otherwise.
   */
  public long insert_place(String name, /* String desc, */String position,
      Bitmap pic) {
    this.insertStmt_place.bindString(1, name);
    // this.insertStmt_place.bindString(2, desc);
    this.insertStmt_place.bindString(2, position);
    this.insertStmt_place.bindLong(3, insert_image(pic));
    return this.insertStmt_place.executeInsert();
  }

  /**
   * Add one stuff record
   * 
   * @param name
   * @param desc
   * @param expiredate
   * @param placeId
   * @param picId
   * @return the row ID of the last row inserted, if this insert is successful.
   *         -1 otherwise.
   */
  public long insert_stuff(String name, /* String desc, */Date expiredate,
      String place_id, Bitmap pic) {
    this.insertStmt_stuff.bindString(1, name);
    // this.insertStmt_stuff.bindString(2, desc);
    this.insertStmt_stuff.bindString(2, expiredate.toGMTString());
    this.insertStmt_stuff.bindString(3, place_id);
    this.insertStmt_stuff.bindLong(4, insert_image(pic));
    this.insertStmt_stuff.bindString(5, String.valueOf(0));
    return this.insertStmt_stuff.executeInsert();
  }

  /**
   * Delete some specific record in some table
   * 
   * @param Table
   * @param id
   * @return the number of rows affected if a whereClause is passed in, 0
   *         otherwise. To remove all rows and get a count pass "1" as the
   *         whereClause.
   */
  public int deleteFromTable(String Table, String id) {
    int rowAffected = this.db.delete(Table, "id = (?)", new String[] { id });
    Log.i(TAG, "Deleting row, " + rowAffected + " is deleted.");
    return rowAffected;
  }

  /**
   * Delete all records in some table
   * 
   * @param table_name
   * @return the number of rows affected if a whereClause is passed in, 0
   *         otherwise. To remove all rows and get a count pass "1" as the
   *         whereClause.
   */
  public int deleteAll(String table_name) {
    if (table_name.equals(TABLE_PLACE))
      return this.db.delete(TABLE_PLACE, null, null);
    else
      return this.db.delete(TABLE_STUFF, null, null);
  }

  /**
   * Select all records from indicated TABLE NAME, for now only support TABLE
   * place & stuff.(TABLE images usually only query for one record).
   * 
   * @param table_name
   * @return List of all records in that TABLE, in format of String
   */
  public List<String> selectAll(String table_name, String order) {
    List<String> list = new ArrayList<String>();
    if (table_name.equals(TABLE_PLACE)) {
      Cursor cursor = this.db.query(TABLE_PLACE, null, null, null, null, null,
          "name " + order);

      if (cursor.moveToFirst()) {
        do {
          list.add(cursor.getInt(cursor.getColumnIndex(PLACE_ID)) + ","
              + cursor.getString(cursor.getColumnIndex(PLACE_NAME)) + ","
              + cursor.getString(cursor.getColumnIndex(PLACE_POSITION)) + ","
              + cursor.getString(cursor.getColumnIndex(PLACE_PICID)));
        } while (cursor.moveToNext());
      }
      if (cursor != null && !cursor.isClosed()) {
        cursor.close();
      }
    }
    if (table_name.equals(TABLE_STUFF)) {
      Cursor cursor = this.db.query(TABLE_STUFF, null, null, null, null, null,
          STUFF_EXPIREDATE + " " + order);

      // new String[] { "name","desc", "expiredate", "place_name", "pic" }
      /*
       * .rawQuery( "SELECT name,desc,expiredate,place_name,pic FROM stuff",
       * null);
       */

      if (cursor.moveToFirst()) {
        do {
          list.add(cursor.getInt(cursor.getColumnIndex(STUFF_ID))
              + ","
              + cursor.getString(cursor.getColumnIndex(STUFF_NAME))
              + ","
              /*
               * + cursor.getString(cursor.getColumnIndex(STUFF_DESC)) + ","
               */
              + cursor.getString(cursor.getColumnIndex(STUFF_EXPIREDATE))
              + ","
              + String.valueOf(cursor.getInt(cursor
                  .getColumnIndex(STUFF_PLACEID)))
              + ","
              + String.valueOf(cursor.getInt(cursor.getColumnIndex(STUFF_PICID))));
        } while (cursor.moveToNext());

      }
      if (cursor != null && !cursor.isClosed()) {
        cursor.close();
      }

      SortExpireDate(list, order);
    }
    return list;
  }

  private void SortExpireDate(List<String> list, String order) {
    if (order.equalsIgnoreCase("asc")) {
      Collections.sort(list, new Comparator<String>() {
        public int compare(String object1, String object2) {
          Date d1 = new Date(object1.split(",")[2]);
          Date d2 = new Date(object2.split(",")[2]);
          // asc
          return d1.compareTo(d2);
        }
      });
    } else {
      Collections.sort(list, new Comparator<String>() {
        public int compare(String object1, String object2) {
          Date d1 = new Date(object1.split(",")[2]);
          Date d2 = new Date(object2.split(",")[2]);
          // desc
          return d2.compareTo(d1);
        }
      });
    }

    // for (int i = 0; i < list.size(); i++) {
    // Date date = (Date) list.get(i);
    // Log.i(TAG, list.get(i).split(",")[3]);
    // }
  }

  public byte[] getBitmapAsByteArray(Bitmap bitmap) {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    // Middle parameter is quality, but since PNG is lossless, it doesn't
    // matter
    bitmap.compress(CompressFormat.PNG, 0, outputStream);
    return outputStream.toByteArray();
  }

  public byte[] getHashCode(byte[] bitmapData) throws NoSuchAlgorithmException {
    MessageDigest md = MessageDigest.getInstance("SHA-1");
    md.update(bitmapData); // It's the same bitmap data that you got from
    // getBitmapAsByteArray
    byte[] digest = md.digest();
    return digest;
  }

  private static class OpenHelper extends SQLiteOpenHelper {

    private static final String TAG = "OpenHelper";

    OpenHelper(Context context) {
      super(context, DATABASE_NAME, null, DATABASE_VERSION);
      Log.i(TAG, "Create or Opening database...");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
      db.execSQL("CREATE TABLE " + TABLE_PLACE + " (" + PLACE_ID
          + " INTEGER PRIMARY KEY, " + PLACE_NAME + " TEXT UNIQUE NOT NULL, "
          /* + PLACE_DESC + " TEXT, " */+ PLACE_POSITION + " TEXT, "
          + PLACE_PICID + " INTEGER)");
      db.execSQL("CREATE TABLE " + TABLE_STUFF + " (" + STUFF_ID
          + " INTEGER PRIMARY KEY, " + STUFF_NAME + " TEXT UNIQUE NOT NULL, "
          /* + STUFF_DESC + " TEXT, " */+ STUFF_EXPIREDATE
          + " DATETIME NOT NULL, " + STUFF_PLACEID + " INTEGER, " + STUFF_PICID
          + " INTEGER, " + STUFF_SHARED + " INTEGER)");
      db.execSQL("CREATE TABLE " + TABLE_IMAGE + "(" + IMG_ID
          + " INTEGER PRIMARY KEY AUTOINCREMENT," + IMG_DATA + " BLOB,"
          + IMG_HASH + " BLOB" + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
      Log.w("Example",
          "Upgrading database, this will drop tables and recreate.");
      db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLACE);
      db.execSQL("DROP TABLE IF EXISTS " + TABLE_STUFF);
      db.execSQL("DROP TABLE IF EXISTS " + TABLE_IMAGE);
      onCreate(db);
    }
  }
}