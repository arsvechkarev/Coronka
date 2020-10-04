package core.recycler

import android.view.ViewGroup
import androidx.collection.SparseArrayCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import kotlin.reflect.KClass

abstract class BaseAdapter(
  private val delegates: List<AdapterDelegate>
) : RecyclerView.Adapter<ViewHolder>() {
  
  protected var data: List<DisplayableItem> = ArrayList()
  
  private val classesMap = HashMap<KClass<*>, Int>()
  private val delegatesSparseArray = SparseArrayCompat<AdapterDelegate>()
  
  constructor(vararg delegates: AdapterDelegate) : this(delegates.toList())
  
  init {
    delegates.forEachIndexed { i, delegate ->
      classesMap[delegate.modelClass] = i
      delegatesSparseArray.put(i, delegate)
    }
  }
  
  fun submitList(list: List<DisplayableItem>?, notify: Boolean = true) {
    data = list ?: ArrayList()
    if (notify) notifyDataSetChanged()
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
  
  override fun getItemCount(): Int {
    return data.size
  }
  
  override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
    super.onDetachedFromRecyclerView(recyclerView)
    delegates.forEach { it.onDetachedFromRecyclerView(recyclerView) }
  }
}