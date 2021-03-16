package config

object AnimationsConfigurator {
  
  private var _short = 150L
  private var _default = 300L
  private var _medium = 500L
  private var _long = 800L
  private var _bottomSheetSlide = 225L
  private var _loadingStubIdle = 1200L
  
  val DurationShort get() = _short
  val DurationDefault get() = _default
  val DurationMedium get() = _medium
  val DurationLong get() = _long
  val DurationBottomSheetSlide get() = _bottomSheetSlide
  val DurationLoadingStubIdle get() = _loadingStubIdle
  
  fun resetDurations() {
    _short = 0
    _default = 0
    _medium = 0
    _long = 0
    _bottomSheetSlide = 0
    _loadingStubIdle = 0
  }
}