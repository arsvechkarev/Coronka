package core.recycler

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlin.reflect.KClass

abstract class AdapterDelegate(val modelClass: KClass<out DisplayableItem>) {
  
  abstract fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder
  
  abstract fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: DisplayableItem)
  
  open fun onAttachedToRecyclerView(recyclerView: RecyclerView) {}
  
  open fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {}
}