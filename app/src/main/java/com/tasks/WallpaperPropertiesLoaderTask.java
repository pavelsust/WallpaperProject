package com.tasks;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;


import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.pojo.WallpaperItem;

import java.io.File;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executor;


public class WallpaperPropertiesLoaderTask extends AsyncTask<Void, Void, Boolean> {

    private WallpaperItem mWallpaper;
    private WeakReference<Callback> mCallback;
    private final WeakReference<Context> mContext;

    private WallpaperPropertiesLoaderTask(Context context) {
        mContext = new WeakReference<>(context);
    }

    public WallpaperPropertiesLoaderTask wallpaper(WallpaperItem wallpaper) {
        mWallpaper = wallpaper;
        return this;
    }

    public WallpaperPropertiesLoaderTask callback(@Nullable Callback callback) {
        mCallback = new WeakReference<>(callback);
        return this;
    }

    public AsyncTask start() {
        return start(SERIAL_EXECUTOR);
    }

    public AsyncTask start(@NonNull Executor executor) {
        return executeOnExecutor(executor);
    }

    public static WallpaperPropertiesLoaderTask prepare(@NonNull Context context) {
        return new WallpaperPropertiesLoaderTask(context);
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        while (!isCancelled()) {
            try {
                Thread.sleep(1);
                if (mWallpaper == null) return false;

                if (mWallpaper.getImageDimension() != null &&
                        mWallpaper.getMimeType() != null &&
                        mWallpaper.getImageSie() > 0) {
                    return false;
                }

                final BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;

                URL url = new URL(mWallpaper.getUrl());

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(15000);

                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    Log.d("connectio" ,""+connection.getResponseCode() );
                    InputStream stream = connection.getInputStream();
                    BitmapFactory.decodeStream(stream, null, options);

                    ImageSize imageSize = new ImageSize(options.outWidth, options.outHeight);
                    mWallpaper.setImageDimension(imageSize);
                    mWallpaper.setMimeType(options.outMimeType);

                    int contentLength = connection.getContentLength();
                    if (contentLength > 0) {
                        mWallpaper.setImageSie(contentLength);
                    }

                    stream.close();
                    return true;
                }
                return false;
            } catch (Exception e) {

                return false;
            }
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        if (aBoolean && mContext.get() != null && !((AppCompatActivity) mContext.get()).isFinishing()) {
            if (mWallpaper.getImageSie() <= 0) {
                File target = ImageLoader.getInstance().getDiskCache().get(mWallpaper.getUrl());
                if (target.exists()) {
                    mWallpaper.setImageSie((int) target.length());
                }
            }
        }

        if (mCallback != null && mCallback.get() != null) {
            mCallback.get().onPropertiesReceived(mWallpaper);
        }
    }

    public interface Callback {
        void onPropertiesReceived(WallpaperItem wallpaper);
    }
}
