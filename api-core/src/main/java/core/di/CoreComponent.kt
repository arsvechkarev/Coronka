package core.di

object CoreComponent {
  
  private lateinit var _coreModule: CoreModule
  
  val threader get() = _coreModule.threader
  
  val schedulers get() = _coreModule.schedulers
  
  val networkAvailabilityNotifier get() = _coreModule.networkAvailabilityNotifier
  
  val okHttpClient get() = _coreModule.okHttpClient
  
  val webApi get() = _coreModule.webApi
  
  val imageLoader get() = _coreModule.imageLoader
  
  val dateTimeFormatter get() = _coreModule.dateTimeFormatter
  
  fun initialize(coreModule: CoreModule) {
    _coreModule = coreModule
  }
}