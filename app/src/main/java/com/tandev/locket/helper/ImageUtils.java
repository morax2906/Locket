package com.tandev.locket.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class ImageUtils {

    public static Uri processImage(Context context, Uri imageUri, int quality) throws IOException {
        Bitmap bitmap = getBitmapFromUri(context, imageUri);

        if (bitmap == null) {
            throw new IOException("Không thể đọc ảnh từ Uri");
        }

        // Lưu ảnh mà không xoay hoặc cắt
        return saveBitmapToCache(context, bitmap, quality);
    }

    private static Bitmap getBitmapFromUri(Context context, Uri uri) throws IOException {
        try (ParcelFileDescriptor parcelFileDescriptor = context.getContentResolver().openFileDescriptor(uri, "r");
             FileInputStream fileInputStream = new FileInputStream(parcelFileDescriptor.getFileDescriptor())) {
            return BitmapFactory.decodeStream(fileInputStream);
        }
    }

    public static Uri saveBitmapToCache(Context context, Bitmap bitmap, int quality) throws IOException {
        File file = new File(context.getCacheDir(), "processed_image.jpg");
        try (OutputStream out = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out);
        }
        return Uri.fromFile(file);
    }
}
