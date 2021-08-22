package com.liuzhenli.common.utils;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;


import androidx.annotation.NonNull;

import com.liuzhenli.common.exception.ApiCodeException;
import com.orhanobut.logger.Logger;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {
    private static final String TAG = "FileUtils";
    private static final String[] filters = {".txt", ".pdf", ".epub"};
    private static final int BUFFER = 1024 * 8;

    public static File getFontDir(String font) {
        File file = new File(getFontPath(font));
        if (file.exists()) {
            return file;
        }

        return null;

    }

    public static String getFontPath(String font) {
        return Constant.FONT_PATH + font;
    }

    public static String getTtsPath(String filename) {
        return Constant.TTS_PATH + filename;
    }

    public static String getImgPath(String img) {
        return Constant.IMG_PATH + img;
    }

    public static String getUpdatePath(String fileName) {
        return Constant.UPDATE_PATH + fileName;
    }

    //复制文件
    public static void copyFile(File sourceFile, File targetFile) throws IOException {
        try (BufferedInputStream inBuff = new BufferedInputStream(new FileInputStream(sourceFile));
             BufferedOutputStream outBuff = new BufferedOutputStream(new FileOutputStream(targetFile))) {
            byte[] buffer = new byte[BUFFER];
            int length;
            while ((length = inBuff.read(buffer)) != -1) {
                outBuff.write(buffer, 0, length);
            }
            outBuff.flush();
        }
    }

    public static void copyFromAssets(AssetManager assets, String source, String dest, boolean isCover) throws IOException {
        File file = new File(dest);
        if (!file.getParentFile().exists())
            file.getParentFile().mkdirs();
        if (isCover || !file.exists()) {
            InputStream is = null;
            FileOutputStream fos = null;
            try {
                is = assets.open(source);
                String path = dest;
                fos = new FileOutputStream(path);
                byte[] buffer = new byte[1024];
                int size = 0;
                while ((size = is.read(buffer, 0, 1024)) >= 0) {
                    fos.write(buffer, 0, size);
                }
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } finally {
                        is.close();
                    }
                }
            }
        }
    }

    /**
     * 读取表情配置文件
     */
    public static List<String> getFacesFile(Context context) {
        BufferedReader br = null;
        try {
            List<String> list = new ArrayList<String>();
            InputStream in = context.getResources().getAssets().open("faces");
            br = new BufferedReader(new InputStreamReader(in,
                    "UTF-8"));
            String str = null;
            while ((str = br.readLine()) != null) {
                list.add(str);
            }

            return list;
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            assert br != null;
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static File[] read(File file) {
        // 是文件夹
        if (file != null && file.isDirectory()) {
            // 设置过滤条件
            File[] files = file.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    // 是文件夹，true不过滤
                    if (pathname.isDirectory()) {
                        return true;
                    }
                    boolean flag = false;
                    if (pathname.isFile()) {
                        String fileName = pathname.getName().toLowerCase();
                        for (int i = 0; i < filters.length; i++) {
                            if (fileName.endsWith(filters[i])) {
                                flag = true;
                                break;
                            }
                        }
                    }
                    return flag;
                }
            });

            return files;
        }
        return null;
    }

    public static String readString(File file, int begin, int size) {
        try {
            DataInputStream dataInputStream = new DataInputStream(new FileInputStream(file));
            Logger.d("MyBook", "文件总长度：" + file.length());
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            // 从什么位置开始读
            dataInputStream.skip(begin);
            // 读多少数据
            byte[] b = new byte[size];
            int i = -1;
            if ((i = dataInputStream.read(b, 0, b.length)) != -1) {
                out.write(b, 0, i);
                // randomAccessFile.skipBytes(1024);
                String string = out.toString("UTF-8");
                Logger.d("MyBook", "begin：" + begin);
                Logger.d("MyBook", "size：" + size);
                // Log.d("MyBook", "读取的文本：" + string);
                return string;
            }
            out.close();
            dataInputStream.close();
        } catch (Exception e) {
            Logger.d("MyBook", e.getMessage());
        }
        return null;
    }

    public static String readString0(File file, int begin, int size) {
        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
            Logger.d("MyBook", "文件总长度：" + randomAccessFile.length());
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            // 从什么位置开始读
            randomAccessFile.seek(begin);
            // 读多少数据
            byte[] b = new byte[size];
            int i = -1;
            if ((i = randomAccessFile.read(b, 0, b.length)) != -1) {
                out.write(b, 0, i);
                String string = out.toString("UTF-8");
                Logger.d("MyBook", "begin：" + begin);
                Logger.d("MyBook", "size：" + size);
                Logger.d("MyBook", "FilePointer：" + randomAccessFile.getFilePointer());
                // Log.d("MyBook", "读取的文本：" + string);
                return string;
            }
            out.close();
            randomAccessFile.close();
        } catch (Exception e) {
            Logger.d("MyBook", e.getMessage());
        }
        return null;
    }


    public static File getLogFile() {
        File file = new File(Constant.LOG_PATH + "log.txt");
        if (!file.exists()) {
            createFile(file);
        }
        return file;
    }

    public static File getErrorLogFile() {
        File file = new File(Constant.LOG_PATH + "errorlog.txt");
        if (!file.exists()) {
            createFile(file);
        }
        return file;
    }

    public static File getBookDir(String bookId) {
        return new File(Constant.BASE_PATH + bookId);
    }


    /**
     * root cache path
     */
    public static String createRootPath(Context context) {
        String cacheRootPath = "";
        if (isSdCardAvailable()) {
            // /sdcard/Android/data/<application package>/cache
            File externalCacheDir = context.getExternalCacheDir();
            if (externalCacheDir == null) {
                cacheRootPath = context.getCacheDir().getPath();
            } else {

                cacheRootPath = externalCacheDir.getPath();
            }
        } else {
            // /data/data/<application package>/cache
            cacheRootPath = context.getCacheDir().getPath();
        }
        return cacheRootPath;
    }

    public static boolean isSdCardAvailable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    /**
     * 创建WiFi传书目录
     * create download dir
     * /data/data/<application package>/Download
     */
    public static String createDownloadBookPath(Context context) {
        String cacheRootPath = "";
        if (isSdCardAvailable()) {
            File externalFileDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
            if (externalFileDir == null) {
                cacheRootPath = context.getFilesDir().getPath();
            } else {
                cacheRootPath = externalFileDir.getPath();
            }
        } else {
            cacheRootPath = context.getFilesDir().getPath();
        }
        return cacheRootPath;
    }

    public static String createDocumentBookPath(Context context) {
        String cacheRootPath = "";
        if (isSdCardAvailable()) {
            File externalFileDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
            if (externalFileDir == null) {
                cacheRootPath = context.getFilesDir().getPath();
            } else {
                cacheRootPath = externalFileDir.getPath();
            }
        } else {
            cacheRootPath = context.getFilesDir().getPath();
        }
        return cacheRootPath;
    }

    /**
     * 递归创建文件夹
     *
     * @return 创建失败返回""
     */
    public static String createDir(String dirPath) {
        try {
            File file = new File(dirPath);
            if (file.getParentFile().exists()) {
                file.mkdir();
                return file.getAbsolutePath();
            } else {
                createDir(file.getParentFile().getAbsolutePath());
                file.mkdir();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dirPath;
    }

    /**
     * 递归创建文件夹
     *
     * @return 创建失败返回""
     */
    public static String createFile(File file) {
        try {
            if (file.getParentFile().exists()) {
                file.createNewFile();
                return file.getAbsolutePath();
            } else {
                createDir(file.getParentFile().getAbsolutePath());
                file.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static File createFile(String fullPath) throws IOException {
        File file = new File(fullPath);
        file.createNewFile();
        return file;
    }

    public static String getImageCachePath(String path) {
        return createDir(path);
    }

    /**
     * 获取图片缓存目录
     *
     * @return 创建失败, 返回""
     */
    public static String getImageCachePath(Context context) {
        String path = createDir(createRootPath(context) + File.separator + "img" + File.separator);
        return path;
    }

    /**
     * 获取图片裁剪缓存目录
     *
     * @return 创建失败, 返回""
     */
    public static String getImageCropCachePath(Context context) {
        String path = createDir(createRootPath(context) + File.separator + "imgCrop" + File.separator);
        return path;
    }

    /**
     * 将内容写入文件
     *
     * @param filePath eg:/mnt/sdcard/demo.txt
     * @param content  内容
     */
    public static synchronized void writeFile(String filePath, String content, boolean isAppend) {
//        Logger.i("save:" + filePath);
        try {
            FileOutputStream fout = new FileOutputStream(filePath, isAppend);
            byte[] bytes = content.getBytes();
            fout.write(bytes);
            fout.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static File writeFromBuffer(String fullPath, byte[] buffer) {
        File file;
        OutputStream output = null;
        try {
            String path = fullPath.substring(0, fullPath.lastIndexOf('/'));
            createDir(path);
            deleteFile(fullPath);
            file = createFile(fullPath);
            output = new FileOutputStream(file);
            output.write(buffer);
            output.flush();
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApiCodeException(22212, "保存文件失败,请检查手机扩展卡是否可用");
        } finally {
            try {
                if (output != null) {
                    output.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    public static void writeFile(String filePathAndName, String fileContent) {
        try {
            OutputStream outStream = new FileOutputStream(filePathAndName);
            OutputStreamWriter out = new OutputStreamWriter(outStream);
            out.write(fileContent);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 写入文件
     *
     * @param in
     * @param file
     */
    public static void writeFile(InputStream in, File file) throws IOException {
        if (!file.getParentFile().exists())
            file.getParentFile().mkdirs();

        if (file != null && file.exists())
            file.delete();

        FileOutputStream out = new FileOutputStream(file);
        byte[] buffer = new byte[1024 * 128];
        int len = -1;
        while ((len = in.read(buffer)) != -1) {
            out.write(buffer, 0, len);
        }
        out.flush();
        out.close();
        in.close();

    }

    /**
     * 打开Asset下的文件
     *
     * @param context
     * @param fileName
     * @return
     */
    public static InputStream openAssetFile(Context context, String fileName) {
        AssetManager am = context.getAssets();
        InputStream is = null;
        try {
            is = am.open(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return is;
    }

    /**
     * 获取Raw下的文件内容
     *
     * @param context
     * @param resId
     * @return 文件内容
     */
    public static String getFileFromRaw(Context context, int resId) {
        if (context == null) {
            return null;
        }

        BufferedReader br = null;
        StringBuilder s = new StringBuilder();
        try {
            InputStreamReader in = new InputStreamReader(context.getResources().openRawResource(resId));
            br = new BufferedReader(in);
            String line;
            while ((line = br.readLine()) != null) {
                s.append(line);
            }
            return s.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 文件拷贝
     *
     * @param src  源文件
     * @param desc 目的文件
     */
    public static void fileChannelCopy(File src, File desc) {
        FileInputStream fi = null;
        FileOutputStream fo = null;
        try {
            fi = new FileInputStream(src);
            fo = new FileOutputStream(desc);
            FileChannel in = fi.getChannel();//得到对应的文件通道
            FileChannel out = fo.getChannel();//得到对应的文件通道
            in.transferTo(0, in.size(), out);//连接两个通道，并且从in通道读取，然后写入out通道
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fo != null) {
                    fo.close();
                }
                if (fi != null) {
                    fi.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 转换文件大小
     *
     * @param fileLen 单位B
     * @return
     */
    public static String formatFileSizeToString(long fileLen) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        if (fileLen < 1024) {
            fileSizeString = df.format((double) fileLen) + "B";
        } else if (fileLen < 1048576) {
            fileSizeString = df.format((double) fileLen / 1024) + "K";
        } else if (fileLen < 1073741824) {
            fileSizeString = df.format((double) fileLen / 1048576) + "M";
        } else {
            fileSizeString = df.format((double) fileLen / 1073741824) + "G";
        }
        return fileSizeString;
    }

    /**
     * 删除指定文件
     *
     * @param file
     */
    public static boolean deleteFile(File file) throws IOException {
        return deleteFileOrDirectory(file);
    }

    public static boolean deleteFile(String filePath) throws IOException {
        return deleteFile(new File(filePath));
    }

    /**
     * 删除指定文件，如果是文件夹，则递归删除
     *
     * @param file
     * @return
     * @throws IOException
     */
    public static boolean deleteFileOrDirectory(File file) throws IOException {
        try {
            if (file != null && file.isFile()) {
                return file.delete();
            }
            if (file != null && file.isDirectory()) {
                File[] childFiles = file.listFiles();
                // 删除空文件夹
                if (childFiles == null || childFiles.length == 0) {
                    return file.delete();
                }
                // 递归删除文件夹下的子文件
                for (int i = 0; i < childFiles.length; i++) {
                    deleteFileOrDirectory(childFiles[i]);
                }
                return file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 查询某个文件下的子文件个数
     */
    public static int getFileChildCount(File file) {

        if (file != null && file.exists()) {
            return file.list().length;
        }
        return 0;
    }


    /***
     * 获取文件扩展名
     *
     * @param filename 文件名
     * @return
     */
    public static String getExtensionName(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length() - 1))) {
                return filename.substring(dot + 1);
            }
        }
        return filename;
    }

    /**
     * 获取文件内容
     *
     * @param path
     * @return
     */
    public static String getFileOutputString(String path) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(path), 8192);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append("\n").append(line);
            }
            bufferedReader.close();
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getFromAssets(String fileName, Context context) {
        try {
            InputStreamReader inputReader = new InputStreamReader(context.getResources().getAssets().open(fileName));
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line = "";
            String Result = "";
            while ((line = bufReader.readLine()) != null)
                Result += line;
            return Result;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 读取asset目录下的json文件
     */
    public static JSONObject getJsonObjectFromAssets(String fileName, Context context) {
        StringBuilder sb = new StringBuilder();
        try {
            InputStream is = context.getAssets().open(fileName);
            int len;
            byte[] buffer = new byte[1024];
            while ((len = is.read(buffer)) != -1) {
                sb.append(new String(buffer, 0, len, "utf-8"));
            }
            is.close();
            return new JSONObject(sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据Uri获取真实的文件路径
     *
     * @param context
     * @param uri
     * @return
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getPath(final Context context, final Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final String[] split = id.split(":");
                final String type = split[0];
                if ("raw".equalsIgnoreCase(type)) { //处理某些机型（比如Goole Pixel ）ID是raw:/storage/emulated/0/Download/c20f8664da05ab6b4644913048ea8c83.mp4
                    return split[1];
                }

                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        final String column = "_data";
        final String[] projection = {
                column
        };

        try (Cursor cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                null)) {
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    public static List<File> list(File dir, String nametxt, String ext,
                                  String type, List<File> fs) {

        listFile(dir, nametxt, type, ext, fs);
        File[] all = dir.listFiles();
        // 递归获得当前目录的所有子目录
        for (int i = 0; i < all.length; i++) {
            File d = all[i];
            if (d.isDirectory()) {
                list(d, nametxt, ext, type, fs);
            }
        }
        return null;

    }

    /**
     * @param dir     根目�?
     * @param nametxt 文件名中包含的关键字
     * @param type    文件夹的类型
     * @param ext     后缀�?
     * @param fs      返回的结�?
     * @return
     */
    private static List<File> listFile(File dir, String nametxt, String type,
                                       String ext, List<File> fs) {
        File[] all = dir.listFiles(new Fileter(ext));
        for (int i = 0; i < all.length; i++) {
            File d = all[i];
            if (d.getName().toLowerCase().indexOf(nametxt.toLowerCase()) >= 0) {
                if (type.equals("1")) {
                    fs.add(d);
                } else if (d.isDirectory() && type.equals("2")) {
                    fs.add(d);
                } else if (!d.isDirectory() && type.equals("3")) {
                    fs.add(d);
                }
            }

        }
        return fs;
    }

    static class Fileter implements FilenameFilter {
        private final String ext;

        public Fileter(String ext) {
            this.ext = ext;
        }

        @Override
        public boolean accept(File dir, String name) {
            return name.endsWith(ext);

        }
    }

    public static String getCapturePath(String img) {
        return Constant.CAPTURE_PATH + img;
    }

    @NonNull
    public static String getSdCardPath() {
        String sdCardDirectory = Environment.getExternalStorageDirectory().getAbsolutePath();
        try {
            sdCardDirectory = new File(sdCardDirectory).getCanonicalPath();
        } catch (IOException ioe) {
            Log.e(TAG, "Could not get SD directory", ioe);
        }
        return sdCardDirectory;
    }


    public static String readFileToBuffer(String fullPath, String encoding) {
        InputStreamReader reader = null;
        BufferedReader bufferedReader = null;
        try {
            File file = new File(fullPath);
            StringBuilder buffer = new StringBuilder();
            String txt = "";
            if (file.isFile() && file.exists()) {
                reader = new InputStreamReader(new FileInputStream(file), encoding);
                bufferedReader = new BufferedReader(reader);
                while ((txt = bufferedReader.readLine()) != null) {
                    buffer.append(txt);
                }
                return buffer.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static boolean isContentFile(Uri uri) {
        return uri != null && TextUtils.equals(uri.getScheme(), "content");
    }

    public static boolean isContentFile(String path) {
        return path != null && path.startsWith("content://");
    }

    public static boolean isContentFile(File file) {
        return file != null && file.getAbsolutePath().startsWith("content://");
    }
}