package com.liuzhenli.reader.ui.activity

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import butterknife.BindView
import com.liuzhenli.reader.base.BaseActivity
import com.liuzhenli.reader.network.AppComponent
import com.liuzhenli.reader.ui.adapter.DatabaseTableListAdapter
import com.liuzhenli.reader.ui.contract.DatabaseTableListContract
import com.liuzhenli.reader.ui.presenter.DatabaseTableListPresenter
import com.microedu.reader.R
import nl.siegmann.epublib.util.StringUtil
import java.util.*

/**
 * 数据库中包含的数据表
 *
 * @author liuzhenli
 * @date 2021.02.18
 */
class DatabaseTableListActivity : BaseActivity<DatabaseTableListPresenter?>(), DatabaseTableListContract.View {

    var databaseTablesList: ListView? = null
    private var tablesAdapter: DatabaseTableListAdapter? = null
    private val tableList: MutableList<String> = ArrayList()

    override fun getLayoutId(): Int {
        return R.layout.act_databasetablelist
    }

    override fun setupActivityComponent(appComponent: AppComponent) {
        appComponent.inject(this)
    }

    override fun initToolBar() {
        mTvTitle.text = "数据表"
    }

    override fun initData() {}

    override fun configViews() {
        databaseTablesList = findViewById(R.id.list_diagnose_database_tables)
        tablesAdapter = DatabaseTableListAdapter(this)
        databaseTablesList!!.adapter = tablesAdapter
        databaseTablesList!!.onItemClickListener = AdapterView.OnItemClickListener { parent: AdapterView<*>?, view: View?, position: Int, id: Long ->
            val tableName = tableList[position]
            if (StringUtil.isNotBlank(tableName)) {
                val intent = Intent(this@DatabaseTableListActivity, DatabaseTableRecordsActivity::class.java)
                intent.putExtra(DatabaseTableRecordsActivity.EXTRA_TABLE_NAME, tableName)
                startActivity(intent)
            }
        }
        mPresenter!!.loadDatabase()
    }

    override fun showDatabase(data: List<String>) {
        tableList.addAll(data)
        tablesAdapter!!.setDataset(tableList)
    }

    override fun showError(e: Exception) {}
    override fun complete() {}

    companion object {
        @JvmStatic
        fun start(context: Context) {
            val intent = Intent(context, DatabaseTableListActivity::class.java)
            context.startActivity(intent)
        }
    }
}