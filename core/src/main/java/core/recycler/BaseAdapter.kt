package core.recycler

import android.view.ViewGroup
import androidx.collection.SparseArrayCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder

abstract class BaseAdapter<T : DisplayableItem>(block: BaseAdapter<T>.() -> Unit = {}) :
  RecyclerView.Adapter<ViewHolder>() {
  
  protected var data: MutableList<T> = ArrayList()
  protected val delegates = SparseArrayCompat<AdapterDelegate>()
  
  init {
    this.apply(block)
  }
  
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    return delegates[viewType]!!.onCreateViewHolder(parent)
  }
  
  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    delegates[getItemViewType(position)]!!.onBindViewHolder(holder, data[position])
  }
  
  override fun getItemViewType(position: Int): Int {
    return data[position].type
  }
  
  override fun getItemCount(): Int {
    return data.size
  }
  
  fun submitList(list: List<T>?) {
    data = list as? MutableList<T> ?: ArrayList()
    notifyDataSetChanged()
  }
  
}