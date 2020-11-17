package com.liuzhenli.reader.ui.fragment;

import androidx.fragment.app.FragmentActivity;

import com.liuzhenli.reader.base.BaseRVFragment;
import com.liuzhenli.reader.bean.LocalFileBean;
import com.liuzhenli.reader.network.AppComponent;
import com.liuzhenli.reader.ui.adapter.LocalTxtAdapter;
import com.liuzhenli.reader.ui.contract.LocalTxtContract;
import com.liuzhenli.reader.ui.presenter.LocalTxtPresenter;
import com.microedu.reader.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * describe: 导入书籍,文件夹
 *
 * @author Liuzhenli on 2019-12-15 10:06
 */
public class LocalTxtFragment extends BaseRVFragment<LocalTxtPresenter, LocalFileBean> implements LocalTxtContract.View {
    @Override
    public int getLayoutResId() {
        return R.layout.fragment_localtxt;
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {
        appComponent.inject(this);
    }

    @Override
    public void initData() {
    }

    @Override
    public void configViews() {
        initAdapter(LocalTxtAdapter.class, false, false);
        refreshData();
    }


    @Override
    public void showError(Exception e) {

    }

    @Override
    public void complete() {

    }

    @Override
    public void onItemClick(int position) {
        LocalFileBean data = mAdapter.getItem(position);
        data.isSelected = !data.isSelected;
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void showLocalTxt(List<LocalFileBean> fileList) {
        if (mAdapter.getCount() > 0) {
            mAdapter.clear();
        }
        mAdapter.addAll(fileList);
    }

    public List<File> getSelectedBooks() {
        ArrayList<File> bookFiles = new ArrayList<>();
        List<LocalFileBean> data = mAdapter.getRealAllData();
        for (int i = 0; i < data.size(); i++) {
            LocalFileBean item = data.get(i);
            if (item.isSelected) {
                bookFiles.add(item.file);
            }
        }
        return bookFiles;
    }

    public void notifyDataChanged() {
        mAdapter.notifyDataSetChanged();
    }

    public List<LocalFileBean> getAllBooks() {
        return mAdapter.getRealAllData();
    }

    public void refreshData() {
        if (mPresenter != null) {
            mPresenter.getLocalTxt((FragmentActivity) mContext);
        }
    }

}
