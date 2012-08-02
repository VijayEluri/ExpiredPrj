package com.expired.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.expired.v1.R;

public class PlaceAdapter extends BaseAdapter {
  private LayoutInflater m_inflater;
  public List<Bitmap> m_preview;
  public List<String> m_location;

  // private List<String> m_desc;

  public PlaceAdapter(Context context, List<Bitmap> preview,
      List<String> location/* , List<String> desc */) {
    m_inflater = LayoutInflater.from(context);
    m_preview = preview;
    m_location = location;
    // m_desc = desc;
  }

  public int getCount() {

    return m_location.size();
  }

  public Object getItem(int position) {
    return m_location.get(position);
  }

  public long getItemId(int position) {
    return position;
  }

  public View getView(int position, View convertView, ViewGroup parent) {
    if (convertView == null)
      convertView = m_inflater.inflate(R.layout.place_list_item, null);

    TextView locName = (TextView) convertView.findViewById(R.id.place_name);
    // TextView locDesc = (TextView) convertView.findViewById(R.id.place_desc);
    locName.setText(m_location.get(position));
    // locDesc.setText(m_desc.get(position));

    ImageView previewImg = (ImageView) convertView
        .findViewById(R.id.PlacePreviewImg);
    previewImg.setImageBitmap(m_preview.get(position));

    return convertView;
  }

}
