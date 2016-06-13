package com.vito.mykeyboard.ui.adapters;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.vito.mykeyboard.R;

/**
 * Created by vito on 6/13/2016.
 */
public class HistoryListAdapter extends ArrayAdapter<String> {
  private final Context context;
  private final String[] names;

  public HistoryListAdapter(Context context, String[] names) {
    super(context, R.layout.item_history_list, names);
    this.context = context;
    this.names = names;
  }

  static class ViewHolder {
    public TextView textView;
  }

//  @Override
//  public View getView(int position, View convertView, ViewGroup parent) {
//
//    ViewHolder holder;
//    // Очищает сущетсвующий шаблон, если параметр задан
//    // Работает только если базовый шаблон для всех классов один и тот же
//    View rowView = convertView;
//    if (rowView == null) {
//      LayoutInflater inflater = context.getLayoutInflater();
//      rowView = inflater.inflate(R.layout.item_history_list, null, true);
//      holder = new ViewHolder();
//      holder.textView = (TextView) rowView.findViewById(R.id.textView);
//      rowView.setTag(holder);
//    } else {
//      holder = (ViewHolder) rowView.getTag();
//    }
//
//    holder.textView.setText(names[position]);
//
//    return rowView;
//  }
}