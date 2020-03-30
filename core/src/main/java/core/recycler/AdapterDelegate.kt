package core.recycler

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

interface AdapterDelegate {
  
  fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder
  
  fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: DisplayableItem)
}