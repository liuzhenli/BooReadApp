package com.micoredu.reader.help.glide

import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.HttpException
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.data.DataFetcher
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.util.ContentLengthInputStream
import com.bumptech.glide.util.Preconditions
import com.micoredu.reader.dao.appDb
import com.micoredu.reader.bean.BaseSource
import com.liuzhenli.common.exception.NoStackTraceException
import com.micoredu.reader.help.http.addHeaders
import com.micoredu.reader.help.http.okHttpClient
import com.liuzhenli.common.utils.isWifiConnect
import com.micoredu.reader.utils.ImageUtils
import okhttp3.Call
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import splitties.init.appCtx
import java.io.IOException
import java.io.InputStream
import java.io.ByteArrayInputStream


class OkHttpStreamFetcher(private val url: GlideUrl, private val options: Options) :
    DataFetcher<InputStream>, okhttp3.Callback {
    private var stream: InputStream? = null
    private var responseBody: ResponseBody? = null
    private var callback: DataFetcher.DataCallback<in InputStream>? = null
    private var source: BaseSource? = null

    @Volatile
    private var call: Call? = null

    override fun loadData(priority: Priority, callback: DataFetcher.DataCallback<in InputStream>) {
        val loadOnlyWifi = options.get(OkHttpModelLoader.loadOnlyWifiOption) ?: false
        if (loadOnlyWifi && !appCtx.isWifiConnect) {
            callback.onLoadFailed(NoStackTraceException("只在wifi加载图片"))
            return
        }
        val requestBuilder: Request.Builder = Request.Builder().url(url.toStringUrl())
        val headerMap = HashMap<String, String>()
        options.get(OkHttpModelLoader.sourceOriginOption)?.let { sourceUrl ->
            source = appDb.bookSourceDao.getBookSource(sourceUrl)
                ?: appDb.rssSourceDao.getByKey(sourceUrl)
            source?.getHeaderMap(true)?.let {
                headerMap.putAll(it)
            }
        }
        headerMap.putAll(url.headers)
        requestBuilder.addHeaders(headerMap)
        val request: Request = requestBuilder.build()
        this.callback = callback
        call = okHttpClient.newCall(request)
        call?.enqueue(this)
    }

    override fun cleanup() {
        kotlin.runCatching {
            stream?.close()
        }
        responseBody?.close()
        callback = null
    }

    override fun cancel() {
        call?.cancel()
    }

    override fun getDataClass(): Class<InputStream> {
        return InputStream::class.java
    }

    override fun getDataSource(): DataSource {
        return DataSource.REMOTE
    }

    override fun onFailure(call: Call, e: IOException) {
        callback?.onLoadFailed(e)
    }

    override fun onResponse(call: Call, response: Response) {
        responseBody = response.body
        if (response.isSuccessful) {
            val decodeResult = ImageUtils.decode(
                url.toStringUrl(), responseBody!!.byteStream(),
                isCover = true, source
            )
            if (decodeResult == null) {
                callback?.onLoadFailed(NoStackTraceException("封面二次解密失败"))
            } else {
                val contentLength: Long = if (decodeResult is ByteArrayInputStream) decodeResult.available().toLong() else Preconditions.checkNotNull(responseBody).contentLength()
                stream = ContentLengthInputStream.obtain(decodeResult, contentLength)
                callback?.onDataReady(stream)
            }
        } else {
            callback?.onLoadFailed(HttpException(response.message, response.code))
        }
    }
}