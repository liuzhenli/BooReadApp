package com.liuzhenli.reader.ui.fragment;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.hwangjr.rxbus.RxBus;
import com.liuzhenli.common.constant.RxBusTag;
import com.liuzhenli.common.base.BaseFragment;
import com.liuzhenli.common.AppComponent;
import com.liuzhenli.reader.DaggerReadBookComponent;
import com.liuzhenli.reader.ui.contract.DiscoverContract;
import com.liuzhenli.reader.ui.presenter.DiscoverPresenter;
import com.liuzhenli.reader.view.ScaleTransitionPagerTitleView;
import com.micoredu.reader.analyzerule.AnalyzeRule;
import com.micoredu.reader.bean.BookCategoryBean;
import com.micoredu.reader.bean.BookSourceBean;
import com.microedu.reader.R;
import com.microedu.reader.databinding.FragmentDiscoverBinding;


import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.UIUtil;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.script.ScriptException;
import javax.script.SimpleBindings;

import static androidx.fragment.app.FragmentPagerAdapter.BEHAVIOR_SET_USER_VISIBLE_HINT;
import static com.liuzhenli.common.constant.AppConstant.SCRIPT_ENGINE;

/**
 * describe:推荐书源列表
 *
 * @author Liuzhenli on 2019-11-09 22:28
 */
public class DiscoverFragment extends BaseFragment<DiscoverPresenter> implements DiscoverContract.View {

    private CommonNavigatorAdapter mCommonNavigationAdapter;
    private FragmentStatePagerAdapter fragmentPagerAdapter;
    private ArrayList<BookCategoryBean> mBookCategory = new ArrayList<>();
    protected List<BaseFragment> mFragmentList = new ArrayList<>();
    private FragmentDiscoverBinding inflate;

    public static DiscoverFragment getInstance() {
        DiscoverFragment instance = new DiscoverFragment();
        Bundle bundle = new Bundle();
        instance.setArguments(bundle);
        return instance;
    }

    @Override
    public View bindContentView(LayoutInflater inflater, ViewGroup container, boolean attachParent) {
        inflate = FragmentDiscoverBinding.inflate(inflater, container, attachParent);
        return inflate.getRoot();
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {
        DaggerReadBookComponent.builder().build().inject(this);
    }

    @Override
    public void attachView() {
        mPresenter.attachView(this);
    }


    @Override
    public void initData() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }

    @Override
    public void configViews() {
        inflate.mViewPager.setOffscreenPageLimit(10);
        FragmentManager fm = getChildFragmentManager();
        fragmentPagerAdapter = new FragmentStatePagerAdapter(fm, BEHAVIOR_SET_USER_VISIBLE_HINT) {
            @Override
            public Fragment getItem(int position) {
                return mFragmentList.get(position);
            }

            @Override
            public int getCount() {
                return mFragmentList.size();
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                return mBookCategory.get(position).name;
            }

            @Override
            public int getItemPosition(@NotNull Object object) {
                if (mFragmentList.contains((Fragment) object)) {
                    // 如果当前 item 未被 remove，则返回 item 的真实 position
                    return mFragmentList.indexOf((Fragment) object);
                } else {
                    // 否则返回状态值 POSITION_NONE
                    return POSITION_NONE;
                }
            }
        };
        inflate.mViewPager.setAdapter(fragmentPagerAdapter);

        CommonNavigator commonNavigator7 = new CommonNavigator(mContext);
        //这个控制左右滑动的时候,选中文字的位置,0.5表示在中间
        commonNavigator7.setScrollPivotX(0.5f);
        mCommonNavigationAdapter = new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return mFragmentList.size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, int index) {
                ScaleTransitionPagerTitleView simplePagerTitleView = new ScaleTransitionPagerTitleView(context);
                simplePagerTitleView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                simplePagerTitleView.setText(mBookCategory.get(index).name);
                simplePagerTitleView.setTextSize(16);
                simplePagerTitleView.setNormalColor(getResources().getColor(R.color.text_color_99));
                simplePagerTitleView.setSelectedColor(getResources().getColor(R.color.text_color_66));
                simplePagerTitleView.setOnClickListener(v -> inflate.mViewPager.setCurrentItem(index));
                return simplePagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator indicator = new LinePagerIndicator(context);
                indicator.setMode(LinePagerIndicator.MODE_EXACTLY);
                indicator.setLineHeight(UIUtil.dip2px(context, 4));
                indicator.setLineWidth(UIUtil.dip2px(context, 10));
                indicator.setRoundRadius(UIUtil.dip2px(context, 3));
                indicator.setStartInterpolator(new AccelerateInterpolator());
                indicator.setEndInterpolator(new DecelerateInterpolator(2.0f));
                indicator.setColors(getResources().getColor(R.color.main_blue));
                return indicator;
            }
        }

        ;
        commonNavigator7.setAdapter(mCommonNavigationAdapter);
        inflate.mIndicator.setNavigator(commonNavigator7);
        ViewPagerHelper.bind(inflate.mIndicator, inflate.mViewPager);
        inflate.mBookSourceView.setOnItemClick(this::onBookSourceChange);
        inflate.mBookSourceView.setVisibility(View.GONE);
        TextView tvText = inflate.mViewEmpty.viewEmpty.findViewById(R.id.tv_empty_text);
        if (inflate.mViewEmpty != null) {
            tvText.setText("暂无书源,搜索微信公众号:异书房,\n回复\"书源\"获取书源~");
        }
    }

    public void onRefresh() {
        mPresenter.getSource();
    }

    @Override
    public void showSource(List<BookSourceBean> bookSourceData) {
        if (bookSourceData != null && bookSourceData.size() > 0) {
            Random random = new Random();
            int index = random.nextInt(bookSourceData.size());
            onBookSourceChange(bookSourceData.get(index));
            inflate.mViewEmpty.viewEmpty.setVisibility(View.GONE);
        } else {
            inflate.mViewEmpty.viewEmpty.setVisibility(View.VISIBLE);
        }
        inflate.mBookSourceView.setData(bookSourceData);
    }


    @Override
    public void showError(Exception e) {
    }

    @Override
    public void complete() {
    }

    @Override
    protected void onFragmentVisibleChange(boolean isVisible) {
        super.onFragmentVisibleChange(isVisible);
        if (isVisible) {
            onRefresh();
        }
    }

    public void showBookSourceView() {
        if (inflate == null) {
            return;
        }
        if (inflate.mBookSourceView.getVisibility() == View.GONE) {
            inflate.mBookSourceView.show();
        } else {
            inflate.mBookSourceView.close();
        }
    }


    public synchronized void onBookSourceChange(BookSourceBean mBookSource) {
        mFragmentList.clear();
        mBookCategory.clear();
        String ruleFindUrl = mBookSource.getRuleFindUrl();
        if (TextUtils.isEmpty(ruleFindUrl)) {
            return;
        }
        if (ruleFindUrl.startsWith("<js>")) {
            try {
                String jsStr = ruleFindUrl.substring(4, ruleFindUrl.lastIndexOf("<"));
                SimpleBindings bindings = new SimpleBindings();
                bindings.put("java", new AnalyzeRule(null));
                bindings.put("baseUrl", ruleFindUrl);
                Object object = SCRIPT_ENGINE.eval(jsStr, bindings);
                ruleFindUrl = object.toString();
            } catch (ScriptException e) {
                e.printStackTrace();
            }
        }
        //一个类目
        String[] findItem = ruleFindUrl.split("(&&|\n)+");
        for (String s : findItem) {
            String[] categoryInfo = s.split("::");
            mBookCategory.add(new BookCategoryBean(categoryInfo[0], categoryInfo[1]));
            String url = categoryInfo[1];
            String tag = mBookSource.getBookSourceUrl();
            mFragmentList.add(BookCategoryFragment.getInstance(url, tag, mBookSource.getBookSourceName()));
        }
        fragmentPagerAdapter.notifyDataSetChanged();
        mCommonNavigationAdapter.notifyDataSetChanged();
        inflate.mViewPager.setCurrentItem(0);
        RxBus.get().post(RxBusTag.CHANGE_DISCOVER_BOOK_SOURCE, mBookSource);
    }

    @Override
    public void onResume() {
        super.onResume();
        //如果没有书源显示,再刷新一次
        if (inflate.mViewEmpty.viewEmpty.getVisibility() == View.VISIBLE) {
            onRefresh();
        }
    }


    public boolean isShowBookSourceMenu() {
        return inflate.mBookSourceView.isShowing();
    }

    public void dismissBookSourceMenu() {
        inflate.mBookSourceView.close();
    }

}
