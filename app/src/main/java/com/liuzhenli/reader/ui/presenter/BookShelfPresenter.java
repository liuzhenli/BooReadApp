package com.liuzhenli.reader.ui.presenter;

import com.liuzhenli.common.BaseApplication;
import com.liuzhenli.common.utils.NetworkUtils;
import com.liuzhenli.reader.base.RxPresenter;
import com.liuzhenli.reader.observer.SampleProgressObserver;
import com.liuzhenli.reader.ui.contract.BookShelfContract;
import com.micoredu.readerlib.bean.BookShelfBean;
import com.micoredu.readerlib.helper.BookshelfHelper;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

/**
 * describe:
 *
 * @author Liuzhenli on 2020-01-11 15:26
 */
public class BookShelfPresenter extends RxPresenter<BookShelfContract.View> implements BookShelfContract.Presenter<BookShelfContract.View> {
    private int threadsNum = 6;
    private int refreshIndex;
    private List<BookShelfBean> bookShelfBeans;
    private int group;
    private boolean hasUpdate = false;
    private List<String> errBooks = new ArrayList<>();
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Inject
    public BookShelfPresenter() {
    }

    @Override
    public void queryBooks(Boolean needRefresh, int group) {
        this.group = group;
        if (needRefresh) {
            hasUpdate = false;
            errBooks.clear();
        }
        Observable.create((ObservableOnSubscribe<List<BookShelfBean>>) e -> {
            List<BookShelfBean> bookShelfList;
            if (group == 0) {
                //获取数据库中,书架的所有书籍
                bookShelfList = BookshelfHelper.getAllBook();
            } else {
                //按分类获取
                bookShelfList = BookshelfHelper.getBooksByGroup(group - 1);
            }
            e.onNext(bookShelfList == null ? new ArrayList<>() : bookShelfList);
            e.onComplete();
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SampleProgressObserver<List<BookShelfBean>>() {
                    @Override
                    public void onNext(List<BookShelfBean> value) {
                        if (null != value) {
                            bookShelfBeans = value;
                            mView.showBooks(bookShelfBeans);
                            if (needRefresh && NetworkUtils.isNetWorkAvailable(BaseApplication.getInstance())) {
                                //startRefreshBook();
                            }
                        }
                    }
                });

    }

    @Override
    public void removeFromBookShelf(BookShelfBean bookShelfBean) {
        BookshelfHelper.removeFromBookShelf(bookShelfBean);
        mView.onBookRemoved(bookShelfBean);
    }
}
