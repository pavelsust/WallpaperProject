package com.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.danimahardhika.android.helpers.animation.AnimationHelper;
import com.danimahardhika.android.helpers.core.DrawableHelper;

import com.danimahardhika.android.helpers.core.WindowHelper;
import com.database.DatabaseManager;

import com.example.lolipop.wallpaperproject.MainActivity;
import com.example.lolipop.wallpaperproject.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.pojo.WallpaperItem;
import com.squareup.picasso.Picasso;
import com.tasks.WallpaperApplyTask;
import com.tasks.WallpaperDownloader;
import com.util.ImageConfig;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by lolipop on 2/25/2018.
 */

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.CustomViewHolder>{

    public Context context;
    public ArrayList<WallpaperItem> itemsArrayList;
    private final DisplayImageOptions.Builder mOptions;
    public CallBack callBack;


    public DatabaseManager databaseManager;
    public ArrayList<WallpaperItem> wallpaperFavouriteList;



    public ItemAdapter(Context context , ArrayList<WallpaperItem> itemsArrayList , CallBack callBack ){
        this.context = context;
        this.itemsArrayList = itemsArrayList;
        mOptions = ImageConfig.getRawDefaultImageOptions();
        mOptions.resetViewBeforeLoading(true);
        this.callBack = callBack;
        mOptions.cacheInMemory(true);
        mOptions.cacheOnDisk(true);
        mOptions.displayer(new FadeInBitmapDisplayer(700));
        databaseManager = new DatabaseManager(context);
        wallpaperFavouriteList = new ArrayList<>();

    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_test , null);
        CustomViewHolder customViewHolder = new CustomViewHolder(view);
        return customViewHolder;
    }



    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {

        WallpaperItem items = itemsArrayList.get(position);
        Log.d("Message" , ""+items.getUrl());
        Picasso.with(context).load(items.getUrl()).into(holder.imageView);

        //resetImageViewHeight(holder.imageView, items.getImageDimension());


        if (isFavourite(items.getUrl()) == true){
            setFavorite(holder.love, Color.WHITE, position, true , true);
        }else {
            setFavorite(holder.love, Color.WHITE, position, true , false);
        }

        ViewCompat.setTransitionName(holder.imageView, "pavel");
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callBack.show(holder.getAdapterPosition() , items , holder.imageView);
            }
        });

    }

    @Override
    public int getItemCount() {
        return itemsArrayList.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.image)
        ImageView imageView;
        @BindView(R.id.favorite)
        ImageView love;

        @BindView(R.id.apply)
        ImageView apply;

        View view;
        @BindView(R.id.download)
        ImageView download;


        public CustomViewHolder(View itemView) {
            super(itemView);
            this.view = itemView;
            ButterKnife.bind(this , itemView);
            imageView.setOnClickListener(this);
            love.setOnClickListener(this);
            apply.setOnClickListener(this);
            download.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            int id = v.getId();
            WallpaperItem items = itemsArrayList.get(position);
            if (id == R.id.favorite){

                if (isFavourite(items.getUrl())==true){
                    databaseManager.deleteWallpaper(items);
                    setFavorite(love, Color.WHITE, position, true , false);

                }else if (isFavourite(items.getUrl())==false){
                    setFavorite(love, Color.WHITE, position, true, true);
                    databaseManager.addIntoDatabase(items.getUrl() , items.getId());
                }

            } else if (id == R.id.download){
                callBack.downloadWallpaper(position , items);
            }else if (id == R.id.apply){
                callBack.setWallpaper(position , items);
            }

        }
    }

    public interface CallBack{
        void show(int position, WallpaperItem items, ImageView imageView);
        void setWallpaper(int position , WallpaperItem wallpaperItem);
        void downloadWallpaper(int position , WallpaperItem wallpaperItem);
    }


    private void resetImageViewHeight(@NonNull ImageView imageView, ImageSize imageSize) {
        if (imageSize == null) imageSize = new ImageSize(400, 300);

        int width = WindowHelper.getScreenSize(context).x;
        int spanCount = context.getResources().getInteger(R.integer.latest_wallpapers_column_count);
        if (spanCount > 1) {
            width = width/spanCount;
        }
        double scaleFactor = (double) width / (double) imageSize.getWidth();
        double measuredHeight = (double) imageSize.getHeight() * scaleFactor;
        imageView.getLayoutParams().height = Double.valueOf(measuredHeight).intValue();
        imageView.requestLayout();
    }

    private void setFavorite(@NonNull ImageView imageView, @ColorInt int color, int position, boolean animate , boolean isFavo) {
        if (position < 0 || position > itemsArrayList.size()) return;

        //boolean isFavo = true;
        if (animate) {
            AnimationHelper.show(imageView)
                    .interpolator(new LinearOutSlowInInterpolator())
                    .callback(new AnimationHelper.Callback() {
                        @Override
                        public void onAnimationStart() {
                            imageView.setImageDrawable(DrawableHelper.getTintedDrawable(context,
                                    isFavo ? R.drawable.ic_toolbar_love : R.drawable.ic_toolbar_unlove, color));
                        }

                        @Override
                        public void onAnimationEnd() {

                        }
                    })
                    .start();
            return;
        }

        imageView.setImageDrawable(DrawableHelper.getTintedDrawable(context,
                isFavo ? R.drawable.ic_toolbar_love : R.drawable.ic_toolbar_unlove, color));
    }


    public boolean isFavourite(String songId) {
        wallpaperFavouriteList = databaseManager.getAllFavouriteWallpaperData();
        if (wallpaperFavouriteList.contains(new WallpaperItem(songId))) {
            return true;
        }
        return false;
    }
}

