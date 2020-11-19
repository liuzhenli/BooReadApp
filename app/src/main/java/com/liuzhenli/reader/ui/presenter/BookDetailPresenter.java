package com.liuzhenli.reader.ui.presenter;

import com.hwangjr.rxbus.RxBus;
import com.liuzhenli.common.constant.RxBusTag;
import com.liuzhenli.common.utils.RxUtil;
import com.liuzhenli.reader.base.RxPresenter;
import com.liuzhenli.reader.network.Api;
import com.liuzhenli.reader.observer.SampleProgressObserver;
import com.liuzhenli.reader.ui.contract.BookCatalogContract;
import com.liuzhenli.reader.ui.contract.BookDetailContract;
import com.micoredu.readerlib.bean.BookChapterBean;
import com.micoredu.readerlib.bean.BookShelfBean;
import com.micoredu.readerlib.helper.BookshelfHelper;
import com.micoredu.readerlib.helper.DbHelper;
import com.micoredu.readerlib.model.WebBookModel;
import com.microedu.reader.R;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;

/**
 * describe:
 *
 * @author Liuzhenli on 2019-12-14 11:00
 */
public class BookDetailPresenter extends RxPresenter<BookDetailContract.View> implements BookDetailContract.Presenter<BookDetailContract.View> {

    private Api mApi;

    @Inject
    public BookDetailPresenter(Api api) {
        this.mApi = api;
    }

    @Override
    public void getBookInfo(BookShelfBean bookShelfBean, boolean isInBookShelf) {

        Observable<List<BookChapterBean>> listObservable = WebBookModel.getInstance().getBookInfo(bookShelfBean)
                .flatMap(bookShelfBean1 -> WebBookModel.getInstance().getChapterList(bookShelfBean1))
                .flatMap((Function<List<BookChapterBean>, ObservableSource<List<BookChapterBean>>>)
                        bookChapterBeans -> saveBookToShelfO(bookShelfBean, bookChapterBeans, isInBookShelf));

        DisposableObserver subscribe = RxUtil.subscribe(listObservable, new SampleProgressObserver<List<BookChapterBean>>() {
            @Override
            public void onNext(List<BookChapterBean> bookChapterBeans) {
                bookShelfBean.setChapterListSize(bookChapterBeans.size());
                mView.showBookInfo(bookShelfBean.getBookInfoBean(), bookChapterBeans);
            }
        });
        addSubscribe(subscribe);
    }

    @Override
    public void getBookSource(BookShelfBean bookShelfBean) {

    }

    /**
     * 保存数据
     */
    private Observable<List<BookChapterBean>> saveBookToShelfO(BookShelfBean bookShelfBean, List<BookChapterBean> chapterBeans, boolean isInBookShelf) {
        return Observable.create(e -> {
            if (isInBookShelf) {
                BookshelfHelper.saveBookToShelf(bookShelfBean);
                if (!chapterBeans.isEmpty()) {
                    BookshelfHelper.delChapterList(bookShelfBean.getNoteUrl());
                    DbHelper.getDaoSession().getBookChapterBeanDao().insertOrReplaceInTx(chapterBeans);
                }
                RxBus.get().post(RxBusTag.HAD_ADD_BOOK, bookShelfBean);
            }
            e.onNext(chapterBeans);
            e.onComplete();
        });
    }
}