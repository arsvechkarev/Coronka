package com.arsvechkarev.viewdsl

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

fun ViewGroup.addViews(vararg views: View) {
  views.forEach { addView(it) }
}

inline fun ViewGroup.forEachChild(action: (child: View) -> Unit) {
  for (i in 0 until childCount) action(getChildAt(i))
}

fun ViewGroup.inflate(@LayoutRes layoutRes: Int): View {
  return LayoutInflater.from(context).inflate(layoutRes, this, false)
}

fun RecyclerView.setupWithAdapter(adapter: RecyclerView.Adapter<*>) {
  layoutManager = LinearLayoutManager(context)
  this.adapter = adapter
}