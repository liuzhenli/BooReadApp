package com.micoredu.reader.bean;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.Gson;
import com.liuzhenli.common.SharedPreferencesUtil;
import com.liuzhenli.common.constant.AppConstant;
import com.liuzhenli.common.constant.BookType;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 书架item Bean
 */

@Entity(tableName = "bookShelf", primaryKeys = "noteUrl")
public class BookShelfBean implements Cloneable, BaseBookBean {
    @Ignore
    public static final String LOCAL_TAG = "loc_book";
    @Ignore
    private String errorMsg;
    @Ignore
    private boolean isLoading;
    /***对应BookInfoBean noteUrl  本地书路径**/
    @NonNull
    private String noteUrl = "";
    /*** 当前章节 （包括番外）**/
    private int durChapter = 0;
    /***当前章节位置   用页码**/
    private int durChapterPage = 0;
    /***最后阅读时间**/
    private long finalDate = System.currentTimeMillis();
    /***是否有更新**/
    private boolean hasUpdate = false;
    /***更新章节数**/
    private int newChapters = 0;
    private String tag;
    /***手动排序**/
    private int serialNumber = 0;
    /***章节最后更新时间**/
    private long finalRefreshData = System.currentTimeMillis();
    private int group = 0;
    private String durChapterName;
    private String lastChapterName;
    private int chapterListSize = 0;
    private String customCoverPath;
    private boolean allowUpdate = true;
    private boolean useReplaceRule = true;
    private String variable;
    private boolean replaceEnable = SharedPreferencesUtil.getInstance().getBoolean("replaceEnableDefault", true);

    @Ignore
    private Map<String, String> variableMap;

    @Ignore
    private BookInfoBean bookInfoBean;

    public BookShelfBean() {

    }

    @Override
    public Object clone() {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(this);
            return gson.fromJson(json, BookShelfBean.class);
        } catch (Exception ignored) {
        }
        return this;
    }

    @Override
    public String getVariable() {
        return this.variable;
    }

    @Override
    public void setVariable(String variable) {
        this.variable = variable;
    }

    @Override
    public void putVariable(String key, String value) {
        if (variableMap == null) {
            variableMap = new HashMap<>();
        }
        variableMap.put(key, value);
        variable = new Gson().toJson(variableMap);
    }

    @Override
    public Map<String, String> getVariableMap() {
        if (variableMap == null && !TextUtils.isEmpty(variable)) {
            variableMap = new Gson().fromJson(variable, AppConstant.MAP_STRING);
        }
        return variableMap;
    }

    @NonNull
    @Override
    public String getNoteUrl() {
        return noteUrl;
    }

    @Override
    public void setNoteUrl(@NonNull String noteUrl) {
        this.noteUrl = noteUrl;
    }

    public int getDurChapter() {
        return durChapter < 0 ? 0 : durChapter;
    }

    public int getDurChapter(int chapterListSize) {
        if (durChapter < 0 | chapterListSize == 0) {
            return 0;
        } else if (durChapter >= chapterListSize) {
            return chapterListSize - 1;
        }
        return durChapter;
    }

    public int getDurChapterPage() {
        return durChapterPage < 0 ? 0 : durChapterPage;
    }

    public long getFinalDate() {
        return finalDate;
    }

    @Override
    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public BookInfoBean getBookInfoBean() {
        if (bookInfoBean == null) {
            bookInfoBean = new BookInfoBean();
            bookInfoBean.setNoteUrl(noteUrl);
            bookInfoBean.setTag(tag);
        }
        return bookInfoBean;
    }

    public void setBookInfoBean(BookInfoBean bookInfoBean) {
        this.bookInfoBean = bookInfoBean;
    }

    public boolean getHasUpdate() {
        return hasUpdate && !isAudio();
    }

    public int getNewChapters() {
        return newChapters;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public int getSerialNumber() {
        return this.serialNumber;
    }

    public long getFinalRefreshData() {
        return this.finalRefreshData;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }

    public int getGroup() {
        return this.group;
    }

    public void setDurChapter(int durChapter) {
        this.durChapter = durChapter;
    }

    public void setDurChapterPage(int durChapterPage) {
        this.durChapterPage = durChapterPage;
    }

    public void setFinalDate(long finalDate) {
        this.finalDate = finalDate;
    }

    public void setHasUpdate(boolean hasUpdate) {
        this.hasUpdate = hasUpdate;
    }

    public void setNewChapters(int newChapters) {
        this.newChapters = newChapters;
    }

    public void setSerialNumber(int serialNumber) {
        this.serialNumber = serialNumber;
    }

    public void setFinalRefreshData(long finalRefreshData) {
        this.finalRefreshData = finalRefreshData;
    }

    public void setGroup(int group) {
        this.group = group;
    }

    public String getDurChapterName() {
        return this.durChapterName;
    }

    public void setDurChapterName(String durChapterName) {
        this.durChapterName = durChapterName;
    }

    public String getLastChapterName() {
        return this.lastChapterName;
    }

    public void setLastChapterName(String lastChapterName) {
        this.lastChapterName = lastChapterName;
    }

    public int getUnreadChapterNum() {
        int num = getChapterListSize() - getDurChapter() - 1;
        return num < 0 ? 0 : num;
    }

    public int getChapterListSize() {
        return this.chapterListSize;
    }

    public void setChapterListSize(int chapterListSize) {
        this.chapterListSize = chapterListSize;
    }

    public String getCustomCoverPath() {
        return this.customCoverPath;
    }

    public void setCustomCoverPath(String customCoverPath) {
        this.customCoverPath = customCoverPath;
    }

    public boolean getAllowUpdate() {
        return allowUpdate;
    }

    public void setAllowUpdate(boolean allowUpdate) {
        this.allowUpdate = allowUpdate;
    }

    public boolean getUseReplaceRule() {
        return this.useReplaceRule;
    }

    public void setUseReplaceRule(boolean useReplaceRule) {
        this.useReplaceRule = useReplaceRule;
    }

    public boolean isAudio() {
        return Objects.equals(bookInfoBean.getBookSourceType(), BookType.AUDIO);
    }

    public boolean getReplaceEnable() {
        return replaceEnable ? SharedPreferencesUtil.getInstance().getBoolean("replaceEnableDefault", true) : replaceEnable;
    }

    public void setReplaceEnable(boolean replaceEnable) {
        this.replaceEnable = replaceEnable;
    }
}