package core.recycler

import android.view.ViewGroup
import androidx.collection.SparseArrayCompat
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import core.extenstions.forEach
import core.recycler.SortableDisplayableItem.DiffCallBack

abstract class BaseListAdapter : ListAdapter<SortableDisplayableItem, ViewHolder>(DiffCallBack()) {
  
  protected var data: List<SortableDisplayableItem> = ArrayList()
  private val delegates = SparseArrayCompat<ListAdapterDelegate>()
  
  protected fun addDelegate(delegate: ListAdapterDelegate) {
    delegates.put(delegate.modelClass.hashCode(), delegate)
  }
  
  override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
    delegates.forEach { it.onAttachedToRecyclerView(recyclerView) }
  }
  
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    return delegates[viewType]?.onCreateViewHolder(parent)
        ?: error("Can't find delegate for type $viewType")
  }
  
  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val adapterDelegate = delegates[getItemViewType(position)]
        ?: error("Can't find delegate for position: $position")
    adapterDelegate.onBindViewHolder(holder, data[position])
  }
  
  override fun getItemViewType(position: Int): Int {
    return data[position]::class.hashCode()
  }
  
  override fun submitList(list: List<SortableDisplayableItem>?) {
    data = list ?: ArrayList()
    super.submitList(list)
  }
  
  override fun submitList(list: List<SortableDisplayableItem>?, commitCallback: Runnable?) {
    data = list ?: ArrayList()
    super.submitList(list, commitCallback)
  }
  
  override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
    super.onDetachedFromRecyclerView(recyclerView)
    delegates.forEach { it.onDetachedFromRecyclerView(recyclerView) }
  }
}