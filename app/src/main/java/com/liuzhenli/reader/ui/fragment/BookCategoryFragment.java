package com.liuzhenli.reader.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.liuzhenli.common.BitIntentDataManager;
import com.liuzhenli.common.constant.AppConstant;
import com.liuzhenli.reader.base.BaseActivity;
import com.liuzhenli.reader.base.BaseRVFragment;
import com.liuzhenli.reader.network.AppComponent;
import com.liuzhenli.reader.ui.activity.BookDetailActivity;
import com.liuzhenli.reader.ui.adapter.BookListAdapter;
import com.liuzhenli.reader.ui.contract.BookListContract;
import com.liuzhenli.reader.ui.presenter.BookListPresenter;
import com.liuzhenli.reader.utils.DataDiffUtil;
import com.micoredu.readerlib.bean.SearchBookBean;
import com.microedu.reader.R;

import java.util.ArrayList;
import java.util.List;

import static com.liuzhenli.common.BitIntentDataManager.DATA_KEY;

/**
 * Description:某个分类下的书列表
 *
 * @author liuzhenli 2020/10/20
 * Email: 848808263@qq.com
 */
public class BookCategoryFragment extends BaseRVFragment<BookListPresenter, SearchBookBean> implements BookListContract.View {

    public static final String URL = "url";
    public static final String BOOK_SOURCE_NAME = "source_name";
    public static final String TAG = "tag";


    private String mUrl;
    private String mTag;
    private String mBookSourceName;

    public static BookCategoryFragment getInstance(String url, String tag, String bookSourceName) {
        BookCategoryFragment instance = new BookCategoryFragment();
        Bundle bundle = new Bundle();
        bundle.putString(URL, url);
        bundle.putString(TAG, tag);
        bundle.putString(BOOK_SOURCE_NAME, bookSourceName);
        instance.setArguments(bundle);
        return instance;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_booklist;
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {
        appComponent.inject(this);
    }

    @Override
    public void initData() {
        if (getArguments() != null) {
            mUrl = getArguments().getString(URL);
            mTag = getArguments().getString(TAG);
            mBookSourceName = getArguments().getString(BOOK_SOURCE_NAME);
        }
    }

    @Override
    public void configViews() {
        initAdapter(BookListAdapter.class, true, true, true);
        ((BookListAdapter) mAdapter).setBookSourceName(mBookSourceName);
    }

    @Override
    public void onItemClick(int position) {
        SearchBookBean item = mAdapter.getItem(position);
        String dataKey = String.valueOf(System.currentTimeMillis());
        Intent intent = new Intent(mContext, BookDetailActivity.class);
        intent.putExtra(BookDetailActivity.OPEN_FROM, AppConstant.BookOpenFrom.OPEN_FROM_SEARCH);
        intent.putExtra(DATA_KEY, dataKey);
        BitIntentDataManager.getInstance().putData(dataKey, item);
        startActivity(intent);
    }

    @Override
    public void onLoadMore() {
        super.onLoadMore();
        mPage++;
        mPresenter.getBookList(mUrl, mPage, mTag);
    }

    @Override
    public void showError(Exception e) {
        if (e.getMessage().contains("没有下一页")) {
            mAdapter.addAll(new ArrayList<>());
        } else if (mAdapter.getCount() == 0) {
            mRecyclerView.showError();
        } else {
            loadError();
        }
        hideDialog();
    }

    @Override
    public void complete() {
        hideDialog();
        mRecyclerView.setRefreshing(false);
    }

    @Override
    public void onRefresh() {
        super.onRefresh();
        mPage = 1;
        mPresenter.getBookList(mUrl, mPage, mTag);
    }

    @Override
    public void showBookList(List<SearchBookBean> data) {
        hideDialog();
        mRecyclerView.setRefreshing(false);
        if (mAdapter.getCount() != 0 && mPage == 1) {
            DataDiffUtil.diffResult(mAdapter, data, new DataDiffUtil.ItemSameCallBack<SearchBookBean>() {
                @Override
                public boolean isItemSame(SearchBookBean oldItem, SearchBookBean newItem) {
                    return oldItem != null && newItem != null && TextUtils.equals(oldItem.getName(), newItem.getName());
                }

                @Override
                public boolean isContentSame(SearchBookBean oldItem, SearchBookBean newItem) {
                    return TextUtils.equals(oldItem.getCoverUrl(), newItem.getCoverUrl());
                }
            });
        } else {
            mAdapter.addAll(data);
        }
    }

    @Override
    protected void onFragmentVisibleChange(boolean isVisible) {
        super.onFragmentVisibleChange(isVisible);
        if (isVisible) {
            if (mAdapter.getCount() == 0) {
                onRefresh();
            }
        }
    }
}
