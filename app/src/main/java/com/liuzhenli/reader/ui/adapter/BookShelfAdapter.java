package com.liuzhenli.reader.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;


import com.liuzhenli.reader.utils.image.ImageUtil;
import com.liuzhenli.reader.view.BadgeView;
import com.liuzhenli.reader.view.RotateLoading;
import com.liuzhenli.reader.view.recyclerview.adapter.BaseViewHolder;
import com.liuzhenli.reader.view.recyclerview.adapter.RecyclerArrayAdapter;
import com.micoredu.readerlib.bean.BookShelfBean;
import com.microedu.reader.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * describe:书架
 *
 * @author Liuzhenli on 2020-01-11 15:47
 */
public class BookShelfAdapter extends RecyclerArrayAdapter<BookShelfBean> {


    public BookShelfAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseViewHolder<BookShelfBean> OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new BookShelfHolder(parent, R.layout.item_bookshelf_list);
    }

    public static class BookShelfHolder extends BaseViewHolder<BookShelfBean> {
        @BindView(R.id.iv_cover)
        ImageView mIvCover;
        @BindView(R.id.bv_unread)
        BadgeView mBvUnread;
        @BindView(R.id.rl_loading)
        RotateLoading mRlLoading;
        @BindView(R.id.fl_has_new)
        FrameLayout mFlHasNew;
        @BindView(R.id.tv_name)
        TextView mTvName;
        @BindView(R.id.tv_author)
        TextView mTvAuthor;
        @BindView(R.id.tv_read)
        TextView mTvRead;
        @BindView(R.id.tv_last)
        TextView mTvLast;
        @BindView(R.id.vw_select)
        View mVwSelect;

        public BookShelfHolder(ViewGroup parent, int res) {
            super(parent, res);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void setData(BookShelfBean item) {
            super.setData(item);
            mTvName.setText(item.getBookInfoBean().getName() == null ? "[未知书名]" : item.getBookInfoBean().getName());
            mTvAuthor.setText(String.format("%s 著", TextUtils.isEmpty(item.getBookInfoBean().getAuthor()) ? "佚名" : item.getBookInfoBean().getAuthor()));
            mTvRead.setText(String.format("读至:%s", item.getDurChapterName() == null ? "章节名未知" : item.getDurChapterName()));
            mTvLast.setText(String.format("最新:%s", item.getLastChapterName() == null ? "章节名未知" : item.getLastChapterName()));
            if (item.getBookInfoBean() != null) {
                ImageUtil.setImage(mContext, item.getBookInfoBean().getCoverUrl(), R.drawable.book_cover, R.drawable.book_cover, mIvCover);
            }
            if (item.isLoading()) {
                mBvUnread.setVisibility(View.GONE);
                mRlLoading.setVisibility(View.VISIBLE);
                mRlLoading.start();
            } else {
                mBvUnread.setVisibility(View.VISIBLE);
                //未读章节数
                mBvUnread.setBadgeCount(item.getUnreadChapterNum());
                //显示有(无)更新
                mBvUnread.setHighlight(item.getHasUpdate());
                //停止loading
                mRlLoading.setVisibility(View.GONE);
                mRlLoading.stop();
            }
        }
    }

}
