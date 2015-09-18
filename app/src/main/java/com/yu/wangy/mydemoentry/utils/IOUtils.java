package com.yu.wangy.mydemoentry.utils;

import android.content.Context;
import android.net.Uri;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by wangyu on 2015/9/17.
 */
public class IOUtils {
    private static final String TAG = "IOUtils";

    /**
     * 线程本地数据
     */
    private static ThreadLocal<byte[]> buffer = new ThreadLocal<byte[]>(){

        @Override
        protected byte[] initialValue() {
            return new byte[4*1024];
        }
    };

    /**
     *
     * @param src 源文件
     * @param dst 目标文件
     * @throws IOException
     */
    public static void copy(File src, File dst) throws IOException {

        InputStream in = null;
        OutputStream out = null;
        try {
            in = new FileInputStream(src);
            out = new FileOutputStream(dst);

            byte[] bytes = buffer.get();
            int len;
            while ((len = in.read(bytes)) > 0) {
                Thread.yield();
                out.write(bytes, 0, len);
            }

        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void copy(InputStream in, File dst) throws IOException {

        OutputStream out = null;
        try {
            out = new FileOutputStream(dst);
            byte[] bytes = buffer.get();
            int len;
            while ((len = in.read(bytes)) > 0) {
                Thread.yield();
                out.write(bytes, 0, len);
            }
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     *
     * @param uri 资源uri
     * @param dst 目标文件路径
     * @param context
     * @throws IOException
     */
    public static void copy(Uri uri, File dst, Context context) throws IOException {

        InputStream stream = null;
        try {
            stream = context.getContentResolver().openInputStream(uri);
            copy(stream, dst);
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void writeAll(String filename, byte[] data) throws IOException {
        OutputStream stream = null;
        try {
            stream = new FileOutputStream(filename);
            stream.write(data);
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static byte[] readAll(String filename) throws IOException {
        InputStream  stream = new FileInputStream(filename);
        return readAll(stream, null);

    }

    public static byte[] readAll(InputStream in) throws IOException {
        return readAll(in, null);
    }

    public static byte[] readAll(InputStream in, ProgressListener listener) throws IOException {

        BufferedInputStream bufferedInputStream = null;
        ByteArrayOutputStream os = null;
        try {

            bufferedInputStream = new BufferedInputStream(in);
            os = new ByteArrayOutputStream();

            byte[] bytes = buffer.get();
            int len;
            int readed = 0;
            while ((len = bufferedInputStream.read()) > 0) {
                Thread.yield();
                os.write(bytes, 0, len);
                readed += len;
                if (listener != null) {
                    listener.onProgress(readed);
                }
            }
            return os.toByteArray();

        } finally {
            if (bufferedInputStream != null) {
                try {
                    bufferedInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static interface ProgressListener {

        public void onProgress(int bytes);
    }

}
