package com.liuzhenli.reader.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.microedu.reader.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liuzhenli
 * @date 2021.02.18
 */
public class DatabaseTableDataGridAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private int cloumnCount;
    private int selectedRow = -1;

    private List<String[]> dataset;


    public DatabaseTableDataGridAdapter(Context ctx) {
        context = ctx;
        inflater = LayoutInflater.from(context);
    }

    public void setDataset(List<String[]> data) {
        if (dataset == null) {
            dataset = new ArrayList<String[]>();
        } else {
            dataset.clear();
        }
        selectedRow = -1;
        cloumnCount = data.get(0).length;

        dataset.addAll(data);
        notifyDataSetChanged();
    }

    public void setSelectedRow(int clickedItemPostion) {
        if (clickedItemPostion > cloumnCount) {
            selectedRow = clickedItemPostion / cloumnCount;
            notifyDataSetChanged();
        }
    }

    public String[] getTableTitles() {
        if (dataset != null && dataset.size() > 0) {
            return dataset.get(0);
        }
        return null;
    }

    public String[] getRowData(int cellPostion) {
        if (dataset != null && dataset.size() > 0 && cellPostion > cloumnCount) {
            int row = cellPostion / cloumnCount;
            return dataset.get(row);
        }
        return null;
    }

    @Override
    public int getCount() {
        if (dataset == null) {
            return 0;
        } else {
            int rowCount = dataset.size();
            int columnCount = dataset.get(0).length;
            return rowCount * columnCount;
        }
    }

    @Override
    public Object getItem(int position) {
        if (dataset == null) {
            return null;
        } else {
            int column = 0;
            int row = position / cloumnCount;

            if (row == 0) {
                column = position;
            } else {
                column = position % cloumnCount;
            }
            return dataset.get(row)[column];
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemHolder holder;
        if (convertView == null) {
            holder = new ItemHolder();
            convertView = inflater.inflate(R.layout.item_diagnose_database_table_cell, null);
            holder.layoutRoot = convertView.findViewById(R.id.layout_root_diagnose_database_table_cell);
            holder.tvTableCellValue = (TextView) convertView.findViewById(R.id.tv_diagnose_database_table_cell_value);
            convertView.setTag(holder);
        } else {
            holder = (ItemHolder) convertView.getTag();
        }

        if (position < cloumnCount) { // 表头(字段名称)
            holder.layoutRoot.setBackgroundResource(R.drawable.bg_database_table_title_cell);
        } else {
            int row = position / cloumnCount;
            if (row == selectedRow) {
                holder.layoutRoot.setBackgroundResource(R.drawable.bg_database_table_data_row_selected);
            } else {
                holder.layoutRoot.setBackgroundResource(R.drawable.bg_database_table_data_cell);
            }
        }

        String cellValue = "";
        Object value = getItem(position);
        if (value != null) {
            cellValue = (String) value;
        }

        holder.tvTableCellValue.setText(cellValue);

        return convertView;
    }

    class ItemHolder {
        View layoutRoot;
        TextView tvTableCellValue;
    }

}
