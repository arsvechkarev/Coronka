package core.di

object CoreComponent {
  
  private var _coreModule: CoreModule? = null
  private var _failureReasonToMessageModule: FailureReasonToMessageModule? = null
  private var _dateTimeFormatterModule: DateTimeFormatterModule? = null
  
  val coreModule: CoreModule
    get() = _coreModule ?: error("create() hasn't been called")
  
  val threader get() = _coreModule!!.threader
  
  val schedulers get() = _coreModule!!.schedulers
  
  val networkAvailabilityNotifier get() = _coreModule!!.networkAvailabilityNotifier
  
  val okHttpClient get() = _coreModule!!.okHttpClient
  
  val webApiFactory get() = _coreModule!!.webApiFactory
  
  val imageLoader get() = _coreModule!!.imageLoader
  
  val failureReasonToMessageConverter get() = _failureReasonToMessageModule!!.failureReasonToMessageConverter
  
  val dateTimeFormatter get() = _dateTimeFormatterModule!!.timeFormatter
  
  fun initialize(
    coreModule: CoreModule,
    dateTimeFormatterModule: DateTimeFormatterModule
  ) {
    _coreModule = coreModule
    _dateTimeFormatterModule = dateTimeFormatterModule
  }
}