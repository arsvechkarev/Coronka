package core.extenstions

import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText

operator fun View.contains(event: MotionEvent): Boolean {
  val x = event.x
  val y = event.y
  return x >= left && y >= top && x <= right && y <= bottom
}

infix fun MotionEvent.happenedIn(view: View): Boolean {
  return x >= 0 && y >= 0 && x <= view.width && y <= view.height
}

fun View.heightWithMargins(): Int {
  val params = layoutParams as ViewGroup.MarginLayoutParams
  return measuredHeight + params.topMargin + params.bottomMargin
}

fun EditText.onTextChanged(block: (String) -> Unit) {
  addTextChangedListener(object : TextWatcher {
  
    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
    }
  
    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
    }
  
    override fun afterTextChanged(s: Editable) {
      block(s.toString())
    }
  })
}