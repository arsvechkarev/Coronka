package core.recycler

import android.view.ViewGroup
import androidx.collection.SparseArrayCompat
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import kotlin.reflect.KClass

abstract class BaseListAdapter(
  private val delegates: List<ListAdapterDelegate<out DifferentiableItem>>,
  diffCallback: ItemCallback<DifferentiableItem> = DifferentiableItem.DiffCallBack()
) : ListAdapter<DifferentiableItem, ViewHolder>(diffCallback) {
  
  protected var data: List<DifferentiableItem> = ArrayList()
  
  private val classesToViewTypes = HashMap<KClass<*>, Int>()
  private val delegatesSparseArray = SparseArrayCompat<ListAdapterDelegate<out DifferentiableItem>>()
  
  constructor(
    delegate: ListAdapterDelegate<out DifferentiableItem>,
    diffCallback: ItemCallback<DifferentiableItem> = DifferentiableItem.DiffCallBack()
  ) : this(listOf(delegate), diffCallback)
  
  init {
    delegates.forEachIndexed { i, delegate ->
      classesToViewTypes[delegate.modelClass] = i
      delegatesSparseArray.put(i, delegate)
    }
  }
  
  override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
    delegates.forEach { it.onAttachedToRecyclerView(recyclerView) }
  }
  
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val delegate = delegatesSparseArray[viewType] ?: error("No delegate for view type $viewType")
    return delegate.onCreateViewHolder(parent)
  }
  
  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val adapterDelegate = delegatesSparseArray[getItemViewType(position)]
        ?: error("No delegate for position $position")
    adapterDelegate.onBindViewHolderRaw(holder, data[position])
  }
  
  override fun getItemViewType(position: Int): Int {
    return classesToViewTypes[data[position]::class] ?: error(
      "Can't find delegate for position: $position")
  }
  
  override fun submitList(list: List<DifferentiableItem>?) {
    data = list ?: ArrayList()
    super.submitList(list)
  }
  
  override fun submitList(list: List<DifferentiableItem>?, commitCallback: Runnable?) {
    data = list ?: ArrayList()
    super.submitList(list, commitCallback)
  }
  
  override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
    delegates.forEach { it.onDetachedFromRecyclerView(recyclerView) }
  }
}