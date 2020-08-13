package core.recycler

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlin.reflect.KClass

abstract class ListAdapterDelegate(val modelClass: KClass<out SortableDisplayableItem>) {
  
  abstract fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder
  
  abstract fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: SortableDisplayableItem)
  
  open fun onAttachedToRecyclerView(recyclerView: RecyclerView) {}
  
  open fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {}
}