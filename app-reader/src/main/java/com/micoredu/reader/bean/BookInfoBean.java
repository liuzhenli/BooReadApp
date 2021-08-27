package com.micoredu.reader.bean;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.Gson;
import com.liuzhenli.common.FileHelp;
import com.liuzhenli.common.constant.BookType;
import com.liuzhenli.common.encript.MD5Utils;
import com.liuzhenli.common.utils.StringUtils;
import com.micoredu.reader.R;
import com.micoredu.reader.helper.BookshelfHelper;
import com.micoredu.reader.helper.AppReaderDbHelper;
import com.micoredu.reader.page.PageLoaderEpub;

import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.util.Objects;

/**
 * 书本信息
 */
@Entity(tableName = "bookInfo", primaryKeys = "noteUrl")
public class BookInfoBean implements Cloneable, Serializable {
    @Ignore
    private static final long serialVersionUID = 126832618391379238L;
    /***小说名*/
    private String name;
    private String tag;
    /***如果是来源网站,则小说根地址,如果是本地则是小说本地MD5*/
    @NonNull
    private String noteUrl = "";
    /***章节目录地址,本地目录正则*/
    private String chapterUrl;
    /***章节最后更新时间*/
    private long finalRefreshData;
    /***小说封面*/
    private String coverUrl;
    /***作者笔名*/
    private String author;
    /***图书简介**/
    private String introduce;
    /***来源  本地或者网站名字*/
    private String origin;
    /***编码*/
    private String charset;
    private String bookSourceType;
    @Ignore
    private String bookInfoHtml;
    @Ignore
    private String chapterListHtml;

    public BookInfoBean() {
    }

    @Override
    protected Object clone() {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(this);
            return gson.fromJson(json, BookInfoBean.class);
        } catch (Exception ignored) {
        }
        return this;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    @NonNull
    public String getNoteUrl() {
        return noteUrl;
    }

    public void setNoteUrl(@NonNull String noteUrl) {
        this.noteUrl = noteUrl;
    }

    public String getChapterUrl() {
        return chapterUrl;
    }

    public void setChapterUrl(String chapterUrl) {
        this.chapterUrl = chapterUrl;
    }

    public long getFinalRefreshData() {
        return finalRefreshData;
    }

    public void setFinalRefreshData(long finalRefreshData) {
        this.finalRefreshData = finalRefreshData;
    }

    public String getCoverUrl() {
        if (isEpub() && (TextUtils.isEmpty(coverUrl) || !(new File(coverUrl)).exists())) {
            extractEpubCoverImage();
            return "";
        }
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = BookshelfHelper.formatAuthor(author);
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public String getOrigin() {
        return TextUtils.isEmpty(origin) && tag.equals(BookShelfBean.LOCAL_TAG) ? StringUtils.getString(R.string.local) : origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getCharset() {
        return this.charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    private void extractEpubCoverImage() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    FileHelp.createFolderIfNotExists(coverUrl);
                    Bitmap cover = BitmapFactory.decodeStream(Objects.requireNonNull(PageLoaderEpub.readBook(new File(noteUrl))).getCoverImage().getInputStream());
                    String md5Path = FileHelp.getCachePath() + File.separator + "cover" + File.separator + MD5Utils.strToMd5By16(noteUrl) + ".jpg";
                    FileOutputStream out = new FileOutputStream(new File(md5Path));
                    cover.compress(Bitmap.CompressFormat.JPEG, 90, out);
                    out.flush();
                    out.close();
                    setCoverUrl(md5Path);
                    AppReaderDbHelper.getInstance().getDatabase().getBookInfoDao().insertOrReplace(BookInfoBean.this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private boolean isEpub() {
        return TextUtils.equals(tag, BookShelfBean.LOCAL_TAG) && noteUrl.toLowerCase().matches(".*\\.epub$");
    }

    public String getBookSourceType() {
        return this.bookSourceType;
    }

    public void setBookSourceType(String bookSourceType) {
        this.bookSourceType = bookSourceType;
    }

    public boolean isAudio() {
        return Objects.equals(BookType.AUDIO, bookSourceType);
    }

    public String getBookInfoHtml() {
        return bookInfoHtml;
    }

    public void setBookInfoHtml(String bookInfoHtml) {
        this.bookInfoHtml = bookInfoHtml;
    }

    public String getChapterListHtml() {
        return chapterListHtml;
    }

    public void setChapterListHtml(String chapterListHtml) {
        this.chapterListHtml = chapterListHtml;
    }
}