package com.example.lolipop.wallpaperproject;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.ColorInt;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.adapter.ItemAdapter;
import com.pojo.WallpaperItem;
import com.tasks.CropWallpaperApplyTask;
import com.tasks.WallpaperApplyTask;
import com.tasks.WallpaperDownloader;
import com.tasks.WallpaperPropertiesLoaderTask;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements ItemAdapter.CallBack{

    @BindView(R.id.main_recycler)
    RecyclerView recyclerView;
    public ItemAdapter adapter;
    public ArrayList<WallpaperItem> wallpaperItemArrayList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        wallpaperItemArrayList = new ArrayList<>();
        wallpaperItemArrayList = getSortItems();
        adapter = new ItemAdapter(getApplicationContext() , wallpaperItemArrayList , this);
        recyclerView.setAdapter(adapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static ArrayList<WallpaperItem> getSortItems() {
        ArrayList<WallpaperItem> items = new ArrayList<>();
        items.add(new WallpaperItem(1 , "asd" , "https://farm5.staticflickr.com/4607/25572405437_bc0cf1162e_h.jpg" ));
        items.add(new WallpaperItem(2 , "name" , "https://farm5.staticflickr.com/4611/40469076211_df1f703951_h.jpg"));
        items.add(new WallpaperItem(4 , "asdasd" , "https://farm5.staticflickr.com/4631/39584770924_806f6285c0_h.jpg" ));
        items.add(new WallpaperItem(5 , "Akasdasdasdash" , "https://farm5.staticflickr.com/4627/26484056658_3f07428306_h.jpg" ));
        items.add(new WallpaperItem(6 , "sadasda" , "https://farm5.staticflickr.com/4766/40415816491_3aba0f8fbd_h.jpg" ));
        items.add(new WallpaperItem(7 , "sadasdasd" , "https://farm5.staticflickr.com/4663/25523427907_c4286db0e7_h.jpg" ));
        items.add(new WallpaperItem(8 , "sdasdasd" , "https://farm6.staticflickr.com/5498/11133774015_9ec94cb7cc_h.jpg" ));
        items.add(new WallpaperItem(9 , "dasdjl" , "https://farm5.staticflickr.com/4714/25018360577_517ae13a36_h.jpg" ));
        items.add(new WallpaperItem(10 , "ihasdkh" , "https://farm5.staticflickr.com/4675/39637696144_9d3da401b4_h.jpg" ));
        items.add(new WallpaperItem(11 , "lhasdh" , "https://farm5.staticflickr.com/4720/40226743741_202786ab09_h.jpg" ));
        return items;
    }

    @Override
    public void show(int position, WallpaperItem items, ImageView imageView) {


        Toast.makeText(getApplicationContext(), "Click", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, ImageShowActivity.class);
        intent.putExtra("item", items.getUrl());
        intent.putExtra("view", ViewCompat.getTransitionName(imageView));

        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this,
                imageView,
                ViewCompat.getTransitionName(imageView));

        startActivity(intent, options.toBundle());
    }

    @Override
    public void setWallpaper(int position, WallpaperItem wallpaperItem) {

                CropWallpaperApplyTask.prepare(MainActivity.this)
                        .wallpaper(wallpaperItem)
                        .to(CropWallpaperApplyTask.Apply.HOMESCREEN)
                        .start(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void downloadWallpaper(int position, WallpaperItem wallpaperItem) {

        WallpaperDownloader.prepare(getApplicationContext()).wallpaper(wallpaperItem).start();
    }
}
