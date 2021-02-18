package com.liuzhenli.reader.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.microedu.reader.R;


/**
 * 数据表，行数据详情 (由于内容长度过长，需要查看完整内容)
 *
 * @author liuzhenli
 * @date 2021.02.18
 */
public class DatabaseTableRecordDetailAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private String[] titles;
    private String[] cellValues;


    public DatabaseTableRecordDetailAdapter(Context ctx, String[] titles, String[] cellValues) {
        context = ctx;
        inflater = LayoutInflater.from(context);
        this.titles = titles;
        this.cellValues = cellValues;
    }

    @Override
    public int getCount() {
        return cellValues == null ? 0 : cellValues.length;
    }

    @Override
    public Object getItem(int position) {
        return cellValues == null ? null : cellValues[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemHolder itemHolder;
        if (convertView == null) {
            itemHolder = new ItemHolder();
            convertView = inflater.inflate(R.layout.item_diagnose_database_table_record_column, null);
            itemHolder.tvColumnTitle = (TextView) convertView.findViewById(R.id.tv_diagnose_databse_table_colum_title);
            itemHolder.tvColumnValue = (TextView) convertView.findViewById(R.id.tv_diagnose_databse_table_column_value);
            convertView.setTag(itemHolder);
        } else {
            itemHolder = (ItemHolder) convertView.getTag();
        }

        itemHolder.tvColumnTitle.setText(titles[position]);
        itemHolder.tvColumnValue.setText(cellValues[position]);

        return convertView;
    }

    class ItemHolder {
        TextView tvColumnTitle;
        TextView tvColumnValue;
    }

}
