package com.tasks;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.util.TypefaceHelper;
import com.danimahardhika.android.helpers.core.ColorHelper;
import com.danimahardhika.android.helpers.core.WindowHelper;
import com.danimahardhika.cafebar.CafeBar;
import com.danimahardhika.cafebar.CafeBarTheme;
import com.example.lolipop.wallpaperproject.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.pojo.WallpaperItem;

import com.util.ImageConfig;

import java.lang.ref.WeakReference;
import java.util.Locale;
import java.util.concurrent.Executor;


public class CropWallpaperApplyTask extends AsyncTask<Void, Void, Boolean> implements WallpaperPropertiesLoaderTask.Callback{

    private final WeakReference<Context> mContext;
    private Apply mApply;
    private RectF mRectF;
    private Executor mExecutor;
    private WallpaperItem mWallpaper;
    private MaterialDialog mDialog;
    public boolean isCrop = true;

    private CropWallpaperApplyTask(Context context) {
        mContext = new WeakReference<>(context);
    }


    public CropWallpaperApplyTask to(Apply apply) {
        mApply = apply;
        return this;
    }

    public CropWallpaperApplyTask wallpaper(@NonNull WallpaperItem wallpaper) {
        mWallpaper = wallpaper;
        return this;
    }

    public CropWallpaperApplyTask crop(@Nullable RectF rectF) {
        mRectF = rectF;
        return this;
    }

    public AsyncTask start() {
        return start(SERIAL_EXECUTOR);
    }

    public AsyncTask start(@NonNull Executor executor) {
        if (mDialog == null) {
            int color = mWallpaper.getColor();
            if (color == 0) {
                color = ColorHelper.getAttributeColor(mContext.get(), R.attr.colorAccent);
            }

            final MaterialDialog.Builder builder = new MaterialDialog.Builder(mContext.get());
            builder.widgetColor(color)

                    .progress(true, 0)
                    .cancelable(false)
                    .progressIndeterminateStyle(true)
                    .content(R.string.wallpaper_loading)
                    .positiveColor(color)
                    .positiveText(android.R.string.cancel)
                    .onPositive((dialog, which) -> {
                        ImageLoader.getInstance().stop();
                        cancel(true);
                    });

            mDialog = builder.build();
        }

        if (!mDialog.isShowing()) mDialog.show();

        mExecutor = executor;
        if (mWallpaper == null) {

            return null;
        }

        if (mWallpaper.getImageDimension() == null) {
            return WallpaperPropertiesLoaderTask.prepare(mContext.get())
                    .wallpaper(mWallpaper)
                    .callback(this)
                    .start(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        return executeOnExecutor(executor);
    }

    public static CropWallpaperApplyTask prepare(@NonNull Context context) {
        return new CropWallpaperApplyTask(context);
    }

    @Override
    public void onPropertiesReceived(WallpaperItem wallpaper) {
        mWallpaper = wallpaper;
        if (mExecutor == null) mExecutor = SERIAL_EXECUTOR;
        if (mWallpaper.getImageDimension() == null) {


            if (mContext.get() == null) return;
            if (mContext.get() instanceof Activity) {
                if (((Activity) mContext.get()).isFinishing())
                    return;
            }

            if (mDialog != null && mDialog.isShowing()) {
                mDialog.dismiss();
            }


            return;
        }

        try {
            executeOnExecutor(mExecutor);
        } catch (IllegalStateException e) {

        }
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        while (!isCancelled()) {
            try {
                Thread.sleep(1);
                ImageSize imageSize = WallpaperHelper.getTargetSize(mContext.get());



                if (mRectF != null && Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
                    Point point = WindowHelper.getScreenSize(mContext.get());
                    int height = point.y - WindowHelper.getStatusBarHeight(mContext.get()) - WindowHelper.getNavigationBarHeight(mContext.get());
                    float heightFactor = (float) imageSize.getHeight() / (float) height;
                    mRectF = WallpaperHelper.getScaledRectF(mRectF, heightFactor, 1f);
                }

                if (mRectF == null && isCrop) {
                    /*
                     * Create a center crop rectF if wallpaper applied from grid, not opening the preview first
                     */
                    float widthScaleFactor = (float) imageSize.getHeight() / (float) mWallpaper.getImageDimension().getHeight();

                    float side = ((float) mWallpaper.getImageDimension().getWidth() * widthScaleFactor - (float) imageSize.getWidth())/2f;
                    float leftRectF = 0f - side;
                    float rightRectF = (float) mWallpaper.getImageDimension().getWidth() * widthScaleFactor - side;
                    float topRectF = 0f;
                    float bottomRectF = (float) imageSize.getHeight();
                    mRectF = new RectF(leftRectF, topRectF, rightRectF, bottomRectF);

                }

                ImageSize adjustedSize = imageSize;
                RectF adjustedRectF = mRectF;

                float scaleFactor = (float) mWallpaper.getImageDimension().getHeight() / (float) imageSize.getHeight();
                if (scaleFactor > 1f) {
                    /*
                     * Applying original wallpaper size caused a problem (wallpaper zoomed in)
                     * if wallpaper dimension bigger than device screen resolution
                     *
                     * Solution: Resize wallpaper to match screen resolution
                     */

                    /*
                     * Use original wallpaper size:
                     * adjustedSize = new ImageSize(width, height);
                     */

                    /*
                     * Adjust wallpaper size to match screen resolution:
                     */
                    float widthScaleFactor = (float) imageSize.getHeight() / (float) mWallpaper.getImageDimension().getHeight();
                    int adjustedWidth = Float.valueOf((float) mWallpaper.getImageDimension().getWidth() * widthScaleFactor).intValue();
                    adjustedSize = new ImageSize(adjustedWidth, imageSize.getHeight());

                    if (adjustedRectF != null) {
                        /*
                         * If wallpaper crop enabled, original wallpaper size should be loaded first
                         */
                        adjustedSize = new ImageSize(mWallpaper.getImageDimension().getWidth(), mWallpaper.getImageDimension().getHeight());
                        adjustedRectF = WallpaperHelper.getScaledRectF(mRectF, scaleFactor, scaleFactor);

                    }


                }

                int call = 1;
                do {
                    /*
                     * Load the bitmap first
                     */
                    Bitmap loadedBitmap = ImageLoader.getInstance().loadImageSync(
                            mWallpaper.getUrl(), adjustedSize, ImageConfig.getWallpaperOptions());
                    if (loadedBitmap != null) {
                        try {
                            /*
                             * Checking if loaded bitmap resolution supported by the device
                             * If texture size too big then resize it
                             */
                            Bitmap bitmapTemp = Bitmap.createBitmap(
                                    loadedBitmap.getWidth(),
                                    loadedBitmap.getHeight(),
                                    loadedBitmap.getConfig());
                            bitmapTemp.recycle();

                            /*
                             * Texture size is ok
                             */

                            publishProgress();

                            Bitmap bitmap = loadedBitmap;
                            if (isCrop && adjustedRectF != null) {

                                /*
                                 * Cropping bitmap
                                 */
                                ImageSize targetSize = WallpaperHelper.getTargetSize(mContext.get());

                                int targetWidth = Double.valueOf(
                                        ((double) loadedBitmap.getHeight() / (double) targetSize.getHeight())
                                                * (double) targetSize.getWidth()).intValue();

                                bitmap = Bitmap.createBitmap(
                                        targetWidth,
                                        loadedBitmap.getHeight(),
                                        loadedBitmap.getConfig());
                                Paint paint = new Paint();
                                paint.setFilterBitmap(true);
                                paint.setAntiAlias(true);
                                paint.setDither(true);

                                Canvas canvas = new Canvas(bitmap);
                                canvas.drawBitmap(loadedBitmap, null, adjustedRectF, paint);

                                float scale = (float) targetSize.getHeight() / (float) bitmap.getHeight();
                                if (scale < 1f) {

                                    int resizedWidth = Float.valueOf((float) bitmap.getWidth() * scale).intValue();
                                    bitmap = Bitmap.createScaledBitmap(bitmap, resizedWidth, targetSize.getHeight(), true);
                                }
                            }

                            /*
                             * Final bitmap generated
                             */


                            if (mApply == Apply.HOMESCREEN_LOCKSCREEN) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    WallpaperManager.getInstance(mContext.get().getApplicationContext()).setBitmap(
                                            bitmap, null, true, WallpaperManager.FLAG_LOCK | WallpaperManager.FLAG_SYSTEM);
                                    return true;
                                }
                            }

                            if (mApply == Apply.HOMESCREEN) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    WallpaperManager.getInstance(mContext.get().getApplicationContext()).setBitmap(
                                            bitmap, null, true, WallpaperManager.FLAG_SYSTEM);
                                    return true;
                                }

                                WallpaperManager.getInstance(mContext.get().getApplicationContext()).setBitmap(bitmap);
                                return true;
                            }

                            if (mApply == Apply.LOCKSCREEN) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    WallpaperManager.getInstance(mContext.get().getApplicationContext()).setBitmap(
                                            bitmap, null, true, WallpaperManager.FLAG_LOCK);
                                    return true;
                                }
                            }
                        } catch (OutOfMemoryError e) {

                            /*
                             * Texture size is too big
                             * Resizing bitmap
                             */

                            double scale = 1 - (0.1 * call);
                            int scaledWidth = Double.valueOf(adjustedSize.getWidth() * scale).intValue();
                            int scaledHeight = Double.valueOf(adjustedSize.getHeight() * scale).intValue();

                            adjustedRectF = WallpaperHelper.getScaledRectF(adjustedRectF,
                                    (float) scale, (float) scale);
                            adjustedSize = new ImageSize(scaledWidth, scaledHeight);
                        }
                    }

                    /*
                     * Continue to next iteration
                     */
                    call++;
                } while (call <= 5 && !isCancelled());
                return false;
            } catch (Exception e) {

                return false;
            }
        }
        return false;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
        if (mContext.get() == null) return;
        if (mContext.get() instanceof Activity) {
            if (((Activity) mContext.get()).isFinishing())
                return;
        }

        mDialog.setContent(R.string.wallpaper_applying);
    }

    @Override
    protected void onCancelled(Boolean aBoolean) {
        super.onCancelled(aBoolean);
        if (mContext.get() == null) return;
        if (mContext.get() instanceof Activity) {
            if (((Activity) mContext.get()).isFinishing())
                return;
        }

        Toast.makeText(mContext.get(), R.string.wallpaper_apply_cancelled,
                Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        if (mContext.get() == null) return;
        if (mContext.get() instanceof Activity) {
            if (((Activity) mContext.get()).isFinishing())
                return;
        }

        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }

        if (aBoolean) {
            CafeBar.builder(mContext.get())

                    .floating(true)
                    .fitSystemWindow()
                    .content(R.string.wallpaper_applied)
                    .show();
        } else {
            Toast.makeText(mContext.get(), R.string.set_task_error,
                    Toast.LENGTH_LONG).show();
        }
    }

    public enum Apply {
        LOCKSCREEN,
        HOMESCREEN,
        HOMESCREEN_LOCKSCREEN
    }
}
