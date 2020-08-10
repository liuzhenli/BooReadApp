package com.liuzhenli.reader.base.rxlife;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.LifecycleOwner;

import com.liuzhenli.reader.view.loading.CustomDialog;

import io.reactivex.subjects.BehaviorSubject;


/**
 * @author Liuzhenli
 * @since 2019-07-06 16:51
 */
public class RxAppCompatActivity extends AppCompatActivity implements LifecycleOwner {
    public final String TAG = RxAppCompatActivity.this.getClass().getSimpleName();
    protected final BehaviorSubject<LifeEvent> lifeSubject = BehaviorSubject.create();
    /**进度条*/
    private CustomDialog dialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        lifeSubject.onNext(LifeEvent.CREATE);
    }

    public CustomDialog getDialog() {
        if (dialog == null) {
            dialog = new CustomDialog(this).instance(this);
            dialog.setCancelable(true);
        }
        return dialog;
    }

    public void setProgress(String progress) {
        if (dialog == null){
            showDialog();
        }
        if (dialog!=null) {
            dialog.setProgress(progress);
        }
    }

    public void hideDialog() {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }

    public void showDialog() {
        getDialog().show();
    }



    @Override
    protected void onStart() {
        super.onStart();
        lifeSubject.onNext(LifeEvent.CREATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        lifeSubject.onNext(LifeEvent.RESUME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        lifeSubject.onNext(LifeEvent.PAUSE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        lifeSubject.onNext(LifeEvent.STOP);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        lifeSubject.onNext(LifeEvent.DESTROY);
    }
}
