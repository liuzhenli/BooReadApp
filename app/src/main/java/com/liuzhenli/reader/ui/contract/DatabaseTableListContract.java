package com.liuzhenli.reader.ui.contract;

import com.liuzhenli.reader.base.BaseContract;

import java.util.List;

/**
 * Description:
 *
 * @author liuzhenli 2021/2/25
 * Email: 848808263@qq.com
 */
public class DatabaseTableListContract {
    public interface View extends BaseContract.BaseView {
        void showDatabase(List<String> data);
    }

    public interface Presenter<T> extends BaseContract.BasePresenter<T> {
        void loadDatabase();
    }
}
