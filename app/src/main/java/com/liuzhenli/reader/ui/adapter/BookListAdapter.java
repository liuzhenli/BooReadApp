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
import com.micoredu.readerlib.bean.SearchBookBean;
import com.microedu.reader.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Description:
 *
 * @author liuzhenli 2020/10/9
 * Email: 848808263@qq.com
 */
public class BookListAdapter extends RecyclerArrayAdapter<SearchBookBean> {


    public BookListAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new BookShelfHolder(parent, R.layout.item_book_list);
    }

    public class BookShelfHolder extends BaseViewHolder<SearchBookBean> {
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
        @BindView(R.id.tv_book_summary)
        TextView mTvSummary;
        @BindView(R.id.tv_author_info)
        TextView mTvAuthor;

        public BookShelfHolder(ViewGroup parent, int res) {
            super(parent, res);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void setData(SearchBookBean item) {
            super.setData(item);
            mTvName.setText(TextUtils.isEmpty(item.getName()) ? "[未知书名]" : item.getName());
            mTvSummary.setText(getSummary(item));
            //数据源的数量
            if (item.getOriginNum() > 1) {
                mBvUnread.setVisibility(View.VISIBLE);
                mBvUnread.setText(String.format("%s", item.getOriginNum()));
            } else {
                mBvUnread.setVisibility(View.GONE);
            }
            //作者
            if (!TextUtils.isEmpty(item.getKind())) {
                mTvAuthor.setText(String.format("%s  %s", getAuthor(item), item.getKind()));
            } else {
                mTvAuthor.setText(String.format("%s", getAuthor(item)));
            }
            ImageUtil.setRoundedCornerImage(mContext, item.getCoverUrl(), R.drawable.ic_book_cover_placeholder, R.drawable.book_cover, mIvCover, 4);
        }

        private String getAuthor(SearchBookBean item) {
            return TextUtils.isEmpty(item.getAuthor()) ? "佚名" : String.format("%s·著", item.getAuthor());
        }
    }

    public String getSummary(SearchBookBean item) {
        if (item.getIntroduce() != null) {
            return item.getIntroduce();
        }
        if (item.getLastChapter() != null) {
            return item.getLastChapter();
        }
        return mBookSourceName;
    }

    private String mBookSourceName;

    public void setBookSourceName(String bookSourceName) {
        mBookSourceName = bookSourceName;
    }

}
