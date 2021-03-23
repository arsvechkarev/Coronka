package core.di

/**
 * Enables replacing di modules in runtime
 */
object ModuleInterceptorManager {
  
  @PublishedApi
  internal val moduleInterceptors = ArrayList<ModuleInterceptor>()
  
  /**
   * Returns [value] whenever somebody requests module of type [T]
   */
  inline fun <reified T : Module> addInterceptorForModule(crossinline value: () -> T) {
    moduleInterceptors.add(ModuleInterceptor { clazz ->
      if (clazz == T::class.java) {
        value()
      } else {
        null
      }
    })
  }
  
  /**
   * Tries to get intercepted module of type [T], or returns [defaultValue] if nobody
   * intercepted the module
   */
  inline fun <reified T : Module> interceptModuleOrDefault(defaultValue: () -> T): T {
    var resultModule: T? = null
    moduleInterceptors.forEach { interceptor ->
      val module = interceptor.getModule(T::class.java)
      if (module != null) {
        if (resultModule == null) {
          resultModule = module as? T ?: error(
            "Cannot create module of class ${T::class.java} with interceptor $interceptor")
        } else {
          error(
            "Two interceptors create the same $resultModule, cannot figure out which to use")
        }
      }
    }
    return resultModule ?: defaultValue.invoke()
  }
}
