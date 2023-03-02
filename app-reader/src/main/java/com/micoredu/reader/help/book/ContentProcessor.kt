package com.micoredu.reader.help.book

import com.github.liuyueyi.quick.transfer.ChineseUtils
import com.liuzhenli.common.utils.AppLog
import com.micoredu.reader.bean.Book
import com.micoredu.reader.bean.BookChapter
import com.micoredu.reader.bean.ReplaceRule
import com.liuzhenli.common.exception.RegexTimeoutException
import com.liuzhenli.common.utils.AppConfig
import com.liuzhenli.common.utils.ToastUtil
import com.liuzhenli.common.utils.stackTraceStr
import com.micoredu.reader.dao.appDb
import com.micoredu.reader.help.config.ReadBookConfig
import kotlinx.coroutines.CancellationException
import java.lang.ref.WeakReference
import java.util.concurrent.CopyOnWriteArrayList
import java.util.regex.Pattern
import com.micoredu.reader.utils.replace
import splitties.init.appCtx

class ContentProcessor private constructor(
    private val bookName: String,
    private val bookOrigin: String
) {

    companion object {
        private val processors = hashMapOf<String, WeakReference<ContentProcessor>>()
        var enableRemoveSameTitle = true

        fun get(bookName: String, bookOrigin: String): ContentProcessor {
            val processorWr = processors[bookName + bookOrigin]
            var processor: ContentProcessor? = processorWr?.get()
            if (processor == null) {
                processor = ContentProcessor(bookName, bookOrigin)
                processors[bookName + bookOrigin] = WeakReference(processor)
            }
            return processor
        }

        fun upReplaceRules() {
            processors.forEach {
                it.value.get()?.upReplaceRules()
            }
        }

    }

    private val titleReplaceRules = CopyOnWriteArrayList<ReplaceRule>()
    private val contentReplaceRules = CopyOnWriteArrayList<ReplaceRule>()

    init {
        upReplaceRules()
    }

    fun upReplaceRules() {
        titleReplaceRules.run {
            clear()
            addAll(appDb.replaceRuleDao.findEnabledByTitleScope(bookName, bookOrigin))
        }
        contentReplaceRules.run {
            clear()
            addAll(appDb.replaceRuleDao.findEnabledByContentScope(bookName, bookOrigin))
        }
    }

    fun getTitleReplaceRules(): List<ReplaceRule> {
        return titleReplaceRules
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun getContentReplaceRules(): List<ReplaceRule> {
        return contentReplaceRules
    }

    suspend fun getContent(
        book: Book,
        chapter: BookChapter,
        content: String,
        includeTitle: Boolean = true,
        useReplace: Boolean = true,
        chineseConvert: Boolean = true,
        reSegment: Boolean = true
    ): BookContent {
        var mContent = content
        var sameTitleRemoved = false
        if (content != "null") {
            //去除重复标题
            if (enableRemoveSameTitle && BookHelp.removeSameTitle(book, chapter)) try {
                val name = Pattern.quote(book.name)
                var title = Pattern.quote(chapter.title)
                var matcher = Pattern.compile("^(\\s|\\p{P}|${name})*${title}(\\s)*")
                    .matcher(mContent)
                if (matcher.find()) {
                    mContent = mContent.substring(matcher.end())
                    sameTitleRemoved = true
                } else if (useReplace) {
                    title = Pattern.quote(
                        chapter.getDisplayTitle(
                            contentReplaceRules,
                            chineseConvert = false
                        )
                    )
                    matcher = Pattern.compile("^(\\s|\\p{P}|${name})*${title}(\\s)*")
                        .matcher(mContent)
                    if (matcher.find()) {
                        mContent = mContent.substring(matcher.end())
                        sameTitleRemoved = true
                    }
                }
            } catch (e: Exception) {
                AppLog.put("去除重复标题出错\n${e.localizedMessage}", e)
            }
            if (reSegment && book.getReSegment()) {
                //重新分段
                mContent = ContentHelp.reSegment(mContent, chapter.title)
            }
            if (chineseConvert) {
                //简繁转换
                try {
                    when (AppConfig.chineseConverterType) {
                        1 -> mContent = ChineseUtils.t2s(mContent)
                        2 -> mContent = ChineseUtils.s2t(mContent)
                    }
                } catch (e: Exception) {
                    ToastUtil.showToast("简繁转换出错")
                }
            }
            if (useReplace && book.getUseReplaceRule()) {
                //替换
                mContent = replaceContent(mContent)
            }
        }
        if (includeTitle) {
            //重新添加标题
            mContent = chapter.getDisplayTitle(
                getTitleReplaceRules(),
                useReplace = useReplace && book.getUseReplaceRule()
            ) + "\n" + mContent
        }
        val contents = arrayListOf<String>()
        mContent.split("\n").forEach { str ->
            val paragraph = str.trim {
                it.code <= 0x20 || it == '　'
            }
            if (paragraph.isNotEmpty()) {
                if (contents.isEmpty() && includeTitle) {
                    contents.add(paragraph)
                } else {
                    contents.add("${ReadBookConfig.paragraphIndent}$paragraph")
                }
            }
        }
        return BookContent(sameTitleRemoved, contents)
    }

    private suspend fun replaceContent(content: String): String {
        var mContent = content
        mContent = mContent.lines().joinToString("\n") { it.trim() }
        getContentReplaceRules().forEach { item ->
            if (item.pattern.isNotEmpty()) {
                try {
                    mContent = if (item.isRegex) {
                        mContent.replace(
                            item.pattern.toRegex(),
                            item.replacement,
                            item.timeoutMillisecond
                        )
                    } else {
                        mContent.replace(item.pattern, item.replacement)
                    }
                } catch (e: RegexTimeoutException) {
                    item.isEnabled = false
                    appDb.replaceRuleDao.update(item)
                    return item.name + e.stackTraceStr
                } catch (e: CancellationException) {
                    return mContent
                } catch (e: Exception) {
                    AppLog.put("替换净化: 规则 ${item.name}替换出错\n替换内容\n${mContent}", e)
                   ToastUtil.showToast("替换净化: 规则 ${item.name}替换出错")
                }
            }
        }
        return mContent
    }

}

