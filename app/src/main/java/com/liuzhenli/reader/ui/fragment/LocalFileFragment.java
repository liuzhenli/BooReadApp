package com.liuzhenli.reader.ui.fragment;

import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.documentfile.provider.DocumentFile;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.liuzhenli.common.base.BaseFragment;
import com.liuzhenli.common.AppComponent;
import com.liuzhenli.common.utils.AppSharedPreferenceHelper;
import com.liuzhenli.common.utils.Constant;
import com.liuzhenli.common.utils.DeviceUtil;
import com.liuzhenli.reader.DaggerReadBookComponent;
import com.liuzhenli.reader.bean.LocalFileBean;
import com.liuzhenli.reader.ui.adapter.LocalFileAdapter;
import com.liuzhenli.reader.ui.contract.LocalFileContract;
import com.liuzhenli.reader.ui.presenter.LocalFilePresenter;
import com.liuzhenli.common.utils.ToastUtil;
import com.liuzhenli.common.utils.filepicker.adapter.PathAdapter;
import com.microedu.reader.databinding.FragmentLocalfileBinding;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * describe:导入书籍,文件夹
 *
 * @author Liuzhenli on 2019-12-15 10:06
 */
public class LocalFileFragment extends BaseFragment<LocalFilePresenter> implements LocalFileContract.View, PathAdapter.CallBack {

    private LocalFileAdapter mAdapter;
    /*** 初始文件路径  android Q 以下版本***/
    private File rootDir;
    private PathAdapter pathAdapter = new PathAdapter();
    private FragmentLocalfileBinding inflate;
    private DocumentFile rootDoc;

    @Override
    public View bindContentView(LayoutInflater inflater, ViewGroup container, boolean attachParent) {
        inflate = FragmentLocalfileBinding.inflate(inflater, container, attachParent);
        return inflate.getRoot();
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {
        DaggerReadBookComponent.builder().build().inject(this);
    }

    @Override
    public void attachView() {
        mPresenter.attachView(this);
    }


    @Override
    public void initData() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            rootDir = Environment.getExternalStorageDirectory();
        } else {
            rootDir = Environment.getRootDirectory();
        }

        //目录

        if (DeviceUtil.isLaterQ()) {
            String importLocalBookPath = AppSharedPreferenceHelper.getImportLocalBookPath();
            if (importLocalBookPath != null && importLocalBookPath.startsWith(Constant.CONTENT_PREFIX)) {
                rootDoc = DocumentFile.fromTreeUri(mContext.getApplicationContext(), Uri.parse(importLocalBookPath));
                if (rootDoc != null) {
                    mPresenter.getDirectory(rootDoc);
                }
            }
        } else {
            //读取本地书 // mnt/sdcard
            mPresenter.getDirectory(rootDir);
        }

    }

    @Override
    public void configViews() {
        inflate.rvPath.setLayoutManager(new LinearLayoutManager(mContext));
        inflate.recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new LocalFileAdapter(mContext);
        inflate.recyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(position -> {
            LocalFileBean item = mAdapter.getItem(position);
            rootDir = (File) item.file;
            if (rootDir != null && rootDir.isDirectory()) {
                refreshCurrentDirPath(rootDir.getPath());
                //当前文件的路径
            } else if (rootDir != null) {
                //已经获取到文件的路径  设置选中状态
                ToastUtil.showToast(rootDir.getAbsolutePath());
                boolean checked = item.isSelected;
                mAdapter.getRealAllData().get(position).isSelected = !checked;
            }
            mAdapter.notifyDataSetChanged();
        });
        inflate.rvPath.setLayoutManager(new LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false));
        pathAdapter.setCallBack(this);
        refreshCurrentDirPath(rootDir.getPath());
        inflate.rvPath.setAdapter(pathAdapter);
    }

    @Override
    public void showDirectory(ArrayList<LocalFileBean> data, File file) {
        mAdapter.clear();
        mAdapter.addAll(data);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void showError(Exception e) {
        dismissDialog();
    }

    @Override
    public void complete() {
        dismissDialog();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onPathClick(int position) {
        refreshCurrentDirPath(pathAdapter.getItem(position));
    }

    public void refreshCurrentDirPath(String currentPath) {
        if (File.separator.equals(currentPath)) {
            pathAdapter.updatePath(File.separator);
        } else {
            pathAdapter.updatePath(currentPath);
        }
        mPresenter.getDirectory(new File(currentPath));
    }

    public List<LocalFileBean> getSelectedBooks() {
        ArrayList<LocalFileBean> bookFiles = new ArrayList<>();
        List<LocalFileBean> data = mAdapter.getRealAllData();
        for (int i = 0; i < data.size(); i++) {
            LocalFileBean item = data.get(i);
            rootDir = (File) item.file;
            if (rootDir != null && rootDir.isFile() && (boolean) item.isSelected) {
                bookFiles.add(item);
                Logger.e(rootDir.getAbsolutePath());
            }
        }
        return bookFiles;
    }

    public void notifyDataChanged() {
        mAdapter.notifyDataSetChanged();
    }

    public void updatePath(DocumentFile documentFile) {

    }
}
