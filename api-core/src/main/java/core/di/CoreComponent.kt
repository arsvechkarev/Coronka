package core.di

/**
 * Di component that provides feature-independent dependencies
 */
object CoreComponent {
  
  private lateinit var _coreModule: CoreModule
  private lateinit var _drawerStateModule: DrawerStateModule
  
  val schedulers get() = _coreModule.schedulers
  
  val networkAvailabilityNotifier get() = _coreModule.networkAvailabilityNotifier
  
  val okHttpClient get() = _coreModule.okHttpClient
  
  val imageLoader get() = _coreModule.imageLoader
  
  val dateTimeFormatter get() = _coreModule.dateTimeFormatter
  
  val rxJava2CallAdapterFactory get() = _coreModule.rxJava2CallAdapterFactory
  
  val gsonConverterFactory get() = _coreModule.gsonConverterFactory
  
  val drawerStateSendingChannel get() = _drawerStateModule.drawerStateSendingChannel
  
  val drawerStateReceivingChannel get() = _drawerStateModule.drawerStateReceivingChannel
  
  fun initialize(
    coreModule: CoreModule,
    drawerStateModule: DrawerStateModule
  ) {
    _coreModule = coreModule
    _drawerStateModule = drawerStateModule
  }
}