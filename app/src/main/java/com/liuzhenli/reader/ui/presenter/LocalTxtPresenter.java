package com.liuzhenli.reader.ui.presenter;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.FragmentActivity;

import com.liuzhenli.common.utils.AppSharedPreferenceHelper;
import com.liuzhenli.common.utils.FileUtils;
import com.liuzhenli.common.utils.L;
import com.liuzhenli.common.utils.media.ImportBookFileHelper;
import com.liuzhenli.common.base.RxPresenter;
import com.liuzhenli.reader.ReaderApplication;
import com.liuzhenli.reader.bean.LocalFileBean;
import com.liuzhenli.reader.network.Api;
import com.liuzhenli.reader.ui.contract.LocalTxtContract;
import com.liuzhenli.common.utils.Constant;
import com.liuzhenli.reader.utils.BackupRestoreUi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

    private static final String LOCAL_BOOK_PATH = Constant.BASE_PATH + "local/";

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

    @Override
    public void getLocalTxt(Context context, String path) {
        List<LocalFileBean> fileList = new ArrayList<>();
        if (TextUtils.isEmpty(path)) {
            mView.showLocalTxt(fileList);
            return;
        }

        DocumentFile documentFile = DocumentFile.fromTreeUri(context, Uri.parse(path));
        if (documentFile != null && documentFile.isDirectory()) {
            for (DocumentFile file : documentFile.listFiles()) {
                if (file.isDirectory()) {
                    getLocalTxt(context, file.getUri().toString());
                } else {
                    addFileList(fileList, file);
                }
            }
        } else if (documentFile != null && documentFile.isFile() && documentFile.getName() != null) {
            addFileList(fileList, documentFile);
        }
        mView.showLocalTxt(fileList);
    }

    private void addFileList(List<LocalFileBean> fileList, DocumentFile documentFile) {
        Context context = ReaderApplication.getInstance();
        String fileName = documentFile.getName();

        try {
            FileUtils.createDir(LOCAL_BOOK_PATH);
            File doc = FileUtils.createFile(LOCAL_BOOK_PATH + documentFile.getName());
            InputStream inputStream = context.getContentResolver().openInputStream(documentFile.getUri());
            OutputStream outputStream = new FileOutputStream(doc);
            byte[] buf = new byte[1024 * 100];
            int n = 0;
            while ((n = inputStream.read(buf)) > 0) {
                outputStream.write(buf, 0, n);
                outputStream.flush();
            }
            outputStream.close();
            inputStream.close();

            LocalFileBean localFile = new LocalFileBean();
            localFile.file = doc;
            localFile.fileType = Constant.FileAttr.FILE;
            if (fileName.endsWith(Constant.Fileuffix.TET)) {
                localFile.Fileuffix = Constant.Fileuffix.TET;
            } else if (fileName.endsWith(Constant.Fileuffix.PDF)) {
                localFile.Fileuffix = Constant.Fileuffix.PDF;
            } else if (fileName.endsWith(Constant.Fileuffix.EPUB)) {
                localFile.Fileuffix = Constant.Fileuffix.EPUB;
            }
            fileList.add(localFile);

        } catch (IOException e) {
            e.printStackTrace();
        }


    }


}
