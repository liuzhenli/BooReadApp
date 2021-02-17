package com.liuzhenli.reader.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.hwangjr.rxbus.RxBus;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.liuzhenli.common.BitIntentDataManager;
import com.liuzhenli.common.constant.RxBusTag;
import com.liuzhenli.common.utils.ClickUtils;
import com.liuzhenli.reader.base.BaseRVFragment;
import com.liuzhenli.reader.network.AppComponent;
import com.liuzhenli.reader.ui.activity.ReaderActivity;
import com.liuzhenli.reader.ui.adapter.BookShelfAdapter;
import com.liuzhenli.reader.ui.contract.BookShelfContract;
import com.liuzhenli.reader.ui.presenter.BookShelfPresenter;
import com.liuzhenli.common.utils.Constant;
import com.liuzhenli.reader.utils.DataDiffUtil;
import com.liuzhenli.reader.utils.ToastUtil;
import com.liuzhenli.reader.utils.image.ImageUtil;
import com.micoredu.readerlib.bean.BookShelfBean;
import com.micoredu.readerlib.helper.BookshelfHelper;
import com.microedu.reader.R;

import java.util.List;

import butterknife.BindView;

/**
 * describe:书架
 *
 * @author Liuzhenli on 2019-11-09 22:28
 */
public class BookShelfFragment extends BaseRVFragment<BookShelfPresenter, BookShelfBean> implements BookShelfContract.View {

    /***是否启动第一次访问书架  如果是,需要检查更新*/
    private boolean mFirstRequest = true;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_bookshelf;
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {
        appComponent.inject(this);
    }

    @Override
    public void initData() {

    }

    @Override
    public void configViews() {
        initAdapter(BookShelfAdapter.class, false, false, true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Subscribe(thread = EventThread.MAIN_THREAD, tags = {@Tag(RxBusTag.REFRESH_BOOK_LIST), @Tag(RxBusTag.UPDATE_BOOK_PROGRESS)})
    public void onAddOrRemoveBook(BookShelfBean book) {
        mPresenter.queryBooks(false, 0);
    }

    @Override
    public void onItemClick(int position) {
        BookShelfBean bookShelfBean = mAdapter.getRealAllData().get(position);
        bookShelfBean.setFinalDate(System.currentTimeMillis());
        BookshelfHelper.saveBookToShelf(bookShelfBean);
        Intent intent = new Intent(getContext(), ReaderActivity.class);
        intent.putExtra("openFrom", Constant.BookOpenFrom.FROM_BOOKSHELF);

        String bookKey = String.valueOf(System.currentTimeMillis());
        intent.putExtra(BitIntentDataManager.DATA_KEY, bookKey);
        BitIntentDataManager.getInstance().putData(bookKey, bookShelfBean.clone());

        mContext.startActivity(intent);
    }


    @Override
    public void showBooks(List<BookShelfBean> bookShelfBeanList) {
        if (mAdapter.getCount() != 0) {
            DataDiffUtil.diffResult(mAdapter, bookShelfBeanList, new DataDiffUtil.ItemSameCallBack<BookShelfBean>() {
                @Override
                public boolean isItemSame(BookShelfBean oldItem, BookShelfBean newItem) {

                    return oldItem.getBookInfoBean() != null && oldItem.getBookInfoBean().getName() != null
                            && newItem.getBookInfoBean() != null
                            && oldItem.getBookInfoBean().getName().equals(newItem.getBookInfoBean().getName());
                }

                @Override
                public boolean isContentSame(BookShelfBean oldItem, BookShelfBean newItem) {
                    return false;
                }
            });
        } else {
            mAdapter.addAll(bookShelfBeanList);
        }
    }

    @Override
    public void onBookRemoved(BookShelfBean bookShelfBean) {
        mAdapter.getRealAllData().remove(bookShelfBean);
        mAdapter.notifyDataSetChanged();
        ToastUtil.showToast("已从书架中移除");
    }

    @Override
    public void setRefreshingBook(BookShelfBean data) {
        for (int i = 0; mAdapter != null && mAdapter.getRealAllData() != null
                && mAdapter.getRealAllData().size() > 0
                && i < mAdapter.getRealAllData().size(); i++) {
            if (TextUtils.equals(mAdapter.getRealAllData().get(i).getNoteUrl(), data.getNoteUrl())) {
                mAdapter.getRealAllData().get(i).setLoading(data.isLoading());
                mAdapter.notifyItemChanged(i);
            }
        }
    }

    @Override
    public void onRefresh() {
        super.onRefresh();
        mPresenter.queryBooks(mFirstRequest, 0);
        mFirstRequest = false;
    }

    @Override
    public void showError(Exception e) {
        mRecyclerView.setRefreshing(false);
    }

    @Override
    public void complete() {
        mRecyclerView.setRefreshing(false);
    }

    @Override
    public void onLoadMore() {
        super.onLoadMore();
    }

    @Override
    public boolean onItemLongClick(int position) {

        BookShelfBean bookShelfBean = mAdapter.getRealAllData().get(position);

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(mContext);
        bottomSheetDialog.setContentView(R.layout.layout_bookshelf_bottom_sheet);
        bottomSheetDialog.findViewById(R.id.tv_delete_book).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.removeFromBookShelf(bookShelfBean);
                bottomSheetDialog.dismiss();
            }
        });
        bottomSheetDialog.findViewById(R.id.tv_continue_read).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClick(position);
                bottomSheetDialog.dismiss();
            }
        });
        TextView tvBookName = bottomSheetDialog.findViewById(R.id.tv_book_name);
        TextView tvBookAuthor = bottomSheetDialog.findViewById(R.id.tv_book_author);
        TextView tvBookSource = bottomSheetDialog.findViewById(R.id.tv_book_source);
        TextView tvProgress = bottomSheetDialog.findViewById(R.id.tv_book_read_progress);
        TextView tvDes = bottomSheetDialog.findViewById(R.id.tv_book_des);
        ImageView mIvCover = bottomSheetDialog.findViewById(R.id.iv_book_cover);
        CheckBox mVAllowUpdate = bottomSheetDialog.findViewById(R.id.cb_allow_update);

        ClickUtils.click(mVAllowUpdate, o -> {
            bookShelfBean.setAllowUpdate(!bookShelfBean.getAllowUpdate());
            mVAllowUpdate.setChecked(bookShelfBean.getAllowUpdate());
            mPresenter.updateBookInfo(bookShelfBean);
        });
        //本地书,不显示允许更新
        if (TextUtils.equals(bookShelfBean.getTag(), BookShelfBean.LOCAL_TAG)) {
            mVAllowUpdate.setVisibility(View.GONE);
        } else {
            mVAllowUpdate.setVisibility(View.VISIBLE);
        }
        ImageUtil.setImage(mContext, bookShelfBean.getBookInfoBean().getCoverUrl(), R.drawable.ic_book_cover_placeholder, R.drawable.book_cover, mIvCover);
        String author = TextUtils.isEmpty(bookShelfBean.getBookInfoBean().getAuthor()) ? "未知" : bookShelfBean.getBookInfoBean().getAuthor();
        mVAllowUpdate.setChecked(bookShelfBean.getAllowUpdate());
        tvBookName.setText(bookShelfBean.getBookInfoBean().getName());
        tvBookAuthor.setText(String.format("作者:%s", author));
        tvBookSource.setText(String.format("书源:%s", bookShelfBean.getBookInfoBean().getOrigin()));
        if (bookShelfBean.getChapterListSize() == 0) {
            tvProgress.setText("已阅读:0%");
        } else {
            tvProgress.setText(String.format("已阅读:%.2f%s", bookShelfBean.getDurChapter() * 100.f / bookShelfBean.getChapterListSize(), "%"));
        }
        tvDes.setText(bookShelfBean.getBookInfoBean().getIntroduce());
        bottomSheetDialog.show();
        return false;
    }

    @Override
    protected void onFragmentVisibleChange(boolean isVisible) {
        super.onFragmentVisibleChange(isVisible);
        if (isVisible) {
            onRefresh();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        onRefresh();
    }
}
