package com.micoredu.readerlib.helper;


import com.liuzhenli.common.observer.MyObserver;
import com.liuzhenli.common.utils.RxUtil;
import com.micoredu.readerlib.bean.BookChapterBean;
import com.micoredu.readerlib.bean.BookShelfBean;
import com.micoredu.readerlib.bean.SearchBookBean;
import com.micoredu.readerlib.bean.TwoDataBean;
import com.micoredu.readerlib.model.SearchBookModel;
import com.micoredu.readerlib.model.WebBookModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.reactivex.Observable;

public class ChangeSourceHelper {
    private SearchBookModel searchBookModel;
    private BookShelfBean bookShelfBean;
    private ChangeSourceListener changeSourceListener;
    private boolean finish;

    public ChangeSourceHelper() {
        SearchBookModel.OnSearchListener searchListener = new SearchBookModel.OnSearchListener() {
            @Override
            public void refreshSearchBook() {

            }

            @Override
            public void refreshFinish(Boolean value) {

            }

            @Override
            public void loadMoreFinish(Boolean value) {

            }

            @Override
            public void loadMoreSearchBook(List<SearchBookBean> value) {
                selectBook(value);
            }

            @Override
            public void searchBookError(Throwable throwable) {
                if (!finish && changeSourceListener != null) {
                    changeSourceListener.error(throwable);
                    searchBookModel.onDestroy();
                }
            }

            @Override
            public int getItemCount() {
                return 0;
            }
        };
        searchBookModel = new SearchBookModel(searchListener);
    }

    public void autoChange(BookShelfBean bookShelfBean, ChangeSourceListener changeSourceListener) {
        this.bookShelfBean = bookShelfBean;
        this.changeSourceListener = changeSourceListener;
        long searchTime = System.currentTimeMillis();
        finish = false;
        searchBookModel.setSearchTime(searchTime);
        searchBookModel.search(bookShelfBean.getBookInfoBean().getName(), searchTime, new ArrayList<>(), false);
    }

    private synchronized void selectBook(List<SearchBookBean> value) {
        if (finish) return;
        for (SearchBookBean searchBookBean : value) {
            if (Objects.equals(searchBookBean.getName(), bookShelfBean.getBookInfoBean().getName())) {
                if (Objects.equals(searchBookBean.getAuthor(), bookShelfBean.getBookInfoBean().getAuthor())) {
                    if (changeSourceListener != null) {
                        finish = true;
                        changeBookSource(searchBookBean, bookShelfBean)
                                .subscribe(new MyObserver<TwoDataBean<BookShelfBean, List<BookChapterBean>>>() {
                                    @Override
                                    public void onNext(TwoDataBean<BookShelfBean, List<BookChapterBean>> twoData) {
                                        changeSourceListener.finish(twoData.getData1(), twoData.getData2());
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        changeSourceListener.error(e);
                                    }
                                });
                    }
                    searchBookModel.onDestroy();
                    break;
                }
            } else {
                break;
            }
        }
    }

    public void stopSearch() {
        if (searchBookModel != null) {
            searchBookModel.onDestroy();
        }
    }

    public static Observable<TwoDataBean<BookShelfBean, List<BookChapterBean>>> changeBookSource(SearchBookBean searchBook, BookShelfBean oldBook) {
        BookShelfBean bookShelfBean = BookshelfHelper.getBookFromSearchBook(searchBook);
        bookShelfBean.setSerialNumber(oldBook.getSerialNumber());
        bookShelfBean.setLastChapterName(oldBook.getLastChapterName());
        bookShelfBean.setDurChapterName(oldBook.getDurChapterName());
        bookShelfBean.setDurChapter(oldBook.getDurChapter());
        bookShelfBean.setDurChapterPage(oldBook.getDurChapterPage());
        bookShelfBean.setReplaceEnable(oldBook.getReplaceEnable());
        bookShelfBean.setAllowUpdate(oldBook.getAllowUpdate());
        return WebBookModel.getInstance().getBookInfo(bookShelfBean)
                .flatMap(book -> WebBookModel.getInstance().getChapterList(book))
                .flatMap(chapterBeanList -> saveChangedBook(bookShelfBean, oldBook, chapterBeanList))
                .compose(RxUtil::toSimpleSingle);
    }

    private static Observable<TwoDataBean<BookShelfBean, List<BookChapterBean>>> saveChangedBook(BookShelfBean newBook, BookShelfBean oldBook, List<BookChapterBean> chapterBeanList) {
        return Observable.create(e -> {
            if (newBook.getChapterListSize() <= oldBook.getChapterListSize()) {
                newBook.setHasUpdate(false);
            }
            newBook.setCustomCoverPath(oldBook.getCustomCoverPath());
            newBook.setDurChapter(BookshelfHelper.getDurChapter(oldBook.getDurChapter(), oldBook.getChapterListSize(), oldBook.getDurChapterName(), chapterBeanList));
            newBook.setDurChapterName(chapterBeanList.get(newBook.getDurChapter()).getDurChapterName());
            newBook.setGroup(oldBook.getGroup());
            newBook.getBookInfoBean().setName(oldBook.getBookInfoBean().getName());
            newBook.getBookInfoBean().setAuthor(oldBook.getBookInfoBean().getAuthor());
            BookshelfHelper.removeFromBookShelf(oldBook);
            BookshelfHelper.saveBookToShelf(newBook);
            DbHelper.getDaoSession().getBookChapterBeanDao().insertOrReplaceInTx(chapterBeanList);
            e.onNext(new TwoDataBean<>(newBook, chapterBeanList));
            e.onComplete();
        });
    }

    public interface ChangeSourceListener {
        void finish(BookShelfBean bookShelfBean, List<BookChapterBean> chapterBeanList);

        void error(Throwable throwable);
    }

}
