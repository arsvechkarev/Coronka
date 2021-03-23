package core.di

/**
 * Special class that enables replacing dependencies in runtime. For example, it could be used
 * in android tests to provide fake network dependencies instead of real ones
 */
fun interface DependencyInterceptor {
  
  /**
   * Returns dependency with class [dependencyClass] or null, if standard creation of the dependency
   * should be used
   */
  fun getDependency(dependencyClass: Class<*>): Any?
}