package viewdsl

sealed class Size {
  
  object MATCH_PARENT : Size()
  object WRAP_PARENT : Size()
  class IntSize(val size: Int) : Size()
  class Dimen(val dimenRes: Int) : Size()
  
  companion object {
    
    val MatchParent get() = MATCH_PARENT
    val WrapContent get() = WRAP_PARENT
  }
}