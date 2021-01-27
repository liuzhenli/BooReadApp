package com.liuzhenli.reader.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import androidx.fragment.app.Fragment;

import com.liuzhenli.common.BitIntentDataManager;
import com.liuzhenli.common.utils.ClickUtils;
import com.liuzhenli.reader.base.BaseTabActivity;
import com.liuzhenli.reader.network.AppComponent;
import com.liuzhenli.reader.ui.fragment.BookChapterListFragment;
import com.liuzhenli.reader.ui.fragment.BookMarkFragment;
import com.micoredu.readerlib.bean.BookChapterBean;
import com.micoredu.readerlib.bean.BookShelfBean;
import com.micoredu.readerlib.bean.BookmarkBean;
import com.micoredu.readerlib.helper.BookshelfHelper;
import com.micoredu.readerlib.helper.ReadConfigManager;
import com.microedu.reader.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import io.reactivex.functions.Consumer;

/**
 * Description:
 *
 * @author liuzhenli 2021/1/26
 * Email: 848808263@qq.com
 */
public class BookChapterListActivity extends BaseTabActivity {

    @BindView(R.id.view_root_chapter_list)
    View mViewRoot;
    private BookShelfBean mBookShelf;
    /***正序*/
    private List<BookChapterBean> mChapterBeanList;
    /***倒序*/
    private List<BookChapterBean> mChapterList = new ArrayList<>();
    private List<BookmarkBean> mBookMarkList = new ArrayList<>();
    private List<BookmarkBean> mBookMarkDesc = new ArrayList<>();

    private boolean mIsBookMark;

    private boolean isAsc = true;

    public static void start(Context context, BookShelfBean bookShelf, List<BookChapterBean> chapterBeanList, boolean isBookMark) {
        Intent intent = new Intent(context, BookChapterListActivity.class);
        String key = String.valueOf(System.currentTimeMillis());

        String bookKey = "book" + key;
        intent.putExtra("isBookMark", isBookMark);
        intent.putExtra("bookKey", bookKey);
        BitIntentDataManager.getInstance().putData(bookKey, bookShelf.clone());

        String chapterListKey = "chapterList" + key;
        intent.putExtra("chapterListKey", chapterListKey);
        BitIntentDataManager.getInstance().putData(chapterListKey, chapterBeanList);

        context.startActivity(intent);
    }

    public static void start(Context context, BookShelfBean bookShelf, List<BookChapterBean> chapterBeanList) {
        start(context, bookShelf, chapterBeanList, false);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.act_bookchapterlist;
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {

    }

    @Override
    protected void initToolBar() {
        mTvRight.setText("倒序");
        ClickUtils.click(mTvRight, new Consumer() {
            @Override
            public void accept(Object o) throws Exception {
                isAsc = !isAsc;
                ((BookChapterListFragment) mFragmentList.get(0)).refreshData();
                ((BookMarkFragment) mFragmentList.get(1)).refreshData();
                if (isAsc) {
                    mTvRight.setText("倒序");
                } else {
                    mTvRight.setText("正序");
                }
            }
        });
        mTvRight.setVisibility(View.VISIBLE);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void initData() {
        String bookshelfKey = getIntent().getStringExtra("bookKey");
        mBookShelf = (BookShelfBean) BitIntentDataManager.getInstance().getData(bookshelfKey);
        mIsBookMark = getIntent().getBooleanExtra("isBookMark", false);
        String chapterListKey = getIntent().getStringExtra("chapterListKey");
        mChapterBeanList = (List<BookChapterBean>) BitIntentDataManager.getInstance().getData(chapterListKey);
        if (mBookShelf != null && mBookShelf.getBookInfoBean() != null) {
            mTvTitle.setText(mBookShelf.getBookInfoBean().getName());
        }

        if (mChapterBeanList != null) {
            for (int i = 0; i < mChapterBeanList.size(); i++) {
                mChapterList.add(0, mChapterBeanList.get(i));
            }
        }

        if (mBookShelf != null && mBookShelf.getBookInfoBean() != null) {
            mBookMarkList = BookshelfHelper.getBookmarkList(mBookShelf.getBookInfoBean().getName());
            for (int i = 0; i < mBookMarkList.size(); i++) {
                mBookMarkDesc.add(0, mBookMarkList.get(i));
            }
        }

    }

    @Override
    protected List<Fragment> createTabFragments() {
        return Arrays.asList(BookChapterListFragment.getInstance(), BookMarkFragment.getInstance());
    }

    @Override
    protected List<String> createTabTitles() {
        ArrayList<String> titles = new ArrayList<>();
        titles.add("目录");
        titles.add("书签");
        return titles;
    }

    public BookShelfBean getBookShelf() {
        return mBookShelf;
    }

    public List<BookChapterBean> getChapterBeanList() {
        if (!isAsc) {
            return mChapterList;
        }
        return mChapterBeanList;
    }

    public List<BookmarkBean> getBookMarkList() {
        if (isAsc) {
            return mBookMarkList;
        }
        return mBookMarkDesc;
    }

    @Override
    protected void configViews() {
        super.configViews();
        if (mIsBookMark) {
            mVp.setCurrentItem(1);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        changeTheme();
    }

    private void changeTheme() {
        mTabLayout.setBackground(ReadConfigManager.getInstance().getTextBackground(mContext));
        if (ReadConfigManager.getInstance().getIsNightTheme()) {
            mToolBar.setBackgroundColor(getResources().getColor(R.color.skin_night_reader_scene_bg_color));
            mImmersionBar.statusBarColor(R.color.skin_night_reader_scene_bg_color);
            mTabLayout.setSelectedTabIndicatorColor(ReadConfigManager.getInstance().getTextColor());
        } else {
            mTabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.main));
            mImmersionBar.statusBarColor(R.color.main);
            mToolBar.setBackgroundColor(getResources().getColor(R.color.main));
        }
        mViewRoot.setBackground(ReadConfigManager.getInstance().getTextBackground(mContext));
        mTabLayout.setTabTextColors(getResources().getColor(R.color.text_color_99), ReadConfigManager.getInstance().getTextColor());
        mImmersionBar.statusBarDarkFont(false);
        mImmersionBar.init();
    }
}
