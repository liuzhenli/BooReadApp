package com.liuzhenli.reader.ui.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListView;


import com.liuzhenli.reader.base.BaseActivity;
import com.liuzhenli.reader.network.AppComponent;
import com.liuzhenli.reader.ui.adapter.DatabaseTableListAdapter;
import com.micoredu.readerlib.helper.DbHelper;
import com.microedu.reader.R;

import java.util.ArrayList;
import java.util.List;

import nl.siegmann.epublib.util.StringUtil;

/**
 * 数据库中包含的数据表
 *
 * @author liuzhenli
 * @date 2021.02.18
 */
public class DatabaseTableListActivity extends BaseActivity {


    private ListView databaseTablesList;
    private DatabaseTableListAdapter tablesAdapter;
    private final List<String> tableList = new ArrayList<String>();

    public static void start(Context context) {
        Intent intent = new Intent(context, DatabaseTableListActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new LoadDatabaseTablesTask().execute();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.act_databasetablelist;
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {

    }

    @Override
    protected void initToolBar() {
        mTvTitle.setText("数据表");
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void configViews() {

        databaseTablesList = (ListView) findViewById(R.id.list_diagnose_database_tables);
        tablesAdapter = new DatabaseTableListAdapter(this);
        databaseTablesList.setAdapter(tablesAdapter);

        databaseTablesList.setOnItemClickListener((parent, view, position, id) -> {
            String tableName = tableList.get(position);
            if (StringUtil.isNotBlank(tableName)) {
                Intent intent = new Intent(DatabaseTableListActivity.this, DatabaseTableRecordsActivity.class);
                intent.putExtra(DatabaseTableRecordsActivity.EXTRA_TABLE_NAME, tableName);
                startActivity(intent);
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    class LoadDatabaseTablesTask extends AsyncTask<String, Integer, List<String>> {
        @Override
        protected List<String> doInBackground(String... params) {
            List<String> tables = new ArrayList<String>();
            SQLiteDatabase database = DbHelper.getDb();
            Cursor cursor = database.rawQuery("select name from sqlite_master where type='table' order by name", null);
            while (cursor.moveToNext()) {
                String tableName = cursor.getString(0);
                tables.add(tableName);
            }
            cursor.close();
            return tables;
        }

        @Override
        protected void onPostExecute(List<String> result) {
            tableList.addAll(result);
            tablesAdapter.setDataset(tableList);
        }
    }

}
