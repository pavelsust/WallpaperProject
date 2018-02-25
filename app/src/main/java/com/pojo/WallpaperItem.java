package com.pojo;

import android.support.annotation.ColorInt;
import com.nostra13.universalimageloader.core.assist.ImageSize;

/**
 * Created by lolipop on 2/25/2018.
 */

public class WallpaperItem {

    public int id;
    public String author;
    public String url;
    public String name;
    @ColorInt
    public int color;
    public String mimeType;
    public int imageSie;
    public ImageSize imageDimension = null;


    public int getId() {
        return id;
    }

    public WallpaperItem(){

    }

    public WallpaperItem(int id , String name , String url){
        this.id = id;
        this.url = url;
        this.name = name;
    }
    public WallpaperItem(String url){
        this.url = url;
    }

    public ImageSize getImageDimension() {
        return imageDimension;
    }

    public int getColor() {
        return color;
    }

    public int getImageSie() {
        return imageSie;
    }

    public String getAuthor() {
        return author;
    }

    public String getMimeType() {
        return mimeType;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setImageDimension(ImageSize imageDimension) {
        this.imageDimension = imageDimension;
    }

    public void setImageSie(int imageSie) {
        this.imageSie = imageSie;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public boolean equals(final Object obj) {

        // If passed object is an instance of Blacklist, then compare the phone numbers, else return false as they are not equal
        if (obj.getClass().isInstance(new WallpaperItem())) {
            // Cast the object to Blacklist
            final WallpaperItem bl = (WallpaperItem) obj;

            // Compare whether the phone numbers are same, if yes, it defines the objects are equal
            if (bl.url.equalsIgnoreCase(this.url))
                return true;
        }
        return false;
    }


}
