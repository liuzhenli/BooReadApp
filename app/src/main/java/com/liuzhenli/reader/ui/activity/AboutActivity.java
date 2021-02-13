package com.liuzhenli.reader.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.text.StringKt;

import com.liuzhenli.common.BaseApplication;
import com.liuzhenli.common.BitIntentDataManager;
import com.liuzhenli.common.constant.AppConstant;
import com.liuzhenli.common.utils.ClickUtils;
import com.liuzhenli.common.utils.Constant;
import com.liuzhenli.reader.ReaderApplication;
import com.liuzhenli.reader.base.BaseActivity;
import com.liuzhenli.reader.bean.Sayings;
import com.liuzhenli.reader.network.AppComponent;
import com.liuzhenli.reader.utils.AppConfigManager;
import com.liuzhenli.reader.utils.ToastUtil;
import com.microedu.reader.R;

import butterknife.BindView;

/**
 * Description:
 *
 * @author liuzhenli 2020/8/10
 * Email: 848808263@qq.com
 */
public class AboutActivity extends BaseActivity {

    @BindView(R.id.tv_version_info)
    TextView tvVersionInfo;
    @BindView(R.id.tv_version_check_update)
    TextView tvVersionCheckUpdate;
    @BindView(R.id.tv_about_contact)
    TextView tvAboutContact;
    @BindView(R.id.tv_disclaimer)
    TextView tvDisclaimer;

    @BindView(R.id.tv_gg_group_1)
    TextView mTvQQGroup1;
    @BindView(R.id.tv_gg_group_0)
    TextView mTvQQGroup0;

    @BindView(R.id.iv_new_version_icon)
    ImageView mIvNewVersionIcon;
    @BindView(R.id.tv_new_version_info)
    TextView mTvNewVersionInfo;

    @BindView(R.id.tv_donate_title)
    TextView mTvDonateTitle;
    @BindView(R.id.tv_about_donate)
    TextView mTvAbout;

    @BindView(R.id.tv_about_saying)
    TextView mTvSaying;

    @BindView(R.id.tv_donate_zhifubao_kouling)
    TextView mViewDonateAliPay;
    @BindView(R.id.tv_donate_qq)
    TextView mViewQQ;
    @BindView(R.id.tv_donate_weixin)
    TextView mViewWX;
    @BindView(R.id.tv_donate_zfb)
    TextView mViewZFB;


    public static void start(Context context) {
        Intent intent = new Intent(context, AboutActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_about;
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {

    }

    @Override
    protected void initToolBar() {
        mTvTitle.setText("关于");
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void configViews() {
        String appName = getResources().getString(R.string.app_name);
        String versionName = ReaderApplication.getInstance().mVersionName;
        int versionCode = ReaderApplication.getInstance().mVersionCode;
        String channel = ReaderApplication.getInstance().mVersionChannel;
        tvVersionInfo.setText(String.format("%s %s Build#%s %s ", appName, versionName, versionCode, channel));
        ClickUtils.click(tvVersionCheckUpdate, o -> {
            //版本有更新
            if (AppConfigManager.getInstance().getNewVersion() > BaseApplication.getInstance().mVersionCode) {
                toast(AppConfigManager.getInstance().getNewVersionIntro());
            } else {
                toast("已经是最新版本");
            }
        });

        ClickUtils.click(tvAboutContact, o -> {
            //发送邮件
            openIntent(Intent.ACTION_SENDTO, "mailto:hpuzhenli@163.com");
        });
        ClickUtils.click(tvDisclaimer, o -> {
            //免责声明
            String key = System.currentTimeMillis() + "";
            BitIntentDataManager.getInstance().putData(key, mContext.getResources().getString(R.string.disclaimer_content));
            ContentActivity.start(mContext, key, "免责声明");
        });

        configQQGroup();

        //版本有更新
        if (AppConfigManager.getInstance().getNewVersion() > BaseApplication.getInstance().mVersionCode) {
            mIvNewVersionIcon.setVisibility(View.VISIBLE);
            mTvNewVersionInfo.setVisibility(View.VISIBLE);
        } else {
            mIvNewVersionIcon.setVisibility(View.GONE);
            mTvNewVersionInfo.setVisibility(View.GONE);
        }

        Sayings sayings = AppConfigManager.getInstance().getSayings();
        mTvSaying.setText(sayings.getSaying());

        String aboutDes = getResources().getString(R.string.about_donate);
        SpannableString spannableString = new SpannableString(aboutDes);
        String donate = getResources().getString(R.string.donate);
        spannableString.setSpan(new ClickableSpan() {
            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(getResources().getColor(R.color.text_color_66));
                ds.setUnderlineText(false);
            }

            @Override
            public void onClick(@NonNull View widget) {
                Intent intent = new Intent(mContext, DonateActivity.class);
                intent.putExtra(START_SHEAR_ELE, true);
                //startActivityByAnim(intent, android.R.anim.fade_in, android.R.anim.fade_out);
            }
        }, aboutDes.indexOf(donate), aboutDes.indexOf(donate) + donate.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        //设置点击后的颜色为透明
        mTvAbout.setHighlightColor(Color.TRANSPARENT);
        mTvAbout.setMovementMethod(LinkMovementMethod.getInstance());
        mTvAbout.setText(spannableString);

        ClickUtils.click(mViewDonateAliPay, o -> {
            Uri uri = Uri.parse(AppConstant.DonateUrl.ali);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        });
        ClickUtils.click(mViewZFB, o -> {
            Uri uri = Uri.parse(AppConstant.DonateUrl.zfbCode);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        });
        ClickUtils.click(mViewQQ, o -> {
            Uri uri = Uri.parse(AppConstant.DonateUrl.wxCode);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        });
        ClickUtils.click(mViewWX, o -> {
            Uri uri = Uri.parse(AppConstant.DonateUrl.qqCode);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        });

        configDonate(AppConfigManager.getInstance().isShowDonate());
    }

    private void configDonate(boolean showDonate) {
        if (!showDonate) {
            mTvDonateTitle.setVisibility(View.GONE);
            mTvAbout.setVisibility(View.GONE);
            mViewDonateAliPay.setVisibility(View.GONE);
            mViewQQ.setVisibility(View.GONE);
            mViewWX.setVisibility(View.GONE);
            mViewZFB.setVisibility(View.GONE);
        }
    }

    private void configQQGroup() {
        ClickUtils.click(mTvQQGroup0, o -> joinQQGroup(Constant.QQGroup.QQ_272343970));
        ClickUtils.click(mTvQQGroup1, o -> joinQQGroup(Constant.QQGroup.QQ_1140723995));
    }

    /****************
     *
     * 发起添加群流程。群号：阅读①(1140723995) 的 key 为： py5-vU4j3y7mobTS3IkZMKKJAFbiKRgl
     * 调用 joinQQGroup(py5-vU4j3y7mobTS3IkZMKKJAFbiKRgl) 即可发起手Q客户端申请加群 阅读①(1140723995)
     *
     * @param key 由官网生成的key
     * @return 返回true表示呼起手Q成功，返回false表示呼起失败
     ******************/
    public boolean joinQQGroup(String key) {
        Intent intent = new Intent();
        intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26jump_from%3Dwebapi%26k%3D" + key));
        // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            startActivity(intent);
            return true;
        } catch (Exception e) {
            // 未安装手Q或安装的版本不支持
            return false;
        }
    }

    void openIntent(String intentName, String address) {
        try {
            Intent intent = new Intent(intentName);
            intent.setData(Uri.parse(address));
            startActivity(intent);
        } catch (Exception e) {
            toast(getResources().getString(R.string.can_not_open));
        }
    }

}
