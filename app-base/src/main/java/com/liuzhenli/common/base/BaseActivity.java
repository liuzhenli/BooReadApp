package com.liuzhenli.common.base;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.ArrayMap;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.alibaba.android.arouter.launcher.ARouter;
import com.hwangjr.rxbus.RxBus;
import com.liuzhenli.common.BaseApplication;
import com.liuzhenli.common.R;
import com.liuzhenli.common.AppComponent;
import com.liuzhenli.common.utils.AppSharedPreferenceHelper;
import com.liuzhenli.common.utils.SoftInputUtils;
import com.liuzhenli.common.utils.ToastUtil;
import com.liuzhenli.common.base.rxlife.RxAppCompatActivity;
import com.liuzhenli.common.widget.bar.ImmersionBar;
import com.liuzhenli.common.widget.recyclerview.EasyRecyclerView;
import com.orhanobut.logger.Logger;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

/**
 * @author Liuzhenli
 * @since 2019-07-06 17:18
 */
public abstract class BaseActivity<T1 extends BaseContract.BasePresenter> extends RxAppCompatActivity {
    public final static String START_SHEAR_ELE = "start_with_share_ele";
    protected Context mContext;
    public TextView mTvTitle, mTvRight;
    public ImageView mIvRight;
    public Toolbar mToolBar;
    protected EasyRecyclerView mRecyclerView;
    @Inject
    public T1 mPresenter;
    /**
     * 全屏状态下不需要状态栏
     */
    protected boolean mIsFullScreen;
    /***状态栏*/
    protected ImmersionBar mImmersionBar;
    private Boolean startShareAnim = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(bindContentView());
        RxBus.get().register(this);
        ARouter.getInstance().inject(this);
        if (getIntent() != null) {
            startShareAnim = getIntent().getBooleanExtra(START_SHEAR_ELE, false);
        }
        mContext = this;
        if (!mIsFullScreen) {
            initImmersionBar();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getWindow().getDecorView().setImportantForAutofill(View.IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS);
        }
        setupActivityComponent(BaseApplication.getInstance().getAppComponent());
        initData();
        mToolBar = findViewById(R.id.toolbar);
        if (mToolBar != null) {
            mTvTitle = findViewById(R.id.tv_toolbar_title);
            mTvRight = findViewById(R.id.tv_toolbar_right);
            mIvRight = findViewById(R.id.iv_toolbar_right);
            mToolBar.setNavigationOnClickListener(v -> onBackPressed());
            mToolBar.setOverflowIcon(getResources().getDrawable(R.drawable.ic_more));
            initToolBar();
        }
        if (mPresenter != null) {
            //noinspection unchecked
            mPresenter.attachView(this);
        }
        mRecyclerView = findViewById(R.id.recyclerView);
        configViews();
        if (savedInstanceState != null) {
            restoreState(savedInstanceState);
        }
    }

    public void startActivityByAnim(Intent intent, int animIn, int animExit) {
        startActivity(intent);
        overridePendingTransition(animIn, animExit);
    }

    protected abstract View bindContentView();

    protected abstract void setupActivityComponent(AppComponent appComponent);

    protected abstract void initToolBar();

    protected abstract void initData();

    protected abstract void configViews();

    protected void restoreState(Bundle savedInstanceState) {

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // 如果你的app可以横竖屏切换，并且适配4.4或者emui3手机请务必在onConfigurationChanged方法里添加这句话
        ImmersionBar.with(this).init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ImmersionBar.with(this).destroy();
        RxBus.get().unregister(this);
        if (mPresenter != null) {
            mPresenter.detachView();
        }
    }

    /**
     * 沉浸状态栏
     */
    protected void initImmersionBar() {
        mImmersionBar = ImmersionBar.with(this);
        mImmersionBar.statusBarColor(R.color.main);
        mImmersionBar.statusBarDarkFont(false);
        mImmersionBar.fitsSystemWindows(true);
        mImmersionBar.init();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (SoftInputUtils.isSoftShowing(this)) {
            SoftInputUtils.toggleSoftInput(mContext);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (startShareAnim) {
                overridePendingTransition(0, android.R.anim.fade_out);
            } else {
                finishAfterTransition();
            }
        } else {
            overridePendingTransition(0, android.R.anim.fade_out);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        //页面退出时,保存页面信息
        Map<String, Object> data = new HashMap<>();
        data.put("pageName", TAG);
        data.put("timeStamp", System.currentTimeMillis());
        AppSharedPreferenceHelper.saveLastPage(getPageInfo(data));
    }

    public Map<String, Object> getPageInfo(Map<String, Object> data) {
        return null;
    }

    protected void toast(String mst) {
        ToastUtil.showToast(mContext, mst);
    }

}
