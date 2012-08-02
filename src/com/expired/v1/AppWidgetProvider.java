package com.expired.v1;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import com.expired.db.DataHelper;
import com.expired.db.DataPool;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.widget.RemoteViews;

public class AppWidgetProvider extends android.appwidget.AppWidgetProvider {

  private static final String TAG = "AppWidgetProvider";
  private int interval = 5;

  @Override
  public void onDeleted(Context context, int[] appWidgetIds) {
    Log.d(TAG, "onDeleted");
    super.onDeleted(context, appWidgetIds);
  }

  @Override
  public void onDisabled(Context context) {
    Log.d(TAG, "onDisabled");
    super.onDisabled(context);

  }

  @Override
  public void onEnabled(Context context) {
    Log.d(TAG, "onEnabled");

    super.onEnabled(context);
  }

  @Override
  public void onReceive(Context context, Intent intent) {
    super.onReceive(context, intent);
  }

  @Override
  public void onUpdate(Context context, AppWidgetManager appWidgetManager,
      int[] appWidgetIds) {

    WidgetUpdateService.N = appWidgetIds.length;
    WidgetUpdateService.arr = appWidgetIds;

    final Intent intent = new Intent(context, WidgetUpdateService.class);
    final PendingIntent pending = PendingIntent.getService(context, 0, intent,
        0);
    final AlarmManager alarm = (AlarmManager) context
        .getSystemService(Context.ALARM_SERVICE);
    alarm.cancel(pending);
    alarm.setRepeating(AlarmManager.ELAPSED_REALTIME,
        SystemClock.elapsedRealtime(), interval * 1000, pending);

    // context.startService(new Intent(context, WidgetUpdateService.class));
    //
    // PendingIntent updateIntent = PendingIntent.getService(context, 0,
    // refresh, 0);
    // AlarmManager am = (AlarmManager) context
    // .getSystemService(Context.ALARM_SERVICE);
    // am.setRepeating(AlarmManager.ELAPSED_REALTIME,
    // SystemClock.elapsedRealtime(), interval * 1000, updateIntent);

    super.onUpdate(context, appWidgetManager, appWidgetIds);
  }

  /**
   * @author w200
   */
  public static class WidgetUpdateService extends Service {
    /**
     * @uml.property name="dp"
     * @uml.associationEnd
     */
    private DataPool dp;
    private ArrayList<Bitmap> bitmapList;
    /**
     * @uml.property name="dh"
     * @uml.associationEnd
     */
    private DataHelper dh;
    private ArrayList<String> intervalList;
    private Calendar someCal = new GregorianCalendar();;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
    private int idx = 0;
    private int min = 0;
    private int max = 0;
    private Random rand = new Random();

    private static int N = 0;
    @SuppressWarnings("unused")
    private static int[] arr;

    private PendingIntent pendingIntent;
    private Intent intent2View;

    @Override
    public void onCreate() {
      super.onCreate();
    }

    @Override
    public void onStart(Intent intent, int startId) {
      // Log.d("UpdateService", "onStart()");

      // Build the widget update for today
      RemoteViews updateViews = BuildUpdate(this);
      // Log.d("UpdateService", "update built");

      // Push update for this widget to the home screen
      ComponentName thisWidget = new ComponentName(this,
          AppWidgetProvider.class);
      AppWidgetManager manager = AppWidgetManager.getInstance(this);
      manager.updateAppWidget(thisWidget, updateViews);
      // Log.d("UpdateService", "widget updated");
      super.onStart(intent, startId);
    }

    @Override
    public void onDestroy() {
      super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
      return null;
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

    public RemoteViews BuildUpdate(Context context) {
      String stuff_order = getSharedPreferences("prefs", 0).getString(
          "stuff_order", "desc");

      RemoteViews views = null;
      dh = DataHelper.getInstance(context);
      dp = DataPool.getInstance(context);
      dp.ResetStuffContent();
      dp.RefreshStuffContent(stuff_order);

      // Log.i(TAG, "Num of Stuff: "
      // + String.valueOf(dp.stuffNameArr.size()));
      if (dp.stuffNameArr.size() == 0) {
        views = new RemoteViews(context.getPackageName(),
            R.layout.widget_layout);
        views.setTextViewText(R.id.stuff_name, context.getResources()
            .getString(R.string.NoStuffRightNow_widget));
        views.setTextViewText(R.id.dayLeft, "");
      } else {
        //
        List<byte[]> data = null;
        bitmapList = new ArrayList<Bitmap>();
        for (String picId : dp.stuffPicIdArr) {
          data = dh.selectFromImage(Integer.valueOf(picId));
          bitmapList
              .add(StuffListView.getResizedBitmap(BitmapFactory
                  .decodeByteArray(data.get(0), 0, data.get(0).length), 48, 48));
        }

        intervalList = new ArrayList<String>();
        for (String date : dp.stuffExpiredateArr) {
          intervalList.add(getInterval(date));
        }
        //

        for (int i = 0; i < N; i++) {
          // int appWidgetId = arr[i];
          // Log.d(TAG, "onUpdate");
          min = 0;
          max = dp.stuffNameArr.size() - 1;
          idx = rand.nextInt(max - min + 1) + min;

          views = new RemoteViews(context.getPackageName(),
              R.layout.widget_layout);
          views.setImageViewBitmap(R.id.StuffPreviewImg, bitmapList.get(idx));
          if (Locale.getDefault().toString().contains("zh")
              && dp.stuffNameArr.get(idx).length() > 5)
            views.setTextViewText(R.id.stuff_name, dp.stuffNameArr.get(idx)
                .substring(0, 4) + "..");
          else
            views.setTextViewText(R.id.stuff_name, dp.stuffNameArr.get(idx));

          // Log.i(TAG, "idx: " + String.valueOf(idx));
          // Create an Intent to launch ExampleActivity
          intent2View = new Intent(context, StuffViewer.class);
          intent2View.putExtra("stuff_name", dp.stuffNameArr.get(idx));
          // intent2View.putExtra("stuff_desc", dp.stuffDescArr.get(idx));
          intent2View.putExtra("stuff_expiredate",
              dp.stuffExpiredateArr.get(idx));
          intent2View.putExtra("stuff_placeAt", dp.stuffPlaceIdArr.get(idx));
          intent2View.putExtra("stuff_picId", dp.stuffPicIdArr.get(idx));
          // Log.i(TAG, "stuff_picId: "
          // + String.valueOf(intent2View
          // .getStringExtra("stuff_picId")));

          pendingIntent = PendingIntent.getActivity(context, 1, intent2View,
              PendingIntent.FLAG_UPDATE_CURRENT);
          views.setOnClickPendingIntent(R.id.stuff_name, pendingIntent);
          views.setOnClickPendingIntent(R.id.dayLeft, pendingIntent);
          views.setOnClickPendingIntent(R.id.IndicatorImg, pendingIntent);

          String locale = Locale.getDefault().toString();
          String interval = intervalList.get(idx);
          if (Integer.parseInt(interval) < 0) {
            views.setTextColor(R.id.dayLeft,
                context.getResources().getColor(R.color.dark_grey));
            // stuffDayLeft.invalidate();
            if (locale.contains("zh")) {
              views.setTextViewText(R.id.dayLeft,
                  Math.abs(Integer.parseInt(interval)) + " "
                      + context.getResources().getString(R.string.day) + " "
                      + context.getResources().getString(R.string.ago) + " "
                      + context.getResources().getString(R.string.expired));

            } else {
              views.setTextViewText(R.id.dayLeft,
                  context.getResources().getString(R.string.expired) + " "
                      + +Math.abs(Integer.parseInt(interval)) + " "
                      + context.getResources().getString(R.string.day) + " "
                      + context.getResources().getString(R.string.ago));
            }
          } else if (Integer.parseInt(interval) == 0) {
            views.setTextColor(R.id.dayLeft,
                context.getResources().getColor(R.color.dark_grey));
            views.setTextViewText(R.id.dayLeft, context.getResources()
                .getString(R.string.expiredToday));

          } else if (Integer.parseInt(interval) == 1) {
            views.setTextColor(R.id.dayLeft, Color.RED);

            if (locale.contains("zh")) {
              views.setTextViewText(R.id.dayLeft, context.getResources()
                  .getString(R.string.left)
                  + " "
                  + interval
                  + " "
                  + context.getResources().getString(R.string.day));
            } else {
              views.setTextViewText(R.id.dayLeft, interval + " "
                  + context.getResources().getString(R.string.day) + " "
                  + context.getResources().getString(R.string.left));
            }

          } else if (Integer.parseInt(interval) > 1) {
            if (Integer.parseInt(interval) < 3) {
              views.setTextColor(R.id.dayLeft, Color.RED);
            }
            if (Integer.parseInt(interval) >= 3
                && Integer.parseInt(interval) < 7) {
              views.setTextColor(R.id.dayLeft,
                  context.getResources().getColor(R.color.blue));
            }
            if (Integer.parseInt(interval) >= 7) {
              views.setTextColor(R.id.dayLeft,
                  context.getResources().getColor(R.color.blue));
            }
            if (locale.contains("en")) {
              views.setTextViewText(R.id.dayLeft, interval + " "
                  + context.getResources().getString(R.string.day) + "s "
                  + context.getResources().getString(R.string.left));
            } else {
              views.setTextViewText(R.id.dayLeft, context.getResources()
                  .getString(R.string.left)
                  + " "
                  + interval
                  + " "
                  + context.getResources().getString(R.string.day));
            }
          }

        }
      }
      return views;
    }
  }
}
