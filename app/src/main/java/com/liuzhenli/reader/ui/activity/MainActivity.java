package com.liuzhenli.reader.ui.activity;

import android.Manifest;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentPagerAdapter;

import com.balysv.materialmenu.MaterialMenuDrawable;
import com.balysv.materialmenu.MaterialMenuView;
import com.google.android.material.tabs.TabLayout;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.liuzhenli.common.constant.RxBusTag;
import com.liuzhenli.common.utils.ClickUtils;
import com.liuzhenli.common.utils.FillContentUtil;
import com.liuzhenli.reader.network.AppComponent;
import com.liuzhenli.reader.base.BaseActivity;
import com.liuzhenli.reader.observer.SampleProgressObserver;
import com.liuzhenli.reader.ui.adapter.MainTabAdapter;
import com.liuzhenli.common.utils.Constant;
import com.liuzhenli.reader.ui.fragment.DiscoverFragment;
import com.liuzhenli.reader.utils.PermissionUtil;
import com.liuzhenli.reader.utils.ToastUtil;
import com.liuzhenli.reader.view.NoAnimViewPager;
import com.micoredu.readerlib.bean.BookSourceBean;
import com.microedu.reader.R;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author liuzhenli
 */
public class MainActivity extends BaseActivity {
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawLayout;
    @BindView(R.id.material_menu)
    MaterialMenuView mMaterialMenu;
    @BindView(R.id.cl_main_left)
    ViewGroup mDrawLeft;

    @BindView(R.id.view_pager_main)
    NoAnimViewPager mViewPager;
    @BindView(R.id.tab_layout_main)
    TabLayout mTabLayout;
    @BindView(R.id.tv_main_draw_setting)
    View mViewSetting;
    @BindView(R.id.tv_donate)
    View mViewDonate;
    @BindView(R.id.tv_main_feedback)
    View mViewFeedBack;
    @BindView(R.id.tv_main_left_source_manage)
    View mViewBookSourceManag;
    /***关于**/
    @BindView(R.id.tv_main_draw_about)
    View mViewAbout;
    @BindView(R.id.tv_night_mode)
    View mViewNightMode;

    private int mCurrentPosition;
    private MainTabAdapter mainTabAdapter;
    /***发现页面的书源名字*/
    private String mDiscoverBookSourceName;

    @OnClick(R.id.tv_night_mode)
    public void login() {

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main_container;
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {

    }

    @Override
    public void initToolBar() {
        mToolBar.setOverflowIcon(getResources().getDrawable(R.drawable.ic_more));
        FillContentUtil.setText(mTvTitle, getResources().getStringArray(R.array.main_tab_names)[mCurrentPosition]);
        mTvTitle.requestFocus();
        mTvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentPosition == 1) {
                    DiscoverFragment fragment = (DiscoverFragment) mainTabAdapter.getItem(mCurrentPosition);
                    fragment.showBookSourceView();
                }
            }
        });
    }

    @Override
    public void initData() {
        requestPermissions();
    }

    @Override
    public void configViews() {
        setMenu();

        mainTabAdapter = new MainTabAdapter(mContext, getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        mViewPager.setAdapter(mainTabAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        //打开左侧的设置页面
        ClickUtils.click(mMaterialMenu, o -> {
            if (!mDrawLayout.isDrawerOpen(mDrawLeft)) {
                mDrawLayout.openDrawer(Gravity.LEFT, true);
            } else {
                mDrawLayout.closeDrawer(mDrawLeft);
            }

        });

        for (int i = 0; i < mTabLayout.getTabCount(); i++) {
            mTabLayout.getTabAt(i).setCustomView(mainTabAdapter.getTabView(i));
        }
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mCurrentPosition = tab.getPosition();
                if (mCurrentPosition == 1 && !TextUtils.isEmpty(mDiscoverBookSourceName)) {
                    FillContentUtil.setTextDrawable(mTvTitle, mDiscoverBookSourceName, getResources().getDrawable(R.drawable.ic_down_filled), FillContentUtil.Place.right);
                } else {
                    FillContentUtil.setText(mTvTitle, mainTabAdapter.getPageTitle(mCurrentPosition));
                }
                setMenu();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        mDrawLeft.setOnClickListener(null);

        //意见反馈
        ClickUtils.click(mViewFeedBack, o -> {
            WebViewActivity.start(mContext, Constant.FEEDBACK);
        });

        //书源管理
        ClickUtils.click(mViewBookSourceManag, o -> {
            BookSourceActivity.start(mContext);
        });

        //drawableLayout 滑动监听
        mDrawLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                mMaterialMenu.setTransformationOffset(MaterialMenuDrawable.AnimationState.BURGER_ARROW, 2 - slideOffset);
            }
        });
        //设置
        ClickUtils.click(mViewSetting, o -> {
            SettingActivity.start(mContext);
        });
        //关于
        ClickUtils.click(mViewAbout, o -> {
            AboutActivity.start(mContext);
        });
        //捐赠
        ClickUtils.click(mViewDonate, o -> {
            DonateActivity.start(mContext);
        });
        mViewDonate.setVisibility(View.GONE);
        mViewPager.setOffscreenPageLimit(4);
        mViewNightMode.setVisibility(View.GONE);
    }

    private void setMenu() {
        mToolBar.getMenu().clear();
        switch (mCurrentPosition) {
            case 0:
                mToolBar.inflateMenu(R.menu.menu_main);
                mToolBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.item_search:
                                SearchActivity.start(mContext);
                                break;
                            case R.id.item_add_from_local:
                                ImportLocalBookActivity.start(mContext);
                                break;
                            case R.id.item_arrange_bookshelf:
                                ImportLocalBookActivity.start(mContext);
                                break;
                            default:
                                break;
                        }
                        return false;
                    }
                });
                break;
            case 1:
                mToolBar.inflateMenu(R.menu.menu_discover);
                mToolBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.item_search:
                                SearchActivity.start(mContext);
                                break;
                            case R.id.item_arrange_book_source:
                                BookSourceActivity.start(mContext);
                                break;
                            default:
                                break;
                        }
                        return false;
                    }
                });
                break;
            default:
                break;
        }

    }

    private void requestPermissions() {
        PermissionUtil.requestPermission(this, new SampleProgressObserver<Boolean>() {
            @Override
            public void onNext(Boolean aBoolean) {
                if (aBoolean) {

                } else {
                    ToastUtil.showToast("权限被拒绝,拒绝权限将无法使用***功能！");
                }
            }
        }, Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    @Subscribe(thread = EventThread.MAIN_THREAD, tags = {@Tag(RxBusTag.CHANGE_DISCOVER_BOOK_SOURCE)})
    public void onBookSourceChange(BookSourceBean book) {
        if (book != null) {
            mDiscoverBookSourceName = book.getBookSourceName();
        }
        if (mCurrentPosition == 1) {
            FillContentUtil.setTextDrawable(mTvTitle, mDiscoverBookSourceName, getResources().getDrawable(R.drawable.ic_down_filled), FillContentUtil.Place.right);
        }
    }

    @Subscribe(thread = EventThread.MAIN_THREAD, tags = {@Tag(RxBusTag.RECREATE)})
    public void onRecreateEvent(Boolean recreate) {
        recreate();
    }

    @Override
    public void onBackPressed() {
        if (mCurrentPosition == 1) {
            DiscoverFragment fragment = (DiscoverFragment) mainTabAdapter.getItem(mCurrentPosition);
            if (fragment.isShowBookSourceMenu()) {
                fragment.dismissBookSourceMenu();
            } else {
                moveTaskToBack(true);
                //super.onBackPressed();
            }
        } else {
            moveTaskToBack(true);
            //super.onBackPressed();
        }


    }
}
