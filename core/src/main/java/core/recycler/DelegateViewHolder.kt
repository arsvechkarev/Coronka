package core.recycler

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class DelegateViewHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView) {
  
  abstract fun bind(item: T)
}