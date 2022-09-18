package com.micoredu.reader.utils;

import static com.liuzhenli.common.constant.AppConstant.SCRIPT_ENGINE;

import android.text.TextUtils;

import com.micoredu.reader.service.CheckSourceService;
import com.micoredu.reader.analyzerule.AnalyzeRule;
import com.micoredu.reader.bean.BookSourceBean;
import com.micoredu.reader.bean.SearchBookBean;
import com.micoredu.reader.helper.AppReaderDbHelper;
import com.micoredu.reader.model.WebBookModel;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.script.SimpleBindings;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;


public class CheckSourceTask {

    private BookSourceBean sourceBean;
    private Scheduler scheduler;
    private CheckSourceService.CheckSourceListener checkSourceListener;

    public CheckSourceTask(BookSourceBean sourceBean, Scheduler scheduler, CheckSourceService.CheckSourceListener checkSourceListener) {
        this.sourceBean = sourceBean;
        this.scheduler = scheduler;
        this.checkSourceListener = checkSourceListener;
    }

    public void startCheck() {
        if (!TextUtils.isEmpty(sourceBean.getRuleSearchUrl())) {
            WebBookModel.getInstance().searchBook("我的", 1, sourceBean.getBookSourceUrl())
                    .subscribeOn(scheduler)
                    .observeOn(AndroidSchedulers.mainThread())
                    .timeout(60, TimeUnit.SECONDS)
                    .subscribe(new Observer<List<SearchBookBean>>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            checkSourceListener.compositeDisposableAdd(d);
                        }

                        @Override
                        public void onNext(List<SearchBookBean> searchBookBeans) {
                            if (searchBookBeans.isEmpty()) {
                                checkFind();
                            } else {
                                sourceUnInvalid();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            checkFind();
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } else {
            checkFind();
        }
    }

    private void checkFind() {
        if (!TextUtils.isEmpty(sourceBean.getRuleFindUrl())) {
            Observable.create((ObservableOnSubscribe<String>) emitter -> {
                        String[] kindA;
                        if (!TextUtils.isEmpty(sourceBean.getRuleFindUrl())) {
                            if (sourceBean.getRuleFindUrl().startsWith("<js>")) {
                                String jsStr = sourceBean.getRuleFindUrl().substring(4, sourceBean.getRuleFindUrl().lastIndexOf("<"));
                                Object object = evalJS(jsStr, sourceBean.getBookSourceUrl(), sourceBean);
                                kindA = object.toString().split("(&&|\n)+");
                            } else {
                                kindA = sourceBean.getRuleFindUrl().split("(&&|\n)+");
                            }
                            emitter.onNext(kindA[0].split("::")[1]);
                            emitter.onComplete();
                        }
                    }).flatMap(url -> WebBookModel.getInstance().findBook(url, 1, sourceBean.getBookSourceUrl()))
                    .subscribeOn(scheduler)
                    .observeOn(AndroidSchedulers.mainThread())
                    .timeout(60, TimeUnit.SECONDS)
                    .subscribe(new Observer<List<SearchBookBean>>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            checkSourceListener.compositeDisposableAdd(d);
                        }

                        @Override
                        public void onNext(List<SearchBookBean> searchBookBeans) {
                            if (searchBookBeans.isEmpty()) {
                                sourceInvalid();
                            } else {
                                sourceUnInvalid();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            sourceInvalid();
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } else {
            sourceInvalid();
        }
    }

    private void sourceInvalid() {
        sourceBean.addGroup("失效");
        sourceBean.setEnable(false);
        sourceBean.setSerialNumber(10000 + checkSourceListener.getCheckIndex());
        AppReaderDbHelper.getInstance().getDatabase().getBookSourceDao().insertOrReplace(sourceBean);
        checkSourceListener.nextCheck();
    }

    private void sourceUnInvalid() {
        if (sourceBean.containsGroup("失效")) {
            sourceBean.removeGroup("失效");
            AppReaderDbHelper.getInstance().getDatabase().getBookSourceDao().insertOrReplace(sourceBean);
        }
        checkSourceListener.nextCheck();
    }

    /**
     * 执行JS
     */
    private Object evalJS(String jsStr, String baseUrl, BookSourceBean bookSourceBean) throws Exception {
        SimpleBindings bindings = new SimpleBindings();
        bindings.put("java", new AnalyzeRule(null, bookSourceBean));
        bindings.put("baseUrl", baseUrl);
        return SCRIPT_ENGINE.eval(jsStr, bindings);
    }


}
