package com.liuzhenli.reader.ui.activity;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.liuzhenli.common.BitIntentDataManager;
import com.liuzhenli.common.constant.RxBusTag;
import com.liuzhenli.common.utils.AppSharedPreferenceHelper;
import com.liuzhenli.common.utils.ScreenUtils;
import com.liuzhenli.reader.ReaderApplication;
import com.liuzhenli.reader.network.AppComponent;
import com.liuzhenli.reader.reciever.BatteryAndTimeChangeReceiver;
import com.liuzhenli.reader.ui.contract.ReadContract;
import com.liuzhenli.reader.ui.presenter.ReadPresenter;
import com.liuzhenli.reader.utils.BatteryUtil;
import com.liuzhenli.reader.utils.IntentUtils;
import com.liuzhenli.reader.utils.ShareUtils;
import com.liuzhenli.reader.utils.ToastUtil;
import com.liuzhenli.reader.utils.storage.Backup;
import com.liuzhenli.reader.view.ReadLongPressPop;
import com.liuzhenli.reader.view.loading.DialogUtil;
import com.liuzhenli.reader.view.loading.ReplaceRuleDialog;
import com.liuzhenli.reader.view.menu.ReadBottomMenu;
import com.liuzhenli.reader.view.menu.ReadBrightnessMenu;
import com.liuzhenli.reader.view.menu.ReadSettingMenu;
import com.liuzhenli.reader.view.menu.ReadTopBarMenu;
import com.micoredu.readerlib.BaseReaderActivity;
import com.micoredu.readerlib.animation.PageAnimation;
import com.micoredu.readerlib.bean.BookShelfBean;
import com.micoredu.readerlib.bean.BookmarkBean;
import com.micoredu.readerlib.bean.OpenChapterBean;
import com.micoredu.readerlib.bean.ReplaceRuleBean;
import com.micoredu.readerlib.helper.BookshelfHelper;
import com.micoredu.readerlib.helper.ReadConfigManager;
import com.micoredu.readerlib.model.ReplaceRuleManager;
import com.micoredu.readerlib.page.PageLoaderNet;
import com.micoredu.readerlib.page.PageView;
import com.micoredu.readerlib.bean.BookChapterBean;
import com.micoredu.readerlib.page.PageLoader;
import com.micoredu.readerlib.utils.bar.BarHide;
import com.microedu.reader.R;
import com.microedu.theme.ViewUtil;
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;

import static com.liuzhenli.common.BitIntentDataManager.DATA_KEY;
import static com.liuzhenli.common.BitIntentDataManager.getInstance;
import static com.liuzhenli.common.constant.AppConstant.BookOpenFrom.OPEN_FROM_APP;
import static com.liuzhenli.reader.base.BaseActivity.START_SHEAR_ELE;

/**
 * describe:阅读页
 *
 * @author Liuzhenli on 2019-11-09 19:29
 * @since 1.0.0
 */
public class ReaderActivity extends BaseReaderActivity implements ReadContract.View {
    public static final String NOTE_URL = "noteUrl";
    public static final String CHAPTER_ID = "chapterID";
    public static final String PROGRESS = "progress";
    public static final String OPEN_FROM = "openFrom";

    private Boolean startShareAnim = false;

    @BindView(R.id.view_read_root)
    View mViewRoot;
    @BindView(R.id.menu_top_bar)
    ReadTopBarMenu mTopBar;

    @BindView(R.id.reader_page_view)
    PageView mPageView;
    /***底部设置菜单*/
    @BindView(R.id.view_read_bottom_menu)
    ReadBottomMenu mVBottomMenu;
    /***设置菜单**/
    @BindView(R.id.view_read_setting_menu)
    ReadSettingMenu mVSettingMenu;
    /***亮度**/
    @BindView(R.id.view_read_brightness_setting)
    ReadBrightnessMenu mVBrightnessSettingMenu;
    @BindView(R.id.fl_protect_eye_view)
    FrameLayout mVProtectEye;
    @BindView(R.id.readLongPress)
    ReadLongPressPop readLongPress;
    @BindView(R.id.cursor_left)
    ImageView cursorLeft;
    @BindView(R.id.cursor_right)
    ImageView cursorRight;

    private float mAlpha = 0F;
    private PageLoader mPageLoader;
    private int mCurrentChapterIndex;

    @Inject
    ReadPresenter mPresenter;

    /***章节id mChapterId  durChapterIndex*/
    private int mChapterId;
    private float mProgress;
    private int mOpenFrom;
    private String mDataKey;
    /***是否在书架里*/
    private boolean mIsInBookShelf = false;

    private String mNoteUrl;
    private BookShelfBean mBookShelf;
    private int lastX, lastY;
    private BatteryAndTimeChangeReceiver batteryAndTimeChangeReceiver;

    public static void start(Context context, String noteUrl, String chapterId, float progress, String openFrom) {
        Intent intent = new Intent(context, ReaderActivity.class);
        intent.putExtra(NOTE_URL, noteUrl);
        intent.putExtra(CHAPTER_ID, chapterId);
        intent.putExtra(PROGRESS, progress);
        intent.putExtra(OPEN_FROM, openFrom);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mNoteUrl = savedInstanceState.getString(NOTE_URL);
            mIsInBookShelf = savedInstanceState.getBoolean("isAdd");
        }
        mPresenter.attachView(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (batteryAndTimeChangeReceiver == null && mPageLoader != null) {
            batteryAndTimeChangeReceiver = new BatteryAndTimeChangeReceiver(mPageLoader);
            batteryAndTimeChangeReceiver.register(mContext, batteryAndTimeChangeReceiver);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mBookShelf != null) {
            outState.putString("noteUrl", mBookShelf.getNoteUrl());
            outState.putBoolean("isAdd", mIsInBookShelf);
            String key = String.valueOf(System.currentTimeMillis());
            String bookKey = "book" + key;
            getIntent().putExtra("bookKey", bookKey);
            BitIntentDataManager.getInstance().putData(bookKey, mBookShelf.clone());
            String chapterListKey = "chapterList" + key;
            getIntent().putExtra("chapterListKey", chapterListKey);
            BitIntentDataManager.getInstance().putData(chapterListKey, mPresenter.getChapterList());
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.act_read;
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void initData() {
        AppComponent appComponent = ReaderApplication.getInstance().getAppComponent();
        appComponent.inject(this);

        if (getIntent() != null) {
            startShareAnim = getIntent().getBooleanExtra(START_SHEAR_ELE, false);
            mOpenFrom = getIntent().getIntExtra(OPEN_FROM, OPEN_FROM_APP);
            mDataKey = getIntent().getStringExtra(DATA_KEY);
            mNoteUrl = getIntent().getStringExtra(NOTE_URL);
            mChapterId = getIntent().getIntExtra(CHAPTER_ID, 0);
            mProgress = getIntent().getFloatExtra(PROGRESS, 0);
        }
        if (mDataKey != null) {
            mBookShelf = (BookShelfBean) getInstance().getData(mDataKey);
            mNoteUrl = mBookShelf.getNoteUrl();
        }

        if (mBookShelf == null) {
            mBookShelf = BookshelfHelper.getBook(mNoteUrl);
        }

        if (mBookShelf == null) {
            List<BookShelfBean> allBook = BookshelfHelper.getAllBook();
            if (allBook != null && allBook.size() > 0) {
                mBookShelf = allBook.get(0);
            }
        }

        if (mBookShelf != null && mPresenter.getChapterList().isEmpty()) {
            mPresenter.setChapterList(BookshelfHelper.getChapterList(mBookShelf.getNoteUrl()));
            mIsInBookShelf = BookshelfHelper.isInBookShelf(mBookShelf.getNoteUrl());
        } else {
            finish();
        }

        if (mChapterId > 0) {
            mBookShelf.setDurChapter(mChapterId);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void configViews() {
        ButterKnife.bind(this);
        mTopBar.getToolBar().setNavigationOnClickListener(v -> onBackPressed());
        initPageView();
        //底部menu中 菜单按钮点击从底部弹出一个章节菜单
        mVBottomMenu.setOnMenuElementClickListener(new ReadBottomMenu.OnElementClickListener() {
            @Override
            public void onMenuClick() {
                BookChapterListActivity.start(mContext, mBookShelf, mPresenter.getChapterList(), false, true);
            }

            @Override
            public void onPreChapterClick() {
                mPageLoader.skipPreChapter();
            }

            @Override
            public void onNextChapterClick() {
                mPageLoader.skipNextChapter();
            }

            @Override
            public void onBrightnessClick() {
                ViewUtil.hideBottomView(mVBottomMenu);
                ViewUtil.hideTopView(mTopBar);
                ViewUtil.showBottomView(mVBrightnessSettingMenu);
            }

            @Override
            public void onNightModeClick() {
                boolean isNightTheme = ReadConfigManager.getInstance().getIsNightTheme();
                ReadConfigManager.getInstance().setIsNightTheme(!isNightTheme);
                ReadConfigManager.getInstance().initTextDrawableIndex();
                ReadConfigManager.getInstance().setTextDrawableIndex(ReadConfigManager.getInstance().getTextDrawableIndex());
                mPageView.setBackground(ReadConfigManager.getInstance().getTextBackground(mContext));
                mPageLoader.refreshUi();
                mVBottomMenu.changeTheme();
            }

            @Override
            public void onSettingClick() {
                ViewUtil.hideBottomView(mVBottomMenu);
                ViewUtil.hideTopView(mTopBar);
                ViewUtil.showBottomView(mVSettingMenu);
            }

            @Override
            public void onListenBookClick() {
                toast("听书");
            }

            @Override
            public void onChapterProgressed(int progress, boolean isStop) {

                mCurrentChapterIndex = (mPresenter.getChapterList().size() - 1) * progress / 100;
                if (isStop) {
                    mPageLoader.skipToChapter(mCurrentChapterIndex, 0);
                } else {
                    mTopBar.setChapterTitle(mPresenter.getChapterList().get(mCurrentChapterIndex).getDurChapterName());
                }
            }
        });
        mVBrightnessSettingMenu.setCallback(this, new ReadBrightnessMenu.CallBack() {
            @Override
            public void onProtectEyeClick(boolean on) {
                if (on) {
                    mAlpha = 3E-1F;
                } else {
                    mAlpha = 0f;
                }
                AppSharedPreferenceHelper.setProtectEyeReadMode(on);
                mVProtectEye.setAlpha(mAlpha);
                mVProtectEye.invalidate();
            }
        });
        mVSettingMenu.setReadSettingCallBack(new ReadSettingMenu.ReadSettingCallBack() {
            @Override
            public void onPageAnimChanged() {
                mPageLoader.setPageMode(PageAnimation.Mode.getPageMode(ReadConfigManager.getInstance().getPageMode()));
            }

            @Override
            public void onTextStyleChanged() {
                if (mPageLoader != null) {
                    mPageLoader.setTextSize();
                }
            }

            @Override
            public void onTypeFaceClicked() {
                mPresenter.getFontFile();
            }

            /***background changed*/
            @Override
            public void onBackGroundChanged() {
                if (mPageLoader != null) {
                    ReadConfigManager.getInstance().initTextDrawableIndex();
                    mPageView.setBackground(ReadConfigManager.getInstance().getTextBackground(mContext));
                    mPageLoader.refreshUi();
                }
            }
        });
        mVBrightnessSettingMenu.setProtectedEyeMode(AppSharedPreferenceHelper.tetProtectEyeReadMode());
        mVProtectEye.setAlpha(mAlpha);
        mVBrightnessSettingMenu.setBrightnessFollowSystem(ReadConfigManager.getInstance().getLightFollowSys());
        cursorLeft.setOnTouchListener(this);
        cursorRight.setOnTouchListener(this);
        mViewRoot.setOnTouchListener(this);
        mTopBar.setOnMenuElementClickListener(new ReadTopBarMenu.OnElementClickListener() {
            @Override
            public void onBackClick() {

            }

            @Override
            public void onMenuClick() {
                toast("已添加书签");
                String content = mPageLoader.getContent();
                String saveContent = null;
                if (!TextUtils.isEmpty(content) && content.trim().length() > 0) {
                    String[] split = content.trim().split("\n");
                    for (int i = 1; i < split.length; i++) {
                        if (split[i].length() > 0) {
                            saveContent = split[i];
                            break;
                        }
                    }
                }
                BookshelfHelper.saveBookmark(new BookmarkBean(System.currentTimeMillis(), mNoteUrl,
                        mBookShelf.getBookInfoBean().getName(), mBookShelf.getDurChapterName(), mBookShelf.getDurChapter(), mBookShelf.getDurChapterPage(), saveContent));
            }
        });
        setBrightness();
        mVBottomMenu.changeTheme();
    }

    /**
     * 阅读页面信息
     */
    private void initPageView() {
        mPageLoader = mPageView.getPageLoader((BaseReaderActivity) mContext, mBookShelf, new PageLoader.Callback() {
            @Override
            public List<BookChapterBean> getChapterList() {
                return mPresenter.getChapterList();
            }

            /**
             * @param pos:切换章节的序号
             */
            @Override
            public void onChapterChange(int pos) {
                if (mPresenter.getChapterList().isEmpty()) {
                    return;
                }
                if (pos >= mPresenter.getChapterList().size()) {
                    return;
                }
                mCurrentChapterIndex = pos;
                mTopBar.setChapterTitle(mPresenter.getChapterList().get(mCurrentChapterIndex).getDurChapterName());
            }

            /***
             * @param chapters：返回章节目录
             */
            @Override
            public void onCategoryFinish(List<BookChapterBean> chapters) {
                mPresenter.setChapterList(chapters);
                mBookShelf.setChapterListSize(chapters.size());
                mCurrentChapterIndex = mBookShelf.getDurChapter();
                mBookShelf.setDurChapterName(chapters.get(mBookShelf.getDurChapter()).getDurChapterName());
                mBookShelf.setLastChapterName(chapters.get(chapters.size() - 1).getDurChapterName());
                mTopBar.setChapterTitle(mPresenter.getChapterList().get(mCurrentChapterIndex).getDurChapterName());
            }

            @Override
            public void onPageCountChange(int count) {
            }

            @Override
            public void onPageChange(int chapterIndex, int pageIndex, boolean resetReadAloud) {
                //记录阅读位置
                mBookShelf.setDurChapter(chapterIndex);
                mBookShelf.setDurChapterPage(pageIndex);
                if (mIsInBookShelf) {
                    mPresenter.saveProgress(mBookShelf);
                }
            }

            @Override
            public void vipPop() {

            }
        });
        //电量信息
        mPageLoader.updateBattery(BatteryUtil.INSTANCE.getBatteryLevel(this));
        //书页点击
        mPageView.setTouchListener(new PageView.TouchListener() {
            @Override
            public void onTouch() {
                mPageView.post(new Runnable() {
                    @Override
                    public void run() {
                        ViewUtil.hideBottomView(mVBrightnessSettingMenu);
                        ViewUtil.hideBottomView(mVBottomMenu);
                        ViewUtil.hideTopView(mTopBar);
                        ViewUtil.hideBottomView(mVSettingMenu);
                    }
                });
            }

            @Override
            public void onTouchClearCursor() {
                clearSelect();
            }

            @Override
            public void onLongPress() {
                if (!mPageView.isRunning()) {
                    selectTextCursorShow();
                    showAction(cursorLeft);
                }
            }

            @Override
            public void center() {
                if (mVBottomMenu.getVisibility() != View.VISIBLE) {
                    ViewUtil.showBottomView(mVBottomMenu);
                    ViewUtil.showTopView(mTopBar);
                }
                mTopBar.getToolBar().setTitle(mBookShelf.getBookInfoBean().getName());
                mTopBar.getToolBar().setTitleTextColor(Color.WHITE);
            }

        });
        mPageLoader.refreshChapterList();
        initReadLongPressPop();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v.getId() == R.id.cursor_left || v.getId() == R.id.cursor_right) {
            int ea = event.getAction();
            switch (ea) {
                case MotionEvent.ACTION_DOWN:
                    // 获取触摸事件触摸位置的原始X坐标
                    lastX = (int) event.getRawX();
                    lastY = (int) event.getRawY();
                    readLongPress.setVisibility(View.INVISIBLE);
                    break;
                case MotionEvent.ACTION_MOVE:
                    int dx = (int) event.getRawX() - lastX;
                    int dy = (int) event.getRawY() - lastY;
                    int l = v.getLeft() + dx;
                    int b = v.getBottom() + dy;
                    int r = v.getRight() + dx;
                    int t = v.getTop() + dy;

                    v.layout(l, t, r, b);
                    lastX = (int) event.getRawX();
                    lastY = (int) event.getRawY();
                    v.postInvalidate();

                    //移动过程中要画线
                    mPageView.setSelectMode(PageView.SelectMode.SelectMoveForward);

                    int hh = cursorLeft.getHeight();
                    int ww = cursorLeft.getWidth();

                    if (v.getId() == R.id.cursor_left) {
                        mPageView.setFirstSelectTxtChar(mPageView.getCurrentTxtChar(lastX + ww, lastY - hh));
                    } else {
                        mPageView.setLastSelectTxtChar(mPageView.getCurrentTxtChar(lastX - ww, lastY - hh));
                    }

                    mPageView.invalidate();

                    break;
                case MotionEvent.ACTION_UP:
                    showAction(v);
                    break;
                default:
                    break;
            }
        }
        return true;
    }

    @Override
    public void showError(Exception e) {

    }

    @Override
    public void complete() {

    }

    @Override
    public void autoChangeSource() {
        mPresenter.changeBookSource();
    }

    /***
     * 没有上一页或者没有下一页
     */
    @Override
    public void showSnackBar(PageView pageView, String msg) {
        ToastUtil.showToast(msg);
    }

    @Override
    protected void initImmersionBar() {
        mImmersionBar.fullScreen(true);
        mImmersionBar.hideBar(BarHide.FLAG_HIDE_STATUS_BAR);
        mImmersionBar.init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.detachView();
        }
        unregisterReceiver(batteryAndTimeChangeReceiver);
        if (mPageLoader != null) {
            mPageLoader.closeBook();
        }
    }

    @Override
    public void showBookInfo(BookShelfBean bookInfo) {
        this.mBookShelf = bookInfo;
    }

    @Override
    public void showFontFile(File[] files) {
        if (files == null || files.length == 0) {
            //获取字体列表
            DialogUtil.showOneButtonDialog(mContext, "添加字体",
                    "本软件默认使用系统字体,若更换字体,请先下载字体文件,然后拷贝到根目录Fonts文件夹下即可.",
                    (dialog, index) -> {
                        dialog.dismiss();
                    }
            );
        } else {
            String[] fonts = new String[files.length + 1];
            int current = 0;
            String fontPath = ReadConfigManager.getInstance().getFontPath();
            fonts[0] = "默认字体";
            for (int i = 0; i < files.length; i++) {
                fonts[i + 1] = files[i].getName();
                if (TextUtils.equals(fontPath, files[i].getAbsolutePath())) {
                    current = i + 1;
                }
            }
            DialogUtil.sowSingleChoiceDialog(mContext, fonts, (dialog, index) -> {
                if (index == 0) {
                    ReadConfigManager.getInstance().setReadBookFont(null);
                } else {
                    ReadConfigManager.getInstance().setReadBookFont(files[index - 1].getAbsolutePath());
                }
                mPageLoader.refreshUi();
                dialog.dismiss();
            }, current);
        }
    }

    @Override
    public BookShelfBean getBookShelf() {
        return this.mBookShelf;
    }

    @Override
    public void showChangeBookSourceResult(BookShelfBean book) {
        this.mBookShelf = book;
        if (mPageLoader != null && mPageLoader instanceof PageLoaderNet) {
            ((PageLoaderNet) mPageLoader).changeSourceFinish(book);
        }
    }

    @Override
    public String getNoteUrl() {
        return mNoteUrl;
    }

    @Override
    public boolean isInBookShelf() {
        return mIsInBookShelf;
    }

    @Override
    public void onBackPressed() {
        Backup.INSTANCE.autoBack();
        //如果不在书架,提示添加书架
        if (!BookshelfHelper.isInBookShelf(mBookShelf.getNoteUrl())) {
            showSaveDialog();
        } else {
            mPresenter.saveProgress(mBookShelf);
            super.onBackPressed();
        }
    }

    private void showSaveDialog() {
        DialogUtil.showMessagePositiveDialog(mContext, "提示", "喜欢这本书就加入到书架吧?", "取消", new QMUIDialogAction.ActionListener() {
            @Override
            public void onClick(QMUIDialog dialog, int index) {
                ReaderActivity.super.onBackPressed();
            }
        }, "添加", new QMUIDialogAction.ActionListener() {
            @Override
            public void onClick(QMUIDialog dialog, int index) {
                mPresenter.saveProgress(mBookShelf);
                ReaderActivity.super.onBackPressed();
            }
        }, true);
    }

    /**
     * 长按选择按钮
     */
    private void initReadLongPressPop() {
        readLongPress.setListener(new ReadLongPressPop.OnBtnClickListener() {
            @Override
            public void copySelect() {
                ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText(null, mPageView.getSelectStr());
                if (clipboard != null) {
                    clipboard.setPrimaryClip(clipData);
                    toast("内容已经复制");
                }
                clearSelect();
            }

            @Override
            public void search() {
                IntentUtils.openBrowser(mContext, String.format("https://www.baidu.com/s?wd=%s", mPageView.getSelectStr()));
                clearSelect();
            }

            @Override
            public void replaceSelect() {
                ReplaceRuleBean oldRuleBean = new ReplaceRuleBean();
                oldRuleBean.setReplaceSummary(mPageView.getSelectStr().trim());
                oldRuleBean.setEnable(true);
                oldRuleBean.setRegex(mPageView.getSelectStr().trim());
                oldRuleBean.setIsRegex(false);
                oldRuleBean.setReplacement("");
                oldRuleBean.setSerialNumber(0);
                oldRuleBean.setUseTo(String.format("%s,%s", mBookShelf.getBookInfoBean().getName(), mBookShelf.getTag()));

                ReplaceRuleDialog.builder(mContext, oldRuleBean, mBookShelf, ReplaceRuleDialog.DefaultUI)
                        .setPositiveButton(replaceRuleBean1 ->
                                ReplaceRuleManager.saveData(replaceRuleBean1)
                                        .subscribe(new SingleObserver<Boolean>() {
                                            @Override
                                            public void onSubscribe(Disposable d) {

                                            }

                                            @Override
                                            public void onSuccess(Boolean aBoolean) {
                                                cursorLeft.setVisibility(View.INVISIBLE);
                                                cursorRight.setVisibility(View.INVISIBLE);
                                                readLongPress.setVisibility(View.INVISIBLE);
                                                mPageView.setSelectMode(PageView.SelectMode.Normal);
//                                                moDialogHUD.dismiss();
                                                refresh(false);
                                            }

                                            @Override
                                            public void onError(Throwable e) {

                                            }
                                        })).show();
            }

            @Override
            public void replaceSelectAd() {
                String selectString = mPageView.getSelectStr();
                if (selectString != null) {
                    String spacer = null;
                    String name = (mBookShelf.getBookInfoBean().getName());
                    if (name != null) {
                        if (name.trim().length() > 0) {
                            spacer = "|" + Pattern.quote(name.trim());
                        }
                    }
                    name = (mBookShelf.getBookInfoBean().getAuthor());
                    if (name != null) {
                        if (name.trim().length() > 0) {
                            if (spacer != null) {
                                spacer = spacer + "|" + Pattern.quote(name.trim());
                            } else {
                                spacer = "|" + Pattern.quote(name.trim());
                            }
                        }
                    }
                    String rule = "(\\s*\n\\s*" + spacer + ")";
                    selectString = ReplaceRuleManager.formateAdRule(
                            selectString.replaceAll(rule, "\n")
                    );

                    Log.i("selectString.afterAd2", selectString);

                }
                ReplaceRuleBean oldRuleBean = new ReplaceRuleBean();
                oldRuleBean.setReplaceSummary(getString(R.string.replace_ad) + "-" + mBookShelf.getTag());
                oldRuleBean.setEnable(true);
                oldRuleBean.setRegex(selectString);
                oldRuleBean.setIsRegex(false);
                oldRuleBean.setReplacement("");
                oldRuleBean.setSerialNumber(0);
                oldRuleBean.setUseTo(String.format(mBookShelf.getTag()));

                ReplaceRuleDialog.builder(mContext, oldRuleBean, mBookShelf, ReplaceRuleDialog.AddAdUI)
                        .setPositiveButton(replaceRuleBean1 ->
                                ReplaceRuleManager.mergeAdRules(replaceRuleBean1)
                                        .subscribe(new SingleObserver<Boolean>() {
                                            @Override
                                            public void onSubscribe(Disposable d) {

                                            }

                                            @Override
                                            public void onSuccess(Boolean aBoolean) {
                                                cursorLeft.setVisibility(View.INVISIBLE);
                                                cursorRight.setVisibility(View.INVISIBLE);
                                                readLongPress.setVisibility(View.INVISIBLE);
                                                mPageView.setSelectMode(PageView.SelectMode.Normal);
//                                                moDialogHUD.dismiss();
                                                refresh(false);
                                            }

                                            @Override
                                            public void onError(Throwable e) {

                                            }

                                        })).show();

            }

            @Override
            public void share() {
                ShareUtils.INSTANCE.share(mContext, mPageView.getSelectStr());
                clearSelect();
            }
        });
    }

    /**
     * 显示
     */
    private void selectTextCursorShow() {
        if (mPageView.getFirstSelectTxtChar() == null || mPageView.getLastSelectTxtChar() == null) {
            return;
        }
        //show Cursor on current position
        cursorShow();
        //set current word selected
        mPageView.invalidate();
    }

    private void cursorShow() {
        cursorLeft.setVisibility(View.VISIBLE);
        cursorRight.setVisibility(View.VISIBLE);
        int hh = cursorLeft.getHeight();
        int ww = cursorLeft.getWidth();
        if (mPageView.getFirstSelectTxtChar() != null) {
            cursorLeft.setX(mPageView.getFirstSelectTxtChar().getTopLeftPosition().x - ww);
            cursorLeft.setY(mPageView.getFirstSelectTxtChar().getBottomLeftPosition().y);
            cursorRight.setX(mPageView.getFirstSelectTxtChar().getBottomRightPosition().x);
            cursorRight.setY(mPageView.getFirstSelectTxtChar().getBottomRightPosition().y);
        }
    }


    public void showAction(View clickView) {

        int hh = cursorLeft.getHeight();
        int ww = cursorLeft.getWidth();
        if (mPageView.getFirstSelectTxtChar() != null && mPageView.getLastSelectTxtChar() != null) {
            cursorLeft.setX(Objects.requireNonNull(mPageView.getFirstSelectTxtChar().getTopLeftPosition()).x - ww);
            cursorLeft.setY(Objects.requireNonNull(mPageView.getFirstSelectTxtChar().getBottomLeftPosition()).y);
            cursorRight.setX(Objects.requireNonNull(mPageView.getLastSelectTxtChar().getBottomRightPosition()).x);
            cursorRight.setY(mPageView.getLastSelectTxtChar().getBottomRightPosition().y);
        }

        readLongPress.setVisibility(View.VISIBLE);
        //弹窗的宽度
        int width = readLongPress.getWidth();
        //弹窗的起始位置
        int x = (int) (cursorLeft.getX() / 2 + cursorLeft.getWidth() / 2 + cursorRight.getX() / 2 + cursorLeft.getWidth() / 2);
        //1.如果选择的文本行数大于1,则靠中间防止  2.如果太靠右，则靠左
        int[] aa = ScreenUtils.getScreenSize(this);
        if (mPageView.getSelectLines() > 1) {
            readLongPress.setX(ScreenUtils.getScreenWidth(ReaderActivity.this) / 2f - readLongPress.getWidth() / 2f);
        } else if (x < width / 2 + ScreenUtils.dpToPx(8)) {
            readLongPress.setX(ScreenUtils.dpToPx(8));
        } else if (x > (aa[0] - width / 2 - ScreenUtils.dpToPx(8))) {
            readLongPress.setX(aa[0] - width - ScreenUtils.dpToPx(8));
        } else {
            readLongPress.setX(x - width / 2f);
        }

        //如果太靠上
        if ((cursorLeft.getY() - ScreenUtils.spToPx(ReadConfigManager.getInstance().getTextSize()) - ScreenUtils.dpToPx(60)) < 0) {
            readLongPress.setY(cursorLeft.getY() - ScreenUtils.spToPx(ReadConfigManager.getInstance().getTextSize()));
        } else {
            readLongPress.setY(cursorLeft.getY() - ScreenUtils.spToPx(ReadConfigManager.getInstance().getTextSize()) - ScreenUtils.dpToPx(60));
        }
    }

    /**
     * 刷新
     */
    public void refresh(boolean recreate) {
        if (recreate) {
            recreate();
        } else {
            mViewRoot.setBackground(ReadConfigManager.getInstance().getTextBackground(this));
            if (mPageLoader != null) {
                mPageLoader.refreshUi();
            }
            //readInterfacePop.setBg();
        }
    }


    private void setBrightness() {
        if (ReadConfigManager.getInstance().getLightFollowSys()) {
            ScreenUtils.setScreenBrightness(this, -1);
        } else {
            ScreenUtils.setScreenBrightness(this, ReadConfigManager.getInstance().getLight());
        }
    }

    private void clearSelect() {
        cursorLeft.setVisibility(View.INVISIBLE);
        cursorRight.setVisibility(View.INVISIBLE);
        readLongPress.setVisibility(View.INVISIBLE);
        mPageView.clearSelect();
    }

    @Subscribe(thread = EventThread.MAIN_THREAD, tags = {@Tag(RxBusTag.SKIP_TO_CHAPTER)})
    public void skipToChapter(OpenChapterBean openChapterBean) {
        mPageLoader.skipToChapter(openChapterBean.getChapterIndex(), openChapterBean.getPageIndex());
    }
}
