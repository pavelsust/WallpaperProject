package com.tasks;

import android.app.Application;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.preferance.Preferences;
import com.util.ImageConfig;



/*
 * Wallpaper Board
 *
 * Copyright (c) 2017 Dani Mahardhika
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public abstract class WallpaperBoardApplication extends Application  {

    public static boolean sIsClickable = true;
    private static boolean mIsLatestWallpapersLoaded;
    private static WallpaperBoardConfiguration mConfiguration;

    private Thread.UncaughtExceptionHandler mHandler;

    @Override
    public void onCreate() {
        super.onCreate();

        Preferences.get(this);

        if (!ImageLoader.getInstance().isInited())
            ImageLoader.getInstance().init(ImageConfig.getImageLoaderConfiguration(this));
    }

    public static WallpaperBoardConfiguration getConfig() {
        if (mConfiguration == null) {
            mConfiguration = new WallpaperBoardConfiguration();
        }
        return mConfiguration;
    }

    public static boolean isLatestWallpapersLoaded() {
        return mIsLatestWallpapersLoaded;
    }

    public static void setLatestWallpapersLoaded(boolean loaded) {
        mIsLatestWallpapersLoaded = loaded;
    }


}
