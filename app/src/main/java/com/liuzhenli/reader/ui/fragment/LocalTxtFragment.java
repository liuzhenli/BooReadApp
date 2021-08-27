package com.liuzhenli.reader.ui.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.FragmentActivity;

import com.liuzhenli.common.base.BaseRVFragment;
import com.liuzhenli.common.AppComponent;
import com.liuzhenli.common.utils.AppSharedPreferenceHelper;
import com.liuzhenli.common.utils.DeviceUtil;
import com.liuzhenli.reader.DaggerReadBookComponent;
import com.liuzhenli.reader.bean.LocalFileBean;
import com.liuzhenli.reader.ui.adapter.LocalTxtAdapter;
import com.liuzhenli.reader.ui.contract.LocalTxtContract;
import com.liuzhenli.reader.ui.presenter.LocalTxtPresenter;
import com.microedu.reader.databinding.FragmentLocaltxtBinding;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * describe: 导入书籍,文件
 *
 * @author Liuzhenli on 2019-12-15 10:06
 */
public class LocalTxtFragment extends BaseRVFragment<LocalTxtPresenter, LocalFileBean> implements LocalTxtContract.View {

    private FragmentLocaltxtBinding inflate;

    @Override
    public View bindContentView(LayoutInflater inflater, ViewGroup container, boolean attachParent) {
        inflate = FragmentLocaltxtBinding.inflate(inflater, container, attachParent);
        return inflate.getRoot();
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {
        DaggerReadBookComponent.builder().build().inject(this);
    }

    @Override
    public void initData() {
    }

    @Override
    public void configViews() {
        initAdapter(LocalTxtAdapter.class, false, false, true);
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

    public List<LocalFileBean> getSelectedBooks() {
        ArrayList<LocalFileBean> bookFiles = new ArrayList<>();
        List<LocalFileBean> data = mAdapter.getRealAllData();
        for (int i = 0; i < data.size(); i++) {
            LocalFileBean item = data.get(i);
            if (item.isSelected) {
                bookFiles.add(item);
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
            //
            if (DeviceUtil.isLaterQ()) {
                mPresenter.getLocalTxt(this.getApplicationContext(), AppSharedPreferenceHelper.getImportLocalBookPath());
            } else {
                mPresenter.getLocalTxt((FragmentActivity) mContext);
            }
        }
    }

}
