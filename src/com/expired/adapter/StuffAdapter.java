package com.expired.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.expired.db.DataPool;
import com.expired.v1.R;

public class StuffAdapter extends BaseAdapter {
  private LayoutInflater m_inflater;
  public List<Bitmap> m_preview;
  // public List<Bitmap> m_indicator;
  private List<String> m_stuff;
  // private List<String> m_desc;
  private List<String> m_interval;
  @SuppressWarnings("unused")
  private static final String TAG = "StuffAdapter";

  public StuffAdapter(Context context, List<Bitmap> preview, List<String> name,
  /* List<String> desc, */List<String> interval) {
    m_inflater = LayoutInflater.from(context);
    m_preview = preview;
    m_stuff = name;
    // m_desc = desc;
    m_interval = interval;
  }

  public int getCount() {
    return m_stuff.size();
  }

  public String getItem(int position) {
    return m_stuff.get(position);
  }

  public long getItemId(int position) {
    return position;
  }

  public View getView(int position, View convertView, ViewGroup parent) {
    if (convertView == null)
      convertView = m_inflater.inflate(R.layout.stuff_list_item, null);

    ImageView previewImg = (ImageView) convertView
        .findViewById(R.id.StuffPreviewImg);
    previewImg.setImageBitmap(m_preview.get(position));

    TextView stuffName = (TextView) convertView.findViewById(R.id.stuff_name);
    // TextView stuffDesc = (TextView)
    // convertView.findViewById(R.id.stuff_desc);
    stuffName.setText(m_stuff.get(position));
    // stuffDesc.setText(m_desc.get(position));
    TextView stuffDayLeft = (TextView) convertView.findViewById(R.id.dayLeft);
    stuffDayLeft.setOnTouchListener(new OnTouchListener() {
      public boolean onTouch(View v, MotionEvent event) {
        Toast.makeText(v.getContext(),
            v.getResources().getString(R.string.DayLeftTip), Toast.LENGTH_SHORT);
        return false;
      }
    });
    String interval = m_interval.get(position);

    DataPool.locale = convertView.getResources().getConfiguration().locale
        .getDisplayName();
    // Log.i("StuffAdapter", DataPool.locale.toString());
    if (Integer.parseInt(interval) < 0) {
      stuffDayLeft.setTextColor(convertView.getResources().getColor(
          R.color.dark_grey));
      stuffDayLeft.invalidate();
      if (DataPool.locale.contains("Eng")) {

        stuffDayLeft.setText(convertView.getResources().getString(
            R.string.expired)
            + " "
            + Math.abs(Integer.parseInt(interval))
            + " "
            + convertView.getResources().getString(R.string.day)
            + " "
            + convertView.getResources().getString(R.string.ago));
      } else {
        stuffDayLeft.setText(Math.abs(Integer.parseInt(interval)) + " "
            + convertView.getResources().getString(R.string.day) + " "
            + convertView.getResources().getString(R.string.ago)
            + convertView.getResources().getString(R.string.expired));
      }
    } else if (Integer.parseInt(interval) == 0) {
      stuffDayLeft.setTextColor(Color.RED);
      stuffDayLeft.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD_ITALIC));
      stuffDayLeft.setText(convertView.getResources().getString(
          R.string.expiredToday));
      stuffDayLeft.invalidate();

    } else if (Integer.parseInt(interval) == 1) {
      stuffDayLeft.setTextColor(Color.RED);
      stuffDayLeft.invalidate();
      if (DataPool.locale.contains("Eng")) {
        stuffDayLeft.setText(interval + " "
            + convertView.getResources().getString(R.string.day) + " "
            + convertView.getResources().getString(R.string.left));
      } else {
        stuffDayLeft.setText(convertView.getResources()
            .getString(R.string.left)
            + " "
            + interval
            + " "
            + convertView.getResources().getString(R.string.day));
      }

    } else if (Integer.parseInt(interval) > 1) {
      if (Integer.parseInt(interval) < 3) {
        stuffDayLeft.setTextColor(Color.RED);
        stuffDayLeft.invalidate();
      }
      if (Integer.parseInt(interval) >= 3 && Integer.parseInt(interval) < 7) {
        stuffDayLeft.setTextColor(convertView.getResources().getColor(
            R.color.blue));
        stuffDayLeft.invalidate();
      }
      if (Integer.parseInt(interval) >= 7) {
        stuffDayLeft.setTextColor(convertView.getResources().getColor(
            R.color.blue));
        stuffDayLeft.invalidate();
      }
      if (DataPool.locale.contains("Eng")) {
        stuffDayLeft.setText(interval + " "
            + convertView.getResources().getString(R.string.day) + "s "
            + convertView.getResources().getString(R.string.left));
      } else {
        stuffDayLeft.setText(convertView.getResources()
            .getString(R.string.left)
            + " "
            + interval
            + " "
            + convertView.getResources().getString(R.string.day));
      }
    }

    // Log.i(TAG, convertView.getResources().getConfiguration().locale
    // .getDisplayName());

    // ImageView indicatorImg = (ImageView) convertView
    // .findViewById(R.id.ExpireIndicatorImg);
    // indicatorImg.setImageBitmap(m_indicator.get(position));
    return convertView;
  }
}
