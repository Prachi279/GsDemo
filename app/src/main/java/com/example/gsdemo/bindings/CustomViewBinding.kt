package com.example.gsdemo.bindings

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

/**
 * The CustomViewBinding class,to set custom attributes in xml file
 */
class CustomViewBinding {
    companion object {
        /**
         * The loadImage method, to load image from the xml
         */
        @JvmStatic
        @BindingAdapter("imageUrl")
        fun loadImage(view: ImageView, imageUrl: String?) {
            Glide.with(view.context)
                .load(imageUrl)
                .into(view)
        }
    }

}