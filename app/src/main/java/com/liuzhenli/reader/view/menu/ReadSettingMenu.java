package com.liuzhenli.reader.view.menu;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.liuzhenli.common.utils.ClickUtils;
import com.liuzhenli.common.utils.FillContentUtil;
import com.liuzhenli.reader.utils.ToastUtil;
import com.micoredu.readerlib.helper.ReadConfigManager;
import com.micoredu.readerlib.utils.ReaderConfig;
import com.microedu.reader.R;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;

import butterknife.BindView;
import io.reactivex.functions.Consumer;

/**
 * Description:设置菜单
 *
 * @author liuzhenli 2020/8/16
 * Email: 848808263@qq.com
 */
public class ReadSettingMenu extends BaseMenu {
    @BindView(R.id.view_setting_menu_root)
    View mViewRoot;
    @BindView(R.id.tv_pre_chapter)
    TextView tvPreChapter;
    /***缩小字体*/
    @BindView(R.id.tv_menu_text_min)
    QMUIRoundButton tvMenuTextMin;
    @BindView(R.id.tv_setting_menu_text_size)
    TextView tvSettingMenuTextSize;
    @BindView(R.id.tv_menu_text_enlarge)
    QMUIRoundButton tvMenuTextEnlarge;
    @BindView(R.id.tv_menu_text_simple)
    QMUIRoundButton tvMenuTextSimple;
    @BindView(R.id.tv_menu_text_style)
    QMUIRoundButton tvMenuTextStyle;
    @BindView(R.id.tv_menu_text_type_face)
    QMUIRoundButton tvMenuTextTypeFace;
    @BindView(R.id.tv_setting_letter_space)
    TextView tvSettingLetterSpace;
    @BindView(R.id.tv_setting_text_background)
    TextView tvSettingTextBackground;
    @BindView(R.id.rb_menu_setting_bg)
    RadioGroup rbPageBg;

    @BindView(R.id.tv_setting_page_mode)
    TextView tvSettingPageMode;
    /****仿真*/
    @BindView(R.id.tv_setting_page_mode_paper)
    QMUIRoundButton tvSettingPageModePaper;
    /****覆盖*/
    @BindView(R.id.tv_setting_page_mode_cover)
    QMUIRoundButton tvSettingPageModeCover;
    /***滑动*/
    @BindView(R.id.tv_setting_page_mode_slide)
    QMUIRoundButton tvSettingPageModeSlide;
    /***上下*/
    @BindView(R.id.tv_setting_page_mode_vertical)
    QMUIRoundButton tvSettingPageModeVertical;
    /***无动画*/
    @BindView(R.id.tv_setting_page_mode_empty)
    QMUIRoundButton tvSettingPageModeEmpty;
    @BindView(R.id.tv_setting_reset)
    QMUIRoundButton tvSettingReset;

    @BindView(R.id.tv_menu_h_space_min)
    QMUIRoundButton mHSpaceMin;
    @BindView(R.id.tv_setting_menu_h_space_size)
    TextView mHSpaceSize;
    @BindView(R.id.tv_menu_h_space_enlarge)
    QMUIRoundButton mHSpaceEnlarge;

    @BindView(R.id.tv_menu_v_space_min)
    QMUIRoundButton mVSpaceMin;
    @BindView(R.id.tv_setting_menu_v_space_size)
    TextView mVSpaceSize;
    @BindView(R.id.tv_menu_v_space_enlarge)
    QMUIRoundButton mVSpaceEnlarge;


    private ReadSettingCallBack callBack;

    public ReadSettingMenu(@NonNull Context context) {
        this(context, null);
    }

    public ReadSettingMenu(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ReadSettingMenu(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.layout_read_menu_setting;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void init(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        fillDefaultContent();
        //仿真模式
        ClickUtils.click(tvSettingPageModePaper, o -> setPageMode(ReaderConfig.PageMode.SIMULATION));
        ClickUtils.click(tvSettingPageModeCover, o -> setPageMode(ReaderConfig.PageMode.COVER));
        ClickUtils.click(tvSettingPageModeVertical, o -> setPageMode(ReaderConfig.PageMode.SCROLL));
        ClickUtils.click(tvSettingPageModeSlide, o -> setPageMode(ReaderConfig.PageMode.SLIDE));
        ClickUtils.click(tvSettingPageModeEmpty, o -> setPageMode(ReaderConfig.PageMode.NONE));

        //减小字号
        ClickUtils.click(tvMenuTextMin, o -> {
            if (callBack != null) {
                int textSize = ReadConfigManager.getInstance().getTextSize();
                if (textSize > ReaderConfig.TextSize.SIZE_MIN) {
                    textSize -= 2;
                    ReadConfigManager.getInstance().setTextSize(textSize);
                    FillContentUtil.setNumberText(tvSettingMenuTextSize, textSize);
                    callBack.onTextStyleChanged();
                } else {
                    ToastUtil.showToast("已经是最小字体了~~");
                }
            }
        });

        //增大字号
        ClickUtils.click(tvMenuTextEnlarge, o -> {
            if (callBack != null) {
                int textSize = ReadConfigManager.getInstance().getTextSize();
                if (textSize < ReaderConfig.TextSize.SIZE_MAX) {
                    textSize += 2;
                    FillContentUtil.setNumberText(tvSettingMenuTextSize, textSize);
                    ReadConfigManager.getInstance().setTextSize(textSize);
                    callBack.onTextStyleChanged();
                } else {
                    ToastUtil.showToast("已经是最大字体了~~");
                }
            }
        });

        //字体简体--繁体
        ClickUtils.click(tvMenuTextSimple, o -> {
            if (ReadConfigManager.getInstance().getTextConvert() != ReaderConfig.CNText.CN_TRADITION) {
                ReadConfigManager.getInstance().setTextConvert(ReaderConfig.CNText.CN_TRADITION);
            } else {
                ReadConfigManager.getInstance().setTextConvert(ReaderConfig.CNText.CN_SIMPLE);
            }
            setCnText();
            if (callBack != null) {
                callBack.onTextStyleChanged();
            }
        });
        //字体加粗,常态
        ClickUtils.click(tvMenuTextStyle, o -> {
            ReadConfigManager.getInstance().setTextBold(!ReadConfigManager.getInstance().getTextBold());
            if (callBack != null) {
                callBack.onTextStyleChanged();
            }
            setTextBold();
        });
        //设置字体
        ClickUtils.click(tvMenuTextTypeFace, o -> {
            if (callBack != null) {
                callBack.onTypeFaceClicked();
            }
        });

        //设置行间距
        ClickUtils.click(mHSpaceMin, new Consumer() {
            @Override
            public void accept(Object o) throws Exception {
                float lineMultiplier = ReadConfigManager.getInstance().getLineMultiplier();
                lineMultiplier -= 0.1;
                ReadConfigManager.getInstance().setLineMultiplier(lineMultiplier);
                mHSpaceSize.setText(lineMultiplier + "");
                if (callBack != null) {
                    callBack.onTextStyleChanged();
                }
            }
        });
        //设置行间距
        ClickUtils.click(mHSpaceEnlarge, new Consumer() {
            @Override
            public void accept(Object o) throws Exception {
                float lineMultiplier = ReadConfigManager.getInstance().getLineMultiplier();
                lineMultiplier += 0.1;
                ReadConfigManager.getInstance().setLineMultiplier(lineMultiplier);
                mHSpaceSize.setText(lineMultiplier + "");
                if (callBack != null) {
                    callBack.onTextStyleChanged();
                }
            }
        });


        //设置背景
        rbPageBg.setOnCheckedChangeListener((group, checkedId) -> {
            setPageBackGround(checkedId);
            if (callBack != null) {
                callBack.onBackGroundChanged();
            }
        });
    }

    /**
     * 默认菜单选项设置
     */
    private void fillDefaultContent() {
        FillContentUtil.setNumberText(tvSettingMenuTextSize, ReadConfigManager.getInstance().getTextSize());
        setCnText();
        setTextBold();
        setPageMode(ReadConfigManager.getInstance().getPageMode());
        mHSpaceSize.setText(ReadConfigManager.getInstance().getLineMultiplier() + "");
    }

    /***繁体,简体*/
    private void setCnText() {
        if (ReadConfigManager.getInstance().getTextConvert() == ReaderConfig.CNText.CN_TRADITION) {
            tvMenuTextSimple.setText("简体");
        } else {
            tvMenuTextSimple.setText("繁体");
        }
    }

    static final int[] CHECKED_STATE_SET = new int[]{android.R.attr.state_checked};
    static final int[] SELECTED_STATE_SET = new int[]{android.R.attr.state_selected};
    static final int[] NOT_PRESSED_OR_FOCUSED_STATE_SET = new int[]{-android.R.attr.state_pressed, -android.R.attr.state_focused};

    private void setPageBackGround(int checkedId) {
        final int[][] states = new int[2][];
        final int[] color = new int[2];
        states[0] = NOT_PRESSED_OR_FOCUSED_STATE_SET;
        states[1] = CHECKED_STATE_SET;
        color[0] = R.color.text_color_99;
        color[1] = R.color.main;
        switch (checkedId) {
            //0 白天
            case R.id.tv_menu_background_0:
                ReadConfigManager.getInstance().setIsNightTheme(false);
                ReadConfigManager.getInstance().setTextDrawableIndex(ReaderConfig.PageBgColor.BG_COLOR_DAY);
                break;
            //1 黄色
            case R.id.tv_menu_background_1:
                ReadConfigManager.getInstance().setIsNightTheme(false);
                ReadConfigManager.getInstance().setTextDrawableIndex(ReaderConfig.PageBgColor.BG_COLOR_YELLOW);
                break;
            //2 绿色
            case R.id.tv_menu_background_2:
                ReadConfigManager.getInstance().setIsNightTheme(false);
                ReadConfigManager.getInstance().setTextDrawableIndex(ReaderConfig.PageBgColor.BG_COLOR_GREEN);
                break;
            //3 粉色
            case R.id.tv_menu_background_3:
                ReadConfigManager.getInstance().setIsNightTheme(false);
                ReadConfigManager.getInstance().setTextDrawableIndex(ReaderConfig.PageBgColor.BG_COLOR_PINK);
                break;
            //4 深蓝色
            case R.id.tv_menu_background_4:
                ReadConfigManager.getInstance().setIsNightTheme(false);
                ReadConfigManager.getInstance().setTextDrawableIndex(ReaderConfig.PageBgColor.BG_COLOR_SBLUE);
                break;
            //5 蓝色
            case R.id.tv_menu_background_5:
                ReadConfigManager.getInstance().setIsNightTheme(false);
                ReadConfigManager.getInstance().setTextDrawableIndex(ReaderConfig.PageBgColor.BG_COLOR_BLUE);
                break;
            //6 夜间  页面模式是一种皮肤,也需要一种开关
            case R.id.tv_menu_background_6:
                ReadConfigManager.getInstance().setIsNightTheme(true);
                ReadConfigManager.getInstance().setTextDrawableIndex(ReaderConfig.PageBgColor.BG_COLOR_NIGHT);
                break;
            default:
                break;
        }
        rbPageBg.check(checkedId);
    }

    private int getColor(int resId) {
        return getContext().getResources().getColor(resId);
    }

    private void setTextBold() {
        if (ReadConfigManager.getInstance().getTextBold()) {
            tvMenuTextStyle.setSelected(true);
        } else {
            tvMenuTextStyle.setSelected(false);
        }
    }

    /***翻页模式*/
    private void setPageMode(int mode) {
        tvSettingPageModePaper.setSelected(false);
        tvSettingPageModeCover.setSelected(false);
        tvSettingPageModeVertical.setSelected(false);
        tvSettingPageModeSlide.setSelected(false);
        tvSettingPageModeEmpty.setSelected(false);
        switch (mode) {
            case ReaderConfig.PageMode.SIMULATION:
                tvSettingPageModePaper.setSelected(true);
                break;
            case ReaderConfig.PageMode.COVER:
                tvSettingPageModeCover.setSelected(true);
                break;
            case ReaderConfig.PageMode.SLIDE:
                tvSettingPageModeSlide.setSelected(true);
                break;
            default:
                tvSettingPageModeEmpty.setSelected(true);
                break;
        }
        ReadConfigManager.getInstance().setPageMode(mode);
        if (callBack != null) {
            callBack.onPageAnimChanged();
        }
    }

    @Override
    protected void changeTheme() {

    }


    public void setReadSettingCallBack(ReadSettingCallBack callBack) {
        this.callBack = callBack;
    }

    public interface ReadSettingCallBack {
        /***翻页模式 */
        void onPageAnimChanged();

        /*** 字体大小  简体--繁体  行间距*/
        void onTextStyleChanged();

        /***点击字体切换了*/
        void onTypeFaceClicked();

        /***background change*/
        void onBackGroundChanged();
    }
}
