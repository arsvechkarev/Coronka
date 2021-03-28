package base.views

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout

class DrawerGroupLinearLayout @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
  
  private var selectedTextViewTag: String? = null
  
  init {
    isSaveEnabled = true
  }
  
  fun setSelectedMenuItem(tag: String) {
    selectedTextViewTag = tag
    for (i in 0 until childCount) {
      val child = getChildAt(i)
      child.isSelected = child.tag == tag
    }
  }
  
  fun onTextViewClicked(textView: View) {
    for (i in 0 until childCount) {
      val child = getChildAt(i)
      child.isSelected = false
    }
    textView.isSelected = true
    selectedTextViewTag = textView.tag as String
  }
  
  override fun onSaveInstanceState(): Parcelable {
    val savedState = SavedState(super.onSaveInstanceState())
    savedState.selectedTextViewTag = selectedTextViewTag
    return savedState
  }
  
  override fun onRestoreInstanceState(state: Parcelable?) {
    val savedState = state as SavedState
    super.onRestoreInstanceState(savedState.superState)
    savedState.selectedTextViewTag?.let(::setSelectedMenuItem)
  }
  
  class SavedState : BaseSavedState {
    
    var selectedTextViewTag: String? = null
    
    constructor(superState: Parcelable?) : super(superState)
    
    private constructor(parcel: Parcel) : super(parcel) {
      selectedTextViewTag = parcel.readString()
    }
    
    override fun writeToParcel(out: Parcel, flags: Int) {
      super.writeToParcel(out, flags)
      out.writeString(selectedTextViewTag)
    }
    
    companion object {
      
      @JvmField
      val CREATOR: Parcelable.Creator<SavedState?> = object : Parcelable.Creator<SavedState?> {
        
        override fun createFromParcel(parcel: Parcel): SavedState {
          return SavedState(parcel)
        }
        
        override fun newArray(size: Int): Array<SavedState?> {
          return arrayOfNulls(size)
        }
      }
    }
  }
}