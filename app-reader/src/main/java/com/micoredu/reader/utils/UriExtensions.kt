package com.micoredu.reader.utils

import android.Manifest
import android.content.Context
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.Fragment
import com.liuzhenli.common.exception.NoStackTraceException
import com.liuzhenli.common.utils.*
import com.microedu.lib.reader.R
import okhttp3.MediaType
import okhttp3.RequestBody
import okio.BufferedSink
import okio.source
import splitties.init.appCtx
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.nio.charset.Charset

fun Uri.isContentScheme() = this.scheme == "content"

fun Uri.isFileScheme() = this.scheme == "file"

/**
 * 读取URI
 */
fun AppCompatActivity.readUri(
    uri: Uri?,
    success: (fileDoc: FileDoc, inputStream: InputStream) -> Unit
) {
    uri ?: return
    try {
        if (uri.isContentScheme()) {
            val doc = DocumentFile.fromSingleUri(this, uri)
            doc ?: throw NoStackTraceException("未获取到文件")
            val fileDoc = FileDoc.fromDocumentFile(doc)
            contentResolver.openInputStream(uri)!!.use { inputStream ->
                success.invoke(fileDoc, inputStream)
            }
        } else {
            PermissionUtil.requestPermission(
                this,
                object : PermissionUtil.PermissionObserver() {
                    override fun onGranted(permissions: MutableList<String>?, all: Boolean) {
                        RealPathUtil.getPath(this@readUri, uri)?.let { path ->
                            val file = File(path)
                            val fileDoc = FileDoc.fromFile(file)
                            FileInputStream(file).use { inputStream ->
                                success.invoke(fileDoc, inputStream)
                            }
                        }
                    }
                },
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }
    } catch (e: Exception) {
        e.printOnDebug()
        ToastUtil.showToast(e.localizedMessage ?: "read uri error")
        if (e is SecurityException) {
            throw e
        }
    }
}

/**
 * 读取URI
 */
fun Fragment.readUri(uri: Uri?, success: (fileDoc: FileDoc, inputStream: InputStream) -> Unit) {
    uri ?: return
    try {
        if (uri.isContentScheme()) {
            val doc = DocumentFile.fromSingleUri(requireContext(), uri)
            doc ?: throw NoStackTraceException("未获取到文件")
            val fileDoc = FileDoc.fromDocumentFile(doc)
            requireContext().contentResolver.openInputStream(uri)!!.use { inputStream ->
                success.invoke(fileDoc, inputStream)
            }
        } else {

            PermissionUtil.requestPermission(
                requireContext(),
                object : PermissionUtil.PermissionObserver() {
                    override fun onGranted(permissions: MutableList<String>?, all: Boolean) {
                        RealPathUtil.getPath(requireContext(), uri)?.let { path ->
                            val file = File(path)
                            val fileDoc = FileDoc.fromFile(file)
                            FileInputStream(file).use { inputStream ->
                                success.invoke(fileDoc, inputStream)
                            }
                        }
                    }

                    override fun onDenied(permissions: MutableList<String>?, never: Boolean) {
                        super.onDenied(permissions, never)
                        ToastUtil.showToast(R.string.get_storage_per)
                    }
                },
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }
    } catch (e: Exception) {
        e.printOnDebug()
        ToastUtil.showToast(e.localizedMessage ?: "read uri error")
    }
}

class PermissionsCompat {

}

@Throws(Exception::class)
fun Uri.readBytes(context: Context): ByteArray {
    return if (this.isContentScheme()) {
        context.contentResolver.openInputStream(this)?.let {
            val len: Int = it.available()
            val buffer = ByteArray(len)
            it.read(buffer)
            it.close()
            return buffer
        } ?: throw NoStackTraceException("打开文件失败\n${this}")
    } else {
        val path = RealPathUtil.getPath(context, this)
        if (path?.isNotEmpty() == true) {
            File(path).readBytes()
        } else {
            throw NoStackTraceException("获取文件真实地址失败\n${this.path}")
        }
    }
}

@Throws(Exception::class)
fun Uri.readText(context: Context): String {
    readBytes(context).let {
        return String(it)
    }
}

@Throws(Exception::class)
fun Uri.writeBytes(
    context: Context,
    byteArray: ByteArray
): Boolean {
    if (this.isContentScheme()) {
        context.contentResolver.openOutputStream(this)?.let {
            it.write(byteArray)
            it.close()
            return true
        }
        return false
    } else {
        val path = RealPathUtil.getPath(context, this)
        if (path?.isNotEmpty() == true) {
            File(path).writeBytes(byteArray)
            return true
        }
    }
    return false
}

@Throws(Exception::class)
fun Uri.writeText(context: Context, text: String, charset: Charset = Charsets.UTF_8): Boolean {
    return writeBytes(context, text.toByteArray(charset))
}

fun Uri.writeBytes(
    context: Context,
    fileName: String,
    byteArray: ByteArray
): Boolean {
    if (this.isContentScheme()) {
        DocumentFile.fromTreeUri(context, this)?.let { pDoc ->
            DocumentUtils.createFileIfNotExist(pDoc, fileName)?.let {
                return it.uri.writeBytes(context, byteArray)
            }
        }
    } else {
        FileUtils.createFileWithReplace(path + File.separatorChar + fileName)
            .writeBytes(byteArray)
        return true
    }
    return false
}

fun Uri.inputStream(context: Context): Result<InputStream> {
    val uri = this
    return kotlin.runCatching {
        try {
            if (isContentScheme()) {
                DocumentFile.fromSingleUri(context, uri)
                    ?: throw NoStackTraceException("未获取到文件")
                return@runCatching context.contentResolver.openInputStream(uri)!!
            } else {
                val path = RealPathUtil.getPath(context, uri)
                    ?: throw NoStackTraceException("未获取到文件")
                val file = File(path)
                if (file.exists()) {
                    return@runCatching FileInputStream(file)
                } else {
                    throw NoStackTraceException("文件不存在")
                }
            }
        } catch (e: Exception) {
            e.printOnDebug()
            throw e
        }
    }
}

fun Uri.toRequestBody(contentType: MediaType? = null): RequestBody {
    val uri = this
    return object : RequestBody() {
        override fun contentType() = contentType

        override fun contentLength(): Long {
            val length = uri.inputStream(appCtx).getOrThrow().available().toLong()
            return if (length > 0) length else -1
        }

        override fun writeTo(sink: BufferedSink) {
            uri.inputStream(appCtx).getOrThrow().source().use { source ->
                sink.writeAll(source)
            }
        }
    }
}