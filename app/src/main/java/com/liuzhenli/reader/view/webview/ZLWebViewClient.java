package com.liuzhenli.reader.view.webview;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.liuzhenli.common.utils.NetworkUtils;
import com.liuzhenli.common.widget.DialogUtil;
import com.liuzhenli.reader.ui.activity.WebViewActivity;
import com.liuzhenli.common.utils.AppUtils;
import com.liuzhenli.common.utils.Constant;
import com.liuzhenli.common.utils.L;
import com.liuzhenli.common.utils.ToastUtil;
import com.microedu.reader.R;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;

import java.util.HashMap;
import java.util.Map;

public class ZLWebViewClient extends WebViewClient {
    private Context mContext;
    private String TAG;

    public ZLWebViewClient(Context context) {
        mContext = context;
        TAG = context.getClass().getName();
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        // 如下方案可在非微信内部WebView的H5页面中调出微信支付
        if (url.startsWith("weixin://wap/pay?")) {
            if (true) {
                try {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    mContext.startActivity(intent);
                } catch (Exception e) {
                    ToastUtil.showToast("无法支付，请安装微信！");
                    view.loadUrl("http://log.umsns.com/link/weixin/download/");
                }
                return true;
            } else {
                ToastUtil.showToast("无法支付，请安装微信！");
                view.loadUrl("http://log.umsns.com/link/weixin/download/");
                return true;
            }
        } else if (parseScheme(url)) {
            try {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                mContext.startActivity(intent);
            } catch (Exception e) {
            }
            return true;
        } else if (url.contains("wx.tenpay.com")) {
            Map<String, String> extraHeaders = new HashMap<String, String>();
            extraHeaders.put("Referer", Constant.REFER);
            view.loadUrl(url, extraHeaders);
            return true;
        }
        url = AppUtils.resetGameUrl(url);
        L.e("ZLWebViewClient", url);
        view.loadUrl(url); // 在当前的webview中跳转到新的url
        return super.shouldOverrideUrlLoading(view, url);
    }

    private void loadComplete(WebView view) {
        if (mContext instanceof WebViewActivity) {
            WebViewActivity activity = (WebViewActivity) mContext;
            activity.mTvTitle.setText(view.getTitle());
            activity.mTvTitle.setSelected(true);//开启走马灯(title);
            activity.setRefresh(false);
        }
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        loadComplete(view);
    }

    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        if (view.getContext() == null) {
            return;
        }
        DialogUtil.showMessagePositiveDialog(view.getContext(), view.getContext().getString(R.string.dialog_title),
                view.getContext().getString(R.string.common_web_ssl_error),
                view.getContext().getString(R.string.common_web_ssl_error_allow), (dialog, index)
                        -> handler.cancel(), view.getContext().getString(R.string.common_web_ssl_error_reject), (dialog, index)
                        -> handler.proceed(), false);
    }

    @Override
    public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
        super.onReceivedHttpError(view, request, errorResponse);
        loadComplete(view);
    }

    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        super.onReceivedError(view, request, error);
        loadComplete(view);
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        if (mContext instanceof WebViewActivity && NetworkUtils.isConnected(mContext)) {
            WebViewActivity activity = (WebViewActivity) mContext;
            activity.startRefresh();
        }
        super.onPageStarted(view, url, favicon);

    }


    public boolean parseScheme(String url) {
        if (url.contains("platformapi/startapp") || url.contains("alipay")) {
            return true;
        } else if (url.contains("web-other")) {
            return false;
        } else {
            return false;
        }
    }
}
