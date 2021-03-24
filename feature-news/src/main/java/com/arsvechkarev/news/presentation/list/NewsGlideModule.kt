package com.arsvechkarev.news.presentation.list

import android.content.Context
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions

@GlideModule
class NewsGlideModule : AppGlideModule() {
  
  override fun applyOptions(context: Context, builder: GlideBuilder) {
    builder.setDefaultRequestOptions(
      RequestOptions().format(DecodeFormat.PREFER_RGB_565)
          .diskCacheStrategy(DiskCacheStrategy.ALL)
    )
  }
}