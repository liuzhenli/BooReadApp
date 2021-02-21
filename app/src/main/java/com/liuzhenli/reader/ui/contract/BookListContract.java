package com.liuzhenli.reader.ui.contract;

import com.liuzhenli.reader.base.BaseContract;
import com.micoredu.readerlib.bean.SearchBookBean;

import java.util.List;

/**
 * Description:
 *
 * @author liuzhenli 2020/10/9
 * Email: 848808263@qq.com
 */
public class BookListContract {
    public interface View extends BaseContract.BaseView {
        /**
         * 显示书籍列表
         */
        void showBookList(List<SearchBookBean> data);
    }

    public interface Presenter<T> extends BaseContract.BasePresenter<T> {
        /**
         * 获取书列表
         *
         * @param url  url
         * @param page page
         * @param tag  tag
         */
        void getBookList(String url, int page, String tag);
    }
}
