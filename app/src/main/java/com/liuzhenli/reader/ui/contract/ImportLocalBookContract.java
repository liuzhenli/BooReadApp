package com.liuzhenli.reader.ui.contract;

import com.liuzhenli.common.base.BaseContract;
import com.liuzhenli.reader.bean.LocalFileBean;

import java.io.File;
import java.util.List;

/**
 * describe:
 *
 * @author Liuzhenli on 2020-01-11 13:54
 */
public class ImportLocalBookContract {
    public interface View extends BaseContract.BaseView {
        void showAddResult();
    }

    public interface Presenter<T> extends BaseContract.BasePresenter<T> {

        /***将本地书的路径保存数据库**/
        void addToBookShelf(List<LocalFileBean> books);
    }
}
