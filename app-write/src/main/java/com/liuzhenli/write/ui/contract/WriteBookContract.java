package com.liuzhenli.write.ui.contract;


import com.liuzhenli.common.base.BaseBean;
import com.liuzhenli.common.base.BaseContract;

/**
 * Description:
 *
 * @author liuzhenli 2021/1/20
 * Email: 848808263@qq.com
 */
public interface WriteBookContract {
    public interface View extends BaseContract.BaseView {
        void showLocalData(BaseBean data);
    }

    public interface Presenter<T> extends BaseContract.BasePresenter<T> {
        void getLocalData();

    }
}