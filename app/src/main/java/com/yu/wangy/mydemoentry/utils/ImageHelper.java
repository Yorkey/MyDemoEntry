package com.yu.wangy.mydemoentry.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Build;
import android.util.Size;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by wangyu on 2015/9/18.
 */
public class ImageHelper {

    private static final int MAX_PIXELS = 1200*1200;
    private static final int MAX_PIXELS_HQ = 1500*1500;

    private static final int JPEG_QUALITY = 80;
    private static final int JPEG_QUALITY_HQ = 90;
    private static final int JPEG_QUALITY_LOW = 55;

    public static final void save(Bitmap src, String filename) {
        save(src, filename, Bitmap.CompressFormat.JPEG, JPEG_QUALITY_HQ);
    }

    public static final byte[] save(Bitmap src) {
        return save(src, Bitmap.CompressFormat.JPEG, JPEG_QUALITY_LOW);
    }

    public Bitmap loadOptimizedHQ(String filename) {
        int scale = getScaleFactor(getImageSize(filename), MAX_PIXELS_HQ);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        options.inSampleSize = scale;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;

        if (Build.VERSION.SDK_INT >= 10) {
            options.inPreferQualityOverSpeed = true;
        }

        if (Build.VERSION.SDK_INT >= 11) {
            options.inMutable = true;
        }

        if (!new File(filename).exists()) {
            return null;
        }

        Bitmap res = BitmapFactory.decodeFile(filename, options);
        if (res == null) {
            return null;
        }

        try {
            ExifInterface exif = new ExifInterface(filename);
            String exifOrientation = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
            int orientation = 0;
            if (exifOrientation != null) {
                orientation = Integer.parseInt(exifOrientation);
            }

        } catch (IOException e) {
            //e.printStackTrace();
        }

        return  res;
    }


    private static Bitmap fixExif(Bitmap src, int orientation) {
        try {
            final Matrix bitmapMatrix = new Matrix();
            switch (orientation) {
                case ExifInterface.ORIENTATION_NORMAL:
                    break;
                case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                    bitmapMatrix.postScale(-1, 1);
                    break;
            }
            return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), bitmapMatrix, false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private static int getScaleFactor(BitmapSize size, int maxPixels) {
        int scale = 1;
        int scaledW = size.getWidth();
        int scaledH = size.getHeight();
        while (scaledW * scaledH > maxPixels) {
            scale *= 2;
            scaledW /= 2;
            scaledH /= 2;
        }

        return scale;
    }

    private static BitmapSize getImageSize(String filename) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filename, options);
        int w = options.outWidth;
        int h = options.outHeight;
        if (w == 0 || h == 0) {
            return null;
        }

        try {
            ExifInterface exif = new ExifInterface(filename);
            String exifOrientation = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
            int orientation = 0;
            if (exifOrientation != null) {
                if (exifOrientation.equals("5") ||
                        exifOrientation.equals("6") ||
                        exifOrientation.equals("7") ||
                        exifOrientation.equals("8")) {
                    w = options.outHeight;
                    h= options.outWidth;
                }
            }

        } catch (IOException e) {
            //e.printStackTrace();
        }

        return new BitmapSize(w, h);
    }

    /**
     *
     * @param src
     * @param format
     * @param quality
     * @return
     */
    public static byte[] save(Bitmap src, Bitmap.CompressFormat format, int quality) {
        ByteArrayOutputStream outputStream = null;
        try {
            outputStream = new ByteArrayOutputStream();
            src.compress(format, quality, outputStream);
            return outputStream.toByteArray();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void save(Bitmap src, String filename, Bitmap.CompressFormat format, int quality) {
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(filename);
            src.compress(format, quality, outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static class BitmapSize {
        private int width;
        private int height;

        public BitmapSize() {

        }

        public BitmapSize(int width, int height) {
            this.width = width;
            this.height = height;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public void setHeight(int height) {
            this.height = height;
        }
    }

}
