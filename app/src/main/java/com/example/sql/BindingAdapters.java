

package com.example.sql;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import android.widget.Switch;

import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;
import androidx.databinding.BindingAdapter;

public class BindingAdapters {

    @BindingAdapter("bindSrcCompat")
    public static void bindSrcCompat(ImageView imageView, @DrawableRes int drawableResId) {
        Context context = imageView.getContext();
        Drawable drawable = ContextCompat.getDrawable(context, drawableResId);
        imageView.setImageDrawable(drawable);
    }

    @BindingAdapter("bindChecked")
    public static void bindChecked(Switch switchView, boolean checked) {
        switchView.setChecked(checked);
    }
}
