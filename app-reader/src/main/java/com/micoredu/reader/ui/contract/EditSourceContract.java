package com.micoredu.reader.ui.contract;

import com.liuzhenli.common.base.BaseContract;
import com.micoredu.reader.bean.BookSource;

/**
 * Description:
 *
 * @author liuzhenli 2020/11/16
 * Email: 848808263@qq.com
 */
public class EditSourceContract {
    public interface View extends BaseContract.BaseView {
        void showSaveBookResult();

        void showBookSource(BookSource data);
    }

    public interface Presenter<T> extends BaseContract.BasePresenter<T> {
        void saveBookSource(BookSource data);

        void getBookSourceFromString(String str);
    }
}
