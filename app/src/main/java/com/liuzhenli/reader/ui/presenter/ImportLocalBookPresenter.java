package com.liuzhenli.reader.ui.presenter;

import com.hwangjr.rxbus.RxBus;
import com.liuzhenli.common.constant.RxBusTag;
import com.liuzhenli.common.utils.RxUtil;
import com.liuzhenli.common.base.RxPresenter;
import com.liuzhenli.reader.bean.ImportBookModel;
import com.liuzhenli.reader.bean.LocalFileBean;
import com.liuzhenli.reader.ui.contract.ImportLocalBookContract;
import com.micoredu.reader.bean.LocBookShelfBean;

import java.io.File;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * describe:本地书处理逻辑
 *
 * @author Liuzhenli on 2020-01-11 14:06
 */
public class ImportLocalBookPresenter extends RxPresenter<ImportLocalBookContract.View> implements
        ImportLocalBookContract.Presenter<ImportLocalBookContract.View> {
    @Inject
    public ImportLocalBookPresenter() {
    }


    @Override
    public void addToBookShelf(List<LocalFileBean> path) {
        Observable.fromIterable(path)
                .flatMap(file -> ImportBookModel.getInstance().importBook(file))
                .compose(RxUtil::toSimpleSingle)
                .subscribe(new Observer<LocBookShelfBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addSubscribe(d);
                    }

                    @Override
                    public void onNext(LocBookShelfBean value) {
                        //有新书插入数据库,刷新页面
                        if (value.getNew()) {
                            RxBus.get().post(RxBusTag.REFRESH_BOOK_LIST, value.getBookShelfBean());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        mView.showAddResult();
                    }
                });
    }
}
