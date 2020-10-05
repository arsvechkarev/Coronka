package core.recycler

import android.view.ViewGroup
import androidx.collection.SparseArrayCompat
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import kotlin.reflect.KClass

abstract class BaseListAdapter(
  private val delegates: List<ListAdapterDelegate>,
  diffCallback: ItemCallback<SortableDisplayableItem>
) :
  ListAdapter<SortableDisplayableItem, ViewHolder>(diffCallback) {
  
  protected var data: List<SortableDisplayableItem> = ArrayList()
  
  private val classesMap = HashMap<KClass<*>, Int>()
  private val delegatesSparseArray = SparseArrayCompat<ListAdapterDelegate>()
  
  init {
    delegates.forEachIndexed { i, delegate ->
      classesMap[delegate.modelClass] = i
      delegatesSparseArray.put(i, delegate)
    }
  }
  
  override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
    delegates.forEach { it.onAttachedToRecyclerView(recyclerView) }
  }
  
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    return delegatesSparseArray[viewType]?.onCreateViewHolder(parent)
        ?: error("No delegate for view type $viewType")
  }
  
  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val adapterDelegate = delegatesSparseArray[getItemViewType(position)]
        ?: error("No delegate for position $position")
    adapterDelegate.onBindViewHolder(holder, data[position])
  }
  
  override fun getItemViewType(position: Int): Int {
    return classesMap[data[position]::class] ?: error("Can't find delegate for position: $position")
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
    delegates.forEach { it.onDetachedFromRecyclerView(recyclerView) }
  }
}