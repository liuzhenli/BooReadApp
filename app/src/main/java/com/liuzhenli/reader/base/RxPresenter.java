package com.liuzhenli.reader.base;


import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * @author Liuzhenli
 * @since 2019-07-07 10:08
 */
public class RxPresenter<T extends BaseContract.BaseView> implements BaseContract.BasePresenter<T> {
    protected T mView;
    private CompositeDisposable mCompositeDisposable;

    @Override
    public void attachView(T view) {
        this.mView = view;
    }

    @Override
    public void detachView() {
        this.mView = null;
        unSubscribe();
    }

    protected void addSubscribe(Disposable compositeDisposable) {
        getCompositeDisposable().add(compositeDisposable);
    }

    private void unSubscribe() {
        if (mCompositeDisposable != null) {
            mCompositeDisposable.dispose();
        }
    }

    public CompositeDisposable getCompositeDisposable() {
        if (mCompositeDisposable == null) {
            mCompositeDisposable = new CompositeDisposable();
        }
        return mCompositeDisposable;
    }
}
