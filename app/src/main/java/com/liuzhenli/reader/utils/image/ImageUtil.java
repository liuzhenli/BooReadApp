package com.liuzhenli.reader.utils.image;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.liuzhenli.common.utils.RxUtil;
import com.liuzhenli.reader.ReaderApplication;
import com.liuzhenli.reader.observer.SampleProgressObserver;
import com.liuzhenli.common.utils.Constant;
import com.liuzhenli.common.utils.FileUtils;
import com.liuzhenli.common.utils.LogUtils;
import com.microedu.reader.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.FileNameMap;
import java.net.URLConnection;

import io.reactivex.Observable;

/**
 * @author Liuzhenli
 * @since 2019-07-06 10:26
 */
public class ImageUtil {
    private static final String TAG = "ImageUtil";

    /**
     * 下载图片
     *
     * @param url                    图片的URL
     * @param sampleProgressObserver 图片下载完成的回调，在onNext方法中会有下载完成后的本地图片路径
     */
    public static void downloadImg(final Context mContext, final String url, SampleProgressObserver<String> sampleProgressObserver) {
        RxUtil.subscribe(Observable.fromCallable(() -> {
            File file = Glide.with(mContext)
                    .load(url)
                    .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                    .get();
            return file.getAbsolutePath();
        }), sampleProgressObserver);
    }

    public static Bitmap getLocalBitmap(String filePath) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(filePath);
            //把流转化为Bitmap图片
            return BitmapFactory.decodeStream(fis);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * Glide 加载图片
     */
    public static void setImage(Context cxt, String imgUrl, ImageView imageView) {
        Glide.with(cxt).load(imgUrl).into(imageView);
    }

    /**
     * Glide 加载图片
     * placeHolderResId 加载失败填充图资源id
     */
    public static void setImage(Context cxt, String imgUrl, int placeHolderResId, ImageView imageView) {
        Glide.with(cxt).load(imgUrl).placeholder(placeHolderResId).into(imageView);
    }

    /**
     * Glide 加载图片
     * placeHolderResId 加载失败填充图资源id
     */
    public static void setImage(Context cxt, String imgUrl, int placeHolderResId, int errorResId, ImageView imageView) {
        Glide.with(cxt).load(imgUrl).placeholder(placeHolderResId).error(errorResId).into(imageView);
    }

    /**
     * Glide 加载图片
     */
    public static void setRoundImage(Context cxt, String imgUrl, ImageView imageView) {
        Glide.with(cxt).load(imgUrl).transform(new GlideCircleTransform()).into(imageView);
    }

    /**
     * Glide 加载图片
     * placeHolderResId 加载失败填充图资源id
     */
    public static void setRoundImage(Context cxt, String imgUrl, int placeHolderResId, ImageView imageView) {
        Glide.with(cxt).load(imgUrl).placeholder(placeHolderResId).transform(new GlideCircleTransform()).into(imageView);
    }

    /**
     * Glide 加载图片
     * placeHolderResId 加载失败填充图资源id
     */
    public static void setRoundedCornerImage(Context cxt, String imgUrl, int placeHolderResId, ImageView imageView) {
        Glide.with(cxt).load(imgUrl).placeholder(placeHolderResId).transform(new GlideRoundTransform()).into(imageView);
    }

    /**
     * Glide 加载图片
     * placeHolderResId 加载失败填充图资源id
     *
     * @param radius dp
     */
    public static void setRoundedCornerImage(Context cxt, String imgUrl, int placeHolderResId, ImageView imageView, int radius) {
        Glide.with(cxt).load(imgUrl).placeholder(placeHolderResId).transform(new GlideRoundTransform(radius)).into(imageView);
    }

    public static void setRoundedCornerImage(Context cxt, String imgUrl, int placeHolderResId, int errorResId, ImageView imageView, int radius) {
        Glide.with(cxt).load(imgUrl).placeholder(placeHolderResId).error(errorResId).transform(new GlideRoundTransform(radius)).into(imageView);
    }

    /**
     * Glide 加载图片 圆形图片有边框
     * placeHolderResId 加载失败填充图资源id
     *
     * @param borderWidth dp
     */
    public static void setCircleImageWithBorder(Context context, String imgUrl, int placeHolderResId, ImageView imageView, int borderWidth) {
        Glide.with(context).load(imgUrl).placeholder(placeHolderResId)
                .transform(new GlideCircleWithStokeTransform(context, borderWidth, context.getResources().getColor(R.color.white)))
                .into(imageView);
    }

    /**
     * Glide 加载图片 圆形图片有边框
     * placeHolderResId 加载失败填充图资源id
     *
     * @param borderWidth dp
     */
    public static void setCircleImageWithBorder(Context context, int imgUrl, int placeHolderResId, ImageView imageView, int borderWidth) {
        Glide.with(context).load(imgUrl).placeholder(placeHolderResId)
                .transform(new GlideCircleWithStokeTransform(context, borderWidth, context.getResources().getColor(R.color.white)))
                .into(imageView);
    }


    // android 4.4 版本获取图库 图片路径 调用此方法
    @TargetApi(19)
    public static String getPhotoPath(final Context context, final Uri uri) {

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
            if (isGooglePhotosUri(uri)) {
                return uri.getLastPathSegment();
            }

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
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

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    /**
     * 根据路径获得突破并压缩返回bitmap用于显示
     *
     * @return
     */
    public static Bitmap getSmallBitmap(String filePath, int newWidth, int newHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, newWidth, newHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
        Bitmap newBitmap = compressImage(bitmap, 500);
        if (bitmap != null) {
            bitmap.recycle();
        }
        return newBitmap;
    }

    /**
     * 计算图片的缩放值
     *
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and
            // width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will
            // guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }

    /**
     * 质量压缩
     *
     * @param image
     * @param maxSize
     */
    public static Bitmap compressImage(Bitmap image, int maxSize) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        // scale
        int options = 80;
        // Store the bitmap into output stream(no compress)
        image.compress(Bitmap.CompressFormat.JPEG, options, os);
        // Compress by loop
        while (os.toByteArray().length / 1024 > maxSize) {
            // Clean up os
            os.reset();
            // interval 10
            options -= 10;
            image.compress(Bitmap.CompressFormat.JPEG, options, os);
        }

        Bitmap bitmap = null;
        byte[] b = os.toByteArray();
        if (b.length != 0) {
            bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
        }
        return bitmap;
    }

    /**
     * 图片保存到SD卡
     *
     * @param bitmap
     * @return
     */
    public static String saveToSdCard(Bitmap bitmap) {
        String imageUrl = Constant.POST_CACHE_PATH + System.currentTimeMillis() + ".jpg";
        File file = new File(imageUrl);
        try {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            FileOutputStream out = new FileOutputStream(file);
            if (bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)) {
                out.flush();
                out.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file.getAbsolutePath();
    }

    /**
     * 保存至相册
     *
     * @param bitmap
     * @param listener
     */
    @SuppressWarnings("All")
    public static void saveBitmapToCapture(Bitmap bitmap, OnSaveResultListener listener) {
        LogUtils.e(TAG, "保存图片");

        File dir = new File(Constant.CAPTURE_PATH);
        if (!dir.exists()) {
            FileUtils.createDir(Constant.CAPTURE_PATH);
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        String imgPath = FileUtils.getCapturePath(fileName);
        File f = new File(imgPath);
        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            LogUtils.i(TAG, "已经保存");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        // 其次把文件插入到系统图库
        try {
//            String s = MediaStore.Images.Media.insertImage(mContext.getContentResolver(),
//                    f.getAbsolutePath(), fileName, null);


            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DATA, f.getAbsolutePath());
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            Uri uri = ReaderApplication.getInstance().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        } catch (Exception e) {
            e.printStackTrace();
        }

        scanFile(f, listener);
    }


    public static String getMimeType(File file) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String type = fileNameMap.getContentTypeFor(file.getName());
        return type;

    }

    public static void scanFile(File file, OnSaveResultListener listener) {
        String mimeType = getMimeType(file);
        if (Build.VERSION.SDK_INT >= 29) {
            String fileName = file.getName();
            ContentValues values = new ContentValues();
            values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
            values.put(MediaStore.MediaColumns.MIME_TYPE, mimeType);
            values.put("relative_path", Environment.DIRECTORY_DCIM);
            ContentResolver contentResolver = ReaderApplication.getInstance().getContentResolver();
            Uri uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            if (uri == null) {
                if (listener != null) {
                    listener.onResult(false, "");
                }
                return;
            }
            try {
                OutputStream out = contentResolver.openOutputStream(uri);
                FileInputStream fis = new FileInputStream(file);
                byte[] buffer = new byte[1024];
                int byteRead;
                while (-1 != (byteRead = fis.read(buffer))) {
                    if (out != null) {
                        out.write(buffer, 0, byteRead);
                    }
                }
                fis.close();
                if (out != null) {
                    out.close();
                }
                if (listener != null) {
                    listener.onResult(true, "相册");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            MediaScannerConnection.scanFile(ReaderApplication.getInstance(), new String[]{file.getPath()}, new String[]{mimeType}, (path, uri) -> {
                Looper.prepare();
                if (listener != null) {
                    listener.onResult(true, path);
                }
                Looper.loop();
            });
        }
    }

    public interface OnSaveResultListener {
        void onResult(boolean success, String path);
    }

    /**
     * 清除内存缓存
     *
     * @param context context
     */
    public static void clearMemoryCache(Context context) {
        Glide.get(context).clearMemory();
    }

    /**
     * 清除磁盘缓存 必须在子线程
     *
     * @param context context
     */
    public static void clearDiskCache(Context context) {
        Glide.get(context).clearDiskCache();
    }
}
