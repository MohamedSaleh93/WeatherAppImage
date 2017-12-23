package com.weather.photo.cache;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.LruCache;

import com.weather.photo.application.MainProgram;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * @author MohamedSaleh on 12/23/2017.
 */

public class CacheManager {

    private static CacheManager cacheManager;
    private static File file;

    private CacheManager() {
        file = MainProgram.getContext().getCacheDir();
    }

    public static CacheManager getInstance() {
        if (cacheManager == null) {
            cacheManager = new CacheManager();
        }
        return cacheManager;
    }

    public void addBitmapToMemoryCache(Integer key, Bitmap bitmap) {
        try {

            FileOutputStream outputStream = new FileOutputStream(file.getPath() + String.valueOf(key));
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Bitmap getBitmapFromMemCache(Integer key) {
        File saveFile = new File(file.getPath() + String.valueOf(key));
        if (!saveFile.exists()) {
            return null;
        }
        Bitmap tmp = BitmapFactory.decodeFile(file.getPath() + String.valueOf(key));
        return tmp;
    }
}
