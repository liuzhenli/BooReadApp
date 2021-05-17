package com.liuzhenli.write.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.android.arouter.launcher.ARouter;
import com.liuzhenli.common.AppComponent;
import com.liuzhenli.common.base.BaseFragment;
import com.liuzhenli.common.constant.ARouterConstants;
import com.liuzhenli.write.DaggerWriteBookComponent;
import com.liuzhenli.write.bean.WriteBook;
import com.liuzhenli.write.databinding.FgWriteCreatebookBinding;
import com.liuzhenli.write.module.WriteModule;
import com.liuzhenli.write.ui.adapter.CreateBookBannerAdapter;
import com.liuzhenli.write.ui.contract.CreateBookContract;
import com.liuzhenli.write.ui.presenter.CreateBookPresenter;
import com.youth.banner.indicator.CircleIndicator;
import com.youth.banner.listener.OnBannerListener;

import java.util.List;

import static com.liuzhenli.write.ui.activity.EditBookInfoActivity.WRITE_BOOK;

/**
 * Description:
 *
 * @author liuzhenli 3/13/21
 * Email: 848808263@qq.com
 */
public class CreateBookFragment extends BaseFragment<CreateBookPresenter> implements CreateBookContract.View {

    private FgWriteCreatebookBinding mBinding;
    private CreateBookBannerAdapter mBannerAdapter;
    private List<WriteBook> books;

    public static CreateBookFragment getInstance() {
        CreateBookFragment instance = new CreateBookFragment();
        Bundle bundle = new Bundle();
        instance.setArguments(bundle);
        return instance;
    }

    @Override
    public View bindContentView(LayoutInflater inflater, ViewGroup container, boolean attachParent) {
        mBinding = FgWriteCreatebookBinding.inflate(inflater, container, attachParent);
        return mBinding.getRoot();
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {
        DaggerWriteBookComponent.builder().writeModule(new WriteModule()).build().inject(this);
    }

    @Override
    public void initData() {

    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.getCreateBooks();
    }

    @Override
    public void configViews() {
        mBannerAdapter = new CreateBookBannerAdapter(books);
        mBinding.banner.setAdapter(mBannerAdapter)
                //observe lifecycle
                .addBannerLifecycleObserver(this)
                .setIndicator(new CircleIndicator(mContext))
                .setOnBannerListener(new OnBannerListener() {
                    @Override
                    public void OnBannerClick(Object data, int position) {
                        WriteBook book = mBannerAdapter.getData(position);
                        if (data instanceof WriteBook) {
                            book = (WriteBook) data;
                        }
                        ARouter.getInstance().build(ARouterConstants.ACT_EDIT_BOOK_INFO).withSerializable(WRITE_BOOK, book).navigation();
                    }
                });
    }

    @Override
    public void showAllCreateBooks(List<WriteBook> books) {
        mBannerAdapter.setDatas(books);
        mBannerAdapter.notifyDataSetChanged();
    }

    @Override
    public void showError(Exception e) {

    }

    @Override
    public void complete() {

    }
}
