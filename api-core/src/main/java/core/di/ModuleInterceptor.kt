package core.di

/**
 * Special class that enables replacing di modules in runtime. For example, it could be used
 * in android tests to provide fake network dependencies instead of real ones
 */
fun interface ModuleInterceptor {
  
  /**
   * Returns module with class [moduleClass] or null, if standard creation of the module
   * should be used
   */
  fun getModule(moduleClass: Class<out Module>): Module?
}