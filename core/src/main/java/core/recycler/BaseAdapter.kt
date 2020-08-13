package core.recycler

import android.view.ViewGroup
import androidx.collection.SparseArrayCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import core.extenstions.forEach

abstract class BaseAdapter : RecyclerView.Adapter<ViewHolder>() {
  
  protected var data: List<DisplayableItem> = ArrayList()
  protected var recyclerView: RecyclerView? = null
  
  private val delegates = SparseArrayCompat<AdapterDelegate>()
  
  fun addDelegate(delegate: AdapterDelegate) {
    delegates.put(delegate.modelClass.hashCode(), delegate)
  }
  
  fun submitList(list: List<DisplayableItem>?, notify: Boolean = true) {
    data = list ?: ArrayList()
    if (notify) notifyDataSetChanged()
  }
  
  override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
    super.onAttachedToRecyclerView(recyclerView)
    this.recyclerView = recyclerView
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
  
  override fun getItemCount(): Int {
    return data.size
  }
  
  override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
    super.onDetachedFromRecyclerView(recyclerView)
    this.recyclerView = null
    delegates.forEach { it.onDetachedFromRecyclerView(recyclerView) }
  }
}