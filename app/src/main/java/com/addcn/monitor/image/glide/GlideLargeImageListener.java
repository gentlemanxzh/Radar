package com.addcn.monitor.image.glide;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.addcn.monitor.image.LargeImageManager;
import com.addcn.monitor.util.ConvertUtils;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.target.ViewTarget;

/**
 * @author Gentleman
 * @time 2022/12/5 15:09
 * @desc
 */
public class GlideLargeImageListener<R> implements RequestListener<R> {
    @Override
    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<R> target, boolean isFirstResource) {
        Log.e("GlideLargeImageListener","onLoadFailed");
        return false;
    }

    @Override
    public boolean onResourceReady(R resource, Object model, Target<R> target, DataSource dataSource, boolean isFirstResource) {
        Bitmap bitmap;
        if (resource instanceof Bitmap) {
            bitmap = (Bitmap) resource;
            double imgSize = ConvertUtils.byte2MemorySize(bitmap.getByteCount(), ConvertUtils.KB);
            LargeImageManager.getInstance().saveImageInfo(model.toString(), imgSize, bitmap.getWidth(),bitmap.getHeight(),"Glide");
        } else if (resource instanceof BitmapDrawable) {
            bitmap = ConvertUtils.drawable2Bitmap((BitmapDrawable) resource);
            double imgSize = ConvertUtils.byte2MemorySize(bitmap.getByteCount(), ConvertUtils.KB);
            LargeImageManager.getInstance().saveImageInfo(model.toString(), imgSize, bitmap.getWidth(),bitmap.getHeight(),"Glide");
        }
        return false;
    }

}
