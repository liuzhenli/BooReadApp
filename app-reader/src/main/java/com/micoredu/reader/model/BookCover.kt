package com.micoredu.reader.model

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.microedu.lib.reader.R
import com.liuzhenli.common.utils.PreferKey
import com.micoredu.reader.bean.BaseSource
import com.micoredu.reader.bean.Book
import com.micoredu.reader.help.CacheManager
import com.micoredu.reader.help.DefaultData
import com.liuzhenli.common.utils.AppConfig
import com.liuzhenli.common.utils.getPrefBoolean
import com.liuzhenli.common.utils.getPrefString
import com.liuzhenli.common.widget.BitmapUtils
import com.micoredu.reader.help.glide.BlurTransformation
import com.micoredu.reader.help.glide.ImageLoader
import com.micoredu.reader.help.glide.OkHttpModelLoader
import com.micoredu.reader.model.analyzeRule.AnalyzeRule
import com.micoredu.reader.model.analyzeRule.AnalyzeUrl
import com.micoredu.reader.utils.*
import splitties.init.appCtx

object BookCover {

    private const val coverRuleConfigKey = "legadoCoverRuleConfig"
    var drawBookName = true
        private set
    var drawBookAuthor = true
        private set
    lateinit var defaultDrawable: Drawable
        private set
    val coverRuleConfig: CoverRuleConfig
        get() {
            return GSON.fromJsonObject<CoverRuleConfig>(CacheManager.get(coverRuleConfigKey))
                .getOrNull()
                ?: DefaultData.coverRuleConfig
        }


    init {
        upDefaultCover()
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    fun upDefaultCover() {
        val isNightTheme = AppConfig.isNightTheme
        drawBookName = if (isNightTheme) {
            appCtx.getPrefBoolean(PreferKey.coverShowNameN, true)
        } else {
            appCtx.getPrefBoolean(PreferKey.coverShowName, true)
        }
        drawBookAuthor = if (isNightTheme) {
            appCtx.getPrefBoolean(PreferKey.coverShowAuthorN, true)
        } else {
            appCtx.getPrefBoolean(PreferKey.coverShowAuthor, true)
        }
        val key = if (isNightTheme) PreferKey.defaultCoverDark else PreferKey.defaultCover
        val path = appCtx.getPrefString(key)
        if (path.isNullOrBlank()) {
            defaultDrawable = appCtx.resources.getDrawable(R.drawable.book_cover, null)
            return
        }
        defaultDrawable = kotlin.runCatching {
            BitmapDrawable(appCtx.resources, BitmapUtils.decodeBitmap(path, 600, 900))
        }.getOrDefault(appCtx.resources.getDrawable(R.drawable.book_cover, null))
    }

    /**
     * 加载封面
     */
    fun load(
        context: Context,
        path: String?,
        loadOnlyWifi: Boolean = false,
        sourceOrigin: String? = null
    ): RequestBuilder<Drawable> {
        if (AppConfig.useDefaultCover) {
            return ImageLoader.load(context, defaultDrawable)
                .centerCrop()
        }
        var options = RequestOptions().set(OkHttpModelLoader.loadOnlyWifiOption, loadOnlyWifi)
        if (sourceOrigin != null) {
            options = options.set(OkHttpModelLoader.sourceOriginOption, sourceOrigin)
        }
        return ImageLoader.load(context, path)
            .apply(options)
            .placeholder(defaultDrawable)
            .error(defaultDrawable)
            .centerCrop()
    }

    /**
     * 加载模糊封面
     */
    fun loadBlur(
        context: Context,
        path: String?,
        loadOnlyWifi: Boolean = false,
        sourceOrigin: String? = null
    ): RequestBuilder<Drawable> {
        val loadBlur = ImageLoader.load(context, defaultDrawable)
            .transform(BlurTransformation(25), CenterCrop())
        if (AppConfig.useDefaultCover) {
            return loadBlur
        }
        var options = RequestOptions().set(OkHttpModelLoader.loadOnlyWifiOption, loadOnlyWifi)
        if (sourceOrigin != null) {
            options = options.set(OkHttpModelLoader.sourceOriginOption, sourceOrigin)
        }
        return ImageLoader.load(context, path)
            .apply(options)
            .transform(BlurTransformation(25), CenterCrop())
            .transition(DrawableTransitionOptions.withCrossFade(1500))
            .thumbnail(loadBlur)
    }

    suspend fun searchCover(book: Book): String? {
        val config = coverRuleConfig
        if (!config.enable || config.searchUrl.isBlank() || config.coverRule.isBlank()) {
            return null
        }
        val analyzeUrl = AnalyzeUrl(
            config.searchUrl,
            book.name,
            source = config,
            headerMapF = config.getHeaderMap()
        )
        val res = analyzeUrl.getStrResponseAwait()
        val analyzeRule = AnalyzeRule(book)
        analyzeRule.setContent(res.body)
        analyzeRule.setRedirectUrl(res.url)
        return analyzeRule.getString(config.coverRule, isUrl = true)
    }

    fun saveCoverRuleConfig(config: CoverRuleConfig) {
        val json = GSON.toJson(config)
        CacheManager.put(coverRuleConfigKey, json)
    }

    fun delCoverRuleConfig() {
        CacheManager.delete(coverRuleConfigKey)
    }

    data class CoverRuleConfig(
        var enable: Boolean = true,
        var searchUrl: String,
        var coverRule: String,
        override var concurrentRate: String? = null,
        override var loginUrl: String? = null,
        override var loginUi: String? = null,
        override var header: String? = null,
        override var enabledCookieJar: Boolean? = false,
    ) : BaseSource {

        override fun getTag(): String {
            return searchUrl
        }

        override fun getKey(): String {
            return searchUrl
        }
    }

}