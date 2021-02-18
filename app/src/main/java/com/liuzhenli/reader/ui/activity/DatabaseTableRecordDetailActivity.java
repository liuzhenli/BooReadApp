package com.liuzhenli.reader.ui.activity;

import android.os.Bundle;
import android.widget.ListView;

import com.liuzhenli.reader.base.BaseActivity;
import com.liuzhenli.reader.network.AppComponent;
import com.liuzhenli.reader.ui.adapter.DatabaseTableRecordDetailAdapter;
import com.microedu.reader.R;


/**
 * 数据表，行数据详情 (由于内容长度过长，需要查看完整内容)
 *
 * @author liuzhenli
 * @date 2021.02.18
 */
public class DatabaseTableRecordDetailActivity extends BaseActivity {

    public static final String EXTRA_TABLE_TITLES = "TableTitles";
    public static final String EXTRA_ROW_DATA = "RowData";
    private ListView listTableRecordDetail;
    private DatabaseTableRecordDetailAdapter recordDetailAdapter;

    private String[] tableTitles;
    private String[] rowData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tableTitles = getIntent().getStringArrayExtra(EXTRA_TABLE_TITLES);
        rowData = getIntent().getStringArrayExtra(EXTRA_ROW_DATA);

        listTableRecordDetail = (ListView) findViewById(R.id.list_diagnose_table_record_detail);
        recordDetailAdapter = new DatabaseTableRecordDetailAdapter(this, tableTitles, rowData);
        listTableRecordDetail.setAdapter(recordDetailAdapter);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_diagnose_database_table_record_detail;
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {

    }

    @Override
    protected void initToolBar() {
        mTvTitle.setText("数据详情");
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void configViews() {

    }

}
