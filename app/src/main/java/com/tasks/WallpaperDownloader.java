package com.tasks;

import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.webkit.URLUtil;
import android.widget.Toast;

import com.danimahardhika.android.helpers.core.FileHelper;
import com.danimahardhika.cafebar.CafeBar;
import com.example.lolipop.wallpaperproject.R;
import com.pojo.WallpaperItem;
import java.io.File;

public class WallpaperDownloader {

    private final Context mContext;
    private WallpaperItem mWallpaper;

    private WallpaperDownloader(Context context) {
        mContext = context;
    }

    public WallpaperDownloader wallpaper(@NonNull WallpaperItem wallpaper) {
        mWallpaper = wallpaper;
        return this;
    }

    public void start() {
        String fileName = mWallpaper.getName() +"."+ WallpaperHelper.getFormat(mWallpaper.getMimeType());
        File directory = WallpaperHelper.getDefaultWallpapersDirectory(mContext);
        File target = new File(directory, fileName);
        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                showCafeBar(R.string.wallpaper_download_failed);
                return;
            }
        }

        boolean check = WallpaperHelper.isWallpaperSaved(mContext , mWallpaper);
        Log.d("Message" , ""+check);

        if (WallpaperHelper.isWallpaperSaved(mContext, mWallpaper)) {
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(mWallpaper.getUrl()));
            request.setTitle(fileName);
            request.setDescription(mContext.getResources().getString(R.string.wallpaper_downloading));
            request.allowScanningByMediaScanner();
            request.setVisibleInDownloadsUi(false);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationUri(Uri.fromFile(target));
            DownloadManager downloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
            try {

                if (downloadManager != null) {
                    downloadManager.enqueue(request);
                    return;
                }
            } catch (IllegalArgumentException e) {

                return;
            }
            showCafeBar(R.string.wallpaper_downloading);
        }else {
            Toast.makeText(mContext, "Al ready downloaded", Toast.LENGTH_SHORT).show();
        }

        if (!URLUtil.isValidUrl(mWallpaper.getUrl())) {
            return;
        }


    }

    private void showCafeBar(int res) {
        CafeBar.builder(mContext)
                .content(res)
                .floating(true)
                .fitSystemWindow()
                .show();
    }

    public static WallpaperDownloader prepare(@NonNull Context context) {
        return new WallpaperDownloader(context);
    }
}
