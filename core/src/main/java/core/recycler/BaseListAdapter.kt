package core.recycler

import android.view.ViewGroup
import androidx.collection.SparseArrayCompat
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import core.recycler.DisplayableItem.DiffCallBack

abstract class BaseListAdapter : ListAdapter<DisplayableItem, ViewHolder>(DiffCallBack()) {
  
  protected var data: List<DisplayableItem> = ArrayList()
  private val delegates = SparseArrayCompat<AdapterDelegate>()
  
  protected fun addDelegate(delegate: AdapterDelegate) {
    delegates.put(delegate.modelClass.hashCode(), delegate)
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
  
  override fun submitList(list: List<DisplayableItem>?) {
    data = list ?: ArrayList()
    super.submitList(list)
  }
  
  override fun submitList(list: List<DisplayableItem>?, commitCallback: Runnable?) {
    data = list ?: ArrayList()
    super.submitList(list, commitCallback)
  }
}