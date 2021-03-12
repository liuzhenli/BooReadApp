package com.liuzhenli.reader.ui.presenter;

import android.util.Log;

import androidx.fragment.app.FragmentActivity;

import com.liuzhenli.common.utils.media.ImportBookFileHelper;
import com.liuzhenli.common.base.RxPresenter;
import com.liuzhenli.reader.bean.LocalFileBean;
import com.liuzhenli.reader.network.Api;
import com.liuzhenli.reader.ui.contract.LocalTxtContract;
import com.liuzhenli.common.utils.Constant;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * describe:
 *
 * @author Liuzhenli on 2019-12-15 10:29
 */
public class LocalTxtPresenter extends RxPresenter<LocalTxtContract.View> implements LocalTxtContract.Presenter<LocalTxtContract.View> {


    @Inject
    public LocalTxtPresenter() {

    }

    @Override
    public void getLocalTxt(FragmentActivity activity) {
        WeakReference<FragmentActivity> act = new WeakReference<>(activity);
        ImportBookFileHelper.getBookFile(act.get(), new ImportBookFileHelper.LoadBookCallBack(act.get(), bookList -> {
            List<LocalFileBean> fileList = new ArrayList<>();

            for (int i = 0; i < bookList.size(); i++) {
                File file = bookList.get(i);
                LocalFileBean localFile = new LocalFileBean();
                localFile.file = file;
                String fileName = file.getName();
                if (file.isDirectory()) {// 文件
                    localFile.fileType = Constant.FileAttr.ZERO;

                } else if (file.isFile()) {
                    localFile.fileType = Constant.FileAttr.FILE;
                    if (fileName.endsWith(Constant.Fileuffix.TET)) {
                        localFile.Fileuffix = Constant.Fileuffix.TET;
                    } else if (fileName.endsWith(Constant.Fileuffix.PDF)) {
                        localFile.Fileuffix = Constant.Fileuffix.PDF;
                    } else if (fileName.endsWith(Constant.Fileuffix.EPUB)) {
                        localFile.Fileuffix = Constant.Fileuffix.EPUB;
                    }
                }
                if (file.listFiles() != null) {// 文件夹非空
                    localFile.fileCount = "(" + file.listFiles().length + ")";
                } else if (file.isFile()) {// 是文件就不显示
                    localFile.fileCount = "(" + file.length() + ")";
                } else {// 文件夹为空
                    localFile.fileCount = "(0)";
                }
                fileList.add(localFile);
            }
            mView.showLocalTxt(fileList);
        }));
    }
}
