package com.liuzhenli.reader.ui.activity

import android.os.Bundle
import android.widget.ListView
import com.liuzhenli.reader.base.BaseActivity
import com.liuzhenli.reader.base.BaseContract.BasePresenter
import com.liuzhenli.reader.network.AppComponent
import com.liuzhenli.reader.ui.adapter.DatabaseTableRecordDetailAdapter
import com.microedu.reader.R

/**
 * 数据表，行数据详情 (由于内容长度过长，需要查看完整内容)
 *
 * @author liuzhenli
 * @date 2021.02.18
 */
class DatabaseTableRecordDetailActivity : BaseActivity<BasePresenter<*>>() {

    private var listTableRecordDetail: ListView? = null
    private var recordDetailAdapter: DatabaseTableRecordDetailAdapter? = null
    private var tableTitles: Array<String>? = null
    private var rowData: Array<String>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tableTitles = intent.getStringArrayExtra(EXTRA_TABLE_TITLES)
        rowData = intent.getStringArrayExtra(EXTRA_ROW_DATA)
        recordDetailAdapter = DatabaseTableRecordDetailAdapter(this, tableTitles, rowData)
        listTableRecordDetail!!.adapter = recordDetailAdapter
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_diagnose_database_table_record_detail
    }

    override fun setupActivityComponent(appComponent: AppComponent) {}
    override fun initToolBar() {
        mTvTitle.text = "数据详情"
    }

    override fun initData() {}
    override fun configViews() {
        listTableRecordDetail = findViewById(R.id.list_diagnose_table_record_detail)
    }

    companion object {
        const val EXTRA_TABLE_TITLES = "TableTitles"
        const val EXTRA_ROW_DATA = "RowData"
    }
}