package com.liuzhenli.reader.ui.activity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.liuzhenli.reader.base.BaseActivity;
import com.liuzhenli.reader.network.AppComponent;
import com.liuzhenli.reader.ui.adapter.DatabaseTableDataGridAdapter;
import com.liuzhenli.reader.utils.ToastUtil;
import com.micoredu.readerlib.helper.DbHelper;
import com.microedu.reader.R;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.ArrayList;
import java.util.List;

import nl.siegmann.epublib.util.StringUtil;


/**
 * 浏览数据表中的数据
 *
 * @author liuzhenli
 * @date 2021.02.18
 */
public class DatabaseTableRecordsActivity extends BaseActivity {

    public static final String EXTRA_TABLE_NAME = "TableName";


    private GridView girdTableData;
    private DatabaseTableDataGridAdapter tableDataAdapter;

    private String tableName;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_diagnose_database_table_record;
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {

    }

    @Override
    protected void initToolBar() {

    }

    @Override
    protected void initData() {
        tableName = getIntent().getStringExtra(EXTRA_TABLE_NAME);
    }

    @Override
    protected void configViews() {
        if (StringUtil.isNotBlank(tableName)) {
            mTvTitle.setText(tableName);
        }

        girdTableData = (GridView) findViewById(R.id.gird_diagnose_database_table_data);
        tableDataAdapter = new DatabaseTableDataGridAdapter(DatabaseTableRecordsActivity.this);
        girdTableData.setAdapter(tableDataAdapter);

        girdTableData.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                tableDataAdapter.setSelectedRow(position);

                String[] titles = tableDataAdapter.getTableTitles();
                String[] dataRow = tableDataAdapter.getRowData(position);
                if (titles != null && dataRow != null) {
                    Intent intent = new Intent(DatabaseTableRecordsActivity.this, DatabaseTableRecordDetailActivity.class);
                    intent.putExtra(DatabaseTableRecordDetailActivity.EXTRA_TABLE_TITLES, titles);
                    intent.putExtra(DatabaseTableRecordDetailActivity.EXTRA_ROW_DATA, dataRow);
                    startActivity(intent);
                }
            }
        });
        new LoadTableRecordTask(tableName).execute();
    }


    class LoadTableRecordTask extends AsyncTask<String, Integer, List<String[]>> {

        private String tableName;

        public LoadTableRecordTask(String tableName) {
            this.tableName = tableName;
        }

        @Override
        protected void onPreExecute() {
            showDialog();
        }

        @Override
        protected List<String[]> doInBackground(String... params) {
            List<String[]> result = new ArrayList<String[]>();

            SQLiteDatabase database = DbHelper.getDb();
            Cursor cursor = database.rawQuery("select * from " + tableName, null);

            String[] ColumnNames = cursor.getColumnNames();
            result.add(ColumnNames);

            int columnsCount = ColumnNames.length;
            while (cursor.moveToNext()) {
                String[] columnValues = new String[columnsCount];
                for (int i = 0; i < columnsCount; i++) {
                    columnValues[i] = cursor.getString(i);
                }
                result.add(columnValues);
            }
            cursor.close();

            return result;
        }

        @Override
        protected void onPostExecute(List<String[]> result) {

            hideDialog();
            if (result != null) {
                int columnCount = result.get(0).length;
                girdTableData.setNumColumns(columnCount);

                ViewGroup.LayoutParams gridLayoutParams = girdTableData.getLayoutParams();
                gridLayoutParams.width = columnCount * UIUtil.dip2px(DatabaseTableRecordsActivity.this, 96);
                girdTableData.setLayoutParams(gridLayoutParams);
                tableDataAdapter.setDataset(result);
            } else {
                ToastUtil.showToast(DatabaseTableRecordsActivity.this, "加载数据表记录失败!!!");
            }
        }
    }


    class LoadTableDataTask extends AsyncTask<Integer, Integer, List<String[]>> {

        private String tableName;

        public LoadTableDataTask(String tableName) {
            this.tableName = tableName;
        }

        @Override
        protected void onPreExecute() {
            showDialog();
        }

        @Override
        protected List<String[]> doInBackground(Integer... params) {
            List<String[]> result = new ArrayList<String[]>();

            int offset = params[0];
            int count = params[1];
            SQLiteDatabase database = DbHelper.getDb();
            Cursor cursor = database.rawQuery("select * from " + tableName + "limit ?,?", new String[]{String.valueOf(offset), String.valueOf(count)});

            // 读取表头
            if (offset == 0) {
                String[] ColumnNames = cursor.getColumnNames();
                result.add(ColumnNames);
            }

            // 读取行数据
            int columnsSize = cursor.getColumnCount();
            while (cursor.moveToNext()) {
                String[] columnValues = new String[columnsSize];
                for (int i = 0; i < columnsSize; i++) {
                    columnValues[i] = cursor.getString(i);
                }
                result.add(columnValues);
            }
            cursor.close();
            return result;
        }

        @Override
        protected void onPostExecute(List<String[]> result) {
            hideDialog();
        }
    }

}
