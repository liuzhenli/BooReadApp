package com.liuzhenli.reader.ui.contract;

import com.liuzhenli.reader.base.BaseContract;
import com.micoredu.readerlib.bean.BookChapterBean;
import com.micoredu.readerlib.bean.BookInfoBean;
import com.micoredu.readerlib.bean.BookShelfBean;
import com.micoredu.readerlib.content.BookChapterList;

import java.io.File;
import java.util.List;

/**
 * describe:
 *
 * @author Liuzhenli on 2019-11-10 12:30
 */
public class ReadContract {
    public interface View extends BaseContract.BaseView {
        String getNoteUrl();

        //判断是否在书架
        boolean isInBookShelf();

        /**
         * 显示书的信息
         *
         * @param bookInfo 书信息
         */
        void showBookInfo(BookShelfBean bookInfo);

        void showFontFile(File[] files);

        BookShelfBean getBookShelf();

        void showChangeBookSourceResult(BookShelfBean book);

    }


    public interface Presenter<T> extends BaseContract.BasePresenter<T> {
        /***获取书的信息*/
        void getBookInfo(String url);

        void saveProgress(BookShelfBean bookShelf);

        void getFontFile();

        void changeBookSource();
    }


}
