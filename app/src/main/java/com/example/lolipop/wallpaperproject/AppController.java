package com.example.lolipop.wallpaperproject;

import android.app.Application;
import android.content.Context;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.util.ImageConfig;


/**
 * Created by lolipop on 6/15/16.
 */
public class AppController extends Application {

    public static Context context;
    private static AppController mInstance;

    public static Context getContext() {
        return AppController.context;
    }

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        AppController.context = getApplicationContext();
        if (!ImageLoader.getInstance().isInited())
            ImageLoader.getInstance().init(ImageConfig.getImageLoaderConfiguration(this));

    }

}
