package com.liuzhenli.reader.ui.contract;

import androidx.documentfile.provider.DocumentFile;

import com.liuzhenli.common.base.BaseContract;
import com.liuzhenli.common.utils.filepicker.entity.FileItem;

import java.io.File;
import java.util.ArrayList;

/**
 * describe:
 *
 * @author Liuzhenli on 2019-12-15 10:24
 */
public class LocalFileContract {
    public interface View extends BaseContract.BaseView {
        void showDirectory(ArrayList<FileItem> data, File file);
    }

    public interface Presenter<T> extends BaseContract.BasePresenter<T> {
        void getDirectory(File file);
        void getDirectory(DocumentFile file);
    }
}
