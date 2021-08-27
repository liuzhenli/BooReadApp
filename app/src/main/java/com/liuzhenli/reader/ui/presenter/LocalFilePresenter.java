package com.liuzhenli.reader.ui.presenter;

import com.liuzhenli.common.utils.DocumentUtil;
import com.liuzhenli.common.utils.RxUtil;
import com.liuzhenli.common.base.RxPresenter;
import com.liuzhenli.common.observer.SampleProgressObserver;
import com.liuzhenli.reader.ReaderApplication;
import com.liuzhenli.reader.bean.LocalFileBean;
import com.liuzhenli.reader.ui.contract.LocalFileContract;
import com.liuzhenli.common.utils.Constant;
import com.liuzhenli.common.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.observers.DisposableObserver;

import static com.liuzhenli.common.utils.Constant.FileSuffix.EPUB;
import static com.liuzhenli.common.utils.Constant.FileSuffix.PDF;
import static com.liuzhenli.common.utils.Constant.FileSuffix.TXT;

import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.util.SparseArray;

import androidx.annotation.RequiresApi;
import androidx.documentfile.provider.DocumentFile;

/**
 * describe:
 *
 * @author Liuzhenli on 2019-12-15 10:29
 */
public class LocalFilePresenter extends RxPresenter<LocalFileContract.View> implements LocalFileContract.Presenter<LocalFileContract.View> {


    @Inject
    public LocalFilePresenter() {

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void getDirectory(DocumentFile file) {

        DisposableObserver subscribe = RxUtil.subscribe(Observable
                .create((ObservableOnSubscribe<ArrayList<LocalFileBean>>) subscriber -> {
                    //子线程
                    try {
                        DocumentFile[] documentFiles = file.listFiles();
                        Uri childUri = DocumentsContract.buildChildDocumentsUriUsingTree(file.getUri(), DocumentsContract.getDocumentId(file.getUri()));
                        String[] protection = {DocumentsContract.Document.COLUMN_DOCUMENT_ID,
                                DocumentsContract.Document.COLUMN_DISPLAY_NAME,
                                DocumentsContract.Document.COLUMN_LAST_MODIFIED,
                                DocumentsContract.Document.COLUMN_SIZE,
                                DocumentsContract.Document.COLUMN_MIME_TYPE};

                        Cursor c = ReaderApplication.getInstance().getContentResolver().query(childUri, protection, null, null, DocumentsContract.Document.COLUMN_DISPLAY_NAME);
                        int columnIndex = c.getColumnIndex(DocumentsContract.Document.COLUMN_DOCUMENT_ID);
                        int columnName = c.getColumnIndex(DocumentsContract.Document.COLUMN_DISPLAY_NAME);
                        int columnSizeIndex = c.getColumnIndex(DocumentsContract.Document.COLUMN_SIZE);
                        int columnTypeIndex = c.getColumnIndex(DocumentsContract.Document.COLUMN_MIME_TYPE);
                        int columnLastModifiedIndex = c.getColumnIndex(DocumentsContract.Document.COLUMN_LAST_MODIFIED);

                        if (c.moveToFirst()) {

                        }
                        ArrayList<LocalFileBean> data = new ArrayList<>();

                        File[] files = FileUtils.read(file);
                        int size = 0;
                        if (files != null) {
                            size = files.length;
                        }
                        try {
                            for (int i = 0; i < size; i++) {
                                LocalFileBean localFileBean = new LocalFileBean();
                                String fileName = files[i].getName();
                                // 文件
                                if (files[i].isDirectory()) {
                                    localFileBean.fileType = Constant.FileSuffix.NONE;
                                } else if (files[i].isFile()) {
                                    if (fileName.endsWith(TXT)) {
                                        localFileBean.fileType = TXT;
                                    } else if (fileName.endsWith(PDF)) {
                                        localFileBean.fileType = PDF;
                                    } else if (fileName.endsWith(".epub")) {
                                        localFileBean.fileType = EPUB;
                                    }
                                }
                                localFileBean.file = files[i];
                                localFileBean.fileName = fileName;
                                localFileBean.isSelected = false;
                                if (files[i].listFiles() != null) {// 文件夹非空
                                    localFileBean.fileCount = "(" + files[i].listFiles().length + ")";
                                } else if (files[i].isFile()) {// 是文件就不显示
                                    localFileBean.fileCount = files[i].length() + "";
                                } else {// 文件夹为空
                                    localFileBean.fileCount = "(0)";
                                }
                                data.add(localFileBean);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        Collections.sort(data, (lhs, rhs) -> ((lhs.fileName).toLowerCase()).compareTo((rhs.fileName).toLowerCase()));

                        List<LocalFileBean> temp = new ArrayList<>();
                        Iterator<LocalFileBean> list = data.iterator();
                        while (list.hasNext()) {
                            LocalFileBean map = list.next();
                            String fileName = map.fileName;
                            if (fileName != null && (TXT.endsWith(fileName) || PDF.endsWith(fileName) || Constant.FileSuffix.EPUB.endsWith(fileName))) {
                                temp.add(map);
                                list.remove();
                            }
                        }
                        data.addAll(temp);
                        temp = null;
                        subscriber.onNext(data);
                        subscriber.onComplete();
                    } catch (Exception e) {
                        subscriber.onError(e);
                    }
                }), new SampleProgressObserver<ArrayList<LocalFileBean>>(mView) {
            @Override
            public void onNext(ArrayList<LocalFileBean> data) {
                if (mView != null) {
                    mView.showDirectory(data, file);
                }
            }


        });
        addSubscribe(subscribe);
    }

    public void getDirectory(File file) {
        DisposableObserver subscribe = RxUtil.subscribe(Observable
                .create((ObservableOnSubscribe<ArrayList<LocalFileBean>>) subscriber -> {
                    //子线程
                    try {
                        ArrayList<LocalFileBean> data = new ArrayList<>();
                        File[] files = FileUtils.read(file);
                        int size = 0;
                        if (files != null) {
                            size = files.length;
                        }
                        try {
                            for (int i = 0; i < size; i++) {
                                LocalFileBean localFileBean = new LocalFileBean();
                                String fileName = files[i].getName();
                                // 文件
                                if (files[i].isDirectory()) {
                                    localFileBean.fileType = Constant.FileSuffix.NONE;
                                } else if (files[i].isFile()) {
                                    if (fileName.endsWith(TXT)) {
                                        localFileBean.fileType = TXT;
                                    } else if (fileName.endsWith(PDF)) {
                                        localFileBean.fileType = PDF;
                                    } else if (fileName.endsWith(".epub")) {
                                        localFileBean.fileType = EPUB;
                                    }
                                }
                                localFileBean.file = files[i];
                                localFileBean.fileName = fileName;
                                localFileBean.isSelected = false;
                                if (files[i].listFiles() != null) {// 文件夹非空
                                    localFileBean.fileCount = "(" + files[i].listFiles().length + ")";
                                } else if (files[i].isFile()) {// 是文件就不显示
                                    localFileBean.fileCount = files[i].length() + "";
                                } else {// 文件夹为空
                                    localFileBean.fileCount = "(0)";
                                }
                                data.add(localFileBean);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        Collections.sort(data, (lhs, rhs) -> ((lhs.fileName).toLowerCase()).compareTo((rhs.fileName).toLowerCase()));

                        List<LocalFileBean> temp = new ArrayList<>();
                        Iterator<LocalFileBean> list = data.iterator();
                        while (list.hasNext()) {
                            LocalFileBean map = list.next();
                            String fileName = map.fileName;
                            if (fileName != null && (TXT.endsWith(fileName) || PDF.endsWith(fileName) || Constant.FileSuffix.EPUB.endsWith(fileName))) {
                                temp.add(map);
                                list.remove();
                            }
                        }
                        data.addAll(temp);
                        temp = null;
                        subscriber.onNext(data);
                        subscriber.onComplete();
                    } catch (Exception e) {
                        subscriber.onError(e);
                    }
                }), new SampleProgressObserver<ArrayList<LocalFileBean>>(mView) {
            @Override
            public void onNext(ArrayList<LocalFileBean> data) {
                if (mView != null) {
                    mView.showDirectory(data, file);
                }
            }


        });
        addSubscribe(subscribe);
    }
}
