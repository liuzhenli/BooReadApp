package com.liuzhenli.reader.ui.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.liuzhenli.common.FileHelp;
import com.liuzhenli.common.utils.ClickUtils;
import com.liuzhenli.common.utils.FileUtils;
import com.liuzhenli.reader.ReaderApplication;
import com.liuzhenli.reader.base.BaseActivity;
import com.liuzhenli.reader.network.AppComponent;
import com.liuzhenli.reader.ui.contract.SettingContract;
import com.liuzhenli.reader.ui.presenter.SettingPresenter;
import com.micoredu.readerlib.helper.BookshelfHelper;
import com.microedu.reader.R;


import butterknife.BindView;

/**
 * Description:设置页面
 *
 * @author liuzhenli 2020/10/26
 * Email: 848808263@qq.com
 */
@SuppressLint("NonConstantResourceId")
public class SettingActivity extends BaseActivity<SettingPresenter> implements SettingContract.View {


    @BindView(R.id.view_setting_file_path)
    View mVFilePath;

    @BindView(R.id.view_setting_clear_cache)
    View mViewClearCache;
    @BindView(R.id.view_setting_backup)
    View mViewBackUp;
    @BindView(R.id.tv_setting_clear_cache_size)
    TextView mTvCacheSize;
    @BindView(R.id.view_app_database)
    View mViewAppDatabase;

    public static void start(Context context) {
        Intent intent = new Intent(context, SettingActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.act_setting;
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {
        appComponent.inject(this);
    }

    @Override
    protected void initToolBar() {
        mTvTitle.setText(R.string.setting);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void configViews() {
        ClickUtils.click(mViewClearCache, o -> {
            showDialog();
            BookshelfHelper.clearCaches(true);
            FileUtils.deleteFileOrDirectory(mContext.getCacheDir());
            FileUtils.deleteFile(FileHelp.getCachePath());
            toast("缓存已清理");
            hideDialog();
            mPresenter.getCacheSize();
        });

        ClickUtils.click(mViewBackUp, o -> {
            BackupSettingActivity.start(mContext);
        });

        ClickUtils.click(mVFilePath, o -> {
            FilePathsListActivity.start(mContext);
        });
        ClickUtils.click(mViewAppDatabase, o -> {
            DatabaseTableListActivity.start(mContext);
        });
        if (ReaderApplication.isDebug) {
            mViewAppDatabase.setVisibility(View.VISIBLE);
        }
        mPresenter.getCacheSize();
    }

    @Override
    public void showCacheSize(String size) {
        mTvCacheSize.setText(size);
    }

    @Override
    public void showError(Exception e) {

    }

    @Override
    public void complete() {

    }
}
